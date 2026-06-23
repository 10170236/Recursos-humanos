package com.mycompany.recursos.humanos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.time.LocalDateTime;

public class ControlHoras {

    /**
     * Calcula el total de horas trabajadas de un empleado consultando la tabla marcaciones.
     * * @param idEmpleado El ID del empleado en la base de datos.
     * @return Total de horas trabajadas en formato BigDecimal para precisión financiera.
     */
    public static BigDecimal obtenerTotalHoras(int idEmpleado) {
        BigDecimal totalHoras = BigDecimal.ZERO;
        
        // Query para obtener todas las marcaciones válidas (que tengan hora de salida registrada)
        // Nota: En un sistema en producción real, aquí agregaríamos un "WHERE fecha BETWEEN inicio AND fin" 
        // para filtrar por quincena o mes.
        String sql = "SELECT fecha_hora_entrada, fecha_hora_salida FROM marcaciones WHERE id_empleado = ? AND fecha_hora_salida IS NOT NULL";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setInt(1, idEmpleado);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // 1. Extraemos los DATETIME de MySQL y los pasamos a LocalDateTime de Java
                    LocalDateTime entrada = rs.getTimestamp("fecha_hora_entrada").toLocalDateTime();
                    LocalDateTime salida = rs.getTimestamp("fecha_hora_salida").toLocalDateTime();
                    
                    // 2. Calculamos los minutos exactos. ¡LocalDateTime maneja la medianoche y cambios de día por sí solo!
                    long minutosTrabajados = Duration.between(entrada, salida).toMinutes();
                    
                    // 3. Convertimos los minutos a horas decimales (minutos / 60) con Redondeo Bancario
                    BigDecimal horasTurno = new BigDecimal(minutosTrabajados)
                            .divide(new BigDecimal(60), 2, RoundingMode.HALF_EVEN);
                    
                    // 4. Sumamos las horas de este turno al acumulador total del empleado
                    totalHoras = totalHoras.add(horasTurno);
                }
            }
        } catch (Exception e) {
            System.err.println("🚨 Error al calcular horas del empleado " + idEmpleado + ": " + e.getMessage());
        }

        return totalHoras;
    }
}