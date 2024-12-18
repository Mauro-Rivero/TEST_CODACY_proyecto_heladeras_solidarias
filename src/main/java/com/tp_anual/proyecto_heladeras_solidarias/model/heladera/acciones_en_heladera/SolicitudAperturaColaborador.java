package com.tp_anual.proyecto_heladeras_solidarias.model.heladera.acciones_en_heladera;

import java.time.LocalDateTime;

import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.ColaboradorHumano;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@DiscriminatorValue("Solicitud Apertura Colaborador")
@Getter
public class SolicitudAperturaColaborador extends AccionColaborador {
    
    @Enumerated(EnumType.STRING)
    private MotivoSolicitud motivo; // final

    public enum MotivoSolicitud {
        INGRESAR_DONACION,
        INGRESAR_LOTE_DE_DISTRIBUCION,
        RETIRAR_LOTE_DE_DISTRIBUCION
    }

    public SolicitudAperturaColaborador() {
        super();
    }

    public SolicitudAperturaColaborador(LocalDateTime vFecha, Heladera vHeladera, ColaboradorHumano vResponsable, MotivoSolicitud vMotivo) {
        super(vFecha, vHeladera, vResponsable);
        motivo = vMotivo;
    }
}
