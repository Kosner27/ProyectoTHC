Use proyectothdc;
Select u.Nombre,u.Apellido,u.Correo,r.tipoUsuario,r.descripcion,i.NombreInstitucion,m.NombreMunicipio
from usuario u inner join roles r on u.idRol = r.idRol 
inner join institucion i on i.idInstitucionAuto = u.idInstitucion 
inner join municipio m on i.idMunicipio = m.idMunicipio 
where NombreInstitucion = 'UNIVERSIDAD NACIONAL DE COLOMBIA' and NombreMunicipio = 'Medellín';

select u.Nombre,U.Apellido,r.tipoUsuario,r.descripcio from usuario u inner join roles r on  u.idRol = r.idRol where Correo = 'Hola@correo.com';
update usuario  set idRol='9' where Correo = 'Kosnerbedoya13@gmail.com';
SET SQL_SAFE_UPDATES = 0;
UPDATE usuario 
SET idRol = '9' 
WHERE Correo = 'Keyos1303@gmail.com';

Delete from usuario where Correo = 'eeee'