package com.example.demo.controller;

import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.FileDTO;
import com.example.demo.entity.Comment;
import com.example.demo.repository.CommentRepository;
import com.example.demo.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;
    private final CommentRepository commentRepository;

    @PostMapping("/save")
    public ResponseEntity<?> save(@ModelAttribute CommentDTO commentDTO) {

        System.out.println(commentDTO);
        Comment comment = commentService.save(commentDTO);

        List<CommentDTO> all = commentService.findAll(commentDTO.getBoardId());

        if (comment != null) {
            return new ResponseEntity<>(all, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("게시글이 없음.", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/update/{id}")
    public String updateCommentForm(@PathVariable Long id, Model model) {
        Optional<Comment> optionalComment = commentService.findById(id);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            model.addAttribute("comment", comment);

            // boardId를 Comment 객체에서 가져올 수 있는 경우
            if (comment.getBoard() != null) {
                model.addAttribute("boardId", comment.getBoard().getId());
            } else {
                // Comment 객체의 Board가 null인 경우, boardId를 null로 설정
                model.addAttribute("boardId", null);
            }
        }
        return "updatecomment";
    }

    @PostMapping("/update")
    public String updateComment(@ModelAttribute CommentDTO commentDTO) {
        commentService.update(commentDTO);
        return "redirect:/board/" + commentDTO.getBoardId();
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        commentService.delete(id);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}