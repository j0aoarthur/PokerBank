package com.j0aoarthur.pokerbank.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Calendar;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendVerificationEmail(String to, String name, String verificationLink) throws MessagingException {
        Context context = new Context();
        context.setVariable("nome_usuario", name);
        context.setVariable("link_de_verificacao", verificationLink);
        context.setVariable("ano", Calendar.getInstance().get(Calendar.YEAR));

        String htmlContent = templateEngine.process("verification-email", context);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("Confirme seu endereço de e-mail");
        helper.setText(htmlContent, true);

        mailSender.send(mimeMessage);
    }


}
