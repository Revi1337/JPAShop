package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MemberRepositoryTest {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberRepositoryTest(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Test
    @DisplayName(value = "testMember")
    @Transactional  // EntityManger 를 통한 모든 데이터 변경은 항상 트랜잭션안에서 이루어져야하는건 당연한 얘기 (@Transactional 이 테스트케이스에 있으면 끝나고 Rollback 을함.)
    @Rollback(value = false) // 만약 테스트가 끝나고 @Transactional 이 Rollback 말고 Commit 을 날리고싶다면 @Rollback(value = false) 적용
    public void testMember() throws Exception {
        // given
        Member member = new Member();
        member.setUsername("memberA");

        //when
        Long savedId = memberRepository.save(member);
        Member findMember = memberRepository.find(savedId);

        //then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

}