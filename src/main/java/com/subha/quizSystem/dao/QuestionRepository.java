package com.subha.quizSystem.dao;

import com.subha.quizSystem.model.Question;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findByCategory(String category);

    @Query(value = "SELECT * FROM Question q WHERE q.category = :category", nativeQuery = true)
    List<Question> getQuestionsForQuizByCategory(@Param("category") String category, Pageable pageable);
}
