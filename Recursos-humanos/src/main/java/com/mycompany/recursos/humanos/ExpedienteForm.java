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
        
        // MODIFICACIÓN: Al abrir la ventana de forma independiente o para un "Nuevo Empleado",
        // nos aseguramos de que los eventos del botón Guardar queden enlazados al controlador.
        new ExpedienteController(this);
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Cambiado a DISPOSE para no tumbar la app entera
        setSize(1000, 720);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(920, 660));

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

        panelAvatar = new AvatarPanel();
        JPanel avatarContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        avatarContainer.setOpaque(false);
        avatarContainer.add(panelAvatar);
        sGbc.insets = new Insets(30, 20, 10, 20);
        sidebarPanel.add(avatarContainer, sGbc);

        lblNombreEmpleadoHeader = new JLabel("Nuevo Empleado", SwingConstants.CENTER);
        lblNombreEmpleadoHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblNombreEmpleadoHeader.setForeground(new Color(33, 37, 41));
        sGbc.insets = new Insets(10, 15, 2, 15);
        sidebarPanel.add(lblNombreEmpleadoHeader, sGbc);

        lblPuestoHeader = new JLabel("Puesto no asignado", SwingConstants.CENTER);
        lblPuestoHeader.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPuestoHeader.setForeground(new Color(108, 117, 125));
        sGbc.insets = new Insets(0, 15, 10, 15);
        sidebarPanel.add(lblPuestoHeader, sGbc);

        badgeEstadoHeader = new StatusBadge();
        JPanel badgeContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        badgeContainer.setOpaque(false);
        badgeContainer.add(badgeEstadoHeader);
        sGbc.insets = new Insets(5, 20, 25, 20);
        sidebarPanel.add(badgeContainer, sGbc);

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(233, 236, 239));
        sGbc.insets = new Insets(0, 15, 20, 15);
        sidebarPanel.add(separator, sGbc);

        JLabel lblQuickFacts = new JLabel("INFORMACIÓN RÁPIDA");
        lblQuickFacts.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblQuickFacts.setForeground(new Color(173, 181, 189));
        sGbc.insets = new Insets(0, 20, 12, 20);
        sidebarPanel.add(lblQuickFacts, sGbc);

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

        sGbc.weighty = 1.0;
        sidebarPanel.add(Box.createVerticalGlue(), sGbc);

        mainContainer.add(sidebarPanel, BorderLayout.WEST);

        // ==========================================
        // 2. PANEL DE TRABAJO PRINCIPAL (Derecha)
        // ==========================================
        JPanel mainWorkPanel = new JPanel(new BorderLayout(15, 15));
        mainWorkPanel.setOpaque(false);
        mainWorkPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel lblMainTitle = new JLabel("Formulario de Expediente");
        lblMainTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblMainTitle.setForeground(new Color(33, 37, 41));
        titlePanel.add(lblMainTitle, BorderLayout.WEST);
        mainWorkPanel.add(titlePanel, BorderLayout.NORTH);

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
        btnCancelar.addActionListener(e -> dispose()); // Acción por defecto para cerrar

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnLimpiar.putClientProperty("JButton.buttonType", "roundRect");
        btnLimpiar.putClientProperty("JComponent.minimumWidth", 110);
        btnLimpiar.setBackground(Color.WHITE);

        btnGuardar = new JButton("Guardar Expediente");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setBackground(new Color(13, 110, 253));
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

        configurarLogicaVisualReactiva();
        configurarValidacionesVisuales();

        btnLimpiar.addActionListener(e -> limpiarFormulario());
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

        spinFechaNacimiento = new JSpinner(new SpinnerDateModel());
        spinFechaNacimiento.setEditor(new JSpinner.DateEditor(spinFechaNacimiento, "dd/MM/yyyy"));
        spinFechaNacimiento.putClientProperty("JComponent.roundRect", true);
        gbc.gridx = 0; gbc.gridy = 1;
        gridPanel.add(crearInputGroup("Fecha de Nacimiento", spinFechaNacimiento, true), gbc);

        comboGenero = new JComboBox<>(new String[]{"Seleccionar...", "Masculino", "Femenino", "Otro"});
        comboGenero.putClientProperty("JComponent.roundRect", true);
        gbc.gridx = 1;
        gridPanel.add(crearInputGroup("Género", comboGenero, false), gbc);

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
        tablaDocumentos.setRowHeight(35);
        tablaDocumentos.setShowVerticalLines(false);
        tablaDocumentos.setGridColor(new Color(240, 242, 245));
        tablaDocumentos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaDocumentos.getTableHeader().setBackground(new Color(248, 249, 250));
        
        JScrollPane scrollTabla = new JScrollPane(tablaDocumentos);
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(230, 235, 242)));
        panel.add(scrollTabla, BorderLayout.CENTER);

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

        txtDni.getDocument().addDocumentListener(new DocumentListener() {
            private void actualizar() {
                String text = txtDni.getText();
                SwingUtilities.invokeLater(() -> actualizarTextoQuickFact(lblSidebarDni, "DNI", text));
            }
            @Override public void insertUpdate(DocumentEvent e) { actualizar(); }
            @Override public void removeUpdate(DocumentEvent e) { actualizar(); }
            @Override public void changedUpdate(DocumentEvent e) { actualizar(); }
        });

        txtCorreo.getDocument().addDocumentListener(new DocumentListener() {
            private void actualizar() {
                String text = txtCorreo.getText();
                SwingUtilities.invokeLater(() -> actualizarTextoQuickFact(lblSidebarCorreo, "Correo", text));
            }
            @Override public void insertUpdate(DocumentEvent e) { actualizar(); }
            @Override public void removeUpdate(DocumentEvent e) { actualizar(); }
            @Override public void changedUpdate(DocumentEvent e) { actualizar(); }
        });

        txtTelefono.getDocument().addDocumentListener(new DocumentListener() {
            private void actualizar() {
                String text = txtTelefono.getText();
                SwingUtilities.invokeLater(() -> actualizarTextoQuickFact(lblSidebarTelefono, "Teléfono", text));
            }
            @Override public void insertUpdate(DocumentEvent e) { actualizar(); }
            @Override public void removeUpdate(DocumentEvent e) { actualizar(); }
            @Override public void changedUpdate(DocumentEvent e) { actualizar(); }
        });

        txtPuesto.getDocument().addDocumentListener(new DocumentListener() {
            private void actualizar() {
                String text = txtPuesto.getText();
                SwingUtilities.invokeLater(() -> lblPuestoHeader.setText(text.trim().isEmpty() ? "Puesto no asignado" : text.trim()));
            }
            @Override public void insertUpdate(DocumentEvent e) { actualizar(); }
            @Override public void removeUpdate(DocumentEvent e) { actualizar(); }
            @Override public void changedUpdate(DocumentEvent e) { actualizar(); }
        });

        comboDepartamento.addActionListener(e -> {
            String selected = (String) comboDepartamento.getSelectedItem();
            actualizarTextoQuickFact(lblSidebarDept, "Departamento", selected);
        });

        comboEstado.addActionListener(e -> {
            String selected = (String) comboEstado.getSelectedItem();
            badgeEstadoHeader.setStatus(selected);
        });

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

        txtDni.putClientProperty("JComponent.outline", null);
        txtNombreCompleto.putClientProperty("JComponent.outline", null);
        txtCorreo.putClientProperty("JComponent.outline", null);
        txtPuesto.putClientProperty("JComponent.outline", null);
        txtSalario.putClientProperty("JComponent.outline", null);

        lblNombreEmpleadoHeader.setText("Nuevo Empleado");
        lblPuestoHeader.setText("Puesto no asignado");
        panelAvatar.setInitials("");
        badgeEstadoHeader.setStatus("Activo");
        
        actualizarTextoQuickFact(lblSidebarDni, "DNI", "No registrado");
        actualizarTextoQuickFact(lblSidebarDept, "Departamento", "No asignado");
        actualizarTextoQuickFact(lblSidebarCorreo, "Correo", "No registrado");
        actualizarTextoQuickFact(lblSidebarTelefono, "Teléfono", "No registrado");
    }

    // --- Getters ---
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

        GradientPaint gp = new GradientPaint(0, 0, startColor, w, h, endColor);
        g2.setPaint(gp);
        g2.fillOval(0, 0, w - 1, h - 1);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 26));
        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(initials);
        int textH = fm.getAscent();

        g2.drawString(initials, (w - textW) / 2, (h + textH) / 2 - 4);
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
        this.text = (status == null || "Seleccionar...".equalsIgnoreCase(status)) ? "Activo" : status;
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

        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, w - 1, h - 1, h, h);

        g2.setColor(dotColor);
        g2.fillOval(10, (h - 8) / 2, 8, 8);

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