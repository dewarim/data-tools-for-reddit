package com.dewarim.reddit.tools;

import com.dewarim.reddit.tools.model.Submission;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("submission")
public class SubmissionController {

    private final  String       filename     = "/home/ingo/code/data-tools-for-reddit/data/RS_2008-02.zst";
    private final  ObjectMapper objectMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    private static Logger       log          = LoggerFactory.getLogger(SubmissionController.class);

    @GetMapping(path = "listFromFile")
    public List<Submission> listFromFile(@RequestParam(value = "sfwOnly", defaultValue = "false") boolean sfwOnly,
                                         @RequestParam(value = "nsfwOnly", defaultValue = "false") boolean nsfwOnly) {
        log.info("listFromFile");
        File file = new File(filename);
        try (FileInputStream inputFile = new FileInputStream(file);
             BufferedInputStream buffy = new BufferedInputStream(inputFile);
             ZstdInputStream zstdInputStream = new ZstdInputStream(buffy);
             BufferedReader buffReader = new BufferedReader(new InputStreamReader(zstdInputStream));

        ) {
            List<Submission> submissions = buffReader.lines().map(this::parseSubmission)
                    .filter(submission -> {
                        if (sfwOnly) {
                            return !submission.isOver18();
                        } else if (nsfwOnly) {
                            return submission.isOver18();
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
            submissions.forEach(System.out::println);
            return submissions;
        } catch (IOException e) {
            log.error("failed to list file: ", e);
            throw new RuntimeException(e);
        }
    }

    @GetMapping(path = "hello")
    public String hello() {
        return "Hello";
    }

    private Submission parseSubmission(String json) {
        try {
            return objectMapper.readValue(json, Submission.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
