//package com.ai.aiagen.Service;
//
//import com.ai.aiagen.Entity.ResumeInfo;
//import com.ai.aiagen.Repository.ResumeInfoRepository;
//import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClient;
//import com.azure.ai.formrecognizer.documentanalysis.models.AnalyzeResult;
//import com.azure.ai.formrecognizer.documentanalysis.models.DocumentKeyValuePair;
//import com.azure.core.util.BinaryData;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//@Slf4j
//@Service
//public class ResumeAnyService {
//
//    private final ResumeInfoRepository resumeInfoRepository;
//    private final DocumentAnalysisClient documentAnalysisClient;
//
//    public ResumeAnyService(
//            ResumeInfoRepository resumeInfoRepository,
//            DocumentAnalysisClient documentAnalysisClient
//    ) {
//        this.resumeInfoRepository = resumeInfoRepository;
//        this.documentAnalysisClient = documentAnalysisClient;
//    }
//
//    // --------------------------------------
//    // Extract Resume and save JSON to DB
//    // --------------------------------------
//    public ResumeInfo extractAndSave(MultipartFile file) throws IOException {
//
//        // Call Azure Form Recognizer
//        AnalyzeResult analyzeResult = documentAnalysisClient
//                .beginAnalyzeDocument("prebuilt-document", BinaryData.fromStream(file.getInputStream()))
//                .getFinalResult();
//
//        // Convert Key-Value pairs into normal JSON Map
//        Map<String, String> extractedFields = new HashMap<>();
//        for (DocumentKeyValuePair kv : analyzeResult.getKeyValuePairs()) {
//            if (kv.getKey() != null && kv.getValue() != null) {
//                extractedFields.put(
//                        kv.getKey().getContent().trim(),
//                        kv.getValue().getContent().trim()
//                );
//            }
//        }
//
//        // Save raw + extracted JSON
//        ResumeInfo resumeInfo = new ResumeInfo();
//        resumeInfo.setFileName(file.getOriginalFilename());
//        resumeInfo.setRawExtract(analyzeResult.getContent());     // full text
//        resumeInfo.setExtractedJson(extractedFields.toString());  // key-value json
//
//        return resumeInfoRepository.save(resumeInfo);
//    }
//    public void printExtractedJson(Map<String, String> jsonData) {
//        System.out.println("-------- Extracted Resume JSON --------");
//        jsonData.forEach((key, value) -> {
//            System.out.println(key + " : " + value);
//        });
//        System.out.println("---------------------------------------");
//    }
//}
package com.ai.aiagen.Service;

import com.ai.aiagen.Entity.ResumeInfo;
import com.ai.aiagen.Repository.ResumeInfoRepository;
import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClient;
import com.azure.ai.formrecognizer.documentanalysis.models.AnalyzeResult;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentKeyValuePair;
import com.azure.core.util.BinaryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ResumeAnyService {

    private final ResumeInfoRepository resumeInfoRepository;
    private final DocumentAnalysisClient documentAnalysisClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResumeAnyService(
            ResumeInfoRepository resumeInfoRepository,
            DocumentAnalysisClient documentAnalysisClient
    ) {
        this.resumeInfoRepository = resumeInfoRepository;
        this.documentAnalysisClient = documentAnalysisClient;
    }

    public ResumeInfo extractAndSave(MultipartFile file) throws IOException {

        AnalyzeResult analyzeResult = documentAnalysisClient
                .beginAnalyzeDocument(
                        "prebuilt-document",
                        BinaryData.fromStream(file.getInputStream(), file.getSize())
                )
                .getFinalResult();


        // Extract key-value pairs
        Map<String, String> extractedFields = new HashMap<>();
        for (DocumentKeyValuePair kv : analyzeResult.getKeyValuePairs()) {
            if (kv.getKey() != null && kv.getValue() != null) {
                extractedFields.put(
                        kv.getKey().getContent().trim(),
                        kv.getValue().getContent().trim()
                );
            }
        }

        // Convert to proper JSON string
        String jsonString = objectMapper.writeValueAsString(extractedFields);

        // Save raw + extracted JSON
        ResumeInfo resumeInfo = new ResumeInfo();
        resumeInfo.setFileName(file.getOriginalFilename());
        resumeInfo.setRawExtract(analyzeResult.getContent());
        resumeInfo.setExtractedJson(jsonString);

        log.info("Resume '{}' extracted and saved successfully", file.getOriginalFilename());
        return resumeInfoRepository.save(resumeInfo);
    }

    public void printExtractedJson(Map<String, String> jsonData) {
        log.info("-------- Extracted Resume JSON --------");
        jsonData.forEach((key, value) -> log.info("{} : {}", key, value));
        log.info("---------------------------------------");
    }
}
