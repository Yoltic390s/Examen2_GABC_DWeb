package com.GABC.Examen2.controllers;

import com.GABC.Examen2.entities.DetalleVenta;
import com.GABC.Examen2.entities.Producto;
import com.GABC.Examen2.entities.Usuario;
import com.GABC.Examen2.entities.Venta;
import com.GABC.Examen2.services.ProductoService;
import com.GABC.Examen2.services.VentaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/facturacion")
public class FacturacionController {

    private final VentaService ventaService;
    private final ProductoService productoService;

    public FacturacionController(VentaService ventaService, ProductoService productoService) {
        this.ventaService = ventaService;
        this.productoService = productoService;
    }

    private boolean isNotLogged(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario != null) {
            model.addAttribute("nombreUsuario", usuario.getNombreCompleto());
            return false;
        }
        return true;
    }

    @GetMapping
    public String listarFacturas(Model model, HttpSession session) {
        if (isNotLogged(session, model)) return "redirect:/login";
        model.addAttribute("ventas", ventaService.obtenerTodasLasVentas());
        return "lista_facturacion";
    }

    @GetMapping("/{id}/generar")
    public String generarFactura(@PathVariable Long id, Model model, HttpSession session) {
        if (isNotLogged(session, model)) return "redirect:/login";

        Venta venta = ventaService.obtenerVentaPorId(id);
        if (venta == null) return "redirect:/facturacion";

        List<DetalleVenta> detalles = ventaService.obtenerDetallesPorVenta(id);

        List<ItemFactura> items = new ArrayList<>();
        for (DetalleVenta d : detalles) {
            Producto prod = productoService.obtenerPorId(d.getProductoId());
            String nombreProd = (prod != null) ? prod.getNombre() : "Producto Eliminado";
            String descProd = (prod != null) ? prod.getDescripcion() : "";
            items.add(new ItemFactura(d.getCantidad(), nombreProd, descProd, d.getPrecioUnitario(), d.getSubtotal()));
        }

        model.addAttribute("venta", venta);
        model.addAttribute("items", items);

        double subtotalFactura = venta.getTotal() / 1.16;
        double ivaFactura = venta.getTotal() - subtotalFactura;

        model.addAttribute("subtotalFactura", String.format("%.2f", subtotalFactura));
        model.addAttribute("ivaFactura", String.format("%.2f", ivaFactura));

        return "factura_invoice";
    }

    public static class ItemFactura {
        public Integer cantidad;
        public String producto;
        public String descripcion;
        public Double precioUnitario;
        public Double subtotal;

        public ItemFactura(Integer c, String p, String d, Double pu, Double s) {
            this.cantidad = c; this.producto = p; this.descripcion = d; this.precioUnitario = pu; this.subtotal = s;
        }
    }
}