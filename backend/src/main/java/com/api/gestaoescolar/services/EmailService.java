package com.api.gestaoescolar.services;

import java.util.Random;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String verificationCode) {
        String subject = "Confirmação de E-mail - Seu Código";
        String body = String.format("Seu código de verificação é: %s\nEle expira em 15 minutos.", verificationCode);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    public String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

}
