package com.tp_anual.proyecto_heladeras_solidarias.domain.heladera;

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.tp_anual.proyecto_heladeras_solidarias.domain.colaborador.Colaborador;
import com.tp_anual.proyecto_heladeras_solidarias.domain.contacto.MedioDeContacto;
import com.tp_anual.proyecto_heladeras_solidarias.domain.incidente.Alerta;
import com.tp_anual.proyecto_heladeras_solidarias.domain.incidente.Incidente;
import com.tp_anual.proyecto_heladeras_solidarias.domain.suscripcion.Suscripcion.CondicionSuscripcion;
import com.tp_anual.proyecto_heladeras_solidarias.domain.ubicacion.Ubicacion;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
public abstract class Heladera implements HeladeraObserver {    // Implementa una Interfaz "HeladeraSubject" a nivel conceptual

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    protected String nombre;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ubicacion_id")
    protected Ubicacion ubicacion;
    
    protected final Integer capacidad;

    protected final Float tempMin;

    protected final Float tempMax;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "heladera_id")
    protected final ArrayList<Vianda> viandas;
    
    protected Float tempActual;

    @Temporal(TemporalType.TIMESTAMP)
    protected LocalDateTime fechaApertura;

    protected Boolean estado;
    
    @Transient
    protected GestorDeAperturas gestorDeAperturas;

    protected Heladera(String vNombre, Ubicacion vUbicacion, Integer vCapacidad, Float vTempMin, Float vTempMax, ArrayList<Vianda> vViandas, Float vTempActual, LocalDateTime vFechaApertura, Boolean vEstado) {
        nombre = vNombre;
        ubicacion = vUbicacion;
        capacidad = vCapacidad;
        tempMin = vTempMin;
        tempMax = vTempMax;
        viandas = vViandas;
        tempActual = vTempActual;
        fechaApertura = vFechaApertura;
        estado = vEstado;
    }

    public abstract void darDeAlta() ;

    public abstract void darDeBaja();

    public abstract Boolean estaVacia();

    public abstract Boolean estaLlena();

    public abstract Integer viandasActuales();

    protected abstract Boolean verificarCapacidad();
    
    protected abstract void verificarCondiciones();

    public abstract void agregarVianda(Vianda vianda);

    public abstract Vianda retirarVianda();

    protected abstract void verificarTempActual();

    @Override
    public abstract void setTempActual(Float temperatura);

    public abstract void marcarComoInactiva();

    public abstract void reaccionarAnteIncidente();

    public abstract void reportarEstadoSegunCondicionSuscripcion(CondicionSuscripcion condicion, MedioDeContacto medioDeContactoElegido);

    public abstract void reportarIncidente(Incidente incidente);

    @Override
    public abstract void producirAlerta(Alerta.TipoAlerta tipo);
    
    public abstract void producirFallaTecnica(Colaborador colaborador, String descripcion, String foto);
}
