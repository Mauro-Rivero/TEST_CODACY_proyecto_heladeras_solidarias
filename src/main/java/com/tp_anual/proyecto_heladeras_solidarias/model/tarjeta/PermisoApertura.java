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
    name = "PermisoApertura.findPermisoMasRecienteParaTarjetaAndHeladera",
    query = "SELECT * FROM permiso_apertura AS p " +
            "WHERE p.heladera = :heladera " +
            "AND p.tarjeta = :tarjeta " +
            "ORDER BY p.fecha_otorgamiento DESC " +
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

    @Enumerated(EnumType.STRING)
    private SolicitudAperturaColaborador.MotivoSolicitud motivo;

    private Integer cantidadViandas;

    @Setter
    private Boolean otorgado;

    // 0 es posible de revocar, 1 es revocado y 2 es usado (imposible de revocar)
    @Setter
    private Integer revocado;

    public PermisoApertura() {}

    public PermisoApertura(Heladera vHeladeraPermitida, LocalDateTime vFechaOtorgamiento, SolicitudAperturaColaborador.MotivoSolicitud vMotivo, Integer vCantidadViandas, Boolean vOtorgado, Integer vRevocado) {
        heladeraPermitida = vHeladeraPermitida;
        fechaOtorgamiento = vFechaOtorgamiento;
        motivo = vMotivo;
        cantidadViandas = vCantidadViandas;
        otorgado = vOtorgado;
        revocado = vRevocado;
    }

    public void revocarPermiso() {
        setRevocado(1);
    }

    public void impedirRevocamientoPermiso() {
        setRevocado(2);
    }
}