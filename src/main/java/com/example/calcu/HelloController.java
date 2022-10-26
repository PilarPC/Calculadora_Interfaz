package com.example.calcu;

import com.example.paquete.Paquete;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


public class HelloController implements Runnable{

    hiloEnviaMensajes hiloEnvia = new hiloEnviaMensajes();

    public int PUERTO_ACTUAL = 0;
    public int PUERTO_MIDDLEWARE = 0;
    public  String huella;
    public Paquete paquete = new Paquete("",  0, '0','0', " ", " ");
    List<String>historial=new ArrayList<>();
    List<Paquete> listaSuma = new ArrayList<>();
    List<Paquete> listaResta = new ArrayList<>();
    List<Paquete> listaMustiplicacion = new ArrayList<>();
    List<Paquete> listaDivision = new ArrayList<>();
    int ACUSE_MIN_SUMA = 2;
    int ACUSE_MIN_RESTA = 1;
    int ACUSE_MIN_MULT = 3;
    int ACUSE_MIN_DIV = 4;




    @FXML
    private Label diisplabel;

    @FXML
    void ACck() {
        lipiaPantalla();
    }

    @FXML
    void ck0() {digitoPantalla("0");}

    @FXML
    void ck1() {
        digitoPantalla("1");
    }

    @FXML
    void ck2() {
        digitoPantalla("2");
    }

    @FXML
    void ck3() {
        digitoPantalla("3");
    }

    @FXML
    void ck4() {
        digitoPantalla("4");
    }

    @FXML
    void ck5() {
        digitoPantalla("5");
    }

    @FXML
    void ck6() {
        digitoPantalla("6");
    }

    @FXML
    void ck7() {
        digitoPantalla("7");
    }

    @FXML
    void ck8() {
        digitoPantalla("8");
    }

    @FXML
    void ck9() {
        digitoPantalla("9");
    }
    @FXML
    void ckHistorial() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("RegistroResultados.fxml"));
        Parent root =fxmlLoader.load();
        Scene scene=new Scene(root);
        Stage stage1=new Stage();
        stage1.initModality(Modality.APPLICATION_MODAL);
        stage1.setScene(scene);
        RegistroResultadosController registroResultadosController=fxmlLoader.getController();
        //System.out.println(historialController);
        registroResultadosController.cargarResultados(historial);
        stage1.showAndWait();
    }


    @FXML
    void ckTablaEventos() throws IOException {
        imprimirEventos();
    }


    public void imprimirEventos(){
        int numEventoSuma = 0;
        int numEventoResta = 0;
        int numEventoMult = 0;
        int numEventoDiv = 0;

        for(Paquete cadena :listaSuma ) {
            numEventoSuma++;
            System.out.println("Evento suma mandado "+cadena.getEvento() + " Numero minimo de acuses "+ ACUSE_MIN_SUMA + " Numero de eventos: "+numEventoSuma);
        }
        for(Paquete cadena :listaResta ) {
            numEventoResta++;
            System.out.println("Evento resta mandado "+cadena.getEvento() + " Numero minimo de acuses "+ ACUSE_MIN_RESTA + " Numero de eventos: "+numEventoResta);
        }
        for(Paquete cadena :listaMustiplicacion ) {
            numEventoMult++;
            System.out.println("Evento multiplicación mandado "+cadena.getEvento() + " Numero minimo de acuses "+ ACUSE_MIN_MULT + " Numero de eventos: "+numEventoMult);
        }
        for(Paquete cadena :listaDivision ) {
            numEventoDiv++;
            System.out.println("Evento división mandado: "+cadena.getEvento() + " Numero minimo de acuses: "+ ACUSE_MIN_DIV + " Numero de eventos: "+numEventoDiv);
        }
    }

    @FXML
    void ckdiv() {
        digitoPantalla("/");
        paquete.setCodigoOperacion('d');

    }

    @FXML
    void ckigual() {
        try {
            Socket misoket = new Socket("127.0.0.1",PUERTO_MIDDLEWARE);
            ObjectOutputStream flujoSalida = new ObjectOutputStream(misoket.getOutputStream());
            //DataOutputStream flujoSalida = new DataOutputStream(misoket.getOutputStream());
            Paquete nuevoPaquete = new Paquete(paquete.getMensaje(),paquete.getPuertoEmisor(), paquete.getIDdireccion(), paquete.getCodigoOperacion(),paquete.getHuellaCliente(),paquete.getHuellaServidor());
            System.out.println(paquete.getMensaje()+" "+paquete.getPuertoEmisor()+" "+paquete.getIDdireccion()+" llega a puerto MW "+PUERTO_MIDDLEWARE);
            paquete.setHuellaCliente(huella);
            nuevoPaquete.setHuellaCliente(huella);
            String evento = generarHuella(paquete.getMensaje());
            paquete.setEvento(evento);
            nuevoPaquete.setEvento(evento);
            nuevoPaquete.setCodigoOperacion(paquete.getCodigoOperacion());
            agregar_a_lista(nuevoPaquete);//agrego el evento a la lista
            enviarHilo();

            flujoSalida.writeObject(nuevoPaquete); //para que el nodo reciba el nuevo paquete
            flujoSalida.close();

        }catch (IOException e){
            e.printStackTrace();
        }
        lipiaPantalla();
    }


    public void enviarHilo(){
        List<Paquete> litaEventos = new ArrayList<>();
        if (listaSuma.size() > 0){litaEventos.add(listaSuma.get(0));}
        if (listaResta.size() > 0){litaEventos.add(listaResta.get(0));}
        if (listaMustiplicacion.size() > 0){litaEventos.add(listaMustiplicacion.get(0));}
        if (listaDivision.size() > 0){litaEventos.add(listaDivision.get(0));}
         hiloEnvia.listaEventosGlobal = litaEventos;
    }
    public void agregar_a_lista (Paquete paquete){
        if(paquete.getCodigoOperacion() == 'a'){
            listaSuma.add(paquete);
        } else if (paquete.getCodigoOperacion() == 'b') {
            listaResta.add(paquete);
        } else if (paquete.getCodigoOperacion() == 'c'){
            listaMustiplicacion.add(paquete);
        } else if (paquete.getCodigoOperacion() == 'd'){
            listaDivision.add(paquete);
        }
    }

