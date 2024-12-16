package com.tp_anual.proyecto_heladeras_solidarias.service.incidente;

import com.tp_anual.proyecto_heladeras_solidarias.model.incidente.Incidente;
import com.tp_anual.proyecto_heladeras_solidarias.model.tecnico.Tecnico;
import com.tp_anual.proyecto_heladeras_solidarias.service.i18n.I18nService;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;
import com.tp_anual.proyecto_heladeras_solidarias.model.incidente.Alerta;
import com.tp_anual.proyecto_heladeras_solidarias.repository.incidente.AlertaRepository;
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
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final NotificadorDeIncidentes notificadorDeIncidentes;

    private final I18nService i18nService;

    public AlertaService(AlertaRepository vAlertaRepository, NotificadorDeIncidentes vNotificadorDeIncidentes, I18nService vI18nService) {
        alertaRepository = vAlertaRepository;
        notificadorDeIncidentes = vNotificadorDeIncidentes;

        i18nService = vI18nService;
    }

    public Alerta obtenerAlerta(Long idAlerta) {
        return alertaRepository.findById(idAlerta).orElseThrow(() -> new EntityNotFoundException(i18nService.getMessage("obtenerEntidad_exception")));
    }

    public List<Alerta> obtenerAlertas() {
        return new ArrayList<>(alertaRepository.findAll());
    }

    public List<Alerta> obtenerAlertasTemperatura() {
        return new ArrayList<>(alertaRepository.findByTipo(Alerta.TipoAlerta.TEMPERATURA));
    }

    public List<Alerta> obtenerAlertasFraude() {
        return new ArrayList<>(alertaRepository.findByTipo(Alerta.TipoAlerta.FRAUDE));
    }

    public List<Alerta> obtenerAlertasFallaConexion() {
        return new ArrayList<>(alertaRepository.findByTipo(Alerta.TipoAlerta.FALLA_CONEXION));
    }

    public List<Alerta> obtenerAlertasParaTecnico(Tecnico tecnico) {
        return new ArrayList<>(alertaRepository.findAlertasParaTecnico(tecnico.getId()));
    }

    public List<Alerta> obtenerAlertasNoResueltas() {
        return new ArrayList<>(alertaRepository.findAlertasNoResueltas());
    }

    public List<Alerta> obtenerAlertasSinTecnicoNoResueltas() {
        return new ArrayList<>(alertaRepository.findAlertasSinTecnicoNoResueltas());
    }

    public Alerta guardarAlerta(Alerta alerta) {
        return alertaRepository.save(alerta);
    }

    public void asignarTecnico(Long alertaId, Tecnico tecnico) {
        Alerta alerta = obtenerAlerta(alertaId);
        alerta.setTecnico(tecnico);
        guardarAlerta(alerta);
    }

    public void producirAlerta(LocalDateTime fecha, Heladera heladera, Alerta.TipoAlerta tipo) {
        Alerta alerta = new Alerta(LocalDateTime.now(), heladera, tipo);
        guardarAlerta(alerta);

        notificadorDeIncidentes.notificarIncidente(alerta);

        log.log(Level.INFO, i18nService.getMessage("incidente.Alerta.producirAlerta_info", alerta.getTipo(), heladera.getNombre()));
    }
}
