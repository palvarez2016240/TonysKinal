package org.pedroalvarez.controller;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.pedroalvarez.bean.Empresa;
import org.pedroalvarez.bean.Presupuesto;
import org.pedroalvarez.db.Conexion;
import org.pedroalvarez.report.GenerarReporte;
import org.pedroalvarez.system.Principal;

public class PresupuestoController implements Initializable{
    private enum operaciones {NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private ObservableList<Empresa> listaEmpresa;
    private ObservableList<Presupuesto> listaPresupuesto;
    private DatePicker fecha;
    boolean vacio = false;
    @FXML private TextField txtCodigoPresupuesto;
    @FXML private GridPane grpFechaSolicitud;
    @FXML private TextField txtCantidadPresupuesto;
    @FXML private ComboBox cmbCodigoEmpresa;
    @FXML private TableView tblPresupuesto;
    @FXML private TableColumn colCodigoPresupuesto;
    @FXML private TableColumn colFechaSolicitud;
    @FXML private TableColumn colCantidadPresupuesto;
    @FXML private TableColumn colCodigoEmpresa;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        fecha = new DatePicker(Locale.ENGLISH);
        fecha.setDateFormat(new SimpleDateFormat("yyy-MM-dd"));
        fecha.getCalendarView().todayButtonTextProperty().set("Today");
        fecha.getCalendarView().setShowWeeks(false);
        fecha.getStylesheets().add("/org/pedroalvarez/resource/DatePicker.css");
        grpFechaSolicitud.add(fecha, 1,1);
        cmbCodigoEmpresa.setItems(getEmpresa());
        fecha.setDisable(true);
    }
    
    public void cargarDatos(){
        tblPresupuesto.setItems(getPresupuesto());
        colCodigoPresupuesto.setCellValueFactory(new PropertyValueFactory<Presupuesto, Integer>("codigoPresupuesto"));
        colFechaSolicitud.setCellValueFactory(new PropertyValueFactory<Presupuesto, Date>("fechaSolicitud"));
        colCantidadPresupuesto.setCellValueFactory(new PropertyValueFactory<Presupuesto, Double>("cantidadPresupuesto"));
        colCodigoEmpresa.setCellValueFactory(new PropertyValueFactory<Presupuesto, Integer>("codigoEmpresa"));
    }
    
