package com.example.demo.dto;


import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    private Long id;

    private String writer;

    private String contents;

    private Long boardId;

    public Comment toEntity() {
        return Comment.builder()
                .id(id)
                .writer(writer)
                .contents(contents)
                .build();
    }

    public static CommentDTO toCommentDTO(Comment comment, Long boardId) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setWriter(comment.getWriter());
        commentDTO.setContents(comment.getContents());
        commentDTO.setBoardId(boardId);
        return commentDTO;
    }
}