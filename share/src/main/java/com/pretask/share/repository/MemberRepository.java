package com.pretask.share.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pretask.share.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{

}
