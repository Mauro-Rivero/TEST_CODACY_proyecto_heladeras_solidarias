package src;

import java.util.ArrayList;

public abstract class Colaborador {
    protected ArrayList<MedioDeContacto> mediosDeContacto;
    protected String direccion;
    protected ArrayList<Contribucion> contribuciones;
    protected ArrayList<Oferta> beneficiosAdquiridos;

    // darDeAlta()
    // darDeBaja()
    // modificar()

    public Double puntos() {
        Double puntos = 0.0;

        // Coeficientes Variables
        Float coeficienteDonacionDinero = 0.5f;
        Float coeficienteDistribucionDeViandas = 1f;
        Float coeficienteDonacionVianda = 1.5f;
        Float coeficienteRegistroDePersonaEnSituacionVulnerable = 2f;
        Float coeficienteHacerseCargoDeHeladera = 5f;

        // Sumatoria de Puntos
        for (Contribucion contribucion : contribuciones) {
            switch (contribucion.getClass().getSimpleName()) {
            case "DonacionDinero":
                DonacionDinero donacionDinero = (DonacionDinero) contribucion;
                puntos += donacionDinero.donadoHastaLaFecha() * coeficienteDonacionDinero;
                break;
            case "DistribucionDeViandas":
                DistribucionViandas distribucionViandas = (DistribucionViandas) contribucion;
                puntos += distribucionViandas.cantidadViandasAMover() * coeficienteDistribucionDeViandas;
                break;
            case "DonacionVianda":
                puntos += coeficienteDonacionVianda;
                break;
            case "RegistroDePersonaEnSituacionVulnerable":
                RegistroDePersonaEnSituacionEnVulnerable registroDePersonaEnSituacionEnVulnerable = (RegistroDePersonaEnSituacionEnVulnerable) contribucion;
                puntos += registroDePersonaEnSituacionEnVulnerable.tarjetasAsignadas().size() * coeficienteRegistroDePersonaEnSituacionVulnerable;
                break;
            case "HacerseCargoDeHeladera":
                HacerseCargoDeHeladera hacerseCargoDeHeladera = (HacerseCargoDeHeladera) contribucion;
                puntos += hacerseCargoDeHeladera.mesesHaciendoseCargo() * coeficienteHacerseCargoDeHeladera;
                break;
            default:
                break;
            }
        }

        return puntos;
    }

    public void adquirirBeneficio(Oferta oferta) {
        beneficiosAdquiridos.add(oferta);
    }
}