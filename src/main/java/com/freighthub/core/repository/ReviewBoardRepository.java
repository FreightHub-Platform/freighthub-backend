package com.freighthub.core.repository;

import com.freighthub.core.entity.ReviewBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewBoardRepository extends JpaRepository<ReviewBoard, Integer> {
}