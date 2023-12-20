package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item)
    {
        if(item.getId() == null)
        {   // jpa는 저장 전 즉 커밋 전까지는 id의 값이 없다.
            // 완전히 새로 생성하는 객체
            em.persist(item);
        }
        else
        {   // 이미 디비에 등록된 것을 가져와서 사용, 업데이트는 아니지만 이와 같다 생각하자.
            // 준영속성 엔티티의 변경을 머지를 사용하여 변경.
            // 머지는 준영속성 엔티티를 영속성 엔티티로 바꿔서 작업한다.
            // service 에서 변경감지를 이용한 방법을 내부적으로 알아서 해주는 것이다.
            // 물론 차이점이 존재한다.
            // 변경 감지 : 원하는 속성만 선택해서 변경할 수 있다.
            // 병합 : 모든 속성이 변경된다. 값이 없으면 null로 업데이트 할 위험도 있다.(병합은 모든 필드 교체)
            em.merge(item);
        }
    }

    public Item findOne(Long id)
    {
        return em.find(Item.class, id);
    }

    public List<Item> findAll()
    {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
