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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.pedroalvarez.bean.Productos;
import org.pedroalvarez.db.Conexion;
import org.pedroalvarez.report.GenerarReporte;
import org.pedroalvarez.system.Principal;


public class ProductosController implements Initializable{
    private enum operaciones {NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private ObservableList<Productos> listaProductos;
    boolean vacio = false;
    @FXML private TextField txtCodigoProducto;
    @FXML private TextField txtNombreProducto;
    @FXML private TextField txtCantidad;
    @FXML private TableView tblProductos;
    @FXML private TableColumn colCodigoProducto;
    @FXML private TableColumn colNombreProducto;
    @FXML private TableColumn colCantidad;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    }
    
    public void cargarDatos(){
        tblProductos.setItems(getProductos());
        colCodigoProducto.setCellValueFactory(new PropertyValueFactory<Productos, Integer>("codigoProducto"));
        colNombreProducto.setCellValueFactory(new PropertyValueFactory<Productos, String>("nombreProducto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<Productos, Integer>("cantidad"));
    }
    
    public void seleccionarElemento(){
        if(tblProductos.getSelectionModel().getSelectedItem() != null){
            txtCodigoProducto.setText(String.valueOf(((Productos)tblProductos.getSelectionModel().getSelectedItem()).getCodigoProducto()));
            txtNombreProducto.setText(((Productos)tblProductos.getSelectionModel().getSelectedItem()).getNombreProducto());
            txtCantidad.setText(String.valueOf(((Productos)tblProductos.getSelectionModel().getSelectedItem()).getCantidad()));
        }
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
    
    public void desactivarControles(){
        txtCodigoProducto.setEditable(false);
        txtNombreProducto.setEditable(false);
        txtCantidad.setEditable(false);
    }
    
    public void activarControles(){
        txtCodigoProducto.setEditable(false);
        txtNombreProducto.setEditable(true);
        txtCantidad.setEditable(true);
    }
    
    public void limpiarControles(){
        txtCodigoProducto.setText("");
        txtNombreProducto.setText("");
        txtCantidad.setText("");
    }
    
    public void nuevo(){
        switch (tipoOperacion){
            case NINGUNO:
                limpiarControles();
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEliminar.setDisable(false);
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tipoOperacion = operaciones.GUARDAR;
                cargarDatos();
                tblProductos.setDisable(true);
                txtCodigoProducto.setDisable(true);
                break;
                
            case GUARDAR:
                validacion();
                if(vacio == false){
                    guardar();
                    desactivarControles();
                    limpiarControles();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliiminar");
                    btnEliminar.setDisable(false);
                    btnEditar.setDisable(false);
                    tipoOperacion = operaciones.NINGUNO;
                    tblProductos.setDisable(false);
                    txtCodigoProducto.setDisable(false);
                    btnReporte.setDisable(false);
                }
                else{
                    JOptionPane.showMessageDialog(null, "Debe llenar todos los datos");
                    cargarDatos();
                    tipoOperacion = operaciones.GUARDAR;
                }
            break;
        }
    }
    
    private boolean validacion(){
        Productos registro = new Productos();
        registro.setNombreProducto(txtNombreProducto.getText());
        String cantidad = String.valueOf(txtCantidad.getText());
        if(registro.getNombreProducto().equals("") || cantidad.equals(""))
            vacio = true;
        else 
            vacio = false;
        return vacio;
    }
    
    public void guardar(){
        Productos registro = new Productos();
        registro.setNombreProducto(txtNombreProducto.getText());
        registro.setCantidad(Integer.parseInt(txtCantidad.getText()));
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarProductos(?,?)}");
            procedimiento.setString(1, registro.getNombreProducto());
            procedimiento.setInt(2, registro.getCantidad());
            procedimiento.execute();
            listaProductos.add(registro);
            cargarDatos();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void eliminar(){
        switch (tipoOperacion){
            case GUARDAR:
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEliminar.setDisable(false);
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                cargarDatos();
                tipoOperacion = operaciones.NINGUNO;
                tblProductos.setDisable(false);
                txtCodigoProducto.setDisable(false);
            break;
            
            default:
                if(tblProductos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Esta seguro de eliminar el registro", "Eliminar Prodoctos", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_eliminarProductos(?)");
                            procedimiento.setInt(1, ((Productos)tblProductos.getSelectionModel().getSelectedItem()).getCodigoProducto());
                            procedimiento.execute();
                            listaProductos.remove(tblProductos.getSelectionModel().getSelectedItem());
                            limpiarControles();
                            cargarDatos();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }if (respuesta == JOptionPane.NO_OPTION){
                        cargarDatos();
                        limpiarControles();
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                    cargarDatos();
                    limpiarControles();
                }
        }
        desactivarControles();
    }
    
    public void editar(){
        switch(tipoOperacion){
            case NINGUNO:
                if(tblProductos.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tipoOperacion = operaciones.ACTUALIZAR;
                    txtCodigoProducto.setDisable(true);
                    JOptionPane.showMessageDialog(null, "Recueda no dejar datos vacios");
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
            break;
            
            case ACTUALIZAR:
                actualizar();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tipoOperacion = operaciones.NINGUNO;
                txtCodigoProducto.setDisable(false);
                cargarDatos();
                limpiarControles();
                desactivarControles();
            break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ActualizarProductos(?,?,?)}");
            Productos registro = (Productos)tblProductos.getSelectionModel().getSelectedItem();
            registro.setNombreProducto(txtNombreProducto.getText());
            registro.setCantidad(Integer.parseInt(txtCantidad.getText()));
            procedimiento.setInt(1, registro.getCodigoProducto());
            procedimiento.setString(2, registro.getNombreProducto());
            procedimiento.setInt(3, registro.getCantidad());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void reporte(){
        switch(tipoOperacion){
            case ACTUALIZAR:
                desactivarControles();
                limpiarControles();
                txtCodigoProducto.setDisable(false);
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnReporte.setText("Reporte");
                cargarDatos();
                tipoOperacion = operaciones.NINGUNO;
                btnEditar.setText("Editar");
            break;
        }
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