package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.dto.DireccionDTO;
import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.mapper.Mapper;
import com.nebulaparfums.nebula_parfums.model.DireccionEnvio;
import com.nebulaparfums.nebula_parfums.repository.IDireccionEnvioRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.IDireccionEnvioService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestionar las direcciones de los usuarios.
 * Provee operaciones de consulta, guardado, eliminación, edición y conversión a DTO.
 */
@Service
@AllArgsConstructor
public class DireccionEnvioService implements IDireccionEnvioService {

    private final IDireccionEnvioRepository direccionEnvioRepository;

    @Override
    public DireccionDTO getDireccionEnvioById(Integer id) {
        return direccionEnvioRepository.findById(id).map(Mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la dirección de envió"));
    }

    /**
     * Guarda una direccion en la base de datos
     * @param direccionEnvio entidad con los datos de la nueva dirección
     * @return Dirección nueva con los datos que fueron guardados
     */
    public DireccionEnvio saveDireccion(DireccionEnvio direccionEnvio) {
        return direccionEnvioRepository.save(direccionEnvio);
    }


    /**
     * Edita la información guardada de una direccion de envío
     * @param id id de la dirección a modificar
     * @param direccionEnvio DirecciónDTO con los datos nuevos para modificar en la db
     * @return DirecciónDTO con los datos actualizados
     */
    @Override
    public DireccionDTO editarDireccionEnvio(Integer id, DireccionDTO direccionEnvio) {

        DireccionEnvio direccion = direccionEnvioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la dirección de envío"));

        direccion.setDireccion(direccion.getDireccion());
        direccion.setCiudad(direccion.getCiudad());
        direccion.setProvincia(direccion.getProvincia());
        direccion.setCodigo_postal(direccion.getCodigo_postal());
        direccion.setTelefono(direccion.getTelefono());

        return  Mapper.toDTO(direccionEnvioRepository.save(direccion));
    }

    /**
     * Borra la direccion de forma física en la base de datos
     * @param id id de la dirección a borrar
     */
    @Override
    public void deleteDireccionEnvioById(Integer id) {
        if (direccionEnvioRepository.existsById(id)) {
            direccionEnvioRepository.deleteById(id);
        }else {
            throw new ResourceNotFoundException("No se encontró la dirección de envío");
        }
    }
}
