package com.freighthub.core.repository;

import com.freighthub.core.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {
  OTP findByOtpAndPhoneNumber(String otp, String phoneNumber);
}
