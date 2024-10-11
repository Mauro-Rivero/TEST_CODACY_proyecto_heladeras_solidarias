package com.tp_anual.proyecto_heladeras_solidarias.service.contribucion;

import com.tp_anual.proyecto_heladeras_solidarias.i18n.I18n;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.Colaborador;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.ColaboradorHumano;
import com.tp_anual.proyecto_heladeras_solidarias.model.contribucion.DonacionDinero;
import com.tp_anual.proyecto_heladeras_solidarias.model.contribucion.DonacionVianda;
import com.tp_anual.proyecto_heladeras_solidarias.repository.contribucion.ContribucionRepository;
import com.tp_anual.proyecto_heladeras_solidarias.repository.contribucion.DonacionViandaRepository;
import com.tp_anual.proyecto_heladeras_solidarias.service.colaborador.ColaboradorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.java.Log;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.logging.Level;

@Service
@Log
public class DonacionViandaService extends ContribucionService {

    private final DonacionViandaRepository donacionViandaRepository;
    private final Double multiplicadorPuntos = 1.5;

    public DonacionViandaService(ContribucionRepository vContribucionRepository, ColaboradorService vColaboradorService, DonacionViandaRepository vDonacionViandaRepository) {
        super(vContribucionRepository, vColaboradorService);
        donacionViandaRepository = vDonacionViandaRepository;
    }

    public DonacionVianda obtenerDonacionVianda(Long donacionViandaId) {
        return donacionViandaRepository.findById(donacionViandaId).orElseThrow(() -> new EntityNotFoundException("Entidad no encontrada"));
    }

    public ArrayList<DonacionVianda> obtenerDonacionesViandaQueSumanPuntos() {
        return new ArrayList<>(donacionViandaRepository.findByYaSumoPuntosFalse());
    }

    public DonacionVianda guardarDonacionVianda(DonacionVianda donacionVianda) {
        return donacionViandaRepository.save(donacionVianda);
    }

    @Override
    public void validarIdentidad(Long contribucionId, Long colaboradorId) {
        ColaboradorHumano colaborador = colaboradorService.obtenerColaboradorHumano(colaboradorId);

        if (colaborador.getDomicilio() == null) {
            log.log(Level.SEVERE, I18n.getMessage("contribucion.DonacionVianda.validarIdentidad_err", colaborador.getPersona().getNombre(2)));
            throw new IllegalArgumentException(I18n.getMessage("contribucion.DonacionVianda.validarIdentidad_exception"));
        }
    }

    @Override
    protected void confirmarSumaPuntos(Long contribucionId, Long colaboradorId, Double puntosSumados) {
        ColaboradorHumano colaborador = colaboradorService.obtenerColaboradorHumano(colaboradorId);
        log.log(Level.INFO, I18n.getMessage("contribucion.DonacionVianda.confirmarSumaPuntos_info", puntosSumados, colaborador.getPersona().getNombre(2)), getClass().getSimpleName());
    }

    // Programo la tarea para ejecutarse todos los días a las 00.00 hs
    @Scheduled(cron = "0 0 0 * * ?")
    @Override
    protected void calcularPuntos() {
        ArrayList<DonacionVianda> donacionesVianda = obtenerDonacionesViandaQueSumanPuntos();

        for (DonacionVianda donacionVianda : donacionesVianda) {
            Colaborador colaborador = donacionVianda.getColaborador();

            colaborador.sumarPuntos(multiplicadorPuntos);
            colaboradorService.guardarColaborador(colaborador);
            
            donacionVianda.setYaSumoPuntos(true);
            guardarDonacionVianda(donacionVianda);
            confirmarSumaPuntos(donacionVianda.getId(), colaborador.getId(), multiplicadorPuntos);
        }
    }
}
