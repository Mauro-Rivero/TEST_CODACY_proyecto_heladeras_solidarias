package com.tp_anual.proyecto_heladeras_solidarias.repository.tarjeta;

import com.tp_anual.proyecto_heladeras_solidarias.model.contribucion.DonacionDinero;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;
import com.tp_anual.proyecto_heladeras_solidarias.model.tarjeta.PermisoApertura;
import com.tp_anual.proyecto_heladeras_solidarias.model.tarjeta.TarjetaColaborador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PermisoAperturaRepository extends JpaRepository<PermisoApertura, Long> {

    @Query(nativeQuery = true, name = "PermisoApertura.findPermisoMasRecienteParaTarjetaAndHeladera")
    Optional<PermisoApertura> findPermisoMasRecienteParaTarjetaAndHeladera(@Param("heladera") Long heladeraId, @Param("tarjeta") String tarjetaCodigo);
}