package com.nebulaparfums.nebula_parfums.service.interfaces;

import com.nebulaparfums.nebula_parfums.dto.OrdenDTO;
import com.nebulaparfums.nebula_parfums.model.Orden;
import org.aspectj.weaver.ast.Or;

import java.util.List;

public interface IOrdenService {
    public List<Orden> getOrdenes();
    public Orden getOrdenById(Integer ordenId);
    public void saveOrden(Orden orden);
    public void deleteOrden(Integer ordenId);
    public void editOrden(Orden orden);
    public void crearOrden(OrdenDTO orden);
    public List<Orden> getUltimasOrdenesPendiente();
}
