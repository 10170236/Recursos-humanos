package com.mycompany.recursos.humanos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Motor Financiero Avanzado.
 * Aplica Redondeo Bancario (Half-Even) y consultas dinámicas de ley sin valores quemados.
 */
public class CalculadorFinanciero {

    /**
     * Obtiene el porcentaje de retención desde la base de datos y calcula el descuento.
     */
    public static BigDecimal calcularRetencionLey(BigDecimal salarioBruto, String nombreRetencion, Connection con) throws Exception {
        String sql = "SELECT porcentaje_descuento FROM parametros_ley WHERE nombre_retencion = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreRetencion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal porcentaje = rs.getBigDecimal("porcentaje_descuento");
                    
                    // Operación: Salario * Porcentaje
                    BigDecimal retencion = salarioBruto.multiply(porcentaje);
                    
                    // Aplicación obligatoria de Redondeo Bancario (HALF_EVEN) a 2 decimales
                    return retencion.setScale(2, RoundingMode.HALF_EVEN);
                }
            }
        }
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
    }

    /**
     * Calcula el valor exacto de las horas extras (pago base por hora * factor 1.5).
     */
    public static BigDecimal calcularPagoHorasExtras(BigDecimal salarioBase, double horasExtrasTrabajadas) {
        if (horasExtrasTrabajadas <= 0) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
        
        // Asumiendo mes laboral estándar de 30 días y jornada de 8 horas (240 horas al mes)
        BigDecimal divisorHorasMes = new BigDecimal("240.00");
        BigDecimal valorHoraNormal = salarioBase.divide(divisorHorasMes, 4, RoundingMode.HALF_EVEN);
        
        BigDecimal factorRecargo = new BigDecimal("1.50"); // Recargo del 50% por hora extra
        BigDecimal valorHoraExtra = valorHoraNormal.multiply(factorRecargo);
        
        BigDecimal cantidadHoras = BigDecimal.valueOf(horasExtrasTrabajadas);
        
        return valorHoraExtra.multiply(cantidadHoras).setScale(2, RoundingMode.HALF_EVEN);
    }

    /**
     * Calcula el descuento exacto por minutos de llegadas tardías.
     */
    public static BigDecimal calcularDescuentoTardanza(BigDecimal salarioBase, long minutosTardanza) {
        if (minutosTardanza <= 0) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
        
        // Salario mensual / (30 días * 8 horas * 60 minutos = 14,400 minutos al mes)
        BigDecimal divisorMinutosMes = new BigDecimal("14400.00");
        BigDecimal valorMinuto = salarioBase.divide(divisorMinutosMes, 4, RoundingMode.HALF_EVEN);
        
        BigDecimal minutos = BigDecimal.valueOf(minutosTardanza);
        
        return valorMinuto.multiply(minutos).setScale(2, RoundingMode.HALF_EVEN);
    }
}