package com.tp_anual.proyecto_heladeras_solidarias.repository.incidente;

import com.tp_anual.proyecto_heladeras_solidarias.model.incidente.Incidente;
import com.tp_anual.proyecto_heladeras_solidarias.model.tecnico.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tp_anual.proyecto_heladeras_solidarias.model.incidente.Alerta;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findByTipo(Alerta.TipoAlerta tipoAlerta);

    // Estas son las que un Técnico puede elegir tomar
    @Query(nativeQuery = true, name = "Alerta.findAlertasParaTecnico")
    List<Alerta> findAlertasParaTecnico(@Param("tecnico") Long tecnicoId);

    // Estas son las que debería ver el admin
    @Query(nativeQuery = true, name = "Alerta.findAlertasNoResueltas")
    List<Alerta> findAlertasNoResueltas();

    // Estas son las que el Técnico debería poder visualizar por si quiere elegir ocuparse
    @Query(nativeQuery = true, name = "Alerta.findAlertasSinTecnicoNoResueltas")
    List<Alerta> findAlertasSinTecnicoNoResueltas();
}