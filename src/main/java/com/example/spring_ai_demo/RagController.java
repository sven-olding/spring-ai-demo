package com.example.spring_ai_demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RagController {
    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/load-data")
    public void loadData() {
        ragService.loadData();
    }

    @PostMapping("/similarity-search")
    public ResponseEntity<String> similaritySearch(@RequestBody String prompt) {
        return new ResponseEntity<String>(ragService.similaritySearch(prompt), HttpStatus.OK);
    }
}
