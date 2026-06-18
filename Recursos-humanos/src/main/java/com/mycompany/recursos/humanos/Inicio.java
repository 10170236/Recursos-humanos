package com.mycompany.recursos.humanos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Pantalla de Inicio de Sesión (Control de Acceso)
 * Cumple con el Requerimiento No Funcional de Privacidad conectando a MySQL.
 */
public class Inicio extends JFrame {

    // Paleta de colores unificada
    private final Color azulNavy = new Color(10, 25, 47);
    private final Color azulRoyal = new Color(13, 110, 253);
    private final Color blancoFondo = new Color(248, 249, 250);
    private final Color grisTexto = new Color(108, 117, 125);

    private JTextField txtUsuario;
    private JPasswordField txtPassword;

    public Inicio() {
        super("HR System - Control de Acceso");
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null); // Centrar en pantalla
        setResizable(false);

        // Panel Principal (Fondo Navy)
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(azulNavy);

        // Tarjeta Blanca (El formulario en sí)
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(255, 255, 255, 50), 1, true),
                new EmptyBorder(40, 40, 40, 40)
        ));
        cardPanel.setPreferredSize(new Dimension(380, 450));

        // Título / Logo
        JLabel lblLogo = new JLabel("HR SYSTEM");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblLogo.setForeground(azulNavy);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Ingrese sus credenciales");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(grisTexto);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Campos de Texto
        JLabel lblUsuario = new JLabel("Usuario");
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUsuario.setForeground(azulNavy);
        
        txtUsuario = new JTextField();
        txtUsuario.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        JLabel lblPassword = new JLabel("Contraseña");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPassword.setForeground(azulNavy);
        
        txtPassword = new JPasswordField();
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        // Botón de Ingreso
        JButton btnIngresar = new JButton("Iniciar Sesión");
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setBackground(azulRoyal);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setBorderPainted(false);
        btnIngresar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnIngresar.addActionListener((ActionEvent e) -> validarCredenciales());

        // Ensamblar la tarjeta
        cardPanel.add(lblLogo);
        cardPanel.add(Box.createVerticalStrut(10));
        cardPanel.add(lblSubtitulo);
        cardPanel.add(Box.createVerticalStrut(40));
        
        cardPanel.add(alinearIzquierda(lblUsuario));
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(txtUsuario);
        cardPanel.add(Box.createVerticalStrut(20));
        
        cardPanel.add(alinearIzquierda(lblPassword));
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(txtPassword);
        cardPanel.add(Box.createVerticalStrut(40));
        
        cardPanel.add(btnIngresar);

        // Añadir tarjeta al panel principal
        mainPanel.add(cardPanel);
        setContentPane(mainPanel);
    }

    // Método auxiliar para que las etiquetas queden alineadas a la izquierda
    private JPanel alinearIzquierda(Component c) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        panel.add(c);
        return panel;
    }

    // Lógica de Autenticación conectada a XAMPP
    private void validarCredenciales() {
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        // Validar que los campos no estén vacíos
        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Credenciales de tu servidor XAMPP (por defecto root sin contraseña)
        String url = "jdbc:mysql://localhost:3306/recursos_humanos"; 
        String userDB = "root";
        String passDB = "";

        // Consulta SQL para buscar el usuario
        String query = "SELECT rol FROM Usuarios WHERE usuario = ? AND contrasena = ?";

        try (Connection conn = DriverManager.getConnection(url, userDB, passDB);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, usuario);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // El usuario existe, extraemos su rol
                String rolObtenido = rs.getString("rol");
                
                JOptionPane.showMessageDialog(this, "Bienvenido: " + rolObtenido, "Acceso Concedido", JOptionPane.INFORMATION_MESSAGE);
                abrirDashboard(rolObtenido);
                
            } else {
                // No se encontró coincidencia en la base de datos
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas o usuario inexistente.", "Error de Acceso", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error crítico de conexión a la base de datos:\n" + ex.getMessage(), "Falla de Sistema", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirDashboard(String rol) {
    // Carga la pantalla principal pasándole el rol de la base de datos
    Menu dashboard = new Menu(rol);
    dashboard.setVisible(true);
    this.dispose(); // Destruye la ventana de login
}
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Inicio().setVisible(true);
        });
    }
}