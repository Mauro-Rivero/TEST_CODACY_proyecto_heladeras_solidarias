package com.tp_anual.proyecto_heladeras_solidarias.service.tarjeta;

import com.tp_anual.proyecto_heladeras_solidarias.exception.heladera.HeladeraLlenaSolicitudIngresoException;
import com.tp_anual.proyecto_heladeras_solidarias.exception.heladera.HeladeraVaciaSolicitudRetiroException;
import com.tp_anual.proyecto_heladeras_solidarias.exception.heladera.PermisoAperturaAusenteException;
import com.tp_anual.proyecto_heladeras_solidarias.exception.heladera.PermisoAperturaExpiradoException;
import com.tp_anual.proyecto_heladeras_solidarias.exception.tarjeta.DatosInvalidosCrearTarjetaColaboradorException;
import com.tp_anual.proyecto_heladeras_solidarias.service.i18n.I18nService;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.ColaboradorHumano;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.acciones_en_heladera.AperturaColaborador;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.acciones_en_heladera.SolicitudAperturaColaborador;
import com.tp_anual.proyecto_heladeras_solidarias.model.tarjeta.PermisoApertura;
import com.tp_anual.proyecto_heladeras_solidarias.model.tarjeta.Tarjeta;
import com.tp_anual.proyecto_heladeras_solidarias.model.tarjeta.TarjetaColaborador;

import com.tp_anual.proyecto_heladeras_solidarias.repository.tarjeta.TarjetaColaboradorRepository;
import com.tp_anual.proyecto_heladeras_solidarias.service.colaborador.ColaboradorService;
import com.tp_anual.proyecto_heladeras_solidarias.service.heladera.AccionHeladeraService;
import com.tp_anual.proyecto_heladeras_solidarias.service.heladera.GestorDeAperturas;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.logging.Level;

@Log
@Service
public class TarjetaColaboradorService extends TarjetaService {

    private final TarjetaColaboradorRepository tarjetaColaboradorRepository;
    private final ColaboradorService colaboradorService;
    private final GestorDeAperturas gestorDeAperturas;
    private final AccionHeladeraService accionHeladeraService;
    private final TarjetaColaboradorCreator tarjetaColaboradorCreator;
    private final PermisoAperturaService permisoAperturaService;

    private final I18nService i18nService;

    public TarjetaColaboradorService(TarjetaColaboradorRepository vTarjetaColaboradorRepository, ColaboradorService vColaboradorService, AccionHeladeraService vAccionHeladeraService, GestorDeAperturas vGestorDeAperturas, TarjetaColaboradorCreator vTarjetaColaboradorCreator, PermisoAperturaService vPermisoAperturaService, I18nService vI18nService) {
        super();
        tarjetaColaboradorRepository = vTarjetaColaboradorRepository;
        colaboradorService = vColaboradorService;
        accionHeladeraService = vAccionHeladeraService;
        gestorDeAperturas = vGestorDeAperturas;
        tarjetaColaboradorCreator = vTarjetaColaboradorCreator;
        permisoAperturaService = vPermisoAperturaService;

        i18nService = vI18nService;
    }

    @Override
    public TarjetaColaborador obtenerTarjeta(String tarjetaColaboradorId) {
        return tarjetaColaboradorRepository.findById(tarjetaColaboradorId).orElseThrow(() -> new EntityNotFoundException(i18nService.getMessage("obtenerEntidad_exception")));
    }

    @Override
    public TarjetaColaborador guardarTarjeta(Tarjeta tarjetaColaborador){
        return tarjetaColaboradorRepository.saveAndFlush((TarjetaColaborador) tarjetaColaborador);
    }

    @Override
    public TarjetaColaborador crearTarjeta(Long colaboradorId) throws DatosInvalidosCrearTarjetaColaboradorException {
        ColaboradorHumano colaborador = colaboradorService.obtenerColaboradorHumano(colaboradorId);

        return (TarjetaColaborador) tarjetaColaboradorCreator.crearTarjeta(colaborador);
    }

    @Override
    public Boolean puedeUsar(String tarjetaColaboradorId) {
        return true;
    }

    public void agregarPermiso(String tarjetaColaboradorId, PermisoApertura permiso) {
        TarjetaColaborador tarjetaColaborador = obtenerTarjeta(tarjetaColaboradorId);
        tarjetaColaborador.agregarPermiso(permiso);
        guardarTarjeta(tarjetaColaborador);
    }

    public SolicitudAperturaColaborador solicitarApertura(String tarjetaColaboradorId, Heladera heladeraInvolucrada, SolicitudAperturaColaborador.MotivoSolicitud motivo, Integer cantidadViandas) throws HeladeraVaciaSolicitudRetiroException, HeladeraLlenaSolicitudIngresoException {
        TarjetaColaborador tarjetaColaborador = obtenerTarjeta(tarjetaColaboradorId);

        gestorDeAperturas.revisarMotivoApertura(heladeraInvolucrada, motivo, cantidadViandas);

        SolicitudAperturaColaborador solicitudApertura = new SolicitudAperturaColaborador(LocalDateTime.now(), heladeraInvolucrada, tarjetaColaborador.getTitular(), motivo);
        accionHeladeraService.guardarAccionHeladera(solicitudApertura);

        // Agrego el permiso correspondiente para abrir la Heladera involucrada
        PermisoApertura permisoApertura = permisoAperturaService.crearPermisoApertura(heladeraInvolucrada, LocalDateTime.now(), motivo, cantidadViandas);
        agregarPermiso(tarjetaColaboradorId, permisoApertura);

        log.log(Level.INFO, i18nService.getMessage("tarjeta.TarjetaColaborador.solicitarApertura_info", heladeraInvolucrada.getNombre(), tarjetaColaborador.getTitular().getPersona().getNombre(2)));

        return solicitudApertura;
    }

    public AperturaColaborador intentarApertura(String tarjetaColaboradorId, Heladera heladera) throws PermisoAperturaAusenteException, PermisoAperturaExpiradoException {
        // Primero chequeo internamente que pueda realizar la Apertura
        TarjetaColaborador tarjetaColaborador = obtenerTarjeta(tarjetaColaboradorId);
        ColaboradorHumano titular = tarjetaColaborador.getTitular();

        PermisoApertura permisoApertura = gestorDeAperturas.revisarPermisoAperturaC(heladera, titular);

        permisoAperturaService.impedirRevocamientoPermisoApertura(permisoApertura.getId());

        AperturaColaborador apertura = new AperturaColaborador(LocalDateTime.now(), heladera, titular);
        accionHeladeraService.guardarAccionHeladera(apertura);

        log.log(Level.INFO, i18nService.getMessage("tarjeta.TarjetaColaborador.intentarApertura_info", heladera.getNombre(), titular.getPersona().getNombre(2)));

        return apertura;
    }
}