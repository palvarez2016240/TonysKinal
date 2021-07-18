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
import org.pedroalvarez.bean.Empleado;
import org.pedroalvarez.bean.TipoEmpleado;
import org.pedroalvarez.db.Conexion;
import org.pedroalvarez.system.Principal; 

public class EmpleadoController implements Initializable{
    private enum operaciones {NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private ObservableList<Empleado> listaEmpleado;
    private ObservableList<TipoEmpleado> listaTipoEmpleado;
    private boolean vacio = false;
    @FXML private TextField txtCodigoEmpleado;
    @FXML private TextField txtNumeroEmpleado;
    @FXML private TextField txtApellidosEmpleado;
    @FXML private TextField txtNombresEmpleado;
    @FXML private TextField txtDireccionEmpleado;
    @FXML private TextField txtTelefonoContacto;
    @FXML private TextField txtGradoCocinero;
    @FXML private ComboBox cmbCodigoTipoEmpleado;
    @FXML private TableView tblEmpleados;
    @FXML private TableColumn colCodigoEmpleado;
    @FXML private TableColumn colNumeroEmpleado;
    @FXML private TableColumn colApellidosEmpleado;
    @FXML private TableColumn colNombresEmpleado;
    @FXML private TableColumn colDireccionEmpleado;
    @FXML private TableColumn colTelefonoContacto;
    @FXML private TableColumn colGradoCocinero;
    @FXML private TableColumn colCodigoTipoEmpleado;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoTipoEmpleado.setItems(getTipoEmpleado());
    }
    
