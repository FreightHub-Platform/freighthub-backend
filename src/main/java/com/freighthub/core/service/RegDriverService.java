package com.freighthub.core.service;

import com.cloudinary.Cloudinary;
import com.freighthub.core.dto.DriverDto;
import com.freighthub.core.dto.VerifyDto;
import com.freighthub.core.entity.Driver;
import com.freighthub.core.entity.ReviewBoard;
import com.freighthub.core.enums.VerifyStatus;
import com.freighthub.core.repository.DriverRepository;
import com.freighthub.core.repository.FleetOwnerRepository;
import com.freighthub.core.repository.ReviewBoardRepository;
import com.freighthub.core.repository.VehicleTypeRepository;
import com.freighthub.core.util.UploadToCloudinary;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RegDriverService {

    private static final Logger logger = LoggerFactory.getLogger(RegDriverService.class);

    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private FleetOwnerRepository fleetOwnerRepository;
    @Autowired
    private ReviewBoardRepository reviewBoardRepository;
    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;

    @Transactional
    public void updatePersonalDetails(DriverDto driverDto) {

        Driver driver = driverRepository.findById(Long.valueOf(driverDto.getId()))
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        // Update the fields
        driver.setContactNumber(driverDto.getContactNumber());
        driver.setNic(driverDto.getNic());
        driver.setAddressLine1(driverDto.getAddressLine1());
        driver.setAddressLine2(driverDto.getAddressLine2());
        driver.setCity(driverDto.getCity());
        driver.setProvince(driverDto.getProvince());
        driver.setPostalCode(driverDto.getPostalCode());
        driver.setOwnership(driverDto.getOwnership());
        driver.setCompletion(1);

        // Update the FleetOwner only if the FleetOwnerId is provided
        if (driverDto.getFleetOwnerId() != null) {
            driver.setFleetOwnerId(fleetOwnerRepository.findById(driverDto.getFleetOwnerId())
                    .orElseThrow(() -> new RuntimeException("FleetOwner not found")));
        }

        driverRepository.save(driver);  // This will update the existing record

    }

    @Transactional
    public void updateDocumentDetails(DriverDto driverDto) {

        Driver driver = driverRepository.findById(Long.valueOf(driverDto.getId()))
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        UploadToCloudinary uploadToCloudinary = new UploadToCloudinary();

        // Update the fields
        driver.setLicenseNumber(driverDto.getLicenseNumber());
        driver.setLicenseExpiry(driverDto.getLicenseExpiry());
        driver.setHasExpire(driverDto.isHasExpire());
        driver.setLiRearPic(uploadToCloudinary.uploadImage(cloudinary, driverDto.getLiRearPic()));
        driver.setLiFrontPic(uploadToCloudinary.uploadImage(cloudinary, driverDto.getLiFrontPic()));
        driver.setProfilePic(uploadToCloudinary.uploadImage(cloudinary, driverDto.getProfilePic()));
        driver.setNicFrontPic(uploadToCloudinary.uploadImage(cloudinary, driverDto.getNicFrontPic()));
        driver.setNicRearPic(uploadToCloudinary.uploadImage(cloudinary, driverDto.getNicRearPic()));
        driver.setBillingProof(uploadToCloudinary.uploadImage(cloudinary, driverDto.getBillingProof()));
        driver.setCompletion(2);

        driverRepository.save(driver);  // This will update the existing record
    }

    @Transactional
    public void updateVehicleDetails(DriverDto driverDto) {

            Driver driver = driverRepository.findById(Long.valueOf(driverDto.getId()))
                    .orElseThrow(() -> new RuntimeException("Driver not found"));

            driver.setVTypeId(vehicleTypeRepository.findById(driverDto.getVTypeId())
                .orElseThrow(() -> new RuntimeException("Vehicle Type not found")));
            driver.setCompletion(3);

            driverRepository.save(driver);
    }

    public void updateDriver(@Valid DriverDto driverDto) {
        Driver driver = driverRepository.findById(Long.valueOf(driverDto.getId()))
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        UploadToCloudinary uploadToCloudinary = new UploadToCloudinary();

        driver.setCity(driverDto.getCity());
        driver.setProvince(driverDto.getProvince());
        driver.setPostalCode(driverDto.getPostalCode());
        driver.setContactNumber(driverDto.getContactNumber());
        driver.setAddressLine1(driverDto.getAddressLine1());
        driver.setAddressLine2(driverDto.getAddressLine2());
        driver.setLicenseExpiry(driverDto.getLicenseExpiry());
        driver.setHasExpire(driverDto.isHasExpire());
        driver.setProfilePic(uploadToCloudinary.uploadImage(cloudinary, driverDto.getProfilePic()));

        driverRepository.save(driver);

    }

    @Transactional
    public void verifyDriver(VerifyDto driverDto) {
        ReviewBoard user = reviewBoardRepository.findById(driverDto.getReviewId()).orElseThrow(() -> new RuntimeException("User not found"));
        Driver driver = new Driver();
        driver.setId(driverDto.getId());
        driver.setReviewBoardId(user);
        driverRepository.verifyDriver(driver.getId(), VerifyStatus.verified, driver.getReviewBoardId(), LocalDateTime.now());

    }

    @Transactional
    public void rejectDriver(VerifyDto driverDto) {
        ReviewBoard user = reviewBoardRepository.findById(driverDto.getReviewId()).orElseThrow(() -> new RuntimeException("User not found"));
        Driver driver = new Driver();
        driver.setId(driverDto.getId());
        driver.setReviewBoardId(user);
        driverRepository.verifyDriver(driver.getId(), VerifyStatus.rejected, driver.getReviewBoardId(), LocalDateTime.now());
    }
}
