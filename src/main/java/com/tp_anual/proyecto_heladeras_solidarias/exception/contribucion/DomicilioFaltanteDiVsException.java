package com.tp_anual.proyecto_heladeras_solidarias.exception.contribucion;

import com.tp_anual.proyecto_heladeras_solidarias.utils.SpringContext;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class DomicilioFaltanteDiVsException extends Exception {
    public DomicilioFaltanteDiVsException() {
        super(SpringContext.getBean(MessageSource.class).getMessage( "contribucion.DistribucionViandas.validarIdentidad_exception", null, Locale.getDefault()));
    }
}
