package com.Alikapp.alikappconductor;

public class Temporizador {
    private int horas;
    private int minutos;
    private int segundos;
    private int horasRestantes;
    private int minutosRestantes;
    private int segundosRestantes;
    private int segundosTotal;

    private Boolean isDetenido = false;

    /**
     * Termporizador de tiempo reiniciable, se deben introducir los
     * parámetros de tiempo en horas, minutos y segundos según la cantidad
     * de tiempo requerida a contabilizar**/
    public Temporizador(int Horas, int Minutos, int Segundos){
        horas = Horas;
        minutos = Minutos;
        segundos = Segundos;
        calculatTiempo();
    }

    private void calculatTiempo() {
        segundosTotal = (horas*3600) + (minutos*60) + segundos;
    }

    public int getSegundosTotal() {
        return segundosTotal;
    }

    public void continuarConteo() {
        isDetenido = false;
    }

    public void reIniciarConteo() {
        isDetenido = false;
        calculatTiempo();
    }

    /**
     * Calcula la cantidad de tiempo restante hasta el momento en el que se detuvo el conteo**/
    private void calculatTiempoRestante() {
        horasRestantes = segundosTotal % 3600;
        minutosRestantes = (segundosTotal - (horasRestantes * 3600)) % 60;
        segundosRestantes = (segundosTotal - (horasRestantes*3600) - (minutosRestantes * 60));
    }

    /**
     * inicia el conteo regresivo de la cantidad de tiempo ingresada**/
    public void conteoRegresivo() {
        segundosTotal--;
        calculatTiempoRestante();
    }
}
