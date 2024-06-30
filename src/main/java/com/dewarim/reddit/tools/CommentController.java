package com.dewarim.reddit.tools;

import com.dewarim.reddit.tools.jpa.CommentRepository;
import com.dewarim.reddit.tools.model.Comment;
import com.dewarim.reddit.tools.model.ImportStatistics;
import com.dewarim.reddit.tools.model.Submission;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

@RestController
@RequestMapping("comment")
public class CommentController {

    private final        String       filename     = "data/RS_2008-02.zst";
    private final        String       path         = "/media/reddit/reddit-comments/";
    private final        ObjectMapper objectMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    private static final Logger       log          = LoggerFactory.getLogger(CommentController.class);

    private final CommentRepository commentRepository;

    public CommentController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @GetMapping(path = "listFromFile")
    public List<Comment> listFromFile() {
        log.info("listFromFile");
        List<Comment> comments = parseFile(filename).stream().filter(comment ->
                        comment.getSubreddit().equals("Metal")
                )
                .collect(Collectors.toList());
        comments.forEach(s -> log.info(s.toString()));
        return comments;
    }

    private List<Comment> parseFile(String filename) {
        File file = new File(filename);
        try (FileInputStream inputFile = new FileInputStream(file);
             BufferedInputStream buffy = new BufferedInputStream(inputFile);
             ZstdInputStream zstdInputStream = new ZstdInputStream(buffy);
             BufferedReader buffReader = new BufferedReader(new InputStreamReader(zstdInputStream))
        ) {
            return buffReader.lines().map(this::parseComment).collect(Collectors.toList());
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
//                if (!filePath.getFileName().toString().matches(".*\\.zst")) {
                if (!filePath.getFileName().toString().matches("RC_2020-10.zst")) {
                    return;
                }
                log.info("File: {}", filePath);
                try (FileInputStream inputFile = new FileInputStream(filePath.toFile());
                     BufferedInputStream buffy = new BufferedInputStream(inputFile);
                     ZstdInputStream zstdInputStream = new ZstdInputStream(buffy);
                     BufferedReader buffReader = new BufferedReader(new InputStreamReader(zstdInputStream))
                ) {
                    zstdInputStream.setLongMax(31);
                    List<Comment> buffer = new ArrayList<>();
                    buffReader.lines().map(this::parseComment)
                            .forEach(comment -> {
                                statistics.incCommentCount();
                                buffer.add(comment);
                                if (statistics.getComments() % 10000 == 0) {
                                    log.info("row: {}", statistics.getComments());
                                    commentRepository.saveAll(buffer);
                                    buffer.clear();
                                }
                            });
                    commentRepository.saveAll(buffer);
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

    private Comment parseComment(String json) {
        try {
            return objectMapper.readValue(json, Comment.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
