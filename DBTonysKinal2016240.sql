drop database if exists DBTonysKinal2016240;
create database DBTonysKinal2016240;

USE DBTonysKinal2016240;
#-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
CREATE TABLE Productos(
  codigoProducto INT auto_increment,
  nombreProducto VARCHAR(150) NOT NULL,
  cantidad INT NOT NULL,
  PRIMARY KEY PK_codigoProducto (codigoProducto)
);

CREATE TABLE TipoPlato(
  codigoTipoPlato INT auto_increment,
  descripcionTipo VARCHAR(100) NOT NULL,
  PRIMARY KEY PK_codigoTipoPlato (codigoTipoPlato)
);

CREATE TABLE Platos(
  codigoPlato INT auto_increment,
  cantidad INT NOT NULL,
  nombrePlato VARCHAR(50) NOT NULL,
  descripcionPlato VARCHAR(150) NOT NULL,
  precioPlato DECIMAL(10,2) NOT NULL,
  codigoTipoPlato INT NOT NULL,
  PRIMARY KEY PK_codigoPlato (codigoPlato),
  CONSTRAINT FK_Platos_TipoPlato
	FOREIGN KEY (codigoTipoPlato) REFERENCES TipoPlato(codigoTipoPlato)
);

CREATE TABLE Productos_has_Platos(
  codigoProducto INT NOT NULL,
  codigoPlato INT NOT NULL
);

CREATE TABLE Empresas(
  codigoEmpresa INT auto_increment,
  nombreEmpresa VARCHAR(150) NOT NULL,
  direccion VARCHAR(150) NOT NULL,
  telefono VARCHAR(10) NOT NULL,
  PRIMARY KEY PK_codigoEmpresa (codigoEmpresa)
);

CREATE TABLE Servicios(
  codigoServicio INT auto_increment,
  fechaServicio DATE NOT NULL,
  tipoServicio VARCHAR(100) NOT NULL,
  horaServicio TIME NOT NULL,
  lugarServicio VARCHAR(100) NOT NULL,
  telefonoContacto VARCHAR(10) NOT NULL,
  codigoEmpresa INT NOT NULL,
  PRIMARY KEY PK_codigoServicio (codigoServicio),
  CONSTRAINT FK_Servicios_Empresas
    FOREIGN KEY (codigoEmpresa) REFERENCES Empresas(codigoEmpresa)
);

CREATE TABLE Servicios_has_Platos(
  codigoPlato INT NOT NULL,
  codigoServicio INT NOT NULL
);

CREATE TABLE TipoEmpleado(
  codigoTipoEmpleado INT auto_increment,
  descripcion VARCHAR(100) NOT NULL,
  PRIMARY KEY PK_codigoTipoEmpleado (codigoTipoEmpleado)
);

CREATE TABLE Empleados(
  codigoEmpleado INT auto_increment,
  numeroEmpleado INT NOT NULL,
  apellidosEmpleado VARCHAR(150) NOT NULL,
  nombresEmpleado VARCHAR(150) NOT NULL,
  direccionEmpleado VARCHAR(150) NOT NULL,
  telefonoContacto VARCHAR(45) NOT NULL,
  gradoCocinero varchar(50),
  codigoTipoEmpleado INT NOT NULL,
  PRIMARY KEY PK_codigoEmpleado (codigoEmpleado),
  CONSTRAINT FK_Empleados_TipoEmpleado
    FOREIGN KEY (codigoTipoEmpleado) REFERENCES TipoEmpleado(codigoTipoEmpleado)
);

CREATE TABLE Servicios_has_Empleados(
  codigoServicio INT NOT NULL,
  codigoEmpleado INT NOT NULL,
  fechaEvento DATE NOT NULL,
  horaEvento TIME NOT NULL,
  lugarEvento VARCHAR(150) NOT NULL,
  CONSTRAINT FK_Servicios_has_Empleados_Servicios
    FOREIGN KEY (codigoServicio) REFERENCES Servicios(codigoServicio),
  CONSTRAINT FK_Servicios_has_Empleados_Empleados
    FOREIGN KEY (codigoEmpleado) REFERENCES Empleados(codigoEmpleado)
);

