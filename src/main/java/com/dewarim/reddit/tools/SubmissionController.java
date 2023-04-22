package com.dewarim.reddit.tools;

import com.dewarim.reddit.tools.jpa.SubmissionRepository;
import com.dewarim.reddit.tools.model.ImportStatistics;
import com.dewarim.reddit.tools.model.Submission;
import com.dewarim.reddit.tools.util.Partitioner;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.luben.zstd.RecyclingBufferPool;
import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

@RestController
@RequestMapping("submission")
public class SubmissionController {

    private final        String       filename     = "/home/ingo/data2/reddit-submissions/RS_2013-11.zst";
    private final        String       path         = "/home/ingo/data2/reddit-submissions";
    private final        ObjectMapper objectMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    private static final Logger       log          = LoggerFactory.getLogger(SubmissionController.class);

    private SubmissionRepository submissionRepository;

    public SubmissionController(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    @GetMapping(path = "listFromFile")
    public List<Submission> listFromFile(@RequestParam(value = "sfwOnly", defaultValue = "false") boolean sfwOnly,
                                         @RequestParam(value = "nsfwOnly", defaultValue = "false") boolean nsfwOnly) {
        log.info("listFromFile");
        List<Submission> submissions = parseFile(filename).stream().filter(submission -> {
                    if (sfwOnly) {
                        return !submission.isOver18();
                    } else if (nsfwOnly) {
                        return submission.isOver18();
                    }
                    return true;
                })
                .collect(Collectors.toList());
        submissions.forEach(s -> log.info(s.toString()));
        return submissions;
    }

    private List<Submission> parseFile(String filename) {
        File file = new File(filename);
        try (FileInputStream inputFile = new FileInputStream(file);
             BufferedInputStream buffy = new BufferedInputStream(inputFile);
             ZstdInputStream zstdInputStream = new ZstdInputStream(buffy);
             BufferedReader buffReader = new BufferedReader(new InputStreamReader(zstdInputStream))
        ) {
            return buffReader.lines().map(this::parseSubmission).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("failed to list file: ", e);
            throw new RuntimeException(e);
        }
    }

    /*
     * Note: this only works for small archives. I suspect that the transactional mechanism
     * does not work, and it takes more and more RAM (25 GByte after a couple of hours)
     */
    @PostMapping(path = "importFromFiles")
    public ImportStatistics importFromFiles() throws IOException {

        ImportStatistics statistics = new ImportStatistics();
        long             start      = System.currentTimeMillis();
        log.info("Starting import");
        try (Stream<Path> fileStream = Files.list(Path.of(path))) {
            fileStream.forEach(filePath -> {
                if (!filePath.getFileName().toString().matches(".*\\.zst")) {
                    return;
                }
                log.info("File: " + filePath);
                try (FileInputStream inputFile = new FileInputStream(filePath.toFile());
                     BufferedInputStream buffy = new BufferedInputStream(inputFile);
                     ZstdInputStream zstdInputStream = new ZstdInputStream(buffy);
                     BufferedReader buffReader = new BufferedReader(new InputStreamReader(zstdInputStream))
                ) {
                    zstdInputStream.setLongMax(31);
                    List<Submission> buffer = new ArrayList<>();
                    buffReader.lines().map(this::parseSubmission)
                            .filter(not(Submission::isDeleted))
                            .forEach(submission -> {
                                statistics.incSubmissionCount();
                                if (submission.isOver18()) {
                                    statistics.incNsfwCount();
                                }
                                statistics.incSubredditCount(submission.getSubreddit());
                                buffer.add(submission);
                                if (statistics.getSubmissions() % 10000 == 0) {
                                    log.info("row: " + statistics.getSubmissions());
                                    submissionRepository.saveAll(buffer);
                                    buffer.clear();
                                }
//                    log.info("url: " + submission.getUrl());
                            });
                    saveSubmissions(buffer);

                } catch (IOException e) {
                    log.error("failed to list file: ", e);
                    throw new RuntimeException(e);
                }
            });
        }

        long end = System.currentTimeMillis();
        statistics.setDuration(end - start);
        log.info(statistics.toString());
        return statistics;
    }

    @PostMapping(path = "filterSubmissions")
    public ImportStatistics filterSubmissions() throws IOException {
        String           exportPath = "data/sub-data/simple-submissions.zst";
        ImportStatistics statistics = new ImportStatistics();
        long             start      = System.currentTimeMillis();
        log.info("Starting import");
        try (Stream<Path> fileStream = Files.list(Path.of(path))) {
            fileStream.forEach(filePath -> {
                if (!filePath.getFileName().toString().matches(".*\\.zst")) {
                    return;
                }
                log.info("File: " + filePath);
                try (FileInputStream inputFile = new FileInputStream(filePath.toFile());
                     BufferedInputStream buffy = new BufferedInputStream(inputFile);
                     ZstdInputStream zstdInputStream = new ZstdInputStream(buffy);
                     FileOutputStream out = new FileOutputStream(exportPath);
                     ZstdOutputStream zstdOutputStream = new ZstdOutputStream(out, 10);
                     BufferedReader buffReader = new BufferedReader(new InputStreamReader(zstdInputStream))
                ) {
                    zstdInputStream.setLongMax(31);
                    zstdOutputStream.setLong(31);
                    buffReader.lines().map(this::parseSubmission)
                            .filter(not(Submission::isDeleted))
                            .forEach(submission -> {
                                statistics.incSubmissionCount();
                                if (submission.isOver18()) {
                                    statistics.incNsfwCount();
                                }
                                statistics.incSubredditCount(submission.getSubreddit());
                                if (statistics.getSubmissions() % 10000 == 0) {
                                    log.info("row: " + statistics.getSubmissions());
                                }
                                try {
                                    zstdOutputStream.write(objectMapper.writeValueAsBytes(submission));
                                    zstdOutputStream.write("\n".getBytes(StandardCharsets.UTF_8));
                                } catch (IOException e) {
                                    log.info("Failed to write to zstdOutputStream:", e);
                                    throw new RuntimeException(e);
                                }
                            });
                } catch (IOException e) {
                    log.error("failed to list file: ", e);
                    throw new RuntimeException(e);
                }
            });
        }

        long end = System.currentTimeMillis();
        statistics.setDuration(end - start);
        log.info(statistics.toString());
        return statistics;
    }


    @Transactional
    public void saveSubmissions(List<Submission> buffer) {
        submissionRepository.saveAll(buffer);
    }

    private Submission parseSubmission(String json) {
        try {
            return objectMapper.readValue(json, Submission.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
