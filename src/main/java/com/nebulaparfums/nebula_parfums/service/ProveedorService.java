package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.model.LogActividad;
import com.nebulaparfums.nebula_parfums.model.Proveedor;
import com.nebulaparfums.nebula_parfums.model.Usuario;
import com.nebulaparfums.nebula_parfums.repository.IProveedorRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.ILogActividadService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IProveedorService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProveedorService implements IProveedorService {
    @Autowired
    private IProveedorRepository iProveedorRepository;

    @Autowired
    private IUsuarioService iUsuarioService;

    @Autowired
    private ILogActividadService iLogActividadService;


    @Override
    public Page<Proveedor> getProveedores(Pageable pageable) {
        return iProveedorRepository.findAll(pageable);
    }

    @Override
    public List<Proveedor> getAllProveedores() {
        return iProveedorRepository.findAll();
    }

    @Override
    public Proveedor getProveedorById(Integer id) {
        Proveedor proveedor = iProveedorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontro el proveedor"));
        return proveedor;
    }

    @Override
    public void saveProveedor(Proveedor proveedor) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = iUsuarioService.getUsuarioByEmail(username);

        LogActividad logActividad = new LogActividad();
        logActividad.setUsuario(usuario);
        logActividad.setAccion("Registro de proveedor");
        logActividad.setDetalle("Usuario " + usuario.getNombre() + " registro un nuevo proveedor" + proveedor.getNombre());
        logActividad.setFecha_actualizacion(LocalDateTime.now());

        iLogActividadService.saveLogActividad(logActividad);

        iProveedorRepository.save(proveedor);
    }

    @Override
    public void deleteProveedor(Integer id) {
        if (iProveedorRepository.existsById(id)) {
            iProveedorRepository.deleteById(id);
        }else {
            throw new ResourceNotFoundException("No se encontro el proveedor");
        }
    }

    @Override
    public void editProveedor(Proveedor proveedor) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = iUsuarioService.getUsuarioByEmail(username);

        LogActividad logActividad = new LogActividad();
        logActividad.setUsuario(usuario);
        logActividad.setAccion("Modificacion de proveedor");
        logActividad.setDetalle("Usuario " + usuario.getNombre() + " modifico los datos  del proveedor" + proveedor.getNombre());
        logActividad.setFecha_actualizacion(LocalDateTime.now());

        iLogActividadService.saveLogActividad(logActividad);

        iProveedorRepository.save(proveedor);
    }

    @Override
    public int totalProveedores() {
        return iProveedorRepository.findAll().size();
    }
}
