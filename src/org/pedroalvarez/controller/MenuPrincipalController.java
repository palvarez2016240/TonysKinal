package org.pedroalvarez.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.pedroalvarez.system.Principal;


public class MenuPrincipalController implements Initializable{
    private Principal escenarioPrincipal;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaDatosProgramador(){
        escenarioPrincipal.ventanaDatosProgramador();
    }
    
    public void ventanaEmpresas(){
        escenarioPrincipal.ventanaEmpresas();
    }
    
    public void ventanaPresupuesto(){
        escenarioPrincipal.ventanaPresupuesto();
    }
    
    public void ventanaTipoEmpleado(){
        escenarioPrincipal.ventanaTipoEmpleado();
    }
    
    public void ventanaEmpleado(){
        escenarioPrincipal.ventanaEmpleado();
    }
    
    public void ventanaServicios(){
        escenarioPrincipal.ventanaServicios();
    }
    
    public void ventanaServiciosHasEmpleados(){
        escenarioPrincipal.ventanaServiciosHasEmpleados();
    }
    
    public void ventanaProductos(){
        escenarioPrincipal.ventanaProductos();
    }
    
    public void ventanaTipoPlato(){
        escenarioPrincipal.ventanaTipoPlato();
    }
    
    public void ventanaPlatos(){
        escenarioPrincipal.ventanaPlatos();
    }
    
    public void ventanaProductosHasPlatos(){
        escenarioPrincipal.ventanaProductosHasPlatos();
    }
    
    public void ventanaServiciosHasPlatos(){
        escenarioPrincipal.ventanaServiciosHasPlatos();
    }
}