    public void cargarDatos(){
        tblEmpleados.setItems(getEmpleado());
        colCodigoEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado, Integer>("codigoEmpleado"));
        colNumeroEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado, Integer>("numeroEmpleado"));
        colApellidosEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado, String>("apellidosEmpleado"));
        colNombresEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado, String>("nombresEmpleado"));
        colDireccionEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado, String>("direccionEmpleado"));
        colTelefonoContacto.setCellValueFactory(new PropertyValueFactory<Empleado, String>("telefonoContacto"));
        colGradoCocinero.setCellValueFactory(new PropertyValueFactory<Empleado, String>("gradoCocinero"));
        colCodigoTipoEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado, Integer>("codigoTipoEmpleado"));
    }
    
    public ObservableList<Empleado> getEmpleado(){
        ArrayList<Empleado> lista = new ArrayList<Empleado>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarEmpleados");
            ResultSet resultado = procedimiento.executeQuery();
            while (resultado.next()){
                lista.add(new Empleado( resultado.getInt("codigoEmpleado"), resultado.getInt("numeroEmpleado"),
                                        resultado.getString("apellidosEmpleado"), resultado.getString("nombresEmpleado"),
                                        resultado.getString("direccionEmpleado"), resultado.getString("telefonoContacto"),
                                        resultado.getString("gradoCocinero"), resultado.getInt("codigoTipoEmpleado")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaEmpleado = FXCollections.observableList(lista);
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
                tblEmpleados.setDisable(true);
                txtCodigoEmpleado.setDisable(true);
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
                tblEmpleados.setDisable(false);
                txtCodigoEmpleado.setDisable(false);
                btnReporte.setDisable(false);
                cmbCodigoTipoEmpleado.setDisable(true);
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
        Empleado registro = new Empleado();
        registro.setApellidosEmpleado(txtApellidosEmpleado.getText());
        registro.setNombresEmpleado(txtNombresEmpleado.getText());
        registro.setDireccionEmpleado(txtDireccionEmpleado.getText());
        registro.setTelefonoContacto(txtTelefonoContacto.getText());
        registro.setGradoCocinero(txtGradoCocinero.getText());
        //registro.setCodigoTipoEmpleado(((TipoEmpleado)cmbCodigoTipoEmpleado.getSelectionModel().getSelectedItem()).getCodigoTipoEmpleado());
            if(registro.getApellidosEmpleado().equals("") || registro.getNombresEmpleado().equals("") || registro.getDireccionEmpleado().equals("") || 
               txtNumeroEmpleado.getText().equals("") || registro.getTelefonoContacto().equals("") || cmbCodigoTipoEmpleado.getSelectionModel().getSelectedItem() == null)
                vacio = true;
            else
                vacio = false;
        return vacio;
    }
    
    public void guardar(){
        Empleado registro = new Empleado();
        registro.setNumeroEmpleado(Integer.parseInt(txtNumeroEmpleado.getText()));
        registro.setApellidosEmpleado(txtApellidosEmpleado.getText());
        registro.setNombresEmpleado(txtNombresEmpleado.getText());
        registro.setDireccionEmpleado(txtDireccionEmpleado.getText());
        registro.setTelefonoContacto(txtTelefonoContacto.getText());
        registro.setGradoCocinero(txtGradoCocinero.getText());
        registro.setCodigoTipoEmpleado(((TipoEmpleado)cmbCodigoTipoEmpleado.getSelectionModel().getSelectedItem()).getCodigoTipoEmpleado());
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarEmpleados(?,?,?,?,?,?,?)}");
            procedimiento.setInt(1, registro.getNumeroEmpleado());
            procedimiento.setString(2, registro.getApellidosEmpleado());
            procedimiento.setString(3, registro.getNombresEmpleado());
            procedimiento.setString(4, registro.getDireccionEmpleado());
            procedimiento.setString(5, registro.getTelefonoContacto());
            procedimiento.setString(6, registro.getGradoCocinero());
            procedimiento.setInt(7, registro.getCodigoTipoEmpleado());
            procedimiento.execute();
            listaEmpleado.add(registro);
            cargarDatos();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void seleccionarElemento(){
        if(tblEmpleados.getSelectionModel().getSelectedItem() != null){
        cmbCodigoTipoEmpleado.setDisable(false);
        txtCodigoEmpleado.setText(String.valueOf(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getCodigoEmpleado()));
        txtNumeroEmpleado.setText(String.valueOf(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getNumeroEmpleado()));
        txtApellidosEmpleado.setText(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getApellidosEmpleado());
        txtNombresEmpleado.setText(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getNombresEmpleado());
        txtDireccionEmpleado.setText(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getDireccionEmpleado());
        txtTelefonoContacto.setText(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getTelefonoContacto());
        txtGradoCocinero.setText(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getGradoCocinero());
        cmbCodigoTipoEmpleado.getSelectionModel().select(buscarTipoEmpleado(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getCodigoTipoEmpleado()));
        }
    }
    
    public Empleado buscarEmpleado(int codigoEmpleado){
        Empleado resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarEmpleado(?)}");
            procedimiento.setInt(1, codigoEmpleado);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Empleado(
                        registro.getInt("codigoEmpleado"),
                        registro.getInt("numeroEmpleado"),
                        registro.getString("apelidosEmpleado"),
                        registro.getString("nombresEmpleado"),
                        registro.getString("direccionEmpleado"),
                        registro.getString("telefonoContacto"),
                        registro.getString("gradoCocinero"),
                        registro.getInt("codigoTipoEmpleado")
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
                tblEmpleados.setDisable(false);
                txtCodigoEmpleado.setDisable(false);
                cmbCodigoTipoEmpleado.setDisable(true);
                break;
                
            default:
                if(tblEmpleados.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Esta seguro de eliminar el registro?", "Eliminar Empleado", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_NO_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_eliminarEmpleados(?)}");
                            procedimiento.setInt(1, ((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getCodigoEmpleado());
                            procedimiento.execute();
                            listaEmpleado.remove(tblEmpleados.getSelectionModel().getSelectedIndex());
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
                else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                    cargarDatos();
                    limpiarControles();
                }
        }
        cmbCodigoTipoEmpleado.setDisable(true);
    }
    
    public void editar(){
        switch (tipoOperacion){
            case NINGUNO:
                if(tblEmpleados.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tipoOperacion = operaciones.ACTUALIZAR;
                    txtCodigoEmpleado.setDisable(true);
                    JOptionPane.showMessageDialog(null, "Recuerda no dejar datos vacios");
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
                txtCodigoEmpleado.setDisable(false);
                cargarDatos();
                limpiarControles();
                desactivarControles();
                cmbCodigoTipoEmpleado.setDisable(true);
            break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ActualizarEmpleados(?,?,?,?,?,?,?,?)}");
            Empleado registro = (Empleado)tblEmpleados.getSelectionModel().getSelectedItem();
            registro.setNumeroEmpleado(Integer.parseInt(txtNumeroEmpleado.getText()));
            registro.setApellidosEmpleado(txtApellidosEmpleado.getText());
            registro.setNombresEmpleado(txtNombresEmpleado.getText());
            registro.setDireccionEmpleado(txtDireccionEmpleado.getText());
            registro.setTelefonoContacto(txtTelefonoContacto.getText());
            registro.setGradoCocinero(txtGradoCocinero.getText());
            //registro.setCodigoTipoEmpleado(cmbCodigoTipoEmpleado.getSelectionModel());
            procedimiento.setInt(1, registro.getCodigoEmpleado());
            procedimiento.setInt(2, registro.getNumeroEmpleado());
            procedimiento.setString(3, registro.getApellidosEmpleado());
            procedimiento.setString(4, registro.getNombresEmpleado());
            procedimiento.setString(5, registro.getDireccionEmpleado());
            procedimiento.setString(6, registro.getTelefonoContacto());
            procedimiento.setString(7, registro.getGradoCocinero());
            procedimiento.setInt(8, registro.getCodigoTipoEmpleado());
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
                txtCodigoEmpleado.setDisable(false);
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnReporte.setText("Reporte");
                cargarDatos();
                tipoOperacion = operaciones.NINGUNO;
                btnEditar.setText("Editar");
                cmbCodigoTipoEmpleado.setDisable(true);
        }
    }
    
    public void desactivarControles(){
        txtCodigoEmpleado.setEditable(false);
        txtNumeroEmpleado.setEditable(false);
        txtApellidosEmpleado.setEditable(false);
        txtNombresEmpleado.setEditable(false);
        txtDireccionEmpleado.setEditable(false);
        txtTelefonoContacto.setEditable(false);
        txtGradoCocinero.setEditable(false);
        cmbCodigoTipoEmpleado.setDisable(false);
    }
    
    public void activarControles(){
        txtCodigoEmpleado.setEditable(false);
        txtNumeroEmpleado.setEditable(true);
        txtApellidosEmpleado.setEditable(true);
        txtNombresEmpleado.setEditable(true);
        txtDireccionEmpleado.setEditable(true);
        txtTelefonoContacto.setEditable(true);
        txtGradoCocinero.setEditable(true);
        cmbCodigoTipoEmpleado.setDisable(false);
    }
    
    public void limpiarControles(){
        txtCodigoEmpleado.setText("");
        txtNumeroEmpleado.setText("");
        txtApellidosEmpleado.setText("");
        txtNombresEmpleado.setText("");
        txtDireccionEmpleado.setText("");
        txtTelefonoContacto.setText("");
        txtGradoCocinero.setText("");
        cmbCodigoTipoEmpleado.getSelectionModel().clearSelection();
    }
    
    public void sinErrores(){
    }
    
    public Principal getEscenarioPrincipal(){
        return escenarioPrincipal;
    }
    
    public void setEscenarioPrincipal(Principal escenarioPrincipal){
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
    public void ventanaTipoEmpleado(){
        escenarioPrincipal.ventanaTipoEmpleado();
    }
}