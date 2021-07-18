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
import org.pedroalvarez.bean.TipoPlato;
import org.pedroalvarez.db.Conexion;
import org.pedroalvarez.system.Principal;

public class TipoPlatoController implements Initializable{
    
    private enum operaciones {NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private ObservableList<TipoPlato> listaTipoPlato;
    private boolean vacio = false;
    @FXML private TextField txtCodigoTipoPlato;
    @FXML private TextField txtDescripcionTipo;
    @FXML private TableView tblTipoPlato;
    @FXML private TableColumn colCodigoTipoPlato;
    @FXML private TableColumn colDescripcionTipo;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    }
    
    public void cargarDatos(){
        tblTipoPlato.setItems(getTipoPlato());
        colCodigoTipoPlato.setCellValueFactory(new PropertyValueFactory<TipoPlato, Integer>("codigoTipoPlato"));
        colDescripcionTipo.setCellValueFactory(new PropertyValueFactory<TipoPlato, String>("DescripcionTipo"));
    }
    
    public void seleccionarElemento(){
        if(tblTipoPlato.getSelectionModel().getSelectedItem() != null){
            txtCodigoTipoPlato.setText(String.valueOf(((TipoPlato)tblTipoPlato.getSelectionModel().getSelectedItem()).getCodigoTipoPlato()));
            txtDescripcionTipo.setText(((TipoPlato)tblTipoPlato.getSelectionModel().getSelectedItem()).getDescripcionTipo());
        }
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
        txtCodigoTipoPlato.setEditable(false);
        txtDescripcionTipo.setEditable(false);
    }
    
    public void activarControles(){
        txtCodigoTipoPlato.setEditable(false);
        txtDescripcionTipo.setEditable(true);
    }
    
    public void limpiarControles(){
        txtCodigoTipoPlato.setText("");
        txtDescripcionTipo.setText("");
    }
    
    public void nuevo(){
        switch (tipoOperacion){
            case NINGUNO:
                limpiarControles();
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setDisable(false);
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tipoOperacion = operaciones.GUARDAR;
                cargarDatos();
                tblTipoPlato.setDisable(true);
                txtCodigoTipoPlato.setDisable(true);
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
                    tblTipoPlato.setDisable(false);
                    txtCodigoTipoPlato.setDisable(false);
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
        TipoPlato registro = new TipoPlato();
        registro.setDescripcionTipo(txtDescripcionTipo.getText());
            if(registro.getDescripcionTipo().equals(""))
                vacio = true;
            else 
                vacio = false;
        return vacio;
    }
    
    public void guardar(){
        TipoPlato registro = new TipoPlato();
        registro.setDescripcionTipo(txtDescripcionTipo.getText());
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarTipoPlato(?)}");
            procedimiento.setString(1, registro.getDescripcionTipo());
            procedimiento.execute();
            listaTipoPlato.add(registro);
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
                tblTipoPlato.setDisable(false);
                txtCodigoTipoPlato.setDisable(false);
            break;
            
            default:
                if (tblTipoPlato.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Esta seguto de elimnar el registro?", "Eliminar Tipo Plato", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_eliminarTipoPlato(?)}");
                            procedimiento.setInt(1, ((TipoPlato)tblTipoPlato.getSelectionModel().getSelectedItem()).getCodigoTipoPlato());
                            procedimiento.execute();
                            listaTipoPlato.remove(tblTipoPlato.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            cargarDatos();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    if (respuesta == JOptionPane.NO_OPTION){
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
        desactivarControles();
    }
    
    public void editar(){
        switch (tipoOperacion){
            case NINGUNO:
                if(tblTipoPlato.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tipoOperacion = operaciones.ACTUALIZAR;
                    txtCodigoTipoPlato.setDisable(true);
                    JOptionPane.showMessageDialog(null, "Recuerda no dejar datos vacios");
                }else
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un dato");
            break;
            
            case ACTUALIZAR:
                actualizar();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnReporte.setDisable(false);
                btnEliminar.setDisable(false);
                btnNuevo.setDisable(false);
                tipoOperacion = operaciones.NINGUNO;
                txtCodigoTipoPlato.setDisable(false);
                limpiarControles();
                cargarDatos();
                desactivarControles();
            break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ActualizarTipoPlato(?,?)}");
            TipoPlato registro = (TipoPlato)tblTipoPlato.getSelectionModel().getSelectedItem();
            registro.setDescripcionTipo(txtDescripcionTipo.getText());
            procedimiento.setInt(1, registro.getCodigoTipoPlato());
            procedimiento.setString(2, registro.getDescripcionTipo());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void reporte(){
        switch (tipoOperacion){
            case ACTUALIZAR:
                desactivarControles();
                limpiarControles();
                txtCodigoTipoPlato.setDisable(false);
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
    
    public void ventanaPlatos(){
        escenarioPrincipal.ventanaPlatos();
    }
}