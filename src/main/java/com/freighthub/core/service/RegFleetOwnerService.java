package com.freighthub.core.service;

import com.freighthub.core.dto.FleetOwnerDto;
import com.freighthub.core.dto.VerifyDto;
import com.freighthub.core.entity.FleetOwner;
import com.freighthub.core.entity.ReviewBoard;
import com.freighthub.core.repository.FleetOwnerRepository;
import com.freighthub.core.repository.ReviewBoardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class RegFleetOwnerService {

    private static final Logger logger = LoggerFactory.getLogger(RegConsignerService.class);

    @Autowired
    private FleetOwnerRepository fleetOwnerRepository;

    @Autowired
    private ReviewBoardRepository reviewBoardRepository;

    @Transactional
    public void updateBusinessDetails(FleetOwnerDto fleetOwnerDto) throws IOException {

        try {
            fleetOwnerRepository.updateBusinessDetails(
                    fleetOwnerDto.getId(),
                    fleetOwnerDto.getCompanyName(),
                    fleetOwnerDto.getBrn()
            );
            System.out.println("Business details updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Failed to update business details", e);
        }
    }

    @Transactional
    public void updateContactDetails(FleetOwnerDto fleetOwnerDto) {
        fleetOwnerRepository.updateContactDetails(
                fleetOwnerDto.getId(),
                fleetOwnerDto.getMainNumber(),
                fleetOwnerDto.getAltNumber()
        );
    }

    @Transactional
    public void updateLocationDetails(FleetOwnerDto fleetOwnerDto) {
        fleetOwnerRepository.updateLocationDetails(
                fleetOwnerDto.getId(),
                fleetOwnerDto.getAddressLine1(),
                fleetOwnerDto.getAddressLine2(),
                fleetOwnerDto.getCity(),
                fleetOwnerDto.getProvince(),
                fleetOwnerDto.getPostalCode()
        );
    }

    @Transactional
    public void verifyFleetOwner(VerifyDto fleetOwnerDto){
        ReviewBoard user = reviewBoardRepository.findById(fleetOwnerDto.getReviewId()).orElseThrow(() -> new RuntimeException("User not found"));
        FleetOwner fleetOwner = new FleetOwner();
        fleetOwner.setId(fleetOwnerDto.getId());
        fleetOwner.setReviewBoardId(user);
        fleetOwnerRepository.verifyFleetOwner(fleetOwner.getId(), fleetOwner.getReviewBoardId(), LocalDateTime.now());
    }
}
