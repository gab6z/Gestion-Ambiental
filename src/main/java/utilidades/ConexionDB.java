package utilidades;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase utilitaria encargada de gestionar la conexión
 * con la base de datos MySQL del sistema EcoVida.

 * @author Gabriela Solange Gonzalez Roman
 * @version 1.0
 * @since 2026-05-05
 */
public class ConexionDB {
    
    //Configuración de datos
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/ecovida?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root"; 
    private static final String PASSWORD = "";
 
    
/**
 * Carga el driver JDBC de MySQL al iniciar la clase.
 */
    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println(" No se encontró el driver de MySQL: " + e.getMessage());
        }
    }


/**
 * Obtiene una conexión activa con la base de datos EcoVida.
 *
 * @return objeto {@link Connection} conectado a la base de datos.
 * @throws SQLException si ocurre un error al establecer la conexión.
 */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
 

/**
 * Cierra una conexión con la base de datos.
 *
 * @param cn objeto {@link Connection} a cerrar.
 */
    public static void cerrar(Connection cn) {
        if (cn != null) {
            try { cn.close(); } catch (SQLException e) {
                System.err.println("Error al cerrar Connection: " + e.getMessage());
            }
        }
    }
 
    
/**
 * Cierra un objeto {@link java.sql.PreparedStatement}.
 *
 * @param ps sentencia preparada a cerrar.
 */
    public static void cerrar(java.sql.PreparedStatement ps) {
        if (ps != null) {
            try { ps.close(); } catch (SQLException e) {
                System.err.println("Error al cerrar PreparedStatement: " + e.getMessage());
            }
        }
    }
 /**
 * Cierra un objeto {@link java.sql.ResultSet}.
 *
 * @param rs resultado de consulta a cerrar.
 */
    public static void cerrar(java.sql.ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException e) {
                System.err.println("Error al cerrar ResultSet: " + e.getMessage());
            }
        }
    }

    
/**
 * Cierra de forma ordenada un {@link java.sql.ResultSet},
 * un {@link java.sql.PreparedStatement} y una
 * {@link Connection}.

 *
 * @param rs resultado de consulta a cerrar.
 * @param ps sentencia preparada a cerrar.
 * @param cn conexión a la base de datos a cerrar.
 */
    public static void cerrar(java.sql.ResultSet rs,
                               java.sql.PreparedStatement ps,
                               Connection cn) {
        cerrar(rs);
        cerrar(ps);
        cerrar(cn);
    }
}