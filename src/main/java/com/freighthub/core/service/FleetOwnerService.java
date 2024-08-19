package com.freighthub.core.service;

import com.freighthub.core.dto.AssignDto;
import com.freighthub.core.entity.Driver;
import com.freighthub.core.entity.FleetOwner;
import com.freighthub.core.repository.DriverRepository;
import com.freighthub.core.repository.FleetOwnerRepository;
import com.freighthub.core.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FleetOwnerService {

    @Autowired
    FleetOwnerRepository fleetOwnerRepository;
    @Autowired
    VehicleRepository vehicleRepository;
    @Autowired
    DriverRepository driverRepository;

    @Transactional(readOnly = true)
    public List<?> getAllFleetOwners() {
        return fleetOwnerRepository.findAll();
    }


    public List<FleetOwner> getAllUnverifiedFleetOwners() {
        return fleetOwnerRepository.findUnverifiedFleetOwners();
    }

    public FleetOwner getFleetOwnerById(int id) {
        return fleetOwnerRepository.findById(id).orElse(null);
    }

    public void assignDriver(AssignDto assign) {
        Driver driverId = driverRepository.findById((long) assign.getDriverId()).orElseThrow(() -> new RuntimeException("User not found"));
        vehicleRepository.assignDriver(driverId, assign.getVehicleId());
    }

    public void deAssignDriver(AssignDto assign) {
        vehicleRepository.deassignDriver(assign.getVehicleId());
    }
}
