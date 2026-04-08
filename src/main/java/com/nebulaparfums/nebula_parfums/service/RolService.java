package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.model.Rol;
import com.nebulaparfums.nebula_parfums.repository.IRolRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.IRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolService implements IRolService {
    @Autowired
    private IRolRepository iRolRepository;


    @Override
    public void saveRol(Rol rol) {
        iRolRepository.save(rol);
    }

    @Override
    public Rol getRolById(Integer id) {
        Rol rol = iRolRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontro el rol"));
        return rol;
    }

    @Override
    public List<Rol> getRols() {
        List<Rol> rols = iRolRepository.findAll();
        return rols;
    }

    @Override
    public void deleteRol(Integer id) {
        if (iRolRepository.existsById(id)) {
            iRolRepository.deleteById(id);
        }else {
            throw new ResourceNotFoundException("No se encontro el rol");
        }
    }

    @Override
    public void editRol(Rol rol) {
        this.saveRol(rol);
    }
}
