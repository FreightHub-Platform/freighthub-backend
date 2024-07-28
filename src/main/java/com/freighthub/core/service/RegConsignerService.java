package com.freighthub.core.service;

import com.freighthub.core.dto.ConsignerDto;
import com.freighthub.core.repository.ConsignerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegConsignerService {

    private static final Logger logger = LoggerFactory.getLogger(RegConsignerService.class);

    @Autowired
    private ConsignerRepository consignerRepository;

    @Transactional
    public void updateBusinessDetails(ConsignerDto consignerDto) {
        consignerRepository.updateBusinessDetails(
                consignerDto.getId(),
                consignerDto.getBusinessName(),
                consignerDto.getBrn()
        );
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
    public void verifyConsigner(ConsignerDto consignerDto){
        consignerRepository.verifyConsigner(consignerDto.getId());
    }
}
