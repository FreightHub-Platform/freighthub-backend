package com.freighthub.core.repository;

import com.freighthub.core.entity.Driver;
import com.freighthub.core.entity.FleetOwner;
import com.freighthub.core.entity.ReviewBoard;
import com.freighthub.core.entity.Vehicle;
import com.freighthub.core.enums.Availability;
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
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE Vehicle c SET c.verifyStatus = :verifyStatus, c.reviewBoardId = :reviewBoardId, c.verifyTime = :verifyTime WHERE c.id = :id")
    void verifyVehicle(@Param("id") int id, @Param("verifyStatus") VerifyStatus verifyStatus, @Param("reviewBoardId") ReviewBoard reviewBoardId, @Param("verifyTime") LocalDateTime verifyTime);

    @Modifying
    @Transactional
    @Query("UPDATE Vehicle v SET v.driverId = :driverId WHERE v.id = :vehicleId")
    void assignDriver(@Param("driverId") Driver driverId, @Param("vehicleId") int vehicleId);

    @Modifying
    @Transactional
    @Query("UPDATE Vehicle v SET v.driverId = NULL WHERE v.id = :vehicleId")
    void deassignDriver(@Param("vehicleId") int vehicleId);

    @Transactional
    @Query("SELECT v FROM Vehicle v WHERE v.verifyStatus = :verifyStatus")
    List<Vehicle> findVehiclesByVerifyStatus(VerifyStatus verifyStatus);

    @Transactional
    @Query("SELECT v FROM Vehicle v WHERE v.availability = :availability")
    List<Vehicle> findVehiclesByAvailability(Availability availability);

    @Transactional
    @Query("SELECT v FROM Vehicle v WHERE v.fleetOwnerId = :fleetOwner")
    List<Vehicle> findVehiclesByFleetOwner(FleetOwner fleetOwner);

    @Transactional
    @Modifying
    @Query("UPDATE Vehicle v SET v.availability = :availability WHERE v.id = :id")
    void swapAvailability(Integer id, Availability availability);

    @Transactional
    @Query("SELECT v FROM Vehicle v WHERE v.driverId = :driver")
    List<Vehicle> findVehiclesByDriver(Driver driver);

    @Transactional
    @Query("SELECT v FROM Vehicle v WHERE v.driverId = :driver")
    Vehicle findVehicleByDriver(Driver driver);
}