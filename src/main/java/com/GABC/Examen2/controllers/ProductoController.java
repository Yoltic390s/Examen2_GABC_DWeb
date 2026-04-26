package com.GABC.Examen2.controllers;

import com.GABC.Examen2.entities.Producto;
import com.GABC.Examen2.entities.Usuario;
import com.GABC.Examen2.services.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
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
    public String listarProductos(Model model, HttpSession session) {
        if (isNotLogged(session, model)) return "redirect:/login";

        model.addAttribute("productos", productoService.obtenerTodos());
        return "lista_productos";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model, HttpSession session) {
        if (isNotLogged(session, model)) return "redirect:/login";

        model.addAttribute("producto", new Producto());
        return "formulario_producto";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, HttpSession session) {
        if (isNotLogged(session, model)) return "redirect:/login";

        Producto producto = productoService.obtenerPorId(id);
        if(producto == null) return "redirect:/productos";

        model.addAttribute("producto", producto);
        return "formulario_producto";
    }

    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute Producto producto, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        productoService.guardarProducto(producto);
        return "redirect:/productos";
    }

    @GetMapping("/borrar/{id}")
    public String mostrarConfirmacionBorrar(@PathVariable Long id, Model model, HttpSession session) {
        if (isNotLogged(session, model)) return "redirect:/login";

        Producto producto = productoService.obtenerPorId(id);
        if (producto == null) return "redirect:/productos";

        model.addAttribute("producto", producto);
        return "confirmar_borrar_producto";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        productoService.eliminarProducto(id);
        return "redirect:/productos";
    }
}