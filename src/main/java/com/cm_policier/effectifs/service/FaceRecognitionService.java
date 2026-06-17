package com.cm_policier.effectifs.service;

import java.io.IOException;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpMethod;

import com.cm_policier.effectifs.dto.FaceRecognitionResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FaceRecognitionService {

    private final RestTemplate restTemplate;

    public FaceRecognitionResponse recognize(
            MultipartFile file) throws IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        body.add("file", resource);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<FaceRecognitionResponse> response = restTemplate.exchange(
                "http://localhost:8000/recognize",
                HttpMethod.POST,
                request,
                FaceRecognitionResponse.class);

        return response.getBody();
    }

}