package com.cm_policier.effectifs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FaceRecognitionResponse {

    private Boolean success;

    private String filename;

    private Double distance;

    private String message;

}