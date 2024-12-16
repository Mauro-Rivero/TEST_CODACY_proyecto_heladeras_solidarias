package com.tp_anual.proyecto_heladeras_solidarias.service.contribucion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;

import com.tp_anual.proyecto_heladeras_solidarias.exception.contribucion.DatosInvalidosCrearHCHException;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.Colaborador;
import com.tp_anual.proyecto_heladeras_solidarias.model.contribucion.Contribucion;
import com.tp_anual.proyecto_heladeras_solidarias.model.contribucion.HacerseCargoDeHeladera;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.vianda.Vianda;
import com.tp_anual.proyecto_heladeras_solidarias.model.ubicacion.Ubicacion;
import com.tp_anual.proyecto_heladeras_solidarias.service.i18n.I18nService;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Service
@Log
public class HacerseCargoDeHeladeraCreator implements ContribucionCreator {

    private final I18nService i18nService;

    public HacerseCargoDeHeladeraCreator(I18nService vI18nService) {
        i18nService = vI18nService;
    }

    @Override
    public Contribucion crearContribucionDefault(Colaborador colaborador, LocalDateTime fechaContribucion) {
        HacerseCargoDeHeladera hacerseCargoDeHeladera = new HacerseCargoDeHeladera(colaborador, fechaContribucion, null);
        hacerseCargoDeHeladera.seCompletoYSumoPuntos(); // Llamo directamente al método de hacerseCargoDeHeladera, porque no quiero que se guarde en este momento

        return hacerseCargoDeHeladera;
    }

    @Override
    public Contribucion crearContribucion(Colaborador colaborador, LocalDateTime fechaContribucion, Boolean paraMigrar, Object... args) throws DatosInvalidosCrearHCHException {
        if (paraMigrar)
            return crearContribucionDefault(colaborador, fechaContribucion);

        if (args.length != 1 ||
            !(args[0] instanceof Heladera)) {
            
            log.log(Level.SEVERE, i18nService.getMessage("contribucion.HacerseCargoDeHeladeraCreator.crearContribucion_err"));
            throw new DatosInvalidosCrearHCHException();
        }
        
        return new HacerseCargoDeHeladera(colaborador, fechaContribucion, (Heladera) args[0]);
    }
}
