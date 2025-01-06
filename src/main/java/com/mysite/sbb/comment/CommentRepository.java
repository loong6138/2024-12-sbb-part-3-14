package com.mysite.sbb.comment;

import com.mysite.sbb.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findAll(Pageable pageable);

    List<Comment> findByQuestion(Question question);
}
