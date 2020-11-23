package com.pretask.share.entity;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class HandOutDetail {

	@Id @GeneratedValue
	@Column(name = "hand_out_detail_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "hand_out_id")
	private HandOut handOut;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "member_id")
	private Member receiveMember;
	
	private long receivePrice;
	
	private LocalDateTime regDate;
	private LocalDateTime udtDate;
	
	public HandOutDetail(HandOut handOut, long receivePrice) {
		this.handOut = handOut;
		this.receivePrice = receivePrice;
	}
	
	@PrePersist
	public void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		regDate = now;
		udtDate = now;
	}
	
	@PreUpdate
	public void preUpdate() {
		udtDate = LocalDateTime.now();
	}
}
