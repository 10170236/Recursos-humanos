package com.mycompany.recursos.humanos;

import java.time.LocalTime;
import java.time.Duration;

public class ControlHoras {

    public static double calcularHorasTrabajadas(LocalTime entrada, LocalTime salida) {
        // Si cruza la medianoche (Ej: entra a las 22:00 y sale a las 06:00)
        if (salida.isBefore(entrada)) {
            long minutosPrimerDia = Duration.between(entrada, LocalTime.MIDNIGHT).toMinutes();
            if (minutosPrimerDia < 0) {
                minutosPrimerDia += 1440; // 1440 son los minutos que tiene un día completo (24h * 60m)
            }
            
            long minutosSegundoDia = Duration.between(LocalTime.MIDNIGHT, salida).toMinutes();
            
            return (minutosPrimerDia + minutosSegundoDia) / 60.0;
        } else {
            // Turno normal en el mismo día
            return Duration.between(entrada, salida).toMinutes() / 60.0;
        }
    }

    public static void main(String[] args) {
        LocalTime horaEntrada = LocalTime.of(22, 0); // 10:00 PM
        LocalTime horaSalida = LocalTime.of(6, 0);   // 06:00 AM
        
        double horasTotales = calcularHorasTrabajadas(horaEntrada, horaSalida);
        
        System.out.println("====== PRUEBA DE TURNOS NOCTURNOS ======");
        System.out.println("Hora de Entrada: " + horaEntrada);
        System.out.println("Hora de Salida:  " + horaSalida);
        System.out.println("Total de horas calculadas: " + horasTotales);
    }
}