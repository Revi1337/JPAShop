package jpabook.jpashop.config;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component @RequiredArgsConstructor @Transactional
public class ModernInitializeDatabase implements CommandLineRunner {

    private final EntityManager entityManager;

    @Override
    public void run(String... args) throws Exception {
        init1();
        init2();
    }

    public void init1() {
        Member member = createMember("userA", "서울", "1", "11111");
        entityManager.persist(member);

        Book book1 = createBook("JPA1 BOOK", 10000, 100);
        entityManager.persist(book1);

        Book book2 = createBook("JPA2 BOOK", 20000, 100);
        entityManager.persist(book2);

        OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
        OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

        Delivery delivery = createDelivery(member);
        Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
        entityManager.persist(order);
    }

    public void init2() {
        Member member = createMember("userB", "진주", "2", "2222");
        entityManager.persist(member);

        Book book1 = createBook("SPRING1 BOOK", 20000, 200);
        entityManager.persist(book1);

        Book book2 = createBook("SPRING2 BOOK", 40000, 300);
        entityManager.persist(book2);

        OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 3);
        OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);

        Delivery delivery = createDelivery(member);
        Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
        entityManager.persist(order);
    }

    private Member createMember(String name, String city, String street, String zipcode) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address(city, street, zipcode));
        return member;
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book1 = new Book();
        book1.setName(name);
        book1.setPrice(price);
        book1.setStockQuantity(stockQuantity);
        return book1;
    }

    private Delivery createDelivery(Member member) {
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        return delivery;
    }

}
