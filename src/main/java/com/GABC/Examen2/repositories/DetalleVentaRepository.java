package com.GABC.Examen2.repositories;

import com.GABC.Examen2.entities.DetalleVenta;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DetalleVentaRepository extends CrudRepository<DetalleVenta, Long> {

    @Query("SELECT * FROM detalle_ventas WHERE venta_id = :ventaId")
    List<DetalleVenta> findByVentaId(@Param("ventaId") Long ventaId);
}