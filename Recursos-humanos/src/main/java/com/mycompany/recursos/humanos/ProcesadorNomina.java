package com.mycompany.recursos.humanos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProcesadorNomina {

    /**
     * Aplica la ecuación del arquitecto usando los datos reales de incidencias.
     * Calcula deducciones por ley, descuentos por tardanzas y pagos por horas extras.
     */
    public static BigDecimal obtenerSalarioNeto(BigDecimal salarioBase, long minutosTardanza, double horasExtras) {
        
        // 1. Calculamos cuánto vale 1 hora de trabajo de este empleado
        // Asumiendo una jornada estándar de 30 días al mes, 8 horas diarias = 240 horas mensuales
        BigDecimal pagoPorHora = salarioBase.divide(new BigDecimal("240"), 4, RoundingMode.HALF_EVEN);
        
        // 2. CALCULAMOS LOS COMPONENTES DE LA ECUACIÓN
        
        // [Horas Extras] -> Se pagan con un recargo (ej. 1.5 veces el valor de la hora normal)
        BigDecimal pagoHorasExtras = BigDecimal.valueOf(horasExtras)
                .multiply(pagoPorHora)
                .multiply(new BigDecimal("1.5"))
                .setScale(2, RoundingMode.HALF_EVEN);
        
        // [Deducción por Tardanzas] -> Convertimos minutos a horas y los multiplicamos por el valor de su hora
        BigDecimal deduccionTardanza = BigDecimal.valueOf(minutosTardanza)
                .divide(new BigDecimal("60"), 4, RoundingMode.HALF_EVEN)
                .multiply(pagoPorHora)
                .setScale(2, RoundingMode.HALF_EVEN);
        
        // Unimos el Salario Base + Horas Extras (Dejamos Bonos en 0 por ahora)
        BigDecimal ingresosTotales = salarioBase.add(pagoHorasExtras);
        
        // Acumulador para los descuentos de ley
        BigDecimal totalDeduccionesLey = BigDecimal.ZERO;
        
        String sql = "SELECT porcentaje_descuento FROM parametros_ley WHERE estado = true";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // 3. Bucle Dinámico para Impuestos (AFP, Seguro, Renta, etc.)
            while (rs.next()) {
                BigDecimal porcentaje = rs.getBigDecimal("porcentaje_descuento");
                
                // Las leyes se aplican sobre los ingresos totales acumulados
                BigDecimal deduccion = ingresosTotales.multiply(porcentaje).setScale(2, RoundingMode.HALF_EVEN);
                totalDeduccionesLey = totalDeduccionesLey.add(deduccion);
            }

        } catch (Exception e) {
            System.err.println("🚨 Error crítico al leer parámetros de ley: " + e.getMessage());
        }

        // 4. AGRUPAMOS SEGÚN LA FÓRMULA MAESTRA
        // Total Deducciones = Leyes + Descuento por llegar tarde
        BigDecimal totalDescuentos = totalDeduccionesLey.add(deduccionTardanza);
        
        // Salario Neto = Ingresos - Descuentos
        BigDecimal salarioNeto = ingresosTotales.subtract(totalDescuentos);

        return salarioNeto.setScale(2, RoundingMode.HALF_EVEN);
    }
}