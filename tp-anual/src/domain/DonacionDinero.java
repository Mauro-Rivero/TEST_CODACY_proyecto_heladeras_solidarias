package domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DonacionDinero extends Contribucion {
    private Double monto;
    private FrecuenciaDePago frecuencia;
    private LocalDateTime ultimaActualizacion;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    
    enum FrecuenciaDePago { // Tal vez podriamos implementar una clase, para que no quede toda esta logica en DonacionDinero
        SEMANAL {
            @Override
            public long periodo() {
                return 1;
            }
    
            @Override
            public ChronoUnit unidad() {
                return ChronoUnit.WEEKS;
            }
        },
        MENSUAL {
            @Override
            public long periodo() {
                return 1;
            }
    
            @Override
            public ChronoUnit unidad() {
                return ChronoUnit.MONTHS;
            }
        },
        SEMESTRAL {
            @Override
            public long periodo() {
                return 6;
            }
    
            @Override
            public ChronoUnit unidad() {
                return ChronoUnit.MONTHS;
            }
        },
        ANUAL {
            public long periodo() {
                return 1;
            }
    
            @Override
            public ChronoUnit unidad() {
                return ChronoUnit.YEARS;
            }
        },
        UNICA_VEZ {
            @Override
            public long periodo() {
                return 0;
            }
    
            @Override
            public ChronoUnit unidad() {
                return null;
            }
        };
    
        public abstract long periodo();
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
    public void validarIdentidad(Colaborador colaboradorAspirante) {
        if(!(esColaboradorHumano(colaboradorAspirante) || esColaboradorJuridico(colaboradorAspirante))) {
            throw new IllegalArgumentException("El colaborador aspirante no es un Colaborador Humano");
        }
    }

    @Override
    public void accionar() {
        System.out.println(monto);
        System.out.println(frecuencia);
        // Esto es temporal, para que no tire errores. La logica es *registrar la donacion en el sistema*
    }

    public void calcularPuntos(Colaborador colaborador) {
        if (frecuencia == FrecuenciaDePago.UNICA_VEZ) {
            colaborador.sumarPuntos(monto * 0.5);
            return; // Corta la ejecucion del metodo
        }

        Runnable calculoPuntos = () -> {
            LocalDateTime ahora = LocalDateTime.now();
            long periodosPasados = frecuencia.unidad().between(ultimaActualizacion, ahora);
            if (periodosPasados >= frecuencia.periodo()) {
                colaborador.sumarPuntos(monto * 0.5);
                ultimaActualizacion = ahora;
            }
        };

        // Programa la tarea para que se ejecute una vez por dia
        scheduler.scheduleAtFixedRate(calculoPuntos, 0, 1, TimeUnit.DAYS);  // Ejecuta una vez por dia, puede ser ineficiente en casos como MENSUAL, SEMESTRAL o ANUAL
    }
}