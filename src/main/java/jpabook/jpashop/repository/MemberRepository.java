package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {

    @PersistenceContext // 스프링컨테이너 위에서 동작하기 때문에  @PersistenceContext 가 있으면 EntityManger 를 주입해줌. (따로 EntityMangerFactory 를 통해 Manger 를 만들 필요가 없음.)
    private EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

}