CREATE TABLE Presupuesto(
  codigoPresupuesto INT auto_increment,
  fechaSolicitud DATE NOT NULL,
  cantidadPresupuesto DECIMAL(10,2) NOT NULL,
  codigoEmpresa INT NOT NULL,
  PRIMARY KEY PK_codigoPresupuesto (codigoPresupuesto),
  CONSTRAINT FK_Presupuesto_Empresas
    FOREIGN KEY (codigoEmpresa) REFERENCES Empresas(codigoEmpresa)
);
#-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
call sp_agregarServicios_has_platos(1,1);
call sp_agregarServicios_has_platos(2,2);
call sp_agregarServicios_has_platos(3,3);
call sp_agregarServicios_has_platos(4,4);
call sp_agregarServicios_has_platos(5,5);
#Listar
delimiter $$
	create procedure sp_ListarProductos() 
    begin
		select Productos.codigoProducto, Productos.nombreProducto, Productos.cantidad from Productos;
    end $$
delimiter ;

delimiter $$
	create procedure sp_ListarTipoPlato() 
    begin
		select TipoPlato.codigoTipoPlato, TipoPlato.descripcionTipo from TipoPlato;
    end $$
delimiter ;

delimiter $$
	create procedure sp_ListarPlatos() 
    begin
		select Platos.codigoPlato, Platos.cantidad, Platos.nombrePlato, Platos.descripcionPlato, Platos.precioPlato, Platos.codigoTipoPlato from Platos;
    end $$
delimiter ;	

delimiter $$  
	create procedure sp_ListarProductos_has_Platos() 
    begin
		select codigoProducto, codigoPlato from 
	(select p.codigoProducto as codigoProducto from productos p
		left join productos_has_platos php on php.codigoProducto = p.codigoProducto) c1,
	(Select pl.codigoPlato as codigoPlato from platos pl
		left join productos_has_platos php on php.codigoPlato = pl.codigoPlato) c2;
    end $$
delimiter ;

delimiter $$
	create procedure sp_ListarEmpresas() 
    begin
		select Empresas.codigoEmpresa, Empresas.nombreEmpresa, Empresas.direccion, Empresas.telefono from Empresas;
    end $$
delimiter ;

delimiter $$
	create procedure sp_ListarServicios() 
    begin
		select Servicios.codigoServicio, Servicios.fechaServicio, Servicios.tipoServicio, Servicios.horaServicio, Servicios.lugarServicio, Servicios.telefonoContacto, Servicios.codigoEmpresa
        from Servicios;
    end $$
delimiter ;

delimiter $$  
	create procedure sp_ListarServicios_has_platos() 
    begin
		select codigoServicio, codigoPlato from
	(Select s.codigoServicio as codigoServicio from servicios s
		left join servicios_has_platos shp on shp.codigoServicio = s.codigoServicio) c1,
	(Select pl.codigoPlato as codigoPlato from platos pl
		left join servicios_has_platos shp on shp.codigoPlato = pl.codigoPlato) c2;
    end $$
delimiter ;

delimiter $$  
	create procedure sp_ListarTipoEmpleado() 
    begin
		select TipoEmpleado.codigoTipoEmpleado, TipoEmpleado.descripcion from TipoEmpleado;
    end $$
delimiter ;

delimiter $$  
	create procedure sp_ListarEmpleados() 
    begin
		select Empleados.codigoEmpleado, Empleados.numeroEmpleado, Empleados.apellidosEmpleado, Empleados.nombresEmpleado, Empleados.direccionEmpleado, Empleados.telefonoContacto,
		Empleados.gradoCocinero, Empleados.codigoTipoEmpleado from Empleados;
    end $$
delimiter ;

delimiter $$  
	create procedure sp_ListarServicios_has_Empleados() 
    begin
		select Servicios_has_Empleados.codigoServicio, Servicios_has_Empleados.codigoEmpleado,
        Servicios_has_Empleados.fechaEvento, Servicios_has_Empleados.horaEvento, Servicios_has_Empleados.lugarEvento from Servicios_has_Empleados;
    end $$
delimiter ;

delimiter $$  
	create procedure sp_ListarPresupuesto() 
    begin
		select Presupuesto.codigoPresupuesto, Presupuesto.fechaSolicitud, Presupuesto.cantidadPresupuesto, Presupuesto.codigoEmpresa
        from Presupuesto;
    end $$
delimiter ;


