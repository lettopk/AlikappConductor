package com.Alikapp.alikappconductor;

public class MensajeRecibir extends Mensaje {
    private long hora;

    public MensajeRecibir() {
    }

    public MensajeRecibir(long hora) {
        this.hora = hora;
    }

    public MensajeRecibir(String mensaje, String urlFoto, String nombre, String fotoperfil, String type_mensaje, long hora) {
        super(mensaje, urlFoto, nombre, fotoperfil, type_mensaje);
        this.hora = hora;
    }

    public long getHora() {
        return hora;
    }

    public void setHora(long hora) {
        this.hora = hora;
    }
}
