package com.ai.aiagen.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "resume_info")
public class ResumeInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // candidate/user reference

    private String fileName;

    @Column(columnDefinition = "LONGTEXT")
    private String extractedJson;  // Full JSON from Azure Form Recognizer or OpenAI

    public ResumeInfo() {}

    public ResumeInfo(Long userId, String fileName, String extractedJson) {
        this.userId = userId;
        this.fileName = fileName;
        this.extractedJson = extractedJson;
    }

    // ========= Getters & Setters =========

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExtractedJson() {
        return extractedJson;
    }
    public void setExtractedJson(String extractedJson) {
        this.extractedJson = extractedJson;
    }

    // ========= PRINT OPTION =========
    public void printJson() {
        System.out.println("===== Extracted Resume JSON =====");
        System.out.println(this.extractedJson);
        System.out.println("=================================");
    }

    public void setRawExtract(String content) {
    }
}
