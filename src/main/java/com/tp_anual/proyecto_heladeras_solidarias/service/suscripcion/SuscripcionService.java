package com.tp_anual.proyecto_heladeras_solidarias.service.suscripcion;

import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.Colaborador;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.ColaboradorHumano;
import com.tp_anual.proyecto_heladeras_solidarias.service.i18n.I18nService;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;
import com.tp_anual.proyecto_heladeras_solidarias.model.suscripcion.Suscripcion;
import com.tp_anual.proyecto_heladeras_solidarias.repository.suscripcion.SuscripcionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log
public class SuscripcionService {

    private final SuscripcionRepository suscripcionRepository;

    private final I18nService i18nService;

    public SuscripcionService(SuscripcionRepository vSuscripcionRepository, I18nService vI18nService) {
        suscripcionRepository = vSuscripcionRepository;

        i18nService = vI18nService;
    }

    public Suscripcion obtenerSuscripcion(Long suscripcionId) {
        return suscripcionRepository.findById(suscripcionId).orElseThrow(() -> new EntityNotFoundException(i18nService.getMessage("obtenerEntidad_exception")));
    }

    public List<Suscripcion> obtenerSuscripciones() {
        return new ArrayList<>(suscripcionRepository.findAll());
    }

    public List<Suscripcion> obtenerSuscripcionesPorHeladera(Heladera heladera) {
        return new ArrayList<>(suscripcionRepository.findByHeladera(heladera));
    }

    public List<Suscripcion> obtenerSuscripcionesPorColaborador(ColaboradorHumano colaborador) {
        return new ArrayList<>(suscripcionRepository.findByColaborador(colaborador));
    }

    public Suscripcion guardarSuscripcion(Suscripcion suscripcion) {
        return suscripcionRepository.save(suscripcion);
    }
}
