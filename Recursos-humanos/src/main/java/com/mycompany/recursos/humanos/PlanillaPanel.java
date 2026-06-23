package com.mycompany.recursos.humanos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

/**
 * Módulo Visual de Planillas.
 * Conecta la interfaz gráfica con el motor transaccional de cálculo de nómina.
 */
public class PlanillaPanel extends JPanel {

    private final Color azulNavy = new Color(10, 25, 47);
    private final Color azulRoyal = new Color(13, 110, 253);
    private final Color verdeExito = new Color(25, 135, 84);

    private JTable tablaPagos;
    private DefaultTableModel modeloTabla;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JTextField txtFechaPago;

    public PlanillaPanel() {
        initUI();
        cargarPagosRecientes();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // ==========================================
        // 1. ENCABEZADO Y CONTROLES
        // ==========================================
        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setOpaque(false);

        JLabel lblTitulo = new JLabel("Gestión y Cierre de Planillas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(azulNavy);
        topPanel.add(lblTitulo, BorderLayout.NORTH);

        JPanel controlesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        controlesPanel.setOpaque(false);

        // Fechas por defecto (Mes actual)
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        
        controlesPanel.add(new JLabel("Fecha Inicio (YYYY-MM-DD):"));
        txtFechaInicio = new JTextField(inicioMes.toString(), 10);
        controlesPanel.add(txtFechaInicio);

        controlesPanel.add(new JLabel("Fecha Fin:"));
        txtFechaFin = new JTextField(hoy.toString(), 10);
        controlesPanel.add(txtFechaFin);

        controlesPanel.add(new JLabel("Fecha de Pago:"));
        txtFechaPago = new JTextField(hoy.toString(), 10);
        controlesPanel.add(txtFechaPago);

        JButton btnProcesar = new JButton("Procesar Nómina");
        btnProcesar.setBackground(azulRoyal);
        btnProcesar.setForeground(Color.WHITE);
        btnProcesar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnProcesar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnProcesar.addActionListener(e -> ejecutarCierre());
        controlesPanel.add(btnProcesar);

        topPanel.add(controlesPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. TABLA DE RESULTADOS
        // ==========================================
        String[] columnas = {"ID Pago", "ID Empleado", "Fecha Pago", "Total Ingresos", "Total Deducciones", "Salario Neto"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        
        tablaPagos = new JTable(modeloTabla);
        tablaPagos.setRowHeight(30);
        tablaPagos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaPagos.getTableHeader().setBackground(azulNavy);
        tablaPagos.getTableHeader().setForeground(Color.WHITE);
        tablaPagos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollTabla = new JScrollPane(tablaPagos);
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        add(scrollTabla, BorderLayout.CENTER);

        // ==========================================
        // 3. PANEL INFERIOR (Botón PDF y Correo)
        // ==========================================
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        
        JButton btnGenerarPDF = new JButton("Generar Recibo y Enviar Correo");
        btnGenerarPDF.setBackground(verdeExito);
        btnGenerarPDF.setForeground(Color.WHITE);
        btnGenerarPDF.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGenerarPDF.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // 🔌 EVENTO CONECTADO A GENERADOR PDF Y ENVIADOR CORREO
        btnGenerarPDF.addActionListener(e -> {
            int filaSeleccionada = tablaPagos.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un registro de la tabla para emitir la boleta.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 1. Obtener el ID del pago desde la columna 0
            int idPago = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
            
            // 2. Cambiar cursor a modo de espera y deshabilitar botón
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            btnGenerarPDF.setEnabled(false);

            // 3. Ejecutar en un hilo de fondo para no congelar la interfaz
            new Thread(() -> {
                String sql = "SELECT e.nombre_completo, e.correo, p.salario_neto " +
                             "FROM pagos_nomina p " +
                             "INNER JOIN empleados e ON p.id_empleado = e.id_empleado " +
                             "WHERE p.id_pago = ?";
                             
                try (Connection con = Conexion.obtenerConexion();
                     PreparedStatement ps = con.prepareStatement(sql)) {
                    
                    ps.setInt(1, idPago);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String nombreEmpleado = rs.getString("nombre_completo");
                            String correoEmpleado = rs.getString("correo");
                            double salarioNeto = rs.getBigDecimal("salario_neto").doubleValue();
                            
                            // Validar si el empleado tiene correo
                            if (correoEmpleado == null || correoEmpleado.trim().isEmpty()) {
                                SwingUtilities.invokeLater(() -> {
                                    JOptionPane.showMessageDialog(this, "El colaborador " + nombreEmpleado + " no tiene un correo electrónico registrado.", "Error de Envío", JOptionPane.ERROR_MESSAGE);
                                });
                                return;
                            }

                            // 4. Instanciar y ejecutar tus componentes
                            GeneradorPDF generador = new GeneradorPDF();
                            String rutaPdfGenerado = generador.crearBoleta(nombreEmpleado, salarioNeto);
                            
                            EnviadorCorreo correo = new EnviadorCorreo();
                            correo.enviar(correoEmpleado, nombreEmpleado, rutaPdfGenerado);
                            
                            // 5. Notificar éxito en el hilo principal
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(this, "✅ Boleta generada y enviada con éxito a:\n" + correoEmpleado, "Proceso Completado", JOptionPane.INFORMATION_MESSAGE);
                            });
                            
                        } else {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(this, "No se encontraron datos para el ID de pago seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
                            });
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Error en el flujo de PDF/Correo: " + ex.getMessage());
                    ex.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Ocurrió un error al procesar el envío. Revisa la consola.", "Error Crítico", JOptionPane.ERROR_MESSAGE);
                    });
                } finally {
                    // 6. Restablecer siempre el estado
                    SwingUtilities.invokeLater(() -> {
                        setCursor(Cursor.getDefaultCursor());
                        btnGenerarPDF.setEnabled(true);
                    });
                }
            }).start();
        });
        
        bottomPanel.add(btnGenerarPDF);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void ejecutarCierre() {
        try {
            // Convertimos los textos a fechas SQL
            Date fechaInicio = Date.valueOf(txtFechaInicio.getText());
            Date fechaFin = Date.valueOf(txtFechaFin.getText());
            Date fechaPago = Date.valueOf(txtFechaPago.getText());

            // ⚠️ AQUÍ CONECTAMOS LA VISTA CON EL CORAZÓN DEL SISTEMA
            boolean exito = ServicioPlanilla.procesarCierreNominaMasivo(fechaInicio, fechaFin, fechaPago);

            if (exito) {
                JOptionPane.showMessageDialog(this, "Nómina procesada exitosamente.", "Operación Completa", JOptionPane.INFORMATION_MESSAGE);
                cargarPagosRecientes(); // Actualizamos la tabla
            } else {
                JOptionPane.showMessageDialog(this, "Ocurrió un error en el procesamiento. Se revirtieron los cambios.", "Error Transaccional", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use YYYY-MM-DD.", "Error de Formato", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cargarPagosRecientes() {
        modeloTabla.setRowCount(0);
        String sql = "SELECT id_pago, id_empleado, fecha_pago, total_ingresos, total_deducciones, salario_neto FROM pagos_nomina ORDER BY id_pago DESC LIMIT 50";
        
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("id_pago"),
                    rs.getInt("id_empleado"),
                    rs.getDate("fecha_pago"),
                    "$" + rs.getBigDecimal("total_ingresos"),
                    "$" + rs.getBigDecimal("total_deducciones"),
                    "$" + rs.getBigDecimal("salario_neto")
                });
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la tabla de pagos: " + e.getMessage());
        }
    }
}