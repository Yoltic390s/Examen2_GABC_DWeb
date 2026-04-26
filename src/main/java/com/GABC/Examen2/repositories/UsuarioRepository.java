package com.GABC.Examen2.repositories;

import com.GABC.Examen2.entities.Usuario;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

    @Query("SELECT * FROM usuarios WHERE username = :username AND password = :password")
    Usuario findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    @Query("SELECT COUNT(*) FROM usuarios WHERE username = :username")
    long countByUsername(@Param("username") String username);

    @Modifying
    @Query("UPDATE usuarios SET password = :nuevaPassword WHERE username = :username")
    void updatePasswordByUsername(@Param("username") String username, @Param("nuevaPassword") String nuevaPassword);
}