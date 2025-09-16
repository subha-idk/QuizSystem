package com.subha.quizSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizResultDTO {
    private Integer score;
    private List<QuestionResultDTO> questionResults;

}
