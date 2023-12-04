    package com.example.demo.entity;

    import com.example.demo.dto.CommentDTO;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.NoArgsConstructor;

    import javax.persistence.*;

    @NoArgsConstructor
    @Getter
    @Entity
    public class Comment {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(length = 50)
        private String writer;

        // ** 내용
        @Column(length = 50)
        private String contents;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "board_id")
        private Board board;

        @Builder
        public Comment(Long id, String writer, String contents, Board board) {
            this.id = id;
            this.writer = writer;
            this.contents = contents;
            this.board = board;
        }

        public Comment assignToBoard(Board board) {
            Comment comment = new Comment();
            this.board = board;
            return comment;
        }
        public void updateFromDTO(CommentDTO commentDTO){

            // ** 모든 변경 사항을 셋팅.
            this.writer = commentDTO.getWriter();
            this.contents = commentDTO.getContents();
        }
        public Long getBoardId() {
            if (this.board != null) {
                return this.board.getId();
            }
            return null;
        }

        public void linkBoard(Board board) {
            this.board = board;
        }
    }