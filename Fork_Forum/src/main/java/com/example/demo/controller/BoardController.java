package com.example.demo.controller;

import com.example.demo.dto.BoardDTO;
import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.FileDTO;
import com.example.demo.entity.Board;
import com.example.demo.entity.BoardFile;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.service.BoardService;
import com.example.demo.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final FileRepository fileRepository;
    private final CommentService commentService;

    @GetMapping("/create")
    public String create(){
        return "create";
    }

    @GetMapping(value = {"/paging", "/"})
    public String paging(@PageableDefault(page = 1) Pageable pageable, Model model){

        Page<BoardDTO> boards = boardService.paging(pageable);

        int blockLimit = 3;
        int startPage = (int)(Math.ceil((double)pageable.getPageNumber() / blockLimit) - 1) * blockLimit + 1;
        int endPage = ((startPage + blockLimit - 1)
                < boards.getTotalPages()) ? (startPage + blockLimit - 1) : boards.getTotalPages();

        model.addAttribute("boardList", boards);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage",endPage);

        return "paging";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {

        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("board", boardDTO);
        return "update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute BoardDTO boardDTO) {
        boardService.update(boardDTO);
        return "redirect:/board/";
    }

    @GetMapping("/{id}")
    public String paging(@PathVariable Long id, Model model,
                         @PageableDefault(page = 1) Pageable pageable){

        BoardDTO dto = boardService.findById(id);

        model.addAttribute("board", dto);
        model.addAttribute("page", pageable.getPageNumber());

        // 댓글 데이터 불러오기
        List<CommentDTO> comments = commentService.findAll(id);
        model.addAttribute("commentList", comments);

        List<BoardFile> byBoardId = fileRepository.findByBoardId(id);
        model.addAttribute("files", byBoardId);

        return "detail";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO,
                       @RequestParam MultipartFile[] files) throws IOException {

        boardDTO.setCreateTime(LocalDateTime.now());
        boardService.save(boardDTO, files);

        return "redirect:/board/";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, FileDTO fileDTO) {
        System.out.println(id);
        boardService.delete(id);
        return "redirect:/board/paging";
    }
}