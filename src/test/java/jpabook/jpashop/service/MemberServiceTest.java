package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // 1. @Transactional 어노테이션이 테스트케이스에 있으면, commit 이 아니라, rollback 하기때문에, 영속성 컨텍스트의 SQL 쓰기지연 저장소에 있던 쿼리가 DB 에 반영되지 않음.
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @PersistenceContext EntityManager entityManager;  // 3. 혹은 EntityManger 를 주입한 후

    // @Rollback(value = false) // 2. 정상적으로 나가는 것을 확인하고싶을때 명시. (Rollback 하지 않고 강제로 commit 을 날리는 방법.) --> 결과적으로 DB 에 결과가 반영됨.
    @Test
    @DisplayName(value = "회원가입 테스트")
    public void joinTest() throws Exception {
        Member member = new Member();
        member.setName("kim");
        
        Long savedId = memberService.join(member);

        // entityManager.flush();  // 4. 강제로 flush() 를 실행시켜 DB 에 쿼리를 날리면 됨. (강제로 flush() 로 DB 에 쿼리를 보내보고 @Transactional 이 끝날때 Rollback 함.) --> 결과적으로 DB 에 반영이 되지 않음.
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test
    @DisplayName(value = "중복예외가 발생하면 테스트 성공")
    public void duplicateMemberExceptionTest() throws Exception {
        Member member1 = new Member();
        member1.setName("kim");
        Member member2 = new Member();
        member2.setName("kim");

        memberService.join(member1);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> memberService.join(member2));// 정상적으로 예외가 발생하여  assertThrows 가 catch 하면 테스트 성공
        assertEquals("이미 존재하는 회원입니다.", exception.getMessage());
        // fail("예외가 발생해야 한다.");   // 여기오면 테스트 실패
    }

}