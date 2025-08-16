package com.subha.quizSystem.controller;

import com.subha.quizSystem.dto.QuestionDTO; // Import the DTO
import com.subha.quizSystem.model.Question;
import com.subha.quizSystem.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/all")
    public ResponseEntity<List<QuestionDTO>> getAllQuestions() {
        return questionService.fetchAllQuestions();
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<QuestionDTO>> getAllQuestionsByCategory(@PathVariable String category) {
        return questionService.fetchAllQuestionsByCategory(category);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addQuestion(@RequestBody QuestionDTO questionDTO) {
        return questionService.addQuestion(questionDTO);
    }

    @PutMapping("/update/{qId}")
    public ResponseEntity<String> updateQuestion(@PathVariable Integer qId, @RequestBody QuestionDTO questionDTO) {
        System.out.println("The update question"+ questionDTO.toString());
        return questionService.updateQuestion(qId, questionDTO);
    }

    @DeleteMapping("/delete/{qId}")
    public ResponseEntity<String> deleteQuestion(@PathVariable Integer qId) {
        return questionService.deleteQuestion(qId);
    }
}