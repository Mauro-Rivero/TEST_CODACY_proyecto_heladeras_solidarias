package com.tp_anual.proyecto_heladeras_solidarias.service.incidente;

import com.tp_anual.proyecto_heladeras_solidarias.model.incidente.FallaTecnica;
import com.tp_anual.proyecto_heladeras_solidarias.model.tecnico.Tecnico;
import com.tp_anual.proyecto_heladeras_solidarias.service.i18n.I18nService;
import com.tp_anual.proyecto_heladeras_solidarias.model.incidente.Incidente;
import com.tp_anual.proyecto_heladeras_solidarias.repository.incidente.IncidenteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log
public class IncidenteService {

    private final IncidenteRepository incidenteRepository;

    private final I18nService i18nService;

    public IncidenteService(IncidenteRepository vIncidenteRepository, I18nService vI18nService) {
        incidenteRepository = vIncidenteRepository;

        i18nService = vI18nService;
    }

    public Incidente obtenerIncidente(Long incidenteId) {
        return incidenteRepository.findById(incidenteId).orElseThrow(() -> new EntityNotFoundException(i18nService.getMessage("obtenerEntidad_exception")));
    }

    public List<Incidente> obtenerIncidentes() {
        return new ArrayList<>(incidenteRepository.findAll());
    }

    public Incidente guardarIncidente(Incidente incidente) {
        return incidenteRepository.save(incidente);
    }

    public void asignarTecnico(Long alertaId, Tecnico tecnico) {
        Incidente incidente = obtenerIncidente(alertaId);
        incidente.setTecnico(tecnico);
        guardarIncidente(incidente);
    }
}
