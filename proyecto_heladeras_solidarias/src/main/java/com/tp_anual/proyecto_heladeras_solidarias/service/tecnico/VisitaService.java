package com.tp_anual.proyecto_heladeras_solidarias.service.tecnico;

import com.tp_anual.proyecto_heladeras_solidarias.i18n.I18n;
import com.tp_anual.proyecto_heladeras_solidarias.model.tecnico.Visita;
import com.tp_anual.proyecto_heladeras_solidarias.repository.tecnico.VisitaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Log
public class VisitaService {
    private final VisitaRepository visitaRepository;

    public VisitaService(VisitaRepository vVisitaRepository) {
        visitaRepository = vVisitaRepository;
    }

    public Visita obtenerVisita(Long visitaId) {
        return visitaRepository.findById(visitaId).orElseThrow(() -> new EntityNotFoundException(I18n.getMessage("obtenerEntidad_exception")));
    }

    public ArrayList<Visita> obtenerVisitas() {
        return new ArrayList<>(visitaRepository.findAll());
    }

    public Visita guardarVisita(Visita visita) {
        return visitaRepository.save(visita);
    }
}
