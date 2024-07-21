package com.tp_anual_dds.reporte;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.tp_anual_dds.domain.colaborador.Colaborador;
import com.tp_anual_dds.domain.colaborador.ColaboradorHumano;
import com.tp_anual_dds.domain.contribucion.DistribucionViandas;
import com.tp_anual_dds.domain.contribucion.DonacionVianda;
import com.tp_anual_dds.sistema.Sistema;

public class ReporteViandasPorColaborador extends Reporte {
    private final LinkedHashMap<ColaboradorHumano, Integer> hashMap = new LinkedHashMap<>(); // Usamos LinkedHashMap para que persista el orden de insercion de los elementos (por fechaInscripcion)

    public LinkedHashMap<ColaboradorHumano, Integer> getHashMap(){
        return hashMap;
    }

    @Override
    public void programarReporte() {
        Runnable reportar = () -> {
            hashMap.clear();
            
            ArrayList<Colaborador> colaboradores = Sistema.getColaboradores();
            ArrayList<ColaboradorHumano> colaboradoresHumanos = colaboradores.stream()
                .filter(colaborador -> colaborador instanceof ColaboradorHumano)
                .map(colaborador -> (ColaboradorHumano) colaborador)
                .collect(Collectors.toCollection(ArrayList::new));

            for (ColaboradorHumano colaborador : colaboradoresHumanos) {
                Integer cantidadViandas = colaborador.getContribuciones().stream()
                    .filter(contribucion -> contribucion instanceof DonacionVianda)
                    .collect(Collectors.toList()).size();

                cantidadViandas += colaborador.getContribuciones().stream()
                    .filter(contribucion -> contribucion instanceof DistribucionViandas)
                    .map(distribucionViandas -> (DistribucionViandas) distribucionViandas)
                    .map(distribucion -> distribucion.getCantidadViandasAMover())
                    .mapToInt(Integer::intValue)
                    .sum();

                    hashMap.put(colaborador, cantidadViandas);
            }

            System.out.println("REPORTE - VIANDAS POR COLABORADOR\n");
            for(ColaboradorHumano colaborador : hashMap.keySet()) {
                System.out.println(
                    colaborador.getPersona().getNombre() + " " +
                    colaborador.getPersona().getApellido() + ": " +  hashMap.get(colaborador));
            }
        };
        
        // Programa la tarea para que se ejecute una vez por semana
        scheduler.scheduleAtFixedRate(reportar, 0, 7, TimeUnit.DAYS);
    }
}