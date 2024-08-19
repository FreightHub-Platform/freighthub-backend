package com.freighthub.core.repository;

import com.freighthub.core.entity.Driver;
import com.freighthub.core.entity.ReviewBoard;
import com.freighthub.core.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE Vehicle c SET c.verifyStatus = true, c.reviewBoardId = :reviewBoardId, c.verifyTime = :verifyTime WHERE c.id = :id")
    void verifyVehicle(@Param("id") int id, @Param("reviewBoardId") ReviewBoard reviewBoardId, @Param("verifyTime") LocalDateTime verifyTime);

    @Modifying
    @Transactional
    @Query("UPDATE Vehicle v SET v.driverId = :driverId WHERE v.id = :vehicleId")
    void assignDriver(@Param("driverId") Driver driverId, @Param("vehicleId") int vehicleId);

    @Modifying
    @Transactional
    @Query("UPDATE Vehicle v SET v.driverId = NULL WHERE v.id = :vehicleId")
    void deassignDriver(@Param("vehicleId") int vehicleId);


}