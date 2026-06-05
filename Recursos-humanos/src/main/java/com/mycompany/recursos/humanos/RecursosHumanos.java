package com.mycompany.recursos.humanos;

public class RecursosHumanos {
    public static void main(String[] args) {
        GeneradorPDF pdf = new GeneradorPDF();
        EnviadorCorreo correo = new EnviadorCorreo();
        
        System.out.println("Generando PDF...");
        String rutaArchivo = pdf.crearBoleta("Carol Martinez", 1500.50);
        
        System.out.println("Enviando Correo...");
        // Pon aquí un correo al que tengas acceso para ver si llega
        correo.enviar("correo_de_prueba@gmail.com", "Carol Martinez", rutaArchivo); 
    }
}