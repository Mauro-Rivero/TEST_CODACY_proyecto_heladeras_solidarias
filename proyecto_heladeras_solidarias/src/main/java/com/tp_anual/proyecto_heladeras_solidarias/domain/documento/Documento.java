package com.tp_anual.proyecto_heladeras_solidarias.domain.documento;

import lombok.Getter;

@Getter
public class Documento {
    private final TipoDocumento tipo;
    private final String numero;
    private final Sexo sexo;
    
    public enum TipoDocumento {
        DNI,
        PASAPORTE,
        LICENCIA_CONDUCIR,
        LIBRETA_CIVICA,
        LIBRETA_ENROLAMIENTO;
    }
    
    public enum Sexo {
        MASCULINO,
        FEMENINO,
        OTRO
    }
        
    public Documento(TipoDocumento vTipo, String vNumero, Sexo vSexo) {
        tipo = vTipo;
        numero = vNumero;
        sexo = vSexo;
    }
}