package com.freighthub.core.repository;

import com.freighthub.core.entity.FleetOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FleetOwnerRepository extends JpaRepository<FleetOwner, Integer> {

    @Query("SELECT f.completion FROM FleetOwner f WHERE f.id = :uid")
    Integer findCompletionByUid(@Param("uid") Long uid);
}