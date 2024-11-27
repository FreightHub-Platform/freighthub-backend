package com.freighthub.core.service;

import com.freighthub.core.dto.VehicleDto;
import com.freighthub.core.entity.Driver;
import com.freighthub.core.entity.Vehicle;
import com.freighthub.core.enums.Availability;
import com.freighthub.core.enums.VerifyStatus;
import com.freighthub.core.repository.DriverRepository;
import com.freighthub.core.repository.FleetOwnerRepository;
import com.freighthub.core.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class VehicleService {

    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private FleetOwnerRepository fleetOwnerRepository;
    @Autowired
    private VehicleRepository vehicleRepository;

    @Transactional(readOnly = true)
    public List<?> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Vehicle> getVehiclesByVerifyStatus(VerifyStatus verifyStatus) {
        return vehicleRepository.findVehiclesByVerifyStatus(verifyStatus);
    }

    @Transactional(readOnly = true)
    public  Map<String, Object> getVehicleById(int id) {
        Vehicle vehicle = vehicleRepository.findById(id).orElse(null);
        assert vehicle != null;

        Map<String, Object> vehicleDetails = new HashMap<>();
        vehicleDetails.put("vehicle", vehicle);
        vehicleDetails.put("driver", vehicle.getDriverId());

        return vehicleDetails;
    }

    @Transactional(readOnly = true)
    public List<Vehicle> getVehiclesByAvailability(Availability availability) {
        return vehicleRepository.findVehiclesByAvailability(availability);
    }

    @Transactional(readOnly = true)
    public List<Vehicle> getVehiclesByFleetOwner(int id) {
        return vehicleRepository.findVehiclesByFleetOwner(fleetOwnerRepository.findById(id).orElse(null));
    }

    @Transactional
    public void swapAvailability(VehicleDto vehicle) {
        vehicleRepository.swapAvailability(vehicle.getId(), vehicle.getAvailability());
    }

    @Transactional
    public List<Vehicle> getVehiclesByDriver(int id) {
        return vehicleRepository.findVehiclesByDriver(driverRepository.findById((long) id).orElse(null));
    }
}
