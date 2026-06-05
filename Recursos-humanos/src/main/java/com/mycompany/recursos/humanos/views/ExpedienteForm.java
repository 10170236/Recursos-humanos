package com.mycompany.recursos.humanos.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Date;

/**
 * Vista premium y profesional del Formulario de Expedientes de Empleados.
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

    // Componentes dinámicos del Encabezado Premium
    private AvatarPanel panelAvatar;
    private JLabel lblNombreEmpleadoHeader;
    private StatusBadge badgeEstadoHeader;

    public ExpedienteForm() {
        super("Portal del Empleado - Gestión de Expediente");
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 720);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(850, 650));

        // Panel Principal con fondo gris premium claro
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(248, 249, 250)); // Slate background

        // --- ENCABEZADO DE PERFIL PREMIUM (Estilo Tarjeta de Usuario) ---
        JPanel panelPerfil = new JPanel(new BorderLayout(15, 15));
        panelPerfil.setBackground(Color.WHITE);
        panelPerfil.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(233, 236, 239), 1, true),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Panel Izquierdo: Avatar e Identidad
        JPanel panelIdentidad = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panelIdentidad.setOpaque(false);

        panelAvatar = new AvatarPanel();
        panelIdentidad.add(panelAvatar);

        JPanel panelInfoTextos = new JPanel(new GridLayout(2, 1, 0, 4));
        panelInfoTextos.setOpaque(false);

        lblNombreEmpleadoHeader = new JLabel("Nuevo Empleado");
        lblNombreEmpleadoHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblNombreEmpleadoHeader.setForeground(new Color(33, 37, 41));
        panelInfoTextos.add(lblNombreEmpleadoHeader);

        JPanel panelCargoYEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelCargoYEstado.setOpaque(false);

        JLabel lblCargoFijo = new JLabel("Ficha de Expediente Laboral");
        lblCargoFijo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCargoFijo.setForeground(new Color(108, 117, 125));
        panelCargoYEstado.add(lblCargoFijo);

        badgeEstadoHeader = new StatusBadge();
        panelCargoYEstado.add(badgeEstadoHeader);

        panelInfoTextos.add(panelCargoYEstado);
        panelIdentidad.add(panelInfoTextos);

        panelPerfil.add(panelIdentidad, BorderLayout.WEST);

        // Panel Derecho: Visualizadores rápidos
        JPanel panelMetricas = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        panelMetricas.setOpaque(false);
        // Podríamos añadir estadísticas rápidas en el futuro, por ahora dejamos espacio limpio
        panelPerfil.add(panelMetricas, BorderLayout.EAST);

        mainPanel.add(panelPerfil, BorderLayout.NORTH);

        // --- PANEL DE PESTAÑAS (Estilizado con FlatLaf) ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty("JTabbedPane.showTabSeparators", true);
        tabbedPane.putClientProperty("JTabbedPane.tabHeight", 40);
        tabbedPane.putClientProperty("JTabbedPane.tabInsets", new Insets(0, 24, 0, 24));
        tabbedPane.putClientProperty("JTabbedPane.selectedBackground", Color.WHITE);
        tabbedPane.putClientProperty("JTabbedPane.hasFullBorder", true);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // 1. Pestaña Datos Personales
        tabbedPane.addTab("Datos Personales", crearPanelDatosPersonales());
        
        // 2. Pestaña Datos Laborales
        tabbedPane.addTab("Datos Laborales", crearPanelDatosLaborales());
        
        // 3. Pestaña Documentación
        tabbedPane.addTab("Documentación", crearPanelDocumentacion());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // --- PANEL DE ACCIONES INFERIOR (Estilo moderno) ---
        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setOpaque(false);
        actionPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightActions.setOpaque(false);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancelar.putClientProperty("JButton.buttonType", "roundRect");
        btnCancelar.putClientProperty("JComponent.minimumWidth", 110);
        btnCancelar.setBackground(Color.WHITE);
        btnCancelar.setForeground(new Color(108, 117, 125));

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnLimpiar.putClientProperty("JButton.buttonType", "roundRect");
        btnLimpiar.putClientProperty("JComponent.minimumWidth", 110);
        btnLimpiar.setBackground(Color.WHITE);

        btnGuardar = new JButton("Guardar Expediente");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setBackground(new Color(13, 110, 253)); // Azul Premium
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.putClientProperty("JButton.buttonType", "roundRect");
        btnGuardar.putClientProperty("JComponent.minimumWidth", 180);

        rightActions.add(btnCancelar);
        rightActions.add(btnLimpiar);
        rightActions.add(btnGuardar);

        actionPanel.add(rightActions, BorderLayout.EAST);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // Lógica visual reactiva e interactiva
        configurarLógicaVisualReactiva();
        configurarValidacionesVisuales();

        // Acción de limpiar visual
        btnLimpiar.addActionListener(e -> limpiarFormulario());
    }

    private JPanel crearPanelDatosPersonales() {
        JPanel mainTabPanel = new JPanel(new BorderLayout(15, 15));
        mainTabPanel.setBackground(Color.WHITE);
        mainTabPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Sub-Tarjeta 1: Identidad
        JPanel cardIdentidad = crearTarjeta("Información de Identidad");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        cardIdentidad.add(crearEtiquetaCampo("Cédula / DNI:", true), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.8;
        txtDni = new JTextField();
        txtDni.putClientProperty("JTextField.placeholderText", "Ej. 1-2345-6789");
        txtDni.putClientProperty("JComponent.roundRect", true);
        cardIdentidad.add(txtDni, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        cardIdentidad.add(crearEtiquetaCampo("Nombre Completo:", true), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.8;
        txtNombreCompleto = new JTextField();
        txtNombreCompleto.putClientProperty("JTextField.placeholderText", "Ej. Juan Pérez González");
        txtNombreCompleto.putClientProperty("JComponent.roundRect", true);
        cardIdentidad.add(txtNombreCompleto, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.2;
        cardIdentidad.add(crearEtiquetaCampo("F. Nacimiento:", true), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.8;
        spinFechaNacimiento = new JSpinner(new SpinnerDateModel());
        spinFechaNacimiento.setEditor(new JSpinner.DateEditor(spinFechaNacimiento, "dd/MM/yyyy"));
        spinFechaNacimiento.putClientProperty("JComponent.roundRect", true);
        cardIdentidad.add(spinFechaNacimiento, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.2;
        cardIdentidad.add(crearEtiquetaCampo("Género:", false), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.8;
        comboGenero = new JComboBox<>(new String[]{"Seleccionar...", "Masculino", "Femenino", "Otro"});
        comboGenero.putClientProperty("JComponent.roundRect", true);
        cardIdentidad.add(comboGenero, gbc);

        // Sub-Tarjeta 2: Contacto
        JPanel cardContacto = crearTarjeta("Información de Contacto");
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(8, 8, 8, 8);
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        gbc2.anchor = GridBagConstraints.WEST;

        gbc2.gridx = 0; gbc2.gridy = 0; gbc2.weightx = 0.2;
        cardContacto.add(crearEtiquetaCampo("Teléfono:", false), gbc2);
        
        gbc2.gridx = 1; gbc2.weightx = 0.8;
        txtTelefono = new JTextField();
        txtTelefono.putClientProperty("JTextField.placeholderText", "Ej. 8888-8888");
        txtTelefono.putClientProperty("JComponent.roundRect", true);
        cardContacto.add(txtTelefono, gbc2);

        gbc2.gridx = 0; gbc2.gridy = 1; gbc2.weightx = 0.2;
        cardContacto.add(crearEtiquetaCampo("Correo Electrónico:", true), gbc2);
        
        gbc2.gridx = 1; gbc2.weightx = 0.8;
        txtCorreo = new JTextField();
        txtCorreo.putClientProperty("JTextField.placeholderText", "ejemplo@correo.com");
        txtCorreo.putClientProperty("JComponent.roundRect", true);
        cardContacto.add(txtCorreo, gbc2);

        gbc2.gridx = 0; gbc2.gridy = 2; gbc2.weightx = 0.2;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        cardContacto.add(crearEtiquetaCampo("Dirección:", false), gbc2);
        
        gbc2.gridx = 1; gbc2.weightx = 0.8;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.weighty = 1.0;
        txtDireccion = new JTextArea(3, 20);
        txtDireccion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDireccion.setLineWrap(true);
        txtDireccion.setWrapStyleWord(true);
        JScrollPane scrollDir = new JScrollPane(txtDireccion);
        scrollDir.putClientProperty("JComponent.roundRect", true);
        cardContacto.add(scrollDir, gbc2);

        // Integrar sub-tarjetas
        JPanel container = new JPanel(new GridLayout(1, 2, 20, 0));
        container.setOpaque(false);
        container.add(cardIdentidad);
        container.add(cardContacto);

        mainTabPanel.add(container, BorderLayout.CENTER);
        return mainTabPanel;
    }

    private JPanel crearPanelDatosLaborales() {
        JPanel mainTabPanel = new JPanel(new BorderLayout(15, 15));
        mainTabPanel.setBackground(Color.WHITE);
        mainTabPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Sub-Tarjeta 1: Detalles de Cargo
        JPanel cardCargo = crearTarjeta("Puesto y Ubicación");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        cardCargo.add(crearEtiquetaCampo("Puesto / Cargo:", true), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.8;
        txtPuesto = new JTextField();
        txtPuesto.putClientProperty("JTextField.placeholderText", "Ej. Ingeniero Senior");
        txtPuesto.putClientProperty("JComponent.roundRect", true);
        cardCargo.add(txtPuesto, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        cardCargo.add(crearEtiquetaCampo("Departamento:", true), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.8;
        comboDepartamento = new JComboBox<>(new String[]{
            "Seleccionar...", "Recursos Humanos", "Tecnología de Información", "Finanzas", "Ventas", "Operaciones"
        });
        comboDepartamento.putClientProperty("JComponent.roundRect", true);
        cardCargo.add(comboDepartamento, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        cardCargo.add(Box.createVerticalGlue(), gbc);

        // Sub-Tarjeta 2: Contrato y Nómina
        JPanel cardContrato = crearTarjeta("Detalles Contractuales");
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(8, 8, 8, 8);
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        gbc2.anchor = GridBagConstraints.WEST;

        gbc2.gridx = 0; gbc2.gridy = 0; gbc2.weightx = 0.2;
        cardContrato.add(crearEtiquetaCampo("Fecha Ingreso:", true), gbc2);
        
        gbc2.gridx = 1; gbc2.weightx = 0.8;
        spinFechaIngreso = new JSpinner(new SpinnerDateModel());
        spinFechaIngreso.setEditor(new JSpinner.DateEditor(spinFechaIngreso, "dd/MM/yyyy"));
        spinFechaIngreso.putClientProperty("JComponent.roundRect", true);
        cardContrato.add(spinFechaIngreso, gbc2);

        gbc2.gridx = 0; gbc2.gridy = 1; gbc2.weightx = 0.2;
        cardContrato.add(crearEtiquetaCampo("Salario Mensual:", true), gbc2);
        
        gbc2.gridx = 1; gbc2.weightx = 0.8;
        txtSalario = new JTextField();
        txtSalario.putClientProperty("JTextField.placeholderText", "0.00");
        txtSalario.putClientProperty("JTextField.leadingText", "$");
        txtSalario.putClientProperty("JComponent.roundRect", true);
        cardContrato.add(txtSalario, gbc2);

        gbc2.gridx = 0; gbc2.gridy = 2; gbc2.weightx = 0.2;
        cardContrato.add(crearEtiquetaCampo("Horario Laboral:", false), gbc2);
        
        gbc2.gridx = 1; gbc2.weightx = 0.8;
        comboHorario = new JComboBox<>(new String[]{
            "Matutino (8:00 AM - 5:00 PM)", "Vespertino (1:00 PM - 9:00 PM)", "Nocturno (9:00 PM - 6:00 AM)", "Mixto"
        });
        comboHorario.putClientProperty("JComponent.roundRect", true);
        cardContrato.add(comboHorario, gbc2);

        gbc2.gridx = 0; gbc2.gridy = 3; gbc2.weightx = 0.2;
        cardContrato.add(crearEtiquetaCampo("Estado:", true), gbc2);
        
        gbc2.gridx = 1; gbc2.weightx = 0.8;
        comboEstado = new JComboBox<>(new String[]{"Activo", "Inactivo", "Licencia"});
        comboEstado.putClientProperty("JComponent.roundRect", true);
        cardContrato.add(comboEstado, gbc2);

        // Integrar sub-tarjetas
        JPanel container = new JPanel(new GridLayout(1, 2, 20, 0));
        container.setOpaque(false);
        container.add(cardCargo);
        container.add(cardContrato);

        mainTabPanel.add(container, BorderLayout.CENTER);
        return mainTabPanel;
    }

    private JPanel crearPanelDocumentacion() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Título de la tabla
        JLabel lblTablaTitulo = new JLabel("Documentos del Expediente");
        lblTablaTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTablaTitulo.setForeground(new Color(33, 37, 41));
        panel.add(lblTablaTitulo, BorderLayout.NORTH);

        // Tabla de documentos estilizada
        String[] columnas = {"Nombre del Archivo", "Formato / Tipo", "Fecha de Subida", "Estado de Revisión"};
        modeloTablaDocs = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        modeloTablaDocs.addRow(new Object[]{"Contrato_Trabajo_Firmado.pdf", "PDF / Contrato", "01/06/2026", "Aprobado"});
        modeloTablaDocs.addRow(new Object[]{"DNI_Escaneado.jpg", "Imagen / Identidad", "01/06/2026", "Aprobado"});

        tablaDocumentos = new JTable(modeloTablaDocs);
        tablaDocumentos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaDocumentos.setRowHeight(32); // Mayor espaciado en la tabla
        tablaDocumentos.setShowVerticalLines(false); // Grid horizontal solamente
        tablaDocumentos.setGridColor(new Color(240, 242, 245));
        tablaDocumentos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaDocumentos.getTableHeader().setBackground(new Color(248, 249, 250));
        
        JScrollPane scrollTabla = new JScrollPane(tablaDocumentos);
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(230, 235, 242)));
        panel.add(scrollTabla, BorderLayout.CENTER);

        // Botones de acción de documentos
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBotones.setOpaque(false);

        btnAdjuntarDoc = new JButton("Adjuntar Archivo...");
        btnAdjuntarDoc.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAdjuntarDoc.putClientProperty("JButton.buttonType", "roundRect");
        btnAdjuntarDoc.setBackground(new Color(13, 110, 253));
        btnAdjuntarDoc.setForeground(Color.WHITE);

        btnEliminarDoc = new JButton("Eliminar");
        btnEliminarDoc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnEliminarDoc.putClientProperty("JButton.buttonType", "roundRect");
        btnEliminarDoc.setBackground(Color.WHITE);
        btnEliminarDoc.setForeground(new Color(220, 53, 69));

        panelBotones.add(btnAdjuntarDoc);
        panelBotones.add(btnEliminarDoc);
        panel.add(panelBotones, BorderLayout.SOUTH);

        // Lógica visual básica
        btnAdjuntarDoc.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getName();
                String ext = filename.contains(".") ? filename.substring(filename.lastIndexOf(".") + 1).toUpperCase() : "Desconocido";
                modeloTablaDocs.addRow(new Object[]{filename, ext, "Hoy", "Pendiente de Revisión"});
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

    private JPanel crearTarjeta(String tituloSeccion) {
        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(Color.WHITE);
        // Borde suave de tarjeta moderno
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 235, 242), 1, true),
            BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));
        tarjeta.setLayout(new GridBagLayout());
        
        // Título de la sub-tarjeta
        JLabel lblTitulo = new JLabel(tituloSeccion);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(new Color(73, 80, 87));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 8, 15, 8);
        tarjeta.add(lblTitulo, gbc);

        return tarjeta;
    }

    private JLabel crearEtiquetaCampo(String texto, boolean esObligatorio) {
        JLabel label = new JLabel();
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(73, 80, 87));
        if (esObligatorio) {
            label.setText("<html>" + texto + " <font color='#DC3545'>*</font></html>");
        } else {
            label.setText(texto);
        }
        return label;
    }

    private void configurarLógicaVisualReactiva() {
        // Al escribir el nombre, actualizar el avatar y el nombre del header en tiempo real
        txtNombreCompleto.getDocument().addDocumentListener(new DocumentListener() {
            private void actualizar() {
                String text = txtNombreCompleto.getText();
                SwingUtilities.invokeLater(() -> {
                    panelAvatar.setInitials(text);
                    if (text.trim().isEmpty()) {
                        lblNombreEmpleadoHeader.setText("Nuevo Empleado");
                    } else {
                        lblNombreEmpleadoHeader.setText(text.trim());
                    }
                });
            }
            @Override public void insertUpdate(DocumentEvent e) { actualizar(); }
            @Override public void removeUpdate(DocumentEvent e) { actualizar(); }
            @Override public void changedUpdate(DocumentEvent e) { actualizar(); }
        });

        // Al cambiar el combo de estado, actualizar el badge del header inmediatamente
        comboEstado.addActionListener(e -> {
            String selected = (String) comboEstado.getSelectedItem();
            badgeEstadoHeader.setStatus(selected);
        });
        
        // Inicializar el badge del header con el estado por defecto
        badgeEstadoHeader.setStatus("Activo");
    }

    private void configurarValidacionesVisuales() {
        txtDni.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { validarCampoVacio(txtDni); }
        });
        txtNombreCompleto.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { validarCampoVacio(txtNombreCompleto); }
        });
        txtCorreo.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                if (validarCampoVacio(txtCorreo)) {
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
            @Override public void focusLost(FocusEvent e) { validarCampoVacio(txtPuesto); }
        });
        txtSalario.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
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
        
        lblNombreEmpleadoHeader.setText("Nuevo Empleado");
        panelAvatar.setInitials("");
        badgeEstadoHeader.setStatus("Activo");
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

/**
 * Avatar circular dinámico que renderiza las iniciales de una persona.
 */
