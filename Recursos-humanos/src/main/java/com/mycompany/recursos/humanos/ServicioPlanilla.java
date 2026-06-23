package com.mycompany.recursos.humanos;

import com.mycompany.recursos.humanos.ProcesadorNomina;
import com.mycompany.recursos.humanos.ControlHoras;
// Conectamos con nuestro motor centralizado de XAMPP
import com.mycompany.recursos.humanos.Conexion; 

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ServicioPlanilla {

    public void procesarPlanillaGeneral() {
        Connection conexion = null;
        PreparedStatement psEmpleados = null;
        PreparedStatement psInsertPago = null;
        ResultSet rsEmpleados = null;

        try {
            // 1. 🔌 RECONEXIÓN: Iniciamos la conexión (autoCommit = false ya viene configurado)
            conexion = Conexion.obtenerConexion(); 

            // 2. Preparamos las consultas SQL
            // Traemos solo a los empleados que están activos
            String queryEmpleados = "SELECT id_empleado, salario_base FROM empleados WHERE estado = true";
            // Insertamos en la tabla real aprobada en el script SQL
            String queryInsert = "INSERT INTO pagos_nomina (id_empleado, fecha_pago, total_ingresos, total_deducciones, salario_neto, descripcion_nomina) VALUES (?, ?, ?, ?, ?, ?)";
            
            psEmpleados = conexion.prepareStatement(queryEmpleados);
            psInsertPago = conexion.prepareStatement(queryInsert);
            rsEmpleados = psEmpleados.executeQuery();

            LocalDate fechaHoy = LocalDate.now();
            int empleadosProcesados = 0;

            // 3. 🔄 BUCLE DINÁMICO: Recorremos todos los empleados activos
            while (rsEmpleados.next()) {
                int idEmpleado = rsEmpleados.getInt("id_empleado");
                BigDecimal salarioBase = rsEmpleados.getBigDecimal("salario_base");

                // ⚠️ NOTA DE INTEGRACIÓN: Por ahora usaremos el salario base completo como ingreso.
                // En el siguiente paso, conectaremos ControlHoras para calcular deducciones por llegadas tardías.
                BigDecimal totalIngresos = salarioBase; 
                
                // Calculamos el salario neto llamando a nuestro procesador dinámico actualizado
                // (Para fines de este ejemplo, asumimos 1 hora de trabajo y el salario base como pago por hora)
                BigDecimal salarioNetoFinal = ProcesadorNomina.obtenerSalarioNeto(BigDecimal.ONE, totalIngresos);
                
                // Deducciones = Ingresos - Neto
                BigDecimal totalDeducciones = totalIngresos.subtract(salarioNetoFinal);

                // 4. Preparamos los parámetros para el INSERT en pagos_nomina
                psInsertPago.setInt(1, idEmpleado);
                psInsertPago.setDate(2, java.sql.Date.valueOf(fechaHoy)); // Fecha de pago
                psInsertPago.setBigDecimal(3, totalIngresos);
                psInsertPago.setBigDecimal(4, totalDeducciones);
                psInsertPago.setBigDecimal(5, salarioNetoFinal);
                psInsertPago.setString(6, "Pago de nómina automático - " + fechaHoy.toString());
                
                // Ejecuta el comando en la memoria caché temporal de MariaDB
                psInsertPago.executeUpdate(); 
                empleadosProcesados++;
            }

            // =================================================================
            // 🛡️ CONSOLIDACIÓN ACID (COMMIT)
            // =================================================================
            conexion.commit(); 
            System.out.println("✅ ¡Transacción Exitosa! " + empleadosProcesados + " empleados procesados y guardados firmemente en XAMPP.");

        } catch (Exception e) {
            // =================================================================
            // 🚨 SALVAVIDAS CRÍTICO (ROLLBACK)
            // =================================================================
            System.err.println("❌ ¡ALERTA CRÍTICA! Hubo un error en el proceso de planilla: " + e.getMessage());
            System.err.println("🔄 Aplicando Rollback de seguridad. Ningún dato fue alterado en la base de datos.");
            
            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error al intentar hacer rollback: " + ex.getMessage());
                }
            }
        } finally {
            // 5. Limpieza de memoria y cierre de conexiones
            try {
                if (rsEmpleados != null) rsEmpleados.close();
                if (psEmpleados != null) psEmpleados.close();
                if (psInsertPago != null) psInsertPago.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexiones: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        ServicioPlanilla servicio = new ServicioPlanilla();
        System.out.println("Iniciando procesamiento de nómina masiva...");
        servicio.procesarPlanillaGeneral();
    }
}