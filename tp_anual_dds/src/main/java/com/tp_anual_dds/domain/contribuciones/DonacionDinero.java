package com.tp_anual_dds.domain.contribuciones;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.tp_anual_dds.domain.colaborador.Colaborador;

public class DonacionDinero extends Contribucion {
    private final Double monto;
    private final FrecuenciaDePago frecuencia;
    private LocalDateTime ultimaActualizacion;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Double multiplicador_puntos = 0.5;
    
    public enum FrecuenciaDePago { // Tal vez podriamos implementar una clase, para que no quede toda esta logica en DonacionDinero
        SEMANAL {
            @Override
            public Integer periodo() {
                return 1;
            }
    
            @Override
            public ChronoUnit unidad() {
                return ChronoUnit.WEEKS;
            }
        },
        MENSUAL {
            @Override
            public Integer periodo() {
                return 1;
            }
    
            @Override
            public ChronoUnit unidad() {
                return ChronoUnit.MONTHS;
            }
        },
        SEMESTRAL {
            @Override
            public Integer periodo() {
                return 6;
            }
    
            @Override
            public ChronoUnit unidad() {
                return ChronoUnit.MONTHS;
            }
        },
        ANUAL {
            @Override
            public Integer periodo() {
                return 1;
            }
    
            @Override
            public ChronoUnit unidad() {
                return ChronoUnit.YEARS;
            }
        },
        UNICA_VEZ {
            @Override
            public Integer periodo() {
                return 0;
            }
    
            @Override
            public ChronoUnit unidad() {
                return null;
            }
        };
    
        public abstract Integer periodo();
        public abstract ChronoUnit unidad();
    }

    public DonacionDinero(Colaborador vColaborador, LocalDateTime vFechaContribucion, Double vMonto, FrecuenciaDePago vFrecuencia) {
        colaborador = vColaborador;
        fechaContribucion = vFechaContribucion;
        monto = vMonto;
        frecuencia = vFrecuencia;
        ultimaActualizacion = LocalDateTime.now();
    }

    // obtenerDetalles()
    
    @Override
    protected void validarIdentidad() {}

    @Override
    protected void accionar() {
        System.out.println(this); // Esto es temporal, para que no tire errores. La logica es *registrar la donacion en el sistema*
    }

    @Override
    protected void calcularPuntos() {    
        if (frecuencia == FrecuenciaDePago.UNICA_VEZ) {
            colaborador.sumarPuntos(monto * multiplicador_puntos);
            return; // Corta la ejecucion del metodo
        } 

        Integer periodo = 1;
        TimeUnit unidad = TimeUnit.DAYS;

        Runnable calculoPuntos = () -> {
            LocalDateTime ahora = LocalDateTime.now();
            Long periodosPasados = frecuencia.unidad().between(ultimaActualizacion, ahora);
            if (periodosPasados >= frecuencia.periodo()) {
                colaborador.sumarPuntos(monto * multiplicador_puntos);
                ultimaActualizacion = ahora;
            }
        };

        // Programa la tarea para que se ejecute una vez por dia
        scheduler.scheduleAtFixedRate(calculoPuntos, 0, periodo, unidad);  // Ejecuta una vez por dia, puede ser ineficiente en casos como MENSUAL, SEMESTRAL o ANUAL
    }
}
