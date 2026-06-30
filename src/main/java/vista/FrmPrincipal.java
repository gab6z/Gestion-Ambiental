package vista;

/**
 * Descripción: Ventana principal de la aplicación.
 * Gestiona el contenedor principal usando un CardLayout para alternar 
 * dinámicamente entre los distintos paneles o submódulos del sistema 
 * a través de un menú lateral de navegación.
 * Proyecto: Sistema de Gestión Ambiental (EcoVida)
 * @author Gabriela Solange Gonzalez Roman
 * @version 1.0
 * @since 2026-05-05
 */

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import modelo.Persona;
import modelo.Administrador;
import modelo.Voluntario;

public class FrmPrincipal extends JFrame {

    private final Color COLOR_LATERAL = new Color(23, 93, 62);     
    private final Color COLOR_HOVER = new Color(34, 115, 78);      
    private final Color COLOR_FONDO = new Color(245, 247, 250);    
    private final Color COLOR_CATEGORIA = new Color(130, 180, 150); 

    private JPanel pnlContenido;
    private CardLayout cardLayout;
    private Persona usuarioLogeado;
    private FrmPerfilVoluntario pnlPerfil;

    public FrmPrincipal(Persona usuario) {
        this.usuarioLogeado = usuario;
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("Sistema de Gestión Ambiental - EcoVida");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void inicializarComponentes() {
        JPanel pnlMenuLateral = new JPanel();
        pnlMenuLateral.setBackground(COLOR_LATERAL);
        pnlMenuLateral.setPreferredSize(new Dimension(260, 0));
        pnlMenuLateral.setLayout(new BorderLayout());

        JPanel pnlLogo = new JPanel();
        pnlLogo.setLayout(new BoxLayout(pnlLogo, BoxLayout.Y_AXIS));
        pnlLogo.setBackground(COLOR_LATERAL);
        pnlLogo.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel lblLogoTitulo = new JLabel("EcoVida");
        lblLogoTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblLogoTitulo.setForeground(Color.WHITE);

        JLabel lblLogoSubtitulo = new JLabel("Gestión Ambiental");
        lblLogoSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblLogoSubtitulo.setForeground(new Color(200, 230, 210));

        pnlLogo.add(lblLogoTitulo);
        pnlLogo.add(lblLogoSubtitulo);
        pnlMenuLateral.add(pnlLogo, BorderLayout.NORTH);

        JPanel pnlBotones = new JPanel();
        pnlBotones.setLayout(new BoxLayout(pnlBotones, BoxLayout.Y_AXIS));
        pnlBotones.setBackground(COLOR_LATERAL);
        pnlBotones.setBorder(new EmptyBorder(0, 10, 0, 10));

        

        if (usuarioLogeado instanceof Voluntario) {
            pnlBotones.add(crearEtiquetaCategoria("PRINCIPAL"));
            pnlBotones.add(crearBotonMenu("Dashboard", "DASHBOARD"));
            pnlBotones.add(crearBotonMenu("Mi Perfil", "PERFIL"));
            pnlBotones.add(crearBotonMenu("Ser voluntario","INSCRIBIRSE"));
            

        }

        pnlBotones.add(Box.createVerticalStrut(15));

        if (usuarioLogeado instanceof Administrador) {
            pnlBotones.add(crearEtiquetaCategoria("ADMINISTRACIÓN"));
            pnlBotones.add(crearBotonMenu("Voluntarios", "VOLUNTARIOS"));
            pnlBotones.add(crearBotonMenu("Sectores", "SECTORES"));
            pnlBotones.add(crearBotonMenu("Tareas", "TAREAS"));
            pnlBotones.add(crearBotonMenu("Gestión Ambiental", "GESTION"));
            pnlBotones.add(crearBotonMenu("Iniciativas", "INICIATIVAS"));
            pnlBotones.add(Box.createVerticalStrut(15));
        }

        pnlBotones.add(crearEtiquetaCategoria("SISTEMA"));
        pnlBotones.add(crearBotonMenu("Configuración", "CONFIGURACION"));

        pnlMenuLateral.add(pnlBotones, BorderLayout.CENTER);

        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setBackground(COLOR_LATERAL);
        pnlFooter.setBorder(new EmptyBorder(10, 10, 20, 10));

        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrarSesion.setForeground(new Color(200, 230, 210));
        btnCerrarSesion.setBackground(COLOR_LATERAL);
        btnCerrarSesion.setBorderPainted(false);
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.setContentAreaFilled(false);
        btnCerrarSesion.setOpaque(true);
        btnCerrarSesion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrarSesion.setHorizontalAlignment(SwingConstants.LEFT);
        btnCerrarSesion.setBorder(new EmptyBorder(12, 15, 12, 15));

        btnCerrarSesion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCerrarSesion.setBackground(new Color(220, 53, 69));
                btnCerrarSesion.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnCerrarSesion.setBackground(COLOR_LATERAL);
                btnCerrarSesion.setForeground(new Color(255, 120, 120));
            }
        });

        btnCerrarSesion.addActionListener(e -> {
            int opcion = JOptionPane.showConfirmDialog(this, "¿Cerrar sesión?", "Aviso", JOptionPane.YES_NO_OPTION);
            if (opcion == JOptionPane.YES_OPTION) {
                new FrmLogin().setVisible(true);
                this.dispose();
            }
        });

        pnlFooter.add(btnCerrarSesion, BorderLayout.CENTER);
        pnlMenuLateral.add(pnlFooter, BorderLayout.SOUTH);
        add(pnlMenuLateral, BorderLayout.WEST);

        cardLayout = new CardLayout();
        pnlContenido = new JPanel(cardLayout);
        pnlContenido.setBackground(COLOR_FONDO);

        pnlContenido.add(crearPanelBienvenida(), "BIENVENIDA");

        if (usuarioLogeado instanceof Administrador) {
            instanciarModulosAdmin();
        }else if (usuarioLogeado instanceof Voluntario) {
            instanciarModulosVoluntario(); 
            
            this.pnlPerfil = new FrmPerfilVoluntario((Voluntario) usuarioLogeado);
            pnlContenido.add(pnlPerfil, "PERFIL");
            
            FrmExplorarIniciativas pnlExplorar = new FrmExplorarIniciativas((Voluntario) usuarioLogeado);
            pnlContenido.add(pnlExplorar, "INSCRIBIRSE");
        }

        add(pnlContenido, BorderLayout.CENTER);
        cardLayout.show(pnlContenido, "BIENVENIDA");
    }

    private void instanciarModulosAdmin() {
        SectoresPnl pnlSectores = new SectoresPnl();
        new controlador.SectorControlador(pnlSectores);
        pnlContenido.add(pnlSectores, "SECTORES");

        TareasPnl pnlTareas = new TareasPnl();
        new controlador.TareaControlador(new modelo.Tarea(), new service.TareaService(), pnlTareas).iniciar();
        pnlContenido.add(pnlTareas, "TAREAS");

        vista.VoluntariosPnl pnlVoluntarios = new vista.VoluntariosPnl();
        new controlador.VoluntarioControlador(pnlVoluntarios);
        pnlContenido.add(pnlVoluntarios, "VOLUNTARIOS");

        vista.GestionAmbiental_panel pnlGestion = new vista.GestionAmbiental_panel();
        new controlador.GestionControlador(pnlGestion);
        pnlContenido.add(pnlGestion, "GESTION");

        IniciativaPnl pnlIniciativas = new IniciativaPnl();
        new controlador.IniciativaControlador(pnlIniciativas);
        pnlContenido.add(pnlIniciativas, "INICIATIVAS");
    }

    private JPanel crearPanelBienvenida() {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBackground(COLOR_FONDO);
        
        String mensaje = "Bienvenida, " + usuarioLogeado.getNombres_completos();
        if (usuarioLogeado instanceof Administrador) {
            mensaje = "Bienvenido Administrador, " + usuarioLogeado.getNombres_completos();
        }

        JLabel lblBienvenida = new JLabel(mensaje);
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblBienvenida.setForeground(new Color(150, 160, 170));

        pnl.add(lblBienvenida);
        return pnl;
    }
    
    private void instanciarModulosVoluntario() {
       
        DashboardVoluntarioPnl pnlDashVol = new DashboardVoluntarioPnl();

        
        new controlador.DashboardVoluntarioControlador(pnlDashVol, (Voluntario) usuarioLogeado);

        pnlContenido.add(pnlDashVol, "DASHBOARD");

        // También puedes instanciar aquí el panel de "Mi Perfil"
        // pnlContenido.add(new MiPerfilPnl((Voluntario) usuarioLogeado), "PERFIL");
    }

    private JLabel crearEtiquetaCategoria(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(COLOR_CATEGORIA);
        lbl.setBorder(new EmptyBorder(10, 15, 5, 0)); 
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JButton crearBotonMenu(String texto, String nombreCarta) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(COLOR_LATERAL); 
        btn.setBorderPainted(false);    
        btn.setFocusPainted(false);      
        btn.setContentAreaFilled(false);  
        btn.setOpaque(true);              
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(12, 15, 12, 15));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); 
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(COLOR_HOVER); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(COLOR_LATERAL); }
        });

        btn.addActionListener(e -> {
            try {
                if ("PERFIL".equals(nombreCarta) && pnlPerfil != null) {
                    pnlPerfil.cargarIniciativas();
                }
                cardLayout.show(pnlContenido, nombreCarta);
            } catch (Exception ex) {
                System.out.println("Pantalla no programada aún: " + nombreCarta);
            }
        });
        return btn;
    }
}