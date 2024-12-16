package com.tp_anual.proyecto_heladeras_solidarias.model.heladera;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.vianda.Vianda;
import com.tp_anual.proyecto_heladeras_solidarias.model.incidente.Incidente;
import com.tp_anual.proyecto_heladeras_solidarias.model.ubicacion.Ubicacion;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Min;

@Entity
@NamedNativeQuery(
        name = "Heladera.findHeladerasParaColaborador",
        query = "SELECT * FROM heladera AS h " +
                "WHERE h.id IN (" +
                "SELECT h0.id FROM heladera AS h0 " +
                "INNER JOIN hacerse_cargo_de_heladera AS hcdh " +
                "ON h0.id = hcdh.heladera " +
                "INNER JOIN contribucion AS c " +
                "ON hcdh.id = c.id " +
                "INNER JOIN colaborador AS cj " +
                "ON c.colaborador = cj.id )",
        resultClass =  Heladera.class
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
public class Heladera implements HeladeraObserver {    // Implementa una Interfaz "HeladeraSubject" a nivel conceptual

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Setter
    protected String nombre;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ubicacion")
    @Setter
    protected Ubicacion ubicacion;
    
    @Min(value = 0)
    protected Integer capacidad;    // final

    protected Float tempMin;    // final

    protected Float tempMax;    // final

    @OneToMany(mappedBy = "heladera", fetch = FetchType.EAGER)  // Quiero que los cambios en Vianda se hagan manualmente, y no que se propaguen
    protected List<Vianda> viandas;    // final

    @Setter
    protected Integer cantidadReservada;

    @Setter
    protected Float tempActual;

    @Temporal(TemporalType.TIMESTAMP)
    @Setter
    protected LocalDateTime fechaApertura;

    @Setter
    protected Boolean estado;

    protected Integer tiempoPermiso = 3;    // final

    protected ChronoUnit unidadTiempoPermiso = ChronoUnit.HOURS;  // final

    public Heladera() {}

    public Heladera(String vNombre, Ubicacion vUbicacion, Integer vCapacidad, Float vTempMin, Float vTempMax, List<Vianda> vViandas, Float vTempActual, LocalDateTime vFechaApertura, Boolean vEstado) {
        nombre = vNombre;
        ubicacion = vUbicacion;
        capacidad = vCapacidad;
        tempMin = vTempMin;
        tempMax = vTempMax;
        viandas = vViandas;
        cantidadReservada = vViandas.size();
        tempActual = vTempActual;
        fechaApertura = vFechaApertura;
        estado = vEstado;
    }

    public String getFechaAperturaFormateada() {
        if (fechaApertura != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return fechaApertura.format(formatter);
        }

        return "";
    }

    public String getEstadoString() {
        return estado ? "Activa" : "Inactiva";
    }

    public Integer viandasActuales() {return viandas.size();}

    public void actualizarFechaApertura() {setFechaApertura(LocalDateTime.now());}

    public void marcarComoActiva() {setEstado(true);}

    public void marcarComoInactiva() {setEstado(false);}
}
