/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pedroalvarez.system;

import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.pedroalvarez.controller.DatosProgramadorController;
import org.pedroalvarez.controller.EmpleadoController;
import org.pedroalvarez.controller.EmpresaController;
import org.pedroalvarez.controller.MenuPrincipalController;
import org.pedroalvarez.controller.PlatosController;
import org.pedroalvarez.controller.PresupuestoController;
import org.pedroalvarez.controller.ProductosController;
import org.pedroalvarez.controller.ProductosHasPlatosController;
import org.pedroalvarez.controller.ServicioController;
import org.pedroalvarez.controller.ServiciosHasEmpleadosController;
import org.pedroalvarez.controller.ServiciosHasPlatosController;
import org.pedroalvarez.controller.TipoEmpleadoController;
import org.pedroalvarez.controller.TipoPlatoController;

/**     
 *
 * @author elwic
 */
public class Principal extends Application {
   private final String PAQUETE_VISTA = "/org/pedroalvarez/view/";
   private Stage escenarioPrincipal;
   private Scene escena;
    
    
    @Override
    public void start(Stage escenarioPrincipal) throws Exception {
        this.escenarioPrincipal = escenarioPrincipal; 
        this.escenarioPrincipal.setTitle("Tonys Kinal App");
        escenarioPrincipal.getIcons().add(new Image("/org/pedroalvarez/images/Logo.png"));
        //Parent root = FXMLLoader.load(getClass().getResource("/org/pedroalvarez/view/MenuPrincipalView.fxml"));
        //Scene escena = new Scene(root);
       // escenarioPrincipal.setScene(escena);
       menuPrincipal();
        escenarioPrincipal.show();
    }

    public void menuPrincipal(){
       try { 
           MenuPrincipalController menuPrincipal = (MenuPrincipalController)cambiarEscena("MenuPrincipalView.fxml", 560, 399);
           menuPrincipal.setEscenarioPrincipal(this);
       }catch(Exception e){
        e.printStackTrace();
       }
    }
    
    public void ventanaDatosProgramador(){
        try { 
           DatosProgramadorController datosProgramador = (DatosProgramadorController)cambiarEscena("DatosProgramadorView.fxml", 400, 400);
           datosProgramador.setEscenarioPrincipal(this);
       }catch(Exception e){
        e.printStackTrace();
       }
    }
    
    public void ventanaEmpresas(){
        try{
            EmpresaController empresaController = (EmpresaController)cambiarEscena("EmpresaView.fxml", 600,600);
            empresaController.setEscenarioPrincipal(this);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    public Initializable cambiarEscena(String fxml,int ancho, int alto) throws Exception{
        Initializable resultado = null;
        FXMLLoader cargadorFXML = new FXMLLoader();
        InputStream archivo = Principal.class.getResourceAsStream(PAQUETE_VISTA+fxml);
        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Principal.class.getResource(PAQUETE_VISTA+fxml));
        escena = new Scene((AnchorPane)cargadorFXML.load(archivo), ancho, alto);
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.sizeToScene();
        resultado = (Initializable)cargadorFXML.getController();
                
        return resultado;
    }
    
    public void ventanaPresupuesto(){
        try{
            PresupuestoController presupuesto = (PresupuestoController)cambiarEscena("PresupuestoView.fxml", 600,600);
            presupuesto.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaTipoEmpleado(){
        try{
            TipoEmpleadoController tipoEmpleado = (TipoEmpleadoController)cambiarEscena("TipoEmpleadoView.fxml", 600,600);
            tipoEmpleado.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaEmpleado(){
        try{
            EmpleadoController empleado = (EmpleadoController)cambiarEscena("EmpleadosView.fxml", 600, 600);
            empleado.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaServicios(){
        try{
            ServicioController servicios = (ServicioController)cambiarEscena("ServiciosView.fxml", 600, 600);
            servicios.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaServiciosHasEmpleados(){
        try{
            ServiciosHasEmpleadosController ServiciosHasEmpleados = (ServiciosHasEmpleadosController)cambiarEscena("ServiciosHasEmpleadosView.fxml", 600, 600);
            ServiciosHasEmpleados.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaProductos(){
        try{
            ProductosController productos = (ProductosController)cambiarEscena("ProductosView.fxml", 600, 600);
            productos.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaTipoPlato(){
        try{
            TipoPlatoController TipoPlato = (TipoPlatoController)cambiarEscena("TipoPlatoView.fxml", 600, 600);
            TipoPlato.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaPlatos(){
        try{
            PlatosController platos = (PlatosController)cambiarEscena("PlatosView.fxml", 600, 600);
            platos.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaProductosHasPlatos(){
        try{
            ProductosHasPlatosController ProductosHasPlatos = (ProductosHasPlatosController)cambiarEscena("ProductosHasPlatosView.fxml", 600, 600);
            ProductosHasPlatos.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaServiciosHasPlatos(){
        try{
        ServiciosHasPlatosController ServiciosHasPlatos = (ServiciosHasPlatosController)cambiarEscena("ServiciosHasPlatosView.fxml", 600, 600);
        ServiciosHasPlatos.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
