package com.mycompany.recursos.humanos;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpedienteController {

    private ExpedienteForm vista;

    public ExpedienteController(ExpedienteForm vista) {
        this.vista = vista;
        inicializarEventos();
    }

    private void inicializarEventos() {
        // Evento para guardar un registro NUEVO
        this.vista.getBtnGuardar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarEnBaseDeDatos();
            }
        });

        // 🟢 NUEVO: Evento para ACTUALIZAR un registro existente
        this.vista.getBtnEditar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarEnBaseDeDatos();
            }
        });
    }

    // =========================================================================
    // MÉTODO: BUSCAR EL EMPLEADO POR ID Y MANDAR SUS DATOS A LA VISTA
    // =========================================================================
    public void cargarDatosEmpleado(String idEmpleado) {
        String sql = "SELECT * FROM empleados WHERE id_empleado = ?";
        
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, idEmpleado);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // 🟢 Guardamos el ID en la propiedad oculta de la vista
                    vista.setIdEmpleadoOculto(idEmpleado);

                    // 🟢 Intercambiamos visibilidad de botones: Ocultamos Guardar, Mostramos Actualizar
                    vista.getBtnGuardar().setVisible(false);
                    vista.getBtnEditar().setVisible(true);

                    // Rellenar los campos de la vista directamente
                    vista.getTxtDni().setText(rs.getString("dni"));
                    vista.getTxtNombreCompleto().setText(rs.getString("nombre_completo"));
                    vista.getTxtCorreo().setText(rs.getString("correo"));
                    vista.getTxtTelefono().setText(rs.getString("telefono"));
                    vista.getTxtDireccion().setText(rs.getString("direccion"));
                    vista.getComboGenero().setSelectedItem(rs.getString("genero"));
                    
                    vista.getComboDepartamento().setSelectedItem(rs.getString("departamento"));
                    vista.getTxtPuesto().setText(rs.getString("puesto"));
                    vista.getComboHorario().setSelectedItem(rs.getString("horario_laboral"));
                    vista.getTxtSalario().setText(String.valueOf(rs.getDouble("salario")));
                    vista.getComboEstado().setSelectedItem(rs.getString("estado"));

                    // Conversión de fechas de texto SQL (yyyy-MM-dd) a objetos Date
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    
                    String fNac = rs.getString("fecha_nacimiento");
                    if (fNac != null) {
                        Date dateNac = sdf.parse(fNac);
                        vista.getSpinFechaNacimiento().setValue(dateNac);
                    }
                    
                    String fIng = rs.getString("fecha_ingreso");
                    if (fIng != null) {
                        Date dateIng = sdf.parse(fIng);
                        vista.getSpinFechaIngreso().setValue(dateIng);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, 
                "Error al cargar el expediente desde el Controlador:\n" + ex.getMessage(), 
                "Error de Carga", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // MÉTODO: GUARDAR REGISTRO NUEVO (INSERT)
    // =========================================================================
    private void guardarEnBaseDeDatos() {
        // 1. Extraemos TODOS los datos de la Vista
        String dni = vista.getTxtDni().getText().trim();
        String nombre = vista.getTxtNombreCompleto().getText().trim();
        String correo = vista.getTxtCorreo().getText().trim();
        String telefono = vista.getTxtTelefono().getText().trim();
        String direccion = vista.getTxtDireccion().getText().trim(); 
        String genero = (String) vista.getComboGenero().getSelectedItem();
        
        String departamento = (String) vista.getComboDepartamento().getSelectedItem();
        String puesto = vista.getTxtPuesto().getText().trim();
        String horario = (String) vista.getComboHorario().getSelectedItem(); 
        String salarioStr = vista.getTxtSalario().getText().trim();
        String estado = (String) vista.getComboEstado().getSelectedItem();

        // Extraemos y formateamos fechas
        Date fechaIngreso = (Date) vista.getSpinFechaIngreso().getValue();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaIngresoFormat = sdf.format(fechaIngreso);

        Date fechaNacimiento = (Date) vista.getSpinFechaNacimiento().getValue();
        String fechaNacimientoFormat = sdf.format(fechaNacimiento);

        // 2. Validación básica
        if (dni.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "El DNI y el Nombre Completo son obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double salario = 0.0;
        try {
            if (!salarioStr.isEmpty()) {
                salario = Double.parseDouble(salarioStr);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "El salario debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Consulta SQL de Inserción
        String sql = "INSERT INTO empleados (dni, nombre_completo, fecha_nacimiento, correo, telefono, direccion, genero, departamento, puesto, horario_laboral, salario, estado, fecha_ingreso) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Conexion.obtenerConexion(); 
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, dni);
            stmt.setString(2, nombre);
            stmt.setString(3, fechaNacimientoFormat);
            stmt.setString(4, correo);
            stmt.setString(5, telefono);
            stmt.setString(6, direccion);
            stmt.setString(7, genero);
            stmt.setString(8, departamento);
            stmt.setString(9, puesto);
            stmt.setString(10, horario);
            stmt.setDouble(11, salario);
            stmt.setString(12, estado);
            stmt.setString(13, fechaIngresoFormat);

            stmt.executeUpdate();
            conn.commit(); 

            JOptionPane.showMessageDialog(vista, "¡Expediente COMPLETO guardado exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            vista.dispose(); // Opcional: Cerrar ventana al terminar
            
        } catch (SQLException ex) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException rollbackEx) { }
            }
            JOptionPane.showMessageDialog(vista, "Error al guardar en la base de datos:\n" + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException closeEx) { }
        }
    }

    // =========================================================================
    // 🟢 NUEVO MÉTODO: MODIFICAR REGISTRO EXISTENTE (UPDATE)
    // =========================================================================
    private void actualizarEnBaseDeDatos() {
        // 1. Verificar que tengamos un ID válido cargado en la vista
        String idEmpleado = vista.getIdEmpleadoOculto();
        if (idEmpleado == null || idEmpleado.trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "No se puede actualizar: Falta el ID del empleado.", "Error interno", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Extraemos los datos modificados de los controles visuales
        String dni = vista.getTxtDni().getText().trim();
        String nombre = vista.getTxtNombreCompleto().getText().trim();
        String correo = vista.getTxtCorreo().getText().trim();
        String telefono = vista.getTxtTelefono().getText().trim();
        String direccion = vista.getTxtDireccion().getText().trim(); 
        String genero = (String) vista.getComboGenero().getSelectedItem();
        
        String departamento = (String) vista.getComboDepartamento().getSelectedItem();
        String puesto = vista.getTxtPuesto().getText().trim();
        String horario = (String) vista.getComboHorario().getSelectedItem(); 
        String salarioStr = vista.getTxtSalario().getText().trim();
        String estado = (String) vista.getComboEstado().getSelectedItem();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaIngresoFormat = sdf.format((Date) vista.getSpinFechaIngreso().getValue());
        String fechaNacimientoFormat = sdf.format((Date) vista.getSpinFechaNacimiento().getValue());

        // 3. Validaciones de datos obligatorios
        if (dni.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "El DNI y el Nombre Completo son obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double salario = 0.0;
        try {
            if (!salarioStr.isEmpty()) {
                salario = Double.parseDouble(salarioStr);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "El salario debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4. Consulta SQL de Actualización basada en el ID
        String sql = "UPDATE empleados SET dni = ?, nombre_completo = ?, fecha_nacimiento = ?, correo = ?, "
                   + "telefono = ?, direccion = ?, genero = ?, departamento = ?, puesto = ?, horario_laboral = ?, "
                   + "salario = ?, estado = ?, fecha_ingreso = ? WHERE id_empleado = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Conexion.obtenerConexion();
            stmt = conn.prepareStatement(sql);

            // Inyectamos los 13 datos actualizables
            stmt.setString(1, dni);
            stmt.setString(2, nombre);
            stmt.setString(3, fechaNacimientoFormat);
            stmt.setString(4, correo);
            stmt.setString(5, telefono);
            stmt.setString(6, direccion);
            stmt.setString(7, genero);
            stmt.setString(8, departamento);
            stmt.setString(9, puesto);
            stmt.setString(10, horario);
            stmt.setDouble(11, salario);
            stmt.setString(12, estado);
            stmt.setString(13, fechaIngresoFormat);
            
            // Filtro WHERE del registro
            stmt.setString(14, idEmpleado);

            int filasAfectadas = stmt.executeUpdate();
            conn.commit(); // Confirmamos la transacción atómica

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(vista, "¡El expediente ha sido actualizado correctamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                vista.dispose(); // Cierra el formulario tras guardar cambios con éxito
            } else {
                JOptionPane.showMessageDialog(vista, "No se encontró ningún empleado con el ID especificado.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException ex) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException rollbackEx) { }
            }
            JOptionPane.showMessageDialog(vista, "Error al actualizar la base de datos:\n" + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException closeEx) { }
        }
    }
}