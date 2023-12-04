    package com.example.demo.service;


    import com.example.demo.dto.BoardDTO;
    import com.example.demo.entity.Board;
    import com.example.demo.entity.BoardFile;
    import com.example.demo.repository.BoardRepository;
    import com.example.demo.repository.FileRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.File;
    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.util.List;
    import java.util.Optional;
    import java.util.UUID;

    @Transactional(readOnly = true)
    @Service
    @RequiredArgsConstructor
    public class BoardService {
        private final BoardRepository boardRepository;
        private final FileRepository fileRepository;

        // ** 학원에서는 /G/
        // ** 집에서는 본인 PC 이름.
        private final String filePath = "C:/Users/G/Desktop/green/Board Files/";


        // ** paging 을 함수
        public Page<BoardDTO> paging(Pageable pageable) {

            // ** 페이지 시작 번호 셋팅
            int page = pageable.getPageNumber() - 1;

            // ** 페이지에 포함될 게시물 개수
            int size = 5;

            // ** 전체 게시물을 불러온다.
            Page<Board> boards = boardRepository.findAll(
                    PageRequest.of(page, size));

            return boards.map(board -> new BoardDTO(
                    board.getId(),
                    board.getTitle(),
                    board.getContents(),
                    board.getCreateTime(),
                    board.getUpdateTime() ));
        }

        public BoardDTO findById(Long id) {

            //if(boardRepository.findById(id).isPresent()) ... 예외처리 생략
            Board board = boardRepository.findById(id).get();
            return BoardDTO.toBoardDTO(board);
        }

        @Transactional
        public void save(BoardDTO dto, MultipartFile[] files) throws IOException {
            //boardRepository.save(dto.toEntity());

            // 게시글 DB에 저장 후 pk를 받아옴
            Long id = boardRepository.save(dto.toEntity()).getId();
            Board board = boardRepository.findById(id).get();

            // 만약 파일이 없을 경우의 예외처리
            if (files == null || files.length == 0 || files[0].isEmpty()) {
                return;
            }

            // ** 파일 정보 저장.
            for(MultipartFile file : files) {
                if (file.isEmpty()){
                    continue;
                }

                Path uploadPath = Paths.get(filePath);

                // ** 만약 경로가 없다면... 경로 생성.
                if(!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // ** 파일명 추출
                String originalFileName = file.getOriginalFilename();

                // ** 확장자 추출
                String formatType = originalFileName.substring(
                        originalFileName.lastIndexOf("."));

                // ** UUID 생성
                String uuid = UUID.randomUUID().toString();

                // 경로 지정
                String path = filePath + uuid + originalFileName;
                // 경로에 파일을 저장 (DB의 저장이 아님)
                file.transferTo(new File(path));

                BoardFile boardFile = BoardFile.builder()
                        .filePath(filePath)
                        .fileName(originalFileName)
                        .uuid(uuid)
                        .fileType(formatType)
                        .fileSize(file.getSize())
                        .board(board)
                        .build();

                fileRepository.save(boardFile);
            }
        }

        @Transactional
        public void delete(Long id) {

            List<BoardFile> boardFiles = fileRepository.findByBoardId(id);

            for (BoardFile file : boardFiles) {
                File targetFile = new File(file.getFilePath() + file.getUuid() + file.getFileName());
                if (targetFile.exists()) {
                    targetFile.delete();
                }
                fileRepository.delete(file);
            }

            boardRepository.deleteById(id);
        }

        @Transactional
        public void update(BoardDTO boardDTO) {
            Optional<Board> boardOptional = boardRepository.findById(boardDTO.getId());

            //if(boardOptional.isPresent()) ... 예외처리 생략
            Board board = boardOptional.get();

            board.updateFromDTO(boardDTO);

            boardRepository.save(board);
        }
    }