package com.tp_anual.proyecto_heladeras_solidarias.model.tarjeta;

import java.time.LocalDateTime;

import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;

import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.acciones_en_heladera.SolicitudAperturaColaborador;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import jakarta.persistence.*;

@Entity
@NamedNativeQuery(
    name = "PermisoApertura.findPermisoParaTarjetaAndHeladera",
    query = "SELECT * FROM permiso_apertura AS p " +
            "WHERE p.heladera = :heladera " +
            "AND p.tarjeta = :tarjeta " +
            "LIMIT 1",
    resultClass = PermisoApertura.class
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Log
@Getter
public class PermisoApertura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "heladera")
    @Setter
    private Heladera heladeraPermitida;

    @Temporal(TemporalType.TIMESTAMP)
    @Setter
    private LocalDateTime fechaOtorgamiento;

    // TODO: Arreglar el TypeString
    private SolicitudAperturaColaborador.MotivoSolicitud motivo;

    private Integer cantidadViandas;

    @Setter
    private Boolean otorgado;

    public PermisoApertura() {}

    public PermisoApertura(Heladera vHeladeraPermitida, LocalDateTime vFechaOtorgamiento, SolicitudAperturaColaborador.MotivoSolicitud vMotivo, Integer vCantidadViandas, Boolean vOtorgado) {
        heladeraPermitida = vHeladeraPermitida;
        fechaOtorgamiento = vFechaOtorgamiento;
        motivo = vMotivo;
        cantidadViandas = vCantidadViandas;
        otorgado = vOtorgado;
    }

    public void revocarPermiso() {
        setOtorgado(false);
    }
}