package com.unannn.study.domain.posts;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
@SequenceGenerator(
        name = "POSTS_SEQ_GENERATOR",
        sequenceName = "POSTS_SEQ", // 매핑할 데이터베이스 시퀀스 이름
        initialValue = 1,
        allocationSize = 50
)
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POSTS_SEQ_GENERATOR")
    private Long id;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT",nullable = false)
    private String content;

    private String author;

    @Builder
    public Posts(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

}
