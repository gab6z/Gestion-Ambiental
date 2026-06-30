package vista;

import vista.FrmPrincipal; 

public class Main {

    public static void main(String[] args) {
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                FrmLogin ventanaLogin = new FrmLogin();
                ventanaLogin.setVisible(true);
            }
        });
        
    }
}
