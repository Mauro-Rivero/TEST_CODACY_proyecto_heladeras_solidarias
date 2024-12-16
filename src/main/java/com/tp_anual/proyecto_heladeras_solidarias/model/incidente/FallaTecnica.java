package com.tp_anual.proyecto_heladeras_solidarias.model.incidente;

import java.time.LocalDateTime;

import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.Colaborador;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@NamedNativeQuery(
        name = "FallaTecnica.findFallasTecnicasParaTecnico",
        query = "SELECT * FROM falla_tecnica AS f " +
                "WHERE f.id IN (" +
                "SELECT f0.id FROM falla_tecnica AS f0 " +
                "INNER JOIN tecnico AS t " +
                "ON f0.tecnico = t.id " +
                "WHERE t.id = :tecnico)",
        resultClass =  FallaTecnica.class
)
@NamedNativeQuery(
        name = "FallaTecnica.findFallasTecnicasNoResueltas",
        query = "SELECT * FROM falla_tecnica AS f " +
                "WHERE f.id IN (" +
                "SELECT f0.id FROM falla_tecnica AS f0 " +
                "LEFT JOIN visita AS v " +
                "ON f0.id = v.incidente " +
                "WHERE v.id ISNULL OR v.estado = false)",
        resultClass =  FallaTecnica.class
)
// TODO: El LEFT JOIN, por algún motivo, hace que se rompa cuando tenemos un array de Incidentes que incluye Fallas Técnicas y Alertas (si ponemos INNER JOIN se resuelve pero no estamos teniendo en cuenta las Fallas Técnicas que no fueron revisadas)
@NamedNativeQuery(
        name = "FallaTecnica.findFallasTecnicasSinTecnicoNoResueltas",
        query = "SELECT * FROM falla_tecnica AS f " +
                "WHERE f.id IN (" +
                "SELECT f0.id FROM falla_tecnica AS f0 " +
                "LEFT JOIN visita AS v " +
                "ON f0.id = v.incidente " +
                "WHERE (v.id ISNULL OR v.estado = false) AND f0.tecnico ISNULL)",
        resultClass =  FallaTecnica.class
)
@Getter
public class FallaTecnica extends Incidente {
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "colaborador")
    private Colaborador colaborador;    // final

    private String descripcion; // final
    
    private String foto;    // final

    public FallaTecnica() {
        super();
    }

    public FallaTecnica(LocalDateTime vFecha, Heladera vHeladera, Colaborador vColaborador, String vDescripcion, String vFoto) {
        super(vFecha, vHeladera);
        colaborador = vColaborador;
        descripcion = vDescripcion;
        foto = vFoto;
    }

    public String getNombre() {
        return "Falla Técnica";
    }
}
