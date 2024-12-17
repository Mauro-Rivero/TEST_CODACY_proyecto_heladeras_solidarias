package com.tp_anual.proyecto_heladeras_solidarias.service.heladera;

import com.tp_anual.proyecto_heladeras_solidarias.exception.heladera.HeladeraLlenaAgregarViandaException;
import com.tp_anual.proyecto_heladeras_solidarias.exception.heladera.HeladeraVaciaRetirarViandaException;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.ColaboradorJuridico;
import com.tp_anual.proyecto_heladeras_solidarias.service.i18n.I18nService;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.Colaborador;
import com.tp_anual.proyecto_heladeras_solidarias.model.contacto.MedioDeContacto;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.vianda.Vianda;
import com.tp_anual.proyecto_heladeras_solidarias.model.incidente.Alerta;
import com.tp_anual.proyecto_heladeras_solidarias.service.incidente.AlertaService;
import com.tp_anual.proyecto_heladeras_solidarias.service.incidente.FallaTecnicaService;
import com.tp_anual.proyecto_heladeras_solidarias.service.notificador.NotificadorDeEstado;
import com.tp_anual.proyecto_heladeras_solidarias.model.suscripcion.*;
import com.tp_anual.proyecto_heladeras_solidarias.repository.heladera.HeladeraRepository;
import com.tp_anual.proyecto_heladeras_solidarias.service.suscripcion.GestorDeSuscripciones;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

@Service
@Log
public class HeladeraService {

    private final HeladeraRepository heladeraRepository;
    private final AlertaService alertaService;
    private final FallaTecnicaService fallaTecnicaService;
    private final GestorDeSuscripciones gestorDeSuscripciones;
    private final NotificadorDeEstado notificadorDeEstado;

    private final I18nService i18nService;

    public HeladeraService(HeladeraRepository vHeladeraRepository, AlertaService vAlertaService, FallaTecnicaService vFallaTecnicaService, GestorDeSuscripciones vGestorDeSuscripciones, NotificadorDeEstado vNotificadorDeEstado, I18nService vI18nService) {
        heladeraRepository = vHeladeraRepository;
        alertaService = vAlertaService;
        fallaTecnicaService = vFallaTecnicaService;
        gestorDeSuscripciones = vGestorDeSuscripciones;
        notificadorDeEstado = vNotificadorDeEstado;

        i18nService = vI18nService;
    }

    public Heladera obtenerHeladera(Long heladeraId) {
        return heladeraRepository.findById(heladeraId).orElseThrow(() -> new EntityNotFoundException(i18nService.getMessage("obtenerEntidad_exception")));
    }

    public List<Heladera> obtenerHeladeras(){
        return new ArrayList<>(heladeraRepository.findAll());
    }

    public List<Heladera> obtenerHeladerasPorColaborador(ColaboradorJuridico colaboradorJuridico) {return new ArrayList<>(heladeraRepository.findHeladerasParaColaborador(colaboradorJuridico.getId()));}

    public Heladera guardarHeladera(Heladera heladera) {
        return heladeraRepository.saveAndFlush(heladera);
    }

    public void elimiarHeladera(Long heladeraId) {
         heladeraRepository.deleteById(heladeraId);
    }

