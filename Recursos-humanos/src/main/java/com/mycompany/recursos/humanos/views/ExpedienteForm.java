package com.mycompany.recursos.humanos.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Date;

/**
 * Vista del Formulario de Expedientes de Empleados.
 * Capa exclusivamente visual, sin lógica de base de datos ni negocio.
 */
public class ExpedienteForm extends JFrame {

    // Componentes Datos Personales
    private JTextField txtDni;
    private JTextField txtNombreCompleto;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JSpinner spinFechaNacimiento;
    private JComboBox<String> comboGenero;
    private JTextArea txtDireccion;

    // Componentes Datos Laborales
    private JTextField txtPuesto;
    private JComboBox<String> comboDepartamento;
    private JSpinner spinFechaIngreso;
    private JTextField txtSalario;
    private JComboBox<String> comboHorario;
    private JComboBox<String> comboEstado;

    // Componentes Documentación
    private JTable tablaDocumentos;
    private DefaultTableModel modeloTablaDocs;
    private JButton btnAdjuntarDoc;
    private JButton btnEliminarDoc;

    // Botones de acción principales
    private JButton btnGuardar;
    private JButton btnLimpiar;
    private JButton btnCancelar;

    public ExpedienteForm() {
        super("Gestión de Recursos Humanos - Expediente de Empleado");
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 650);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(750, 550));

        // Panel Principal con margen
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 247, 250)); // Fondo claro premium

        // --- Encabezado ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel lblTitulo = new JLabel("Expediente Digital de Empleado");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(33, 37, 41));
        
        JLabel lblSubtitulo = new JLabel("Gestione y actualice los datos del expediente laboral del personal.");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(108, 117, 125));

        headerPanel.add(lblTitulo, BorderLayout.NORTH);
        headerPanel.add(lblSubtitulo, BorderLayout.SOUTH);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- Panel de Pestañas ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // 1. Pestaña Datos Personales
        tabbedPane.addTab("Datos Personales", crearPanelDatosPersonales());
        
        // 2. Pestaña Datos Laborales
        tabbedPane.addTab("Datos Laborales", crearPanelDatosLaborales());
        
        // 3. Pestaña Documentación
        tabbedPane.addTab("Documentación", crearPanelDocumentacion());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // --- Panel de Acciones Inferior ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionPanel.setOpaque(false);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancelar.putClientProperty("JButton.buttonType", "roundRect");

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnLimpiar.putClientProperty("JButton.buttonType", "roundRect");

        btnGuardar = new JButton("Guardar Expediente");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setBackground(new Color(13, 110, 253)); // Azul primary moderno
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.putClientProperty("JButton.buttonType", "roundRect");
        // FlatLaf custom properties
        btnGuardar.putClientProperty("JComponent.minimumWidth", 150);

        actionPanel.add(btnCancelar);
        actionPanel.add(btnLimpiar);
        actionPanel.add(btnGuardar);

        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        // Asignar el panel principal
        setContentPane(mainPanel);

        // Validaciones visuales básicas (visual-only)
        configurarValidacionesVisuales();
        
        // Acción de limpiar visual
        btnLimpiar.addActionListener(e -> limpiarFormulario());
    }

    private JPanel crearPanelDatosPersonales() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Fila 0: DNI y Nombre Completo
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        panel.add(crearEtiquetaObligatoria("Cédula / DNI:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtDni = new JTextField();
        txtDni.putClientProperty("JTextField.placeholderText", "Ej. 1-2345-6789");
        txtDni.putClientProperty("JComponent.roundRect", true);
        panel.add(txtDni, gbc);

        gbc.gridx = 2; gbc.weightx = 0.3;
        panel.add(crearEtiquetaObligatoria("Nombre Completo:"), gbc);
        
        gbc.gridx = 3; gbc.weightx = 0.7;
        txtNombreCompleto = new JTextField();
        txtNombreCompleto.putClientProperty("JTextField.placeholderText", "Nombre y Apellidos");
        txtNombreCompleto.putClientProperty("JComponent.roundRect", true);
        panel.add(txtNombreCompleto, gbc);

        // Fila 1: Teléfono y Correo
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        panel.add(new JLabel("Teléfono:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtTelefono = new JTextField();
        txtTelefono.putClientProperty("JTextField.placeholderText", "Ej. 8888-8888");
        txtTelefono.putClientProperty("JComponent.roundRect", true);
        panel.add(txtTelefono, gbc);

        gbc.gridx = 2; gbc.weightx = 0.3;
        panel.add(crearEtiquetaObligatoria("Correo Electrónico:"), gbc);
        
        gbc.gridx = 3; gbc.weightx = 0.7;
        txtCorreo = new JTextField();
        txtCorreo.putClientProperty("JTextField.placeholderText", "ejemplo@correo.com");
        txtCorreo.putClientProperty("JComponent.roundRect", true);
        panel.add(txtCorreo, gbc);

        // Fila 2: Fecha Nacimiento y Género
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        panel.add(crearEtiquetaObligatoria("F. Nacimiento:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.7;
        spinFechaNacimiento = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor de = new JSpinner.DateEditor(spinFechaNacimiento, "dd/MM/yyyy");
        spinFechaNacimiento.setEditor(de);
        spinFechaNacimiento.putClientProperty("JComponent.roundRect", true);
        panel.add(spinFechaNacimiento, gbc);

        gbc.gridx = 2; gbc.weightx = 0.3;
        panel.add(new JLabel("Género:"), gbc);
        
        gbc.gridx = 3; gbc.weightx = 0.7;
        comboGenero = new JComboBox<>(new String[]{"Seleccionar...", "Masculino", "Femenino", "Otro"});
        comboGenero.putClientProperty("JComponent.roundRect", true);
        panel.add(comboGenero, gbc);

        // Fila 3: Dirección (Span completo)
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Dirección Domiciliar:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        txtDireccion = new JTextArea(3, 20);
        txtDireccion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDireccion.setLineWrap(true);
        txtDireccion.setWrapStyleWord(true);
        JScrollPane scrollDireccion = new JScrollPane(txtDireccion);
        scrollDireccion.putClientProperty("JComponent.roundRect", true);
        panel.add(scrollDireccion, gbc);

        return panel;
    }

    private JPanel crearPanelDatosLaborales() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Fila 0: Puesto y Departamento
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        panel.add(crearEtiquetaObligatoria("Puesto / Cargo:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtPuesto = new JTextField();
        txtPuesto.putClientProperty("JTextField.placeholderText", "Ej. Desarrollador Software");
        txtPuesto.putClientProperty("JComponent.roundRect", true);
        panel.add(txtPuesto, gbc);

        gbc.gridx = 2; gbc.weightx = 0.3;
        panel.add(crearEtiquetaObligatoria("Departamento:"), gbc);
        
        gbc.gridx = 3; gbc.weightx = 0.7;
        comboDepartamento = new JComboBox<>(new String[]{
            "Seleccionar...", "Recursos Humanos", "Tecnología de Información", "Finanzas", "Ventas", "Operaciones"
        });
        comboDepartamento.putClientProperty("JComponent.roundRect", true);
        panel.add(comboDepartamento, gbc);

        // Fila 1: Fecha Ingreso y Salario
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        panel.add(crearEtiquetaObligatoria("Fecha de Ingreso:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.7;
        spinFechaIngreso = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor de = new JSpinner.DateEditor(spinFechaIngreso, "dd/MM/yyyy");
        spinFechaIngreso.setEditor(de);
        spinFechaIngreso.putClientProperty("JComponent.roundRect", true);
        panel.add(spinFechaIngreso, gbc);

        gbc.gridx = 2; gbc.weightx = 0.3;
        panel.add(crearEtiquetaObligatoria("Salario Mensual:"), gbc);
        
        gbc.gridx = 3; gbc.weightx = 0.7;
        txtSalario = new JTextField();
        txtSalario.putClientProperty("JTextField.placeholderText", "Ej. 1500.00");
        txtSalario.putClientProperty("JComponent.roundRect", true);
        panel.add(txtSalario, gbc);

        // Fila 2: Horario y Estado
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        panel.add(new JLabel("Horario Laboral:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.7;
        comboHorario = new JComboBox<>(new String[]{"Matutino (8:00 AM - 5:00 PM)", "Vespertino (1:00 PM - 9:00 PM)", "Nocturno (9:00 PM - 6:00 AM)", "Mixto"});
        comboHorario.putClientProperty("JComponent.roundRect", true);
        panel.add(comboHorario, gbc);

        gbc.gridx = 2; gbc.weightx = 0.3;
        panel.add(crearEtiquetaObligatoria("Estado:"), gbc);
        
        gbc.gridx = 3; gbc.weightx = 0.7;
        comboEstado = new JComboBox<>(new String[]{"Activo", "Inactivo", "Licencia"});
        comboEstado.putClientProperty("JComponent.roundRect", true);
        panel.add(comboEstado, gbc);

        // Relleno inferior para alinear arriba
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private JPanel crearPanelDocumentacion() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Tabla de documentos
        String[] columnas = {"Nombre del Documento", "Tipo", "Fecha de Subida", "Estado"};
        modeloTablaDocs = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Agregar algunos datos ejemplo
        modeloTablaDocs.addRow(new Object[]{"Contrato_Trabajo_Firmado.pdf", "Contrato", "01/06/2026", "Verificado"});
        modeloTablaDocs.addRow(new Object[]{"Identificacion_Oficial.png", "Identificación", "01/06/2026", "Verificado"});

        tablaDocumentos = new JTable(modeloTablaDocs);
        tablaDocumentos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaDocumentos.setRowHeight(25);
        tablaDocumentos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JScrollPane scrollTabla = new JScrollPane(tablaDocumentos);
        panel.add(scrollTabla, BorderLayout.CENTER);

        // Panel de acciones de documentos
        JPanel panelBotonesDocs = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBotonesDocs.setOpaque(false);

        btnAdjuntarDoc = new JButton("Adjuntar Archivo...");
        btnAdjuntarDoc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnAdjuntarDoc.putClientProperty("JButton.buttonType", "roundRect");

        btnEliminarDoc = new JButton("Eliminar");
        btnEliminarDoc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnEliminarDoc.putClientProperty("JButton.buttonType", "roundRect");

        panelBotonesDocs.add(btnAdjuntarDoc);
        panelBotonesDocs.add(btnEliminarDoc);
        panel.add(panelBotonesDocs, BorderLayout.SOUTH);

        // Funcionalidad visual de los botones de documentos
        btnAdjuntarDoc.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                // Agregar fila ficticia visualmente
                String filename = fileChooser.getSelectedFile().getName();
                String ext = filename.contains(".") ? filename.substring(filename.lastIndexOf(".") + 1).toUpperCase() : "Desconocido";
                modeloTablaDocs.addRow(new Object[]{filename, ext, "Hoy", "Pendiente"});
            }
        });

        btnEliminarDoc.addActionListener(e -> {
            int selectedRow = tablaDocumentos.getSelectedRow();
            if (selectedRow >= 0) {
                modeloTablaDocs.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un documento para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        return panel;
    }

    private JLabel crearEtiquetaObligatoria(String texto) {
        JLabel label = new JLabel(texto);
        // Marcamos campos obligatorios visualmente
        label.setText("<html>" + texto + " <font color='red'>*</font></html>");
        return label;
    }

    private void configurarValidacionesVisuales() {
        // En FlatLaf podemos marcar con error usando txtField.putClientProperty("JComponent.outline", "error")
        // Escuchadores de foco para hacer validaciones visuales sin interferir en la lógica del sistema
        
        txtDni.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validarCampoVacio(txtDni);
            }
        });

        txtNombreCompleto.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validarCampoVacio(txtNombreCompleto);
            }
        });

        txtCorreo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (validarCampoVacio(txtCorreo)) {
                    // Si no está vacío, validar formato
                    String email = txtCorreo.getText().trim();
                    if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                        txtCorreo.putClientProperty("JComponent.outline", "error");
                        txtCorreo.setToolTipText("El formato de correo no es válido.");
                    } else {
                        txtCorreo.putClientProperty("JComponent.outline", null);
                        txtCorreo.setToolTipText(null);
                    }
                }
            }
        });

        txtPuesto.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validarCampoVacio(txtPuesto);
            }
        });

        txtSalario.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (validarCampoVacio(txtSalario)) {
                    try {
                        Double.parseDouble(txtSalario.getText().trim());
                        txtSalario.putClientProperty("JComponent.outline", null);
                        txtSalario.setToolTipText(null);
                    } catch (NumberFormatException ex) {
                        txtSalario.putClientProperty("JComponent.outline", "error");
                        txtSalario.setToolTipText("El salario debe ser un número decimal válido.");
                    }
                }
            }
        });
    }

    private boolean validarCampoVacio(JTextField campo) {
        if (campo.getText().trim().isEmpty()) {
            campo.putClientProperty("JComponent.outline", "error");
            campo.setToolTipText("Este campo es requerido.");
            return false;
        } else {
            campo.putClientProperty("JComponent.outline", null);
            campo.setToolTipText(null);
            return true;
        }
    }

    private void limpiarFormulario() {
        txtDni.setText("");
        txtNombreCompleto.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        spinFechaNacimiento.setValue(new Date());
        comboGenero.setSelectedIndex(0);
        txtDireccion.setText("");
        txtPuesto.setText("");
        comboDepartamento.setSelectedIndex(0);
        spinFechaIngreso.setValue(new Date());
        txtSalario.setText("");
        comboHorario.setSelectedIndex(0);
        comboEstado.setSelectedIndex(0);

        // Limpiar estilos de error
        txtDni.putClientProperty("JComponent.outline", null);
        txtNombreCompleto.putClientProperty("JComponent.outline", null);
        txtCorreo.putClientProperty("JComponent.outline", null);
        txtPuesto.putClientProperty("JComponent.outline", null);
        txtSalario.putClientProperty("JComponent.outline", null);
    }

    // --- Getters y Setters de los componentes para interacción con Controladores (Regla de Oro) ---
    
    public JTextField getTxtDni() { return txtDni; }
    public JTextField getTxtNombreCompleto() { return txtNombreCompleto; }
    public JTextField getTxtTelefono() { return txtTelefono; }
    public JTextField getTxtCorreo() { return txtCorreo; }
    public JSpinner getSpinFechaNacimiento() { return spinFechaNacimiento; }
    public JComboBox<String> getComboGenero() { return comboGenero; }
    public JTextArea getTxtDireccion() { return txtDireccion; }
    public JTextField getTxtPuesto() { return txtPuesto; }
    public JComboBox<String> getComboDepartamento() { return comboDepartamento; }
    public JSpinner getSpinFechaIngreso() { return spinFechaIngreso; }
    public JTextField getTxtSalario() { return txtSalario; }
    public JComboBox<String> getComboHorario() { return comboHorario; }
    public JComboBox<String> getComboEstado() { return comboEstado; }
    public JTable getTablaDocumentos() { return tablaDocumentos; }
    public DefaultTableModel getModeloTablaDocs() { return modeloTablaDocs; }
    public JButton getBtnAdjuntarDoc() { return btnAdjuntarDoc; }
    public JButton getBtnEliminarDoc() { return btnEliminarDoc; }
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnCancelar() { return btnCancelar; }
}
