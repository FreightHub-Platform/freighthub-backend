package com.freighthub.core.repository;

import com.freighthub.core.entity.Driver;
import com.freighthub.core.entity.FleetOwner;
import com.freighthub.core.entity.ReviewBoard;
import com.freighthub.core.enums.Availability;
import com.freighthub.core.enums.VehicleOwnership;
import com.freighthub.core.enums.VerifyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query("SELECT d.completion FROM Driver d WHERE d.id = :uid")
    @Transactional
    Integer findCompletionByUid(@Param("uid") Long uid);

    @Query("SELECT d.verifyStatus FROM Driver d WHERE d.id = :uid")
    @Transactional
    VerifyStatus findVerifyStatusByUid(@Param("uid") Long uid);

    @Modifying
    @Transactional
    @Query("UPDATE Driver c SET c.verifyStatus = :verifyStatus, c.reviewBoardId = :reviewBoardId, c.verifyTime = :verifyTime WHERE c.id = :id")
    void verifyDriver(@Param("id") int id, @Param("verifyStatus") VerifyStatus verifyStatus, @Param("reviewBoardId") ReviewBoard reviewBoardId, @Param("verifyTime") LocalDateTime verifyTime);

    @Transactional
    @Query("SELECT c FROM Driver c WHERE c.verifyStatus = :verifyStatus")
    List<Driver> findDriversByVerifyStatus(VerifyStatus verifyStatus);

    @Transactional
    @Query("SELECT c FROM Driver c WHERE c.fleetOwnerId = :fleetOwner")
    List<Driver> findDriversByFleetOwner(FleetOwner fleetOwner);

    @Modifying
    @Transactional
    @Query("UPDATE Driver d SET d.availability = :availability WHERE d.id = :id")
    void swapAvailability(Integer id, Availability availability);

    @Transactional
    @Query("SELECT d FROM Driver d WHERE d.availability = :availability")
    List<Driver> findDriversByAvailability(Availability availability);

    @Transactional
    @Query("SELECT d FROM Driver d WHERE d.ownership = :ownership")
    List<Driver> findDriversByOwnership(VehicleOwnership ownership);
}
