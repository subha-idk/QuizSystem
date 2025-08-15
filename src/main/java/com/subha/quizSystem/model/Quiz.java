package com.subha.quizSystem.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer quizId;

    private String quizTitle;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Question> questions;
    private Integer timeLimit;

}
