package buaa.cxtj.job_backend.Service.Impl;


import buaa.cxtj.job_backend.Config.OpenAIConfig;
import buaa.cxtj.job_backend.Service.AIService;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class AIServiceImpl implements AIService {

    @Autowired
    private OpenAIConfig openAIConfig;



    public String refineResume(String resume) {
        String apiKey = openAIConfig.getApiKey();
        String apiUrl = "https://api.gptsapi.net/v1/chat/completions";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // Decode the Base64 encoded resume content
        byte[] decodedBytes = Base64.getDecoder().decode(resume);
        String resumeContent = new String(decodedBytes);

        // Constructing the prompt for resume optimization
        String prompt = "Please optimize the following resume to make it more appealing to employers and give me optimized version in .pdf format, dont make examples but use my actual resume:\n\n" + resumeContent;


        JSONObject body = new JSONObject();
        body.putOnce("model", "gpt-4o-2024-05-13");
        body.putOnce("messages", new JSONArray()
                .put(new JSONObject().putOnce("role", "system").putOnce("content", "You are a helpful assistant."))
                .put(new JSONObject().putOnce("role", "user").putOnce("content", prompt))
        );
        body.putOnce("stream", false);  // 关闭流模式

        HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject responseBody = new JSONObject(response.getBody());
            JSONArray choices = responseBody.getJSONArray("choices");
            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject message = firstChoice.getJSONObject("message");
            return message.getStr("content");
        } else {
            throw new RuntimeException("Failed to call OpenAI API: " + response.getStatusCode());
        }
    }
}
