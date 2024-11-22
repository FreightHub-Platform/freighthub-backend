package com.freighthub.core.service;
import com.freighthub.core.entity.AdminDetails;
import com.freighthub.core.repository.AdminDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminDetailsService {

    @Autowired
    private AdminDetailsRepository adminDetailsRepository;

    public AdminDetails saveAdmin(AdminDetails adminDetails) {
        return adminDetailsRepository.save(adminDetails);
    }

    public Optional<AdminDetails> findByUid(String uid) {
        return adminDetailsRepository.findById(uid);
    }

    public AdminDetails findByMobileNumber(String mobileNumber) {
        return adminDetailsRepository.findByMobileNumber(mobileNumber);
    }

    public void deleteAdmin(String uid) {
        adminDetailsRepository.deleteById(uid);
    }
}