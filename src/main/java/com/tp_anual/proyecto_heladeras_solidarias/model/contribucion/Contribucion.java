package com.tp_anual.proyecto_heladeras_solidarias.model.contribucion;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.Colaborador;

import com.tp_anual.proyecto_heladeras_solidarias.utils.SpringContext;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.context.MessageSource;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Log
@Getter
public abstract class Contribucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "colaborador")
    @Setter
    protected Colaborador colaborador;  // Debería ser final, pero necesito setear el Colaborador de las Contribuciones en el Migrador para evitar problemas de persistencia cíclica

    @Temporal(TemporalType.TIMESTAMP)
    @Setter
    protected LocalDateTime fechaContribucion;

    @Setter
    protected Boolean completada;

    @Setter
    protected Boolean yaSumoPuntos;

    @Setter
    private Boolean caducada;

    protected Contribucion() {}

    protected Contribucion(Colaborador vColaborador, LocalDateTime vFechaContribucion) {
        colaborador = vColaborador;
        fechaContribucion = vFechaContribucion;
        completada = false;
        yaSumoPuntos = false;
        caducada = false;
    }

    public String getNombre() {
        String nombre = getClass().getSimpleName();

        return switch (nombre) {
            case "CargaOferta" -> "Cargar Oferta";
            case "DistribucionViandas" -> "Distribuir Viandas";
            case "DonacionDinero" -> "Donar Dinero";
            case "DonacionVianda" -> "Donar Vianda";
            case "HacerseCargoDeHeladera" -> "Hacerse Cargo de Heladera";
            case "RegistroDePersonaEnSituacionVulnerable" -> "Registrar Persona en Situación Vulnerable";
            default -> "";
        };
    }

    public String getFechaContribucionFormateada() {
        if (fechaContribucion != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return fechaContribucion.format(formatter);
        }

        return "";
    }

    public void marcarComoCompletada() {
        setCompletada(true);
    }

    public void sumoPuntos() {
        setYaSumoPuntos(true);
    }

    public void seCompletoYSumoPuntos() {
        marcarComoCompletada();
        sumoPuntos();
    }

    public void obtenerDetalles() {
        MessageSource messageSource = SpringContext.getBean(MessageSource.class);
        String logMessage1 = messageSource.getMessage("contribucion.Contribucion.obtenerDetalles_out_nombre", new Object[] {getClass().getSimpleName()}, Locale.getDefault());
        String logMessage2 = messageSource.getMessage("contribucion.Contribucion.obtenerDetalles_out_colaborador_title", null, Locale.getDefault());

        System.out.println(logMessage1);
        System.out.println(logMessage2);
        colaborador.obtenerDetalles();
        
        if (fechaContribucion != null) {
            String logMessage3 = messageSource.getMessage("contribucion.Contribucion.obtenerDetalles_out_fecha_contribucion", new Object[] {fechaContribucion}, Locale.getDefault());
            System.out.println(logMessage3);
        }
    }
}
