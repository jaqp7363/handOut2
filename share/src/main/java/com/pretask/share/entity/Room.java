package com.pretask.share.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(of = {})
public class Room {

	@Id
	@Column(length = 7)
	private String room_id;
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "rooms")
	private List<Member> members = new ArrayList<>();
	
	@OneToMany(mappedBy = "room")
	private List<HandOut> handOuts = new ArrayList<>();
	
	public Room(String room_id) {
		this.room_id = room_id;
	}
	
	public void joinMember(Member member) {
		members.add(member);
	}
}
