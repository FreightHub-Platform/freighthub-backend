package com.freighthub.core.service;

import com.freighthub.core.dto.DriverDto;
import com.freighthub.core.entity.Driver;
import com.freighthub.core.enums.Availability;
import com.freighthub.core.enums.VehicleOwnership;
import com.freighthub.core.enums.VerifyStatus;
import com.freighthub.core.repository.DriverRepository;
import com.freighthub.core.repository.FleetOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private FleetOwnerRepository fleetOwnerRepository;

    @Transactional(readOnly = true)
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Driver> getDriversByVerifyStatus(VerifyStatus verifyStatus) {
        return driverRepository.findDriversByVerifyStatus(verifyStatus);
    }
    
    @Transactional(readOnly = true)
    public Driver getDriverById(int id) {
        return driverRepository.findById((long) id).orElse(null);
    }
    
    @Transactional(readOnly = true)
    public List<Driver> getDriversByFleetOwner(int id) {
        return driverRepository.findDriversByFleetOwner(fleetOwnerRepository.findById(id).orElse(null));
    }

    @Transactional
    public void swapAvailability(DriverDto driver) {
        driverRepository.swapAvailability(driver.getId(), driver.getAvailability());
    }

    @Transactional(readOnly = true)
    public List<Driver> getDriversByAvailability(Availability availability) {
        return driverRepository.findDriversByAvailability(availability);
    }

    @Transactional(readOnly = true)
    public List<Driver> getDriversByOwnership(VehicleOwnership ownership) {
        return driverRepository.findDriversByOwnership(ownership);
    }
}
