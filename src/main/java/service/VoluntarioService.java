package service;

import dao.VoluntarioDAO;
import modelo.Voluntario;
import java.util.List;
import java.util.Optional;

public class VoluntarioService {
    private final VoluntarioDAO dao = new VoluntarioDAO();

    public List<Voluntario> listarVoluntarios() throws Exception { return dao.listar(); }
    
    public void guardarVoluntario(Voluntario v) throws Exception {
        if (v.getId_voluntario() == 0) dao.insertar(v);
        else dao.actualizar(v);
    }
    
    public void eliminarVoluntario(int id) throws Exception { dao.eliminarLogico(id); }
    
    public Voluntario buscarPorCedula(String cedula) throws Exception {
        return dao.buscarPorCedula(cedula).orElse(null);
    }
    
    public List<Voluntario> buscarPorNombre(String nombre) throws Exception {
        return dao.buscarPorNombre(nombre);
    }
}