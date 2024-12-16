package com.tp_anual.proyecto_heladeras_solidarias.model.incidente;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;

import com.tp_anual.proyecto_heladeras_solidarias.model.tecnico.Tecnico;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@NamedNativeQuery(
        name = "Incidente.findIncidentesParaTecnico",
        query = "SELECT * FROM alerta AS a " +
                "INNER JOIN tecnico AS t1 " +
                "ON a.tecnico = t1.id " +
                "WHERE t1.id = :tecnico " +
                "UNION ALL " +
                "SELECT * FROM falla_tecnica AS f " +
                "INNER JOIN tecnico AS t2 " +
                "ON f.tecnico = t2.id " +
                "WHERE t2.id = :tecnico",
        resultClass =  Incidente.class
)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
public abstract class Incidente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Setter
    protected LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "heladera")
    @Setter
    protected Heladera heladera;

    // Este atributo sólo nos sirve para manejarlo en el backend
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tecnico")
    @Setter
    protected Tecnico tecnico;

    protected Incidente() {}

    protected Incidente(LocalDateTime vFecha, Heladera vHeladera) {
        fecha = vFecha;
        heladera = vHeladera;
    }

    public String getFechaFormateada() {
        if (fecha != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return fecha.format(formatter);
        }

        return "";
    }

    public Boolean tieneTecnico() {
        return tecnico != null;
    }
}
