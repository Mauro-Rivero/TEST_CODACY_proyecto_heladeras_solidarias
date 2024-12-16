package com.tp_anual.proyecto_heladeras_solidarias.service.notificador;

import com.tp_anual.proyecto_heladeras_solidarias.model.contacto.MedioDeContacto;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;
import com.tp_anual.proyecto_heladeras_solidarias.model.incidente.Incidente;
import com.tp_anual.proyecto_heladeras_solidarias.model.tecnico.Tecnico;
import com.tp_anual.proyecto_heladeras_solidarias.service.contacto.MedioDeContactoServiceSelector;
import com.tp_anual.proyecto_heladeras_solidarias.service.incidente.IncidenteService;
import com.tp_anual.proyecto_heladeras_solidarias.service.tecnico.TecnicoService;
import com.tp_anual.proyecto_heladeras_solidarias.service.ubicador.UbicadorTecnico;
import com.tp_anual.proyecto_heladeras_solidarias.service.i18n.I18nService;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.logging.Level;

@Service
@Log
public class NotificadorDeIncidentes extends Notificador {

    private final UbicadorTecnico ubicadorTecnico;
    private final TecnicoService tecnicoService;

    private final I18nService i18nService;
    private final IncidenteService incidenteService;

    public NotificadorDeIncidentes(MedioDeContactoServiceSelector vMedioDeContactoServiceSelector, UbicadorTecnico vUbicadorTecnico, TecnicoService vTecnicoService, I18nService vI18nService, IncidenteService incidenteService) {
        super(vMedioDeContactoServiceSelector);
        ubicadorTecnico = vUbicadorTecnico;
        tecnicoService = vTecnicoService;

        i18nService = vI18nService;
        this.incidenteService = incidenteService;
    }

    public void notificarIncidente(Incidente incidente) {
        try {
            Tecnico tecnicoAAlertar = ubicadorTecnico.obtenerTecnicoCercanoA(incidente);
            incidenteService.asignarTecnico(incidente.getId(), tecnicoAAlertar);
            tecnicoService.agregarAPendientes(tecnicoAAlertar.getId(), incidente);

            Heladera heladera = incidente.getHeladera();
            MedioDeContacto medioDeContacto = tecnicoAAlertar.getMedioDeContacto();
            enviarNotificacion(medioDeContacto, i18nService.getMessage("notificador.NotificadorDeIncidentes.notificarEstado_outer_message_title", heladera.getNombre()), i18nService.getMessage("notificador.NotificadorDeIncidentes.notificarEstado_outer_message_body", incidente.getClass().getSimpleName(), heladera.getNombre()));
        } catch (Exception e) {
            log.log(Level.INFO, i18nService.getMessage("notificador.NotificadorDeIncidentes.notificarEstado_outer_message_error"));
        }

    }
}