//codigo de operación a = +, b= -, c=*,d=/

    @FXML
    void ckmas() {
        digitoPantalla("+");
        paquete.setCodigoOperacion('a');
    }

    @FXML
    void ckmenos() {
        digitoPantalla("-");
        paquete.setCodigoOperacion('b');
    }

    @FXML
    void ckpor() {
        digitoPantalla("x");
        paquete.setCodigoOperacion('c');
    }


    void lipiaPantalla(){
        Digito = false;
        Punto = false;
        operador = ' ';
        diisplabel.setText("0");
    }

    @FXML
    void ckpunto() {
        if(!Punto && !Digito) {
            diisplabel.setText("0.");
            Digito = true;
        }else if(!Punto){
            String valActaul = diisplabel.getText();
            diisplabel.setText(valActaul + ".");
        }
        Punto = true;
    }
    private boolean Digito = false;
    private boolean Punto = false;
    private char operador = ' ';



    private void digitoPantalla(String numero){

        if(!Digito){
            diisplabel.setText("");
            Punto = false;
        }
        String valActual = diisplabel.getText();
        diisplabel.setText(valActual+numero);
        paquete.setMensaje(valActual+numero);
        paquete.setPuertoEmisor(PUERTO_ACTUAL);
        paquete.setIDdireccion('C');
        Digito = true;
    }

    //-----------------Generar huella de la célula en cada creación --------------------------

    public String generarHuella(String puerto){
        String tiempo = DateTimeFormatter.ofPattern("dd-MM-yyyy | HH:mm:ss").format(LocalDateTime.now());
        String huella = puerto + tiempo;
        huella = sha1Mensaje(huella);
        return huella;
    }

    public String sha1Mensaje(String mensaje){
        String devuelvesha1="";
        String sha=mensaje;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            digest.update(sha.getBytes("utf8"));
            devuelvesha1 = String.format("%040x", new BigInteger(1, digest.digest()));
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println();
        return devuelvesha1;
    }






    //--------------Socket de entrada------------------ASIGNACIÓN DINÁMICA DE PUERTOS---------------------
    public  void Establecer(Paquete p){
        this.paquete = p;
    }

    public void initialize(){
        Thread hilo1 = new Thread(this);

        hilo1.start();
    }
    //hios
    ServerSocket servidor;
    public void run(){
        //boolean asignacion = true;
        int puertoCalculadora = 12000;
        int puertoMiddleware = 11000;
        while (true){
            try{
                enviarHilo();
                servidor=new ServerSocket(puertoCalculadora);
                System.out.println("estoy en el puerto"+ puertoCalculadora);
                PUERTO_MIDDLEWARE= puertoMiddleware;
                PUERTO_ACTUAL = puertoCalculadora;
                hiloEnvia.PUERTO_MIDDLEWARE = PUERTO_MIDDLEWARE;
                hiloEnvia.start();
                //-------------------------------------- GENERO HUELLA -----------------
                String puertoString= PUERTO_ACTUAL+"";
                huella = generarHuella(puertoString);
                System.out.println("Huella generada por mi puerto actual "+PUERTO_ACTUAL +" "+huella);
                //------------------------------------- GENERO HUELLA ------------------------


                //-------------------------------- RECIBO PAQUETE -----------------------------
                while(true){
                    Socket misocket=servidor.accept();//aceptara las conexiones que vengan del exterior
                    ObjectInputStream flujoEntrada=new ObjectInputStream(misocket.getInputStream());
                    Paquete data = (Paquete)flujoEntrada.readObject();

                    System.out.println(data);
                    historial.add(data.mensaje);
                    System.out.println("ACUSE: "+data.getAcuse());



                    if (data.getCodigoOperacion() == 'm' & data.getHuellaCliente().equals(huella)){
                        //IMPRIMIR EN DISPLAY
                        Platform.runLater(()->{
                            lipiaPantalla();
                            digitoPantalla(data.mensaje);
                        });
                    }else {System.out.println("La información probiene de la calculadora");}
                    misocket.close();
                }

            }
            catch(IOException|ClassNotFoundException e){
                System.out.println(e.getMessage());
                System.out.println("no se asigno puerto");
                puertoCalculadora++;
                puertoMiddleware++;
            }
        }

    }
}

