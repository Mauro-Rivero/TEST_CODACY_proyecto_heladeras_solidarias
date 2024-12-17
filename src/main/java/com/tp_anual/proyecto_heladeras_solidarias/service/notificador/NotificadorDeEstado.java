package com.tp_anual.proyecto_heladeras_solidarias.service.notificador;

import java.util.List;

import com.tp_anual.proyecto_heladeras_solidarias.model.contacto.MedioDeContacto;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;
import com.tp_anual.proyecto_heladeras_solidarias.model.suscripcion.Suscripcion.CondicionSuscripcion;
import com.tp_anual.proyecto_heladeras_solidarias.service.contacto.MedioDeContactoServiceSelector;
import com.tp_anual.proyecto_heladeras_solidarias.service.ubicador.UbicadorHeladera;
import com.tp_anual.proyecto_heladeras_solidarias.service.i18n.I18nService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Service
@Log
public class NotificadorDeEstado extends Notificador {

    private final UbicadorHeladera ubicadorHeladera;

    @Getter
    @Setter
    private Integer cantidadHeladeras;

    private final I18nService i18nService;
    
    public NotificadorDeEstado(MedioDeContactoServiceSelector vMedioDeContactoServiceSelector, UbicadorHeladera vUbicadorHeladera, I18nService vI18nService) {
        super(vMedioDeContactoServiceSelector);
        ubicadorHeladera = vUbicadorHeladera;
        cantidadHeladeras = 10; // Esta cantidad es arbitraria, pero puede ser modificada

        i18nService = vI18nService;
    }

    public void notificarEstado(Heladera heladera, MedioDeContacto medioDeContacto, CondicionSuscripcion condicion) {    // Usa el Medio de Contacto previamente elegido por el colaborador
        List<Heladera> heladerasCercanas = ubicadorHeladera.obtenerHeladerasCercanasA(heladera, cantidadHeladeras);
        
        switch(condicion) {

            case VIANDAS_MIN -> {

                if (heladerasCercanas == null || heladerasCercanas.isEmpty())
                    return;

                // Obtengo la Heladera más llena
                Heladera heladeraMasLlena = ubicadorHeladera.obtenerHeladeraMasLlena(heladerasCercanas);

                enviarNotificacion(
                medioDeContacto,
                i18nService.getMessage("notificador.NotificadorDeEstado.notificarEstado_outer_message_vmn_title",
                heladera.getNombre()),
                i18nService.getMessage("notificador.NotificadorDeEstado.notificarEstado_outer_message_vmn_body",
                heladera.getNombre(), heladera.viandasActuales(), heladeraMasLlena.getNombre(), heladeraMasLlena.getUbicacion().getDireccion()));
            }

            case VIANDAS_MAX -> {

                if (heladerasCercanas == null || heladerasCercanas.isEmpty())
                    return;

                // Obtengo la Heladera menos llena
                Heladera heladeraMenosLlena = ubicadorHeladera.obtenerHeladeraMenosLlena(heladerasCercanas);

                enviarNotificacion(
                medioDeContacto,
                i18nService.getMessage("notificador.NotificadorDeEstado.notificarEstado_outer_message_vmx_title",
                heladera.getNombre()),
                i18nService.getMessage("notificador.NotificadorDeEstado.notificarEstado_outer_message_vmx_body",
                (heladera.getCapacidad() - heladera.viandasActuales()), heladera.getNombre(), heladeraMenosLlena.getNombre(), heladeraMenosLlena.getUbicacion().getDireccion()));
            }

            case DESPERFECTO -> {

                if (heladerasCercanas == null || heladerasCercanas.isEmpty()) {
                    enviarNotificacion(
                            medioDeContacto,
                            i18nService.getMessage("notificador.NotificadorDeEstado.notificarEstado_outer_message_d_nhc_title",
                                    heladera.getNombre()),
                            i18nService.getMessage("notificador.NotificadorDeEstado.notificarEstado_outer_message_d_nhc_body"));

                    return;
                }

                enviarNotificacion(
                medioDeContacto,
                i18nService.getMessage("notificador.NotificadorDeEstado.notificarEstado_outer_message_d_title",
                heladera.getNombre()),
                i18nService.getMessage("notificador.NotificadorDeEstado.notificarEstado_outer_message_d_body",
                obtenerNombresYDireccionesDe(heladerasCercanas)));
            }

            default -> {}
        }
    }

    public String obtenerNombresYDireccionesDe(List<Heladera> heladeras) {
        Heladera heladera1 = heladeras.removeFirst();
        String nombresYDirecciones = i18nService.getMessage("notificador.NotificadorDeEstado.obtenerNombresYDireccionesDe_value_heladera_1", heladera1.getNombre(), heladera1.getUbicacion().getDireccion());
        
        for (Heladera heladera : heladeras) {
            nombresYDirecciones += i18nService.getMessage("notificador.NotificadorDeEstado.obtenerNombresYDireccionesDe_value_heladera_i", heladera.getNombre(), heladera.getUbicacion().getDireccion());
        }

        return nombresYDirecciones;
    }
}