class AvatarPanel extends JPanel {
    private String initials = "??";
    private final Color startColor = new Color(74, 85, 104); // Slate dark
    private final Color endColor = new Color(45, 55, 72);

    public AvatarPanel() {
        setPreferredSize(new Dimension(60, 60));
        setMinimumSize(new Dimension(60, 60));
        setMaximumSize(new Dimension(60, 60));
        setOpaque(false);
    }

    public void setInitials(String text) {
        if (text == null || text.trim().isEmpty()) {
            this.initials = "??";
        } else {
            String[] parts = text.trim().split("\\s+");
            if (parts.length == 1) {
                this.initials = parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
            } else {
                this.initials = ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Fondo degradado circular
        GradientPaint gp = new GradientPaint(0, 0, startColor, w, h, endColor);
        g2.setPaint(gp);
        g2.fillOval(0, 0, w - 1, h - 1);

        // Texto de iniciales blanco
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(initials);
        int textH = fm.getAscent();

        g2.drawString(initials, (w - textW) / 2, (h + textH) / 2 - 3);
        g2.dispose();
    }
}

/**
 * Badge redondeado moderno para representar el estado del empleado.
 */
class StatusBadge extends JPanel {
    private String text = "Activo";
    private Color dotColor = new Color(46, 204, 113);
    private Color bgColor = new Color(46, 204, 113, 30);
    private Color textColor = new Color(30, 130, 76);

    public StatusBadge() {
        setOpaque(false);
    }

    public void setStatus(String status) {
        this.text = status == null ? "Activo" : status;
        if ("Activo".equalsIgnoreCase(this.text)) {
            dotColor = new Color(46, 204, 113);
            bgColor = new Color(46, 204, 113, 35);
            textColor = new Color(30, 130, 76);
        } else if ("Inactivo".equalsIgnoreCase(this.text)) {
            dotColor = new Color(231, 76, 60);
            bgColor = new Color(231, 76, 60, 35);
            textColor = new Color(150, 40, 27);
        } else { // Licencia
            dotColor = new Color(241, 196, 15);
            bgColor = new Color(241, 196, 15, 35);
            textColor = new Color(180, 120, 10);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Fondo de píldora redondeada
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, w - 1, h - 1, h, h);

        // Círculo indicador de estado
        g2.setColor(dotColor);
        g2.fillOval(10, (h - 8) / 2, 8, 8);

        // Texto del estado
        g2.setColor(textColor);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, 24, (h + fm.getAscent()) / 2 - 2);

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(new Font("Segoe UI", Font.BOLD, 12));
        int width = 24 + fm.stringWidth(text) + 12;
        return new Dimension(width, 26);
    }
}
