package com.talmo.talboard.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.talmo.talboard.config.ResponseConstants;
import com.talmo.talboard.config.TestHelper;
import com.talmo.talboard.controller.MemberController;
import com.talmo.talboard.domain.Member;
import com.talmo.talboard.domain.Post;
import com.talmo.talboard.domain.vo.MemberBlockVO;
import com.talmo.talboard.domain.vo.MemberDataChangeVO;
import com.talmo.talboard.domain.vo.MemberFindIdVO;
import com.talmo.talboard.domain.vo.MemberFindPasswordVO;
import com.talmo.talboard.domain.vo.MemberJoinVO;
import com.talmo.talboard.domain.vo.MemberNoVO;
import com.talmo.talboard.domain.vo.MemberResignVO;
import com.talmo.talboard.config.ExceptionConstants;
import com.talmo.talboard.domain.vo.PostListVO;
import com.talmo.talboard.repository.MemberRepository;
import com.talmo.talboard.repository.PostRepository;
import com.talmo.talboard.service.BlockService;
import com.talmo.talboard.service.MemberService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class MemberIntegrationTest {
    @Autowired MemberService memberService;
    @Autowired BlockService blockService;
    @Autowired MemberController memberController;
    @Autowired PostRepository postRepository;
    @Autowired MemberRepository memberRepository;

    @Test
    void join() {
        // given
        MemberJoinVO vo = new MemberJoinVO(TestHelper.testId, TestHelper.testPw, TestHelper.testEmail);

        // when
        Map<String, Object> body = memberController.join(vo).getBody();

        // then
        assertEquals(ResponseConstants.REGIST_SUCCESS_MESSAGE, body.get("message"));
    }

    @Test
    void join_유효성실패() {
        // given
        MemberJoinVO vo = new MemberJoinVO(TestHelper.failId, TestHelper.testPw, TestHelper.testEmail);
        MemberJoinVO vo2 = new MemberJoinVO(TestHelper.testId, TestHelper.failPw, TestHelper.testEmail);
        MemberJoinVO vo3 = new MemberJoinVO(TestHelper.testId, TestHelper.testPw, TestHelper.failEmail);

        // when
        ResponseEntity<Map<String, Object>> res = memberController.join(vo);
        Map<String, Object> body = res.getBody();
        ResponseEntity<Map<String, Object>> res2 = memberController.join(vo2);
        Map<String, Object> body2 = res2.getBody();
        ResponseEntity<Map<String, Object>> res3 = memberController.join(vo3);
        Map<String, Object> body3 = res3.getBody();

        // then
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertNull(body.get("data"));
        assertEquals(ExceptionConstants.INVALID_ID_MESSAGE, body.get("message"));

        assertEquals(HttpStatus.BAD_REQUEST, res2.getStatusCode());
        assertNull(body2.get("data"));
        assertEquals(ExceptionConstants.INVALID_PW_MESSAGE, body2.get("message"));

        assertEquals(HttpStatus.BAD_REQUEST, res3.getStatusCode());
        assertNull(body3.get("data"));
        assertEquals(ExceptionConstants.INVALID_EMAIL_MESSAGE, body3.get("message"));
    }

    @Test
    void join_중복아이디이메일() {
        // given
        memberService.join(TestHelper.createMember());
        MemberJoinVO vo = TestHelper.createMemberJoinVO();
        vo.setEmailAddress(TestHelper.testEmail2);
        MemberJoinVO vo2 = TestHelper.createMemberJoinVO();
        vo2.setId(TestHelper.testId2);

        // when
        ResponseEntity<Map<String, Object>> res = memberController.join(vo);
        Map<String, Object> body = res.getBody();
        ResponseEntity<Map<String, Object>> res2 = memberController.join(vo2);
        Map<String, Object> body2 = res2.getBody();

        // then
        assertEquals(HttpStatus.CONFLICT, res.getStatusCode());
        assertNull(body.get("data"));
        assertEquals(ExceptionConstants.DUPLICATE_ID_MESSAGE, body.get("message"));

        assertEquals(HttpStatus.CONFLICT, res2.getStatusCode());
        assertNull(body2.get("data"));
        assertEquals(ExceptionConstants.DUPLICATE_EMAIL_MESSAGE, body2.get("message"));

    }

    @Test
    void resign() {
        // given
        Member member = TestHelper.createMember(1);
        Member member2 = TestHelper.createMember(2);
        Member member3 = TestHelper.createMember(3);
        member.setAdminYn(true);
        memberService.join(member);
        memberService.join(member2);
        memberService.join(member3);

        // when
        MemberResignVO vo = new MemberResignVO(member.getMemberNo(), member2.getMemberNo());
        Map<String, Object> body = memberController.resign(vo).getBody();

        MemberResignVO vo2 = new MemberResignVO(member3.getMemberNo(), member3.getMemberNo());
        Map<String, Object> body2 = memberController.resign(vo2).getBody();

        // then
        assertNull(body.get("data"));
        assertEquals(ResponseConstants.RESIGN_SUCCESS_MESSAGE, body.get("message"));
        assertNull(body2.get("data"));
        assertEquals(ResponseConstants.RESIGN_SUCCESS_MESSAGE, body2.get("message"));
    }

    @Test
    void resign_권한없음() {
        // given
        Member member = TestHelper.createMember(1);
        Member member2 = TestHelper.createMember(2);
        memberService.join(member);
        memberService.join(member2);

        // when
        MemberResignVO vo = new MemberResignVO(member.getMemberNo(), member2.getMemberNo());
        Map<String, Object> body = memberController.resign(vo).getBody();

        // then
        assertNull(body.get("data"));
        assertEquals(ExceptionConstants.NO_AUTHORIZE_MESSAGE, body.get("message"));
    }

    @Test
    void resign_회원찾기실패() {
        // given
        Member member = TestHelper.createMember();
        memberService.join(member);
        MemberResignVO vo = new MemberResignVO(member.getMemberNo(), -1L);
        MemberResignVO vo2 = new MemberResignVO(-1L, member.getMemberNo());
        MemberResignVO vo3 = new MemberResignVO(-1L, -1L);

        // when
        Map<String, Object> body = memberController.resign(vo).getBody();
        Map<String, Object> body2 = memberController.resign(vo2).getBody();
        Map<String, Object> body3 = memberController.resign(vo3).getBody();

        // then
        assertNull(body.get("data"));
        assertEquals(ExceptionConstants.NO_MEMBER_FOUND_MESSAGE, body.get("message"));
        assertNull(body2.get("data"));
        assertEquals(ExceptionConstants.NO_MEMBER_FOUND_MESSAGE, body2.get("message"));
        assertNull(body3.get("data"));
        assertEquals(ExceptionConstants.NO_MEMBER_FOUND_MESSAGE, body3.get("message"));
    }

    @Test
    void findId() {
        // given
        Member member = TestHelper.createMember();
        memberService.join(member);
        MemberFindIdVO vo = new MemberFindIdVO();
        vo.setEmailAddress(member.getEmailAddress());

        // when
        Map<String, Object> body = memberController.findId(vo).getBody();

        // then
        assertEquals(member.getId(), body.get("data"));
        assertEquals(ResponseConstants.FINDID_SUCCESS_MESSAGE, body.get("message"));
    }

    @Test
    void findId_실패() {
        // given
        MemberFindIdVO vo = new MemberFindIdVO();
        vo.setEmailAddress(TestHelper.testEmail);

        // when
        Map<String, Object> body = memberController.findId(vo).getBody();

        // then
        assertNull(body.get("data"));
        assertEquals(ExceptionConstants.NO_MEMBER_FOUND_MESSAGE, body.get("message"));
    }

    @Test
    void findPassword() {
        // given
        Member member = TestHelper.createMember();
        memberService.join(member);
        MemberFindPasswordVO vo = new MemberFindPasswordVO();
        vo.setId(member.getId());

        // when
        Map<String, Object> body = memberController.findPassword(vo).getBody();

        // then
        assertEquals(member.getPassword(), body.get("data"));
        assertEquals(ResponseConstants.FINDPW_SUCCESS_MESSAGE, body.get("message"));
    }

    @Test
    void findPassword_실패() {
        // given
        MemberFindPasswordVO vo = new MemberFindPasswordVO();
        vo.setId(TestHelper.testPw);

        // when
        Map<String, Object> body = memberController.findPassword(vo).getBody();

        // then
        assertNull(body.get("data"));
        assertEquals(ExceptionConstants.NO_MEMBER_FOUND_MESSAGE, body.get("message"));
    }

    @Test
    void changeAccountInfo() {
        // given
        Member member = TestHelper.createMember();
        memberService.join(member);

        MemberDataChangeVO vo = new MemberDataChangeVO();
        vo.setMemberNo(member.getMemberNo());
        vo.setPassword(TestHelper.testPw2);
        vo.setEmailAddress(TestHelper.testEmail2);

        // when
        memberController.changeAccountInfo(vo);

        // then
        Member findMember = memberRepository.findOne(member.getMemberNo());
        assertEquals(member.getMemberNo(), findMember.getMemberNo());
        assertEquals(member.getPassword(), findMember.getPassword());
        assertEquals(member.getEmailAddress(), findMember.getEmailAddress());
    }

    @Test
    void changeAccountInfo_실패() {
        // given
        Member member = TestHelper.createMember();
        memberService.join(member);

        MemberDataChangeVO vo = new MemberDataChangeVO();
        vo.setMemberNo(member.getMemberNo());
        vo.setPassword(TestHelper.failPw);
        vo.setEmailAddress(TestHelper.testEmail2);

        MemberDataChangeVO vo2 = new MemberDataChangeVO();
        vo2.setMemberNo(-1L);
        vo2.setPassword(TestHelper.testPw2);
        vo2.setEmailAddress(TestHelper.testEmail2);

        MemberDataChangeVO vo3 = new MemberDataChangeVO();
        vo3.setMemberNo(member.getMemberNo());
        vo3.setPassword(TestHelper.testPw2);
        vo3.setEmailAddress(TestHelper.testEmail);

        // when
        Map<String, Object> body = memberController.changeAccountInfo(vo).getBody();
        Map<String, Object> body2 = memberController.changeAccountInfo(vo2).getBody();
        Map<String, Object> body3 = memberController.changeAccountInfo(vo3).getBody();

        // then
        assertNull(body.get("data"));
        assertNull(body2.get("data"));
        assertNull(body3.get("data"));

        assertEquals(ExceptionConstants.INVALID_PW_MESSAGE, body.get("message"));
        assertEquals(ExceptionConstants.NO_MEMBER_FOUND_MESSAGE, body2.get("message"));
        assertEquals(ExceptionConstants.DUPLICATE_EMAIL_MESSAGE, body3.get("message"));
    }

    @Test
    void findBlockList() {
    }

    @Test
    void findBlockList_실패() {
    }

    @Test
    void blockMember() {
        // given
        Member member = TestHelper.createMember(1);
        Member member2 = TestHelper.createMember(2);
        memberService.join(member);
        memberService.join(member2);

        // when
        MemberBlockVO vo = new MemberBlockVO();
        vo.setMemberNo(member.getMemberNo());
        vo.setBlockedMemberNo(member2.getMemberNo());
        ResponseEntity<Map<String, Object>> res = memberController.blockMember(vo);

        // then
        Map<String, Object> body = res.getBody();
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNull(body.get("data"));
        assertEquals(ResponseConstants.BLOCK_SUCCESS_MESSAGE, body.get("message"));
    }

    @Test
    void blockMember_실패() {
    }

    @Test
    void unblockMember() {
        // given
        Member member = TestHelper.createMember(1);
        Member member2 = TestHelper.createMember(2);
        memberService.join(member);
        memberService.join(member2);
        blockService.blockMember(member, member2);

        // when
        MemberBlockVO vo = new MemberBlockVO();
        vo.setMemberNo(member.getMemberNo());
        vo.setBlockedMemberNo(member2.getMemberNo());
        ResponseEntity<Map<String, Object>> res = memberController.unblockMember(vo);

        // then
        Map<String, Object> body = res.getBody();
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNull(body.get("data"));
        assertEquals(ResponseConstants.UNBLOCK_SUCCESS_MESSAGE, body.get("message"));
    }

    @Test
    void unblockMember_실패() {

    }

    @Test
    void getMemberPostList() {
        // given
        Member member = TestHelper.createMember();
        Post post = TestHelper.createPost(member);
        Post post2 = TestHelper.createPost(member, 3, 5);

        memberService.join(member);
        postRepository.save(post);
        postRepository.save(post2);

        MemberNoVO vo = new MemberNoVO();
        vo.setMemberNo(member.getMemberNo());

        // when
        Map<String, Object> body = memberController.getMemberPostList(vo).getBody();
        List<PostListVO> postList = (List<PostListVO>) body.get("data");
        String message = (String) body.get("message");

        // then
        assertEquals(2, postList.size());
        assertEquals(postList.get(0).getPostNo(), post.getPostNo());
        assertEquals(postList.get(0).getTitle(), post.getTitle());
        assertEquals(postList.get(1).getPostNo(), post2.getPostNo());
        assertEquals(postList.get(1).getTitle(), post2.getTitle());
        assertEquals(ResponseConstants.SEARCH_SUCCESS_MESSAGE, message);
    }

    @Test
    void getMemberPostList_멤버없을때() {
        // given
        MemberNoVO vo = new MemberNoVO();
        vo.setMemberNo(-1L);

        // when
        Map<String, Object> body = memberController.getMemberPostList(vo).getBody();
        List<PostListVO> postList = (List<PostListVO>) body.get("data");
        String message = (String) body.get("message");

        // then
        assertEquals(0, postList.size());
        assertEquals(ResponseConstants.SEARCH_SUCCESS_MESSAGE, message);
    }
}