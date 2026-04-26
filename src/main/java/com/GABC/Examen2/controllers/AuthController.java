package com.GABC.Examen2.controllers;

import com.GABC.Examen2.entities.Usuario;
import com.GABC.Examen2.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String redireccionarInicio() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String mostrarLogin(HttpSession session) {
        if (session.getAttribute("usuarioLogueado") != null) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam("username") String username,
                                @RequestParam("password") String password,
                                HttpSession session, Model model) {
        Usuario usuario = usuarioService.autenticar(username, password);
        if (usuario != null) {
            session.setAttribute("usuarioLogueado", usuario);
            return "redirect:/dashboard";
        }
        model.addAttribute("error", "Usuario o contraseña incorrectos.");
        return "login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute Usuario usuario, Model model) {
        boolean registrado = usuarioService.registrarNuevoUsuario(usuario);
        if (registrado) {
            model.addAttribute("exito", "Registro completado con éxito. Ahora puedes iniciar sesión.");
            return "login";
        }
        model.addAttribute("error", "Error: Ese nombre de usuario ya está registrado.");
        model.addAttribute("usuario", usuario);
        return "registro";
    }

    @GetMapping("/recuperar")
    public String mostrarRecuperar() {
        return "recuperar";
    }

    @PostMapping("/recuperar")
    public String procesarRecuperacion(@RequestParam("username") String username,
                                       @RequestParam("nuevaPassword") String nuevaPassword,
                                       Model model) {
        boolean actualizado = usuarioService.cambiarPassword(username, nuevaPassword);
        if (actualizado) {
            model.addAttribute("exito", "Contraseña actualizada correctamente. Inicia sesión.");
            return "login";
        }
        model.addAttribute("error", "No se encontró ninguna cuenta asociada a ese usuario.");
        return "recuperar";
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}