/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.recursos.humanos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Motor de Conexión del Sistema de Recursos Humanos.
 * Configurado para XAMPP (MariaDB/MySQL) y diseñado bajo arquitectura ACID.
 */
public class Conexion {

    // 1. URL de conexión local apuntando a la base de datos 'recursos_humanos'
    // Incluye parámetros modernos para evitar errores de zona horaria y permitir codificación UTF-8
    private static final String URL = "jdbc:mysql://localhost:3306/recursos_humanos"
            + "?serverTimezone=UTC"
            + "&useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&useUnicode=true"
            + "&characterEncoding=UTF-8";
    
    // 2. Credenciales por defecto de XAMPP
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Vacío por defecto en instalaciones de XAMPP

    /**
     * Establece y retorna una conexión activa con MariaDB.
     * Desactiva el AutoCommit de manera estricta para que el Backend controle las transacciones (ACID).
     * * @return Connection objeto de conexión listo para usar.
     * @throws SQLException si las credenciales fallan, la BD está apagada o falta el Driver.
     */
    public static Connection obtenerConexion() throws SQLException {
        try {
            // Carga explícita del Driver moderno de MySQL en memoria (evita fallos en aplicaciones Swing de escritorio)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Solicitar la conexión al gestor de controladores
            Connection conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            
            // 🛡️ PROPIEDAD ACID CRÍTICA: Desactivamos el guardado automático (AutoCommit = false)
            // Esto significa que si insertas un empleado y a la mitad se corta la luz o hay un error de código,
            // nada se guardará a medias en XAMPP hasta que tú llames explícitamente a .commit()
            conexion.setAutoCommit(false);
            
            return conexion;
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("❌ ERROR CRÍTICO backend: No se encontró el 'MySQL JDBC Driver' en las librerías del proyecto. "
                    + "Asegúrate de haberlo añadido en el pom.xml o en la carpeta Libraries.", e);
        }
    }
}
