package com.GABC.Examen2.services;

import com.GABC.Examen2.entities.DetalleVenta;
import com.GABC.Examen2.entities.Producto;
import com.GABC.Examen2.entities.Venta;
import com.GABC.Examen2.repositories.DetalleVentaRepository;
import com.GABC.Examen2.repositories.ProductoRepository;
import com.GABC.Examen2.repositories.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;

    public VentaService(VentaRepository ventaRepository, DetalleVentaRepository detalleVentaRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.productoRepository = productoRepository;
    }

    public List<Venta> obtenerTodasLasVentas() {
        return (List<Venta>) ventaRepository.findAll();
    }

    public Venta obtenerVentaPorId(Long id) {
        return ventaRepository.findById(id).orElse(null);
    }

    public List<DetalleVenta> obtenerDetallesPorVenta(Long ventaId) {
        return detalleVentaRepository.findByVentaId(ventaId);
    }

    public Venta crearNuevaVenta(String nombreCliente) {
        Venta venta = new Venta();
        venta.setNombreCliente(nombreCliente);
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(0.0);
        return ventaRepository.save(venta);
    }

    @Transactional
    public void agregarProductoAVenta(Long ventaId, Long productoId, Integer cantidad) {
        Producto producto = productoRepository.findById(productoId).orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (producto.getStock() < cantidad) {
            throw new RuntimeException("No hay suficiente stock. Stock actual: " + producto.getStock());
        }

        Venta venta = ventaRepository.findById(ventaId).orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        // 1. Crear el detalle de la venta
        DetalleVenta detalle = new DetalleVenta();
        detalle.setVentaId(ventaId);
        detalle.setProductoId(productoId);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(producto.getPrecio());
        double subtotal = producto.getPrecio() * cantidad;
        detalle.setSubtotal(subtotal);
        detalleVentaRepository.save(detalle);

        // 2. Descontar el stock del producto
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);

        // 3. Sumar el subtotal al total de la Venta
        venta.setTotal(venta.getTotal() + subtotal);
        ventaRepository.save(venta);
    }
}