package src;

import java.time.LocalDate;
import java.time.Period;

public class DonacionDinero extends Contribucion {
    private Double monto;
    private FrecuenciaDePago frecuencia;
    
    enum FrecuenciaDePago {
        SEMANAL,
        MENSUAL,
        SEMESTRAL,
        ANUAL,
        UNICA_VEZ
    }

    public DonacionDinero(Colaborador vColaborador, LocalDate vFechaContribucion, Double vMonto, FrecuenciaDePago vFrecuencia) {
        colaborador = vColaborador;
        fechaContribucion = vFechaContribucion;
        monto = vMonto;
        frecuencia = vFrecuencia;
    }

    // obtenerDetalles()
    
    public void validarIdentidad(Colaborador colaboradorAspirante) {
        if(!(esColaboradorHumano(colaboradorAspirante) || esColaboradorJuridico(colaboradorAspirante))) {
            throw new IllegalArgumentException("El colaborador aspirante no es un Colaborador Humano");
        }
    }

    public Double donadoHastaLaFecha() {
        LocalDate fechaActual = LocalDate.now();
        Period periodo = Period.between(fechaContribucion, fechaActual);
        
        switch(frecuencia) {
        
        case UNICA_VEZ:
            return monto;

        case SEMANAL:
            Integer dias = periodo.getDays() + periodo.getMonths() * 30 + periodo.getYears() * 365; // No es precisa, hay que actualizarla
            Integer semanas = dias / 7;

            return semanas * monto;

        case MENSUAL:
            Integer meses = periodo.getMonths() + periodo.getYears() * 12;

            return meses * monto;

        case SEMESTRAL:
            Integer semestres = (periodo.getMonths() + periodo.getYears() * 12) / 6;

            return semestres * monto;

        case ANUAL:
            return periodo.getYears() * monto;
            
        default:
            return 0d;
        }
    }

    // accionar()
}
