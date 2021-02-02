package com.codegame.security.config;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

public class OTPTemplate {
    private String template;

    public OTPTemplate() throws Exception {
        this.template = loadTemplate("OTPEmail.html");
    }

    private String loadTemplate(String customtemplate) throws Exception {

        ClassLoader classLoader = getClass().getClassLoader();
        StringBuilder sb = new StringBuilder();
        try (InputStream inputStream = classLoader.getResourceAsStream(customtemplate);
             InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            throw new Exception("Could not read template  = " + customtemplate);
        }

        return sb.toString();
    }

    public String getTemplate(Map<String, String> replacements) {

        String cTemplate = this.template;
        //Replace the String
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            cTemplate = cTemplate.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return cTemplate;
    }
}
