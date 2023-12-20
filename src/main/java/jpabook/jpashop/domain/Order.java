package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // cascade 옵션을 걸면 컬렉션을 먼저 저장하고 order를 저장하고 해야한다.
    // 하지만 옵션을 설정하면 그냥 order만 저장해도된다. 전파된다.
    // 컬렉션은 필드에서 초기화하고 손대지말자. null 안전성등
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    // 스프링 부트는 order_date 로 바꾸어준다.
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 [ORDER, CANCEL]

    // == 연관관계 메서드 => 핵심 컨트롤을 하는 곳에 작성하는 것이 좋다. 양방향일때 쓰면 좋다. ==
    public void setMember(Member member)
    {
        this.member = member;
        member.getOrders().add(this);
        /*
         * 연관관계 메서드를 쓰는 이유. 까먹을 수도있고해서 묶는다.
         * Member member = new Member();
         * Order order = new Order();
         * member.getOrders().add(order);
         * order.setMember(member);
         */
    }

    public void addOrderItem(OrderItem orderItem)
    {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery)
    {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems)
    {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for(OrderItem ordetItem : orderItems)
        {
            order.addOrderItem(ordetItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==

    /**
     * 주문 취소
     */
    public void cancel()
    {
        if(delivery.getStatus() == DeliveryStatus.COMP)
        {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }
        this.setStatus(OrderStatus.CANCEL);

        // 수량 되돌리기
        for(OrderItem orderItem : this.orderItems)
        {
            orderItem.cancel();
        }
    }
    //==조회 로직==
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice()
    {
        return  orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }
}
