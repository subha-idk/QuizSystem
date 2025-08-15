package com.subha.quizSystem.service;

import com.subha.quizSystem.dao.QuestionRepository;
import com.subha.quizSystem.dao.QuizRepository;
import com.subha.quizSystem.dao.QuizSessionRepository;
import com.subha.quizSystem.dto.QuestionResultDTO;
import com.subha.quizSystem.dto.QuizDTO;
import com.subha.quizSystem.dto.QuizResultDTO;
import com.subha.quizSystem.dto.QuizSummaryDTO;
import com.subha.quizSystem.exception.NotFoundException;
import com.subha.quizSystem.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.naming.TimeLimitExceededException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// QuizService.java

// ... (imports and class definition)

@Service
public class QuizService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private QuizSessionRepository quizSessionRepository;

    @Autowired
    private QuizSessionService quizSessionService;

    public ResponseEntity<String> createQuizByCategory(String category, Integer limit, String title,
                                                       Integer timeLimit) {
        try {
            // Use PageRequest to limit the number of questions
            List<Question> questions = questionRepository.getQuestionsForQuizByCategory(
                    category, PageRequest.of(0, limit));

            Quiz quiz = new Quiz();
            quiz.setQuizTitle(title);
            quiz.setQuestions(questions);
            quiz.setTimeLimit(timeLimit);

            quizRepository.save(quiz);

            return new ResponseEntity<>("quizTitle = " + title + " successfully saved.\n" + questions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong...", HttpStatus.BAD_REQUEST);
        }
    }

    // The fetchQuizById method is correctly populating the QuestionWrapper
    public ResponseEntity<QuizDTO> fetchQuizById(Integer id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Quiz with ID " + id + " not found."));

        // Debugging: Check if questions are fetched correctly before DTO creation
        System.out.println("fetchQuizById: Found " + quiz.getQuestions().size() + " questions for quizId " + id);

        List<QuestionWrapper> questionWrappers = quiz.getQuestions().stream()
                .map(q -> new QuestionWrapper(
                        q.getQId(),
                        q.getQuestionTitle(),
                        q.getOption1(),
                        q.getOption2(),
                        q.getOption3(),
                        q.getOption4()
                ))
                .collect(Collectors.toList());

        QuizDTO quizDTO = new QuizDTO(quiz.getQuizId(), quiz.getQuizTitle(), quiz.getTimeLimit(), questionWrappers);
        return new ResponseEntity<>(quizDTO, HttpStatus.OK);
    }

    public ResponseEntity<QuizDTO> startQuizSession(Integer quizId, String sessionId, String username) {
        if (quizSessionRepository.existsByUsernameAndActiveTrue(username)) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        ResponseEntity<QuizDTO> quizResponse = fetchQuizById(quizId);
        if (!quizResponse.getStatusCode().is2xxSuccessful()) {
            return quizResponse;
        }

        try {
            quizSessionService.createSession(quizId, sessionId, username);
            return quizResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Main method to get the quiz result.
     */
    public ResponseEntity<QuizResultDTO> getResult(String sessionId, List<Response> responses) {
        try {
            // 1️⃣ Fetch QuizSession
            QuizSession session = quizSessionRepository.findBySessionId(sessionId)
                    .orElseThrow(() -> new NotFoundException("Session ID not found"));

            // 2️⃣ Fetch Quiz
            Quiz quiz = quizRepository.findById(session.getQuizId())
                    .orElseThrow(() -> new NotFoundException("Quiz not found for this session."));

            LocalDateTime submissionTime = LocalDateTime.now();
            long timeLimitInMinutes = quiz.getTimeLimit();
            LocalDateTime deadline = session.getStartTime().plusMinutes(timeLimitInMinutes);

            if (submissionTime.isAfter(deadline)) {
                throw new TimeLimitExceededException(
                        "Submission failed. The time limit for the quiz was exceeded."
                );
            }

            // Mark inactive & save
            session.setActive(false);
            session.setEndTime(submissionTime);
            quizSessionRepository.save(session);

            List<Question> questions = getQuestionsFromQuiz(quiz);
            QuizResultDTO resultDTO = calculateResults(questions, responses);

            return new ResponseEntity<>(resultDTO, HttpStatus.OK);

        } catch (NotFoundException nfe) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        } catch (TimeLimitExceededException tle) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<List<QuizSummaryDTO>> getAllQuizzes() {
        try {
            List<Quiz> quizzes = quizRepository.findAll();

            List<QuizSummaryDTO> summaries = quizzes.stream()
                    .map(quiz -> new QuizSummaryDTO(quiz.getQuizId(), quiz.getQuizTitle()))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(summaries, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Finds and validates a quiz using the ID from a session.
     */
    private Quiz getQuizFromSession(QuizSession session) {
        return quizRepository.findById(session.getQuizId())
                .orElseThrow(() -> new NotFoundException("quizId = " + session.getQuizId() + " not found."));
    }

    /**
     * Retrieves the questions associated with a quiz.
     */
    private List<Question> getQuestionsFromQuiz(Quiz quiz) {
        System.out.println("getQuestionsFromQuiz: Retrieved questions list size: " + quiz.getQuestions().size());
        return quiz.getQuestions();
    }

    /**
     * Calculates the score and generates a detailed list of question results.
     */
    private QuizResultDTO calculateResults(List<Question> questions, List<Response> responses) {
        int totalScore = 0;
        List<QuestionResultDTO> questionResults = new ArrayList<>();

        var questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getQId, q -> q));

        // Debugging: Check if the map is correctly populated
        System.out.println("calculateResults: questionMap size: " + questionMap.size());
        questionMap.keySet().forEach(key -> System.out.println("Map Key: " + key));


        for (Response userResponse : responses) {
            // Debugging: Print the rId from the frontend
            System.out.println("calculateResults: Processing rId from user response: " + userResponse.getRId());

            Question question = questionMap.get(userResponse.getRId());

            if (question != null) {
                // This block is likely not being hit.
                System.out.println("calculateResults: SUCCESS! Found question for rId: " + userResponse.getRId());
                boolean isCorrect = userResponse.getResponse().equals(question.getCorrectAnswer());
                if (isCorrect) {
                    totalScore++;
                }

                QuestionResultDTO result = new QuestionResultDTO();
                result.setQuestionId(question.getQId());
                result.setUserAnswer(userResponse.getResponse());
                result.setCorrectAnswer(question.getCorrectAnswer());
                result.setCorrect(isCorrect);
                questionResults.add(result);
            } else {
                System.err.println("calculateResults: ERROR! No question found in map for rId: " + userResponse.getRId());
            }
        }

        QuizResultDTO quizResultDTO = new QuizResultDTO();
        quizResultDTO.setScore(totalScore);
        quizResultDTO.setQuestionResults(questionResults);

        return quizResultDTO;
    }
}