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
import org.pedroalvarez.bean.Servicios;
import org.pedroalvarez.db.Conexion;
import org.pedroalvarez.report.GenerarReporte;
import org.pedroalvarez.system.Principal;

public class ServicioController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private ObservableList<Servicios> listaServicios;
    private ObservableList<Empresa> listaEmpresa;
    private DatePicker fecha;
    boolean vacio = false;
    @FXML private TextField txtCodigoServicio;
    @FXML private TextField txtTipoServicio;
    @FXML private TextField txtHoraServicio;
    @FXML private TextField txtLugarServicio;
    @FXML private TextField txtTelefonoContacto;
    @FXML private GridPane grpFechaServicio;
    @FXML private ComboBox cmbCodigoEmpresa;
    @FXML private TableView tblServicios;
    @FXML private TableColumn colCodigoServicio;
    @FXML private TableColumn colFechaServicio;
    @FXML private TableColumn colTipoServicio;
    @FXML private TableColumn colHoraServicio;
    @FXML private TableColumn colLugarServicio;
    @FXML private TableColumn colTelefonoContacto;
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
        grpFechaServicio.add(fecha, 1, 1);
        fecha.getStylesheets().add("/org/pedroalvarez/resource/DatePicker.css");
        cmbCodigoEmpresa.setItems(getEmpresa());
        fecha.setDisable(true);
    }
    
    public void cargarDatos(){
        tblServicios.setItems(getServicios());
        colCodigoServicio.setCellValueFactory(new PropertyValueFactory<Servicios, Integer>("codigoServicio"));
        colFechaServicio.setCellValueFactory(new PropertyValueFactory<Servicios, Date>("fechaServicio"));
        colTipoServicio.setCellValueFactory(new PropertyValueFactory<Servicios, String>("tipoServicio"));
        colHoraServicio.setCellValueFactory(new PropertyValueFactory<Servicios, String>("horaServicio"));
        colLugarServicio.setCellValueFactory(new PropertyValueFactory<Servicios, String>("lugarServicio"));
        colTelefonoContacto.setCellValueFactory(new PropertyValueFactory<Servicios, String>("telefonoContacto"));
        colCodigoEmpresa.setCellValueFactory(new PropertyValueFactory<Servicios, Integer>("codigoEmpresa"));
    }
    
    public void seleccionarElemento(){
        if(tblServicios.getSelectionModel().getSelectedItem() != null){
        txtCodigoServicio.setText(String.valueOf(((Servicios)tblServicios.getSelectionModel().getSelectedItem()).getCodigoServicio()));
        fecha.selectedDateProperty().set(((Servicios)tblServicios.getSelectionModel().getSelectedItem()).getFechaServicio());
        txtTipoServicio.setText(((Servicios)tblServicios.getSelectionModel().getSelectedItem()).getTipoServicio());
        txtHoraServicio.setText(((Servicios)tblServicios.getSelectionModel().getSelectedItem()).getHoraServicio());
        txtLugarServicio.setText(((Servicios)tblServicios.getSelectionModel().getSelectedItem()).getLugarServicio());
        txtTelefonoContacto.setText(((Servicios)tblServicios.getSelectionModel().getSelectedItem()).getTelefonoContacto());
        cmbCodigoEmpresa.getSelectionModel().select(buscarEmpresa(((Servicios)tblServicios.getSelectionModel().getSelectedItem()).getCodigoEmpresa()));
        fecha.setDisable(false);
        cmbCodigoEmpresa.setDisable(false);
        }
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
                                         registro.getInt("codigoEmrpesa")
               );
           }
       }catch(Exception e){
           e.printStackTrace();
       }
       return resultado;
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
    
    public void desactivarControles(){
        txtCodigoServicio.setEditable(false);
        txtTipoServicio.setEditable(false);
        txtHoraServicio.setEditable(false);
        txtLugarServicio.setEditable(false);
        txtTelefonoContacto.setEditable(false);
        fecha.setDisable(true);
        cmbCodigoEmpresa.setDisable(false);
    }
    
    public void activarControles(){
        txtCodigoServicio.setEditable(false);
        txtTipoServicio.setEditable(true);
        txtHoraServicio.setEditable(true);
        txtLugarServicio.setEditable(true);
        txtTelefonoContacto.setEditable(true);
        fecha.setDisable(false);
        cmbCodigoEmpresa.setDisable(false);
    }
    
    public void limpiarControles(){
        txtCodigoServicio.setText("");
        txtTipoServicio.setText("");
        txtHoraServicio.setText("");
        txtLugarServicio.setText("");
        txtTelefonoContacto.setText("");
        cmbCodigoEmpresa.getSelectionModel().clearSelection();
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
                tblServicios.setDisable(true);
                txtCodigoServicio.setDisable(true);
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
                    tblServicios.setDisable(false);
                    txtCodigoServicio.setDisable(false);
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
        Servicios registro = new Servicios();
        //registro.setFechaServicio(fecha.getSelectedDate());
        registro.setTipoServicio(txtTipoServicio.getText());
        registro.setHoraServicio(txtHoraServicio.getText());
        registro.setLugarServicio(txtLugarServicio.getText());
        registro.setTelefonoContacto(txtTelefonoContacto.getText());
        //registro.setCodigoEmpresa(((Empresa)cmbCodigoEmpresa.getSelectionModel().getSelectedItem()).getCodigoEmpresa());
            if(registro.getTipoServicio().equals("") || registro.getHoraServicio().equals("") || registro.getLugarServicio().equals("") || 
               registro.getTelefonoContacto().equals("") || cmbCodigoEmpresa.getSelectionModel().getSelectedItem() == null || fecha.getSelectedDate() == null)
                vacio = true;
            else
                vacio = false;
        return vacio;
    }
    
    public void guardar(){
        Servicios registro = new Servicios();
        try{
        registro.setFechaServicio(fecha.getSelectedDate());
        registro.setTipoServicio(txtTipoServicio.getText());
        registro.setHoraServicio(txtHoraServicio.getText());
        registro.setLugarServicio(txtLugarServicio.getText());
        registro.setTelefonoContacto(txtTelefonoContacto.getText());
        registro.setCodigoEmpresa(((Empresa)cmbCodigoEmpresa.getSelectionModel().getSelectedItem()).getCodigoEmpresa());
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarServicios(?,?,?,?,?,?)}");
            procedimiento.setDate(1, new java.sql.Date(registro.getFechaServicio().getTime()));
            procedimiento.setString(2, registro.getTipoServicio());
            procedimiento.setString(3, registro.getHoraServicio());
            procedimiento.setString(4, registro.getLugarServicio());
            procedimiento.setString(5, registro.getTelefonoContacto());
            procedimiento.setInt(6, registro.getCodigoEmpresa());
            procedimiento.execute();
            listaServicios.add(registro);
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
                tblServicios.setDisable(false);
                txtCodigoServicio.setDisable(false);
                cmbCodigoEmpresa.setDisable(true);
                break;
                
            default:
                if(tblServicios.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Esta seguro de eliminar el registro?", "Eliminar Servicio", JOptionPane.YES_NO_OPTION,  JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_NO_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_eliminarServicios(?)}");
                            procedimiento.setInt(1, ((Servicios)tblServicios.getSelectionModel().getSelectedItem()).getCodigoServicio());
                            procedimiento.execute();
                            listaServicios.remove(tblServicios.getSelectionModel().getSelectedIndex());
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
        cmbCodigoEmpresa.setDisable(true);
        fecha.setDisable(true);
    }
    
    public void editar(){
        switch(tipoOperacion){
            case NINGUNO:
                if(tblServicios.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tipoOperacion = operaciones.ACTUALIZAR;
                    txtCodigoServicio.setDisable(true);
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
                txtCodigoServicio.setDisable(false);
                cargarDatos();
                limpiarControles();
                desactivarControles();
                cmbCodigoEmpresa.setDisable(true);
            break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ActualizarServicios(?,?,?,?,?,?,?)}");
            Servicios registro = (Servicios)tblServicios.getSelectionModel().getSelectedItem();
            registro.setFechaServicio(fecha.getSelectedDate());
            registro.setTipoServicio(txtTipoServicio.getText());
            registro.setHoraServicio(txtHoraServicio.getText());
            registro.setLugarServicio(txtLugarServicio.getText());
            registro.setTelefonoContacto(txtTelefonoContacto.getText());
            procedimiento.setInt(1, registro.getCodigoServicio());
            procedimiento.setDate(2, new java.sql.Date(registro.getFechaServicio().getTime()));
            procedimiento.setString(3, registro.getTipoServicio());
            procedimiento.setString(4, registro.getHoraServicio());
            procedimiento.setString(5, registro.getLugarServicio());
            procedimiento.setString(6, registro.getTelefonoContacto());
            procedimiento.setInt(7, registro.getCodigoEmpresa());
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
                txtCodigoServicio.setDisable(false);
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnReporte.setText("Reporte");
                cargarDatos();
                tipoOperacion = operaciones.NINGUNO;
                btnEditar.setText("Editar");
                cmbCodigoEmpresa.setDisable(true);
            break;
            
            case NINGUNO:
               if(tblServicios.getSelectionModel().getSelectedItem() != null){
                   imprimirReporte();
               }
               else{ 
                   JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                   tipoOperacion = operaciones.NINGUNO;}
            break;
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        int codServicio = Integer.valueOf(txtCodigoServicio.getText());
        parametros.put("codServicio", codServicio);
        GenerarReporte.mostrarReporte("ReporteServicios.jasper", "Reporte de Servicios", parametros);
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