package com.subha.quizSystem.service;

import com.subha.quizSystem.dao.QuizSessionRepository;
import com.subha.quizSystem.exception.NotFoundException;
import com.subha.quizSystem.model.QuizSession;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class QuizSessionService {

    private final QuizSessionRepository quizSessionRepository;

    public QuizSessionService(QuizSessionRepository quizSessionRepository) {
        this.quizSessionRepository = quizSessionRepository;
    }

    /**
     * Creates and saves a new QuizSession.
     * @param quizId The ID of the quiz.
     * @param sessionId The unique identifier for the session.
     * @return The newly created QuizSession object.
     */
    public QuizSession createSession(Integer quizId, String sessionId, String username) {
        QuizSession session = new QuizSession();
        session.setSessionId(sessionId);
        session.setUsername(username);
        session.setQuizId(quizId);
        session.setStartTime(LocalDateTime.now());
        session.setActive(true);
        return quizSessionRepository.save(session);
    }

    /**
     * Finds and validates a quiz session by its ID.
     * @param sessionId The session identifier.
     * @return The found QuizSession object.
     * @throws NotFoundException if the session does not exist.
     */
    public QuizSession getSessionById(String sessionId) {
        return quizSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Session ID = " + sessionId + " not found."));
    }
}