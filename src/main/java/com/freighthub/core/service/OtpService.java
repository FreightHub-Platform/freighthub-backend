package com.freighthub.core.service;

import com.freighthub.core.entity.OTP;
import com.freighthub.core.repository.OTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.logging.Logger;

@Service
public class OtpService {

    @Autowired
    private OTPRepository otpRepository;

    @Value("${otp.api.key}")
    private String otpApiKey;

    @Value("${otp.user.id}")
    private String otpUserId;

    @Value("${otp.sender.id}")
    private String otpSenderId;

    @Value("${otp.endpoint}")
    private String otpEndpoint;

    private static final Logger logger = Logger.getLogger(OtpService.class.getName());

    public String generateOTP() {
        try {
            int[] ints = new Random().ints(0, 10).distinct().limit(6).toArray();
            StringBuilder sb = new StringBuilder();
            for (int i : ints) {
                sb.append(i);
            }
            System.out.println("OTP: " + sb.substring(0, 5));
            return sb.substring(0, 5);
        } catch (Exception e) {
            logger.severe("Error generating OTP: " + e.getMessage());
            return null;
        }
    }

    public String sendOtp(String otp, String phoneNumber) {
        try {
            String endpoint = String.format("%s?user_id=%s&api_key=%s&sender_id=%s&to=%s&message=%s",
                    otpEndpoint, otpUserId, otpApiKey, otpSenderId, "94" + phoneNumber, otp);

            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                saveOtp(otp, phoneNumber);
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                logger.info("OTP sent successfully: " + response.toString());
                return response.toString();
            } else {
                logger.severe("Failed to send OTP: HTTP error code : " + responseCode);
                return null;
            }
        } catch (Exception e) {
            logger.severe("Error sending OTP: " + e.getMessage());
            return null;
        }
    }

    private void saveOtp(String otp, String phoneNumber) {
        OTP otpEntity = new OTP();
        otpEntity.setOtp(otp);
        otpEntity.setPhoneNumber(phoneNumber);
        otpEntity.setIsUsed(0);
        otpRepository.save(otpEntity);
    }

    public boolean verifyOtp(String otp, String phoneNumber) {
        OTP otpEntity = otpRepository.findByOtpAndPhoneNumber(otp, phoneNumber);
        if (otpEntity != null) {
            if (otpEntity.getIsUsed() == 1) {
                return false;
            }
            otpEntity.setIsUsed(1);
            otpRepository.save(otpEntity);
            return true;
        }
        return false;
    }

    public String sendSuccessfulRegistrationMessage(String user , String number){

        String message = "Dear customer " +  user +" , You have successfully registered to the FreightHub platform with mobile " +
                "number :" + number+ ".";

        try {
            String endpoint = String.format("%s?user_id=%s&api_key=%s&sender_id=%s&to=%s&message=%s",
                    otpEndpoint, otpUserId, otpApiKey, otpSenderId, "94" + number, message);

            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                logger.info("Registration message sent successfully: " + response.toString());
                return response.toString();
            } else {
                logger.severe("Failed to send registration message : HTTP error code : " + responseCode);
                return null;
            }
        } catch (Exception e) {
            logger.severe("Error sending registration message : " + e.getMessage());
            return null;
        }
    }

    public String sendMessage (String phoneNumber , String message ){
        try {
            String endpoint = String.format("%s?user_id=%s&api_key=%s&sender_id=%s&to=%s&message=%s",
                    otpEndpoint, otpUserId, otpApiKey, otpSenderId, "94" + phoneNumber, message);

            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                logger.info("Message sent successfully: " + response.toString());
                return response.toString();
            } else {
                logger.severe("Failed to send message : HTTP error code : " + responseCode);
                return null;
            }
        } catch (Exception e) {
            logger.severe("Error sending registration message : " + e.getMessage());
            return null;
        }
    }

}
