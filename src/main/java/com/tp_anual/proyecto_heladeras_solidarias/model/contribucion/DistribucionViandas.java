package com.tp_anual.proyecto_heladeras_solidarias.model.contribucion;

import java.time.LocalDateTime;
import java.util.Locale;

import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.Colaborador;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;

import com.tp_anual.proyecto_heladeras_solidarias.utils.SpringContext;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Setter;
import lombok.extern.java.Log;
import lombok.Getter;
import org.springframework.context.MessageSource;

@Entity
@Log
@Getter
public class DistribucionViandas extends Contribucion {

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "heladera_origen")
    private Heladera origen;    // final

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "heladera_destino")
    private Heladera destino; // final

    @Min(value = 0)
    private Integer cantidadViandasAMover;  // final

    @Enumerated(EnumType.STRING)
    private  MotivoDistribucion motivo; // final

    @Setter
    private Boolean retiroRealizado;

    public enum MotivoDistribucion {
        DESPERFECTO_EN_LA_HELADERA,
        FALTA_DE_VIANDAS_EN_DESTINO
    }

    public DistribucionViandas() {
        super();
    }

    public DistribucionViandas(Colaborador vColaborador, LocalDateTime vFechaContribucion, Heladera vOrigen, Heladera vDestino, Integer vCantidadViandasAMover, MotivoDistribucion vMotivo) {
        super(vColaborador, vFechaContribucion);
        origen = vOrigen;
        destino = vDestino;
        cantidadViandasAMover = vCantidadViandasAMover;
        motivo = vMotivo;
        retiroRealizado = false;
    }

    @Override
    public void obtenerDetalles() {
        super.obtenerDetalles();

        MessageSource messageSource = SpringContext.getBean(MessageSource.class);
        String logMessage1 = messageSource.getMessage("contribucion.DistribucionViandas.obtenerDetalles_out_origen", new Object[] {origen.getNombre()}, Locale.getDefault());
        String logMessage2 = messageSource.getMessage("contribucion.DistribucionViandas.obtenerDetalles_out_destino", new Object[] {destino.getNombre()}, Locale.getDefault());
        String logMessage3 = messageSource.getMessage("contribucion.DistribucionViandas.obtenerDetalles_out_candidad_viandas_a_mover", new Object[] {cantidadViandasAMover}, Locale.getDefault());
        String logMessage4 = messageSource.getMessage("contribucion.DistribucionViandas.obtenerDetalles_out_motivo", new Object[] {motivo}, Locale.getDefault());

        System.out.println(logMessage1);
        System.out.println(logMessage2);
        System.out.println(logMessage3);
        System.out.println(logMessage4);
    }

    public void confirmarRetiro() {setRetiroRealizado(true);}

    public void caducar() {setCaducada(true);}
}
