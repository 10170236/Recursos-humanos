package com.mycompany.recursos.humanos;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.File;
import java.util.Properties;

public class EnviadorCorreo {
    
    // Recuerda usar tu correo y una NUEVA contraseña de aplicación
    private final String remitente = "carolinavargas54321@gmail.com"; 
    private final String password = "ixgj ynvy dskm krhe"; 

    public void enviar(String destino, String empleado, String rutaPDF) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, password);
            }
        });

        try {
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(remitente));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destino));
            // Asunto formal del correo
            mensaje.setSubject("Emisión de Boleta de Pago - Recursos Humanos");

            // --- DISEÑO HTML DEL CORREO USANDO TU PALETA ---
            String contenidoHtml = 
                "<div style=\"font-family: Arial, sans-serif; color: #140f07; max-width: 600px; margin: 0 auto; border: 1px solid #9fabfa; border-radius: 8px; overflow: hidden;\">" +
                    "<div style=\"background-color: #1a1d36; padding: 20px; text-align: center;\">" +
                        "<h2 style=\"color: #ffffff; margin: 0;\">Gestión de Recursos Humanos</h2>" +
                    "</div>" +
                    "<div style=\"padding: 20px; background-color: #ffffff;\">" +
                        "<h3 style=\"color: #3e497e;\">Estimado/a " + empleado + ",</h3>" +
                        "<p style=\"font-size: 15px; line-height: 1.6; color: #140f07;\">" +
                            "Adjunto a este correo electrónico encontrarás tu <strong>boleta de pago oficial</strong> en formato PDF, correspondiente al período actual." +
                        "</p>" +
                        "<div style=\"background-color: #9fabfa; padding: 15px; border-left: 5px solid #6a76cc; margin: 25px 0; border-radius: 4px;\">" +
                            "<p style=\"margin: 0; color: #1a1d36; font-weight: bold;\">Importante: Conserva este documento para tus registros personales y/o contables.</p>" +
                        "</div>" +
                        "<p style=\"font-size: 12px; color: #3e497e; margin-top: 30px; text-align: center;\">" +
                            "Este es un mensaje generado automáticamente, por favor no respondas a esta dirección." +
                        "</p>" +
                    "</div>" +
                    "<div style=\"background-color: #6a76cc; padding: 12px; text-align: center;\">" +
                        "<p style=\"color: #ffffff; font-size: 12px; margin: 0;\">© 2026 Recursos Humanos - Documento Confidencial</p>" +
                    "</div>" +
                "</div>";

            MimeBodyPart textoHtml = new MimeBodyPart();
            // Le decimos a Java que el texto es HTML y no texto plano
            textoHtml.setContent(contenidoHtml, "text/html; charset=utf-8");

            MimeBodyPart adjunto = new MimeBodyPart();
            adjunto.attachFile(new File(rutaPDF));

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textoHtml);
            multipart.addBodyPart(adjunto);
            mensaje.setContent(multipart);

            Transport.send(mensaje);
            System.out.println("Correo enviado con éxito a " + destino);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}