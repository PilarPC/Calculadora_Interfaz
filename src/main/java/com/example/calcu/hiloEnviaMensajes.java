package com.example.calcu;

import com.example.paquete.Paquete;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class hiloEnviaMensajes extends Thread {
    public List<Paquete> listaEventosGlobal = new ArrayList<>();
    public int PUERTO_MIDDLEWARE;
    List<Paquete> listaEventosLocal = new ArrayList<>();

    @Override // para usar polimorfismo
    public void run(){
        try {
            Thread.sleep(4000);//10000
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        while (true){
            for (Paquete evento:listaEventosLocal) {
                try {
                    //System.out.println("Envio evento "+evento.getEvento()+" mensaje "+evento.mensaje);
                    evento.setTiempoAcuse(evento.getTiempoAcuse()+"S");
                    Socket misoket = new Socket("127.0.0.1",PUERTO_MIDDLEWARE);
                    ObjectOutputStream flujoSalida = new ObjectOutputStream(misoket.getOutputStream());

                    flujoSalida.writeObject(evento); //para que el nodo reciba el nuevo paquete
                    flujoSalida.close();

                }catch (IOException e){
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(5000);//10000
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            listaEventosLocal = listaEventosGlobal;
        }

    }
}
