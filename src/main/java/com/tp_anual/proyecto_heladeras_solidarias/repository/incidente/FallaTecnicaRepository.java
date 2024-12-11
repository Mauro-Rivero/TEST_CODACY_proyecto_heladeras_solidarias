package com.tp_anual.proyecto_heladeras_solidarias.repository.incidente;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tp_anual.proyecto_heladeras_solidarias.model.incidente.FallaTecnica;

import java.util.List;

public interface FallaTecnicaRepository extends JpaRepository<FallaTecnica, Long> {

}