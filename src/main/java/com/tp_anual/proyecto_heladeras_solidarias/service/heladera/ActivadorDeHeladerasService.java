package com.tp_anual.proyecto_heladeras_solidarias.service.heladera;

import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.ColaboradorJuridico;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;
import com.tp_anual.proyecto_heladeras_solidarias.repository.heladera.HeladeraRepository;
import com.tp_anual.proyecto_heladeras_solidarias.service.i18n.I18nService;
import com.tp_anual.proyecto_heladeras_solidarias.service.incidente.AlertaService;
import com.tp_anual.proyecto_heladeras_solidarias.service.incidente.FallaTecnicaService;
import com.tp_anual.proyecto_heladeras_solidarias.service.notificador.NotificadorDeEstado;
import com.tp_anual.proyecto_heladeras_solidarias.service.suscripcion.GestorDeSuscripciones;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log
public class ActivadorDeHeladerasService {

    private final HeladeraRepository heladeraRepository;

    private final I18nService i18nService;

    public ActivadorDeHeladerasService(HeladeraRepository vHeladeraRepository, I18nService vI18nService) {
        heladeraRepository = vHeladeraRepository;

        i18nService = vI18nService;
    }

    public Heladera obtenerHeladera(Long heladeraId) {
        return heladeraRepository.findById(heladeraId).orElseThrow(() -> new EntityNotFoundException(i18nService.getMessage("obtenerEntidad_exception")));
    }

    public Heladera guardarHeladera(Heladera heladera) {
        return heladeraRepository.saveAndFlush(heladera);
    }

    public void marcarComoActiva(Long heladeraId) {
        Heladera heladera = obtenerHeladera(heladeraId);
        heladera.marcarComoActiva();
        guardarHeladera(heladera);
    }

    public void marcarComoInactiva(Long heladeraId) {
        Heladera heladera = obtenerHeladera(heladeraId);
        heladera.marcarComoInactiva();
        guardarHeladera(heladera);
    }
}
