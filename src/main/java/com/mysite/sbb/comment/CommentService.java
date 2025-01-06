package com.mysite.sbb.comment;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment createAtQuestion(Question question, String content, SiteUser siteUser) {
        Comment comment = new Comment();
        comment.setQuestion(question);
        comment.setContent(content);
        comment.setCreateDate(LocalDateTime.now());
        comment.setAuthor(siteUser);
        return this.commentRepository.save(comment);
    }

    public Comment createAtAnswer(Answer answer, String content, SiteUser siteUser) {
        Comment comment = new Comment();
        comment.setAnswer(answer);
        comment.setContent(content);
        comment.setCreateDate(LocalDateTime.now());
        comment.setAuthor(siteUser);
        return this.commentRepository.save(comment);
    }

    public Page<Comment> getList(int page) {
        Pageable pageable = PageRequest.of(page, 5);
        return this.commentRepository.findAll(pageable);
    }

    public Comment getComment(int id) {
        Optional<Comment> comment = this.commentRepository.findById(id);
        if (comment.isPresent()) {
            return comment.get();
        } else {
            throw new DataNotFoundException("comment not found");
        }
    }

    public List<Comment> getList(Question question) {
        return this.commentRepository.findByQuestion(question);
    }

    public List<Comment> getList() {
        return this.commentRepository.findAll();
    }

    public void modify(Comment comment, String content) {
        comment.setContent(content);
        comment.setModifyDate(LocalDateTime.now());
        this.commentRepository.save(comment);
    }

    public void delete(Comment comment) {
        this.commentRepository.delete(comment);
    }
}
