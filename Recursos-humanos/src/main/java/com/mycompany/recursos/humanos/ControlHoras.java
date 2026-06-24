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
 * Preparado para soportar turnos nocturnos y jornadas que cruzan la medianoche.
 */
public class ControlHoras {

    private static final LocalTime ENTRADA_TEORICA = LocalTime.of(8, 0);  // 08:00 AM
    private static final int TOLERANCIA_MINUTOS = 15;
    private static final long MINUTOS_JORNADA_ESTANDAR = 9 * 60; // 9 horas (incluyendo almuerzo)

    // Clase contenedora para retornar los totales consolidados
    public static class ReporteIncidencias {
        public long totalMinutosTardanza = 0;
        public double totalHorasExtras = 0;
    }

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
                    
                    if (entradaStamp != null && salidaStamp != null) {
                        
                        // 1. Convertimos a LocalDateTime (Día + Hora exacta)
                        LocalDateTime entradaReal = entradaStamp.toLocalDateTime();
                        LocalDateTime salidaReal = salidaStamp.toLocalDateTime();
                        
                        // =========================================================
                        // LÓGICA 1: TARDANZAS (Detectando la hora teórica del día)
                        // =========================================================
                        LocalDateTime inicioEsperado = entradaReal.toLocalDate().atTime(ENTRADA_TEORICA);
                        
                        if (entradaReal.isAfter(inicioEsperado)) {
                            long minutosDemora = Duration.between(inicioEsperado, entradaReal).toMinutes();
                            if (minutosDemora > TOLERANCIA_MINUTOS) {
                                reporte.totalMinutosTardanza += minutosDemora;
                            }
                        }

                        // =========================================================
                        // 🟢 LÓGICA 2: HORAS EXTRAS Y TURNOS NOCTURNOS
                        // =========================================================
                        // Duration.between calcula la diferencia REAL, sin importar si pasó de un día a otro.
                        long minutosTotalesTrabajados = Duration.between(entradaReal, salidaReal).toMinutes();
                        
                        // Si la persona trabajó más de su jornada estándar (ej. 9 horas)
                        if (minutosTotalesTrabajados > MINUTOS_JORNADA_ESTANDAR) {
                            long minutosExtra = minutosTotalesTrabajados - MINUTOS_JORNADA_ESTANDAR;
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