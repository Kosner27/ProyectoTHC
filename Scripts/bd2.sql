use ProyectoTHDC;
create table Emision (
idEmision INT AUTO_INCREMENT PRIMARY KEY,
Emision nvarchar(255),
NombreFuente nvarchar(255),
EstadoFuente nvarchar(255),
unidadMedida nvarchar(30),
alcnance nvarchar(100),
factor double (10,2)
);
call insertarEmision('combustible','acpm','liquido','gal','2',10.5);
use ProyectoTHDC;
select * from Emision;

use ProyectoTHDC;
Call LlenartablaCalcular("energia");
select * from institucion;
use ProyectoTHDC;
select * from emision;
select * from emision where NombreFuente = 'ACPM';
Create table EmisionInstitucion(
idEmisionInstitucion INT AUTO_INCREMENT PRIMARY KEY,
AnioBase DateTime,
Total double(10,2),
CargaAmbiental double(10,2),
idinstitucion varchar(255),
idEmision int ,
FOREIGN KEY (idinstitucion) REFERENCES institucion(Nit),
Foreign Key (idEmision) references emision(idEmision)
)
select * from emision where NombreFuente=nombreFuente;
