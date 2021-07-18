package org.pedroalvarez.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.pedroalvarez.system.Principal;

public class DatosProgramadorController implements Initializable{
    private Principal escenarioPrincipal;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
    /*@FXML
    private void opciones (ActionEvent event){
        if(event.getSource() == btn){
        }
    }*/
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void menuPrincipal() {
        escenarioPrincipal.menuPrincipal();
    }
    
}
