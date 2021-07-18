package org.pedroalvarez.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
import org.pedroalvarez.bean.Empresa;
import org.pedroalvarez.db.Conexion;
import org.pedroalvarez.report.GenerarReporte;
import org.pedroalvarez.system.Principal;

public class EmpresaController implements Initializable{
    private enum operaciones {NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private ObservableList<Empresa> listaEmpresa;
    boolean vacio = false;
    @FXML private TextField txtCodigoEmpresa; 
    @FXML private TextField txtNombreEmpresa;
    @FXML private TextField txtDireccionEmpresa;
    @FXML private TextField txtTelefonoEmpresa;
    @FXML private TableView tblEmpresas;
    @FXML private TableColumn colCodigoEmpresa;
    @FXML private TableColumn colNombreEmpresa;
    @FXML private TableColumn colDireccionEmpresa;
    @FXML private TableColumn colTelefono;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    }
    
    public void cargarDatos(){
        tblEmpresas.setItems(getEmpresa());
        colCodigoEmpresa.setCellValueFactory(new PropertyValueFactory<Empresa, Integer>("codigoEmpresa"));
        colNombreEmpresa.setCellValueFactory(new PropertyValueFactory<Empresa, String>("nombreEmpresa"));
        colDireccionEmpresa.setCellValueFactory(new PropertyValueFactory <Empresa, String>("direccion"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<Empresa, String>("telefono"));
    }
    
    public ObservableList<Empresa> getEmpresa(){
        ArrayList<Empresa> lista = new ArrayList<Empresa>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarEmpresas");
            ResultSet resultado  = procedimiento.executeQuery();
            while (resultado.next()){
                lista.add(new Empresa(  resultado.getInt("codigoEmpresa"), 
                                        resultado.getString("nombreEmpresa"),
                                        resultado.getString("Direccion"),
                                        resultado.getString("Telefono")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaEmpresa = FXCollections.observableArrayList(lista);
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
                tblEmpresas.setDisable(true);
                txtCodigoEmpresa.setDisable(true);
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
                    tblEmpresas.setDisable(false);
                    txtCodigoEmpresa.setDisable(false);
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
        Empresa registro = new Empresa();
        registro.setNombreEmpresa(txtNombreEmpresa.getText());
        registro.setDireccion(txtDireccionEmpresa.getText());
        registro.setTelefono(txtTelefonoEmpresa.getText());
            if(registro.getNombreEmpresa().equals("") || registro.getDireccion().equals("") || registro.getTelefono().equals(""))
                vacio = true;
            else
                vacio = false;
        return vacio;
    }
    
    public void guardar(){
        Empresa registro = new Empresa();
        //registro.setCodigoEmpresa(Integer.parseInt(txtCodigoEmpresa.getText()));
        registro.setNombreEmpresa(txtNombreEmpresa.getText());
        registro.setDireccion(txtDireccionEmpresa.getText());
        registro.setTelefono(txtTelefonoEmpresa.getText());
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarEmpresas(?,?,?)}");
            procedimiento.setString(1, registro.getNombreEmpresa());
            procedimiento.setString(2, registro.getDireccion());
            procedimiento.setString(3, registro.getTelefono());
            procedimiento.execute();
            listaEmpresa.add(registro);
            cargarDatos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void seleccionarElemento(){
        if(tblEmpresas.getSelectionModel().getSelectedItem() != null){
        txtCodigoEmpresa.setText(String.valueOf(((Empresa)tblEmpresas.getSelectionModel().getSelectedItem()).getCodigoEmpresa()));
        txtNombreEmpresa.setText(((Empresa)tblEmpresas.getSelectionModel().getSelectedItem()).getNombreEmpresa());
        txtDireccionEmpresa.setText(((Empresa)tblEmpresas.getSelectionModel().getSelectedItem()).getDireccion());
        txtTelefonoEmpresa.setText(((Empresa)tblEmpresas.getSelectionModel().getSelectedItem()).getTelefono());
        }
    }
    
    public Empresa buscarEmpresa(int codigoEmpresa){
        Empresa resultado = null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarEmpresas(?)}");
            procedimiento.setInt(1, codigoEmpresa);
            ResultSet registro = procedimiento.executeQuery();
            while (registro.next()){
                resultado = new Empresa(
                        registro.getInt("codigoEmpresa"),
                        registro.getString("nombreEmpresa"),
                        registro.getString("direccion"),
                        registro.getString("telefono")
                );
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
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
                tblEmpresas.setDisable(false);
                txtCodigoEmpresa.setDisable(false);
                break;
            
           default: 
               if(tblEmpresas.getSelectionModel().getSelectedItem() !=  null){
                   int respuesta = JOptionPane.showConfirmDialog(null, "¿Esta seguro de eliminar el registro?", "Eliminar Empresa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                   if (respuesta == JOptionPane.YES_OPTION){
                       try{
                           PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_eliminarEmpresas(?)}");
                           procedimiento.setInt(1, ((Empresa)tblEmpresas.getSelectionModel().getSelectedItem()).getCodigoEmpresa());
                           procedimiento.execute();
                           listaEmpresa.remove(tblEmpresas.getSelectionModel().getSelectedIndex());
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
               else/*(tblEmpresas.getSelectionModel().getSelectedItem() ==  null ^ eliminado != 1)*/{
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
                if(tblEmpresas.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tipoOperacion = operaciones.ACTUALIZAR;
                    txtCodigoEmpresa.setDisable(true);
                    JOptionPane.showMessageDialog(null, "Recuerda no dejar datos vacios");
                }else{
                    JOptionPane.showMessageDialog(null, "Debe selecionar un elemento");
                }
                break;
            case ACTUALIZAR:
                actualizar();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tipoOperacion = operaciones.NINGUNO;
                txtCodigoEmpresa.setDisable(false);
                cargarDatos();
                limpiarControles();
                desactivarControles();
                break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ActualizarEmpresas(?,?,?,?)}");
            Empresa registro = (Empresa)tblEmpresas.getSelectionModel().getSelectedItem();
            registro.setNombreEmpresa(txtNombreEmpresa.getText());
            registro.setDireccion(txtDireccionEmpresa.getText());
            registro.setTelefono(txtTelefonoEmpresa.getText());
            procedimiento.setInt(1, registro.getCodigoEmpresa());
            procedimiento.setString(2, registro.getNombreEmpresa());
            procedimiento.setString(3, registro.getDireccion());
            procedimiento.setString(4, registro.getTelefono());
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
                txtCodigoEmpresa.setDisable(false);
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnReporte.setText("Reporte");
                cargarDatos();
                tipoOperacion = operaciones.NINGUNO;
                btnEditar.setText("Editar");
            break;
                
            case NINGUNO:
                imprimirReporte();
            break;
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("codigoEmpresa", null);
        GenerarReporte.mostrarReporte("ReporteEmpresas.jasper", "Reporte de Empresas", parametros);
    }
    
    public void desactivarControles(){
        txtCodigoEmpresa.setEditable(false);
        txtNombreEmpresa.setEditable(false);
        txtDireccionEmpresa.setEditable(false);
        txtTelefonoEmpresa.setEditable(false);
    }
    
    public void activarControles(){
        txtCodigoEmpresa.setEditable(false);
        txtNombreEmpresa.setEditable(true);
        txtDireccionEmpresa.setEditable(true);
        txtTelefonoEmpresa.setEditable(true);
    }
    
    public void limpiarControles(){
        txtCodigoEmpresa.setText("");
        txtNombreEmpresa.setText("");
        txtDireccionEmpresa.setText("");
        txtTelefonoEmpresa.setText("");
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
    
    public void ventanaPresupuesto(){
        escenarioPrincipal.ventanaPresupuesto();
    }
    
    public void ventanaServicios(){
        escenarioPrincipal.ventanaServicios();
    }
}