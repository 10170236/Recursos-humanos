package com.mycompany.recursos.humanos;

import com.mycompany.recursos.humanos.CalculadorFinanciero;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProcesadorNomina {

    /**
     * Calcula el salario neto conectándose dinámicamente a la tabla parametros_ley.
     * * @param horasTrabajadas Total de horas calculadas por ControlHoras
     * @param pagoPorHora Cuánto se le paga por hora al empleado
     * @return El salario neto final redondeado y listo para guardar
     */
    public static BigDecimal obtenerSalarioNeto(BigDecimal horasTrabajadas, BigDecimal pagoPorHora) {
        // 1. Calculamos el sueldo base (Salario Bruto = Horas * Pago)
        BigDecimal salarioBruto = horasTrabajadas.multiply(pagoPorHora);
        
        // Acumulador para la suma de todos los descuentos (ISSS, AFP, Renta, etc.)
        BigDecimal totalDescuentos = BigDecimal.ZERO;
        
        // Consulta SQL para traer solo los impuestos que estén activos (estado = true)
        String sql = "SELECT porcentaje_descuento FROM parametros_ley WHERE estado = true";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // 2. Bucle Dinámico: Recorre cada impuesto de la base de datos
            while (rs.next()) {
                // Obtenemos el DECIMAL(5,4) de MySQL directo como BigDecimal
                BigDecimal porcentaje = rs.getBigDecimal("porcentaje_descuento");
                
                // Calculamos cuánto le quitaremos por este impuesto específico
                BigDecimal deduccion = salarioBruto.multiply(porcentaje);
                
                // Lo sumamos al total de descuentos
                totalDescuentos = totalDescuentos.add(deduccion);
            }

        } catch (Exception e) {
            System.err.println("🚨 Error crítico al leer parámetros de ley: " + e.getMessage());
        }

        // 3. Aplicamos la fórmula matemática: Salario_Neto = Bruto - Deducciones
        BigDecimal salarioNeto = salarioBruto.subtract(totalDescuentos);

        // 4. Retornamos el valor pasando por el redondeo bancario
        return CalculadorFinanciero.redondear(salarioNeto);
    }
}