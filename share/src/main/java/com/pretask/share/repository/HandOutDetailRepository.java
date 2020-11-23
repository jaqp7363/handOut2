package com.pretask.share.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pretask.share.entity.HandOut;
import com.pretask.share.entity.HandOutDetail;

public interface HandOutDetailRepository extends JpaRepository<HandOutDetail, Long>{

}
