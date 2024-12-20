package com.freighthub.core.service;

import com.cloudinary.Cloudinary;
import com.freighthub.core.dto.ConsignerDto;
import com.freighthub.core.dto.VerifyDto;
import com.freighthub.core.entity.Consigner;
import com.freighthub.core.entity.ReviewBoard;
import com.freighthub.core.enums.VerifyStatus;
import com.freighthub.core.repository.ConsignerRepository;
import com.freighthub.core.repository.FleetOwnerRepository;
import com.freighthub.core.repository.ReviewBoardRepository;
import com.freighthub.core.util.UploadToCloudinary;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class RegConsignerService {

    private static final Logger logger = LoggerFactory.getLogger(RegConsignerService.class);

    @Autowired
    private ConsignerRepository consignerRepository;

    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private FleetOwnerRepository fleetOwnerRepository;
    @Autowired
    private ReviewBoardRepository reviewBoardRepository;


    @Transactional
    public void updateBusinessDetails(ConsignerDto consignerDto) throws IOException {
        try {
            cloudinary.config.secure = true;
            System.out.println(cloudinary.config.cloudName);
            System.out.println(cloudinary.config.apiSecret);
            System.out.println(cloudinary.config.apiKey);
//            String base64Image = consignerDto.getLogo();
//
//            // Upload the logo to Cloudinary
//            Map uploadResult = cloudinary.uploader().upload(base64Image, ObjectUtils.asMap("resource_type", "image"));
//            System.out.println("Upload Result: " + uploadResult);
//
//            // Extract the URL from the upload result
//            String logoUrl = (String) uploadResult.get("url");
//            System.out.println("Logo URL: " + logoUrl);

            // Convert base64-encoded PDF file to byte[]
            byte[] regDocBytes = Base64.getDecoder().decode(consignerDto.getRegDoc());

            UploadToCloudinary uploadToCloudinary = new UploadToCloudinary();
            String logoUrl = uploadToCloudinary.uploadImage(cloudinary, consignerDto.getLogo());


            // Update the business details including the logo URL
            consignerRepository.updateBusinessDetails(
                    consignerDto.getId(),
                    consignerDto.getBusinessName(),
                    consignerDto.getBrn(),
                    logoUrl,
                    regDocBytes
            );
            System.out.println("Business details updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Failed to update business details", e);
        }
    }

    @Transactional
    public void updateContactDetails(ConsignerDto consignerDto) {
        consignerRepository.updateContactDetails(
                consignerDto.getId(),
                consignerDto.getMainNumber(),
                consignerDto.getAltNumber()
        );
    }

    @Transactional
    public void updateLocationDetails(ConsignerDto consignerDto) {
        consignerRepository.updateLocationDetails(
                consignerDto.getId(),
                consignerDto.getAddressLine1(),
                consignerDto.getAddressLine2(),
                consignerDto.getCity(),
                consignerDto.getProvince(),
                consignerDto.getPostalCode()
        );
    }

    @Transactional
    public void verifyConsigner(VerifyDto consignerDto){
//        ReviewBoard user = reviewBoardRepository.findById(consignerDto.getReviewId()).orElseThrow(() -> new RuntimeException("User not found"));
        Consigner consigner = new Consigner();
        consigner.setId(consignerDto.getId());
//        consigner.setReviewBoardId(user);
        consignerRepository.verifyConsigner(consigner.getId(), VerifyStatus.verified, LocalDateTime.now());
    }

    @Transactional
    public void rejectConsigner(VerifyDto consignerDto){
//        ReviewBoard user = reviewBoardRepository.findById(consignerDto.getReviewId()).orElseThrow(() -> new RuntimeException("User not found"));
        Consigner consigner = new Consigner();
        consigner.setId(consignerDto.getId());
//        consigner.setReviewBoardId(user);
        consignerRepository.verifyConsigner(consigner.getId(), VerifyStatus.rejected, LocalDateTime.now());
    }

    public void updateConsigner(@Valid ConsignerDto consignerDto) {
        Consigner consigner = consignerRepository.findById(Long.valueOf(consignerDto.getId()))
                .orElseThrow(() -> new RuntimeException("Consigner not found"));

        UploadToCloudinary uploadToCloudinary = new UploadToCloudinary();

        consigner.setCity(consignerDto.getCity());
        consigner.setProvince(consignerDto.getProvince());
        consigner.setPostalCode(consignerDto.getPostalCode());
        consigner.setMainNumber(consignerDto.getMainNumber());
        consigner.setAltNumber(consignerDto.getAltNumber());
        consigner.setAddressLine1(consignerDto.getAddressLine1());
        consigner.setAddressLine2(consignerDto.getAddressLine2());
        consigner.setLogo(uploadToCloudinary.uploadImage(cloudinary, consignerDto.getLogo()));

        consignerRepository.save(consigner);
    }
}
