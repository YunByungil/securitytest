package Maswillaeng.MSLback.controller;

import Maswillaeng.MSLback.domain.entity.Post;
import Maswillaeng.MSLback.dto.post.reponse.PostDetailResponse;
import Maswillaeng.MSLback.dto.post.reponse.PostListResponse;
import Maswillaeng.MSLback.dto.post.reponse.PostResponse;
import Maswillaeng.MSLback.dto.post.request.PostDetailDto;
import Maswillaeng.MSLback.dto.post.request.PostListDto;
import Maswillaeng.MSLback.dto.post.request.PostRequestDto;
import Maswillaeng.MSLback.dto.post.request.PostUpdateRequestDto;
import Maswillaeng.MSLback.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    @GetMapping()
    public String index() {
        return "index";
    }

    @PostMapping("/post")
    public PostResponse addPost(Authentication authentication, @RequestBody PostRequestDto postRequestDto) {
        // TODO: @Valid

        log.info(authentication.getName());
        System.out.println("id = " + authentication.getName());
        Long id = Long.parseLong(authentication.getName());
        log.info("content = {}", postRequestDto.getContent());
        postService.addPost(id, postRequestDto);

        return new PostResponse(HttpStatus.OK.value());
    }

    @PutMapping("/post/{postId}")
    public PostResponse updatePost(@PathVariable Long postId, @RequestBody PostUpdateRequestDto dto) {
        postService.updatePost(postId, dto);
        return new PostResponse(HttpStatus.OK.value());
    }

    /**
     * 게시글 상세 조회
     */
    @GetMapping("/post/{postId}")
    public PostDetailResponse getPost(@PathVariable Long postId) {
        Post post = postService.getPost(postId);
        PostDetailDto dto = new PostDetailDto(post);
        return new PostDetailResponse(HttpStatus.OK.value(), dto);
    }

    /**
     * 게시글 전체 조회
     */
    @GetMapping("/post/page")
    public PostListResponse getAllPost() {
        List<Post> allPost = postService.getAllPost();
        List<PostListDto> collect = allPost.stream()
                .map(p -> new PostListDto(p.getId(), p.getUser().getNickname(), p.getThumbnail(), p.getTitle()))
                .collect(Collectors.toList());

        return new PostListResponse(collect.size(), HttpStatus.OK.value(), collect);
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/post/{postId}")
    public PostResponse deletePost(@PathVariable Long postId, Authentication authentication) {
        /*
        내 게시글이 맞는지 확인하는 검증
         */
        Long myId = Long.parseLong(authentication.getName());
        log.info("deletePost, postId = {}", postId);
        postService.deletePost(postId, myId);
        return new PostResponse(HttpStatus.OK.value());
    }
}
