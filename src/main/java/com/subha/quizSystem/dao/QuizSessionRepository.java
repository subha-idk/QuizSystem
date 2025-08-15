package com.subha.quizSystem.dao;

import com.subha.quizSystem.model.QuizSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizSessionRepository extends JpaRepository<QuizSession, String> {
    boolean existsByUsernameAndActiveTrue(String username);
    Optional<QuizSession> findBySessionId(String sessionId);
}


