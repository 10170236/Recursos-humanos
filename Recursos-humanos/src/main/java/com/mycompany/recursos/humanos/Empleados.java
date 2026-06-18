package com.mycompany.recursos.humanos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Pantalla de Lista de Empleados.
 * Muestra el catálogo principal y permite acceder a los expedientes.
 */
public class Empleados extends JFrame {

    private final Color azulNavy = new Color(10, 25, 47);
    private final Color azulRoyal = new Color(13, 110, 253);
    private final Color blancoFondo = new Color(248, 249, 250);
    
    private JTable tablaEmpleados;
    private DefaultTableModel modeloTabla;

    public Empleados() {
        super("Catálogo de Empleados");
        initUI();
    }

    private void initUI() {
        // DISPOSE_ON_CLOSE es vital aquí para que al cerrar esta ventana no se cierre todo el sistema
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setSize(900, 600);
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
        JButton btnVer = crearBoton("Ver Expediente", azulNavy);

        panelBotones.add(btnNuevo);
        panelBotones.add(btnVer);
        headerPanel.add(panelBotones, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. TABLA DE EMPLEADOS
        // ==========================================
        String[] columnas = {"ID", "DUI", "Nombre Completo", "Cargo", "Salario Base", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Evita que editen la tabla directamente dando doble clic en la celda
            }
        };

        tablaEmpleados = new JTable(modeloTabla);
        tablaEmpleados.setRowHeight(35);
        tablaEmpleados.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaEmpleados.setSelectionBackground(new Color(220, 235, 255));
        tablaEmpleados.setSelectionForeground(azulNavy);
        
        // Estilo del encabezado de la tabla
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
        
        // Lógica: Doble clic en una fila de la tabla
        tablaEmpleados.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2 && tablaEmpleados.getSelectedRow() != -1) {
                    abrirExpedienteSeleccionado();
                }
            }
        });

        // Lógica: Botón Ver Expediente
        btnVer.addActionListener(e -> {
            if (tablaEmpleados.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un empleado de la lista primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            } else {
                abrirExpedienteSeleccionado();
            }
        });

        // Lógica: Botón Nuevo Empleado
        btnNuevo.addActionListener(e -> {
            // Abre el formulario en blanco
            ExpedienteForm nuevoForm = new ExpedienteForm();
            nuevoForm.setVisible(true);
        });

        setContentPane(mainPanel);
        
        // Cargar algunos datos de prueba simulados
        cargarDatosDePrueba();
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

    private void abrirExpedienteSeleccionado() {
        int fila = tablaEmpleados.getSelectedRow();
        String nombre = modeloTabla.getValueAt(fila, 2).toString();
        
        // Aquí conectamos con tu ExpedienteForm
        // Nota: Por ahora solo abrimos el formulario. Más adelante le pasaremos los datos para que se llenen solos.
        JOptionPane.showMessageDialog(this, "Abriendo expediente de: " + nombre);
        
        ExpedienteForm form = new ExpedienteForm();
        form.setVisible(true);
    }

    private void cargarDatosDePrueba() {
        modeloTabla.addRow(new Object[]{"1", "01234567-8", "Carlos Martínez", "Desarrollador Java", "$1,200.00", "Activo"});
        modeloTabla.addRow(new Object[]{"2", "08765432-1", "Ana Gómez", "Analista RRHH", "$850.00", "Activo"});
        modeloTabla.addRow(new Object[]{"3", "05554443-2", "Luis Pineda", "Gerente IT", "$2,500.00", "Activo"});
    }

    // Método main solo para probar esta pantalla individualmente si lo necesitas
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Empleados().setVisible(true);
        });
    }
}