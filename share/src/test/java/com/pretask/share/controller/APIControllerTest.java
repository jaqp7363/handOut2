package com.pretask.share.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pretask.share.entity.Member;
import com.pretask.share.entity.Room;

@SpringBootTest
@Transactional
class APIControllerTest {

	@Autowired
	EntityManager em;
	
	@Autowired
	private APIController apiController;
	
	private MockMvc mockMvc;
	
	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
	
	private long member1_id;
	private long member2_id;
	
	public static String asJsonString(final Object obj) {
	       try {
	           final ObjectMapper mapper = new ObjectMapper();
	           final String jsonContent = mapper.writeValueAsString(obj);
	           System.out.println(jsonContent);
	           return jsonContent;
	       } catch (Exception e) {
	           throw new RuntimeException(e);
	       }
	}
	
	@BeforeEach
	void test() {
		mockMvc = MockMvcBuilders.standaloneSetup(apiController).build();

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
		
		member1_id = member1.getMember_id();
		member2_id = member2.getMember_id();
	}
	
	@Test
	void API_Header정보_누락() throws Exception {
		mockMvc.perform(post("/api/v0/handout").header("X-ROOM-ID", "1"))
		.andExpect(status().isBadRequest());
		
		mockMvc.perform(post("/api/v0/handout").header("X-USER-ID", "1"))
		.andExpect(status().isBadRequest());
	}
}
