package com.tp_anual.proyecto_heladeras_solidarias.validador_password;

import lombok.extern.java.Log;

@Log
public abstract class CriterioValidacion {
    public abstract Boolean validar(String contrasenia);
}
