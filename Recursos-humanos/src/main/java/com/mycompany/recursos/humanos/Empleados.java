package com.mycompany.recursos.humanos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Pantalla de Lista de Empleados.
 * Muestra el catálogo principal y permite acceder a los expedientes.
 */
public class Empleados extends JFrame {

    private final Color azulNavy = new Color(10, 25, 47);
    private final Color azulRoyal = new Color(13, 110, 253);
    private final Color blancoFondo = new Color(248, 249, 250);
    private final Color verdeExito = new Color(25, 135, 84); // 🟢 Nuevo color para Editar
    
    private JTable tablaEmpleados;
    private DefaultTableModel modeloTabla;

    public Empleados() {
        super("Catálogo de Empleados");
        initUI();
    }

    private void initUI() {
        // DISPOSE_ON_CLOSE evita que al cerrar esta ventana se cierre todo el sistema
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setSize(1000, 600);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(blancoFondo);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // ==========================================
        // 1. ENCABEZADO Y BOTONES SUPERIORES
        // ==========================================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel lblTitulo = new JLabel("Lista de Empleados");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(azulNavy);
        headerPanel.add(lblTitulo, BorderLayout.WEST);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);

        JButton btnNuevo = crearBoton("Nuevo Empleado", azulRoyal);
        JButton btnEditar = crearBoton("Editar Empleado", verdeExito); // 🟢 Nuevo Botón
        JButton btnVer = crearBoton("Ver Expediente", azulNavy);

        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar); // 🟢 Agregado al panel
        panelBotones.add(btnVer);
        headerPanel.add(panelBotones, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. TABLA DE EMPLEADOS
        // ==========================================
        String[] columnas = {"ID", "DUI / DNI", "Nombre Completo", "Puesto / Cargo", "Salario Base", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Evita edición directa en celdas
            }
        };

        tablaEmpleados = new JTable(modeloTabla);
        tablaEmpleados.setRowHeight(35);
        tablaEmpleados.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaEmpleados.setSelectionBackground(new Color(220, 235, 255));
        tablaEmpleados.setSelectionForeground(azulNavy);
        
        JTableHeader header = tablaEmpleados.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(azulNavy);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 40));

        JScrollPane scrollPane = new JScrollPane(tablaEmpleados);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // ==========================================
        // 3. EVENTOS (Doble Clic y Botones)
        // ==========================================
        
        tablaEmpleados.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2 && tablaEmpleados.getSelectedRow() != -1) {
                    abrirExpedienteSeleccionado();
                }
            }
        });

        btnVer.addActionListener(e -> {
            if (tablaEmpleados.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un empleado de la lista primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            } else {
                abrirExpedienteSeleccionado();
            }
        });

        // 🟢 Evento del Botón Editar
        btnEditar.addActionListener(e -> {
            if (tablaEmpleados.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un empleado de la tabla para editar su información.", "Aviso", JOptionPane.WARNING_MESSAGE);
            } else {
                abrirExpedienteSeleccionado();
            }
        });

        btnNuevo.addActionListener(e -> {
            ExpedienteForm vistaExpediente = new ExpedienteForm();
            ExpedienteController controlador = new ExpedienteController(vistaExpediente);
            vistaExpediente.setVisible(true);
        });

        setContentPane(mainPanel);
        cargarDatosDesdeBaseDeDatos();
    }

    private JButton crearBoton(String texto, Color colorFondo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(colorFondo);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 35));
        return btn;
    }

    // =========================================================================
    // CONEXIÓN MEDIANTE MVC PASANDO EL ID SELECCIONADO
    // =========================================================================
    private void abrirExpedienteSeleccionado() {
        int fila = tablaEmpleados.getSelectedRow();
        if (fila == -1) return; 
        
        // Capturamos el ID real de la fila de la tabla
        String idEmpleado = modeloTabla.getValueAt(fila, 0).toString();
        
        // 1. Instanciamos la Vista del Expediente
        ExpedienteForm vistaForm = new ExpedienteForm();
        
        // 2. Instanciamos el Controlador asignándole su vista
        ExpedienteController controlador = new ExpedienteController(vistaForm);
        
        // 3. Le ordenamos al controlador buscar este ID en la BD y llenar los campos
        controlador.cargarDatosEmpleado(idEmpleado);
        
        // 4. Hacemos visible la ventana ya procesada y rellena
        vistaForm.setVisible(true);
    }

    private void cargarDatosDesdeBaseDeDatos() {
        modeloTabla.setRowCount(0);
        String sql = "SELECT id_empleado, dni, nombre_completo, puesto, salario, estado FROM empleados";

        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id_empleado");
                String dni = rs.getString("dni"); 
                String nombre = rs.getString("nombre_completo");
                String puesto = rs.getString("puesto");
                double salario = rs.getDouble("salario"); 
                String estadoStr = rs.getString("estado"); 

                modeloTabla.addRow(new Object[]{
                    id, 
                    dni, 
                    nombre, 
                    (puesto == null || puesto.isEmpty()) ? "No Asignado" : puesto, 
                    "$" + String.format(java.util.Locale.US, "%,.2f", salario), 
                    estadoStr
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar la lista de empleados:\n" + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Empleados().setVisible(true);
        });
    }
}