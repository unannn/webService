package com.unannn.study.web;

import com.unannn.study.domain.posts.Posts;
import com.unannn.study.domain.posts.PostsRepository;
import com.unannn.study.web.dto.PostsSaveRequestDto;
import com.unannn.study.web.dto.PostsUpdateRequestDto;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @After
    public void tearDown() throws Exception{
        postsRepository.deleteAll();
    }

    @Test
    public void ports_등록() throws Exception {
        //given
        String title = "title";
        String content = "content";
        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                .title(title)
                .content(content)
                .author("author")
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts";
        //when
        ResponseEntity<Long> responseEntity = restTemplate
                .postForEntity(url, requestDto, Long.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title );
        assertThat(all.get(0).getContent()).isEqualTo(content);
    }

    @Test
    public void posts_수정() throws Exception {
        //given

        Posts posts = Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build();
        Posts savedPosts = postsRepository.save(posts);

        Long updateId =savedPosts.getId();
        String title = "title2";
        String content = "content2";

        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                .title(title)
                .content(content)
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts/" + updateId;

        HttpEntity<PostsUpdateRequestDto> requestDtoHttpEntity = new HttpEntity<>(requestDto);

        //when
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestDtoHttpEntity, Long.class);

        //then
        System.out.println(requestDtoHttpEntity);
        System.out.println(responseEntity);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent()).isEqualTo(content);
    }

    @Test
    public void 아이디로_조회() throws Exception {

        Posts posts = Posts.builder()
                .title("title")
                .author("author")
                .content("content")
                .build();

        //given
        Posts savedPosts = postsRepository.save(posts);

        //when
        Posts findPosts = postsRepository.findById(savedPosts.getId()).orElseThrow(() ->
                new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        //then
        assertThat(savedPosts.getId()).isEqualTo(findPosts.getId());
        assertThat(savedPosts.getTitle()).isEqualTo(findPosts.getTitle());
        assertThat(savedPosts.getContent()).isEqualTo(findPosts.getContent());
        assertThat(savedPosts.getAuthor()).isEqualTo(findPosts.getAuthor());
    }

    @Test
    public void posts_삭제() throws Exception {
        //given
        Posts savedPosts = postsRepository.save(Posts.builder()
                .title("title")
                .author("author")
                .content("content")
                .build());

        String url = "http://localhost:" + port + "/api/v1/posts/" + savedPosts.getId();
        HttpEntity<Long> requestDtoHttpEntity = new HttpEntity<>(savedPosts.getId());

        //when
        restTemplate.delete(url, Long.class);

        //then
        List<Posts> all = postsRepository.findAll();

        assertThat(all.size()).isEqualTo(0);
    }

}