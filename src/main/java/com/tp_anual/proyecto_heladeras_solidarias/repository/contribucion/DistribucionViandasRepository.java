package com.tp_anual.proyecto_heladeras_solidarias.repository.contribucion;

import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.Colaborador;
import com.tp_anual.proyecto_heladeras_solidarias.model.contribucion.DistribucionViandas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DistribucionViandasRepository extends JpaRepository<DistribucionViandas, Long> {

    List<DistribucionViandas> findByYaSumoPuntosFalse();

    List<DistribucionViandas> findByColaboradorAndCompletadaFalseAndCaducadaFalse(Colaborador colaborador);

    DistribucionViandas findTopByColaboradorOrderByFechaContribucionDesc(Colaborador colaborador);

}
