package com.freighthub.core.service;

import com.freighthub.core.dto.RegisterRequest;
import com.freighthub.core.entity.*;
import com.freighthub.core.repository.ConsignerRepository;
import com.freighthub.core.repository.DriverRepository;
import com.freighthub.core.repository.FleetOwnerRepository;
import com.freighthub.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConsignerRepository consignerRepository;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private FleetOwnerRepository fleetOwnerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(RegisterRequest registerRequest) {
        logger.info("Registering user: {}", registerRequest.getUsername());

        switch(registerRequest.getRole()) {
            case admin:
                User admin = new User();
                admin.setId(registerRequest.getId());
                admin.setUsername(registerRequest.getUsername());
//                admin.setPassword(registerRequest.getPassword());
                admin.setRole(registerRequest.getRole());
                userRepository.save(admin);
                return admin;

            case review_board:
                ReviewBoard review_board = new ReviewBoard();
                review_board.setId(registerRequest.getId());
                review_board.setUsername(registerRequest.getUsername());
//                review_board.setPassword(registerRequest.getPassword());
                review_board.setRole(registerRequest.getRole());
                userRepository.save(review_board);
                return review_board;

            case fleet_owner:
                FleetOwner fleet_owner = new FleetOwner();
                fleet_owner.setId(registerRequest.getId());
                fleet_owner.setUsername(registerRequest.getUsername());
                fleet_owner.setRole(registerRequest.getRole());
                fleetOwnerRepository.save(fleet_owner);
                return fleet_owner;

            case consigner:
                Consigner consigner = new Consigner();
                consigner.setId(registerRequest.getId());
                consigner.setUsername(registerRequest.getUsername());
//                consigner.setPassword(registerRequest.getPassword());
                consigner.setRole(registerRequest.getRole());
                consignerRepository.save(consigner);
                return consigner;

            case driver:
                Driver driver = new Driver();
                driver.setId(registerRequest.getId());
                driver.setUsername(registerRequest.getUsername());
//                driver.setPassword(registerRequest.getPassword());
                driver.setRole(registerRequest.getRole());
                driverRepository.save(driver);
                return driver;
            // Add cases for other roles...
            default:
                throw new IllegalArgumentException("Unknown role: " + registerRequest.getRole());
        }
    }

    @Transactional(readOnly = true)
    public Integer loginCheck(User user) {
        Integer completion = null;
        switch (user.getRole()) {
            case consigner:
                completion = consignerRepository.findCompletionByUid((long) user.getId());
                break;
            case fleet_owner:
                completion = fleetOwnerRepository.findCompletionByUid((long) user.getId());
                break;
            case driver:
                completion = driverRepository.findCompletionByUid((long) user.getId());
                break;
            default:
                break;
        }
        return completion;
    }

//    public User loginUser(RegisterRequest registerRequest) {
//        logger.info("Logging in user: {}", registerRequest.getUsername());
//
//        switch(registerRequest.getRole()) {
//            case admin:
//                User admin = new User();
//                admin.setId(registerRequest.getId());
//                admin.setUsername(registerRequest.getUsername());
//                admin.setPassword(registerRequest.getPassword());
//                admin.setRole(registerRequest.getRole());
//                userRepository.save(admin);
//                return admin;
//
//            case review_board:
//                User review_board = new User();
//                review_board.setId(registerRequest.getId());
//                review_board.setUsername(registerRequest.getUsername());
//                review_board.setPassword(registerRequest.getPassword());
//                review_board.setRole(registerRequest.getRole());
//                userRepository.save(review_board);
//                return review_board;
//
//            case consigner:
//                Consigner consigner = new Consigner();
//                consigner.setId(registerRequest.getId());
//                consigner.setUsername(registerRequest.getUsername());
//                consigner.setPassword(registerRequest.getPassword());
//                consigner.setRole(registerRequest.getRole());
//                consignerRepository.save(consigner);
//                return consigner;
//
//            case driver:
//                Driver driver = new Driver();
//                driver.setId(registerRequest.getId());
//                driver.setUsername(registerRequest.getUsername());
//                driver.setPassword(registerRequest.getPassword());
//                driver.setRole(registerRequest.getRole());
//                driverRepository.save(driver);
//                return driver;
//            // Add cases for other roles...
//            default:
//                throw new IllegalArgumentException("Unknown role: " + registerRequest.getRole());
//        }
//
//        return userRepository.findByUsername(registerRequest.getUsername())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//    }
}
