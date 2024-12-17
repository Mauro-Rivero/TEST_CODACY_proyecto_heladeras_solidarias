package com.tp_anual.proyecto_heladeras_solidarias.service.heladera;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Comparator;
import java.util.logging.Level;

import com.tp_anual.proyecto_heladeras_solidarias.exception.heladera.*;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.ColaboradorHumano;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.acciones_en_heladera.SolicitudAperturaColaborador.MotivoSolicitud;
import com.tp_anual.proyecto_heladeras_solidarias.model.tarjeta.TarjetaColaborador;
import com.tp_anual.proyecto_heladeras_solidarias.model.tarjeta.PermisoApertura;
import com.tp_anual.proyecto_heladeras_solidarias.service.i18n.I18nService;
import com.tp_anual.proyecto_heladeras_solidarias.service.tarjeta.PermisoAperturaService;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Service
@Log
public class GestorDeAperturas {

    private final HeladeraService heladeraService;
    private final PermisoAperturaService permisoAperturaService;

    private final I18nService i18nService;

    public GestorDeAperturas(HeladeraService vHeladeraService, PermisoAperturaService vPermisoAperturaService, I18nService vI18nService) {
        heladeraService = vHeladeraService;
        permisoAperturaService = vPermisoAperturaService;

        i18nService = vI18nService;
    }
    
    public void revisarMotivoApertura(Heladera heladera, MotivoSolicitud motivo, Integer cantidadViandas) throws HeladeraVaciaSolicitudRetiroException, HeladeraLlenaSolicitudIngresoException {
        if (motivo == MotivoSolicitud.RETIRAR_LOTE_DE_DISTRIBUCION &&
            heladeraService.seVaciaCon(heladera.getId(), cantidadViandas - 1)) {
            
            log.log(Level.SEVERE, i18nService.getMessage("heladera.GestorDeAperturas.revisarSolicitudApertura_err_heladera_vacia", heladera.getNombre()));
            throw new HeladeraVaciaSolicitudRetiroException();
        }

        if ((motivo == MotivoSolicitud.INGRESAR_DONACION ||
            motivo == MotivoSolicitud.INGRESAR_LOTE_DE_DISTRIBUCION) &&
                heladeraService.seLlenaCon(heladera.getId(), cantidadViandas - 1)) {

            log.log(Level.SEVERE, i18nService.getMessage("heladera.GestorDeAperturas.revisarSolicitudApertura_err_heladera_llena", heladera.getNombre()));
            throw new HeladeraLlenaSolicitudIngresoException();
        }
    }
    
    public PermisoApertura revisarPermisoAperturaC(Heladera heladera, ColaboradorHumano colaborador) throws PermisoAperturaAusenteException, PermisoAperturaExpiradoException {
        TarjetaColaborador tarjetaColaborador = colaborador.getTarjeta();

        PermisoApertura permisoARevisar = permisoAperturaService.obtenerPermisoMasRecienteParaTarjetaYHeladera(heladera, tarjetaColaborador);

        if (permisoARevisar == null || permisoARevisar.getRevocado() == 1) {
            log.log(Level.SEVERE, i18nService.getMessage("heladera.GestorDeAperturas.revisarPermisoAperturaC_err", colaborador.getPersona().getNombre(2), heladera.getNombre()));
            throw new PermisoAperturaAusenteException();
        }

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaOtorgamiento = permisoARevisar.getFechaOtorgamiento();
        long horasPasadas = heladera.getUnidadTiempoPermiso().between(fechaOtorgamiento, ahora);

        if (horasPasadas >= heladera.getTiempoPermiso()) {
            permisoAperturaService.revocarPermisoApertura(permisoARevisar.getId());

            log.log(Level.SEVERE, i18nService.getMessage("heladera.GestorDeAperturas.revisarPermisoAperturaC_err", colaborador.getPersona().getNombre(2), heladera.getNombre()));
            throw new PermisoAperturaExpiradoException();
        }

        return permisoARevisar;
    }

    public void revisarPermisoAperturaP(Heladera heladera) throws HeladeraVaciaIntentoRetiroPESVException {
        if(heladeraService.estaVacia(heladera.getId())) {
            log.log(Level.SEVERE, i18nService.getMessage("heladera.GestorDeAperturas.resvisarPermisoAperturaP_err_heladera_vacia", heladera.getNombre()));
            throw new HeladeraVaciaIntentoRetiroPESVException();
        }
    }
}