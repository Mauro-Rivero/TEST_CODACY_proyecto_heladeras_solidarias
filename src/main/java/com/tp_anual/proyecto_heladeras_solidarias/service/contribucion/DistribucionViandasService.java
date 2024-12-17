package com.tp_anual.proyecto_heladeras_solidarias.service.contribucion;

import com.tp_anual.proyecto_heladeras_solidarias.exception.contribucion.DomicilioFaltanteDiVsException;
import com.tp_anual.proyecto_heladeras_solidarias.model.contribucion.DonacionVianda;
import com.tp_anual.proyecto_heladeras_solidarias.service.i18n.I18nService;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.Colaborador;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.ColaboradorHumano;
import com.tp_anual.proyecto_heladeras_solidarias.model.contribucion.Contribucion;
import com.tp_anual.proyecto_heladeras_solidarias.model.contribucion.DistribucionViandas;
import com.tp_anual.proyecto_heladeras_solidarias.repository.contribucion.DistribucionViandasRepository;
import com.tp_anual.proyecto_heladeras_solidarias.service.colaborador.ColaboradorPuntosService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.java.Log;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Service
@Log
public class DistribucionViandasService extends ContribucionService {

    private final DistribucionViandasRepository distribucionViandasRepository;
    private final ColaboradorPuntosService colaboradorPuntosService;
    private final Double multiplicadorPuntos = 1d;

    private final I18nService i18nService;

    public DistribucionViandasService(DistribucionViandasRepository vDistribucionViandasRepository, ColaboradorPuntosService vColaboradorPuntosService, I18nService vI18nService) {
        super();
        distribucionViandasRepository = vDistribucionViandasRepository;
        colaboradorPuntosService = vColaboradorPuntosService;

        i18nService = vI18nService;
    }

    @Override
    public DistribucionViandas obtenerContribucion(Long distribucionViandasId) {
        return distribucionViandasRepository.findById(distribucionViandasId).orElseThrow(() -> new EntityNotFoundException(i18nService.getMessage("obtenerEntidad_exception")));
    }

    public DistribucionViandas obtenerDistribucionViandasMasRecientePorColaborador(ColaboradorHumano colaborador) {
        return distribucionViandasRepository.findTopByColaboradorOrderByFechaContribucionDesc(colaborador);
    }

    public List<DistribucionViandas> obtenerDistribucionesViandasQueSumanPuntos() {
        return new ArrayList<>(distribucionViandasRepository.findByYaSumoPuntosFalse());
    }

    public List<DistribucionViandas> obtenerDistribucionesViandasNoConfirmadasParaColaborador(ColaboradorHumano colaborador) {
        return new ArrayList<>(distribucionViandasRepository.findByColaboradorAndCompletadaFalseAndCaducadaFalse(colaborador));
    }

    @Override
    public DistribucionViandas guardarContribucion(Contribucion distribucionViandas) {
        return distribucionViandasRepository.saveAndFlush((DistribucionViandas) distribucionViandas);
    }

    public void caducar(Long distribucionViandasId) {
        DistribucionViandas distribucionViandas = obtenerContribucion(distribucionViandasId);

        distribucionViandas.caducar();
        guardarContribucion(distribucionViandas);
    }

    @Override
    public void validarIdentidad(Long distribucionViandasId, Colaborador colaborador) throws DomicilioFaltanteDiVsException {
        if (colaborador.getDomicilio() == null) {
            log.log(Level.SEVERE, i18nService.getMessage("contribucion.DistribucionViandas.validarIdentidad_err", colaborador.getPersona().getNombre(2)));
            throw new DomicilioFaltanteDiVsException();
        }
    }

    @Override
    protected void confirmarSumaPuntos(Long distribucionViandasId, Colaborador colaborador, Double puntosSumados) {
        log.log(Level.INFO, i18nService.getMessage("contribucion.DistribucionViandas.confirmarSumaPuntos_info", puntosSumados, colaborador.getPersona().getNombre(2)), getClass().getSimpleName());
    }

    // Programo la tarea para ejecutarse todos los días a las 00.00 hs
    @Scheduled(cron = "0 0 0 * * ?")
    @Override
    protected void calcularPuntos() {
        List<DistribucionViandas> distribucionesViandas = obtenerDistribucionesViandasQueSumanPuntos();

        for (DistribucionViandas distribucionViandas : distribucionesViandas) {
            Double puntosASumar = Double.valueOf(distribucionViandas.getCantidadViandasAMover()) * multiplicadorPuntos;
            ColaboradorHumano colaborador = (ColaboradorHumano) distribucionViandas.getColaborador();
            colaboradorPuntosService.sumarPuntos(colaborador.getId(), puntosASumar);

            distribucionViandas.sumoPuntos();
            guardarContribucion(distribucionViandas);   // Al guardar la contribución, se guarda el colaborador por cascada (aunque ya había sido guardado)
            confirmarSumaPuntos(distribucionViandas.getId(), colaborador, puntosASumar);
        }
    }

    public void confirmarRetiro(Long distribucionViandasId) {
        DistribucionViandas distribucionViandas = obtenerContribucion(distribucionViandasId);
        distribucionViandas.confirmarRetiro();
        guardarContribucion(distribucionViandas);
    }
}
