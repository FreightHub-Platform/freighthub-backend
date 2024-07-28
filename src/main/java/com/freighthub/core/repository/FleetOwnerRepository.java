package com.freighthub.core.repository;

import com.freighthub.core.entity.FleetOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FleetOwnerRepository extends JpaRepository<FleetOwner, Integer> {
}