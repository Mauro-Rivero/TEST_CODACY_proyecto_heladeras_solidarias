package com.tp_anual_dds.domain.colaborador;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import com.tp_anual_dds.domain.contribuciones.Contribucion;
import com.tp_anual_dds.domain.contribuciones.DistribucionViandas;
import com.tp_anual_dds.domain.contribuciones.DistribucionViandasCreator;
import com.tp_anual_dds.domain.contribuciones.DonacionVianda;
import com.tp_anual_dds.domain.contribuciones.DonacionViandaCreator;
import com.tp_anual_dds.domain.documento.Documento;
import com.tp_anual_dds.domain.documento.Documento.Sexo;
import com.tp_anual_dds.domain.documento.Documento.TipoDocumento;
import com.tp_anual_dds.domain.heladera.HeladeraActiva;
import com.tp_anual_dds.domain.heladera.HeladeraNula;
import com.tp_anual_dds.domain.heladera.Vianda;
import com.tp_anual_dds.domain.tarjeta.TarjetaColaboradorActiva;
import com.tp_anual_dds.domain.tarjeta.TarjetaColaboradorCreator;
import com.tp_anual_dds.domain.ubicacion.Ubicacion;

public class Colaborador2Test {
    @Test
    @DisplayName("Testeo la correcta creación de Contribuciones y que se agreguen a las contribuciones del Colaborador")
    public void ColaborarTest() {
        ColaboradorHumano colaborador = new ColaboradorHumano(new Ubicacion(-34.6083, -58.3709, "Balcarce 78", "Ciudad Autónoma de Buenos Aires", "Argentina"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0d, "NombrePrueba", "ApellidoPrueba", new Documento(TipoDocumento.DNI, "40123456", Sexo.MASCULINO), LocalDateTime.parse("2003-01-01T00:00:00")); // Uso ColaboradorHumano porque Colaborador es abstract y el metodo es igual para ambos (Humano y Juridico)
        
        LocalDateTime fechaAperturaH1   = LocalDateTime.parse("2024-01-01T00:00:00");
        LocalDateTime fechaAperturaH2   = LocalDateTime.parse("2024-02-01T00:00:00");
        LocalDateTime fechaCaducidadV   = LocalDateTime.parse("2025-01-01T00:00:00");
        
        HeladeraActiva heladera1 = new HeladeraActiva("HeladeraPrueba1", new Ubicacion(-34.601978, -58.383865, "Tucumán 1171", "Ciudad Autónoma de Buenos Aires", "Argentina"), new ArrayList<>(), 2, fechaAperturaH1, -20f, 5f);
        HeladeraActiva heladera2 = new HeladeraActiva("HeladeraPrueba2", new Ubicacion(-34.6092, -58.3842, "Avenida de Mayo 1370", "Ciudad Autónoma de Buenos Aires", "Argentina"), new ArrayList<>(), 5, fechaAperturaH2, -20f, 5f);
        Vianda vianda = new Vianda("ComidaPrueba" , colaborador, fechaCaducidadV, LocalDateTime.now(), 0, 0, false);
        
        DonacionViandaCreator donacionViandaCreator = new DonacionViandaCreator();
        DonacionVianda donacionVianda = (DonacionVianda) colaborador.colaborar(donacionViandaCreator, LocalDateTime.now(), vianda, heladera1);
        TarjetaColaboradorCreator tarjetaColaboradorCreator = new TarjetaColaboradorCreator();
        TarjetaColaboradorActiva tarjetaColaboradorActiva = (TarjetaColaboradorActiva) tarjetaColaboradorCreator.crearTarjeta(colaborador);
        colaborador.setTarjeta(tarjetaColaboradorActiva);
        colaborador.getTarjeta().solicitarApertura(TarjetaColaboradorActiva.MotivoSolicitud.INGRESAR_DONACION, heladera1);
        colaborador.getTarjeta().intentarApertura(heladera1);
        heladera1.agregarVianda(vianda);
        vianda.setHeladera(heladera1);
        colaborador.confirmarContribucion(donacionVianda);

        DistribucionViandasCreator distribucionViandasCreator = new DistribucionViandasCreator();
        DistribucionViandas distribucionViandas = (DistribucionViandas) colaborador.colaborar(distribucionViandasCreator, LocalDateTime.now(), heladera1, heladera2, 1, DistribucionViandas.MotivoDistribucion.FALTA_DE_VIANDAS_EN_DESTINO);
        colaborador.getTarjeta().solicitarApertura(TarjetaColaboradorActiva.MotivoSolicitud.RETIRAR_LOTE_DE_DISTRIBUCION, heladera1);
        colaborador.getTarjeta().intentarApertura(heladera1);
        heladera1.retirarVianda();
        vianda.setHeladera(new HeladeraNula());
        colaborador.getTarjeta().solicitarApertura(TarjetaColaboradorActiva.MotivoSolicitud.INGRESAR_LOTE_DE_DISTRIBUCION, heladera2);
        colaborador.getTarjeta().intentarApertura(heladera2);
        heladera2.agregarVianda(vianda);
        vianda.setHeladera(heladera2);
        colaborador.confirmarContribucion(distribucionViandas);

        ArrayList<Contribucion> contribuciones = new ArrayList<>();
        contribuciones.add(donacionVianda);
        contribuciones.add(distribucionViandas);

        Assertions.assertThat(colaborador.getContribuciones())
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrderElementsOf(contribuciones);
    }
}