#Agregar
call sp_agregarServicios_has_platos(6,6);
call sp_agregarServicios_has_platos(7,7);
call sp_agregarServicios_has_platos(8,8);
call sp_agregarServicios_has_platos(9,9);
call sp_agregarServicios_has_platos(10,10);
delimiter $$
	create procedure sp_AgregarProductos(in nombreProducto varchar(150), in cantidad int)
		begin
			insert into Productos(nombreProducto, cantidad)
            values(nombreProducto, cantidad);
		end$$
delimiter ;

delimiter $$
	create procedure sp_AgregarTipoPlato(in descripcionTipo varchar(100))
		begin
			insert into TipoPlato(descripcionTipo)
            values(descripcionTipo);
		end$$
delimiter ;

delimiter $$
	create procedure sp_AgregarPlatos(in cantidad int, in precioPlato decimal(10,2), in descripcionPlato varchar(150),  in nombrePlato varchar(150),
    in codigoTipoPlato int)
		begin
			insert into Platos(cantidad, precioPlato, descripcionPlato, nombrePlato, codigoTipoPlato)
            values(cantidad, precioPlato, descripcionPlato, nombrePlato, codigoTipoPlato);
		end$$
delimiter ;

delimiter $$
	create procedure sp_AgregarProductos_has_Platos(in codigoProducto int, in codigoPlato int)
		begin
			insert into Productos_has_Platos(codigoProducto, codigoPlato)
            values(codigoProducto, codigoPlato);
		end$$
delimiter ;

delimiter $$
	create procedure sp_AgregarEmpresas(in nombreEmpresa varchar(150), in direccion varchar(150), in telefono varchar(150))
		begin
			insert into Empresas(nombreEmpresa, direccion, telefono)
            values(nombreEmpresa, direccion, telefono );
		end$$
delimiter ;

delimiter $$
	create procedure sp_AgregarServicios(in fechaServicio date, in tipoServicio varchar(100), in horaServicio time, in lugarServicio varchar(100),
    in telefonoContacto varchar(10), in codigoEmpresa int)
		begin
			insert into Servicios(fechaServicio, tipoServicio, horaServicio, lugarServicio, telefonoContacto, codigoEmpresa)
            values(fechaServicio, tipoServicio, horaServicio, lugarServicio, telefonoContacto, codigoEmpresa);
		end$$
delimiter ;

delimiter $$
	create procedure sp_AgregarServicios_has_platos(in codigoPlato int, in codigoServicio int)
		begin
			insert into Servicios_has_platos(codigoPlato, codigoServicio)
            values(codigoPlato, codigoServicio);
		end$$
delimiter ;

delimiter $$
	create procedure sp_AgregarTipoEmpleado(in descripcion varchar(100))
		begin
			insert into TipoEmpleado(descripcion)
            values(descripcion);
		end$$
delimiter ;

delimiter $$
	create procedure sp_AgregarEmpleados(in numeroEmpleado int, in apellidosEmpleado varchar(150), in nombresEmpleado varchar(150),
    in direccionEmpleado varchar(150), in telefonoContacto varchar(10), in gradoCocinero varchar(10), in codigoTipoEmpleado int)
		begin
			insert into Empleados(numeroEmpleado, apellidosEmpleado, nombresEmpleado, direccionEmpleado, telefonoContacto, gradoCocinero,
    codigoTipoEmpleado)
            values(numeroEmpleado, apellidosEmpleado, nombresEmpleado, direccionEmpleado, telefonoContacto, gradoCocinero,
    codigoTipoEmpleado);
		end$$
delimiter ;

delimiter $$
	create procedure sp_AgregarServicios_has_Empleados(in codigoServicio int, in codigoEmpleado int, in fechaEvento date, in horaEvento time, in lugarEvento varchar(150))
		begin
			insert into Servicios_has_Empleados(codigoServicio, codigoEmpleado, fechaEvento, horaEvento, lugarEvento)
            values(codigoServicio, codigoEmpleado, fechaEvento, horaEvento, lugarEvento);
		end$$
delimiter ;

delimiter $$
	create procedure sp_AgregarPresupuesto(in fechaSolicitud date, in cantidadPresupuesto decimal(10,2), in codigoEmpresa int)
		begin
			insert into Presupuesto(fechaSolicitud, cantidadPresupuesto, codigoEmpresa)
            values(fechaSolicitud, cantidadPresupuesto, codigoEmpresa);
		end$$
