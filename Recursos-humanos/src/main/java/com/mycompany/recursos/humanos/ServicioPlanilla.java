package com.mycompany.recursos.humanos;

import com.mycompany.recursos.humanos.ProcesadorNomina;
import com.mycompany.recursos.humanos.ControlHoras;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalTime;

public class ServicioPlanilla {

    // Configuración para conectar a tu base de datos (SQL Server en este ejemplo)
    private String url = "jdbc:sqlserver://localhost:1433;databaseName=PlanillaDB;encrypt=false;";
    private String usuario = "sa";
    private String clave = "tu_password"; // Aquí irá la contraseña de tu SQL Server

    public void procesarPlanillaGeneral() {
        Connection conexion = null;
        PreparedStatement comando = null;

        try {
            // 1. Intentamos abrir la conexión con la base de datos
            conexion = DriverManager.getConnection(url, usuario, clave);
            
            // =================================================================
            // ¡PROTECCIÓN ACID INICIA AQUÍ!
            // Desactivamos el guardado automático. 
            // Java ahora mantendrá todo en "pausa" hasta que confirmemos.
            // =================================================================
            conexion.setAutoCommit(false); 

            String query = "INSERT INTO planilla (empleado_id, salario_final) VALUES (?, ?)";
            comando = conexion.prepareStatement(query);

            // -----------------------------------------------------------------
            // SIMULACIÓN DE PROCESAMIENTO (Aquí unimos tus Pasos 2 y 3)
            // -----------------------------------------------------------------
            int empleadoId = 101;
            
            // Usamos el Paso 2 (ControlHoras) para calcular el turno nocturno (10 PM a 6 AM)
            double horas = ControlHoras.calcularHorasTrabajadas(LocalTime.of(22, 0), LocalTime.of(6, 0)); 
            
            // Usamos el Paso 3 (ProcesadorNomina) para calcular el salario neto con descuentos y redondeo
            double salarioNetoFinal = ProcesadorNomina.obtenerSalarioNeto(horas, 5.0);

            // Preparamos los datos para guardarlos temporalmente en la base de datos
            comando.setInt(1, empleadoId);
            comando.setDouble(2, salarioNetoFinal);
            
            // Ejecuta el comando en la memoria temporal de la base de datos
            comando.executeUpdate(); 

            // =================================================================
            // SI TODO SALIÓ BIEN: Consolidamos los datos (COMMIT)
            // Aquí es donde realmente se escriben los cambios permanentemente
            // =================================================================
            conexion.commit(); 
            System.out.println("¡Transacción Exitosa! Planilla guardada firmemente de forma masiva.");

        } catch (Exception e) {
            // =================================================================
            // EL SALVAVIDAS EN CASO DE ERROR (ROLLBACK)
            // Si algo falla, deshacemos absolutamente todo lo que estaba en memoria
            // =================================================================
            System.err.println("¡ALERTA! Hubo un error en el proceso: " + e.getMessage());
            System.err.println("Aplicando Rollback de seguridad. Ningún dato fue alterado.");
            
            if (conexion != null) {
                try {
                    conexion.rollback(); // Todo vuelve a la normalidad, base de datos limpia
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            // Cerramos los canales de comunicación de forma limpia siempre
            try {
                if (comando != null) comando.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Método principal para mandar a llamar la simulación
    public static void main(String[] args) {
        ServicioPlanilla servicio = new ServicioPlanilla();
        servicio.procesarPlanillaGeneral();
    }
}