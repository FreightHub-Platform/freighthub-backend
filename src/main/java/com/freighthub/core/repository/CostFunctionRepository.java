package com.freighthub.core.repository;

import com.freighthub.core.entity.CostFunction;
import com.freighthub.core.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CostFunctionRepository extends JpaRepository<CostFunction, Integer> {
}
