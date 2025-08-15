package com.subha.quizSystem.service;

import com.subha.quizSystem.dao.QuestionRepository;
import com.subha.quizSystem.dto.QuestionDTO;
import com.subha.quizSystem.exception.NotFoundException;
import com.subha.quizSystem.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository repo;

    // --- Helper to convert Entity -> DTO (for sending data to frontend) ---
    private QuestionDTO convertToDto(Question question) {
        if (question == null) {
            return null;
        }
        return new QuestionDTO(
                question.getQId(),
                question.getQuestionTitle(),
                question.getOption1(),
                question.getOption2(),
                question.getOption3(),
                question.getOption4(),
                question.getCorrectAnswer(),
                question.getCategory(),
                question.getDifficultyLevel()
        );
    }

    // --- Helper to convert DTO -> Entity (for receiving data from frontend) ---
    private Question convertToQuestion(QuestionDTO questionDTO) {
        Question question = new Question();
        // NOTE: We don't set the qId here for a new question, as it will be generated.
        question.setQuestionTitle(questionDTO.getQuestionTitle());
        question.setOption1(questionDTO.getOption1());
        question.setOption2(questionDTO.getOption2());
        question.setOption3(questionDTO.getOption3());
        question.setOption4(questionDTO.getOption4());
        question.setCorrectAnswer(questionDTO.getCorrectAnswer());
        question.setCategory(questionDTO.getCategory());
        question.setDifficultyLevel(questionDTO.getDifficultyLevel());
        return question;
    }

    // --- Service Methods (Now fully using DTOs) ---

//    public ResponseEntity<List<QuestionDTO>> fetchAllQuestions() {
//        try {
//            List<Question> questions = repo.findAll();
//            List<QuestionDTO> questionDTOs = questions.stream()
//                    .map(this::convertToDto)
//                    .collect(Collectors.toList());
//            return new ResponseEntity<>(questionDTOs, HttpStatus.OK);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
//        }
//    }

    public ResponseEntity<List<QuestionDTO>> fetchAllQuestions() {
        try{
            List<Question> questions = repo.findAll();

            List<QuestionDTO> questionDTOs = questions.stream()
                    .map(q -> new QuestionDTO(
                            q.getQId(),
                            q.getQuestionTitle(),
                            q.getOption1(),
                            q.getOption2(),
                            q.getOption3(),
                            q.getOption4(),
                            q.getCorrectAnswer(),
                            q.getCategory(),
                            q.getDifficultyLevel()
                    ))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(questionDTOs, HttpStatus.OK);

        } catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<QuestionDTO>> fetchAllQuestionsByCategory(String category) {
        try {
            List<Question> questions = repo.findByCategory(category);
            List<QuestionDTO> questionDTOs = questions.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(questionDTOs, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }
    }

    // UPDATE: This method now accepts a DTO
    public ResponseEntity<String> addQuestion(QuestionDTO questionDTO) {
        try {
            // Convert the DTO to a Question entity before saving
            Question question = convertToQuestion(questionDTO);
            Question savedQuestion = repo.save(question);
            return new ResponseEntity<>("saved record in DB.\n" + savedQuestion, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("failed persisting in DB", HttpStatus.BAD_REQUEST);
        }
    }

    // UPDATE: This method now accepts a DTO
    public ResponseEntity<String> updateQuestion(Integer id, QuestionDTO questionDTO) {
        try {
            Question existingQuestion = repo.findById(id)
                    .orElseThrow(() -> new NotFoundException("Question not found with id: " + id));

            // Update the existing entity with data from the DTO
            existingQuestion.setQuestionTitle(questionDTO.getQuestionTitle());
            existingQuestion.setOption1(questionDTO.getOption1());
            existingQuestion.setOption2(questionDTO.getOption2());
            existingQuestion.setOption3(questionDTO.getOption3());
            existingQuestion.setOption4(questionDTO.getOption4());
            existingQuestion.setCorrectAnswer(questionDTO.getCorrectAnswer());
            existingQuestion.setCategory(questionDTO.getCategory());
            existingQuestion.setDifficultyLevel(questionDTO.getDifficultyLevel());

            repo.save(existingQuestion);
            return new ResponseEntity<>("updated record in DB.\n" + existingQuestion, HttpStatus.OK);
        } catch (NotFoundException nfe) {
            return new ResponseEntity<>(nfe.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("failed persisting in DB", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> deleteQuestion(Integer id) {
        try {
            Question record = repo.findById(id)
                    .orElseThrow(() -> new NotFoundException("Question not found.\nqId= " + id + " not found."));
            repo.delete(record);
            return new ResponseEntity<>("record for qId: " + id + " removed.\n" + record, HttpStatus.OK);
        } catch (NotFoundException nfe) {
            return new ResponseEntity<>(nfe.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("something went wrong", HttpStatus.BAD_REQUEST);
        }
    }
}