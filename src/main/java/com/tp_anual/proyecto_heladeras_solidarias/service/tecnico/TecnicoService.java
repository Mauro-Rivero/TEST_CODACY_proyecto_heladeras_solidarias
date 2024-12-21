package com.tp_anual.proyecto_heladeras_solidarias.service.tecnico;

import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.Colaborador;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;
import com.tp_anual.proyecto_heladeras_solidarias.service.heladera.ActivadorDeHeladerasService;
import com.tp_anual.proyecto_heladeras_solidarias.service.heladera.HeladeraService;
import com.tp_anual.proyecto_heladeras_solidarias.service.i18n.I18nService;
import com.tp_anual.proyecto_heladeras_solidarias.model.area.Area;
import com.tp_anual.proyecto_heladeras_solidarias.model.incidente.Incidente;
import com.tp_anual.proyecto_heladeras_solidarias.model.tecnico.Tecnico;
import com.tp_anual.proyecto_heladeras_solidarias.model.tecnico.Visita;
import com.tp_anual.proyecto_heladeras_solidarias.model.usuario.Usuario;
import com.tp_anual.proyecto_heladeras_solidarias.repository.tecnico.TecnicoRepository;
import com.tp_anual.proyecto_heladeras_solidarias.service.area.AreaService;
import com.tp_anual.proyecto_heladeras_solidarias.service.usuario.CustomUserDetailsService;
import lombok.extern.java.Log;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Service
@Log
public class TecnicoService {

    private final TecnicoRepository tecnicoRepository;
    private final VisitaService visitaService;
    private final AreaService areaService;
    private final ActivadorDeHeladerasService activadorDeHeladerasService;

    private final I18nService i18nService;

    public TecnicoService(TecnicoRepository vTecnicoRepository, VisitaService vVisitaService, AreaService vAreaService, ActivadorDeHeladerasService vActivadorDeHeladerasService, I18nService vI18nService) {
        tecnicoRepository = vTecnicoRepository;
        visitaService = vVisitaService;
        areaService = vAreaService;
        activadorDeHeladerasService = vActivadorDeHeladerasService;

        i18nService = vI18nService;
    }

    public Tecnico obtenerTecnico(Long tecnicoId) {
        return tecnicoRepository.findById(tecnicoId).orElseThrow(() -> new EntityNotFoundException(i18nService.getMessage("obtenerEntidad_exception")));
    }

    public List<Tecnico> obtenerTecnicos() {
        return new ArrayList<>(tecnicoRepository.findAll());
    }

    public Tecnico obtenerTecnicoPorUsername(String username) {
        return tecnicoRepository.findByUsuario_Username(username);
    }

    public Tecnico guardarTecnico(Tecnico tecnico) {
         return tecnicoRepository.saveAndFlush(tecnico);
    }

    public void eliminarTecnico(Long tecnicoId) { tecnicoRepository.deleteById(tecnicoId); }

    public Tecnico asignarUsuario(Long tecnicoId, Usuario usuario) {
        Tecnico tecnico = obtenerTecnico(tecnicoId);
        tecnico.setUsuario(usuario);

        return guardarTecnico(tecnico);
    }

    public void agregarAPendientes(Long tecnicoId, Incidente incidente) {
        Tecnico tecnico = obtenerTecnico(tecnicoId);
        tecnico.agregarAPendientes(incidente);
        guardarTecnico(tecnico);
    }

    public void registrarVisita(Long tecnicoId, Incidente incidente, LocalDateTime fecha, String descripcion, String foto, Boolean estadoConsulta) {
        Tecnico tecnico = obtenerTecnico(tecnicoId);

        incidente.setTecnico(null);
        tecnico.getPendientes().remove(incidente);
        guardarTecnico(tecnico);

        Visita visita = new Visita(tecnico, incidente, fecha, descripcion, foto, estadoConsulta, estadoConsulta);
        visitaService.guardarVisita(visita);

        if (visita.getEstado()) {
            Heladera heladera = incidente.getHeladera();
            activadorDeHeladerasService.marcarComoActiva(heladera.getId());
        }

        log.log(Level.INFO, i18nService.getMessage("tecnico.Tecnico.registrarVisita_info", incidente.getHeladera().getNombre(), incidente.getClass().getSimpleName(), tecnico.getPersona().getNombre(2)));
    }

    // Como convención, para aproximar la Ubicación de un Técnico, vamos a usar el punto medio de su área de cobertura
    public Pair<Double,Double> ubicacionAprox(Long tecnicoId) {
        Tecnico tecnico = obtenerTecnico(tecnicoId);
        Area area = tecnico.getAreaDeCobertura();

        return areaService.puntoMedio(area.getId());
    }
}
