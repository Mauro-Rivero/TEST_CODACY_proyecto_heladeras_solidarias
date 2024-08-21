package com.tp_anual.proyecto_heladeras_solidarias.domain.contacto;

public class Telegram extends MedioDeContacto{
    private final String numero;    // TODO: Pensar si tiene que contener un Telefono o repetimos los atributos de Telefono
    
    public Telegram(String vNumero) {
        numero = vNumero;
    }

    public String getNumero() {
        return numero;
    }

    @Override
    public void contactar(String asunto, String cuerpo) {} // TODO: Se implementará posteriormente, por ahora sólo lo creamos para los Tests
}
