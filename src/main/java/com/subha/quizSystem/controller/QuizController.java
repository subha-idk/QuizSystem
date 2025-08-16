package com.subha.quizSystem.controller;

import com.subha.quizSystem.dto.QuizDTO;
import com.subha.quizSystem.dto.QuizResultDTO;
import com.subha.quizSystem.dto.QuizSummaryDTO;
import com.subha.quizSystem.model.QuestionWrapper;
import com.subha.quizSystem.model.Quiz;
import com.subha.quizSystem.service.QuizService;
import com.subha.quizSystem.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://localhost:5173/" })
@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private QuizService service;

    

    /**
     * 
     * @param quizId
     * @return
     */
    @GetMapping("/{quizId}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Integer quizId) {
        return service.getQuizById(quizId);
    }

    /**
     * Create a new quiz based on category, number of questions, and title.
     * Endpoint: POST /api/quiz/generate
     * Query Params: category, numQ, title
     */
    @PostMapping("/generate")
    public ResponseEntity<String> generateQuizByCategory(
            @RequestParam String category,
            @RequestParam("numQ") Integer limit,
            @RequestParam String title,
            @RequestParam(required = false, defaultValue = "300") Integer timeLimit) {
        return service.createQuizByCategory(category, limit, title, timeLimit);
    }

    /**
     * Create an Empty Quiz.
     * Endpoint: POST /api/quiz/create
     * title & timelimit as param post
     */

    @PostMapping("/create")
    public ResponseEntity<?> createEmptyQuiz(
            @RequestParam String title,
            @RequestParam(required = false, defaultValue = "300") Integer timeLimit) {
        System.out.println("empty quiz creting" + title + timeLimit);
        return service.createEmptyQuiz(title, timeLimit);
    }

    /**
     * Start quiz using session id.
     * Endpoint: GET /api/quiz/get/{quizId}
     * sessionId as param post
     */
    @PostMapping("/start/{quizId}")
    public ResponseEntity<QuizDTO> startQuizSession(
            @PathVariable Integer quizId,
            @RequestParam String sessionId,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {

        return service.startQuizSession(quizId, sessionId, userDetails.getUsername());
    }

    /**
     * Submit responses and get the quiz result.
     * Endpoint: POST /api/quiz/submit/{quizId}
     * Request Body: List of responses
     */

    @PostMapping("/submit/{sessionId}")
    public ResponseEntity<QuizResultDTO> submitQuiz(
            @PathVariable("sessionId") String sessionId,
            @RequestBody List<Response> responses) {
        // The service method now returns the detailed DTO
        System.out.println("submit request hit to the controller");
        System.out.println("Received responses of the quiz : " + responses.toString());
        return service.getResult(sessionId, responses);
    }

    /**
     * Adds a specific question to a specific quiz.
     * Endpoint: PUT /api/quiz/{quizId}/add/{questionId}
     */
    @PutMapping("/{quizId}/add/{questionId}")
    public ResponseEntity<?> addQuestionToQuiz(
            @PathVariable Integer quizId,
            @PathVariable Integer questionId) {
        return service.addQuestionToQuiz(quizId, questionId);
    }

    @GetMapping("all")
    public ResponseEntity<List<QuizSummaryDTO>> getAllQuizzes() {
        return service.getAllQuizzes();
    }

    /**
     * Removes a specific question from a specific quiz.
     * Endpoint: DELETE /api/quiz/{quizId}/remove/{questionId}
     */
    @DeleteMapping("/{quizId}/remove/{questionId}")
    public ResponseEntity<?> removeQuestionFromQuiz(
            @PathVariable Integer quizId,
            @PathVariable Integer questionId) {
        return service.removeQuestionFromQuiz(quizId, questionId);
    }

}
