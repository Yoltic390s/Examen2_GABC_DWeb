package com.GABC.Examen2.controllers;

import com.GABC.Examen2.entities.Producto;
import com.GABC.Examen2.entities.Usuario;
import com.GABC.Examen2.entities.Venta;
import com.GABC.Examen2.services.ProductoService;
import com.GABC.Examen2.services.VentaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final ProductoService productoService; // Para llenar el select de productos

    public VentaController(VentaService ventaService, ProductoService productoService) {
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
    public String listarVentas(Model model, HttpSession session) {
        if (isNotLogged(session, model)) return "redirect:/login";
        model.addAttribute("ventas", ventaService.obtenerTodasLasVentas());
        return "lista_ventas";
    }

    @GetMapping("/nueva")
    public String mostrarFormularioNuevaVenta(Model model, HttpSession session) {
        if (isNotLogged(session, model)) return "redirect:/login";
        return "nueva_venta";
    }

    @PostMapping("/guardar")
    public String guardarNuevaVenta(@RequestParam("nombreCliente") String nombreCliente, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";
        Venta nuevaVenta = ventaService.crearNuevaVenta(nombreCliente);
        return "redirect:/ventas/" + nuevaVenta.getId() + "/agregar";
    }

    @GetMapping("/{id}/agregar")
    public String mostrarAgregarProductos(@PathVariable("id") Long id, Model model, HttpSession session) {
        if (isNotLogged(session, model)) return "redirect:/login";

        Venta venta = ventaService.obtenerVentaPorId(id);
        if (venta == null) return "redirect:/ventas";

        model.addAttribute("venta", venta);
        model.addAttribute("productos", productoService.obtenerTodos());
        // También mandamos los detalles actuales para que sepa qué ha agregado
        model.addAttribute("detalles", ventaService.obtenerDetallesPorVenta(id));

        return "agregar_detalle";
    }

    @PostMapping("/{id}/agregar")
    public String procesarAgregarProducto(@PathVariable("id") Long ventaId,
                                          @RequestParam("productoId") Long productoId,
                                          @RequestParam("cantidad") Integer cantidad,
                                          RedirectAttributes redirectAttributes,
                                          HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        try {
            ventaService.agregarProductoAVenta(ventaId, productoId, cantidad);
            redirectAttributes.addFlashAttribute("exito", "Producto agregado con éxito.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/ventas/" + ventaId + "/agregar";
    }
}