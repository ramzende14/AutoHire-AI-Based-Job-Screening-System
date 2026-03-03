package com.ai.aiagen.Config;

import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClient;
import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClientBuilder;

import com.azure.core.credential.AzureKeyCredential;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureConfig {

    // ------------------ FORM RECOGNIZER ------------------
    @Value("${azure.formrecognizer.endpoint}")
    private String formRecognizerEndpoint;

    @Value("${azure.formrecognizer.apikey}")
    private String formRecognizerKey;

    @Bean
    public DocumentAnalysisClient documentAnalysisClient() {
        return new DocumentAnalysisClientBuilder()
                .endpoint(formRecognizerEndpoint)
                .credential(new AzureKeyCredential(formRecognizerKey))
                .buildClient();
    }


}
