package com.example.demo.service;

import com.example.demo.dto.CommentDTO;
import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;


    @Transactional
    public Comment save(CommentDTO commentDTO) {
        Optional<Board> optionalBoard =
                boardRepository.findById(commentDTO.getBoardId());

        if(optionalBoard.isPresent()) {
            Board board = optionalBoard.get();

            Comment entity = commentDTO.toEntity();
            entity.assignToBoard(board);
            return commentRepository.save(entity);
        } else {
            return null;
        }
    }

    public List<CommentDTO> findAll(Long boardId) {
        Board boardEntity = boardRepository.findById(boardId).get();
        java.util.List<Comment> commentEntityList = commentRepository.findAllByBoardOrderByIdDesc(boardEntity);
        /* EntityList -> DTOList */
        List<com.example.demo.dto.CommentDTO> commentDTOList = new ArrayList<>();
        for (Comment commentEntity: commentEntityList) {
            com.example.demo.dto.CommentDTO commentDTO = com.example.demo.dto.CommentDTO.toCommentDTO(commentEntity, boardId);
            commentDTOList.add(commentDTO);
        }
        return commentDTOList;
    }

    @Transactional
    public void update(CommentDTO commentDTO){
        log.info("CommentDTO id: {}", commentDTO.getId());
        log.info("CommentDTO boardId: {}", commentDTO.getBoardId());

        Comment comment = commentRepository.findById(commentDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 댓글이 존재하지 않습니다."));

        comment.updateFromDTO(commentDTO);

        commentRepository.save(comment);
    }


    public Optional<Comment> findById(Long id){
        return commentRepository.findById(id);
    }

    @Transactional
    public void delete(Long id){
        commentRepository.deleteById(id);
    }
}