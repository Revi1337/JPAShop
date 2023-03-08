package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {
        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        // 회원 이름  검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }

    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> member = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        // 주문상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }
        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(member.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        ).getResultList();
    }

    // Collection v3
    public List<Order> findAllWithItem() {
        // JPA 에서 distinct 는 두가지 기능을 함.
        // 1. from 뒤의 첫번째 Entity (여기서는 Order) 가 중복인 경우, Entity 의 중복(pk 로 판단)을 제거해서 컬렉션을 담아줌.
        // 2. DB 에 distinct 를 날림 --> 하지만 DB 에는 조인한 모든 컬럼이 한줄로 출력되기때문에 distinct 되는경우는 거의 없음.
        // TODO 하지만, hibernate 6.0 부터는 알아서 distinct 를 넣지 않아도 from 절 뒤 첫번째로 오는 Entity 의 중복을 제거해줌. (알고만 있을것)
        // TODO 개중요한것. 컬렉션(1:N) 에서 fetch join 이든 그냥 join 조인이든 join 쿼리가 나가면 페이징 처리가 불가함. 로그를 보면 (firstResult/maxResults specified with collection fetch; applying in memory) 가 뜸
        // 이 뜻은 페이징처리인 firstResult/maxResults 가 설정되었지만, collection fetch 랑 같이 조인되었기 때문에 메모리상에서 페이징처리를 할것이라는 경고가 나오는것임. --> 데이터가 만약 10000개 면 이걸 메모리에서 페이징 --> out of memory
        // fetch 조인을 포함해서 1:N 에서 조인쿼리가 나가버리는순간 DB 의 row 가 뻥튀기 되면서, 순서의 기준을 잃어버리는 것임.
        // TODO 컬렉션(1:N) fetch join 은 1개만 사용 가능함. 컬렉션 둘 이상에 fetch join 을 사용하면 안됨. --> 데이터가 부정함하게 조회될 수 있음
        return em.createQuery(
                "select distinct o from Order o" + // hibernate 6.0 이상부터는 distinct 쓰지 않아도 됨.
                        " inner join fetch o.member m" +
                        " inner join fetch o.delivery d" +
                        " inner join fetch o.orderItems oi" +
                        " inner join fetch oi.item i", Order.class
        ).setFirstResult(0).setMaxResults(100).getResultList();
    }

    // Collection v3.1
    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                        "select o from Order o" +
                                " join fetch o.member m" +
                                " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
