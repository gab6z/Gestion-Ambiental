package vista;

import vista.FrmPrincipal; // Importamos la ventana del login

public class Main {

    public static void main(String[] args) {
        
        // Usamos EventQueue (Buenas prácticas en Java Swing para evitar cuelgues visuales)
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Instanciamos el Login y lo hacemos visible
                FrmLogin ventanaLogin = new FrmLogin();
                ventanaLogin.setVisible(true);
            }
        });
        
    }
}
