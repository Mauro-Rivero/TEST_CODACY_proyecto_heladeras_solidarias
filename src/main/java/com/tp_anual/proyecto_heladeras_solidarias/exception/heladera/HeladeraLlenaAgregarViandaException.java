package com.tp_anual.proyecto_heladeras_solidarias.exception.heladera;

import com.tp_anual.proyecto_heladeras_solidarias.utils.SpringContext;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class HeladeraLlenaAgregarViandaException extends Exception {
    public HeladeraLlenaAgregarViandaException() {
        super(SpringContext.getBean(MessageSource.class).getMessage("heladera.Heladera.agregarVianda_exception", null, Locale.getDefault()));
    }
}
