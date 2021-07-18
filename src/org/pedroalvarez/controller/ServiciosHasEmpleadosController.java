package org.pedroalvarez.controller;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
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
import org.pedroalvarez.bean.Empleado;
import org.pedroalvarez.bean.Servicios;
import org.pedroalvarez.bean.ServiciosHasEmpleados;
import org.pedroalvarez.db.Conexion;
import org.pedroalvarez.system.Principal;

public class ServiciosHasEmpleadosController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private ObservableList<ServiciosHasEmpleados> listaServiciosHasEmpleados;
    private ObservableList<Servicios> listaServicios;
    private ObservableList<Empleado> listaEmpleado;
    private DatePicker fecha;
    boolean vacio = false;
    @FXML private ComboBox cmbCodigoServicio;
    @FXML private ComboBox cmbCodigoEmpleado;
    @FXML private GridPane grpFechaEvento;
    @FXML private TextField txtHoraEvento;
    @FXML private TextField txtLugarEvento;
    @FXML private TableView tblServiciosHasEmpleados;
    @FXML private TableColumn colCodigoServicio;
    @FXML private TableColumn colCodigoEmpleado;
    @FXML private TableColumn colFechaEvento;
    @FXML private TableColumn colHoraEvento;
    @FXML private TableColumn colLugarEvento;
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
        grpFechaEvento.add(fecha, 3, 0);
        fecha.getStylesheets().add("/org/pedroalvarez/resource/DatePicker.css");
        cmbCodigoServicio.setItems(getServicios());
        cmbCodigoEmpleado.setItems(getEmpleado());
        fecha.setDisable(true);
    }
    
    public void cargarDatos(){
        tblServiciosHasEmpleados.setItems(getServiciosHasEmpleados());
        colCodigoServicio.setCellValueFactory(new PropertyValueFactory<ServiciosHasEmpleados, Integer>("codigoServicio"));
        colCodigoEmpleado.setCellValueFactory(new PropertyValueFactory<ServiciosHasEmpleados, Integer>("codigoEmpleado"));
        colFechaEvento.setCellValueFactory(new PropertyValueFactory<ServiciosHasEmpleados, Date>("fechaEvento"));
        colHoraEvento.setCellValueFactory(new PropertyValueFactory<ServiciosHasEmpleados, String>("horaEvento"));
        colLugarEvento.setCellValueFactory(new PropertyValueFactory<ServiciosHasEmpleados, String>("lugarEvento"));
    }
    
    public void seleccionarElemento(){
        if(tblServiciosHasEmpleados.getSelectionModel().getSelectedItem() != null){
        txtHoraEvento.setDisable(false);
        txtLugarEvento.setDisable(false);
        cmbCodigoServicio.getSelectionModel().select(buscarServicio(((ServiciosHasEmpleados)tblServiciosHasEmpleados.getSelectionModel().getSelectedItem()).getCodigoServicio()));
        cmbCodigoEmpleado.getSelectionModel().select(buscarEmpleado(((ServiciosHasEmpleados)tblServiciosHasEmpleados.getSelectionModel().getSelectedItem()).getCodigoEmpleado()));
        fecha.selectedDateProperty().set(((ServiciosHasEmpleados)tblServiciosHasEmpleados.getSelectionModel().getSelectedItem()).getFechaEvento());
        txtHoraEvento.setText(((ServiciosHasEmpleados)tblServiciosHasEmpleados.getSelectionModel().getSelectedItem()).getHoraEvento());
        txtLugarEvento.setText(((ServiciosHasEmpleados)tblServiciosHasEmpleados.getSelectionModel().getSelectedItem()).getLugarEvento());
        fecha.setDisable(false);
        cmbCodigoServicio.setDisable(false);
        cmbCodigoEmpleado.setDisable(false);
        }
    }
    
    public ObservableList<ServiciosHasEmpleados> getServiciosHasEmpleados(){
        ArrayList<ServiciosHasEmpleados> lista = new ArrayList<ServiciosHasEmpleados>();
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarServicios_Has_Empleados}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new ServiciosHasEmpleados(resultado.getInt("codigoServicio"),
                                                    resultado.getInt("codigoEmpleado"),
                                                    resultado.getDate("fechaEvento"),
                                                    resultado.getString("horaEvento"),
                                                    resultado.getString("lugarEvento")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaServiciosHasEmpleados = FXCollections.observableArrayList(lista);
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
    
    public ServiciosHasEmpleados buscarServiciosHasEmpleados(int codigoServicio, int codigoEmpleado){
        ServiciosHasEmpleados resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarServicios_Has_Empleados(?,?)}");
            procedimiento.setInt(1, codigoServicio);
            procedimiento.setInt(2, codigoEmpleado);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new ServiciosHasEmpleados(registro.getInt("codigoServicio"),
                                                     registro.getInt("codigoEmpleado"),
                                                     registro.getDate("fechaEvento"),
                                                     registro.getString("horaEvento"),
                                                     registro.getString("lugarEvento"));
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
    
    public Empleado buscarEmpleado(int codigoEmpleado){
        Empleado resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarEmpleados(?)}");
            procedimiento.setInt(1, codigoEmpleado);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Empleado(
                        registro.getInt("codigoEmpleado"),
                        registro.getInt("numeroEmpleado"),
                        registro.getString("apellidosEmpleado"),
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
                tblServiciosHasEmpleados.setDisable(true);
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
                    tblServiciosHasEmpleados.setDisable(false);
                    btnReporte.setDisable(false);
                    cmbCodigoServicio.setDisable(true);
                    cmbCodigoEmpleado.setDisable(true);
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
        ServiciosHasEmpleados registro = new ServiciosHasEmpleados();
        registro.setHoraEvento(txtHoraEvento.getText());
        registro.setLugarEvento(txtLugarEvento.getText());
            if(cmbCodigoServicio.getSelectionModel().getSelectedItem() == null || cmbCodigoEmpleado.getSelectionModel().getSelectedItem() == null || 
               fecha.getSelectedDate() == null || registro.getHoraEvento().equals("") || registro.getLugarEvento().equals(""))
                vacio = true;
            else 
                vacio = false;
        return vacio;
    }
    
    public void guardar(){
        ServiciosHasEmpleados registro = new ServiciosHasEmpleados();
        try{
            registro.setCodigoServicio(((Servicios)cmbCodigoServicio.getSelectionModel().getSelectedItem()).getCodigoServicio());
            registro.setCodigoEmpleado(((Empleado)cmbCodigoEmpleado.getSelectionModel().getSelectedItem()).getCodigoEmpleado());
            registro.setFechaEvento(fecha.getSelectedDate());
            registro.setHoraEvento(txtHoraEvento.getText());
            registro.setLugarEvento(txtLugarEvento.getText());
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarServicios_has_Empleados(?,?,?,?,?)}");
                procedimiento.setInt(1, registro.getCodigoServicio());
                procedimiento.setInt(2, registro.getCodigoEmpleado());
                procedimiento.setDate(3, new java.sql.Date(registro.getFechaEvento().getTime()));
                procedimiento.setString(4, registro.getHoraEvento());
                procedimiento.setString(5, registro.getLugarEvento());
                procedimiento.execute();
                listaServiciosHasEmpleados.add(registro);
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
                tblServiciosHasEmpleados.setDisable(false);
                cmbCodigoServicio.setDisable(true);
                cmbCodigoEmpleado.setDisable(true);
                break;
            
            default:
                if(tblServiciosHasEmpleados.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Esta seguro de eliminar el registro?", "Eliminar Servicio", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_NO_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_eliminarServicios_Has_Empleados(?,?)}");
                            procedimiento.setInt(1, ((ServiciosHasEmpleados)tblServiciosHasEmpleados.getSelectionModel().getSelectedItem()).getCodigoServicio());
                            procedimiento.setInt(2, ((ServiciosHasEmpleados)tblServiciosHasEmpleados.getSelectionModel().getSelectedItem()).getCodigoEmpleado());
                            procedimiento.execute();
                            listaServiciosHasEmpleados.remove(tblServiciosHasEmpleados.getSelectionModel().getSelectedIndex());
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
        cmbCodigoServicio.setDisable(true);
        cmbCodigoEmpleado.setDisable(true);
        fecha.setDisable(true);
    }
    
    public void editar(){
        switch(tipoOperacion){
            case NINGUNO:
                if(tblServiciosHasEmpleados.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tipoOperacion = operaciones.ACTUALIZAR;
                    cmbCodigoServicio.setDisable(true);
                    cmbCodigoEmpleado.setDisable(true);
                    JOptionPane.showMessageDialog(null, "Recuarde no dejar datos vacios");
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemnto");
                }
            break;
            
            case ACTUALIZAR:
                actualizar();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tipoOperacion = operaciones.NINGUNO;
                desactivarControles();
                cmbCodigoServicio.setDisable(true);
                cmbCodigoEmpleado.setDisable(true);
                cargarDatos();
                limpiarControles();
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ActualizarServicios_Has_Empleados(?,?,?,?,?)}");
            ServiciosHasEmpleados registro = (ServiciosHasEmpleados)tblServiciosHasEmpleados.getSelectionModel().getSelectedItem();
            registro.setFechaEvento(fecha.getSelectedDate());
            registro.setHoraEvento(txtHoraEvento.getText());
            registro.setLugarEvento(txtLugarEvento.getText());
            procedimiento.setInt(1, registro.getCodigoServicio());
            procedimiento.setInt(2, registro.getCodigoEmpleado());
            procedimiento.setDate(3, new java.sql.Date(registro.getFechaEvento().getTime()));
            procedimiento.setString(4, registro.getHoraEvento());
            procedimiento.setString(5, registro.getLugarEvento());
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
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnReporte.setText("Reporte");
                cargarDatos();
                tipoOperacion = operaciones.NINGUNO;
                btnEditar.setText("Editar");
                cmbCodigoServicio.setDisable(true);
                cmbCodigoEmpleado.setDisable(true);
        }
    }
    
    public void desactivarControles(){
        cmbCodigoServicio.setDisable(false);
        cmbCodigoEmpleado.setDisable(false);
        fecha.setDisable(true);
        txtHoraEvento.setEditable(false);
        txtLugarEvento.setEditable(false);
    }
    
    public void activarControles(){
        cmbCodigoServicio.setDisable(false);
        cmbCodigoEmpleado.setDisable(false);
        fecha.setDisable(false);
        txtHoraEvento.setDisable(false);
        txtLugarEvento.setDisable(false);
        txtHoraEvento.setEditable(true);
        txtLugarEvento.setEditable(true);
    }
    
    public void limpiarControles(){
        cmbCodigoServicio.getSelectionModel().clearSelection();
        cmbCodigoEmpleado.getSelectionModel().clearSelection();
        txtHoraEvento.setText("");
        txtLugarEvento.setText("");
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