package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository @RequiredArgsConstructor
public class MemberRepository {

//    @PersistenceUnit    // @PersistenceUnit 가 있으면 스프링이 만든 EntityMangerFactory 빈을 주입해줌, (없어도 됨.)
//    private EntityManagerFactory emf;

//    @PersistenceContext // 1. @PersistenceContext 가 있으면 스프링이 만든 EntityManger 빈을 주입해줌. (따로 EntityMangerFactory 를 통해 Manger 를 만들 필요가 없음.)
//    private EntityManager em;

    private final EntityManager em; // 2. @PersistenceContext 말고 생성자 주입으로도 가능.

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member as m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member as m where m.name=:name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }

}
