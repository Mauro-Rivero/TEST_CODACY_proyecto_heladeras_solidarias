package com.tp_anual.proyecto_heladeras_solidarias.repository.tecnico;

import com.tp_anual.proyecto_heladeras_solidarias.model.incidente.Incidente;
import com.tp_anual.proyecto_heladeras_solidarias.model.tecnico.Tecnico;
import com.tp_anual.proyecto_heladeras_solidarias.model.tecnico.Visita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitaRepository extends JpaRepository<Visita, Long> {

    List<Visita> findByEstadoFalseAndRevisadaFalse();

    List<Visita> findByTecnico(Tecnico tecnico);

    Visita findByIncidenteAndRevisadaFalseOrderByFechaDesc(Incidente incidente);
}