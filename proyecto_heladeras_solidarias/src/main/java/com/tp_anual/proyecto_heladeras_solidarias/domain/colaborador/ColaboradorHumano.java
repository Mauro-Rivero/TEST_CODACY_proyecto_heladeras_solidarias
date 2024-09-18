package com.tp_anual.proyecto_heladeras_solidarias.domain.colaborador;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;

import com.tp_anual.proyecto_heladeras_solidarias.domain.contacto.MedioDeContacto;
import com.tp_anual.proyecto_heladeras_solidarias.domain.contribucion.Contribucion;
import com.tp_anual.proyecto_heladeras_solidarias.domain.contribucion.DistribucionViandasCreator;
import com.tp_anual.proyecto_heladeras_solidarias.domain.contribucion.DonacionDineroCreator;
import com.tp_anual.proyecto_heladeras_solidarias.domain.contribucion.DonacionViandaCreator;
import com.tp_anual.proyecto_heladeras_solidarias.domain.contribucion.RegistroDePersonaEnSituacionVulnerableCreator;
import com.tp_anual.proyecto_heladeras_solidarias.domain.heladera.HeladeraActiva;
import com.tp_anual.proyecto_heladeras_solidarias.domain.oferta.Oferta;
import com.tp_anual.proyecto_heladeras_solidarias.domain.persona.PersonaFisica;
import com.tp_anual.proyecto_heladeras_solidarias.domain.suscripcion.Suscripcion;
import com.tp_anual.proyecto_heladeras_solidarias.domain.suscripcion.Suscripcion.CondicionSuscripcion;
import com.tp_anual.proyecto_heladeras_solidarias.domain.tarjeta.TarjetaColaborador;
import com.tp_anual.proyecto_heladeras_solidarias.domain.tarjeta.TarjetaColaboradorNula;
import com.tp_anual.proyecto_heladeras_solidarias.domain.ubicacion.Ubicacion;
import com.tp_anual.proyecto_heladeras_solidarias.i18n.I18n;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Entity
@DiscriminatorValue("Humano")
@Log
@Getter
@Setter
public class ColaboradorHumano extends Colaborador {    // Implementa una Interfaz "ColaboradorHumanoObserver" a nivel conceptual
    
    @OneToOne(mappedBy = "titular", fetch = FetchType.EAGER)
    protected TarjetaColaborador tarjeta;
    
    @OneToMany(mappedBy = "colaborador", fetch = FetchType.EAGER)
    protected final ArrayList<Suscripcion> suscripciones;   // Será una Suscripción por Heladera

    public ColaboradorHumano(PersonaFisica vPersona, Ubicacion vDomicilio, ArrayList<MedioDeContacto> vMediosDeContacto, ArrayList<Contribucion> vContribuciones, ArrayList<Oferta> vBeneficiosAdquiridos, Double vPuntos) {
        super(vPersona, vDomicilio, vMediosDeContacto, vContribuciones, vBeneficiosAdquiridos, vPuntos);
        creatorsPermitidos = new HashSet<>();
        creatorsPermitidos.add(DistribucionViandasCreator.class);
        creatorsPermitidos.add(DonacionDineroCreator.class);
        creatorsPermitidos.add(DonacionViandaCreator.class);
        creatorsPermitidos.add(RegistroDePersonaEnSituacionVulnerableCreator.class);

        tarjeta = new TarjetaColaboradorNula();
        suscripciones = new ArrayList<>();
    }

    @Override
    public PersonaFisica getPersona() {
        return (PersonaFisica) persona;
    }

    private void agregarSuscripcion(Suscripcion suscripcion) {
        suscripciones.add(suscripcion);
    }

    private void eliminarSuscripcion(Suscripcion suscripcion) {
        suscripciones.remove(suscripcion);
    }
    
    public Suscripcion suscribirse(HeladeraActiva heladeraObjetivo, Integer viandasDisponiblesMin, Integer viandasParaLlenarMax, Boolean notificarDesperfecto, MedioDeContacto medioDeContacto) {
        Suscripcion suscripcion = new Suscripcion(this, heladeraObjetivo, viandasDisponiblesMin, viandasParaLlenarMax, notificarDesperfecto, medioDeContacto);
        suscripcion.darDeAlta();
        agregarSuscripcion(suscripcion);
        log.log(Level.INFO, I18n.getMessage("colaborador.ColaboradorHumano.agregarSuscripcion_info", persona.getNombre(2), suscripcion.getHeladera().getNombre()));

        return suscripcion;
    }

    public void modificarSuscripcion(Suscripcion suscripcion, CondicionSuscripcion flag, Integer nuevoValor) {
        // Tenemos un flag que nos indica qué atributo debe ser cambiado
        switch(flag) {
        
        case VIANDAS_MIN -> suscripcion.setViandasDisponiblesMin(nuevoValor);
        
        case VIANDAS_MAX -> suscripcion.setViandasParaLlenarMax(nuevoValor);
        
        case DESPERFECTO -> suscripcion.setNotificarDesperfecto(nuevoValor != 0);
        
        default -> {}
        
        }

        log.log(Level.INFO, I18n.getMessage("colaborador.ColaboradorHumano.modificarSuscripcion_info", suscripcion.getHeladera().getNombre(), persona.getNombre(2)));
    }

    public void cancelarSuscripcion(Suscripcion suscripcion) {
        suscripcion.darDeBaja();
        eliminarSuscripcion(suscripcion);
        log.log(Level.INFO, I18n.getMessage("colaborador.ColaboradorHumano.cancelarSuscripcion_info", suscripcion.getHeladera().getNombre(), persona.getNombre(2)));
    }
}