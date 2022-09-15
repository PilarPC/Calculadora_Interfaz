package com.example.paquete;

import java.io.Serializable;

public class Paquete implements Serializable {


    public Paquete(String mensaje, int puertoEmisor, char IDdireccion) {
        this.mensaje = mensaje;
        this.puertoEmisor = puertoEmisor;
        this.IDdireccion = '0';

    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getPuertoEmisor() {
        return puertoEmisor;
    }

    public void setPuertoEmisor(int puertoEmisor) {
        this.puertoEmisor = puertoEmisor;
    }

    public char getIDdireccion() {
        return IDdireccion;
    }

    public void setIDdireccion(char IDdireccion) {
        this.IDdireccion = IDdireccion;
    }

    public String mensaje;
    public int puertoEmisor;
    char IDdireccion;
}
