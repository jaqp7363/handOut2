package com.pretask.share.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Member {

	@Id @GeneratedValue
	private Long member_id;
	
	private String name;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "tbl_member_room",
               joinColumns = @JoinColumn(name = "member_id"),
               inverseJoinColumns = @JoinColumn(name = "room_id"))
	private List<Room> rooms = new ArrayList<>();
	
	@OneToOne(mappedBy = "handOutMember")
	private HandOut handOut;
	
	@OneToOne(mappedBy = "receiveMember")
	private HandOutDetail detail;
	
	private long price;
	
	public Member(String name) {
		this.name = name;
		this.price = 0L;
	}
	
	public void joinRoom(Room room) {
		rooms.add(room);
		room.joinMember(this);
	}
	
	public void addPrice(long receivePrice) {
		this.price += receivePrice;
	}
}
