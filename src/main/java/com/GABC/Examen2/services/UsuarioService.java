package com.GABC.Examen2.services;

import com.GABC.Examen2.entities.Usuario;
import com.GABC.Examen2.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario autenticar(String username, String password) {
        return usuarioRepository.findByUsernameAndPassword(username, password);
    }

    public boolean registrarNuevoUsuario(Usuario usuario) {
        if (usuarioRepository.countByUsername(usuario.getUsername()) > 0) {
            return false;
        }
        usuarioRepository.save(usuario);
        return true;
    }

    public boolean cambiarPassword(String username, String nuevaPassword) {
        if (usuarioRepository.countByUsername(username) > 0) {
            usuarioRepository.updatePasswordByUsername(username, nuevaPassword);
            return true;
        }
        return false;
    }
}