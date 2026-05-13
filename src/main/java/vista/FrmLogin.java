/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

/**
 *
 * @author Usuario
 */
import dao.VoluntarioDAO;
import modelo.Persona;
import modelo.Administrador;
import modelo.Voluntario;
import java.awt.*;
import javax.swing.*;

/**
 * Descripción: Pantalla de inicio de sesión (Login) de EcoVida.
 * @author Gabriela Solange Gonzalez Roman
 */
public class FrmLogin extends JFrame {

    private JTextField txtCorreo;
    private JPasswordField txtContrasena;
    private JButton btnEntrar;
    private JButton btnRegistrarse; // El nuevo botón para registrarse

    public FrmLogin() {
        iniciarComponentes();
    }

    private void iniciarComponentes() {
        setTitle("EcoVida - Iniciar Sesión");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar en la pantalla
        setLayout(new BorderLayout());
        setResizable(false);

        // --- PANEL IZQUIERDO (Diseño Verde) ---
        JPanel pnlIzquierdo = new JPanel(new GridBagLayout());
        pnlIzquierdo.setBackground(new Color(23, 93, 62)); // Verde EcoVida
        pnlIzquierdo.setPreferredSize(new Dimension(350, 500));
        
        JLabel lblLogoText = new JLabel("EcoVida");
        lblLogoText.setFont(new Font("Segoe UI", Font.BOLD, 48));
        lblLogoText.setForeground(Color.WHITE);
        
        JLabel lblSubtitulo = new JLabel("Sistema de Gestión Ambiental");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitulo.setForeground(new Color(200, 230, 210));
        
        GridBagConstraints gbcIzq = new GridBagConstraints();
        gbcIzq.gridy = 0; pnlIzquierdo.add(lblLogoText, gbcIzq);
        gbcIzq.gridy = 1; pnlIzquierdo.add(lblSubtitulo, gbcIzq);

        // --- PANEL DERECHO (Formulario Blanco) ---
        JPanel pnlDerecho = new JPanel(new GridBagLayout());
        pnlDerecho.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblBienvenido = new JLabel("Bienvenido de nuevo");
        lblBienvenido.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblBienvenido.setForeground(new Color(50, 50, 50));
        lblBienvenido.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(10, 20, 30, 20); 
        pnlDerecho.add(lblBienvenido, gbc);

        // Correo
        gbc.insets = new Insets(5, 20, 5, 20);
        gbc.gridy = 1;
        JLabel lblCorreo = new JLabel("Correo Electrónico:");
        lblCorreo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlDerecho.add(lblCorreo, gbc);

        gbc.gridy = 2;
        txtCorreo = new JTextField(20);
        txtCorreo.setPreferredSize(new Dimension(250, 35));
        pnlDerecho.add(txtCorreo, gbc);

        // Contraseña
        gbc.gridy = 3;
        JLabel lblContra = new JLabel("Contraseña:");
        lblContra.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlDerecho.add(lblContra, gbc);

        gbc.gridy = 4;
        txtContrasena = new JPasswordField(20);
        txtContrasena.setPreferredSize(new Dimension(250, 35));
        pnlDerecho.add(txtContrasena, gbc);

        // Botón Entrar
        gbc.gridy = 5;
        gbc.insets = new Insets(30, 20, 10, 20);
        btnEntrar = new JButton("Ingresar");
        btnEntrar.setBackground(new Color(34, 166, 89));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEntrar.setPreferredSize(new Dimension(250, 40));
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnlDerecho.add(btnEntrar, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(5, 20, 10, 20);
        btnRegistrarse = new JButton("¿No tienes cuenta? Regístrate aquí");
        btnRegistrarse.setContentAreaFilled(false);
        btnRegistrarse.setBorderPainted(false);
        btnRegistrarse.setForeground(new Color(0, 102, 204));
        btnRegistrarse.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnlDerecho.add(btnRegistrarse, gbc);

        // Ensamblaje
        add(pnlIzquierdo, BorderLayout.WEST);
        add(pnlDerecho, BorderLayout.CENTER);

        btnEntrar.addActionListener(e -> iniciarSesion());
        
        btnRegistrarse.addActionListener(e -> {
            FrmRegistroVoluntario ventanaRegistro = new FrmRegistroVoluntario();
                        ventanaRegistro.setVisible(true);
            
            FrmLogin.this.dispose(); 
        }); 
    }

    private void iniciarSesion() {
        String correo = txtCorreo.getText().trim();
        String contra = new String(txtContrasena.getPassword());

        if (correo.isEmpty() || contra.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, llene todos los campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            VoluntarioDAO dao = new VoluntarioDAO(); 
            // ¡MIRA LA MAGIA DE LA HERENCIA AQUÍ! Devuelve un objeto Persona
            Persona usuarioLogeado = dao.validarLogin(correo, contra);

            // DENTRO DE FrmLogin.java, método iniciarSesion()
           if (usuarioLogeado instanceof Administrador || usuarioLogeado instanceof Voluntario) {
    // AHORA PASAMOS EL USUARIO AL CONSTRUCTOR
            FrmPrincipal pantalla = new FrmPrincipal(usuarioLogeado); 
             pantalla.setVisible(true);
             this.dispose(); 
             } else {
             JOptionPane.showMessageDialog(this, "Credenciales incorrectas");
             }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error conectando a la base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
