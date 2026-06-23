package com.mycompany.recursos.humanos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;

/**
 * Motor Central de Nómina (El Corazón del Sistema).
 * Ejecuta el cierre masivo de pagos bajo estricto cumplimiento ACID de base de datos.
 */
public class ServicioPlanilla {

    /**
     * Ejecuta el cierre mensual de la nómina para todos los empleados activos.
     * Si ocurre un solo error, revierte los cambios para asegurar integridad financiera.
     */
    public static boolean procesarCierreNominaMasivo(Date fechaInicio, Date fechaFin, Date fechaPago) {
        String sqlEmpleados = "SELECT id_empleado, nombre_completo, salario_base FROM empleados WHERE estado = '1' OR estado = 'Activo'";
        String sqlInsertPago = "INSERT INTO pagos_nomina (id_empleado, fecha_pago, total_ingresos, total_deducciones, salario_neto) VALUES (?, ?, ?, ?, ?)";
        
        Connection con = null;
        
        try {
            con = Conexion.obtenerConexion();
            // ACTIVACIÓN DE ACID: Quitamos el autocommit para controlar manualmente la transacción
            con.setAutoCommit(false); 
            
            try (PreparedStatement psEmp = con.prepareStatement(sqlEmpleados);
                 ResultSet rsEmp = psEmp.executeQuery();
                 PreparedStatement psInsert = con.prepareStatement(sqlInsertPago)) {
                 
                int procesados = 0;
                
                while (rsEmp.next()) {
                    int idEmpleado = rsEmp.getInt("id_empleado");
                    String nombre = rsEmp.getString("nombre_completo");
                    BigDecimal salarioBase = rsEmp.getBigDecimal("salario_base").setScale(2, RoundingMode.HALF_EVEN);
                    
                    // 1. Invocar al motor de tiempos para consolidar incidencias reales del periodo
                    ControlHoras.ReporteIncidencias incidencias = ControlHoras.procesarIncidenciasPeriodo(idEmpleado, fechaInicio, fechaFin, con);
                    
                    // 2. Calcular montos financieros con el módulo de precisión matemática
                    BigDecimal ingresosHorasExtras = CalculadorFinanciero.calcularPagoHorasExtras(salarioBase, incidencias.totalHorasExtras);
                    BigDecimal descuentoTardanzas = CalculadorFinanciero.calcularDescuentoTardanza(salarioBase, incidencias.totalMinutosTardanza);
                    
                    // Fórmula de Ingresos: Salario Base + Horas Extras (Bonos omitidos por defecto o expandibles)
                    BigDecimal totalIngresos = salarioBase.add(ingresosHorasExtras).setScale(2, RoundingMode.HALF_EVEN);
                    
                    // 3. Cargar retenciones dinámicas directo desde la BD sin quemar lógica en Java
                    BigDecimal descuentoISSS = CalculadorFinanciero.calcularRetencionLey(totalIngresos, "ISSS", con);
                    BigDecimal descuentoAFP = CalculadorFinanciero.calcularRetencionLey(totalIngresos, "AFP", con);
                    BigDecimal descuentoRenta = CalculadorFinanciero.calcularRetencionLey(totalIngresos, "Renta", con);
                    
                    // Sumatoria de deducciones legales + penalizaciones por asistencia
                    BigDecimal totalDeducciones = descuentoISSS.add(descuentoAFP).add(descuentoRenta).add(descuentoTardanzas).setScale(2, RoundingMode.HALF_EVEN);
                    
                    // Aplicación rigurosa de la Ecuación del Arquitecto: Salario Neto = Ingresos - Deducciones
                    BigDecimal salarioNeto = totalIngresos.subtract(totalDeducciones).setScale(2, RoundingMode.HALF_EVEN);
                    
                    // 4. Setear los parámetros en el lote de guardado masivo
                    psInsert.setInt(1, idEmpleado);
                    psInsert.setDate(2, fechaPago);
                    psInsert.setBigDecimal(3, totalIngresos);
                    psInsert.setBigDecimal(4, totalDeducciones);
                    psInsert.setBigDecimal(5, salarioNeto);
                    psInsert.addBatch(); // Empaqueta para alta velocidad de ejecución (Menos de 10 seg)
                    
                    procesados++;
                }
                
                // Ejecución por lotes en el servidor de base de datos
                if (procesados > 0) {
                    psInsert.executeBatch();
                }
                
                // SI TODO FUE EXITOSO: Guardamos los cambios permanentemente de forma atómica
                con.commit();
                System.out.println("🚀 Cierre transaccional de nómina exitoso para " + procesados + " empleados.");
                return true;
            }
            
        } catch (Exception e) {
            // DETECCIÓN DE ANOMALÍAS: Si falla aunque sea en un registro, se ejecuta un rollback total
            System.err.println("🚨 Falla detectada en la transacción masiva. Ejecutando Rollback de emergencia...");
            if (con != null) {
                try {
                    con.rollback(); 
                } catch (Exception ex) {
                    System.err.println("Error crítico al revertir la transacción: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true); // Restauramos el comportamiento estándar
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}