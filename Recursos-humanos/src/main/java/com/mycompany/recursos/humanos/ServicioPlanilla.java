package com.mycompany.recursos.humanos;

import com.mycompany.recursos.humanos.ProcesadorNomina;
import com.mycompany.recursos.humanos.ControlHoras;
// Conectamos con nuestro motor centralizado de XAMPP
import com.mycompany.recursos.humanos.Conexion; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalTime;

public class ServicioPlanilla {

    public void procesarPlanillaGeneral() {
        Connection conexion = null;
        PreparedStatement comando = null;

        try {
            // 1. 🔌 RECONEXIÓN: Llamamos a nuestra clase centralizada de XAMPP.
            // Recuerda que este método ya viene configurado con setAutoCommit(false)
            // de forma nativa para proteger tus transacciones bajo arquitectura ACID.
            conexion = Conexion.obtenerConexion(); 

            // Query estándar compatible con MariaDB
            String query = "INSERT INTO planilla (empleado_id, salario_final) VALUES (?, ?)";
            comando = conexion.prepareStatement(query);

            // -----------------------------------------------------------------
            // PROCESAMIENTO INTEGRADO (Unión de piezas del Backend)
            // -----------------------------------------------------------------
            int empleadoId = 101;
            
            // ⏱️ Pieza 1: Calculamos las horas del turno nocturno usando ControlHoras (10 PM a 6 AM = 8h)
            double horas = ControlHoras.calcularHorasTrabajadas(LocalTime.of(22, 0), LocalTime.of(6, 0)); 
            
            // 💵 Pieza 2: Procesamos el salario neto (descuentos de ley AFP/ISSS + Redondeo Bancario)
            double salarioNetoFinal = ProcesadorNomina.obtenerSalarioNeto(horas, 5.0);

            // Preparamos los parámetros para el INSERT
            comando.setInt(1, empleadoId);
            comando.setDouble(2, salarioNetoFinal);
            
            // Ejecuta el comando en la memoria caché temporal de MariaDB
            comando.executeUpdate(); 

            // =================================================================
            // 🛡️ CONSOLIDACIÓN ACID (COMMIT)
            // Si el cálculo de horas y nómina fue correcto, guardamos permanentemente.
            // =================================================================
            conexion.commit(); 
            System.out.println("¡Transacción Exitosa! Planilla procesada y guardada firmemente en XAMPP.");

        } catch (Exception e) {
            // =================================================================
            // 🚨 SALVAVIDAS CRÍTICO (ROLLBACK)
            // Si la base de datos se cae, el ID no existe, o hay un error matemático,
            // cancelamos todo el lote para no generar descuadres contables.
            // =================================================================
            System.err.println("¡ALERTA! Hubo un error en el proceso de planilla: " + e.getMessage());
            System.err.println("Aplicando Rollback de seguridad en XAMPP. Ningún dato fue alterado.");
            
            if (conexion != null) {
                try {
                    conexion.rollback(); // Deshace los inserts temporales en MariaDB
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            // Cerramos los canales de comunicación limpiamente para evitar fugas de memoria en XAMPP
            try {
                if (comando != null) comando.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Método principal para ejecutar y probar la integración en la consola
    public static void main(String[] args) {
        ServicioPlanilla servicio = new ServicioPlanilla();
        servicio.procesarPlanillaGeneral();
    }
}