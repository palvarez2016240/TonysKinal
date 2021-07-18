package org.pedroalvarez.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.pedroalvarez.bean.Platos;
import org.pedroalvarez.bean.Servicios;
import org.pedroalvarez.bean.ServiciosHasPlatos;
import org.pedroalvarez.db.Conexion;
import org.pedroalvarez.system.Principal;

public class ServiciosHasPlatosController implements Initializable{
    
    private Principal escenarioPrincipal;
    private ObservableList<Servicios> listaServicios;
    private ObservableList<Platos> listaPlatos;
    private ObservableList<ServiciosHasPlatos> listaServiciosHasPlatos;
    @FXML private ComboBox cmbCodigoServicio;
    @FXML private ComboBox cmbCodigoPlato;
    @FXML private TableView tblServiciosHasPlatos;
    @FXML private TableColumn colCodigoServicio;
    @FXML private TableColumn colCodigoPlato;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoServicio.setItems(getServicios());
        cmbCodigoPlato.setItems(getPlatos());
    }
    
    public void cargarDatos(){
        tblServiciosHasPlatos.setItems(getServiciosHasPlatos());
        colCodigoServicio.setCellValueFactory((new PropertyValueFactory<ServiciosHasPlatos, Integer>("codigoServicio")));
        colCodigoPlato.setCellValueFactory((new PropertyValueFactory<ServiciosHasPlatos, Integer>("codigoPlato")));
    }
    
    public void seleccionarElemento(){
        if(tblServiciosHasPlatos.getSelectionModel().getSelectedItem() != null){
            cmbCodigoServicio.setDisable(false);
            cmbCodigoPlato.setDisable(false);
            cmbCodigoServicio.setEditable(false);
            cmbCodigoPlato.setEditable(false);
            cmbCodigoServicio.getSelectionModel().select(buscarServicio(((ServiciosHasPlatos)tblServiciosHasPlatos.getSelectionModel().getSelectedItem()).getCodigoServicio()));
            cmbCodigoPlato.getSelectionModel().select(buscarPlatos(((ServiciosHasPlatos)tblServiciosHasPlatos.getSelectionModel().getSelectedItem()).getCodigoPlato()));
        }
    }
    
    public ObservableList<ServiciosHasPlatos> getServiciosHasPlatos(){
        ArrayList<ServiciosHasPlatos> lista = new ArrayList<ServiciosHasPlatos>();
            try{
/*Aqui*/        PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarServicios_Has_Platos}");
                ResultSet resultado = procedimiento.executeQuery();
                while (resultado.next()){
                    lista.add(new ServiciosHasPlatos( resultado.getInt("codigoServicio"),
                                                      resultado.getInt("codigoPlato")));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        return listaServiciosHasPlatos = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<Servicios> getServicios(){
        ArrayList<Servicios> lista = new ArrayList<Servicios>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarServicios}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Servicios(resultado.getInt("codigoServicio"),
                                        resultado.getDate("fechaServicio"),
                                        resultado.getString("tipoServicio"),
                                        resultado.getString("horaServicio"),
                                        resultado.getString("lugarServicio"),
                                        resultado.getString("telefonoContacto"),
                                        resultado.getInt("codigoEmpresa")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaServicios = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<Platos> getPlatos(){
        ArrayList<Platos> lista = new ArrayList<Platos>();
            try{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarPlatos");
                ResultSet resultado = procedimiento.executeQuery();
                while(resultado.next()){
                    lista.add(new Platos(resultado.getInt("codigoPlato"),
                                         resultado.getInt("cantidad"),
                                         resultado.getString("nombrePlato"),
                                         resultado.getString("descripcionPlato"),
                                         resultado.getDouble("precioPlato"),
                                         resultado.getInt("codigoTipoPlato")));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        return listaPlatos = FXCollections.observableArrayList(lista);
    }
    
    public ServiciosHasPlatos buscarServiciosHasPlatos(int codigoServicio, int codigoPlato){
        ServiciosHasPlatos resultado = null;
            try{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarServicios_Has_Platos(?,?)}");
                procedimiento.setInt(1, codigoServicio);
                procedimiento.setInt(2, codigoPlato);
                ResultSet registro = procedimiento.executeQuery();
                while(registro.next()){
                    resultado = new ServiciosHasPlatos(registro.getInt("codigoServicio"),
                                                       registro.getInt("codigoPlato"));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        return resultado;
    }
    
    public Servicios buscarServicio(int codigoServicio){
       Servicios resultado = null;
       try {
           PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarServicios(?)}");
           procedimiento.setInt(1, codigoServicio);
           ResultSet registro = procedimiento.executeQuery();
           while(registro.next()){
               resultado = new Servicios(registro.getInt("codigoServicio"),
                                         registro.getDate("fechaServicio"),
                                         registro.getString("tipoServicio"),
                                         registro.getString("horaServicio"),
                                         registro.getString("lugarServicio"),
                                         registro.getString("telefonoContacto"),
                                         registro.getInt("codigoEmpresa")
               );
           }
       }catch(Exception e){
           e.printStackTrace();
       }
       return resultado;
    }
    
    public Platos buscarPlatos(int codigoPlatos){
        Platos resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarPlatos(?)}");
            procedimiento.setInt(1, codigoPlatos);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Platos(registro.getInt("codigoPlato"),
                                       registro.getInt("cantidad"),
                                       registro.getString("nombrePlato"),
                                       registro.getString("descripcionPlato"),
                                       registro.getDouble("precioPlato"),
                                       registro.getInt("codigoTipoPlato"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
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