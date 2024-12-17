package com.tp_anual.proyecto_heladeras_solidarias.service.tecnico;

import com.tp_anual.proyecto_heladeras_solidarias.model.incidente.Incidente;
import com.tp_anual.proyecto_heladeras_solidarias.model.tecnico.Tecnico;
import com.tp_anual.proyecto_heladeras_solidarias.service.i18n.I18nService;
import com.tp_anual.proyecto_heladeras_solidarias.model.tecnico.Visita;
import com.tp_anual.proyecto_heladeras_solidarias.repository.tecnico.VisitaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log
public class VisitaService {

    private final VisitaRepository visitaRepository;

    private final I18nService i18nService;

    public VisitaService(VisitaRepository vVisitaRepository, I18nService vI18nService) {
        visitaRepository = vVisitaRepository;

        i18nService = vI18nService;
    }

    public Visita obtenerVisita(Long visitaId) {
        return visitaRepository.findById(visitaId).orElseThrow(() -> new EntityNotFoundException(i18nService.getMessage("obtenerEntidad_exception")));
    }

    public List<Visita> obtenerVisitas() {
        return new ArrayList<>(visitaRepository.findAll());
    }

    public List<Visita> obtenerVisitasPorTecnico(Tecnico tecnico) {
        return new ArrayList<>(visitaRepository.findByTecnico(tecnico));
    }

    public Visita obtenerVisitaNoRevisadaMasRecientePorIncidente(Incidente incidente) {
        return visitaRepository.findByIncidenteAndRevisadaFalseOrderByFechaDesc(incidente);
    }

    public List<Visita> obtenerVisitasNoExitosas() {return new ArrayList<>(visitaRepository.findByEstadoFalseAndRevisadaFalse());}

    public Visita guardarVisita(Visita visita) {
        return visitaRepository.save(visita);
    }

    public void eliminarVisitas(List<Visita> visitas) {
        if (visitas != null && !visitas.isEmpty()) {
            visitaRepository.deleteAll(visitas);
        }
    }

    public void seReviso(Long visitaId) {
        Visita visita = obtenerVisita(visitaId);
        visita.seReviso();
        guardarVisita(visita);
    }
}
