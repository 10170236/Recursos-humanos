package com.mycompany.recursos.humanos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;

/**
 * Motor Central de Nómina (El Corazón del Sistema).
 * Contiene tanto el cierre tradicional como el cierre por reloj biométrico.
 */
public class ServicioPlanilla {

    // =========================================================================
    // MÉTODO 1: Procesamiento Tradicional
    // =========================================================================
    public static boolean procesarCierreNominaMasivo(Date fechaInicio, Date fechaFin, Date fechaPago) {
        String sqlEmpleados = "SELECT id_empleado, nombre_completo, salario FROM empleados WHERE estado = '1' OR estado = 'Activo'";
        String sqlInsertPago = "INSERT INTO pagos_nomina (id_empleado, fecha_pago, total_ingresos, total_deducciones, salario_neto) VALUES (?, ?, ?, ?, ?)";
        
        Connection con = null;
        try {
            con = Conexion.obtenerConexion();
            con.setAutoCommit(false); 
            
            try (PreparedStatement psEmp = con.prepareStatement(sqlEmpleados);
                 ResultSet rsEmp = psEmp.executeQuery();
                 PreparedStatement psInsert = con.prepareStatement(sqlInsertPago)) {
                 
                int procesados = 0;
                while (rsEmp.next()) {
                    int idEmpleado = rsEmp.getInt("id_empleado");
                    BigDecimal salarioBase = rsEmp.getBigDecimal("salario").setScale(2, RoundingMode.HALF_EVEN);
                    
                    ControlHoras.ReporteIncidencias incidencias = ControlHoras.procesarIncidenciasPeriodo(idEmpleado, fechaInicio, fechaFin, con);
                    
                    BigDecimal ingresosHorasExtras = CalculadorFinanciero.calcularPagoHorasExtras(salarioBase, incidencias.totalHorasExtras);
                    BigDecimal descuentoTardanzas = CalculadorFinanciero.calcularDescuentoTardanza(salarioBase, incidencias.totalMinutosTardanza);
                    
                    BigDecimal totalIngresos = salarioBase.add(ingresosHorasExtras).setScale(2, RoundingMode.HALF_EVEN);
                    
                    BigDecimal descuentoISSS = CalculadorFinanciero.calcularRetencionLey(totalIngresos, "ISSS", con);
                    BigDecimal descuentoAFP = CalculadorFinanciero.calcularRetencionLey(totalIngresos, "AFP", con);
                    BigDecimal descuentoRenta = CalculadorFinanciero.calcularRetencionLey(totalIngresos, "Renta", con);
                    
                    BigDecimal totalDeducciones = descuentoISSS.add(descuentoAFP).add(descuentoRenta).add(descuentoTardanzas).setScale(2, RoundingMode.HALF_EVEN);
                    BigDecimal salarioNeto = totalIngresos.subtract(totalDeducciones).setScale(2, RoundingMode.HALF_EVEN);
                    
                    psInsert.setInt(1, idEmpleado);
                    psInsert.setDate(2, fechaPago);
                    psInsert.setBigDecimal(3, totalIngresos);
                    psInsert.setBigDecimal(4, totalDeducciones);
                    psInsert.setBigDecimal(5, salarioNeto);
                    psInsert.addBatch(); 
                    
                    procesados++;
                }
                
                if (procesados > 0) {
                    psInsert.executeBatch();
                }
                
                con.commit();
                return true;
            }
            
        } catch (Exception e) {
            System.err.println("🚨 Error en método masivo tradicional: " + e.getMessage());
            if (con != null) {
                try { con.rollback(); } catch (Exception ex) { }
            }
            return false;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (Exception e) { }
            }
        }
    }

    // =========================================================================
    // 🟢 MÉTODO 2 OPTIMIZADO: Sincronizado con el Reloj y Parámetros Dinámicos
    // =========================================================================
    public static boolean procesarCierreNominaPorReloj(Date fechaInicio, Date fechaFin, Date fechaPago) {
        String sqlEmpleados = "SELECT id_empleado, salario FROM empleados WHERE estado = '1' OR estado = 'Activo'";
        String sqlInsertNomina = "INSERT INTO pagos_nomina (id_empleado, fecha_pago, total_ingresos, total_deducciones, salario_neto) VALUES (?, ?, ?, ?, ?)";
        String sqlLeyes = "SELECT porcentaje_descuento FROM parametros_ley WHERE estado = true";

        Connection con = null;
        try {
            con = Conexion.obtenerConexion();
            con.setAutoCommit(false); 

            try (PreparedStatement psEmp = con.prepareStatement(sqlEmpleados);
                 ResultSet rsEmp = psEmp.executeQuery();
                 PreparedStatement psInsert = con.prepareStatement(sqlInsertNomina);
                 PreparedStatement psLeyes = con.prepareStatement(sqlLeyes)) {
                 
                int procesados = 0;

                while (rsEmp.next()) {
                    int idEmpleado = rsEmp.getInt("id_empleado");
                    BigDecimal salarioBase = rsEmp.getBigDecimal("salario").setScale(2, RoundingMode.HALF_EVEN);

                    // 1. Conexión directa con el Reloj Biométrico dinámico
                    ControlHoras.ReporteIncidencias incidencias = ControlHoras.procesarIncidenciasPeriodo(idEmpleado, fechaInicio, fechaFin, con);

                    // 2. Ecuación Financiera de precisión (Jornada base mensual = 240 horas)
                    BigDecimal pagoPorHora = salarioBase.divide(new BigDecimal("240"), 4, RoundingMode.HALF_EVEN);

                    // [Horas Extras con recargo del 1.5]
                    BigDecimal pagoHorasExtras = BigDecimal.valueOf(incidencias.totalHorasExtras)
                            .multiply(pagoPorHora)
                            .multiply(new BigDecimal("1.5"))
                            .setScale(2, RoundingMode.HALF_EVEN);

                    // [Descuento por Tardanza convertido de minutos a valor monetario]
                    BigDecimal deduccionTardanza = BigDecimal.valueOf(incidencias.totalMinutosTardanza)
                            .divide(new BigDecimal("60"), 4, RoundingMode.HALF_EVEN)
                            .multiply(pagoPorHora)
                            .setScale(2, RoundingMode.HALF_EVEN);

                    // Bruto acumulado para la base imponible
                    BigDecimal totalIngresos = salarioBase.add(pagoHorasExtras).setScale(2, RoundingMode.HALF_EVEN);

                    // 3. Deducción Dinámica de Impuestos de Ley de la Base de Datos
                    BigDecimal totalDeduccionesLey = BigDecimal.ZERO;
                    try (ResultSet rsLeyes = psLeyes.executeQuery()) {
                        while (rsLeyes.next()) {
                            BigDecimal porcentaje = rsLeyes.getBigDecimal("porcentaje_descuento");
                            BigDecimal deduccion = totalIngresos.multiply(porcentaje).setScale(2, RoundingMode.HALF_EVEN);
                            totalDeduccionesLey = totalDeduccionesLey.add(deduccion);
                        }
                    }

                    // 4. Consolidación de la ecuación del Arquitecto
                    BigDecimal totalDeducciones = totalDeduccionesLey.add(deduccionTardanza).setScale(2, RoundingMode.HALF_EVEN);
                    BigDecimal salarioNeto = totalIngresos.subtract(totalDeducciones).setScale(2, RoundingMode.HALF_EVEN);

                    // 5. Inyección al lote transaccional
                    psInsert.setInt(1, idEmpleado);
                    psInsert.setDate(2, fechaPago);
                    psInsert.setBigDecimal(3, totalIngresos);
                    psInsert.setBigDecimal(4, totalDeducciones);
                    psInsert.setBigDecimal(5, salarioNeto);
                    psInsert.addBatch();

                    procesados++;
                }

                // Si hay empleados procesados, se ejecuta la transacción en bloque de forma ultra rápida
                if (procesados > 0) {
                    psInsert.executeBatch();
                }

                con.commit(); 
                return true;
            }

        } catch (Exception e) {
            System.err.println("🚨 Error crítico al cerrar nómina por asistencia: " + e.getMessage());
            if (con != null) {
                try { con.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (Exception ex) { ex.printStackTrace(); }
            }
        }
    }
}