package com.mycompany.recursos.humanos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Motor de Gestión de Tiempos e Incidencias.
 * Compara las marcas reales contra los parámetros de la jornada laboral establecida.
 */
public class ControlHoras {

    private static final LocalTime ENTRADA_TEORICA = LocalTime.of(8, 0);  // 08:00 AM
    private static final LocalTime SALIDA_TEORICA = LocalTime.of(17, 0); // 05:00 PM
    private static final int TOLERANCIA_MINUTOS = 15;

    // Clase contenedora para retornar los totales consolidados de un periodo
    public static class ReporteIncidencias {
        public long totalMinutosTardanza = 0;
        public double totalHorasExtras = 0;
    }

    /**
     * Procesa todas las marcaciones de un empleado en un rango de fechas.
     */
    public static ReporteIncidencias procesarIncidenciasPeriodo(int idEmpleado, java.sql.Date fechaInicio, java.sql.Date fechaFin, Connection con) {
        ReporteIncidencias reporte = new ReporteIncidencias();
        
        String sql = "SELECT fecha_hora_entrada, fecha_hora_salida FROM marcaciones " +
                     "WHERE id_empleado = ? AND DATE(fecha_hora_entrada) BETWEEN ? AND ?";
                     
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ps.setDate(2, fechaInicio);
            ps.setDate(3, fechaFin);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp entradaStamp = rs.getTimestamp("fecha_hora_entrada");
                    Timestamp salidaStamp = rs.getTimestamp("fecha_hora_salida");
                    
                    if (entradaStamp != null) {
                        LocalDateTime entradaReal = entradaStamp.toLocalDateTime();
                        LocalTime horaEntrada = entradaReal.toLocalTime();
                        
                        // Evaluar entrada tardía
                        if (horaEntrada.isAfter(ENTRADA_TEORICA)) {
                            long minutosDemora = Duration.between(ENTRADA_TEORICA, horaEntrada).toMinutes();
                            if (minutosDemora > TOLERANCIA_MINUTOS) {
                                reporte.totalMinutosTardanza += minutosDemora;
                            }
                        }
                    }
                    
                    if (salidaStamp != null) {
                        LocalDateTime salidaReal = salidaStamp.toLocalDateTime();
                        LocalTime horaSalida = salidaReal.toLocalTime();
                        
                        // Evaluar horas extras (Turno Diurno estándar)
                        if (horaSalida.isAfter(SALIDA_TEORICA)) {
                            long minutosExtra = Duration.between(SALIDA_TEORICA, horaSalida).toMinutes();
                            reporte.totalHorasExtras += (minutosExtra / 60.0);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("🚨 Error procesando control de horas: " + e.getMessage());
        }
        return reporte;
    }
}