delimiter ;


#Eliminar
call sp_agregarServicios_has_platos(11,11);
call sp_agregarServicios_has_platos(12,12);
call sp_agregarServicios_has_platos(13,13);
call sp_agregarServicios_has_platos(14,14);
call sp_agregarServicios_has_platos(15,15);
delimiter $$
	create procedure sp_EliminarProductos(in idProducto int)
		begin
			delete from Productos where codigoProducto = idProducto;
		end$$
delimiter ;

delimiter $$
	create procedure sp_EliminarTipoPlato(in idTipoPlato int)
		begin
			delete from TipoPlato where codigoTipoPlato = idTipoPlato;
		end$$
delimiter ;

delimiter $$
	create procedure sp_EliminarPlatos(in idPlato int)
		begin
			delete from Platos where codigoPlato = idPlato;
		end$$
delimiter ;

delimiter $$
	create procedure sp_EliminarProductos_has_Platos(in idProducto int, in idPlato int)
		begin
			delete from Productos_has_Platos where codigoPlato = idPlato and codigoProducto = idProducto;
		end$$
delimiter ;

delimiter $$
	create procedure sp_EliminarEmpresas(in idEmpresa int)
		begin
			delete from Empresas where codigoEmpresa = idEmpresa;
		end$$
delimiter ;

delimiter $$
	create procedure sp_EliminarServicios(in idServicio int)
		begin
			delete from Servicios where codigoServicio = idServicio;
		end$$
delimiter ;

delimiter $$
	create procedure sp_EliminarServicios_has_platos(in idServicio int, in idPlato int)
		begin
			delete from Servicios_has_platos where codigoServicio = idServicio and codigoPlato = idPlato;
		end$$
delimiter ;

delimiter $$
	create procedure sp_EliminarTipoEmpleado(in idTipoEmpleado int)
		begin
			delete from TipoEmpleado where codigoTipoEmpleado = idTipoEmpleado;
		end$$
delimiter ;

delimiter $$
	create procedure sp_EliminarEmpleados(in idEmpleado int)
		begin
			delete from Empleados where codigoEmpleado = idEmpleado;
		end$$
delimiter ;

delimiter $$
	create procedure sp_EliminarServicios_has_Empleados(in idServicio int, in idEmpleado int)
		begin
			delete from Servicios_has_Empleados where codigoServicio = idServicio and codigoEmpleado = idEmpleado;
		end$$
delimiter ;

delimiter $$
	create procedure sp_EliminarPresupuesto(in idPresupuesto int)
		begin
			delete from Presupuesto where codigoPresupuesto = idPresupuesto;
		end$$
delimiter ;


#Buscar
call sp_agregarProductos_Has_Platos(1,1);
call sp_agregarProductos_Has_Platos(2,2);
call sp_agregarProductos_Has_Platos(3,3);
call sp_agregarProductos_Has_Platos(4,4);
call sp_agregarProductos_Has_Platos(5,5);
delimiter $$
	create procedure sp_BuscarProductos(in idProducto int)
		begin
			select * from Productos where codigoProducto = idProducto;
		end$$
delimiter ;

delimiter $$
	create procedure sp_BuscarTipoPlato(in idTipoPlato int)
		begin
			select * from TipoPlato where codigoTipoPlato = idTipoPlato;
		end$$
delimiter ;

delimiter $$
	create procedure sp_BuscarPlatos(in idPlato int)
		begin
			select * from Platos where codigoPlato = idPlato;
		end$$
delimiter ;

delimiter $$
	create procedure sp_BuscarProductos_has_Platos(in idProducto int, in id_Plato int)
		begin
			select * from Productos_has_Platos where codigoPlato = idPlato and codigoProducto = idProducto;
		end$$
delimiter ;

delimiter $$
	create procedure sp_BuscarEmpresas(in idEmpresa int)
		begin
			select * from Empresas where codigoEmpresa = idEmpresa;
		end$$
delimiter ;

delimiter $$
	create procedure sp_BuscarServicios(in idServicio int)
		begin
			select * from Servicios where codigoServicio = idServicio;
		end$$
delimiter ;

delimiter $$
	create procedure sp_BuscarServicios_has_platos(in idServicio int, in idPlato int)
		begin
			select * from Servicios_has_platos where codigoServicio = idServicio and codigoPlato = codigoPlato;
		end$$
