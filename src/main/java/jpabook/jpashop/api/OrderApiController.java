package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


/**
 * API 개발 고급 - 컬렉션 조회 최적화
 * 컬렉션인 일대다 관계 (OneToMany)를 조회하고, 최적화하는 방법
 */
@RestController @RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    /**
     * 주문 조회 V1: 엔티티 직접 노출 방법 --> 쓰면안되는 방법.
     * - 엔티티가 변하면 API 스펙이 변한다.
     * - 트랜잭션 안에서 지연 로딩 필요
     * - 양방향 연관관계 문제 (@JsonIgnore 를 다 걸어주어야함. --> 굉장히 쓰레기같은 방법)
     */
    @GetMapping(value = "/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) { // Order 프록시 객체를 강제 초기화하는 과정
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName()); // OrderItem 프록시 객체를 강제 초기화하는 과정
        }
        return all;
    }

    /**
     * 주문 조회 V2: 엔티티를 DTO 로 변환 --> DTO 변환한건 많이 아쉬운 방법 (연관관계 Entity 가 외부로 노출됨 ㅇㅇ) --> Entity 에 대한 의존을 완전히 끊어버려야 함.
     * Entity 를  외부로 노출하지 말라는 것은 단순히 껍데기만 DTO 로 노출하라는 것이 아니라, 속에 있는 Entity 까지 노출하지말라는것임.
     * 속에 있는 Entity 가 무슨의미냐면, DTO 로 감깐 Entity 안에 존재하는 연관관계를 맺고있는 다른 Entity 를 말하는 것임.
     * V2 의 예에서는 Order 엔티티안의 OrderItem 엔티티도 DTO 로 변환시켜주어야 한다는 것임.
     * 귀찮지만, 이렇게 연관된 엔티티까지 모두 DTO 로 바꿧을떄 얻는 이점은, client 의 요구사항에 따라 원하는 정보만 json 으로 다르게 보내줄수 있다는 것이며,
     * Entity 의 스펙이 바뀌어도 다른 계층에 영향을 많이 주지 않음.
     *
     * 단점으로는 일반 지연로딩도 쿼리가 많이나가는데, ~ToMany 시리즈는 Collection 이라서.. 쿼리가 정말 많이 실행됨...
     */
    @GetMapping(value = "/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
        return collect;
    }

    @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;
        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(OrderItemDto::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    static class OrderItemDto {
        private String itemName; // 상품명
        private int orderPrice; // 주문 가격
        private int count; // 주무 수량
        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }

}
