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
import org.pedroalvarez.bean.Productos;
import org.pedroalvarez.bean.ProductosHasPlatos;
import org.pedroalvarez.db.Conexion;
import org.pedroalvarez.system.Principal;

public class ProductosHasPlatosController implements Initializable{
    
    private Principal escenarioPrincipal;
    private ObservableList<Productos> listaProductos;
    private ObservableList<Platos> listaPlatos;
    private ObservableList<ProductosHasPlatos> listaProductosHasPlatos;
    @FXML private ComboBox cmbCodigoProducto;
    @FXML private ComboBox cmbCodigoPlato;
    @FXML private TableView tblProductosHasPlatos;
    @FXML private TableColumn colCodigoProducto;
    @FXML private TableColumn colCodigoPlato;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoProducto.setItems(getProductos());
        cmbCodigoPlato.setItems(getPlatos());
    }
    
    public void cargarDatos(){
        tblProductosHasPlatos.setItems(getProductosHasPlatos());
        colCodigoProducto.setCellValueFactory((new PropertyValueFactory<ProductosHasPlatos, Integer>("codigoProducto")));
        colCodigoPlato.setCellValueFactory(new PropertyValueFactory<ProductosHasPlatos, Integer>("codigoPlato"));
    }
    
    public void seleccionarElemento(){
        if(tblProductosHasPlatos.getSelectionModel().getSelectedItem() != null){
            cmbCodigoProducto.setDisable(false);
            cmbCodigoPlato.setDisable(false);
            cmbCodigoProducto.setEditable(false);
            cmbCodigoPlato.setEditable(false);
            cmbCodigoProducto.getSelectionModel().select(buscarProductos(((ProductosHasPlatos)tblProductosHasPlatos.getSelectionModel().getSelectedItem()).getCodigoProducto()));
            cmbCodigoPlato.getSelectionModel().select(buscarPlatos(((ProductosHasPlatos)tblProductosHasPlatos.getSelectionModel().getSelectedItem()).getCodigoPlato()));
        }
    }
    
    public ObservableList<ProductosHasPlatos> getProductosHasPlatos(){
        ArrayList<ProductosHasPlatos> lista = new ArrayList<ProductosHasPlatos>();
            try{
/*Aqui*/        PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_listarProductos_has_Platos");
                ResultSet resultado = procedimiento.executeQuery();
                while(resultado.next()){
                    lista.add(new ProductosHasPlatos(resultado.getInt("codigoProducto"),
                                                     resultado.getInt("codigoPlato")));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        return listaProductosHasPlatos = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<Productos> getProductos(){
        ArrayList<Productos> lista = new ArrayList<Productos>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_listarProductos");
            ResultSet resultado = procedimiento.executeQuery();
            while (resultado.next()){
                lista.add(new Productos( resultado.getInt("codigoProducto"),
                                         resultado.getString("nombreProducto"),
                                         resultado.getInt("cantidad")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaProductos = FXCollections.observableArrayList(lista);
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
    
    public ProductosHasPlatos buscarProductosHasPlatos(int codigoProducto, int codigoPlato){
        ProductosHasPlatos resultado = null;
            try{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarProductos_has_Platos(?,?)}");
                procedimiento.setInt(1, codigoProducto);
                procedimiento.setInt(2, codigoPlato);
                ResultSet registro = procedimiento.executeQuery();
                while (registro.next()){
                    resultado = new ProductosHasPlatos( registro.getInt("codigoProducto"),
                                                        registro.getInt("codigoPlato"));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        return resultado;
    }
    
    public Productos buscarProductos(int codigoProducto){
        Productos resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarProductos(?)}");
            procedimiento.setInt(1, codigoProducto);
            ResultSet registro = procedimiento.executeQuery();
            while (registro.next()){
                resultado = new Productos( registro.getInt("codigoProducto"),
                                           registro.getString("nombreProducto"),
                                           registro.getInt("cantidad"));
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