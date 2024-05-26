package domain;

import java.util.ArrayList;
import java.time.LocalDateTime;

public class Heladera implements HeladeraObserver {
    private String nombre;
    private String direccion;
    private Double longitud;
    private Double latitud;
    private ArrayList<Vianda> viandas;
    private Integer capacidad;
    private LocalDateTime fechaApertura;
    private Float tempMin;
    private Float tempMax;
    private Float tempActual;
    private Boolean activa;


    public Heladera(String vNombre, String vDireccion, Double vLongitud, Double vLatitud, ArrayList<Vianda> vViandas, Integer vCapacidad, LocalDateTime vFechaApertura, Float vTempMin, Float vTempMax) {
        nombre = vNombre;
        direccion = vDireccion;
        longitud = vLongitud;
        latitud = vLatitud;
        viandas = vViandas;
        capacidad = vCapacidad;
        fechaApertura = vFechaApertura;
        tempMin = vTempMin;
        tempMax = vTempMax;
        tempActual = 0f;
        activa = true;
    }

    public ArrayList<Vianda> getViandas() {
        return viandas;
    }

    public Boolean estaActiva() {
        return activa;
    }

    public void setTempActual(Float temperatura) {
        tempActual = temperatura;
        verificarTempActual();
    }

    public void verificarTempActual() {
        if (tempActual < tempMin || tempActual > tempMax) {
            alertarTemperatura();
        }
    }

    public void alertarTemperatura() {
        System.out.println("La temperatura no esta dentro de los parametros correspondientes.");    // Esto es temporal, simula la notificacion a quienes corresponda, que seguramente sea responsabilidad de un Alertador (a implementar)
    }

    public void alertarMovimiento(){
        System.out.println("LA HELADERA SE ESTA MOVIENDO.");    // Idem alertarTemperatura()
    }

    // darDeAlta()
    // darDeBaja()
    // modificar()

    // actualizarEstado()

    public Vianda retirarVianda() {
        return viandas.remove(0);
    }

    public void agregarVianda(Vianda vianda) {
        viandas.add(vianda);
    }
    
    // alertaMovimiento()
    // alertaTemperatura()
}