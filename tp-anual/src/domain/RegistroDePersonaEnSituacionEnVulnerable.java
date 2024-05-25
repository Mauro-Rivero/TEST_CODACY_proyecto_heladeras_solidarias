package domain;
import java.util.ArrayList;
import java.time.LocalDate;

public class RegistroDePersonaEnSituacionEnVulnerable extends Contribucion {
    private Tarjeta tarjetaAsignada;
    
    public RegistroDePersonaEnSituacionEnVulnerable(Colaborador vColaborador, LocalDate vFechaContribucion, Tarjeta vTarjetaAsignada) {
        colaborador = vColaborador;
        fechaContribucion = vFechaContribucion;
        tarjetaAsignada = vTarjetaAsignada;
    }

    public Tarjeta tarjetaAsignada() {
        return tarjetaAsignada;
    }

    // obtenerDetalles()
    
    public void validarIdentidad(Colaborador colaboradorAspirante) {
        if(!esColaboradorHumano(colaboradorAspirante) && !colaboradorAspirante.tieneDomicilio()) {
            throw new IllegalArgumentException("El colaborador aspirante no es un Colaborador Humano");
        }
    }

    public void accionar() {
        tarjetaAsignada.titular().tarjeta(tarjetaAsignada);
        System.out.println(tarjetaAsignada);    // Esto es temporal, para que no tire errores. La idea es *agregar la tarjeta al sistema*
    }
}