    public ObservableList<Presupuesto> getPresupuesto(){
        ArrayList<Presupuesto> lista = new ArrayList<Presupuesto>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarPresupuesto");
            ResultSet resultado = procedimiento.executeQuery();
            while (resultado.next()){
                lista.add(new Presupuesto(resultado.getInt("codigoPresupuesto"),
                                          resultado.getDate("fechaSolicitud"),
    /* Aqui */                            resultado.getFloat("cantidadPresupuesto"), 
                                          resultado.getInt("codigoEmpresa")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaPresupuesto = FXCollections.observableArrayList(lista);
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
    
    public Empresa buscarEmpresa(int codigoEmpresa){
        Empresa resultado = null;
            try{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarEmpresas(?)}");
                procedimiento.setInt(1, codigoEmpresa);
                ResultSet registro = procedimiento.executeQuery();
                while(registro.next()){
                    resultado = new Empresa(registro.getInt("codigoEmpresa"),
                                            registro.getString("nombreEmpresa"),
                                            registro.getString("direccion"),
                                            registro.getString("telefono"));
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
                tblPresupuesto.setDisable(true);
                txtCodigoPresupuesto.setDisable(true);
                cargarDatos();
                cmbCodigoEmpresa.setDisable(false);
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
                    tblPresupuesto.setDisable(false);
                    txtCodigoPresupuesto.setDisable(false);
                    btnReporte.setDisable(false);
                    cmbCodigoEmpresa.setDisable(true);
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
        Presupuesto registro = new Presupuesto();
        //registro.setFechaSolicitud(fecha.getSelectedDate());
        //registro.setCodigoEmpresa(((Empresa)cmbCodigoEmpresa.getSelectionModel().getSelectedItem()).getCodigoEmpresa());
        String cantidad = String.valueOf(txtCantidadPresupuesto.getText());
        if(cantidad.equals("") || cmbCodigoEmpresa.getSelectionModel().getSelectedItem() == null || fecha.getSelectedDate() == null)
                vacio = true;
            else
                vacio = false;
        return vacio;
    }
    
    public void guardar(){
        Presupuesto registro = new Presupuesto();
        registro.setFechaSolicitud(fecha.getSelectedDate());
        registro.setCantidadPresupuesto(Double.parseDouble(txtCantidadPresupuesto.getText()));
        registro.setCodigoEmpresa(((Empresa)cmbCodigoEmpresa.getSelectionModel().getSelectedItem()).getCodigoEmpresa());
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarPresupuesto(?,?,?)}");
            procedimiento.setDate(1, new java.sql.Date(registro.getFechaSolicitud().getTime()));
            procedimiento.setDouble(2, registro.getCantidadPresupuesto());
            procedimiento.setInt(3, registro.getCodigoEmpresa());
            procedimiento.execute();
            listaPresupuesto.add(registro);
            cargarDatos();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public void seleccionarElemento(){
        if(tblPresupuesto.getSelectionModel().getSelectedItem() != null){
        fecha.setDisable(false);
        cmbCodigoEmpresa.setDisable(false);
        txtCodigoPresupuesto.setText(String.valueOf(((Presupuesto)tblPresupuesto.getSelectionModel().getSelectedItem()).getCodigoPresupuesto()));
        fecha.selectedDateProperty().set(((Presupuesto)tblPresupuesto.getSelectionModel().getSelectedItem()).getFechaSolicitud());
        txtCantidadPresupuesto.setText(String.valueOf(((Presupuesto)tblPresupuesto.getSelectionModel().getSelectedItem()).getCantidadPresupuesto()));
        cmbCodigoEmpresa.getSelectionModel().select(buscarEmpresa(((Presupuesto)tblPresupuesto.getSelectionModel().getSelectedItem()).getCodigoEmpresa()));
        }
    }
    
    public Presupuesto buscarPresupuesto(int codigoPresupuesto){
        Presupuesto resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarPresupuesto(?)}");
            procedimiento.setInt(1, codigoPresupuesto);
            ResultSet registro = procedimiento.executeQuery();
            while (registro.next()){
                resultado = new Presupuesto(
                    registro.getInt("CodigoPresupuesto"),
                    registro.getDate("fechaSolicitud"),
    /*Aqui*/        registro.getFloat("cantidadPresupuesto"),
                    registro.getInt("codigoEmpresa")
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
                tblPresupuesto.setDisable(false);
                txtCodigoPresupuesto.setDisable(false);
                cmbCodigoEmpresa.setDisable(true);
                break;
            
        default:
            if(tblPresupuesto.getSelectionModel().getSelectedItem() != null){
                int respuesta = JOptionPane.showConfirmDialog(null, "Estas seguro de eliminar el registro", "Eliminar Presupuesto", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(respuesta == JOptionPane.YES_NO_OPTION){
                    try{
                        PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_eliminarPresupuesto(?)}");
                           procedimiento.setInt(1, ((Presupuesto)tblPresupuesto.getSelectionModel().getSelectedItem()).getCodigoPresupuesto());
                           procedimiento.execute();
                           listaPresupuesto.remove(tblPresupuesto.getSelectionModel().getSelectedIndex());
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
        fecha.setDisable(true);
        cmbCodigoEmpresa.setDisable(true);
    }
    
    public void editar(){
        switch (tipoOperacion){
            case NINGUNO:
                if(tblPresupuesto.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tipoOperacion = operaciones.ACTUALIZAR;
                    txtCodigoPresupuesto.setDisable(true);
                    cmbCodigoEmpresa.setDisable(true);
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
                txtCodigoPresupuesto.setDisable(false);
                cargarDatos();
                limpiarControles();
                desactivarControles();
                cmbCodigoEmpresa.setDisable(true);
                break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ActualizarPresupuesto(?,?,?,?)}");
            Presupuesto registro = (Presupuesto)tblPresupuesto.getSelectionModel().getSelectedItem();
            registro.setFechaSolicitud(fecha.getSelectedDate());
            registro.setCantidadPresupuesto(Double.parseDouble(txtCantidadPresupuesto.getText()));
            procedimiento.setInt(1, registro.getCodigoPresupuesto());
            procedimiento.setDate(2, new java.sql.Date(registro.getFechaSolicitud().getTime()));
            procedimiento.setDouble(3, registro.getCantidadPresupuesto());
            procedimiento.setInt(4, registro.getCodigoEmpresa());
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
                txtCodigoPresupuesto.setDisable(false);
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnReporte.setText("Reporte");
                btnEditar.setText("Editar");
                cmbCodigoEmpresa.setDisable(true);
                cargarDatos();
                tipoOperacion = operaciones.NINGUNO;
            break;
            
            case NINGUNO:
                if(tblPresupuesto.getSelectionModel().getSelectedItem() != null)
                    imprimirReporte();
                else
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                    tipoOperacion = operaciones.NINGUNO;
            break;
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        int codEmpresa = Integer.valueOf(((Empresa)cmbCodigoEmpresa.getSelectionModel().getSelectedItem()).getCodigoEmpresa());
        parametros.put("codEmpresa", codEmpresa);
        GenerarReporte.mostrarReporte("ReportePresupuesto.jasper", "Reporte de Presupuesto", parametros);
    }
    
    public void desactivarControles(){
        txtCodigoPresupuesto.setEditable(false);
        fecha.setDisable(true);
        txtCantidadPresupuesto.setEditable(false);
        cmbCodigoEmpresa.setDisable(false);
    }
    
    public void activarControles(){
        txtCodigoPresupuesto.setEditable(false);
        fecha.setDisable(false);
        txtCantidadPresupuesto.setEditable(true);
        cmbCodigoEmpresa.setEditable(false);
    }
    
    public void limpiarControles(){
        txtCodigoPresupuesto.setText("");
        txtCantidadPresupuesto.setText("");
        cmbCodigoEmpresa.getSelectionModel().clearSelection();
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
    
    public void ventanaEmpresas(){
        escenarioPrincipal.ventanaEmpresas();
    }
}