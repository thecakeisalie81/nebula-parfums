package com.nebulaparfums.nebula_parfums.service;

import com.nebulaparfums.nebula_parfums.dto.ProductoDTO;
import com.nebulaparfums.nebula_parfums.exception.QuantityBelowZeroException;
import com.nebulaparfums.nebula_parfums.exception.ResourceNotFoundException;
import com.nebulaparfums.nebula_parfums.mapper.Mapper;
import com.nebulaparfums.nebula_parfums.model.Producto;
import com.nebulaparfums.nebula_parfums.repository.ICategoriaRepository;
import com.nebulaparfums.nebula_parfums.repository.IProductoRepository;
import com.nebulaparfums.nebula_parfums.repository.IProveedorRepository;
import com.nebulaparfums.nebula_parfums.service.interfaces.IProductoService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Servicio para gestionar productos.
 * Provee operaciones de consulta, guardado, eliminación, edición y conversión a DTO.
 */
@Service
@AllArgsConstructor
public class ProductoService implements IProductoService {

    private final IProductoRepository iProductoRepository;
    private final IProveedorRepository iProveedorRepository;
    private final ICategoriaRepository iCategoriaRepository;


    @Override
    public List<ProductoDTO> getProductos() {
        return iProductoRepository.findAll().stream().map(Mapper::toDTO).toList();
    }

    @Override
    public ProductoDTO getProductoById(Integer id) {
        return iProductoRepository.findById(id).map(Mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el producto"));
    }

    @Override
    public Page<ProductoDTO> getProductosBajoStock(Pageable pageable) {
        return iProductoRepository.findProductosConStockBajo(pageable).map(Mapper::toDTO);
    }

    /**
     * Obtiene una página de productos filtrados y convertidos a DTO.
     * <p>
     * Los filtros aplicables son opcionales: si un parámetro es nulo o vacío,
     * no se aplica en la consulta. La paginación se conserva en el resultado.
     * </p>
     *
     * @param pageable     información de paginación (número de página, tamaño, orden)
     * @param nombre       nombre parcial del producto para búsqueda (puede ser null o vacío)
     * @param idCategoria  identificador de la categoría (puede ser null)
     * @param idProveedor  identificador del proveedor (puede ser null)
     * @param estadoStock  estado del stock (ej. "DISPONIBLE", "AGOTADO"), puede ser null o vacío
     * @param precioMinimo precio mínimo para filtrar (puede ser null)
     * @param precioMaximo precio máximo para filtrar (puede ser null)
     * @param disponible   indicador de disponibilidad (ej. 1 = disponible, 0 = no disponible), puede ser null
     * @return Page con ProductoDTO filtrados según los criterios y metadatos de paginación
     */
    @Override
    public Page<ProductoDTO> getProductosFiltrados(
            Pageable pageable,
            String nombre,
            Integer idCategoria,
            Integer idProveedor,
            String estadoStock,
            Integer precioMinimo,
            Integer precioMaximo,
            Integer disponible
    ) {
        if (nombre != null && nombre.isBlank()) {
            nombre = null;
        }

        if (estadoStock != null && estadoStock.isBlank()) {
            estadoStock = null;
        }

        return iProductoRepository.filtrarProductos(
                pageable,
                nombre,
                idCategoria,
                idProveedor,
                estadoStock,
                precioMinimo,
                precioMaximo,
                disponible
        ).map(Mapper::toDTO);
    }


    /**
     * Edita la información guardada de un producto
     * @param id id del producto a modificar
     * @param producto ProductoDTO con la nueva información del producto
     * @return ProductoDTO con la información actualiza
     */
    @Override
    public ProductoDTO editProducto(Integer id, ProductoDTO producto) {
        if (producto.getStock_actual() < 0){
            throw new QuantityBelowZeroException("No hay suficientes unidades   ");
        }

        Producto prod = iProductoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el producto"));
        prod.setNombre(producto.getNombre());
        prod.setDescripcion(producto.getDescripcion());
        prod.setPrecio(producto.getPrecio());
        prod.setStock_actual(producto.getStock_actual());
        prod.setStock_minimo(producto.getStock_minimo());
        prod.setProveedor(iProveedorRepository.findById(producto.getProveedor())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el proveedor")));
        prod.setCategoria(iCategoriaRepository.findById(producto.getCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el categoria")));
        prod.setImagen(producto.getImagen());

        return Mapper.toDTO(iProductoRepository.save(prod));
    }

    /**
     * Elimina un producto físicamente de la db
     * @param id id del producto que se eliminara
     */
    @Override
    public void deleteProducto(Integer id) {
        if (iProductoRepository.existsById(id)){
            iProductoRepository.deleteById(id);
        }else {
            throw new ResourceNotFoundException("No se encontró el producto");
        }
    }

    /**
     * Guarda un nuevo producto en la db
     * @param producto ProductoDto con la información que lleva el nuevo producto
     * @return ProductoDTO con la información que se guardó en la db
     */
    @Override
    public ProductoDTO saveProducto(ProductoDTO producto) {
        Producto prod = Producto.builder()
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .stock_actual(producto.getStock_actual())
                .stock_minimo(producto.getStock_minimo())
                .proveedor(iProveedorRepository.findById(producto.getProveedor())
                        .orElseThrow(() -> new ResourceNotFoundException("No se encontró el proveedor")))
                .categoria(iCategoriaRepository.findById(producto.getCategoria())
                        .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoria")))
                .imagen(producto.getImagen())
                .build();

        return Mapper.toDTO(iProductoRepository.save(prod));
    }

    /**
     * Obtiene la cantidad de productos con menor inventario que el campo de stock minimo
     * @return Número de productos que tienen bajo inventario
     */
    @Override
    public Integer getProductosLowStock() {
        return iProductoRepository.countProductosConStockBajo();
    }

    /**
     * Obtiene la cantidad de productos sin unidades en el inventario
     * @return Número de productos sin stock
     */
    @Override
    public Integer getProductosSinStock() {
        return iProductoRepository.countProductosSinStock();
    }

    /**
     * Obtiene la cantidad total de productos que hay en el inventario
     * @return Número de productos totales
     */
    @Override
    public Integer getTotalStock() {
        return Math.toIntExact(iProductoRepository.count());
    }
}
