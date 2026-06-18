package com.mycompany.recursos.humanos;

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
 * Vista premium estilo Dashboard Dividido del Formulario de Expedientes de Empleados.
 * Capa exclusivamente visual, sin lógica de base de datos ni de negocio.
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

    // Componentes de la Barra Lateral Izquierda (Sidebar)
    private AvatarPanel panelAvatar;
    private JLabel lblNombreEmpleadoHeader;
    private JLabel lblPuestoHeader;
    private StatusBadge badgeEstadoHeader;
    
    // Etiquetas de datos rápidos en Sidebar
    private JLabel lblSidebarDni;
    private JLabel lblSidebarDept;
    private JLabel lblSidebarCorreo;
    private JLabel lblSidebarTelefono;

    public ExpedienteForm() {
        super("Portal del Empleado - Expediente Digital");
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 720);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(920, 660));

        // Panel de fondo gris sutil de la app
        JPanel mainContainer = new JPanel(new BorderLayout(0, 0));
        mainContainer.setBackground(new Color(248, 249, 250));

        // ==========================================
        // 1. BARRA LATERAL DE PERFIL (Sidebar - Izquierda)
        // ==========================================
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(280, 0));
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 235, 242)));
        sidebarPanel.setLayout(new GridBagLayout());

        GridBagConstraints sGbc = new GridBagConstraints();
        sGbc.gridx = 0;
        sGbc.gridy = GridBagConstraints.RELATIVE;
        sGbc.fill = GridBagConstraints.HORIZONTAL;
        sGbc.anchor = GridBagConstraints.CENTER;
        sGbc.insets = new Insets(10, 20, 10, 20);

        // Avatar Circular
        panelAvatar = new AvatarPanel();
        JPanel avatarContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        avatarContainer.setOpaque(false);
        avatarContainer.add(panelAvatar);
        sGbc.insets = new Insets(30, 20, 10, 20);
        sidebarPanel.add(avatarContainer, sGbc);

        // Nombre del Empleado
        lblNombreEmpleadoHeader = new JLabel("Nuevo Empleado", SwingConstants.CENTER);
        lblNombreEmpleadoHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblNombreEmpleadoHeader.setForeground(new Color(33, 37, 41));
        sGbc.insets = new Insets(10, 15, 2, 15);
        sidebarPanel.add(lblNombreEmpleadoHeader, sGbc);

        // Puesto del Empleado
        lblPuestoHeader = new JLabel("Puesto no asignado", SwingConstants.CENTER);
        lblPuestoHeader.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPuestoHeader.setForeground(new Color(108, 117, 125));
        sGbc.insets = new Insets(0, 15, 10, 15);
        sidebarPanel.add(lblPuestoHeader, sGbc);

        // Badge de Estado
        badgeEstadoHeader = new StatusBadge();
        JPanel badgeContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        badgeContainer.setOpaque(false);
        badgeContainer.add(badgeEstadoHeader);
        sGbc.insets = new Insets(5, 20, 25, 20);
        sidebarPanel.add(badgeContainer, sGbc);

        // Línea Divisora
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(233, 236, 239));
        sGbc.insets = new Insets(0, 15, 20, 15);
        sidebarPanel.add(separator, sGbc);

        // Título Datos Rápidos
        JLabel lblQuickFacts = new JLabel("INFORMACIÓN RÁPIDA");
        lblQuickFacts.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblQuickFacts.setForeground(new Color(173, 181, 189));
        sGbc.insets = new Insets(0, 20, 12, 20);
        sidebarPanel.add(lblQuickFacts, sGbc);

        // Panel de Datos Rápidos
        JPanel panelDatosRapidos = new JPanel(new GridLayout(4, 1, 0, 16));
        panelDatosRapidos.setOpaque(false);

        lblSidebarDni = crearLabelQuickFact("DNI", "No registrado");
        lblSidebarDept = crearLabelQuickFact("Departamento", "No asignado");
        lblSidebarCorreo = crearLabelQuickFact("Correo", "No registrado");
        lblSidebarTelefono = crearLabelQuickFact("Teléfono", "No registrado");

        panelDatosRapidos.add(lblSidebarDni);
        panelDatosRapidos.add(lblSidebarDept);
        panelDatosRapidos.add(lblSidebarCorreo);
        panelDatosRapidos.add(lblSidebarTelefono);

        sGbc.insets = new Insets(0, 20, 20, 20);
        sidebarPanel.add(panelDatosRapidos, sGbc);

        // Glue inferior para empujar todo hacia arriba
        sGbc.weighty = 1.0;
        sidebarPanel.add(Box.createVerticalGlue(), sGbc);

        mainContainer.add(sidebarPanel, BorderLayout.WEST);

        // ==========================================
        // 2. PANEL DE TRABAJO PRINCIPAL (Derecha)
        // ==========================================
        JPanel mainWorkPanel = new JPanel(new BorderLayout(15, 15));
        mainWorkPanel.setOpaque(false);
        mainWorkPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Título de la sección principal
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel lblMainTitle = new JLabel("Formulario de Expediente");
        lblMainTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblMainTitle.setForeground(new Color(33, 37, 41));
        titlePanel.add(lblMainTitle, BorderLayout.WEST);
        mainWorkPanel.add(titlePanel, BorderLayout.NORTH);

        // Pestañas Subrayadas (Estilo FlatLaf)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty("JTabbedPane.tabType", "underlined");
        tabbedPane.putClientProperty("JTabbedPane.tabHeight", 45);
        tabbedPane.putClientProperty("JTabbedPane.tabInsets", new Insets(0, 20, 0, 20));
        tabbedPane.putClientProperty("JTabbedPane.selectedBackground", new Color(248, 249, 250));
        tabbedPane.putClientProperty("JTabbedPane.hasFullBorder", false);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.addTab("Datos Personales", crearPanelDatosPersonales());
        tabbedPane.addTab("Datos Laborales", crearPanelDatosLaborales());
        tabbedPane.addTab("Documentos", crearPanelDocumentacion());

        mainWorkPanel.add(tabbedPane, BorderLayout.CENTER);

        // ==========================================
        // 3. BARRA DE ACCIONES INFERIOR (Derecha)
        // ==========================================
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
        btnGuardar.setBackground(new Color(13, 110, 253)); // Royal Blue
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.putClientProperty("JButton.buttonType", "roundRect");
        btnGuardar.putClientProperty("JComponent.minimumWidth", 180);

        rightActions.add(btnCancelar);
        rightActions.add(btnLimpiar);
        rightActions.add(btnGuardar);

        actionPanel.add(rightActions, BorderLayout.EAST);
        mainWorkPanel.add(actionPanel, BorderLayout.SOUTH);

        mainContainer.add(mainWorkPanel, BorderLayout.CENTER);

        setContentPane(mainContainer);

        // Lógica visual e interactiva
        configurarLogicaVisualReactiva();
        configurarValidacionesVisuales();

        // Acción de limpiar
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        
        // ==========================================
        // CONECTANDO LA INTERFAZ CON EL BACKEND
        // ==========================================
        btnGuardar.addActionListener(e -> procesarYGuardarExpediente());
    }

    private JPanel crearPanelDatosPersonales() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 235, 242), 1, true),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;

        // Fila 0: DNI y Nombre Completo
        txtDni = new JTextField();
        txtDni.putClientProperty("JTextField.placeholderText", "Ej. 1-2345-6789");
        txtDni.putClientProperty("JComponent.roundRect", true);
        gbc.gridx = 0; gbc.gridy = 0;
        gridPanel.add(crearInputGroup("Cédula / DNI", txtDni, true), gbc);

        txtNombreCompleto = new JTextField();
        txtNombreCompleto.putClientProperty("JTextField.placeholderText", "Ej. Juan Pérez González");
        txtNombreCompleto.putClientProperty("JComponent.roundRect", true);
        gbc.gridx = 1;
        gridPanel.add(crearInputGroup("Nombre Completo", txtNombreCompleto, true), gbc);

        // Fila 1: Fecha Nacimiento y Género
        spinFechaNacimiento = new JSpinner(new SpinnerDateModel());
        spinFechaNacimiento.setEditor(new JSpinner.DateEditor(spinFechaNacimiento, "dd/MM/yyyy"));
        spinFechaNacimiento.putClientProperty("JComponent.roundRect", true);
        gbc.gridx = 0; gbc.gridy = 1;
        gridPanel.add(crearInputGroup("Fecha de Nacimiento", spinFechaNacimiento, true), gbc);

        comboGenero = new JComboBox<>(new String[]{"Seleccionar...", "Masculino", "Femenino", "Otro"});
        comboGenero.putClientProperty("JComponent.roundRect", true);
        gbc.gridx = 1;
        gridPanel.add(crearInputGroup("Género", comboGenero, false), gbc);

        // Fila 2: Teléfono y Correo
        txtTelefono = new JTextField();
        txtTelefono.putClientProperty("JTextField.placeholderText", "Ej. 8888-8888");
        txtTelefono.putClientProperty("JComponent.roundRect", true);
        gbc.gridx = 0; gbc.gridy = 2;
        gridPanel.add(crearInputGroup("Teléfono de Contacto", txtTelefono, false), gbc);

        txtCorreo = new JTextField();
        txtCorreo.putClientProperty("JTextField.placeholderText", "ejemplo@correo.com");
        txtCorreo.putClientProperty("JComponent.roundRect", true);
        gbc.gridx = 1;
        gridPanel.add(crearInputGroup("Correo Electrónico", txtCorreo, true), gbc);

        // Fila 3: Dirección (Span 2 columnas)
        txtDireccion = new JTextArea(3, 20);
        txtDireccion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDireccion.setLineWrap(true);
        txtDireccion.setWrapStyleWord(true);
        JScrollPane scrollDir = new JScrollPane(txtDireccion);
        scrollDir.putClientProperty("JComponent.roundRect", true);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gridPanel.add(crearInputGroup("Dirección Domiciliaria", scrollDir, false), gbc);

        panel.add(gridPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelDatosLaborales() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 235, 242), 1, true),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;

        // Fila 0: Puesto y Departamento
        txtPuesto = new JTextField();
        txtPuesto.putClientProperty("JTextField.placeholderText", "Ej. Especialista de TI");
        txtPuesto.putClientProperty("JComponent.roundRect", true);
        gbc.gridx = 0; gbc.gridy = 0;
        gridPanel.add(crearInputGroup("Puesto / Cargo", txtPuesto, true), gbc);

        comboDepartamento = new JComboBox<>(new String[]{
            "Seleccionar...", "Recursos Humanos", "Tecnología de Información", "Finanzas", "Ventas", "Operaciones"
        });
        comboDepartamento.putClientProperty("JComponent.roundRect", true);
        gbc.gridx = 1;
        gridPanel.add(crearInputGroup("Departamento", comboDepartamento, true), gbc);

        // Fila 1: Fecha Ingreso y Salario
        spinFechaIngreso = new JSpinner(new SpinnerDateModel());
        spinFechaIngreso.setEditor(new JSpinner.DateEditor(spinFechaIngreso, "dd/MM/yyyy"));
        spinFechaIngreso.putClientProperty("JComponent.roundRect", true);
        gbc.gridx = 0; gbc.gridy = 1;
        gridPanel.add(crearInputGroup("Fecha de Ingreso", spinFechaIngreso, true), gbc);

        txtSalario = new JTextField();
        txtSalario.putClientProperty("JTextField.placeholderText", "0.00");
        txtSalario.putClientProperty("JTextField.leadingText", "$");
        txtSalario.putClientProperty("JComponent.roundRect", true);
        gbc.gridx = 1;
        gridPanel.add(crearInputGroup("Salario Mensual", txtSalario, true), gbc);

        // Fila 2: Horario y Estado
        comboHorario = new JComboBox<>(new String[]{
            "Matutino (8:00 AM - 5:00 PM)", "Vespertino (1:00 PM - 9:00 PM)", "Nocturno (9:00 PM - 6:00 AM)", "Mixto"
        });
        comboHorario.putClientProperty("JComponent.roundRect", true);
        gbc.gridx = 0; gbc.gridy = 2;
        gridPanel.add(crearInputGroup("Horario Laboral", comboHorario, false), gbc);

        comboEstado = new JComboBox<>(new String[]{"Activo", "Inactivo", "Licencia"});
        comboEstado.putClientProperty("JComponent.roundRect", true);
        gbc.gridx = 1;
        gridPanel.add(crearInputGroup("Estado del Empleado", comboEstado, true), gbc);

        // Espacio vacío para balancear el Grid
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gridPanel.add(Box.createVerticalGlue(), gbc);

        panel.add(gridPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelDocumentacion() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 235, 242), 1, true),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        // Tabla de documentos
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
        tablaDocumentos.setRowHeight(35); // Tabla cómoda
        tablaDocumentos.setShowVerticalLines(false);
        tablaDocumentos.setGridColor(new Color(240, 242, 245));
        tablaDocumentos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaDocumentos.getTableHeader().setBackground(new Color(248, 249, 250));
        
        JScrollPane scrollTabla = new JScrollPane(tablaDocumentos);
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(230, 235, 242)));
        panel.add(scrollTabla, BorderLayout.CENTER);

        // Acciones
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

        // Lógica
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

    // ==========================================
    // MÉTODOS DE SOPORTE E INTERACTIVIDAD
    // ==========================================

    private JPanel crearInputGroup(String labelText, JComponent inputComponent, boolean esObligatorio) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        JLabel label = crearEtiquetaCampo(labelText, esObligatorio);
        panel.add(label, BorderLayout.NORTH);
        panel.add(inputComponent, BorderLayout.CENTER);
        return panel;
    }

    private JLabel crearEtiquetaCampo(String texto, boolean esObligatorio) {
        JLabel label = new JLabel();
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(73, 80, 87));
        if (esObligatorio) {
            label.setText("<html>" + texto + " <font color='#DC3545'>*</font></html>");
        } else {
            label.setText(texto);
        }
        return label;
    }

    private JLabel crearLabelQuickFact(String factName, String factValue) {
        JLabel label = new JLabel();
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        actualizarTextoQuickFact(label, factName, factValue);
        return label;
    }

    private void actualizarTextoQuickFact(JLabel label, String factName, String factValue) {
        String cleanValue = (factValue == null || factValue.trim().isEmpty()) ? "No registrado" : factValue.trim();
        if ("No asignado".equalsIgnoreCase(cleanValue) || "No registrado".equalsIgnoreCase(cleanValue) || "Seleccionar...".equalsIgnoreCase(cleanValue)) {
            label.setText("<html><font color='#868E96'>" + factName + ":</font> <font color='#ADB5BD'><i>" + cleanValue + "</i></font></html>");
        } else {
            label.setText("<html><font color='#868E96'>" + factName + ":</font> <font color='#495057'><b>" + cleanValue + "</b></font></html>");
        }
    }

    private void configurarLogicaVisualReactiva() {
        // Enlace del Nombre Completo (Avatar y Header)
        txtNombreCompleto.getDocument().addDocumentListener(new DocumentListener() {
            private void actualizar() {
                String text = txtNombreCompleto.getText();
                SwingUtilities.invokeLater(() -> {
                    panelAvatar.setInitials(text);
                    lblNombreEmpleadoHeader.setText(text.trim().isEmpty() ? "Nuevo Empleado" : text.trim());
                });
            }
            @Override public void insertUpdate(DocumentEvent e) { actualizar(); }
            @Override public void removeUpdate(DocumentEvent e) { actualizar(); }
            @Override public void changedUpdate(DocumentEvent e) { actualizar(); }
        });

        // Enlace del DNI en Sidebar
        txtDni.getDocument().addDocumentListener(new DocumentListener() {
            private void actualizar() {
                String text = txtDni.getText();
                SwingUtilities.invokeLater(() -> actualizarTextoQuickFact(lblSidebarDni, "DNI", text));
            }
            @Override public void insertUpdate(DocumentEvent e) { actualizar(); }
            @Override public void removeUpdate(DocumentEvent e) { actualizar(); }
            @Override public void changedUpdate(DocumentEvent e) { actualizar(); }
        });

        // Enlace del Correo en Sidebar
        txtCorreo.getDocument().addDocumentListener(new DocumentListener() {
            private void actualizar() {
                String text = txtCorreo.getText();
                SwingUtilities.invokeLater(() -> actualizarTextoQuickFact(lblSidebarCorreo, "Correo", text));
            }
            @Override public void insertUpdate(DocumentEvent e) { actualizar(); }
            @Override public void removeUpdate(DocumentEvent e) { actualizar(); }
            @Override public void changedUpdate(DocumentEvent e) { actualizar(); }
        });

        // Enlace del Teléfono en Sidebar
        txtTelefono.getDocument().addDocumentListener(new DocumentListener() {
            private void actualizar() {
                String text = txtTelefono.getText();
                SwingUtilities.invokeLater(() -> actualizarTextoQuickFact(lblSidebarTelefono, "Teléfono", text));
            }
            @Override public void insertUpdate(DocumentEvent e) { actualizar(); }
            @Override public void removeUpdate(DocumentEvent e) { actualizar(); }
            @Override public void changedUpdate(DocumentEvent e) { actualizar(); }
        });

        // Enlace del Puesto en Header
        txtPuesto.getDocument().addDocumentListener(new DocumentListener() {
            private void actualizar() {
                String text = txtPuesto.getText();
                SwingUtilities.invokeLater(() -> lblPuestoHeader.setText(text.trim().isEmpty() ? "Puesto no asignado" : text.trim()));
            }
            @Override public void insertUpdate(DocumentEvent e) { actualizar(); }
            @Override public void removeUpdate(DocumentEvent e) { actualizar(); }
            @Override public void changedUpdate(DocumentEvent e) { actualizar(); }
        });

        // Enlace del Departamento en Sidebar
        comboDepartamento.addActionListener(e -> {
            String selected = (String) comboDepartamento.getSelectedItem();
            actualizarTextoQuickFact(lblSidebarDept, "Departamento", selected);
        });

        // Enlace de Estado en Header
        comboEstado.addActionListener(e -> {
            String selected = (String) comboEstado.getSelectedItem();
            badgeEstadoHeader.setStatus(selected);
        });

        // Inicializar estados iniciales en el Sidebar
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
                        txtCorreo.setToolTipText("El formato del correo electrónico no es válido.");
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
                        txtSalario.setToolTipText("El salario debe ser un valor numérico decimal válido.");
                    }
                }
            }
        });
    }

    private boolean validarCampoVacio(JTextField campo) {
        if (campo.getText().trim().isEmpty()) {
            campo.putClientProperty("JComponent.outline", "error");
            campo.setToolTipText("Este campo es obligatorio.");
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

        // Restablecer etiquetas Sidebar
        lblNombreEmpleadoHeader.setText("Nuevo Empleado");
        lblPuestoHeader.setText("Puesto no asignado");
        panelAvatar.setInitials("");
        badgeEstadoHeader.setStatus("Activo");
        
        actualizarTextoQuickFact(lblSidebarDni, "DNI", "No registrado");
        actualizarTextoQuickFact(lblSidebarDept, "Departamento", "No asignado");
        actualizarTextoQuickFact(lblSidebarCorreo, "Correo", "No registrado");
        actualizarTextoQuickFact(lblSidebarTelefono, "Teléfono", "No registrado");
    }
    
    
    private void procesarYGuardarExpediente() {
        // 1. Extraemos los datos clave de la pantalla
        String nombre = txtNombreCompleto.getText().trim();
        String correo = txtCorreo.getText().trim();
        String salarioStr = txtSalario.getText().trim();

        // 2. Validación básica para no procesar datos vacíos
        if (nombre.isEmpty() || correo.isEmpty() || salarioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, completa los campos obligatorios (Nombre, Correo, Salario) antes de guardar.", 
                "Faltan Datos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Cambiamos el cursor a "Cargando"
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            double salarioBaseMensual = Double.parseDouble(salarioStr);

            // ==========================================
            // INTEGRACIÓN DE LAS PIEZAS DEL SISTEMA
            // ==========================================
            
            // A. Motor Matemático y Nómina: Simulamos 160 horas mensuales para sacar el pago por hora
            double pagoPorHora = salarioBaseMensual / 160.0; 
            double salarioNeto = ProcesadorNomina.obtenerSalarioNeto(160.0, pagoPorHora);

            // B. Generador de Documentos (PDF)
            GeneradorPDF generadorPDF = new GeneradorPDF();
            String rutaPDF = generadorPDF.crearBoleta(nombre, salarioNeto);

            // C. Gestor de Correos (Lo enviamos en segundo plano para que la ventana no se trabe)
            EnviadorCorreo enviador = new EnviadorCorreo();
            new Thread(() -> {
                try {
                    enviador.enviar(correo, nombre, rutaPDF);
                    
                    // Si todo salió bien, mostramos mensaje de éxito y limpiamos
                    SwingUtilities.invokeLater(() -> {
                        setCursor(Cursor.getDefaultCursor());
                        JOptionPane.showMessageDialog(this, 
                            "¡Expediente guardado con éxito!\nLa boleta ha sido enviada a: " + correo, 
                            "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                        limpiarFormulario();
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        setCursor(Cursor.getDefaultCursor());
                        JOptionPane.showMessageDialog(this, 
                            "Se guardó el expediente pero hubo un error al enviar el correo.\nRevisa tu conexión o credenciales.", 
                            "Aviso", JOptionPane.WARNING_MESSAGE);
                    });
                }
            }).start();

        } catch (NumberFormatException ex) {
            setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this, 
                "El salario ingresado no es válido. Usa solo números y puntos decimales.", 
                "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
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
    private final Color startColor = new Color(74, 85, 104);
    private final Color endColor = new Color(45, 55, 72);

    public AvatarPanel() {
        setPreferredSize(new Dimension(80, 80));
        setMinimumSize(new Dimension(80, 80));
        setMaximumSize(new Dimension(80, 80));
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
        g2.fillOval(0, 0, w, h);

        // Texto de iniciales
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
        FontMetrics fm = g2.getFontMetrics();
        int x = (w - fm.stringWidth(initials)) / 2;
        int y = ((h - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(initials, x, y);
        g2.dispose();
    }
}

/**
 * Etiqueta de estado visual (StatusBadge) con forma de píldora y punto indicador.
 */
class StatusBadge extends JPanel {
    private Color bgColor;
    private Color dotColor;
    private Color textColor;
    private String text = "ACTIVO";

    public StatusBadge() {
        setOpaque(false);
        setStatus("Activo"); // Estado por defecto
    }

    public void setStatus(String status) {
        this.text = status.toUpperCase();
        if ("ACTIVO".equals(this.text)) {
            bgColor = new Color(209, 231, 221); // Verde claro
            dotColor = new Color(25, 135, 84);  // Verde vibrante
            textColor = new Color(15, 81, 50);  // Verde oscuro
        } else if ("INACTIVO".equals(this.text)) {
            bgColor = new Color(248, 215, 218); // Rojo claro
            dotColor = new Color(220, 53, 69);  // Rojo vibrante
            textColor = new Color(132, 32, 41); // Rojo oscuro
        } else { // Licencia / Otro
            bgColor = new Color(255, 243, 205); // Amarillo claro
            dotColor = new Color(255, 193, 7);  // Amarillo vibrante
            textColor = new Color(102, 77, 3);  // Amarillo oscuro
        }
        repaint();
        revalidate();
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