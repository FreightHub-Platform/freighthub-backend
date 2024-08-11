package com.freighthub.core.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.freighthub.core.dto.ConsignerDto;
import com.freighthub.core.dto.GetAnyId;
import com.freighthub.core.repository.ConsignerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

@Service
public class RegConsignerService {

    private static final Logger logger = LoggerFactory.getLogger(RegConsignerService.class);

    @Autowired
    private ConsignerRepository consignerRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Transactional
    public void updateBusinessDetails(ConsignerDto consignerDto) throws IOException {
        try {
            cloudinary.config.secure = true;
            System.out.println(cloudinary.config.cloudName);
            System.out.println(cloudinary.config.apiSecret);
            System.out.println(cloudinary.config.apiKey);
            String base64Image = consignerDto.getLogo();

            // Upload the logo to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(base64Image, ObjectUtils.asMap("resource_type", "image"));
            System.out.println("Upload Result: " + uploadResult);

            // Extract the URL from the upload result
            String logoUrl = (String) uploadResult.get("url");
            System.out.println("Logo URL: " + logoUrl);

            // Update the business details including the logo URL
            consignerRepository.updateBusinessDetails(
                    consignerDto.getId(),
                    consignerDto.getBusinessName(),
                    consignerDto.getBrn(),
                    logoUrl
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
    public void verifyConsigner(GetAnyId consignerDto){
        consignerRepository.verifyConsigner(consignerDto.getId());
    }
}
