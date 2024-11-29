package com.freighthub.core.service;

import com.cloudinary.Cloudinary;
import com.freighthub.core.dto.VehicleDto;
import com.freighthub.core.dto.VerifyDto;
import com.freighthub.core.entity.ReviewBoard;
import com.freighthub.core.entity.Vehicle;
import com.freighthub.core.enums.VerifyStatus;
import com.freighthub.core.repository.DriverRepository;
import com.freighthub.core.repository.FleetOwnerRepository;
import com.freighthub.core.repository.ReviewBoardRepository;
import com.freighthub.core.repository.VehicleRepository;
import com.freighthub.core.util.UploadToCloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RegVehicleService {

    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private FleetOwnerRepository fleetOwnerRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private ReviewBoardRepository reviewBoardRepository;
    @Autowired
    private Cloudinary cloudinary;

    @Transactional
    public Integer updateRegistrationDetails(VehicleDto vehicleDto) {

        UploadToCloudinary uploadToCloudinary = new UploadToCloudinary();

        Vehicle vehicle = new Vehicle();
        vehicle.setLicenseNo(vehicleDto.getLicenseNo());
        vehicle.setMake(vehicleDto.getMake());
        vehicle.setModel(vehicleDto.getModel());
        vehicle.setYear(vehicleDto.getYear());
        vehicle.setColor(vehicleDto.getColor());
//        vehicle.setRefrigFlag(vehicleDto.getRefrigFlag());
//        vehicle.setCraneFlag(vehicleDto.getCraneFlag());
        vehicle.setFrontPic(uploadToCloudinary.uploadImage(cloudinary, vehicleDto.getFrontPic()));
        vehicle.setRearPic(uploadToCloudinary.uploadImage(cloudinary, vehicleDto.getRearPic()));
        vehicle.setSide1Pic(uploadToCloudinary.uploadImage(cloudinary, vehicleDto.getSide1Pic()));
        vehicle.setSide2Pic(uploadToCloudinary.uploadImage(cloudinary, vehicleDto.getSide2Pic()));
        vehicle.setTrailerImage(uploadToCloudinary.uploadImage(cloudinary, vehicleDto.getTrailerImage()));
        vehicle.setContainerType(vehicleDto.getContainerType());

        // Update the FleetOwner only if the FleetOwnerId is provided
        if (vehicleDto.getFleetOwnerId() != null) {
            vehicle.setFleetOwnerId(fleetOwnerRepository.findById(vehicleDto.getFleetOwnerId())
                    .orElseThrow(() -> new RuntimeException("FleetOwner not found")));
        }

        vehicle.setCompletion(1);
        // Save the vehicle and retrieve the generated ID
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return savedVehicle.getId(); // Assuming 'id' is the primary key field
    }

    @Transactional
    public void updateDocumentDetails(VehicleDto vehicleDto) {

        UploadToCloudinary uploadToCloudinary = new UploadToCloudinary();

        Vehicle vehicle = vehicleRepository.findById(vehicleDto.getId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        vehicle.setRevenueLicensePic(uploadToCloudinary.uploadImage(cloudinary, vehicleDto.getRevenueLicensePic()));
        vehicle.setLicenseExpiry(vehicleDto.getLicenseExpiry());
        vehicle.setInsurancePic(uploadToCloudinary.uploadImage(cloudinary, vehicleDto.getInsurancePic()));
        vehicle.setInsuranceExpiry(vehicleDto.getInsuranceExpiry());
        vehicle.setRegistrationDoc(uploadToCloudinary.uploadImage(cloudinary, vehicleDto.getRegistrationDoc()));
        vehicle.setCompletion(2);
        vehicleRepository.save(vehicle);
    }

    @Transactional
    public void verifyDVehicle(VerifyDto vehicleDto) {
        ReviewBoard user = reviewBoardRepository.findById(vehicleDto.getReviewId()).orElseThrow(() -> new RuntimeException("User not found"));
        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleDto.getId());
        vehicle.setReviewBoardId(user);
        vehicleRepository.verifyVehicle(vehicle.getId(), VerifyStatus.verified, vehicle.getReviewBoardId(), LocalDateTime.now());
    }

    @Transactional
    public void rejectVehicle(VerifyDto vehicleDto) {
        ReviewBoard user = reviewBoardRepository.findById(vehicleDto.getReviewId()).orElseThrow(() -> new RuntimeException("User not found"));
        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleDto.getId());
        vehicle.setReviewBoardId(user);
        vehicleRepository.verifyVehicle(vehicle.getId(), VerifyStatus.rejected, vehicle.getReviewBoardId(), LocalDateTime.now());
    }
}
