package com.tp_anual.proyecto_heladeras_solidarias.controller.view;

import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.ColaboradorHumano;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;
import com.tp_anual.proyecto_heladeras_solidarias.model.incidente.Alerta;
import com.tp_anual.proyecto_heladeras_solidarias.model.incidente.FallaTecnica;
import com.tp_anual.proyecto_heladeras_solidarias.model.tecnico.Visita;
import com.tp_anual.proyecto_heladeras_solidarias.repository.heladera.HeladeraRepository;
import com.tp_anual.proyecto_heladeras_solidarias.service.i18n.I18nService;
import com.tp_anual.proyecto_heladeras_solidarias.service.incidente.AlertaService;
import com.tp_anual.proyecto_heladeras_solidarias.service.incidente.FallaTecnicaService;
import com.tp_anual.proyecto_heladeras_solidarias.service.incidente.IncidenteService;
import com.tp_anual.proyecto_heladeras_solidarias.service.tecnico.VisitaService;
import org.springframework.ui.Model;
import com.tp_anual.proyecto_heladeras_solidarias.model.incidente.Incidente;
import com.tp_anual.proyecto_heladeras_solidarias.model.tecnico.Tecnico;
import com.tp_anual.proyecto_heladeras_solidarias.service.tecnico.TecnicoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TecnicoViewController {

    private final TecnicoService tecnicoService;
    private final IncidenteService incidenteService;
    private final AlertaService alertaService;
    private final FallaTecnicaService fallaTecnicaService;
    private final VisitaService visitaService;

    private final I18nService i18nService;

    public TecnicoViewController(TecnicoService vTecnicoService, IncidenteService vIncidenteService, AlertaService vAlertaService, FallaTecnicaService vFallaTecnicaService, VisitaService vVisitaService, I18nService vI18nService) {
        tecnicoService = vTecnicoService;
        incidenteService = vIncidenteService;
        alertaService = vAlertaService;
        fallaTecnicaService = vFallaTecnicaService;
        visitaService = vVisitaService;
        i18nService = vI18nService;
    }

    @GetMapping("/tecnico")
    public String mostrarTecnico(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        Tecnico tecnico = tecnicoService.obtenerTecnicoPorUsername(username);

        model.addAttribute("tecnicoActual", tecnico);

        List<Alerta> alertas = new ArrayList<>();
        alertas.addAll(alertaService.obtenerAlertasParaTecnico(tecnico));
        alertas.addAll(alertaService.obtenerAlertasSinTecnicoNoResueltas());
        model.addAttribute("alertas", alertas);

        List<FallaTecnica> fallasTecnicas = new ArrayList<>();
        fallasTecnicas.addAll(fallaTecnicaService.obtenerFallasTecnicasParaTecnico(tecnico));
        fallasTecnicas.addAll(fallaTecnicaService.obtenerFallasTecnicasSinTecnicoNoResueltas());
        model.addAttribute("fallasTecnicas", fallasTecnicas);

        List<Alerta> alertasDelTecnico = alertaService.obtenerAlertasParaTecnico(tecnico);
        model.addAttribute("alertasDelTecnico", alertasDelTecnico);

        List<FallaTecnica> fallasTecnicasDelTecnico = fallaTecnicaService.obtenerFallasTecnicasParaTecnico(tecnico);
        model.addAttribute("fallasTecnicasDelTecnico", fallasTecnicasDelTecnico);

        model.addAttribute("messageNoAlerts", i18nService.getMessage("controller.TecnicoController.mostrarTecnico_no_alertas"));
        model.addAttribute("messageNoFailures", i18nService.getMessage("controller.TecnicoController.mostrarTecnico_no_fallas_tecnicas"));

        return "tecnico";
    }

    @GetMapping("/tecnico/tomar-alerta{id}")
    public String tomarAlerta(@PathVariable Long id) {
        Alerta alerta = alertaService.obtenerAlerta(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        Tecnico tecnico = tecnicoService.obtenerTecnicoPorUsername(username);
        Long tecnicoId = tecnico.getId();

        Visita visitaMasReciente = visitaService.obtenerVisitaNoRevisadaMasRecientePorIncidente(alerta);
        
        if (visitaMasReciente != null && !visitaMasReciente.getRevisada() ) {
            visitaMasReciente.seReviso();
            visitaService.guardarVisita(visitaMasReciente);
        }

        alertaService.asignarTecnico(id, tecnico);
        tecnicoService.agregarAPendientes(tecnicoId, alerta);

        return "redirect:/tecnico";
    }

    @GetMapping("/tecnico/tomar-falla-tecnica{id}")
    public String tomarFallaTecnica(@PathVariable Long id) {
        FallaTecnica fallaTecnica = fallaTecnicaService.obtenerFallaTecnica(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        Tecnico tecnico = tecnicoService.obtenerTecnicoPorUsername(username);
        Long tecnicoId = tecnico.getId();

        Visita visitaMasReciente = visitaService.obtenerVisitaNoRevisadaMasRecientePorIncidente(fallaTecnica);

        if (visitaMasReciente != null && !visitaMasReciente.getRevisada() ) {
            visitaMasReciente.seReviso();
            visitaService.guardarVisita(visitaMasReciente);
        }

        fallaTecnicaService.asignarTecnico(id, tecnico);
        tecnicoService.agregarAPendientes(tecnicoId, fallaTecnica);

        return "redirect:/tecnico";
    }

    @PostMapping("registrar-visita/guardar")
    public String registrarVisita(
        @RequestParam("incidente") Long incidenteId,
        @RequestParam("descripcion") String descripcion,
        @RequestParam("foto") String foto,
        @RequestParam(value = "estado-consulta", defaultValue = "false") Boolean estadoConsulta)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        Tecnico tecnico = tecnicoService.obtenerTecnicoPorUsername(username);
        Long tecnicoId = tecnico.getId();

        Incidente incidente = incidenteService.obtenerIncidente(incidenteId);

        tecnicoService.registrarVisita(tecnicoId, incidente, LocalDateTime.now(), descripcion, foto, estadoConsulta);

        return "redirect:/tecnico";
    }
}
