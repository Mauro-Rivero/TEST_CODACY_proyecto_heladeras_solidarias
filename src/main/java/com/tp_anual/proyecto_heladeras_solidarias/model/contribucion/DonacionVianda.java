package com.tp_anual.proyecto_heladeras_solidarias.model.contribucion;

import java.time.LocalDateTime;
import java.util.Locale;

import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.Colaborador;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.vianda.Vianda;
import com.tp_anual.proyecto_heladeras_solidarias.utils.SpringContext;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.context.MessageSource;

@Entity
@Log
@Getter
public class DonacionVianda extends Contribucion {

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "vianda")
    private Vianda vianda;  // final

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "heladera")
    private Heladera heladera;    // final

    public DonacionVianda() {
        super();
    }

    public DonacionVianda(Colaborador vColaborador, LocalDateTime vFechaContribucion, Vianda vVianda, Heladera vHeladera) {
        super(vColaborador, vFechaContribucion);
        vianda = vVianda;
        heladera = vHeladera;
    }

    @Override
    public void obtenerDetalles() {
        super.obtenerDetalles();

        MessageSource messageSource = SpringContext.getBean(MessageSource.class);
        String logMessage1 = messageSource.getMessage("contribucion.DonacionVianda.obtenerDetalles_out_vianda_comida", new Object[] {vianda.getComida()}, Locale.getDefault());
        String logMessage2 = messageSource.getMessage("contribucion.DonacionVianda.obtenerDetalles_out_heladera", new Object[] {heladera.getNombre()}, Locale.getDefault());

        System.out.println(logMessage1);
        System.out.println(logMessage2);
    }

    public void caducar() {setCaducada(true);}
}