delimiter ;

delimiter $$
	create procedure sp_BuscarTipoEmpleado(in idTipoEmpleado int)
		begin
			select * from TipoEmpleado where codigoTipoEmpleado = idTipoEmpleado;
		end$$
delimiter ;

delimiter $$
	create procedure sp_BuscarEmpleados(in idEmpleado int)
		begin
			select * from Empleados where codigoEmpleado = idEmpleado;
		end$$
delimiter ;

delimiter $$
	create procedure sp_BuscarServicios_has_Empleados(in idServicio int, in idEmpleado int)
		begin
			select * from Servicios_has_Empleados where codigoServicio = idServicio and codigoEmpleado = idEmpleado;
		end$$
delimiter ;

delimiter $$
	create procedure sp_BuscarPresupuesto(in idPresupuesto int)
		begin
			select * from Presupuesto where codigoPresupuesto = idPresupuesto;
		end$$
delimiter ;


#Actualizar
call sp_agregarProductos_Has_Platos(6,6);
call sp_agregarProductos_Has_Platos(7,7);
call sp_agregarProductos_Has_Platos(8,8);
call sp_agregarProductos_Has_Platos(9,9);
call sp_agregarProductos_Has_Platos(10,10);
delimiter $$
	create procedure sp_ActualizarProductos(in idProducto int, in nombreProducto2 varchar(150), in cantidad2 int)
		begin
			update Productos set nombreProducto = nombreProducto2, cantidad = cantidad2 where codigoProducto = idProducto;
		end$$
delimiter ;

delimiter $$
	create procedure sp_ActualizarTipoPlato(in idTipoPlato int, in descripcionTipo2 varchar(100))
		begin
			update TipoPlato set descripcionTipo = descripcionTipo2 where codigoTipoPlato = idTipoPlato;
		end$$
delimiter ;

delimiter $$
	create procedure sp_ActualizarPlatos(in idPlato int, in cantidad2 int, in precioPlato2 decimal(10,2), in descripcionPlato2 varchar(150),  in nombrePlato2 varchar(150),
    in codigoTipoPlato2 int)
		begin
			update Platos set cantidad = cantidad2, precioPlato = precioPlato2, descripcionPlato = descripcionPlato2, nombrePlato = nombrePlato2,
            codigoTipoPlato = codigoTipoPlato2 where codigoPlato = idPlato;
		end$$
delimiter ;

delimiter $$
	create procedure sp_ActualizarProductos_has_Platos(in codigoProducto2 int, in codigoPlato2 int)
		begin
			update Productos_has_Platos set codigoProducto = codigoProducto2, codigoPlato = codigoPlato2 where 
            codigoProducto = codigoProducto2 and codigoPlato = codigoPlato2;
		end$$
delimiter ;

delimiter $$
	create procedure sp_ActualizarEmpresas(in idEmpresa int, in nombreEmpresa2 varchar(150), in direccion2 varchar(150), in telefono2 varchar(150))
		begin
			update Empresas set nombreEmpresa = nombreEmpresa2, direccion = direccion2, telefono = telefono2 where codigoEmpresa = idEmpresa;
		end$$
delimiter ;

delimiter $$
	create procedure sp_ActualizarServicios(in idServicio int, in fechaServicio2 date, in tipoServicio2 varchar(100), in horaServicio2 time, in lugarServicio2 varchar(100),
    in telefonoContacto2 varchar(10), in codigoEmpresa2 int)
		begin
			update Servicios set fechaServicio = fechaServicio2, tipoServicio = tipoServicio2, horaServicio = horaServicio2, lugarServicio = lugarServicio2,
            telefonoContacto = telefonoContacto2, codigoEmpresa = codigoEmpresa2 where codigoServicio = idServicio;
		end$$
delimiter ;

delimiter $$
	create procedure sp_ActualizarServicios_has_platos(in codigoPlato2 int, in codigoServicio2 int)
		begin
			update Servicios_has_platos set codigoPlato = codigoPlato2, codigoServicio = codigoServicio2 where codigoPlato
            = codigoPlato2 and codigoServicio = codigoServicio2;
		end$$
delimiter ;

delimiter $$
	create procedure sp_ActualizarTipoEmpleado(in idTipoEmpleado int,in descripcion2 varchar(100))
		begin
			update TipoEmpleado set descripcion = descripcion2 where codigoTipoEmpleado = idTipoEmpleado;
		end$$
