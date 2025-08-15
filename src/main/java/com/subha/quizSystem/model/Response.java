package com.subha.quizSystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {

    @JsonProperty("rId")
    private int rId;

    @JsonProperty("response")
    private String response;
}

