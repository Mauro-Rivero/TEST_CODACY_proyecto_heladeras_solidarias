package com.tp_anual.proyecto_heladeras_solidarias.service.contribucion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;

import com.tp_anual.proyecto_heladeras_solidarias.exception.contribucion.DatosInvalidosCrearDonacionViandaException;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.Colaborador;
import com.tp_anual.proyecto_heladeras_solidarias.model.contribucion.Contribucion;
import com.tp_anual.proyecto_heladeras_solidarias.model.contribucion.DonacionVianda;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.vianda.Vianda;
import com.tp_anual.proyecto_heladeras_solidarias.model.ubicacion.Ubicacion;
import com.tp_anual.proyecto_heladeras_solidarias.service.i18n.I18nService;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Service
@Log
public class DonacionViandaCreator implements ContribucionCreator {

    private final I18nService i18nService;

    public DonacionViandaCreator(I18nService vI18nService) {
        i18nService = vI18nService;
    }

    @Override
    public Contribucion crearContribucionDefault(Colaborador colaborador, LocalDateTime fechaContribucion) {
        DonacionVianda donacionVianda = new DonacionVianda(colaborador, fechaContribucion, null, null);
        donacionVianda.seCompletoYSumoPuntos(); // Llamo directamente al método de donacionVianda, porque no quiero que se guarde en este momento

        return donacionVianda;
    }
    
    @Override
    public Contribucion crearContribucion(Colaborador colaborador, LocalDateTime fechaContribucion, Boolean paraMigrar, Object... args) throws DatosInvalidosCrearDonacionViandaException {
        if (paraMigrar)
            return crearContribucionDefault(colaborador, fechaContribucion);

        if (args.length != 2 ||
            !(args[0] instanceof Vianda) ||
            !(args[1] instanceof Heladera)) {
            
            log.log(Level.SEVERE, i18nService.getMessage("contribucion.DonacionViandaCreator.crearContribucion_err"));
            throw new DatosInvalidosCrearDonacionViandaException();
        }
        
        return new DonacionVianda(colaborador, fechaContribucion, (Vianda) args[0], (Heladera) args[1]);
    }
}
