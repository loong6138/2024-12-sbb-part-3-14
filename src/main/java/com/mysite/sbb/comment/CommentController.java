package com.mysite.sbb.comment;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequestMapping("/comment")
@RequiredArgsConstructor
@Controller
public class CommentController {

    private final CommentService commentService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    @GetMapping("/list")
    public String commentList(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        Page<Comment> paging = this.commentService.getList(page);
        model.addAttribute("paging", paging);
        return "comment_list";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/question/{id}")
    public String createCommentAtQuestion(@Valid CommentForm commentForm, BindingResult bindingResult, @PathVariable("id") Integer id, Principal principal, Model model) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }
        Comment comment = this.commentService.createAtQuestion(question, commentForm.getContent(), siteUser);
        return String.format("redirect:/question/detail/%s#comment_%s", comment.getQuestion().getId(), comment.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/answer/{id}")
    public String createCommentAtAnswer(@Valid CommentForm commentForm, BindingResult bindingResult, @PathVariable("id") Integer id, Principal principal, Model model) {
        Answer answer = this.answerService.getAnswer(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("answer", answer);
            return "question_detail";
        }
        Comment comment = this.commentService.createAtAnswer(answer, commentForm.getContent(), siteUser);
        return String.format("redirect:/question/detail/%s#answer_%s#comment_%s", answer.getQuestion().getId(), answer.getId(), comment.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modifyComment(CommentForm commentForm, @PathVariable("id") Integer id, Principal principal, Model model) {
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        commentForm.setContent(comment.getContent());
        return "comment_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modifyComment(@Valid CommentForm commentform, BindingResult bindingResult, @PathVariable("id") Integer id, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "comment_form";
        }
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        this.commentService.modify(comment, commentform.getContent());

        if (comment.getQuestion() != null) {
            return String.format("redirect:/question/detail/%s#comment_%s", comment.getQuestion().getId(), comment.getId());
        }
        return String.format("redirect:/question/detail/%s#answer_%s#comment_%s", comment.getAnswer().getQuestion().getId(), comment.getAnswer().getId(), comment.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String deleteComment(@PathVariable("id") Integer id, Principal principal, Model model) {
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        this.commentService.delete(comment);
        return String.format("redirect:/question/detail/%s", comment.getQuestion().getId());
    }
}
