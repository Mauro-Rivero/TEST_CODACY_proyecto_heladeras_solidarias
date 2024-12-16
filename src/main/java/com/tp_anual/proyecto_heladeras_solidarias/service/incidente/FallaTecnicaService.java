package com.tp_anual.proyecto_heladeras_solidarias.service.incidente;

import com.tp_anual.proyecto_heladeras_solidarias.model.incidente.Alerta;
import com.tp_anual.proyecto_heladeras_solidarias.model.tecnico.Tecnico;
import com.tp_anual.proyecto_heladeras_solidarias.service.i18n.I18nService;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.Colaborador;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;
import com.tp_anual.proyecto_heladeras_solidarias.model.incidente.FallaTecnica;
import com.tp_anual.proyecto_heladeras_solidarias.repository.incidente.FallaTecnicaRepository;
import com.tp_anual.proyecto_heladeras_solidarias.service.notificador.NotificadorDeIncidentes;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Service
@Log
public class FallaTecnicaService {

    private final FallaTecnicaRepository fallaTecnicaRepository;
    private final NotificadorDeIncidentes notificadorDeIncidentes;

    private final I18nService i18nService;

    public FallaTecnicaService(FallaTecnicaRepository vFallaTecnicaRepository, NotificadorDeIncidentes vNotificadorDeIncidentes, I18nService vI18nService) {
        fallaTecnicaRepository = vFallaTecnicaRepository;
        notificadorDeIncidentes = vNotificadorDeIncidentes;

        i18nService = vI18nService;
    }

    public FallaTecnica obtenerFallaTecnica(Long idFallaTecnica) {
        return fallaTecnicaRepository.findById(idFallaTecnica).orElseThrow(() -> new EntityNotFoundException(i18nService.getMessage("obtenerEntidad_exception")));
    }

    public List<FallaTecnica> obtenerFallasTecnicas() {
        return new ArrayList<>(fallaTecnicaRepository.findAll());
    }

    public List<FallaTecnica> obtenerFallasTecnicasParaTecnico(Tecnico tecnico) {
        return new ArrayList<>(fallaTecnicaRepository.findFallasTecnicasParaTecnico(tecnico.getId()));
    }

    public List<FallaTecnica> obtenerFallasTecnicasNoResueltas() {
        return new ArrayList<>(fallaTecnicaRepository.findFallasTecnicasNoResueltas());
    }

    public List<FallaTecnica> obtenerFallasTecnicasSinTecnicoNoResueltas() {
        return new ArrayList<>(fallaTecnicaRepository.findFallasTecnicasSinTecnicoNoResueltas());
    }

    public FallaTecnica guardarFallaTecnica(FallaTecnica fallaTecnica) {
        return fallaTecnicaRepository.save(fallaTecnica);
    }

    public void asignarTecnico(Long alertaId, Tecnico tecnico) {
        FallaTecnica fallaTecnica = obtenerFallaTecnica(alertaId);
        fallaTecnica.setTecnico(tecnico);
        guardarFallaTecnica(fallaTecnica);
    }

    public void producirFallaTecnica(LocalDateTime fecha, Heladera heladera, Colaborador colaborador, String descripcion, String foto) {
        FallaTecnica fallaTecnica = new FallaTecnica(fecha, heladera, colaborador, descripcion, foto);
        guardarFallaTecnica(fallaTecnica);

        notificadorDeIncidentes.notificarIncidente(fallaTecnica);

        log.log(Level.INFO, i18nService.getMessage("incidente.FallaTecnica.producirFallaTecnica_info", heladera.getNombre()));
    }
}