    public void actualizarFechaApertura(Long heladeraId) {
        Heladera heladera = obtenerHeladera(heladeraId);
        heladera.actualizarFechaApertura();
        guardarHeladera(heladera);
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

    public Boolean estaVacia(Long heladeraId) {
        Heladera heladera = obtenerHeladera(heladeraId);
        return heladera.getViandas().isEmpty();
    }

    public Boolean estaLlena(Long heladeraId) {
        Heladera heladera = obtenerHeladera(heladeraId);
        return Objects.equals(heladera.viandasActuales(), heladera.getCapacidad());
    }

    public Boolean estaraVacia(Long heladeraId) {
        Heladera heladera = obtenerHeladera(heladeraId);
        return heladera.getCantidadReservada() == 0;
    }

    public Boolean estaraLlena(Long heladeraId) {
        Heladera heladera = obtenerHeladera(heladeraId);
        return Objects.equals(heladera.getCantidadReservada(), heladera.getCapacidad());
    }

    public Boolean verificarCapacidad(Long heladeraId) {
        Heladera heladera = obtenerHeladera(heladeraId);
        return heladera.viandasActuales() < heladera.getCapacidad();
    }

    public Boolean verificarReserva(Long heladeraId) {
        Heladera heladera = obtenerHeladera(heladeraId);
        return heladera.getCantidadReservada() < heladera.getCapacidad();
    }

    public Boolean seVaciaCon(Long heladeraId, Integer viandas) {
        Heladera heladera = obtenerHeladera(heladeraId);
        return heladera.viandasActuales() - viandas <= 0 || heladera.getCantidadReservada() - viandas <= 0;
    }

    public Boolean seLlenaCon(Long heladeraId, Integer viandas) {
        Heladera heladera = obtenerHeladera(heladeraId);
        return heladera.getCapacidad() - (heladera.viandasActuales() + viandas) <= 0 || heladera.getCapacidad() - (heladera.getCantidadReservada() + viandas) <= 0;
    }

    // Este método actúa únicamente sobre lo que ocurre de verdad en la heladera (no sobre los supuestos con cantidad reservada)
    public void verificarCondiciones(Long heladeraId) {
        Heladera heladera = obtenerHeladera(heladeraId);
        List<Suscripcion> suscripciones = gestorDeSuscripciones.suscripcionesPorHeladera(heladera);

        for (Suscripcion suscripcion : suscripciones) {

            switch(suscripcion) {

                case SuscripcionViandasMin suscripcionViandasMin -> {
                    // Verifico si se está vaciando
                    if (heladera.viandasActuales() <= suscripcionViandasMin.getViandasDisponiblesMin())
                        reportarEstadoSegunCondicionSuscripcion(heladeraId, suscripcion.getMedioDeContactoElegido(), Suscripcion.CondicionSuscripcion.VIANDAS_MIN);
                }

                case SuscripcionViandasMax suscripcionViandasMax -> {
                    // Verifico si se está llenando
                    if ((heladera.getCapacidad() - heladera.viandasActuales()) <= suscripcionViandasMax.getViandasParaLlenarMax())
                        reportarEstadoSegunCondicionSuscripcion(heladeraId, suscripcion.getMedioDeContactoElegido(), Suscripcion.CondicionSuscripcion.VIANDAS_MAX);
                }

                case SuscripcionDesperfecto suscripcionDesperfecto -> {
                    // Verifico si hay un desperfecto (y que la Heladera tenga Viandas)
                    if (!heladera.getEstado() && !estaVacia(heladeraId))
                        reportarEstadoSegunCondicionSuscripcion(heladeraId, suscripcion.getMedioDeContactoElegido(), Suscripcion.CondicionSuscripcion.DESPERFECTO);
                }

                default -> {}

            }
        }
    }

    // Este método también sirve para "rollbackear" la reserva de viandas
    public void reservarEspacioParaViandas(Long heladeraId, Integer cantidad) {
        Heladera heladera = obtenerHeladera(heladeraId);
        Integer nuevaCantidadReservada = heladera.getCantidadReservada() + cantidad;

        heladera.setCantidadReservada(nuevaCantidadReservada);
        guardarHeladera(heladera);
    }

    // Este método también sirve para "rollbackear" la reserva de espacio para viandas
    public void reservarViandas(Long heladeraId, Integer cantidad) {
        Heladera heladera = obtenerHeladera(heladeraId);
        Integer nuevaCantidadReservada = heladera.getCantidadReservada() - cantidad;

        heladera.setCantidadReservada(nuevaCantidadReservada);
        guardarHeladera(heladera);
    }

    public void agregarVianda(Long heladeraId, Vianda vianda) throws HeladeraLlenaAgregarViandaException {
        Heladera heladera = obtenerHeladera(heladeraId);

        if (estaLlena(heladeraId)) {
            log.log(Level.SEVERE, i18nService.getMessage("heladera.Heladera.agregarVianda_err", heladera.getNombre()));
            throw new HeladeraLlenaAgregarViandaException();
        }

        heladera.getViandas().add(vianda);
        guardarHeladera(heladera);

        verificarCondiciones(heladeraId);   // Verifico condiciones cuando agregamos una Vianda (una de las dos únicas formas en que la cantidad de Viandas en la Heladera puede cambiar)

        log.log(Level.INFO, i18nService.getMessage("heladera.Heladera.agregarVianda_info", vianda.getComida(), heladera.getNombre()));
    }

    public Vianda retirarVianda(Long heladeraId) throws HeladeraVaciaRetirarViandaException {
        Heladera heladera = obtenerHeladera(heladeraId);

        if (estaVacia(heladeraId)) {
            log.log(Level.SEVERE, i18nService.getMessage("heladera.Heladera.retirarVianda_err", heladera.getNombre()));
            throw new HeladeraVaciaRetirarViandaException();
        }

        Vianda viandaRetirada = heladera.getViandas().removeFirst();
        guardarHeladera(heladera);

        verificarCondiciones(heladeraId);   // Verifico condiciones cuando retiramos una Vianda (una de las dos únicas formas en que la cantidad de Viandas en la Heladera puede cambiar)

        log.log(Level.INFO, i18nService.getMessage("heladera.Heladera.retirarVianda_info", viandaRetirada.getComida(), heladera.getNombre()));

        return viandaRetirada;
    }

    protected void verificarTempActual(Long heladeraId) {
        Heladera heladera = obtenerHeladera(heladeraId);
        Float tempActual = heladera.getTempActual();

        if (tempActual < heladera.getTempMin() || tempActual > heladera.getTempMax()) {
            producirAlerta(heladeraId, Alerta.TipoAlerta.TEMPERATURA);
            guardarHeladera(heladera);
        }
    }

    public void setTempActual(Long heladeraId, Float temperatura) {
        Heladera heladera = obtenerHeladera(heladeraId);
        heladera.setTempActual(temperatura);
        verificarTempActual(heladeraId);  // Siempre que setea/actualiza su temperatura, debe chequearla posteriormente
        guardarHeladera(heladera);
    }

    public void reaccionarAnteIncidente(Long heladeraId) {
        marcarComoInactiva(heladeraId);
        verificarCondiciones(heladeraId);
    }

    public void reportarEstadoSegunCondicionSuscripcion(Long heladeraId, MedioDeContacto medioDeContacto, Suscripcion.CondicionSuscripcion condicion) {   // Usa el Medio de Contacto previamente elegido por el colaborador
        Heladera heladera = obtenerHeladera(heladeraId);
        notificadorDeEstado.notificarEstado(heladera, medioDeContacto, condicion);
    }

    public void producirAlerta(Long heladeraId, Alerta.TipoAlerta tipo) {
        Heladera heladera = obtenerHeladera(heladeraId);
        reaccionarAnteIncidente(heladeraId);  // Si una Alerta debe ser reportada, previamente, se marca la Heladera como inactiva y se avisa a los Colaboradores suscriptos
        guardarHeladera(heladera);

        alertaService.producirAlerta(LocalDateTime.now(), heladera, tipo);
    }

    public void producirFallaTecnica(Long heladeraId, Colaborador colaborador, String descripcion, String foto) {
        Heladera heladera = obtenerHeladera(heladeraId);
        reaccionarAnteIncidente(heladeraId);  // Si una Falla Técnica debe ser reportada, previamente, se marca la Heladera como inactiva y se avisa a los Colaboradores suscriptos
        guardarHeladera(heladera);

        fallaTecnicaService.producirFallaTecnica(LocalDateTime.now(), heladera, colaborador, descripcion, foto);
    }
}

