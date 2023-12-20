package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    // 롤백이지만 쿼리를 보고싶다면
    @Autowired EntityManager em;

    @Test
//    @Rollback(false) 테스트는 기본적으로 롤백이므로 insert가 안보인다. 보려면 false한다. 이친구는 롤백이안된다.
    public void 회원가입 () throws Exception
    {
        //given 이런게 주어졌을때
        Member member = new Member();
        member.setName("kim");

        //when 이렇게 하면
        Long savedId = memberService.join(member);

        //then 이렇게 된다.
        em.flush(); // 롤백이지만 쿼리를 보고싶다면 영속성 컨텍스트에서 쿼리를 내보낸다.
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test(expected = IllegalStateException.class) // try catch 안해도됨 이 예외면된다.
    public void 중복_회원_예외() throws Exception
    {
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
        memberService.join(member2); // 예외 발생

        //then
        fail("예외가 발생해야 한다.");
    }
}