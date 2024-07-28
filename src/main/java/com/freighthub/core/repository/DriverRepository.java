package com.freighthub.core.repository;

import com.freighthub.core.entity.Consigner;
import com.freighthub.core.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query("SELECT d.completion FROM Driver d WHERE d.id = :uid")
    Integer findCompletionByUid(@Param("uid") Long uid);
}
