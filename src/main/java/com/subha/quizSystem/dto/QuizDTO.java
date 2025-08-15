package com.subha.quizSystem.dto;

import com.subha.quizSystem.model.QuestionWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizDTO {
    private Integer quizId;
    private String title;
    private Integer timeLimit;
    private List<QuestionWrapper> questions;
}
