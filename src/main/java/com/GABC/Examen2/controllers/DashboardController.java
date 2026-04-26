package com.GABC.Examen2.controllers;

import com.GABC.Examen2.entities.Usuario;
import com.GABC.Examen2.entities.Venta;
import com.GABC.Examen2.repositories.ProductoRepository;
import com.GABC.Examen2.repositories.UsuarioRepository;
import com.GABC.Examen2.repositories.VentaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class DashboardController {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    // Inyectamos los 3 repositorios
    public DashboardController(VentaRepository ventaRepository, ProductoRepository productoRepository, UsuarioRepository usuarioRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/dashboard")
    public String mostrarDashboard(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }
        model.addAttribute("nombreUsuario", usuarioLogueado.getNombreCompleto());

        // 1. OBTENER DATOS 100% REALES DE LA BASE DE DATOS
        long totalOrdenes = ventaRepository.count();
        long totalProductos = productoRepository.count();
        long totalUsuarios = usuarioRepository.count();

        // 2. SUMAR LOS INGRESOS Y AGRUPARLOS POR MES (Para la gráfica)
        double ingresosTotales = 0.0;
        double[] ventasPorMes = new double[12]; // Arreglo de 12 meses

        Iterable<Venta> ventas = ventaRepository.findAll();
        for (Venta v : ventas) {
            if (v.getTotal() != null) {
                ingresosTotales += v.getTotal();
                if (v.getFecha() != null) {
                    int mesIndex = v.getFecha().getMonthValue() - 1; // 0 = Enero, 11 = Diciembre
                    ventasPorMes[mesIndex] += v.getTotal();
                }
            }
        }

        // Enviamos las variables al HTML
        model.addAttribute("totalOrdenes", totalOrdenes);
        model.addAttribute("ingresosTotales", String.format("%.2f", ingresosTotales));
        model.addAttribute("totalProductos", totalProductos);
        model.addAttribute("totalUsuarios", totalUsuarios);

        // 3. ENVIAR DATOS PARA LA GRÁFICA DE LÍNEAS
        List<String> labelsMeses = Arrays.asList("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic");
        List<Double> dataMeses = new ArrayList<>();
        for (double v : ventasPorMes) {
            dataMeses.add(v);
        }

        model.addAttribute("labelsMeses", labelsMeses);
        model.addAttribute("dataMeses", dataMeses);

        return "dashboard";
    }
}