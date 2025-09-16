package com.subha.quizSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResultDTO {
    private Integer questionId;
    private String userAnswer;
    private String correctAnswer;
    private boolean isCorrect;

}
