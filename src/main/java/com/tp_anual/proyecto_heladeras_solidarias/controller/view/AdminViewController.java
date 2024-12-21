package com.tp_anual.proyecto_heladeras_solidarias.controller.view;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.tp_anual.proyecto_heladeras_solidarias.exception.contribucion.*;
import com.tp_anual.proyecto_heladeras_solidarias.exception.migrador.FilaDeDatosIncompletaException;
import com.tp_anual.proyecto_heladeras_solidarias.model.area.Area;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.ColaboradorHumano;
import com.tp_anual.proyecto_heladeras_solidarias.model.incidente.FallaTecnica;
import com.tp_anual.proyecto_heladeras_solidarias.model.persona.PersonaFisica;
import com.tp_anual.proyecto_heladeras_solidarias.model.tecnico.Tecnico;
import com.tp_anual.proyecto_heladeras_solidarias.model.tecnico.Visita;
import com.tp_anual.proyecto_heladeras_solidarias.service.i18n.I18nService;
import com.tp_anual.proyecto_heladeras_solidarias.service.incidente.FallaTecnicaService;
import com.tp_anual.proyecto_heladeras_solidarias.service.tecnico.TecnicoService;
import com.tp_anual.proyecto_heladeras_solidarias.service.tecnico.VisitaService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.tp_anual.proyecto_heladeras_solidarias.service.incidente.AlertaService;
import com.tp_anual.proyecto_heladeras_solidarias.service.migrador.Migrador;
import com.tp_anual.proyecto_heladeras_solidarias.service.reporte.ReporteService;
import com.tp_anual.proyecto_heladeras_solidarias.model.incidente.Alerta;
import com.tp_anual.proyecto_heladeras_solidarias.model.reporte.FallasPorHeladera;
import com.tp_anual.proyecto_heladeras_solidarias.model.reporte.MovimientosViandaPorHeladera;
import com.tp_anual.proyecto_heladeras_solidarias.model.reporte.ViandasPorColaborador;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminViewController {

    private final Migrador migrador;
    private final AlertaService alertaService;
    private final FallaTecnicaService fallaTecnicaService;
    private final ReporteService reporteService;
    private final TecnicoService tecnicoService;
    private final VisitaService visitaService;

    private final I18nService i18nService;

    public AdminViewController(Migrador vMigrador, AlertaService vAlertaService, FallaTecnicaService vFallaTecnicaService, ReporteService vReporteService, TecnicoService vTecnicoService, VisitaService vVisitaService, I18nService vi18nService) {
        migrador = vMigrador;
        alertaService = vAlertaService;
        fallaTecnicaService = vFallaTecnicaService;
        reporteService = vReporteService;
        tecnicoService = vTecnicoService;
        visitaService = vVisitaService;

        i18nService = vi18nService;
    }

    @GetMapping("/admin")
    public String mostrarAdmin(Model model) {
        List<Alerta> alertas = alertaService.obtenerAlertasNoResueltas();
        model.addAttribute("alertas", alertas);

        List<FallaTecnica> fallasTecnicas = fallaTecnicaService.obtenerFallasTecnicasNoResueltas();
        model.addAttribute("fallasTecnicas", fallasTecnicas);

        List<FallasPorHeladera> fallasPorHeladera = reporteService.obtenerReporteFallasPorHeladera();
        List<MovimientosViandaPorHeladera> movimientosViandaPorHeladera = reporteService.obtenerReporteMovimientosViandaPorHeladera();
        List<ViandasPorColaborador> viandasPorColaborador = reporteService.obtenerReporteViandasPorColaborador();

        model.addAttribute("reporteFallasPorHeladera", fallasPorHeladera);
        model.addAttribute("reporteMovimientosViandaPorHeladera", movimientosViandaPorHeladera);
        model.addAttribute("reporteViandasPorColaborador", viandasPorColaborador);

        List<Tecnico> tecnicos = tecnicoService.obtenerTecnicos();
        model.addAttribute("tecnicos", tecnicos);

        model.addAttribute("messageNoAlerts", i18nService.getMessage("controller.AdminController.mostrarAdmin_no_alertas"));
        model.addAttribute("messageNoFailures", i18nService.getMessage("controller.AdminController.mostrarAdmin_no_fallas_tecnicas"));

        return "admin";
    }

    @PostMapping("/cargar-archivo")
    public String cargarArchivo(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws DatosInvalidosCrearCargaOfertaException, DatosInvalidosCrearDistribucionViandasException, DatosInvalidosCrearRPESVException, DatosInvalidosCrearHCHException, FilaDeDatosIncompletaException, DatosInvalidosCrearDonacionViandaException, IOException, URISyntaxException, DatosInvalidosCrearDonacionDineroException {
        try {
            migrador.migrar(file, false);

        } catch (FilaDeDatosIncompletaException e) {
            redirectAttributes.addFlashAttribute("messageAlert", e.getMessage());

            return "redirect:/admin";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageAlert", i18nService.getMessage("controller.AdminController.cargarArchivo_err"));

            return "redirect:/admin";
        }

        redirectAttributes.addFlashAttribute("messageSuccess", i18nService.getMessage("controller.AdminController.cargarArchivo"));

        return "redirect:/admin";
    }

    @PostMapping("/modificar-tecnico/guardar")
    public String modificarTecncio(
        @RequestParam("tecnico-id") Long tecnicoId,
        @RequestParam("nombre") String nombre,
        @RequestParam("apellido") String apellido,
        @RequestParam("x1") Double x1,
        @RequestParam("x2") Double x2,
        @RequestParam("y1") Double y1,
        @RequestParam("y2") Double y2)
    {
        Tecnico tecnico = tecnicoService.obtenerTecnico(tecnicoId);
        Area area = new Area(x1, y1, x2, y2);

        PersonaFisica persona = tecnico.getPersona();
        persona.setNombre(nombre);
        persona.setApellido(apellido);

        tecnico.setAreaDeCobertura(area);

        tecnicoService.guardarTecnico(tecnico);

        return "redirect:/admin";
    }

    @GetMapping("/tecnico/borrar/{id}")
    public String borrarTecnico(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Tecnico tecnico = tecnicoService.obtenerTecnicoPorUsername(username);

        List<Visita> visitas = visitaService.obtenerVisitasPorTecnico(tecnico);
        visitaService.eliminarVisitas(visitas);

        tecnicoService.eliminarTecnico(id);

        return "redirect:/admin";
    }

}
