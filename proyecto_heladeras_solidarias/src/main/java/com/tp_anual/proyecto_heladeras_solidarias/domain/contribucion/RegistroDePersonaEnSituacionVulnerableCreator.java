package com.tp_anual.proyecto_heladeras_solidarias.domain.contribucion;

import java.time.LocalDateTime;

import com.tp_anual.proyecto_heladeras_solidarias.domain.colaborador.Colaborador;
import com.tp_anual.proyecto_heladeras_solidarias.domain.tarjeta.TarjetaPersonaEnSituacionVulnerable;

public class RegistroDePersonaEnSituacionVulnerableCreator implements ContribucionCreator {
    @Override
    public Contribucion crearContribucion(Colaborador colaborador, LocalDateTime fechaContribucion, Object... args) {
        if(args.length != 1 ||
            !(args[0] instanceof TarjetaPersonaEnSituacionVulnerable))
            
            throw new IllegalArgumentException("Datos inválidos para registrar una persona en situación vulnerable");
        
        return new RegistroDePersonaEnSituacionVulnerable(colaborador, fechaContribucion, (TarjetaPersonaEnSituacionVulnerable) args[0]);
    }
}
