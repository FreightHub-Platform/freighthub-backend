package com.freighthub.core.service;

import com.freighthub.core.entity.Consigner;
import com.freighthub.core.enums.VerifyStatus;
import com.freighthub.core.repository.ConsignerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConsignerService {

    private static final Logger logger = LoggerFactory.getLogger(RegConsignerService.class);

    @Autowired
    private ConsignerRepository consignerRepository;

    @Transactional(readOnly = true)
    public List<Consigner> getConsignersByVerifyStatus(VerifyStatus verifyStatus) {
        return consignerRepository.findConsignersByVerifyStatus(verifyStatus);
    }

    @Transactional(readOnly = true)
    public List<Consigner> getAllConsigners() {
        return consignerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Consigner getConsignerById(int id) {
        return consignerRepository.findById((long) id).orElse(null);
    }
}
