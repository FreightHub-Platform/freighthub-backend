package com.freighthub.core.repository;

import com.freighthub.core.entity.Consigner;
import com.freighthub.core.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long> {
}
