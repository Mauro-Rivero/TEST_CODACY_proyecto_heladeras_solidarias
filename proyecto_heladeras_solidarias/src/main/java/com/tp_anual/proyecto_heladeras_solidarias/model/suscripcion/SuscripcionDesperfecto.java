package com.tp_anual.proyecto_heladeras_solidarias.model.suscripcion;

import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.ColaboradorHumano;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;
import com.tp_anual.proyecto_heladeras_solidarias.model.contacto.MedioDeContacto;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("Desperfecto")
@Log
@Getter
@Setter
public class SuscripcionDesperfecto extends Suscripcion {

    public SuscripcionDesperfecto(ColaboradorHumano vColaborador, Heladera vHeladera, MedioDeContacto vMedioDeContactoElegido) {
        super(vColaborador, vHeladera, vMedioDeContactoElegido);
    }
}