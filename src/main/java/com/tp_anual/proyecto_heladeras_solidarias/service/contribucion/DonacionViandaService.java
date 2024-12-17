package com.tp_anual.proyecto_heladeras_solidarias.service.contribucion;

import com.tp_anual.proyecto_heladeras_solidarias.exception.contribucion.DomicilioFaltanteDoVException;
import com.tp_anual.proyecto_heladeras_solidarias.service.i18n.I18nService;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.Colaborador;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.ColaboradorHumano;
import com.tp_anual.proyecto_heladeras_solidarias.model.contribucion.Contribucion;
import com.tp_anual.proyecto_heladeras_solidarias.model.contribucion.DistribucionViandas;
import com.tp_anual.proyecto_heladeras_solidarias.model.contribucion.DonacionVianda;
import com.tp_anual.proyecto_heladeras_solidarias.repository.contribucion.DonacionViandaRepository;
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
public class DonacionViandaService extends ContribucionService {

    private final DonacionViandaRepository donacionViandaRepository;
    private final ColaboradorPuntosService colaboradorPuntosService;
    private final Double multiplicadorPuntos = 1.5;

    private final I18nService i18nService;

    public DonacionViandaService(DonacionViandaRepository vDonacionViandaRepository, ColaboradorPuntosService vColaboradorPuntosService, I18nService vI18nService) {
        super();
        donacionViandaRepository = vDonacionViandaRepository;
        colaboradorPuntosService = vColaboradorPuntosService;

        i18nService = vI18nService;
    }

    @Override
    public DonacionVianda obtenerContribucion(Long donacionViandaId) {
        return donacionViandaRepository.findById(donacionViandaId).orElseThrow(() -> new EntityNotFoundException(i18nService.getMessage("obtenerEntidad_exception")));
    }

    public DonacionVianda obtenerDonacionViandaMasRecientePorColaborador(ColaboradorHumano colaborador) {
        return donacionViandaRepository.findTopByColaboradorOrderByFechaContribucionDesc(colaborador);
    }

    public List<DonacionVianda> obtenerDonacionesViandaQueSumanPuntos() {
        return new ArrayList<>(donacionViandaRepository.findByYaSumoPuntosFalse());
    }

    public List<DonacionVianda> obtenerDonacionesViandaNoConfirmadasParaColaborador(ColaboradorHumano colaborador) {
        return new ArrayList<>(donacionViandaRepository.findByColaboradorAndCompletadaFalseAndCaducadaFalse(colaborador));
    }

    @Override
    public DonacionVianda guardarContribucion(Contribucion donacionVianda) {
        return donacionViandaRepository.saveAndFlush((DonacionVianda) donacionVianda);
    }

    public void caducar(Long donacionViandaId) {
        DonacionVianda donacionVianda = obtenerContribucion(donacionViandaId);

        donacionVianda.caducar();
        guardarContribucion(donacionVianda);
    }

    @Override
    public void validarIdentidad(Long donacionViandaId, Colaborador colaborador) throws DomicilioFaltanteDoVException {
        if (colaborador.getDomicilio() == null) {
            log.log(Level.SEVERE, i18nService.getMessage("contribucion.DonacionVianda.validarIdentidad_err", colaborador.getPersona().getNombre(2)));
            throw new DomicilioFaltanteDoVException();
        }
    }

    @Override
    protected void confirmarSumaPuntos(Long donacionViandaId, Colaborador colaborador, Double puntosSumados) {
        log.log(Level.INFO, i18nService.getMessage("contribucion.DonacionVianda.confirmarSumaPuntos_info", puntosSumados, colaborador.getPersona().getNombre(2)), getClass().getSimpleName());
    }

    // Programo la tarea para ejecutarse todos los días a las 00.00 hs
    @Scheduled(cron = "0 0 0 * * ?")
    @Override
    protected void calcularPuntos() {
        List<DonacionVianda> donacionesVianda = obtenerDonacionesViandaQueSumanPuntos();

        for (DonacionVianda donacionVianda : donacionesVianda) {
            Colaborador colaborador = donacionVianda.getColaborador();
            colaboradorPuntosService.sumarPuntos(colaborador.getId(), multiplicadorPuntos);

            donacionVianda.sumoPuntos();
            guardarContribucion(donacionVianda);    // Al guardar la contribución, se guarda el colaborador por cascada (aunque ya había sido guardado)
            confirmarSumaPuntos(donacionVianda.getId(), colaborador, multiplicadorPuntos);
        }
    }
}
