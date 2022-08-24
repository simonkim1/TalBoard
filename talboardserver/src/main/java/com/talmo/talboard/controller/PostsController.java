package com.talmo.talboard.controller;

import com.talmo.talboard.config.ResponseObject;
import com.talmo.talboard.domain.Member;
import com.talmo.talboard.domain.Post;
import com.talmo.talboard.domain.vo.PostCreateVO;
import com.talmo.talboard.repository.MemberRepository;
import com.talmo.talboard.repository.PostRepository;
import com.talmo.talboard.service.PostService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PostsController {
    private final PostService postService;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;


    @ApiOperation(value="게시글 작성")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK : 게시글 작성 성공"),
            @ApiResponse(code = 400, message = "Bad Request : 데이터 유효성 검사 실패"),
            @ApiResponse(code = 401, message = "Unauthorized : 게시글 작성 권한 없음"),
            @ApiResponse(code = 404, message = "Not Found : 게시글 정보를 찾지 못함")
    })
    @PostMapping("/posts/create")
    public ResponseEntity<Map<String, Object>> createPost(PostCreateVO vo) {
        try {
            Member member = memberRepository.findOne(vo.getMember_no());
            Post post = Post.create(vo, member);
            Long post_no = postService.create(post);
            return ResponseEntity.ok()
                    .body(ResponseObject.create(post_no, "게시글 등록 성공"));
        }
        catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ResponseObject.create(null, e.getMessage()));
        }
        catch(IllegalStateException e) {
            return new ResponseEntity<>(ResponseObject.create(null, e.getMessage()), HttpStatus.CONFLICT);
        }
    }
    @ApiOperation(value="전체 게시글 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK : 전체 게시글 조회 성공"),
            @ApiResponse(code = 404, message = "Not Found : 전체 게시글 조회 실패")
    })
    @GetMapping("/posts")
    public List<Post> getAllPosts() {
        return null;
    }
    
    @ApiOperation(value="게시글 상세 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK : 게시글 상세 조회 성공"),
            @ApiResponse(code = 404, message = "Not Found : 게시글 상세 조회 실패")
    })
    @GetMapping("/posts/{post_no}")
    public List<Post> getDetailOfPost() {
        return null;
    }

    @ApiOperation(value="게시글 수정")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK : 게시글 수정 성공"),
            @ApiResponse(code = 400, message = "Bad Request : 데이터 유효성 검사 실패"),
            @ApiResponse(code = 401, message = "Unauthorized : 수정 권한 없음"),
            @ApiResponse(code = 404, message = "Not Found : 게시글 수정 실패")
    })
    @PatchMapping("/posts/{post_no}")
    public List<Post> updatePost() {
        return null;
    }

    @ApiOperation(value="게시글 삭제")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK : 게시글 삭제 성공"),
            @ApiResponse(code = 401, message = "Unauthorized : 삭제 권한 없음"),
            @ApiResponse(code = 404, message = "Not Found : 게시글 삭제 실패")
    })
    @DeleteMapping("/posts/{post_no}")
    public List<Post> deletePost() {
        return null;
    }

    @ApiOperation(value="게시글 추천/비추천 수 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK : 게시글 추천/비추천 수 조회 성공"),
            @ApiResponse(code = 404, message = "Not Found : 게시글 추천/비추천 수 조회 실패")
    })
    @GetMapping("/posts/{post_no}/like")
    public List<Post> getLikeAndDislike() {
        return null;
    }

    @ApiOperation(value="게시글 추천/비추천")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK : 게시글 추천/비추천 성공"),
            @ApiResponse(code = 400, message = "Bad Request : 데이터 유효성 검사 실패"),
            @ApiResponse(code = 404, message = "Not Found : 게시글 추천/비추천 실패")
    })
    @PostMapping("/posts/{post_no}/like")
    public List<Post> setLikeAndDislike() {
        return null;
    }

    @ApiOperation(value="댓글 작성")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK : 댓글 작성 성공"),
            @ApiResponse(code = 400, message = "Bad Request : 데이터 유효성 검사 실패"),
            @ApiResponse(code = 404, message = "Not Found : 댓글 작성 실패")
    })
    @PostMapping("/posts/{post_no}/comment")
    public List<Post> setComment() {
        return null;
    }

    @ApiOperation(value="댓글 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK : 조회 성공"),
            @ApiResponse(code = 404, message = "Not Found : 댓글 조회 실패")
    })
    @GetMapping("/posts/{post_no}/comment")
    public List<Post> getComment() {
        return null;
    }

    @ApiOperation(value="댓글 수정")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK : 댓글 수정 성공"),
            @ApiResponse(code = 400, message = "Bad Request : 데이터 유효성 검사 실패"),
            @ApiResponse(code = 401, message = "Unauthorized : 수정 권한 없음"),
            @ApiResponse(code = 404, message = "Not Found : 댓글 수정 실패")
    })
    @PatchMapping("/posts/{post_no}/comment")
    public List<Post> updateComment() {
        return null;
    }

    @ApiOperation(value="댓글 삭제")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK : 댓글 삭제 성공"),
            @ApiResponse(code = 401, message = "Unauthorized : 삭제 권한 없음"),
            @ApiResponse(code = 404, message = "Not Found : 댓글 삭제 실패")
    })
    @DeleteMapping("/posts/{post_no}/comment")
    public List<Post> deleteComment() {
        return null;
    }

    @ApiOperation(value="게시글 신고")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK : 게시글 신고 성공"),
            @ApiResponse(code = 404, message = "Not Found : 사용자 또는 게시글 번호 조회 실패")
    })
    @PostMapping("/posts/{post_no}/report")
    public List<Post> reportPost() {
        return null;
    }

    @ApiOperation(value="게시글 검색")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK : 게시글 검색 성공"),
            @ApiResponse(code = 404, message = "Not Found : 게시글 검색 실패")
    })
    @GetMapping("/posts/search")
    public List<Post> getPost() {
        return null;
    }

    @ApiOperation(value="신고된 글 목록 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK : 신고된 글 목록 조회 성공"),
            @ApiResponse(code = 404, message = "Not Found : 신고된 글 목록 조회 실패")
    })
    @GetMapping("/posts/report")
    public List<Post> getAllReportedPosts() {
        return null;
    }
}
