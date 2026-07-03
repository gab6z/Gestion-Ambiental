/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

/**
 *
 * @author EDUARDO CHAVEZ
 * @version 1.0
 * @since 2026-05-07
 */


import dao.VoluntarioDAO;
import modelo.Voluntario;
import java.awt.*;
import javax.swing.*;

public class FrmRegistroVoluntario extends JFrame {

    private JTextField txtCedula, txtNombres, txtCorreo, txtTelefono, txtDisponibilidad;
    private JPasswordField txtContrasena; // ¡NUEVO CAMPO!
    private JComboBox<String> cbxGenero;
    private JTextArea txtHabilidades;
    private JButton btnRegistrar, btnCancelar;

    public FrmRegistroVoluntario() {
        iniciarComponentes();
    }

    private void iniciarComponentes() {
        setTitle("Registro de Nuevo Voluntario");
        setSize(500, 700); // Lo hice un poquito más alto para que entre la contraseña
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Header
        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(new Color(23, 93, 62));
        pnlHeader.setPreferredSize(new Dimension(500, 60));
        JLabel lblTitulo = new JLabel("Únete a EcoVida");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        pnlHeader.add(lblTitulo);
        add(pnlHeader, BorderLayout.NORTH);

        // Form
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 20, 5, 20);
        gbc.weightx = 1.0;

        int fila = 0;
        agregarCampo(pnlForm, gbc, "Cédula:", txtCedula = new JTextField(), fila++);
        agregarCampo(pnlForm, gbc, "Nombres Completos:", txtNombres = new JTextField(), fila++);
        agregarCampo(pnlForm, gbc, "Correo Electrónico:", txtCorreo = new JTextField(), fila++);
        
        // --- AGREGAMOS EL CAMPO DE CONTRASEÑA VISUALMENTE ---
        gbc.gridy = fila * 2;
        pnlForm.add(new JLabel("Contraseña:"), gbc);
        gbc.gridy = fila * 2 + 1;
        txtContrasena = new JPasswordField();
        pnlForm.add(txtContrasena, gbc);
        fila++;

        agregarCampo(pnlForm, gbc, "Teléfono:", txtTelefono = new JTextField(), fila++);
        
        gbc.gridy = fila++;
        pnlForm.add(new JLabel("Género:"), gbc);
        gbc.gridy = fila++;
        cbxGenero = new JComboBox<>(new String[]{"Seleccionar...", "Masculino", "Femenino"});
        pnlForm.add(cbxGenero, gbc);

        agregarCampo(pnlForm, gbc, "Disponibilidad (Ej: Fines de semana):", txtDisponibilidad = new JTextField(), fila++);

        gbc.gridy = fila++;
        pnlForm.add(new JLabel("Habilidades:"), gbc);
        gbc.gridy = fila++;
        txtHabilidades = new JTextArea(3, 20);
        txtHabilidades.setLineWrap(true);
        pnlForm.add(new JScrollPane(txtHabilidades), gbc);

        add(pnlForm, BorderLayout.CENTER);

        // Buttons
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        pnlBotones.setBackground(Color.WHITE);
        
        btnRegistrar = new JButton("Completar Registro");
        btnRegistrar.setBackground(new Color(34, 166, 89));
        btnRegistrar.setForeground(Color.WHITE);
        
        btnCancelar = new JButton("Volver al Login");
        btnCancelar.setBackground(Color.GRAY);
        btnCancelar.setForeground(Color.WHITE);

        pnlBotones.add(btnCancelar);
        pnlBotones.add(btnRegistrar);
        add(pnlBotones, BorderLayout.SOUTH);

        // Events
        btnCancelar.addActionListener(e -> {
            new FrmLogin().setVisible(true);
            this.dispose();
        });

        btnRegistrar.addActionListener(e -> registrarVoluntario());
    }

    private void agregarCampo(JPanel pnl, GridBagConstraints gbc, String label, JTextField txt, int fila) {
        gbc.gridy = fila * 2;
        pnl.add(new JLabel(label), gbc);
        gbc.gridy = fila * 2 + 1;
        pnl.add(txt, gbc);
    }

    private void registrarVoluntario() {
        try {
            String cedula = txtCedula.getText().trim();
            if (cedula.isEmpty()) {
                throw new IllegalArgumentException("El campo Cédula no puede estar vacío.");
            }
            if (cedula.length() > 10) {
                throw new IllegalArgumentException("La cédula no puede tener más de 10 dígitos.");
            }
           
            String contra = new String(txtContrasena.getPassword());
            if (contra.trim().isEmpty()) {
                throw new IllegalArgumentException("La contraseña no puede estar vacía.");
            }

            Voluntario v = new Voluntario();
            v.setCedula(cedula);
            v.setNombres_completos(txtNombres.getText());
            v.setCorreo(txtCorreo.getText());
            v.setContrasena(contra); 
            v.setTelefono(txtTelefono.getText());
            v.setGenero(cbxGenero.getSelectedItem().toString());
            v.setDisponibilidad_dias(txtDisponibilidad.getText());
            v.setHabilidades(txtHabilidades.getText());
            v.setEstado("Activo"); 

            VoluntarioDAO dao = new VoluntarioDAO();
            dao.insertar(v);

            JOptionPane.showMessageDialog(this, 
                "¡Registro exitoso!\nYa puedes iniciar sesión con tu correo y contraseña.", 
                "Bienvenido a EcoVida", JOptionPane.INFORMATION_MESSAGE);
                
            new FrmLogin().setVisible(true);
            this.dispose();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}