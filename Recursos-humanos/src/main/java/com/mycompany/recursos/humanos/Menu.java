package com.mycompany.recursos.humanos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Dashboard Principal: Centro de Navegación del Sistema.
 * Incluye control de accesos por Rol y estadísticas dinámicas.
 */
public class Menu extends JFrame {

    private final Color azulNavy = new Color(10, 25, 47);
    private final Color azulRoyal = new Color(13, 110, 253);
    private final Color blancoFondo = new Color(248, 249, 250);
    private final Color grisTexto = new Color(108, 117, 125);
    
    private String rolUsuario; 
    private JPanel contentArea; 

    // 🆕 Etiquetas dinámicas para las estadísticas
    private JLabel lblTotalEmpleados;
    private JLabel lblAsistenciasHoy;
    private JLabel lblPlanillaMensual;

    public Menu() {
        this("Gerente_RRHH"); 
    }

    public Menu(String rol) {
        super("Portal Corporativo - Recursos Humanos");
        this.rolUsuario = rol;
        initUI();
        cargarEstadisticasReales(); // 🔌 Inicia la carga de datos al abrir el menú
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(blancoFondo);

        // ==========================================
        // 1. SIDEBAR (Navegación Izquierda)
        // ==========================================
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBackground(azulNavy);
        sidebar.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 20, 10, 20);

        JLabel lblLogo = new JLabel("HR SYSTEM");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(40, 20, 50, 20);
        sidebar.add(lblLogo, gbc);

        gbc.insets = new Insets(5, 10, 5, 10);
        
        sidebar.add(crearBotonMenu("Dashboard", "fa-home"), gbc);
        
        JButton btnListaEmpleados = crearBotonMenu("Empleados", "fa-users");
        btnListaEmpleados.addActionListener(e -> abrirListaEmpleados());
        sidebar.add(btnListaEmpleados, gbc);
        
        JButton btnNuevoEmpleado = crearBotonMenu("Agregar Nuevo Empleado", "fa-user-plus");
        btnNuevoEmpleado.addActionListener(e -> abrirExpediente());
        sidebar.add(btnNuevoEmpleado, gbc);

        JButton btnRelojMarcador = crearBotonMenu("Reloj Marcador", "fa-clock");
        btnRelojMarcador.addActionListener(e -> abrirRelojMarcador());
        sidebar.add(btnRelojMarcador, gbc);
        
        JButton btnPlanillas = crearBotonMenu("Planillas", "fa-file-invoice-dollar");
        if (rolUsuario.equalsIgnoreCase("Supervisor")) {
            btnPlanillas.setEnabled(false); 
            btnPlanillas.setToolTipText("Acceso denegado: Solo disponible para Gerente de RRHH.");
        } else {
            btnPlanillas.addActionListener(e -> abrirPlanillas());
        }
        sidebar.add(btnPlanillas, gbc);
        
        gbc.weighty = 1.0;
        sidebar.add(Box.createVerticalGlue(), gbc);
        gbc.weighty = 0;
        gbc.insets = new Insets(10, 10, 30, 10);
        
        JButton btnCerrarSesion = crearBotonMenu("Cerrar Sesión", "fa-sign-out-alt");
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        sidebar.add(btnCerrarSesion, gbc);

        mainPanel.add(sidebar, BorderLayout.WEST);

        // ==========================================
        // 2. CONTENIDO PRINCIPAL (Derecha)
        // ==========================================
        contentArea = new JPanel(new BorderLayout());
        contentArea.setOpaque(false);
        contentArea.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel topContentPanel = new JPanel(new BorderLayout());
        topContentPanel.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel lblBienvenida = new JLabel("Bienvenido, " + rolUsuario.replace("_", " "));
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblBienvenida.setForeground(azulNavy);
        
