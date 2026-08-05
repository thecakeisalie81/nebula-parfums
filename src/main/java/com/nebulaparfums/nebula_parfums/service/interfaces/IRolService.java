package com.nebulaparfums.nebula_parfums.service.interfaces;

import java.util.List;

public interface IRolService {
    public void saveRol(Rol rol);
    public Rol getRolById(Integer id);
    public List<Rol> getRols();
    public void deleteRol(Integer id);
    public void editRol(Rol rol);
}
