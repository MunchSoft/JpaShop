package jpabook.jpashop.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Embeddable
@Getter
@AllArgsConstructor // 값 타입은 불변값이므로 생성할때 한번만 입력받는게 좋다.
public class Address {

    private String city;
    private String street;
    private String zipcode;

    // JPA 스펙상 제공. => 기본생성자가 필요
    protected Address(){}
}
