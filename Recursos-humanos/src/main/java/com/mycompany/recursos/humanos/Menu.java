package com.mycompany.recursos.humanos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Dashboard Principal: Centro de Navegación del Sistema.
 * Incluye control de accesos por Rol y funcionalidad de Cerrar Sesión.
 */
public class Menu extends JFrame {

    private final Color azulNavy = new Color(10, 25, 47);
    private final Color azulRoyal = new Color(13, 110, 253);
    private final Color blancoFondo = new Color(248, 249, 250);
    private final Color grisTexto = new Color(108, 117, 125);
    
    private String rolUsuario; // Guarda el rol del usuario conectado

    // Constructor predeterminado (por si se ejecuta directo)
    public Menu() {
        this("Gerente_RRHH"); 
    }

    // Constructor principal que recibe el rol desde el Login (Inicio.java)
    public Menu(String rol) {
        super("Portal Corporativo - Recursos Humanos");
        this.rolUsuario = rol;
        initUI();
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
        
        // Botones del Menú
        sidebar.add(crearBotonMenu("Dashboard", "fa-home"), gbc);
        
        // 1. NUEVO BOTÓN: Lista General de Empleados
        JButton btnListaEmpleados = crearBotonMenu("Empleados", "fa-users");
        btnListaEmpleados.addActionListener(e -> abrirListaEmpleados());
        sidebar.add(btnListaEmpleados, gbc);
        
        // 2. BOTÓN MODIFICADO: Agregar Nuevo Empleado (Antes "Expedientes")
        JButton btnNuevoEmpleado = crearBotonMenu("Agregar Nuevo Empleado", "fa-user-plus");
        btnNuevoEmpleado.addActionListener(e -> abrirExpediente());
        sidebar.add(btnNuevoEmpleado, gbc);

        sidebar.add(crearBotonMenu("Reloj Marcador", "fa-clock"), gbc);
        
        // Botón de Planillas con Control de Privacidad por Rol
        JButton btnPlanillas = crearBotonMenu("Planillas", "fa-file-invoice-dollar");
        if (rolUsuario.equalsIgnoreCase("Supervisor")) {
            btnPlanillas.setEnabled(false); // Deshabilitar si es supervisor
            btnPlanillas.setToolTipText("Acceso denegado: Solo disponible para Gerente de RRHH.");
        }
        sidebar.add(btnPlanillas, gbc);
        
        // Pegar el botón de cerrar sesión en la parte inferior
        gbc.weighty = 1.0;
        sidebar.add(Box.createVerticalGlue(), gbc);
        gbc.weighty = 0;
        gbc.insets = new Insets(10, 10, 30, 10);
        
        // FUNCIONALIDAD DE CERRAR SESIÓN
        JButton btnCerrarSesion = crearBotonMenu("Cerrar Sesión", "fa-sign-out-alt");
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        sidebar.add(btnCerrarSesion, gbc);

        mainPanel.add(sidebar, BorderLayout.WEST);

        // ==========================================
        // 2. CONTENIDO PRINCIPAL (Derecha)
        // ==========================================
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setOpaque(false);
        contentArea.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel topContentPanel = new JPanel(new BorderLayout());
        topContentPanel.setOpaque(false);

        // Header del Dashboard
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        // Mensaje de bienvenida dinámico según el rol
        JLabel lblBienvenida = new JLabel("Bienvenido, " + rolUsuario.replace("_", " "));
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblBienvenida.setForeground(azulNavy);
        
        // Formato de Fecha
        String patronFecha = "EEEE, dd 'de' MMMM 'de' yyyy";
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern(patronFecha, new Locale("es", "ES")));
        fecha = fecha.substring(0, 1).toUpperCase() + fecha.substring(1);
        
        JLabel lblFecha = new JLabel(fecha);
        lblFecha.setForeground(grisTexto);

        header.add(lblBienvenida, BorderLayout.NORTH);
        header.add(lblFecha, BorderLayout.SOUTH);
        topContentPanel.add(header, BorderLayout.NORTH);

        // Panel de Estadísticas (Tarjetas Rápidas)
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(30, 0, 30, 0));

        statsPanel.add(crearTarjetaStat("Empleados Totales", "24", azulRoyal));
        statsPanel.add(crearTarjetaStat("Asistencias Hoy", "18", new Color(25, 135, 84)));
        statsPanel.add(crearTarjetaStat("Planilla Mensual", "$12,450.00", azulNavy));

        topContentPanel.add(statsPanel, BorderLayout.CENTER);
        contentArea.add(topContentPanel, BorderLayout.NORTH);

        mainPanel.add(contentArea, BorderLayout.CENTER);
        setContentPane(mainPanel);
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

    private JPanel crearTarjetaStat(String titulo, String valor, Color colorAccent) {
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

        JLabel lblValue = new JLabel(valor);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValue.setForeground(colorAccent);
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(15));
        card.add(lblValue);

        return card;
    }

    // =========================================================================
    // MODIFICACIÓN: AQUÍ SE AÑADIÓ LA CONEXIÓN DEL PATRÓN MVC AL EXPEDIENTE
    // =========================================================================
    private void abrirExpediente() {
        // 1. Instanciamos la Vista (Formulario Premium)
        ExpedienteForm vistaExpediente = new ExpedienteForm();
        // 2. Instanciamos el Controlador y le conectamos la vista
        ExpedienteController controlador = new ExpedienteController(vistaExpediente);
        // 3. Mostramos la ventana en pantalla
        vistaExpediente.setVisible(true);
    }

    // =========================================================================
    // NUEVO MÉTODO: ABRIR LA LISTA DE EMPLEADOS
    // =========================================================================
    private void abrirListaEmpleados() {
        // Descomentamos la llamada a tu clase Empleados
        Empleados lista = new Empleados();
        lista.setVisible(true);
    }
    // LÓGICA DE CERRAR SESIÓN
    private void cerrarSesion() {
        int confirmacion = JOptionPane.showConfirmDialog(
                this, 
                "¿Está seguro de que desea cerrar sesión?", 
                "Confirmar Salida", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            // Regresa a la ventana de login (Inicio) y destruye el menú
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