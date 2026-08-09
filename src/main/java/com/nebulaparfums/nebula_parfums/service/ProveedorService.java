package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.dto.LogDTO;
import com.nebulaparfums.nebula_parfums.dto.ProveedorDTO;
import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.mapper.Mapper;
import com.nebulaparfums.nebula_parfums.model.LogActividad;
import com.nebulaparfums.nebula_parfums.model.Proveedor;
import com.nebulaparfums.nebula_parfums.model.Usuario;
import com.nebulaparfums.nebula_parfums.repository.IProveedorRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.ILogActividadService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IProveedorService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IUsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para gestionar proveedores.
 * Provee operaciones de consulta, guardado, eliminación, edición y conversión a DTO.
 */
@Service
@AllArgsConstructor
public class ProveedorService implements IProveedorService {

    private final IProveedorRepository iProveedorRepository;
    private final IUsuarioService iUsuarioService;
    private final ILogActividadService iLogActividadService;


    @Override
    public Page<ProveedorDTO> getProveedores(Pageable pageable) {
        return iProveedorRepository.findAll(pageable).map(Mapper::toDTO);
    }

    @Override
    public List<ProveedorDTO> getAllProveedores() {
        return iProveedorRepository.findAll().stream().map(Mapper::toDTO).toList();
    }

    @Override
    public ProveedorDTO getProveedorById(Integer id) {
        return iProveedorRepository.findById(id).map(Mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el proveedor"));
    }

    /**
     * Guarda un proveedor nuevo en la base de datos
     * @param proveedor es el ProveedorDTO con los datos que se le insertaran a la db
     * @param email es el email que se usa para buscar el usuario que realiza la inserción a la db
     *              y registrarlo en los logs de actividad
     * @return ProveedorDTO con los datos que se insertaron
     */
    @Override
    public ProveedorDTO saveProveedor(ProveedorDTO proveedor, String email) {
        Usuario usuario = iUsuarioService.getUsuarioByEmail(email);

        LogDTO logActividad = new LogDTO();
        logActividad.setUsuario_id(usuario.getId_usuario());
        logActividad.setAccion("Registro de proveedor");
        logActividad.setDetalle("Usuario " + usuario.getNombre() + " registro un nuevo proveedor" + proveedor.getNombre());
        logActividad.setFecha_actualizacion(LocalDateTime.now());
        iLogActividadService.saveLogActividad(logActividad);

        Proveedor prov = Proveedor.builder()
                .nombre(proveedor.getNombre())
                .contacto(proveedor.getContacto())
                .email(proveedor.getEmail())
                .telefono(proveedor.getTelefono())
                .build();

        return Mapper.toDTO(iProveedorRepository.save(prov));
    }

    /**
     * Elimina un proveedor físicamente de la db
     * @param id id del usuario que se elimina
     */
    @Override
    public void deleteProveedor(Integer id) {
        if (iProveedorRepository.existsById(id)) {
            iProveedorRepository.deleteById(id);
        }else {
            throw new ResourceNotFoundException("No se encontró el proveedor");
        }
    }

    /**
     * Edita la información guardada en la db que tiene un proveedor
     * @param proveedor Es el ProveedorDto que tiene la información nueva
     * @param email Email del usuario que registra los datos
     * @return ProveedorDto con los datos que se actualizaron
     */
    @Override
    public ProveedorDTO editProveedor(ProveedorDTO proveedor, String email, Integer id) {
        Usuario usuario = iUsuarioService.getUsuarioByEmail(email);
        LogDTO logActividad = new LogDTO();
        logActividad.setUsuario_id(usuario.getId_usuario());
        logActividad.setAccion("Modificación de proveedor");
        logActividad.setDetalle("Usuario " + usuario.getNombre() + " modifico los datos  del proveedor" + proveedor.getNombre());
        logActividad.setFecha_actualizacion(LocalDateTime.now());
        iLogActividadService.saveLogActividad(logActividad);

        Proveedor prov = iProveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el proveedor"));
        prov.setNombre(proveedor.getNombre());
        prov.setContacto(proveedor.getContacto());
        prov.setEmail(proveedor.getEmail());
        prov.setTelefono(proveedor.getTelefono());

        return Mapper.toDTO(iProveedorRepository.save(prov));
    }

    /**
     * Devuelve el número de proveedores que hay registrados en la db
     * @return número total de proveedores
     */
    @Override
    public int totalProveedores() {
        return iProveedorRepository.findAll().size();
    }
}
