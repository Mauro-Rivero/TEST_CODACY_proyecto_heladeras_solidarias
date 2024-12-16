package com.tp_anual.proyecto_heladeras_solidarias.model.incidente;

import java.time.LocalDateTime;

import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@NamedNativeQuery(
        name = "Alerta.findAlertasParaTecnico",
        query = "SELECT * FROM alerta AS a " +
                "WHERE a.id IN (" +
                "SELECT a0.id FROM alerta AS a0 " +
                "INNER JOIN tecnico AS t " +
                "ON a0.tecnico = t.id " +
                "WHERE t.id = :tecnico)",
        resultClass =  Alerta.class
)
@NamedNativeQuery(
        name = "Alerta.findAlertasNoResueltas",
        query = "SELECT * FROM alerta AS a " +
                "WHERE a.id IN (" +
                "SELECT a0.id FROM alerta AS a0 " +
                "LEFT JOIN visita AS v " +
                "ON a0.id = v.incidente " +
                "WHERE v.id ISNULL OR v.estado = false)",
        resultClass =  Alerta.class
)
@NamedNativeQuery(
        name = "Alerta.findAlertasSinTecnicoNoResueltas",
        query = "SELECT * FROM alerta AS a " +
                "WHERE a.id IN (" +
                "SELECT a0.id FROM alerta AS a0 " +
                "LEFT JOIN visita AS v " +
                "ON a0.id = v.incidente " +
                "WHERE (v.id ISNULL OR v.estado = false) AND a0.tecnico ISNULL)",
        resultClass =  Alerta.class
)
@Getter
public class Alerta extends Incidente {
    
    @Enumerated(EnumType.STRING)
    private TipoAlerta tipo;    // final

    public enum TipoAlerta {
        TEMPERATURA,
        FRAUDE,
        FALLA_CONEXION
    }

    public Alerta() {
        super();
    }

    public Alerta(LocalDateTime vFecha, Heladera vHeladera, TipoAlerta vTipo) {
        super(vFecha, vHeladera);
        tipo = vTipo;
    }

    public String getTipoAlertaString() {
        return switch (tipo) {
            case TipoAlerta.TEMPERATURA -> "Temperatura";
            case TipoAlerta.FRAUDE -> "Fraude";
            case TipoAlerta.FALLA_CONEXION -> "Falla de Conexión";
        };
    }
}
