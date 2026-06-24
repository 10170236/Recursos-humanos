package com.mycompany.recursos.humanos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Panel del Reloj Marcador integrado al diseño corporativo.
 * Respeta la paleta de colores (Navy, Royal, Blanco Fondo) y la estructura de la BD.
 */
public class RelojMarcadorPanel extends JPanel {

    // Paleta de colores oficial de tus capturas
    private final Color azulNavy = new Color(10, 25, 47);
    private final Color azulRoyal = new Color(13, 110, 253);
    private final Color blancoFondo = new Color(248, 249, 250);
    private final Color grisTexto = new Color(108, 117, 125);
    private final Color verdeExito = new Color(25, 135, 84);

    private JTable tablaEmpleados;
    private DefaultTableModel modeloTabla;
    private JLabel lblRelojDigital;
    private JButton btnMarcarEntrada;
    private JButton btnMarcarSalida;

    public RelojMarcadorPanel() {
        initUI();
        cargarEmpleadosActivos();
        iniciarReloj();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(blancoFondo);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Cuerpo Dividido (Tabla Izquierda, Marcador Derecha) ---
        JPanel cuerpoPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        cuerpoPanel.setOpaque(false);

        // 1. Contenedor Tabla (Panel Izquierdo)
        JPanel panelIzquierdo = new JPanel(new BorderLayout(0, 15));
        panelIzquierdo.setBackground(Color.WHITE);
        panelIzquierdo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel lblSeccion1 = new JLabel("Seleccione el Empleado");
        lblSeccion1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSeccion1.setForeground(azulNavy);
        panelIzquierdo.add(lblSeccion1, BorderLayout.NORTH);

        // Columnas usando la nomenclatura exacta observada en tu lista ("DUI / DNI")
        String[] columnas = {"ID", "DUI / DNI", "Nombre Completo", "Puesto / Cargo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaEmpleados = new JTable(modeloTabla);
        tablaEmpleados.setRowHeight(35);
        tablaEmpleados.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaEmpleados.setShowVerticalLines(false);
        tablaEmpleados.setGridColor(blancoFondo);
        tablaEmpleados.setSelectionBackground(new Color(232, 240, 254)); // Azul tenue al seleccionar
        tablaEmpleados.setSelectionForeground(azulNavy);
        
        // Estilizar encabezado de la tabla para hacer match con tu diseño
        tablaEmpleados.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaEmpleados.getTableHeader().setBackground(azulNavy);
        tablaEmpleados.getTableHeader().setForeground(Color.WHITE);
        tablaEmpleados.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollTabla = new JScrollPane(tablaEmpleados);
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        panelIzquierdo.add(scrollTabla, BorderLayout.CENTER);
        cuerpoPanel.add(panelIzquierdo);

        // 2. Contenedor de Marcaje Dinámico (Panel Derecho)
        JPanel panelDerecho = new JPanel(new GridBagLayout());
        panelDerecho.setBackground(Color.WHITE);
        panelDerecho.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 15, 12, 15);

        JLabel lblSeccion2 = new JLabel("Control de Asistencia", SwingConstants.CENTER);
        lblSeccion2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSeccion2.setForeground(azulNavy);
        panelDerecho.add(lblSeccion2, gbc);

        lblRelojDigital = new JLabel("--:--:--", SwingConstants.CENTER);
        lblRelojDigital.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblRelojDigital.setForeground(azulRoyal); // Color accent idéntico a tus tarjetas métricas
        panelDerecho.add(lblRelojDigital, gbc);

        // Botón Entrada (Estilo Verde/Go de tu tarjeta de Asistencias Hoy)
        btnMarcarEntrada = new JButton("Registrar Entrada");
        btnMarcarEntrada.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnMarcarEntrada.setBackground(verdeExito);
        btnMarcarEntrada.setForeground(Color.WHITE);
        btnMarcarEntrada.setFocusPainted(false);
        btnMarcarEntrada.setPreferredSize(new Dimension(0, 45));
        btnMarcarEntrada.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMarcarEntrada.addActionListener(e -> registrarMarca(true));
        panelDerecho.add(btnMarcarEntrada, gbc);

        // Botón Salida (Estilo Navy Corporativo)
        btnMarcarSalida = new JButton("Registrar Salida");
        btnMarcarSalida.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnMarcarSalida.setBackground(azulNavy);
        btnMarcarSalida.setForeground(Color.WHITE);
        btnMarcarSalida.setFocusPainted(false);
        btnMarcarSalida.setPreferredSize(new Dimension(0, 45));
        btnMarcarSalida.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMarcarSalida.addActionListener(e -> registrarMarca(false));
        panelDerecho.add(btnMarcarSalida, gbc);

        cuerpoPanel.add(panelDerecho);
        add(cuerpoPanel, BorderLayout.CENTER);
    }

