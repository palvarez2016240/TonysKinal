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
import org.pedroalvarez.bean.TipoEmpleado;
import org.pedroalvarez.db.Conexion;
import org.pedroalvarez.system.Principal;

public class TipoEmpleadoController implements Initializable{
    private enum operaciones {NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private ObservableList<TipoEmpleado> listaTipoEmpleado;
    private boolean vacio = false;
    @FXML private TextField txtCodigoTipoEmpleado;
    @FXML private TextField txtDescripcion;
    @FXML private TableView tblTipoEmpleado;
    @FXML private TableColumn colCodigoTipoEmpleado;
    @FXML private TableColumn colDescripcion;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    }
    
    public void cargarDatos(){
        tblTipoEmpleado.setItems(getTipoEmpleado());
        colCodigoTipoEmpleado.setCellValueFactory(new PropertyValueFactory<TipoEmpleado, Integer>("codigoTipoEmpleado"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<TipoEmpleado, String>("Descripcion"));
    }
    
    public ObservableList<TipoEmpleado> getTipoEmpleado(){
        ArrayList<TipoEmpleado> lista = new ArrayList<TipoEmpleado>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarTipoEmpleado");
            ResultSet resultado = procedimiento.executeQuery();
                while (resultado.next()){
                    lista.add(new TipoEmpleado( resultado.getInt("codigoTipoEmpleado"),
                                                resultado.getString("descripcion")));
                }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaTipoEmpleado = FXCollections.observableArrayList(lista);
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
                tblTipoEmpleado.setDisable(true);
                txtCodigoTipoEmpleado.setDisable(true);
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
                    tblTipoEmpleado.setDisable(false);
                    txtCodigoTipoEmpleado.setDisable(false);
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
        TipoEmpleado registro = new TipoEmpleado();
        registro.setDescripcion(txtDescripcion.getText());
            if(registro.getDescripcion().equals(""))
                vacio = true;
            else
                vacio = false;
        return vacio;
    }
    
    public void guardar(){
        TipoEmpleado registro = new TipoEmpleado();
        registro.setDescripcion(txtDescripcion.getText());
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarTipoEmpleado(?)}");
            procedimiento.setString(1, registro.getDescripcion());
            procedimiento.execute();
            listaTipoEmpleado.add(registro);
            cargarDatos();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void seleccionarElemento(){
        if(tblTipoEmpleado.getSelectionModel().getSelectedItem() != null){
        txtCodigoTipoEmpleado.setText(String.valueOf(((TipoEmpleado)tblTipoEmpleado.getSelectionModel().getSelectedItem()).getCodigoTipoEmpleado()));
        txtDescripcion.setText(((TipoEmpleado)tblTipoEmpleado.getSelectionModel().getSelectedItem()).getDescripcion());
        }
    }
    
    public TipoEmpleado buscarTipoEmpleado(int codigoTipoEmpleado){
        TipoEmpleado resultado = null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarTipoEmpleado(?)}");
            procedimiento.setInt(1, codigoTipoEmpleado);
            ResultSet registro = procedimiento.executeQuery();
            while (registro.next()){
                resultado = new TipoEmpleado(
                    registro.getInt("codigoTipoEmpleado"),
                    registro.getString("descripcion")
                );
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
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
                tblTipoEmpleado.setDisable(false);
                txtCodigoTipoEmpleado.setDisable(false);
                break;
            
            default:
                if (tblTipoEmpleado.getSelectionModel().getSelectedItem()!= null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Esta seguro de eliminar el registro?", "Eliminar Tipo Empleado", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_eliminarTipoEmpleado(?)}");
                            procedimiento.setInt(1, ((TipoEmpleado)tblTipoEmpleado.getSelectionModel().getSelectedItem()).getCodigoTipoEmpleado());
                            procedimiento.execute();
                            listaTipoEmpleado.remove(tblTipoEmpleado.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            cargarDatos();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }if (respuesta == JOptionPane.NO_OPTION){
                       cargarDatos();
                       limpiarControles();
                    }
                }       
                    else {
                        JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                        cargarDatos();
                        limpiarControles();
                }
        }
    }       
        
    
    public void editar(){
        switch(tipoOperacion){
            case NINGUNO:
                if(tblTipoEmpleado.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tipoOperacion = operaciones.ACTUALIZAR;
                    txtCodigoTipoEmpleado.setDisable(true);
                    JOptionPane.showMessageDialog(null, "Recuerda no dejar datos vacios");
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemneto");
                }
                break;
            case ACTUALIZAR:
                actualizar();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnReporte.setDisable(false);
                btnEliminar.setDisable(false);
                btnNuevo.setDisable(false);
                tipoOperacion = operaciones.NINGUNO;
                txtCodigoTipoEmpleado.setDisable(false);
                limpiarControles();
                cargarDatos();
                desactivarControles();
                break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ActualizarTipoEmpleado(?,?)}");
            TipoEmpleado registro = (TipoEmpleado)tblTipoEmpleado.getSelectionModel().getSelectedItem();
            registro.setDescripcion(txtDescripcion.getText());
            procedimiento.setInt(1, registro.getCodigoTipoEmpleado());
            procedimiento.setString(2, registro.getDescripcion());
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
                txtCodigoTipoEmpleado.setDisable(false);
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnReporte.setText("Reporte");
                btnEditar.setText("Editar");
                cargarDatos();
                tipoOperacion = operaciones.NINGUNO;
        }
    }
    
    public void desactivarControles(){
        txtCodigoTipoEmpleado.setEditable(false);
        txtDescripcion.setEditable(false);
    }
    
    public void activarControles(){
        txtCodigoTipoEmpleado.setEditable(false);
        txtDescripcion.setEditable(true);
    }
    
    public void limpiarControles(){
        txtCodigoTipoEmpleado.setText("");
        txtDescripcion.setText("");
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

    public void ventanaEmpleado(){
        escenarioPrincipal.ventanaEmpleado();
    }
}