package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item)
    {
        itemRepository.save(item);
    }

    public List<Item> findItems()
    {
        return itemRepository.findAll();
    }

    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity)
    {
        // 준영속성 엔티티를 변경감지를 이용해서 수정하는 방법 => 머지쓰지말고 이방법을 쓰자(변경 감지)
        // 트랜잭션 안에서 엔티티를 다시 조회, 영속성이 관리중인 엔티티를 가져온다.
        Item findItem = itemRepository.findOne(itemId);
        // 변경할 값들을 변경한다.
        // 셋터를 이용하지말고 엔티티안에서 메서드 만들고 이용해서 어디서 값이 바뀌는지 알기쉽게(추적하기 쉽게)하자 셋터는 쓰지말자.

        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);
        // 트랜잭션이 끝나면 커밋을 날리고 이때 수정된 것들도 다같이 커밋된다.(변경 감지)
    }

    public Item findOne(Long itemId)
    {
        return itemRepository.findOne(itemId);
    }
}
