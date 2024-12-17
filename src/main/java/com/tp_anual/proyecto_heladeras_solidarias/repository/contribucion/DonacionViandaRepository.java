package com.tp_anual.proyecto_heladeras_solidarias.repository.contribucion;

import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.Colaborador;
import com.tp_anual.proyecto_heladeras_solidarias.model.contribucion.DonacionDinero;
import com.tp_anual.proyecto_heladeras_solidarias.model.contribucion.DonacionVianda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DonacionViandaRepository extends JpaRepository<DonacionVianda, Long> {
    
    List<DonacionVianda> findByYaSumoPuntosFalse();

    List<DonacionVianda> findByColaboradorAndCompletadaFalseAndCaducadaFalse(Colaborador colaborador);

    DonacionVianda findTopByColaboradorOrderByFechaContribucionDesc(Colaborador colaborador);
}