        String patronFecha = "EEEE, dd 'de' MMMM 'de' yyyy";
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern(patronFecha, new Locale("es", "ES")));
        fecha = fecha.substring(0, 1).toUpperCase() + fecha.substring(1);
        
        JLabel lblFecha = new JLabel(fecha);
        lblFecha.setForeground(grisTexto);

        header.add(lblBienvenida, BorderLayout.NORTH);
        header.add(lblFecha, BorderLayout.SOUTH);
        topContentPanel.add(header, BorderLayout.NORTH);

        // ==========================================
        // 3. TARJETAS DE ESTADÍSTICAS DINÁMICAS
        // ==========================================
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(30, 0, 30, 0));

        // Inicializamos las etiquetas con texto temporal
        lblTotalEmpleados = new JLabel("...");
        lblAsistenciasHoy = new JLabel("...");
        lblPlanillaMensual = new JLabel("...");

        // Pasamos las etiquetas al generador de tarjetas
        statsPanel.add(crearTarjetaStat("Empleados Totales", lblTotalEmpleados, azulRoyal));
        statsPanel.add(crearTarjetaStat("Asistencias Hoy", lblAsistenciasHoy, new Color(25, 135, 84)));
        statsPanel.add(crearTarjetaStat("Planilla Mensual", lblPlanillaMensual, azulNavy));

        topContentPanel.add(statsPanel, BorderLayout.CENTER);
        contentArea.add(topContentPanel, BorderLayout.NORTH);

        mainPanel.add(contentArea, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    // 🔌 MÉTODO PARA CONSULTAR LA BASE DE DATOS EN SEGUNDO PLANO
   private void cargarEstadisticasReales() {
    new Thread(() -> {
        System.out.println("🔄 [Dashboard] Iniciando carga de datos reales...");
        
        String sqlEmpleados = "SELECT COUNT(*) AS total FROM empleados WHERE estado = '1' OR estado = 'Activo'";
        String sqlAsistencias = "SELECT COUNT(DISTINCT id_empleado) AS asisten FROM marcaciones WHERE DATE(fecha_hora_entrada) = CURDATE()";
        String sqlPlanilla = "SELECT COALESCE(SUM(salario_neto), 0) AS total_mes FROM pagos_nomina WHERE MONTH(fecha_pago) = MONTH(CURDATE()) AND YEAR(fecha_pago) = YEAR(CURDATE())";

        int totalEmpleados = 0;
        int asistenciasHoy = 0;
        double planillaMensual = 0.0;

        try (Connection con = Conexion.obtenerConexion()) {
            System.out.println("✅ [Dashboard] ¡Conexión exitosa a la Base de Datos!");

            // 1. Total de Empleados
            try (PreparedStatement ps = con.prepareStatement(sqlEmpleados); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) totalEmpleados = rs.getInt("total");
                System.out.println("📊 [Dashboard] Empleados activos encontrados: " + totalEmpleados);
            }
            
            // 2. Asistencias Hoy
            try (PreparedStatement ps = con.prepareStatement(sqlAsistencias); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) asistenciasHoy = rs.getInt("asisten");
                System.out.println("📊 [Dashboard] Asistencias encontradas hoy: " + asistenciasHoy);
            }
            
            // 3. Planilla Mensual
            try (PreparedStatement ps = con.prepareStatement(sqlPlanilla); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) planillaMensual = rs.getDouble("total_mes");
                System.out.println("📊 [Dashboard] Total dinero planilla del mes: " + planillaMensual);
            }

        } catch (Exception e) {
            System.err.println("❌ [Dashboard] ERROR CRÍTICO Al consultar estadísticas:");
            e.printStackTrace(); // Esto pintará el error en letras rojas en la consola de NetBeans
        }

        // Variables finales para pasarlas de forma segura a la UI
        final String emp = String.valueOf(totalEmpleados);
        final String asis = String.valueOf(asistenciasHoy);
        final String plan = String.format(Locale.US, "$%,.2f", planillaMensual);

        // Actualizar la pantalla
        SwingUtilities.invokeLater(() -> {
            lblTotalEmpleados.setText(emp);
            lblAsistenciasHoy.setText(asis);
            lblPlanillaMensual.setText(plan);
            System.out.println("✨ [Dashboard] Etiquetas de la interfaz gráfica actualizadas.");
        });
        
    }).start();
}
    private JButton crearBotonMenu(String texto, String icon) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(new Color(200, 200, 200));
        btn.setBackground(azulNavy);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 45));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) {
                    btn.setBackground(new Color(30, 45, 67));
                    btn.setForeground(Color.WHITE);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) {
                    btn.setBackground(azulNavy);
                    btn.setForeground(new Color(200, 200, 200));
                }
            }
        });
        
        return btn;
    }

    // 🔌 AHORA RECIBE DIRECTAMENTE EL JLable EN LUGAR DE UN STRING
    private JPanel crearTarjetaStat(String titulo, JLabel lblValue, Color colorAccent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel lblTitle = new JLabel(titulo);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(grisTexto);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValue.setForeground(colorAccent);
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(15));
        card.add(lblValue);

        return card;
    }

    private void abrirExpediente() {
        ExpedienteForm vistaExpediente = new ExpedienteForm();
        ExpedienteController controlador = new ExpedienteController(vistaExpediente);
        vistaExpediente.setVisible(true);
    }

    private void abrirListaEmpleados() {
        Empleados lista = new Empleados();
        lista.setVisible(true);
    }

    private void abrirRelojMarcador() {
        JFrame contenedorReloj = new JFrame("Estación Biométrica - Reloj Marcador");
        contenedorReloj.setSize(900, 550);
        contenedorReloj.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        contenedorReloj.setLocationRelativeTo(this);
        contenedorReloj.add(new RelojMarcadorPanel());
        contenedorReloj.setVisible(true);
    }

    private void abrirPlanillas() {
        JFrame contenedorPlanilla = new JFrame("Módulo de Planillas y Nómina");
        contenedorPlanilla.setSize(1000, 700);
        contenedorPlanilla.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        contenedorPlanilla.setLocationRelativeTo(this);
        contenedorPlanilla.add(new PlanillaPanel());
        contenedorPlanilla.setVisible(true);
    }

    private void cerrarSesion() {
        int confirmacion = JOptionPane.showConfirmDialog(
                this, 
                "¿Está seguro de que desea cerrar sesión?", 
                "Confirmar Salida", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            Inicio loginForm = new Inicio();
            loginForm.setVisible(true);
            this.dispose(); 
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Menu().setVisible(true);
        });
    }
}