    private void cargarEmpleadosActivos() {
        modeloTabla.setRowCount(0);
        // Ajustado exactamente a las columnas de tu phpMyAdmin: 'id_empleado', 'dni', 'nombre_completo', 'puesto'
        String sql = "SELECT id_empleado, dni, nombre_completo, puesto FROM empleados WHERE estado = '1' OR estado = 'Activo'";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                        rs.getInt("id_empleado"),
                        rs.getString("dni"),
                        rs.getString("nombre_completo"),
                        rs.getString("puesto") == null ? "No Asignado" : rs.getString("puesto")
                });
            }
        } catch (Exception e) {
            System.err.println("🚨 Error al poblar tabla de marcación: " + e.getMessage());
        }
    }

    private void iniciarReloj() {
        Timer timer = new Timer(1000, e -> {
            LocalDateTime ahora = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a", Locale.ENGLISH);
            lblRelojDigital.setText(ahora.format(formatter));
        });
        timer.start();
    }

   private void registrarMarca(boolean esEntrada) {
        int filaSeleccionada = tablaEmpleados.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un empleado de la lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idEmpleado = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String nombre = (String) modeloTabla.getValueAt(filaSeleccionada, 2);
        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();

        try (Connection con = Conexion.obtenerConexion()) {
            try {
                if (esEntrada) {
                    String sqlInsert = "INSERT INTO marcaciones (id_empleado, fecha_hora_entrada, fecha_hora_salida) VALUES (?, ?, NULL)";
                    try (java.sql.PreparedStatement ps = con.prepareStatement(sqlInsert)) {
                        ps.setInt(1, idEmpleado);
                        ps.setTimestamp(2, java.sql.Timestamp.valueOf(ahora));
                        ps.executeUpdate();
                        
                        // 🛡️ ¡LA MAGIA OCURRE AQUÍ! Forzamos el guardado en XAMPP
                        con.commit(); 
                        
                        JOptionPane.showMessageDialog(this, "✅ Entrada registrada correctamente para:\n" + nombre, "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    String sqlUpdateUniversal = "UPDATE marcaciones SET fecha_hora_salida = ? WHERE id_marcacion = (SELECT MAX(id_marcacion) FROM (SELECT id_marcacion FROM marcaciones WHERE id_empleado = ? AND fecha_hora_salida IS NULL) AS temp)";
                    try (java.sql.PreparedStatement ps = con.prepareStatement(sqlUpdateUniversal)) {
                        ps.setTimestamp(1, java.sql.Timestamp.valueOf(ahora));
                        ps.setInt(2, idEmpleado);
                        int filasAfectadas = ps.executeUpdate();
                        
                        if (filasAfectadas > 0) {
                            // 🛡️ ¡LA MAGIA OCURRE AQUÍ! Forzamos el guardado en XAMPP
                            con.commit();
                            
                            JOptionPane.showMessageDialog(this, "✅ Salida registrada correctamente para:\n" + nombre, "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this, "⚠️ El empleado no posee marcas de Entrada pendientes por cerrar.", "Aviso del Sistema", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            } catch (java.sql.SQLException ex) {
                // Si algo falla, deshacemos cualquier cambio a medias para proteger la base de datos
                con.rollback(); 
                throw ex; // Lanzamos el error al catch principal
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error de persistencia: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
}