package com.example.spring_ai_demo;

import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class RagService {
    private final VectorStore vectorStore;
    private final ResourceLoader resourceLoader;
    private final JdbcTemplate jdbcTemplate;

    public RagService(VectorStore vectorStore, ResourceLoader resourceLoader, JdbcTemplate jdbcTemplate) {
        this.vectorStore = vectorStore;
        this.resourceLoader = resourceLoader;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void loadData() {
        jdbcTemplate.execute("DELETE FROM vector_store");
        Resource holgerManualPdf = resourceLoader.getResource("classpath:Anwendungshandbuch_Holger.pdf");
        var documentReader = new TikaDocumentReader(holgerManualPdf);
        List<Document> documents = documentReader.get();
        TextSplitter textSplitter = new TokenTextSplitter(300, 120, 5, 1000, true);
        List<Document> splitDocuments = textSplitter.apply(documents);
        vectorStore.add(splitDocuments);
    }

    public String similaritySearch(String prompt) {
        var sb = new StringBuilder();
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder().similarityThreshold(0.6d).query(prompt).build());
        if (documents == null) {
            return "";
        }
        documents.forEach(document -> sb.append(document.getFormattedContent()));
        return sb.toString();
    }
}
