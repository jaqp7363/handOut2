package com.pretask.share;

import static com.pretask.share.entity.QMember.member;
import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.pretask.share.entity.Member;
import com.pretask.share.entity.Room;
import com.querydsl.jpa.impl.JPAQueryFactory;

@SpringBootTest
@Transactional
class ShareApplicationTests {

	@Autowired
	EntityManager em;
	
	JPAQueryFactory queryFactory;
	
	@BeforeEach
	void test() {
		queryFactory = new JPAQueryFactory(em);
		Member member1 = new Member("사용자1");
		Member member2 = new Member("사용자2");
		Member member3 = new Member("사용자3");
		Member member4 = new Member("사용자4");
		
		Room room1 = new Room("room_1");
		Room room2 = new Room("room_2");
		member1.joinRoom(room1);
		member2.joinRoom(room1);
		member3.joinRoom(room1);
		member4.joinRoom(room1);
		
		member1.joinRoom(room2);
		member2.joinRoom(room2);
		
		em.persist(member1);
		em.persist(member2);
		em.persist(member3);
		em.persist(member4);
		em.persist(room1);
		em.persist(room2);
	}
	
	@Test
	void contextLoads() {
		Member findMember = queryFactory
				.select(member)
				.from(member)
				.where(member.name.eq("사용자1"))
				.fetchOne();
		
		assertThat(findMember.getName()).isEqualTo("사용자1");
	}

}

