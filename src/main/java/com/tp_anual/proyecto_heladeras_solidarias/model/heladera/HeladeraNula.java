package com.tp_anual.proyecto_heladeras_solidarias.model.heladera;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.vianda.Vianda;
import com.tp_anual.proyecto_heladeras_solidarias.model.ubicacion.Ubicacion;
import com.tp_anual.proyecto_heladeras_solidarias.utils.SpringContext;
import lombok.extern.java.Log;
import org.springframework.context.MessageSource;

@Log
public class HeladeraNula extends Heladera {

    public HeladeraNula() {
        super("N/A", null, 0, 0f, 0f, new ArrayList<>(), 0f, null, false);
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public Ubicacion getUbicacion() {
        MessageSource messageSource = SpringContext.getBean(MessageSource.class);
        String logMessage = messageSource.getMessage("heladera.HeladeraNula.getUbicacion_err", null, Locale.getDefault());
        String exceptionMessage = messageSource.getMessage("heladera.HeladeraNula.getUbicacion_exception", null, Locale.getDefault());

        log.log(Level.SEVERE, logMessage);
        throw new UnsupportedOperationException(exceptionMessage);
    }

    @Override
    public Integer getCapacidad() {
        return capacidad;
    }

    @Override
    public Float getTempMin() {
        return tempMin;
    }

    @Override public Float getTempMax() {
        return tempMax;
    }

    @Override
    public List<Vianda> getViandas() {
        return viandas;
    }

    @Override
    public Float getTempActual() {
        return tempActual;
    }

    @Override
    public LocalDateTime getFechaApertura() {
        MessageSource messageSource = SpringContext.getBean(MessageSource.class);
        String logMessage = messageSource.getMessage("heladera.HeladeraNula.getFechaApertura_err", null, Locale.getDefault());
        String exceptionMessage = messageSource.getMessage("heladera.HeladeraNula.getFechaApertura_exception", null, Locale.getDefault());

        log.log(Level.SEVERE, logMessage);
        throw new UnsupportedOperationException(exceptionMessage);
    }

    @Override
    public Boolean getEstado() {
        return false;
    }

    @Override
    public void setNombre(String vNombre) {}

    @Override
    public void setUbicacion(Ubicacion vUbicacion) {}

    @Override
    public void setFechaApertura(LocalDateTime vFechaApertura) {}

    @Override
    public void setEstado(Boolean vEstado) {}

    @Override
    public Integer viandasActuales() {
        return 0;
    }

    @Override
    public void marcarComoInactiva() {}
}
