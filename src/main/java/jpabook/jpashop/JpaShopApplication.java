package jpabook.jpashop;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.service.MemberService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpaShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpaShopApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(MemberService memberService) {
        return args -> {
            Member member3 = new Member();
            member3.setName("test1");
            member3.setAddress(new Address("twest", "asdf", "2134"));
            memberService.join(member3);

            Member member4 = new Member();
            member4.setName("test2");
            member4.setAddress(new Address("wqwer213", "reqr", "asdf"));
            memberService.join(member4);

            Member member = new Member();
            member.setName("member1");
            member.setAddress(new Address("서울", "test", "11111"));
            memberService.join(member);

            Member member2 = new Member();
            member2.setName("member2");
            member2.setAddress(new Address("부산", "qwer", "12322"));
            memberService.join(member2);
        };
    }

}
