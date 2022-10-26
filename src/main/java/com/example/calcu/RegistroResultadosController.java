package com.example.calcu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

public class RegistroResultadosController {

    @FXML
    private VBox VBresultados;

    @FXML
    void borrarHistorial(ActionEvent event) {

    }

    void cargarResultados(List<String> resultados){
        for(String resultado:resultados){
            VBresultados.getChildren().add(new Label(resultado));
        }
    }
}
