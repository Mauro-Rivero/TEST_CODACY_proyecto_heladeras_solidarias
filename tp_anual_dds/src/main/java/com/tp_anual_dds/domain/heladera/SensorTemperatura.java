package com.tp_anual_dds.domain.heladera;

import java.util.concurrent.TimeUnit;

public class SensorTemperatura extends Sensor {
    private Float tempActual;

    public SensorTemperatura(Heladera vHeladera) {
        heladera = vHeladera;
        tempActual = 0f;
    }

    public Boolean funcionaSensorFisico() {
        return true;
    }

    @Override
    public void notificar() {
        heladera.setTempActual(tempActual);
    }

    public void notificarFalla() {
        heladera.alertarFallaConexion();
    }
    
    @Override
    public void programarNotificacion() {
        Runnable notificacionTemperatura = () -> {
            if (!funcionaSensorFisico()) {
                notificarFalla();
            }
            notificar();
        };
        
        // Programa la tarea para que se ejecute cada 5 minutos
        scheduler.scheduleAtFixedRate(notificacionTemperatura, 0, 5, TimeUnit.MINUTES);
    }

    // Esto en si no hace nada util, faltaria la vinculacion con el sensor fisico
}