delimiter ;

call sp_agregarProductos_Has_Platos(11,11);
call sp_agregarProductos_Has_Platos(12,12);
call sp_agregarProductos_Has_Platos(13,13);
call sp_agregarProductos_Has_Platos(14,14);
call sp_agregarProductos_Has_Platos(15,15);

delimiter $$
	create procedure sp_ActualizarEmpleados(in idEmpleado int,in numeroEmpleado2 int, in apellidosEmpleado2 varchar(150), in nombresEmpleado2 varchar(150),
    in direccionEmpleado2 varchar(150), in telefonoContacto2 varchar(10), in gradoCocinero2 varchar(10), in codigoTipoEmpleado2 int)
		begin
			update Empleados set numeroEmpleado = numeroEmpleado2, apellidosEmpleado = apellidosEmpleado2, nombresEmpleado = nombresEmpleado2, direccionEmpleado =
            direccionEmpleado2, telefonoContacto = telefonoContacto2, gradoCocinero = gradoCocinero2, codigoTipoEmpleado = codigoTipoEmpleado2
            where codigoEmpleado = idEmpleado;
		end$$
delimiter ;

delimiter $$
	create procedure sp_ActualizarServicios_has_Empleados(in codigoServicio2 int, in codigoEmpleado2 int, in fechaEvento2 date,
    in horaEvento2 time, in lugarEvento2 varchar(150))
		begin
			update Servicios_has_Empleados set codigoServicio = codigoServicio2, codigoEmpleado = codigoEmpleado2,
			fechaEvento = fechaEvento2, horaEvento = horaEvento2, lugarEvento = lugarEvento2 where codigoServicio = codigoServicio2 and 
            codigoEmpleado = codigoEmpleado2;
		end$$
delimiter ;

delimiter $$
	create procedure sp_ActualizarPresupuesto(in idPresupuesto int,in fechaSolicitud2 date, in cantidadPresupuesto2 decimal(10,2), in codigoEmpresa2 int)
		begin
			update Presupuesto set fechaSolicitud = fechaSolicitud2, cantidadPresupuesto = cantidadPresupuesto2, codigoEmpresa = codigoEmpresa2 where
            codigoPresupuesto = idPresupuesto;
		end$$
delimiter ;
#-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
#Procedimientos para reportes
Delimiter $$
	create procedure sp_SubReportePresupuesto (in codEmpresa int)
		Begin
			select p.fechaSolicitud, p.cantidadPresupuesto from Empresas e inner join presupuesto p on e.codigoEmpresa = p.codigoEmpresa where e.codigoEmpresa = codEmpresa
            group by p.cantidadPresupuesto;
        End$$
Delimiter ;

Delimiter $$
	Create procedure sp_ReportePresupuesto (in codEmpresa int)
		Begin 
			select e.nombreEmpresa, e.direccion, s.HoraServicio, s.LugarServicio, s.tipoServicio from empresas e inner join servicios s 
            on e.codigoEmpresa = s.codigoEmpresa where e.codigoEmpresa = codEmpresa;
		End$$
Delimiter ;

Delimiter $$
	Create procedure sp_SubReporteServicio (in codServicio int)
		Begin
			Select p.nombreProducto from Servicios s inner join servicios_has_platos shp on s.codigoServicio = shp.codigoServicio inner join productos_has_platos php
			on shp.codigoPlato = php.codigoPlato inner join productos p on p.codigoProducto = php.codigoProducto where s.codigoServicio = codServicio;
		End$$
Delimiter ;

Delimiter $$
	Create procedure sp_ReporteServicio (in codServicio int)
		Begin 
			Select s.tipoServicio, s.LugarServicio, s.fechaServicio, pl.cantidad, tp.descripcionTipo, p.nombreProducto from servicios s inner join servicios_has_platos shp 
			on s.codigoServicio = shp.codigoServicio inner join platos pl on pl.codigoPlato = shp.codigoPlato inner join tipoPlato tp on tp.codigoTipoPlato = pl.codigoTipoPlato
			inner join productos_has_platos php on shp.codigoPlato = php.codigoPlato inner join productos p on p.codigoProducto = php.codigoProducto
			where s.codigoServicio = codServicio;
		End$$
Delimiter ;
#------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------