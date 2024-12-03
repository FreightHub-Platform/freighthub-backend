package com.freighthub.core.service;

import com.freighthub.core.dto.DriverDto;
import com.freighthub.core.entity.Driver;
import com.freighthub.core.entity.Vehicle;
import com.freighthub.core.enums.Availability;
import com.freighthub.core.enums.VehicleOwnership;
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

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private FleetOwnerRepository fleetOwnerRepository;
    @Autowired
    private VehicleRepository vehicleRepository;

    @Transactional(readOnly = true)
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Driver> getDriversByVerifyStatus(VerifyStatus verifyStatus) {
        return driverRepository.findDriversByVerifyStatus(verifyStatus);
    }
    
    @Transactional(readOnly = true)
    public  Map<String, Object> getDriverById(int id) {
        Driver driver = driverRepository.findById((long) id).orElse(null);
        Vehicle vehicle = vehicleRepository.findVehicleByDriver(driver);

        Map<String, Object> driverDetails = new HashMap<>();
        if (vehicle != null) {
            driverDetails.put("vehicle", vehicle);
        }
        driverDetails.put("driver", driver);
        return driverDetails;
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

    public void deleteDriver(int id) {
        driverRepository.deleteDricer(id);
    }
}
