package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.model.DireccionEnvio;
import com.nebulaparfums.nebula_parfums.model.Usuario;
import com.nebulaparfums.nebula_parfums.repository.IDireccionEnvioRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.IDireccionEnvioService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DireccionEnvioService implements IDireccionEnvioService {

    @Autowired
    private IDireccionEnvioRepository direccionEnvioRepository;

    @Autowired
    private IUsuarioService usuarioService;

    @Override
    public DireccionEnvio getDireccionEnvioById(Integer id) {
        DireccionEnvio direccionEnvio = direccionEnvioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontro la direccion de envio"));;
        return direccionEnvio;
    }

    @Override
    public void saveDireccionEnvio(DireccionEnvio direccionEnvio) {
        direccionEnvioRepository.save(direccionEnvio);
    }

    @Override
    public void deleteDireccionEnvioById(Integer id) {
        if (direccionEnvioRepository.existsById(id)) {
            direccionEnvioRepository.deleteById(id);
        }else {
            throw new ResourceNotFoundException("No se encontro la direccion de envio");
        }
    }

    @Override
    public void editDireccionEnvio(DireccionEnvio direccionEnvio) {
        this.saveDireccionEnvio(direccionEnvio);
    }

    @Override
    public void editarDireccionPorEmail(String email, DireccionEnvio datosNuevaDireccion) {
        Usuario usuario = usuarioService.getUsuarioByEmail(email);

        DireccionEnvio direccionActual = usuario.getDireccionEnvio();

        if (direccionActual == null) {
            DireccionEnvio nuevaDireccion = new DireccionEnvio();
            nuevaDireccion.setDireccion(datosNuevaDireccion.getDireccion());
            nuevaDireccion.setCiudad(datosNuevaDireccion.getCiudad());
            nuevaDireccion.setProvincia(datosNuevaDireccion.getProvincia());
            nuevaDireccion.setCodigo_postal(datosNuevaDireccion.getCodigo_postal());
            nuevaDireccion.setTelefono(datosNuevaDireccion.getTelefono());

            DireccionEnvio guardada = direccionEnvioRepository.save(nuevaDireccion);
            usuario.setDireccionEnvio(guardada);
            usuarioService.saveUsuario(usuario);
            return;
        }

        direccionActual.setDireccion(datosNuevaDireccion.getDireccion());
        direccionActual.setCiudad(datosNuevaDireccion.getCiudad());
        direccionActual.setProvincia(datosNuevaDireccion.getProvincia());
        direccionActual.setCodigo_postal(datosNuevaDireccion.getCodigo_postal());
        direccionActual.setTelefono(datosNuevaDireccion.getTelefono());

        direccionEnvioRepository.save(direccionActual);
    }
}
