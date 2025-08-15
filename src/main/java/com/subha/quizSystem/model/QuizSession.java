package com.subha.quizSystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
public class QuizSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String sessionId;

    private String username;

    private Integer quizId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private boolean active;
}

