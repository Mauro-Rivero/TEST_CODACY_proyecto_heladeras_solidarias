package com.tp_anual.proyecto_heladeras_solidarias.broker;

import com.tp_anual.proyecto_heladeras_solidarias.domain.heladera.SensorTemperatura;

public class MensajeTemperatura implements Mensaje {
    private final SensorTemperatura sensor;
    private final Float temperatura;

    public MensajeTemperatura(SensorTemperatura vSensor, Float vTemperatura) {
        sensor = vSensor;
        temperatura = vTemperatura;
    }

    public SensorTemperatura getSensor() {
        return sensor;
    }

    public Float getTemperatura() {
        return temperatura;
    }

    @Override
    public void procesar() {
        sensor.setTempActual(temperatura);
    }
}