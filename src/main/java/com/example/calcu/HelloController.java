package com.example.calcu;

import com.example.paquete.Paquete;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class HelloController implements Runnable{
    public int PUERTO_ACTUAL = 0;
    public int PUERTO_MIDDLEWARE = 0;
    public Paquete paquete = new Paquete("",  0, '0');
    @FXML
    private Button ACbt;

    @FXML
    private Button bt0;

    @FXML
    private Button bt1;

    @FXML
    private Button bt2;

    @FXML
    private Button bt3;

    @FXML
    private Button bt4;

    @FXML
    private Button bt5;

    @FXML
    private Button bt6;

    @FXML
    private Button bt7;

    @FXML
    private Button bt8;

    @FXML
    private Button bt9;

    @FXML
    private Label diisplabel;

    @FXML
    private Button divbt;

    @FXML
    private Button igualbt;

    @FXML
    private Button masbt;

    @FXML
    private Button menosbt;

    @FXML
    private Button porbt;

    @FXML
    private Button puntobt;

    @FXML
    void ACck(ActionEvent event) {
        lipiaPantalla();
    }

    @FXML
    void ck0(ActionEvent event) {
        digitoPantalla("0");
    }

    @FXML
    void ck1(ActionEvent event) {
        digitoPantalla("1");
    }

    @FXML
    void ck2(ActionEvent event) {
        digitoPantalla("2");
    }

    @FXML
    void ck3(ActionEvent event) {
        digitoPantalla("3");
    }

    @FXML
    void ck4(ActionEvent event) {
        digitoPantalla("4");
    }

    @FXML
    void ck5(ActionEvent event) {
        digitoPantalla("5");
    }

    @FXML
    void ck6(ActionEvent event) {
        digitoPantalla("6");
    }

    @FXML
    void ck7(ActionEvent event) {
        digitoPantalla("7");
    }

    @FXML
    void ck8(ActionEvent event) {
        digitoPantalla("8");
    }

    @FXML
    void ck9(ActionEvent event) {
        digitoPantalla("9");
    }

    @FXML
    void ckdiv(ActionEvent event) {
        digitoPantalla("/");

    }

    @FXML
    void ckigual(ActionEvent event) {
        try {
            Socket misoket = new Socket("127.0.0.1",PUERTO_MIDDLEWARE);
            ObjectOutputStream flujoSalida = new ObjectOutputStream(misoket.getOutputStream());
            //DataOutputStream flujoSalida = new DataOutputStream(misoket.getOutputStream());
            System.out.println(paquete.getMensaje()+" "+paquete.getPuertoEmisor()+" "+paquete.getIDdireccion()+" llega a puerto MW "+PUERTO_MIDDLEWARE);
            flujoSalida.writeObject(paquete);
            flujoSalida.close();

        }catch (IOException e){
            e.printStackTrace();
        }
        lipiaPantalla();
    }


    @FXML
    void ckmas(ActionEvent event) {
        digitoPantalla("+");

    }

    @FXML
    void ckmenos(ActionEvent event) {
        digitoPantalla("-");

    }

    @FXML
    void ckpor(ActionEvent event) {
        digitoPantalla("x");

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

                //asignacion = false;
                //ahora que acepte cualquier conexion que venga del exterior con el metodo accept

                while(true){
                    Socket misocket=servidor.accept();//aceptara las conexiones que vengan del exterior
                    ObjectInputStream flujoEntrada=new ObjectInputStream(misocket.getInputStream());
                    Paquete data = (Paquete)flujoEntrada.readObject();
                    //String resultado = flujoEntrada.readUTF();
                    System.out.println(data);
                    if (data.getIDdireccion()=='S'){
                        //IMPRIMIR EN DISPLAY
                        Platform.runLater(()->{
                            //mensajes.setText(mensaje);
                            lipiaPantalla();
                            digitoPantalla(data.mensaje);
                        });
                    }else if(data.getIDdireccion()=='C'){System.out.println("La información probiene de la calculadora");}



                    // misocket.close();
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
    private Stage stage;
    private Scene scene;
    private Parent root;

}
