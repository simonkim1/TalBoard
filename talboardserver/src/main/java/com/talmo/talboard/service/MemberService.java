package com.talmo.talboard.service;

import com.talmo.talboard.domain.Block;
import com.talmo.talboard.domain.Member;
import com.talmo.talboard.domain.vo.MemberDataChangeVO;
import com.talmo.talboard.exception.NoAuthorizationException;
import com.talmo.talboard.exception.NoMemberFoundException;
import com.talmo.talboard.repository.BlockRepository;
import com.talmo.talboard.repository.MemberRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final BlockService blockService;
    private final MemberRepository memberRepository;
    private final BlockRepository blockRepository;

    /**
     * 회원 가입
     */
    @Transactional
    public Long join(Member member) {
        chkDuplicateMember(member);
        memberRepository.save(member);
        return member.getMember_no();
    }

    /**
     * 아이디가 같거나 이메일이 같은 회원이 있는지 체크
     */
    private void chkDuplicateMember(Member member) {
        if(!memberRepository.findActualMemberById(member.getId()).isEmpty()
            || !memberRepository.findActualMemberByEmailAddress(member.getEmailAddress()).isEmpty()) {
            throw new IllegalStateException("이미 존재하는 아이디 또는 이메일");
        }
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void resign(String id, String resign_member_id) throws NoMemberFoundException {
        Member member = memberRepository.findOneActualMemberById(id);
        Member resignMember = memberRepository.findOneActualMemberById(resign_member_id);

        if(!member.getMember_no().equals(resignMember.getMember_no())
        && !member.isAdminYn()) {
            throw new NoAuthorizationException("회원 탈퇴");
        }

        blockService.cleanMember(resignMember.getMember_no());
        resignMember.resign();
    }

    /**
     * 이메일 주소로 아이디 찾기
     */
    @Transactional
    public String findId(String emailAddress) throws NoMemberFoundException {
        return memberRepository.findOneActualMemberByEmailAddress(emailAddress).getId();
    }

    /**
     * 아이디로 비밀번호 찾기
     */
    @Transactional
    public String findPassword(String id) throws NoMemberFoundException {
        // TODO: 해당 멤버의 이메일 주소로 비밀번호 정보 보내는 기능
        return memberRepository.findOneActualMemberById(id).getPassword();
    }

    /**
     * 회원 정보 변경
     */
    @Transactional
    public void updateMemberData(MemberDataChangeVO vo) throws IllegalArgumentException {
        Member member = memberRepository.findOneActualMemberById(vo.getId());

        if(vo.getPassword() != null) {
            member.changePassword(vo.getPassword());
        }
        if(vo.getEmailAddress() != null) {
            if(!memberRepository.findActualMemberByEmailAddress(vo.getEmailAddress()).isEmpty()) {
                throw new IllegalStateException("이미 존재하는 이메일");
            }
            member.changeEmailAddress(vo.getEmailAddress());
        }
    }

    /**
     * 회원 차단 목록 조회
     */
    public List<Member> findMemberBlockList(String id) throws NoMemberFoundException {
        Member member = memberRepository.findOneActualMemberById(id);
        return blockRepository.findMemberBlockList(member.getMember_no()).stream()
                .map(Block::getBlockedMember)
                .collect(Collectors.toList());
    }
}
