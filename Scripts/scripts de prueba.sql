use proyectothdc;
Select NombreNucleo from emisionnucleo en inner join nucleoinstitucion ni 
on en.IdNucleo = ni.IdNucleo inner join institucion i 
on ni.idInstitucion = i.idInstitucionAuto where i.NombreInstitucion = ? 
group by NombreNucleo;

Select nu.NombreNucleo from nucleoinstitucion nu inner join  municipio m on nu.idMunicipio = m.idMunicipio 
inner join municipioinstitiucion mi on m.idMunicipio = mi.idMuncipio inner join institucion i on i.IdInstitucionAuto=mi.IdInstitucion
where i.NombreInstitucion = ? group by nu.NombreNucleo;

Select count(nu.IdNucleo) from nucleoinstitucion nu inner join  municipio m on nu.idMunicipio = m.idMunicipio 
inner join municipioinstitiucion mi on m.idMunicipio = mi.idMuncipio inner join institucion i on i.IdInstitucionAuto=mi.IdInstitucion
where i.NombreInstitucion = 'INSTITUCION UNIVERSITARIA DE Envigado' group by nu.NombreNucleo;


 INSERT INTO emisionnucleo ( idEmision, IdNucleo, anioBase,cargaAmbiental,Co2Aportado) 
	VALUES (42,null,000, 11,  11);
Select * from municipio;
Select * from emision;
Select * from emisionnucleo;
Select * from nucleoinstitucion;
Select * from institucion;
INSERT INTO nucleoinstitucion (idNucleo,NombreNucleo,idMunicipio)
        values('811.000.278-2','No aplica', 5001);
        
call insertarCalculoSinNucleo(22,111,'INSTITUCION UNIVERSITARIA DE Envigado','ENERGIA',133.2,'Envigado');
Call InstitucionesSinNucleo('INSTITUCION UNIVERSITARIA DE Envigado','Envigado');

CALL BuscarInstitucion('Envigado', 'INSTITUCION UNIVERSITARIA DE Envigado', @idnucleo);
select @idnucleo;
CALL ObtenerIdMunicipio('Envigado', @idm);
select @idm;

insert into  emisionnucleo (idEmision, IdNucleo, anioBase, cargaAmbiental, Co2Aportado) 
values(44,null,12334,112,123);

select * from Institucion i inner join municipioinstitiucion 
mi on i.IdInstitucionAuto = mi.IdInstitucion 
inner join municipio m on mi.idMuncipio= m.idMunicipio 
inner join emisionnucleo en on i.IdInstitucionAuto = en.IdInstitucion;


select NombreNucleo from Institucion i inner join municipioinstitiucion 
mi on i.IdInstitucionAuto = mi.IdInstitucion 
inner join municipio m on mi.idMuncipio= m.idMunicipio 
inner join nucleoinstitucion nu on mi.idMuncipio= nu.idMunicipio
where i.NombreInstitucion = ? group by NombreNucleo;

call ObtenerIdMunicipio('Envigado', @id);
select @id;
SHOW FULL COLUMNS FROM municipio;
 
 SELECT idMunicipio, NombreMunicipio
FROM municipio
WHERE NombreMunicipio = 'Envigado';

Select idMunicipio  from municipio where NombreMunicipio = 'Envigado';

Select anioBase from emisionnucleo en 
inner join nucleoinstitucion n on n.IdNucleo = en.idNucleo
inner join municipio m on m.IdMunicipio = n.IdMunicipio
inner join municipioinstitiucion mi ON m.IdMunicipio = mi.idMuncipio
INNER JOIN institucion i ON i.idInstitucionAuto = mi.IdInstitucion
WHERE i.NombreInstitucion = ? AND m.NombreMunicipio = ? AND n.NombreNucleo = ?
ORDER BY en.anioBase;
Select * from usuario;
Select * from roles;

Select n.NombreNucleo,  i.NombreInstitucion, i.Nit, m.NombreMunicipio, d.NombreDepartamento
 from nucleoinstitucion n
INNER JOIN institucion i ON i.idInstitucionAuto = n.idInstitucion
inner join municipioinstitiucion mi on mi.IdInstitucion= i.idInstitucionAuto
inner join municipio m on m.idMunicipio = mi.idMuncipio
inner join Departamento d on d.idDepartamento=m.idDepartamento
where i.NombreInstitucion = ? and n.NombreNucleo = ? and n.NombreMunicipio = ?;

SELECT NombreInstitucion AS NombreInstitucion 
FROM institucion i 
INNER JOIN nucleoinstitucion n ON n.idInstitucion = i.IdInstitucionAuto
UNION
SELECT NombreInstitucion AS NombreInstitucion 
FROM institucion i 
INNER JOIN emisionnucleo ne ON i.IdInstitucionAuto = ne.IdInstitucion;



