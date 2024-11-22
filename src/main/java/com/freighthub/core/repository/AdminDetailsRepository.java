package com.freighthub.core.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.freighthub.core.entity.AdminDetails;
@Repository
public interface AdminDetailsRepository extends JpaRepository<AdminDetails, String> {
    AdminDetails findByMobileNumber(String mobileNumber);
}