package com.freighthub.core.controller;

import com.freighthub.core.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailController {

  @Autowired
  private MailService emailService;

  @GetMapping("/send-mail")
  public ResponseEntity<String> sendMail(@RequestParam String to, @RequestParam String subject, @RequestParam String text){

    try{
      emailService.sendSimpleMail(to, subject, text);
      return ResponseEntity.ok("Mail sent successfully");
    } catch (MailException e) {
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to send email: " + e.getMessage());
    }

  }
}
