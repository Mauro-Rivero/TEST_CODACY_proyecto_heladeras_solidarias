package com.tp_anual.proyecto_heladeras_solidarias.repository.incidente;

import com.tp_anual.proyecto_heladeras_solidarias.model.incidente.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tp_anual.proyecto_heladeras_solidarias.model.incidente.FallaTecnica;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FallaTecnicaRepository extends JpaRepository<FallaTecnica, Long> {

    // Estas son las que un Técnico puede elegir tomar
    @Query(nativeQuery = true, name = "FallaTecnica.findFallasTecnicasParaTecnico")
    List<FallaTecnica> findFallasTecnicasParaTecnico(@Param("tecnico") Long tecnicoId);

    // Estas son las que debería ver el admin
    @Query(nativeQuery = true, name = "FallaTecnica.findFallasTecnicasNoResueltas")
    List<FallaTecnica> findFallasTecnicasNoResueltas();

    // Estas son las que el Técnico debería poder visualizar por si quiere elegir ocuparse
    @Query(nativeQuery = true, name = "FallaTecnica.findFallasTecnicasSinTecnicoNoResueltas")
    List<FallaTecnica> findFallasTecnicasSinTecnicoNoResueltas();
}