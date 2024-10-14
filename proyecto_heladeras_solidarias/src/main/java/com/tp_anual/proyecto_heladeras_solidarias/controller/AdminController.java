package com.tp_anual.proyecto_heladeras_solidarias.controller;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.tp_anual.proyecto_heladeras_solidarias.service.incidente.AlertaService;
import com.tp_anual.proyecto_heladeras_solidarias.service.migrador.Migrador;
import com.tp_anual.proyecto_heladeras_solidarias.service.reporte.ReporteService;
import com.tp_anual.proyecto_heladeras_solidarias.model.incidente.Alerta;

import org.springframework.ui.Model;

@Controller
public class AdminController {

    private Migrador migrador;
    private AlertaService alertaService;
    private ReporteService reporteService;

    public AdminController(Migrador vMigrador, AlertaService vAlertaService, ReporteService vReporteService){
        migrador = vMigrador;
        alertaService = vAlertaService;
        reporteService = vReporteService;
    }

    @GetMapping("/admin")
    public String mostrarAdmin(Model model) {
        // Obtener ofertas desde el servicio
        ArrayList<Alerta> alertas = alertaService.obtenerAlertas(); // Asegúrate de que este método exista
        model.addAttribute("alertas", alertas); // Añade las ofertas al modelo
        return "admin"; // Retorna a la vista 'admin.html'
    }

    @GetMapping("/admin/reportes")
    public String mostrarReportes(Model model) {
        // Obtener reportes desde el servicio
        //ArrayList<Reporte> reportes = reporteService.obtenerReportes(); // Asegúrate de que este método exista
        //model.addAttribute("reportes", reportes); // Añade los reportes al modelo
        return "admin"; // Retorna a la vista 'admin.html'
    }

    @PostMapping("/migrar-archivo")
    public String migrarArchivo(@RequestParam("file") MultipartFile file, Model model) {
        if (!file.isEmpty() && file.getOriginalFilename().endsWith(".csv")) {
            try {
                String fileContent = new String(file.getBytes(), StandardCharsets.UTF_8);
                migrador.migrar(fileContent, true);
                model.addAttribute("message", "Archivo migrado exitosamente.");
            } catch (Exception e) {
                model.addAttribute("message", "Error al migrar archivo: " + e.getMessage());
            }
        } else {
            model.addAttribute("message", "Por favor seleccione un archivo CSV.");
        }
        return "redirect: admin";  // Vista que muestra el resultado de la migración
    }
}
