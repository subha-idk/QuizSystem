package com.subha.quizSystem.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class QuestionWrapper {

    private final Integer qId;
    private final String questionTitle;
    private final String option1;
    private final String option2;
    private final String option3;
    private final String option4;

}
