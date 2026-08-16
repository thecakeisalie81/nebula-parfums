package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.exception.EmailSendException;
import com.nebulaparfums.nebula_parfums.exception.InvalidPasswordException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void enviarCorreo(String destino, String asunto, String contenido) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(destino);
            mensaje.setSubject(asunto);
            mensaje.setText(contenido);
            mailSender.send(mensaje);
        }catch (Exception e){
            throw new EmailSendException("Error al enviar Correo", e);
        }
    }
}