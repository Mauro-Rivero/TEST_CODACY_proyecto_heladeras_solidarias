package com.tp_anual.proyecto_heladeras_solidarias.model.reporte;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@Entity
@Subselect(
        "SELECT c.id AS colaborador_id, TRIM(pf.nombre) || ' ' || TRIM(pf.apellido) AS colaborador_nombre, COUNT(v.id) AS cantidad_viandas " +
        "FROM colaborador c " +
        "LEFT JOIN vianda v " +
        "ON v.colaborador = c.id " +
        "LEFT JOIN persona_fisica pf " +
        "ON c.persona = pf.id " +
        "WHERE c.tipo_colaborador = 'Humano' " +
        "GROUP BY c.id, pf.nombre, pf.apellido " +
        "ORDER BY 3 DESC"
)
@Immutable
@Getter
public class ViandasPorColaborador {

    @Id
    private Long colaboradorId;

    private String colaboradorNombre;

    private Integer cantidadViandas;
}
