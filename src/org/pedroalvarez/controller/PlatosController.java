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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.pedroalvarez.bean.Platos;
import org.pedroalvarez.bean.TipoPlato;
import org.pedroalvarez.db.Conexion;
import org.pedroalvarez.system.Principal;

public class PlatosController implements Initializable{
    
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private ObservableList<Platos> listaPlatos;
    private ObservableList<TipoPlato> listaTipoPlato;
    boolean vacio = false;
    @FXML private TextField txtCodigoPlato;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtNombrePlato;
    @FXML private TextField txtDescripcionPlato;
    @FXML private TextField txtPrecioPlato;
    @FXML private ComboBox cmbCodigoTipoPlato;
    @FXML private TableView tblPlatos;
    @FXML private TableColumn colCodigoPlato;
    @FXML private TableColumn colCantidad;
    @FXML private TableColumn colNombrePlato;
    @FXML private TableColumn colDescripcionPlato;
    @FXML private TableColumn colPrecioPlato;
    @FXML private TableColumn colCodigoTipoPlato;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoTipoPlato.setItems(getTipoPlato());
    }
    
    public void cargarDatos(){
        tblPlatos.setItems(getPlatos());
        colCodigoPlato.setCellValueFactory(new PropertyValueFactory<Platos, Integer>("codigoPlato"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<Platos, Integer>("cantidad"));
        colNombrePlato.setCellValueFactory(new PropertyValueFactory<Platos, String>("nombrePlato"));
        colDescripcionPlato.setCellValueFactory(new PropertyValueFactory<Platos, String>("descripcionPlato"));
        colPrecioPlato.setCellValueFactory(new PropertyValueFactory<Platos, Double>("precioPlato"));
        colCodigoTipoPlato.setCellValueFactory(new PropertyValueFactory<Platos, Integer>("codigoTipoPlato"));
    }
    
    public void seleccionarElemento(){
        if(tblPlatos.getSelectionModel().getSelectedItem() != null){
            txtCodigoPlato.setText(String.valueOf(((Platos)tblPlatos.getSelectionModel().getSelectedItem()).getCodigoPlato()));
            txtCantidad.setText(String.valueOf(((Platos)tblPlatos.getSelectionModel().getSelectedItem()).getCantidad()));
            txtNombrePlato.setText(((Platos)tblPlatos.getSelectionModel().getSelectedItem()).getNombrePlato());
            txtDescripcionPlato.setText(((Platos)tblPlatos.getSelectionModel().getSelectedItem()).getDescripcionPlato());
            txtPrecioPlato.setText(String.valueOf(((Platos)tblPlatos.getSelectionModel().getSelectedItem()).getPrecioPlato()));
            cmbCodigoTipoPlato.getSelectionModel().select(buscarTipoPlato(((Platos)tblPlatos.getSelectionModel().getSelectedItem()).getCodigoTipoPlato()));
            cmbCodigoTipoPlato.setDisable(false);
        }
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
    
    public ObservableList<TipoPlato> getTipoPlato(){
        ArrayList<TipoPlato> lista = new ArrayList<TipoPlato>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarTipoPlato");
            ResultSet resultado = procedimiento.executeQuery();
                while (resultado.next()){lista.add(new TipoPlato( resultado.getInt("codigoTipoPlato"),
                                                                  resultado.getString("descripcionTipo")));
                }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaTipoPlato = FXCollections.observableArrayList(lista);
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
    
    public TipoPlato buscarTipoPlato(int codigoTipoPlato){
        TipoPlato resultado = null;
            try{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarTipoPlato(?)}");
                procedimiento.setInt(1, codigoTipoPlato);
                ResultSet registro = procedimiento.executeQuery();
                while (registro.next()){
                    resultado = new TipoPlato(
                            registro.getInt("codigoTipoPlato"),
                            registro.getString("descripcionTipo")
                    );
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        return resultado;
    }
    
    public void desactivarControles(){
        txtCodigoPlato.setEditable(false);
        txtCantidad.setEditable(false);
        txtNombrePlato.setEditable(false);
        txtDescripcionPlato.setEditable(false);
        txtPrecioPlato.setEditable(false);
        cmbCodigoTipoPlato.setDisable(false);
    }
    
    public void activarControles(){
        txtCodigoPlato.setEditable(false);
        txtCantidad.setEditable(true);
        txtNombrePlato.setEditable(true);
        txtDescripcionPlato.setEditable(true);
        txtPrecioPlato.setEditable(true);
        cmbCodigoTipoPlato.setDisable(false);
    }
    
    public void limpiarControles(){
        txtCodigoPlato.setText("");
        txtCantidad.setText("");
        txtNombrePlato.setText("");
        txtDescripcionPlato.setText("");
        txtPrecioPlato.setText("");
        cmbCodigoTipoPlato.getSelectionModel().clearSelection();
    }
    
    public void nuevo(){
        switch(tipoOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setDisable(false);
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tipoOperacion = operaciones.GUARDAR;
                tblPlatos.setDisable(true);
                txtCodigoPlato.setDisable(true);
            break;
            
            case GUARDAR:
                validacion();
                if(vacio == false){
                    guardar();
                    desactivarControles();
                    limpiarControles();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEliminar.setDisable(false);
                    btnEditar.setDisable(false);
                    tipoOperacion = operaciones.NINGUNO;
                    tblPlatos.setDisable(false);
                    txtCodigoPlato.setDisable(false);
                    btnReporte.setDisable(false);
                    cmbCodigoTipoPlato.setDisable(true);
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
        Platos registro = new Platos();
        registro.setNombrePlato(txtNombrePlato.getText());
        registro.setDescripcionPlato(txtDescripcionPlato.getText());
        String cantidad = String.valueOf(txtCantidad.getText());
        String precio = String.valueOf(txtPrecioPlato.getText());
        //String codigoTipo = String.valueOf(cmbCodigoTipoPlato.getText());
            if(cantidad.equals("") || registro.getNombrePlato().equals("") || registro.getDescripcionPlato().equals("") || precio.equals("") || 
               cmbCodigoTipoPlato.getSelectionModel().getSelectedItem() == null)
                vacio = true;
            else
                vacio = false;
        return vacio;
    }
    
    public void guardar(){
       Platos registro = new Platos();
       try{
           registro.setCantidad(Integer.parseInt(txtCantidad.getText()));
           registro.setNombrePlato(txtNombrePlato.getText());
           registro.setDescripcionPlato(txtDescripcionPlato.getText());
           registro.setPrecioPlato(Double.parseDouble(txtPrecioPlato.getText()));
           registro.setCodigoTipoPlato(((TipoPlato)cmbCodigoTipoPlato.getSelectionModel().getSelectedItem()).getCodigoTipoPlato());
               PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarPlatos(?,?,?,?,?)}");
               procedimiento.setInt(1, registro.getCantidad());
               procedimiento.setString(4, registro.getNombrePlato());
               procedimiento.setString(3, registro.getDescripcionPlato());
               procedimiento.setDouble(2, registro.getPrecioPlato());
               procedimiento.setInt(5, registro.getCodigoTipoPlato());
               procedimiento.execute();
               listaPlatos.add(registro);
               cargarDatos();
       }catch(Exception e){
           e.printStackTrace();
       }
    }
    
    public void eliminar(){
        switch(tipoOperacion){
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
                tblPlatos.setDisable(false);
                txtCodigoPlato.setDisable(false);
                cmbCodigoTipoPlato.setDisable(false);
            break;
            
            default:
                if(tblPlatos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Esta seguro de eliminar el registro?", "Eliminar Platos", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_NO_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_eliminarPlatos(?)}");
                            procedimiento.setInt(1 , ((Platos)tblPlatos.getSelectionModel().getSelectedItem()).getCodigoPlato());
                            procedimiento.execute();
                            listaPlatos.remove(tblPlatos.getSelectionModel().getSelectedItem());
                            limpiarControles();
                            cargarDatos();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    if(respuesta == JOptionPane.NO_OPTION){
                        cargarDatos();
                        limpiarControles();
                    }
                }
                else{ 
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                    cargarDatos();
                    limpiarControles();
                }
        }
        cmbCodigoTipoPlato.setDisable(true);
    }
    
    public void editar(){
        switch(tipoOperacion){
            case NINGUNO:
                if(tblPlatos.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tipoOperacion = operaciones.ACTUALIZAR;
                    txtCodigoPlato.setDisable(true);
                    cmbCodigoTipoPlato.setDisable(true);
                    JOptionPane.showMessageDialog(null, "Recuerda no dejar datos vacios");
                }else
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
            break;
            
            case ACTUALIZAR:
                actualizar();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tipoOperacion = operaciones.NINGUNO;
                txtCodigoPlato.setDisable(false);
                cargarDatos();
                limpiarControles();
                desactivarControles();
                cmbCodigoTipoPlato.setDisable(true);
            break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ActualizarPlatos(?,?,?,?,?,?)}");
            Platos registro = (Platos)tblPlatos.getSelectionModel().getSelectedItem();
            registro.setCantidad(Integer.parseInt(txtCantidad.getText()));
            registro.setNombrePlato(txtNombrePlato.getText());
            registro.setDescripcionPlato(txtDescripcionPlato.getText());
            registro.setPrecioPlato(Double.parseDouble(txtPrecioPlato.getText()));
            procedimiento.setInt(1, registro.getCodigoPlato());
            procedimiento.setInt(2, registro.getCantidad());
            procedimiento.setString(5, registro.getNombrePlato());
            procedimiento.setString(4, registro.getDescripcionPlato());
            procedimiento.setDouble(3, registro.getPrecioPlato());
            procedimiento.setInt(6, registro.getCodigoTipoPlato());
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
                txtCodigoPlato.setDisable(false);
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnReporte.setText("Reporte");
                cargarDatos();
                tipoOperacion = operaciones.NINGUNO;
                btnEditar.setText("Editar");
                cmbCodigoTipoPlato.setDisable(true);
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
    
    public void ventanaTipoPlato(){
        escenarioPrincipal.ventanaTipoPlato();
    }
}