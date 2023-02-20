package Maswillaeng.MSLback.service;

import Maswillaeng.MSLback.domain.entity.Post;
import Maswillaeng.MSLback.domain.entity.User;
import Maswillaeng.MSLback.domain.repository.PostRepository;
import Maswillaeng.MSLback.domain.repository.UserRepository;
import Maswillaeng.MSLback.dto.post.request.PostRequestDto;
import Maswillaeng.MSLback.dto.post.request.PostUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addPost(Long id, PostRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalStateException("없는 회원")
                );
        Post post = dto.toEntity(user);
        postRepository.save(post);
    }

    /**
     * 게시글 수정
     * @param id
     * @param dto
     */
    @Transactional
    public void updatePost(Long id, PostUpdateRequestDto dto) {
        /*
        굳이 게시글 하나만 찾는 findOne이 필요할까?
        그냥 Repository에서 바로 불러와도 될 거 같은뎅
        근데 Optional때문에 놔둬도 될 거 같기도 하고 (가독성)
         */
        Post post = findOne(id);
        post.update(dto);
    }


    /**
     * 게시글 상세 조회
     * @param id
     */
    public Post getPost(Long id) {
        Post post = findOne(id);
        return post;
    }

    /**
     * 게시글 전체 조회
     */
    public List<Post> getAllPost() {
        List<Post> post = postRepository.findAll();
        return post;
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deletePost(Long postId, Long myId) {

        Post post = findOne(postId);
        if (post.getUser().getId() != myId) {
            throw new IllegalStateException("내가 등록한 글이 아니기 때문에 삭제 불가능합니다");
        }
        System.out.println("log.info(\"deletePost, postId = {}\", postId);");
        postRepository.deleteById(post.getId());
    }





    /**
     * post_id를 이용해서 게시글 불러오기
     * @param id
     */
    private Post findOne(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalStateException("게시글을 찾을 수 없습니다.")
                );

        return post;
    }
}
