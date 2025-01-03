package com.tp_anual.proyecto_heladeras_solidarias.exception.colaborador;

import com.tp_anual.proyecto_heladeras_solidarias.utils.SpringContext;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class ContribucionNoPermitidaException extends Exception {
    public ContribucionNoPermitidaException() {
        super(SpringContext.getBean(MessageSource.class).getMessage("colaborador.Colaborador.colaborar_exception", null, Locale.getDefault()));
    }
}
