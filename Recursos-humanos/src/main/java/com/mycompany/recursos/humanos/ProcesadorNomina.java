package com.mycompany.recursos.humanos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProcesadorNomina {

    /**
     * Calcula el salario neto conectándose dinámicamente a la tabla parametros_ley.
     * Aplica estrictamente el redondeo bancario (HALF_EVEN) en cada operación.
     * * @param horasTrabajadas Total de horas calculadas por ControlHoras
     * @param pagoPorHora Cuánto se le paga por hora al empleado
     * @return El salario neto final redondeado a 2 decimales y listo para la BD
     */
    public static BigDecimal obtenerSalarioNeto(BigDecimal horasTrabajadas, BigDecimal pagoPorHora) {
        // 1. Calculamos el sueldo base aplicando redondeo desde el primer paso
        BigDecimal salarioBruto = horasTrabajadas.multiply(pagoPorHora).setScale(2, RoundingMode.HALF_EVEN);
        
        // Acumulador para la suma de todos los descuentos
        BigDecimal totalDescuentos = BigDecimal.ZERO;
        
        // Consulta SQL: Nota -> Asegúrate de que la columna 'estado' (booleana) exista en tu tabla parametros_ley
        String sql = "SELECT porcentaje_descuento FROM parametros_ley WHERE estado = true";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // 2. Bucle Dinámico: Recorre cada impuesto activo
            while (rs.next()) {
                BigDecimal porcentaje = rs.getBigDecimal("porcentaje_descuento");
                
                // ⚠️ CRÍTICO: Calculamos la deducción y redondeamos INMEDIATAMENTE
                BigDecimal deduccion = salarioBruto.multiply(porcentaje).setScale(2, RoundingMode.HALF_EVEN);
                
                // Sumamos al total acumulado
                totalDescuentos = totalDescuentos.add(deduccion);
            }

        } catch (Exception e) {
            System.err.println("🚨 Error crítico al leer parámetros de ley: " + e.getMessage());
        }

        // 3. Aplicamos la fórmula matemática: Salario_Neto = Bruto - Deducciones
        BigDecimal salarioNeto = salarioBruto.subtract(totalDescuentos);

        // 4. Retornamos el valor asegurando la escala final de 2 decimales
        return salarioNeto.setScale(2, RoundingMode.HALF_EVEN);
    }
}