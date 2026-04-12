package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.dto.MovimientoDTO;
import com.nebulaparfums.nebula_parfums.dto.ProductoDTO;
import com.nebulaparfums.nebula_parfums.model.Categoria;
import com.nebulaparfums.nebula_parfums.model.Producto;
import com.nebulaparfums.nebula_parfums.model.Proveedor;
import com.nebulaparfums.nebula_parfums.service.interfaces.ICategoriaService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IMovimientoInventarioService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IProductoService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@RestController
public class ProductoController {
    @Autowired
    private IProductoService iProductoService;

    @Autowired
    private ICategoriaService iCategoriaService;

    @Autowired
    private IProveedorService iProveedorService;

    @Autowired
    private IMovimientoInventarioService iMovimientoInventarioService;

    @GetMapping("/producto/traer")
    public Page<Producto> traerProductos(Pageable pageable) {
        return iProductoService.getProductos(pageable);
    }

    @GetMapping("/producto/resultados")
    public Page<Producto> resultadoBusqueda(@RequestParam("nombre") String nombre, Pageable pageable) {
        return iProductoService.getProductosBusqueda(pageable, nombre);
    }

    @GetMapping("/producto/lowstock")
    public Integer lowStock() {
        return iProductoService.getProductosLowStock();
    }

    @GetMapping("/producto/bajostock")
    public List<Producto> bajosStock() {
        return iProductoService.get4ProductosBajoStock();
    }

    @GetMapping("/producto/nostock")
    public Integer noStock() {
        return iProductoService.getProductosSinStock();
    }

    @GetMapping("/producto/totalproductos")
    public Integer totalProductos() {
        return iProductoService.getTotalStock();
    }

    @GetMapping("/producto/constock")
    public Integer productoConstock() {
        return iProductoService.getProductosConStock();
    }

    @GetMapping("/producto/categoria")
    public List<Producto> productosPorCategoria(@RequestParam("categoria") Integer categoria) {
        return iProductoService.getProductosCategoria(categoria);
    }

    @GetMapping("/producto/buscar")
    public Producto buscarProducto(@RequestParam("id") Integer id) {
        return iProductoService.getProductoById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @PutMapping("/producto/editar")
    public ResponseEntity<?> editarProducto(@RequestParam Integer id_producto,
                                            @RequestParam String nombre,
                                            @RequestParam String descripcion,
                                            @RequestParam Double precio,
                                            @RequestParam int stock_actual,
                                            @RequestParam int stock_minimo,
                                            @RequestParam Integer categoria,
                                            @RequestParam Integer proveedor,
                                            @RequestParam(required = false) MultipartFile imagen) {

        Categoria cat = iCategoriaService.getCategoriaById(categoria);
        Proveedor prov = iProveedorService.getProveedorById(proveedor);
        LocalDate fecha = LocalDate.now();

        Producto producto = iProductoService.getProductoById(id_producto);

        String nombreImagen = null;
        if (imagen != null && !imagen.isEmpty()) {
            try {
                String ruta = "src/main/uploads/";
                nombreImagen = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();

                Path path = Paths.get(ruta + nombreImagen);
                Files.write(path, imagen.getBytes());

                producto.setImagen(nombreImagen);

            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al guardar imagen");
            }
        }


        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setCategoria(cat);
        producto.setProveedor(prov);
        producto.setPrecio(precio);
        producto.setStock_actual(stock_actual);
        producto.setStock_minimo(stock_minimo);
        producto.setFecha_registro(fecha);



        iProductoService.editProducto(producto);
        MovimientoDTO movimientoDTO = new MovimientoDTO();
        movimientoDTO.setId_producto(producto.getId_producto());
        movimientoDTO.setCantidad(producto.getStock_actual());

        iMovimientoInventarioService.registrarRegistroProducto(movimientoDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(producto);

    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @DeleteMapping("/producto/borrar")
    public String borrarProducto(@RequestParam("id") Integer id) {
        iProductoService.deleteProducto(id);
        return "Producto eliminado con sucesso";
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    @PostMapping("/producto/crear")
    public ResponseEntity<?> crearProducto(@RequestParam String nombre,
                                           @RequestParam String descripcion,
                                           @RequestParam Double precio,
                                           @RequestParam int stock_actual,
                                           @RequestParam int stock_minimo,
                                           @RequestParam Integer categoria,
                                           @RequestParam Integer proveedor,
                                           @RequestParam(required = false) MultipartFile imagen) {

        Categoria cat = iCategoriaService.getCategoriaById(categoria);
        Proveedor prov = iProveedorService.getProveedorById(proveedor);
        LocalDate fecha = LocalDate.now();

        String nombreImagen = null;
        if (imagen != null && !imagen.isEmpty()) {
            try {
                String ruta = "src/main/uploads/";
                nombreImagen = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();

                Path path = Paths.get(ruta + nombreImagen);
                Files.write(path, imagen.getBytes());

            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al guardar imagen");
            }
        }

        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setCategoria(cat);
        producto.setProveedor(prov);
        producto.setPrecio(precio);
        producto.setStock_actual(stock_actual);
        producto.setStock_minimo(stock_minimo);
        producto.setFecha_registro(fecha);
        producto.setImagen(nombreImagen);

        iProductoService.saveProducto(producto);
        MovimientoDTO movimientoDTO = new MovimientoDTO();
        movimientoDTO.setId_producto(producto.getId_producto());
        movimientoDTO.setCantidad(producto.getStock_actual());

        iMovimientoInventarioService.registrarRegistroProducto(movimientoDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(producto);
    }
}
