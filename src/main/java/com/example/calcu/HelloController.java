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
import java.util.*;


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
        int mandar = 0;
        while (mandar<4)
        {
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
                mandar=5;
                break;

            }catch (IOException e){
                PUERTO_MIDDLEWARE++;
                e.printStackTrace();
            }
            mandar++;
            try {
                Socket enviaReceptor=new Socket("127.0.0.1", PUERTO_MIDDLEWARE);
                ObjectOutputStream paqueteReenvio=new ObjectOutputStream(enviaReceptor.getOutputStream());
                Paquete conocerCalcu = new Paquete(" ", PUERTO_ACTUAL, 'N', 'z', " ", " ");
                conocerCalcu.setIDdireccion('N');
                conocerCalcu.setCodigoOperacion('z');
                paqueteReenvio.writeObject(conocerCalcu);
                paqueteReenvio.close();
                enviaReceptor.close();
                hiloEnvia.PUERTO_MIDDLEWARE = PUERTO_MIDDLEWARE;

            } catch(IOException e) {
                //System.out.println(e);
                // System.out.println("servidor apagado: "+puerto);
            }
        }

        lipiaPantalla();
    }

    Paquete Nuevo_paquete(Paquete r){

        Paquete paquete1=new Paquete(r.getMensaje(),r.getPuertoEmisor(),r.getIDdireccion(),r.getCodigoOperacion(),r.getHuellaCliente(),r.getHuellaServidor());
        paquete1.setIDdireccion('O');
        paquete1.setCodigoOperacion('a');
        paquete1.setHuellaCliente(huella);
        paquete1.setHuellaServidor(paquete1.getHuellaServidor());
        paquete1.setEvento(r.getEvento());
        paquete1.setTiempoAcuse(r.getTiempoAcuse());
        paquete1.setAcusesRecibidos(r.getAcusesRecibidos());
        paquete1.setClon(r.getClon());
        return paquete1;
    }
    public void enviarHilo(){
        List<Paquete> litaEventos = new ArrayList<>();
        if (listaSuma.size() > 0){
            litaEventos.add(Nuevo_paquete(listaSuma.get(0)));

        }
        if (listaResta.size() > 0){
            litaEventos.add(Nuevo_paquete(listaResta.get(0)));
        }
        if (listaMustiplicacion.size() > 0){
            litaEventos.add(Nuevo_paquete(listaMustiplicacion.get(0)));
        }
        if (listaDivision.size() > 0){
            litaEventos.add(Nuevo_paquete(listaDivision.get(0)));
        }
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

    public Paquete recuperarEventos(String eventoEnLista){
        for (Paquete paqueteS :listaSuma){
            if (paqueteS.getEvento().equals(eventoEnLista)){
                return paqueteS;
            }
        }
        for (Paquete paqueteR :listaResta){
            if (paqueteR.getEvento().equals(eventoEnLista)){
                return paqueteR;
            }
        }
        for (Paquete paqueteM :listaMustiplicacion){
            if (paqueteM.getEvento().equals(eventoEnLista)){
                return paqueteM;
            }
        }
        for (Paquete paqueteD :listaDivision){
            if (paqueteD.getEvento().equals(eventoEnLista)){
                return paqueteD;
            }
        }

        return null;//Refresa el mismo paquete
    }
    public Paquete agregarHuella(String eventoEnLista,String huella){
        for (Paquete paqueteS :listaSuma){
            if (paqueteS.getEvento().equals(eventoEnLista)){
                paqueteS.setClon(huella);
                return paqueteS;
            }
        }
        for (Paquete paqueteR :listaResta){
            if (paqueteR.getEvento().equals(eventoEnLista)){
                paqueteR.setClon(huella);
                return paqueteR;
            }
        }
        for (Paquete paqueteM :listaMustiplicacion){
            if (paqueteM.getEvento().equals(eventoEnLista)){
                paqueteM.setClon(huella);
                return paqueteM;
            }
        }
        for (Paquete paqueteD :listaDivision){
            if (paqueteD.getEvento().equals(eventoEnLista)){
                paqueteD.setClon(huella);
                return paqueteD;
            }
        }

        return null;//Refresa el mismo paquete
    }

    public int buscarAcuseMinimo(String eventoEnLista){
        for (Paquete paqueteS :listaSuma){
            if (paqueteS.getEvento().equals(eventoEnLista)){
                return ACUSE_MIN_SUMA;
            }
        }
        for (Paquete paqueteR :listaResta){
            if (paqueteR.getEvento().equals(eventoEnLista)){
                return ACUSE_MIN_RESTA;
            }
        }
        for (Paquete paqueteM :listaMustiplicacion){
            if (paqueteM.getEvento().equals(eventoEnLista)){
                return ACUSE_MIN_MULT;
            }
        }
        for (Paquete paqueteD :listaDivision){
            if (paqueteD.getEvento().equals(eventoEnLista)){
                return ACUSE_MIN_DIV;
            }
        }

        return 0;//Refresa el mismo paquete
    }
    void rellenarLista(List<Paquete> original,List<Paquete>  copia){
        for(Paquete elemento:original){
            copia.add(elemento);
        }
    }
    public boolean eliminarDeLista(String eliminarEvento){
        List<Paquete> copiaS = new ArrayList<>();
        rellenarLista(listaSuma,copiaS);
        List<Paquete> copiaR = new ArrayList<>();
        rellenarLista(listaSuma,copiaR);
        List<Paquete> copiaM = new ArrayList<>();
        rellenarLista(listaSuma,copiaM);
        List<Paquete> copiaD = new ArrayList<>();
        rellenarLista(listaSuma,copiaD);
        for (Paquete paqueteS: listaSuma ) {
            if (paqueteS.getEvento().equals(eliminarEvento)){
                copiaS.remove(paqueteS);
                listaSuma=copiaS;
                return true;
            }
        }
        for (Paquete paqueteR :listaResta){
            if (paqueteR.getEvento().equals(eliminarEvento)){
                copiaR.remove(paqueteR);
                listaResta=copiaR;
                return true;
            }
        }
        for (Paquete paqueteM :listaMustiplicacion){
            if (paqueteM.getEvento().equals(eliminarEvento)){
                copiaM.remove(paqueteM);
                listaMustiplicacion=copiaM;
                return true;
            }
        }
        for (Paquete paqueteD :listaDivision){
            if (paqueteD.getEvento().equals(eliminarEvento)){
                copiaD.remove(paqueteD);
                listaDivision=copiaD;
                return true;
            }
        }
        return false;
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
                servidor=new ServerSocket(puertoCalculadora);
                System.out.println("estoy en el puerto"+ puertoCalculadora);
                PUERTO_MIDDLEWARE= puertoMiddleware;
                PUERTO_ACTUAL = puertoCalculadora;
                hiloEnvia.PUERTO_MIDDLEWARE = PUERTO_MIDDLEWARE;
                try {
                    Socket enviaReceptor=new Socket("127.0.0.1",puertoMiddleware);
                    ObjectOutputStream paqueteReenvio=new ObjectOutputStream(enviaReceptor.getOutputStream());
                    Paquete conocerCalcu = new Paquete(" ", puertoCalculadora, 'N', 'z', " ", " ");
                    conocerCalcu.setIDdireccion('N');
                    conocerCalcu.setCodigoOperacion('z');
                    paqueteReenvio.writeObject(conocerCalcu);
                    paqueteReenvio.close();
                    enviaReceptor.close();
                } catch(IOException e) {
                    //System.out.println(e);
                    // System.out.println("servidor apagado: "+puerto);
                }
                hiloEnvia.start();
                //-
                // ------------------------------------- GENERO HUELLA -----------------
                String puertoString= PUERTO_ACTUAL+"";
                huella = generarHuella(puertoString);
                System.out.println("Huella generada por mi puerto actual "+PUERTO_ACTUAL +" "+huella);
                //------------------------------------- GENERO HUELLA ------------------------


                //-------------------------------- RECIBO PAQUETE -----------------------------
                while(true){
                    enviarHilo(); //envio al hilo
                    Socket misocket=servidor.accept();//aceptara las conexiones que vengan del exterior
                    ObjectInputStream flujoEntrada=new ObjectInputStream(misocket.getInputStream());
                    Paquete data = (Paquete)flujoEntrada.readObject();
                    historial.add(data.mensaje);
                    System.out.println("Soy un paquete que llega con el codigo de operacion "+data.getCodigoOperacion());
                    //System.out.println(data);
                    //System.out.println("ACUSE: "+data.getAcuse());
                    if(data.getCodigoOperacion() == 'h'){
                        ACUSE_MIN_SUMA = data.getAcusesActualizadosSuma();
                        ACUSE_MIN_RESTA = data.getAcusesActualizadosResta();
                        ACUSE_MIN_MULT = data.getAcusesActualizadosMultiplicacion();
                        ACUSE_MIN_DIV = data.getAcusesActualizadosDivision();

                    }
                    if (data.getCodigoOperacion() == 'm' & data.getHuellaCliente().equals(huella)) {

                        Paquete paqueteRecuperado = recuperarEventos(data.getEvento());
                        if (paqueteRecuperado != null) {
                            //System.out.println("recuperado "+paqueteRecuperado.getTiempoAcuse().length()+" externo"+data.getTiempoAcuse().length());
                            if (paqueteRecuperado.getTiempoAcuse().equals(data.getTiempoAcuse())) {
                                paqueteRecuperado.setAcusesRecibidos(paqueteRecuperado.getAcusesRecibidos() + 1);
                                System.out.println("misma ronda");
                            } else {
                                System.out.println("solo yo");
                                paqueteRecuperado.setTiempoAcuse(data.getTiempoAcuse());
                                paqueteRecuperado.setAcusesRecibidos(1);
                            }
                            if((paqueteRecuperado.getTiempoAcuse().length())%2==0){
                                //buscar huellaServidor más grande
                                paqueteRecuperado.setClon(data.getHuellaServidor());
                                System.out.println("Mando guella a clonar "+data.getHuellaServidor());
                                System.out.println("Longitud "+paqueteRecuperado.getTiempoAcuse().length());
                                agregarHuella(paqueteRecuperado.getEvento(), data.getHuellaServidor());

                            }else{
                                paqueteRecuperado.setClon("nono");
                                agregarHuella(paqueteRecuperado.getEvento(), "nono");
                            }
                            //IMPRIMIR EN DISPLAY
                            int acuseMinimo = buscarAcuseMinimo(paqueteRecuperado.getEvento());
                            if (paqueteRecuperado.getAcusesRecibidos() >= acuseMinimo) {

                                Platform.runLater(() -> {
                                    lipiaPantalla();
                                    digitoPantalla(data.mensaje);
                                });
                                eliminarDeLista(paqueteRecuperado.getEvento());//elimino el evento
                            }

                        } else {
                            System.out.println("La información probiene de la calculadora");
                        }
                        misocket.close();
                    }
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

