//package com.ai.aiagen.Service;
//
//import com.azure.ai.openai.OpenAIClient;
//import com.azure.ai.openai.OpenAIClientBuilder;
//import com.azure.ai.openai.models.ChatCompletions;
//import com.azure.ai.openai.models.ChatCompletionsOptions;
//import com.azure.ai.openai.models.ChatRequestMessage;
//import com.azure.ai.openai.models.ChatRequestUserMessage;
//import com.azure.core.credential.AzureKeyCredential;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Slf4j
//@Service
//public class MatchService {
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Value("${spring.ai.azure.openai.endpoint}")
//    private String endpoint;
//
//    @Value("${spring.ai.azure.openai.key}")
//    private String apiKey;
//
//    @Value("${spring.ai.azure.openai.chat.options.deployment-name}")
//    private String deployment;
//
//    private OpenAIClient getClient() {
//        return new OpenAIClientBuilder()
//                .endpoint(endpoint)
//                .credential(new AzureKeyCredential(apiKey))
//                .buildClient();
//    }
//
//    public String matchResume(String resumeJson, String jobDescription) {
//        try {
//
//            String prompt = """
//                    You are an ATS (Applicant Tracking System).
//                    Match this resume with the job description and return ONLY raw JSON output.
//
//                    ❌ DO NOT add ```json or any code blocks.
//                    ❌ DO NOT add explanation.
//                    ❌ ONLY return valid JSON object.
//
//                    Required JSON format:
//                    {
//                      "overallScore": 0-100,
//                      "skillMatch": 0-100,
//                      "missingSkills": ["skill1", "skill2"],
//                      "experienceScore": 0-100,
//                      "summary": "3 lines summary",
//                      "shortlist": true
//                    }
//
//                    Resume:
//                    %s
//
//                    Job Description:
//                    %s
//                    """.formatted(resumeJson, jobDescription);
//
//            ChatRequestUserMessage userMessage = new ChatRequestUserMessage(prompt);
//
//            List<ChatRequestMessage> messages = List.of(userMessage);
//
//            ChatCompletionsOptions options = new ChatCompletionsOptions(messages);
//
//            ChatCompletions response = getClient()
//                    .getChatCompletions(deployment, options);
//
//            String aiResponse = response.getChoices()
//                    .get(0)
//                    .getMessage()
//                    .getContent()
//                    .trim();
//
//            log.info("AI Raw Response: {}", aiResponse);
//
//            // 🔥 CLEAN JSON — remove markdown wrappers
//            String cleanJson = aiResponse
//                    .replace("```json", "")
//                    .replace("```", "")
//                    .replace("`", "")
//                    .trim();
//
//            log.info("Clean JSON Response: {}", cleanJson);
//
//            return cleanJson;
//
//        } catch (Exception ex) {
//            log.error("Error matching resume with JD: ", ex);
//            throw new RuntimeException("ATS Matching Failed", ex);
//        }
//    }
//}
package com.ai.aiagen.Service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.azure.core.credential.AzureKeyCredential;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MatchService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.ai.azure.openai.endpoint}")
    private String endpoint;

    @Value("${spring.ai.azure.openai.key}")
    private String apiKey;

    @Value("${spring.ai.azure.openai.chat.options.deployment-name}")
    private String deployment;

    // 🔥 Load temperature values from application.properties
    @Value("${spring.ai.azure.openai.temperature}")
    private double temperature;

    @Value("${spring.ai.azure.openai.top_p}")
    private double topP;

    private OpenAIClient getClient() {
        return new OpenAIClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(apiKey))
                .buildClient();
    }

    public String matchResume(String resumeJson, String jobDescription) {
        try {

            String prompt = """
                    You are an ATS (Applicant Tracking System).
                    Match this resume with the job description and return ONLY raw JSON output.
                    
                    ❌ DO NOT add ```json or any code blocks.
                    ❌ DO NOT add explanation.
                    ❌ ONLY return valid JSON object.

                    Required JSON format:
                    {
                      "overallScore": 0-100,
                      "skillMatch": 0-100,
                      "missingSkills": ["skill1", "skill2"],
                      "experienceScore": 0-100,
                      "summary": "3 lines summary",
                      "shortlist": true
                    }

                    Resume:
                    %s

                    Job Description:
                    %s
                    """.formatted(resumeJson, jobDescription);

            ChatRequestUserMessage userMessage = new ChatRequestUserMessage(prompt);
            List<ChatRequestMessage> messages = List.of(userMessage);

            // 🔥 APPLY temperature & top_p here (most important for consistency)
            ChatCompletionsOptions options = new ChatCompletionsOptions(messages)
                    .setTemperature(temperature)
                    .setTopP(topP);

            ChatCompletions response = getClient()
                    .getChatCompletions(deployment, options);

            String aiResponse = response
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent()
                    .trim();

            log.info("AI Raw Response: {}", aiResponse);

            // 🧹 Clean JSON (remove accidental formatting)
            String cleanJson = aiResponse
                    .replace("```json", "")
                    .replace("```", "")
                    .replace("`", "")
                    .trim();

            log.info("Clean JSON Response: {}", cleanJson);

            return cleanJson;

        } catch (Exception ex) {
            log.error("Error matching resume with JD: ", ex);
            throw new RuntimeException("ATS Matching Failed", ex);
        }
    }
}

