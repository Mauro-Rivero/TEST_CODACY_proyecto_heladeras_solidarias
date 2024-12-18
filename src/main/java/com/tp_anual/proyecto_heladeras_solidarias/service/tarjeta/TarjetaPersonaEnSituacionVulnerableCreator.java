package com.tp_anual.proyecto_heladeras_solidarias.service.tarjeta;

import java.util.logging.Level;

import com.tp_anual.proyecto_heladeras_solidarias.exception.tarjeta.DatosInvalidosCrearTarjetaPESVException;
import com.tp_anual.proyecto_heladeras_solidarias.model.persona_en_situacion_vulnerable.PersonaEnSituacionVulnerable;
import com.tp_anual.proyecto_heladeras_solidarias.service.i18n.I18nService;
import com.tp_anual.proyecto_heladeras_solidarias.model.tarjeta.Tarjeta;
import com.tp_anual.proyecto_heladeras_solidarias.model.tarjeta.TarjetaPersonaEnSituacionVulnerable;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Service
@Log
public class TarjetaPersonaEnSituacionVulnerableCreator implements TarjetaCreator {

    private final GeneradorCodigo generadorCodigo;

    private final I18nService i18nService;

    public TarjetaPersonaEnSituacionVulnerableCreator(GeneradorCodigo vGeneradorCodigo, I18nService vI18nService) {
        generadorCodigo = vGeneradorCodigo;

        i18nService = vI18nService;
    }

    @Override
    public Tarjeta crearTarjeta(Object titular) throws DatosInvalidosCrearTarjetaPESVException {
        if (!(titular instanceof PersonaEnSituacionVulnerable)) {
            log.log(Level.SEVERE, i18nService.getMessage("tarjeta.TarjetaPersonaEnSituacionVulnerableCreator.crearTarjeta_err"));
            throw new DatosInvalidosCrearTarjetaPESVException();
        }

        String codigo = generadorCodigo.generarCodigo(false);
        return new TarjetaPersonaEnSituacionVulnerable(codigo, (PersonaEnSituacionVulnerable) titular);
    }
}
