use ProyectoTHDC;
SELECT 
	e.Alcance,
    SUM(ei.Co2Aportado) AS "co2_por_año",
    ei.anioBase
FROM 
    emisioninstitucion ei 
INNER JOIN 
    institucion i ON ei.idInstitucion = i.Nit 
INNER JOIN 
    emision e ON ei.idEmision = e.IdEmision 
WHERE 
    i.NombreInstitucion = 'eafi'
GROUP BY 
    e.Alcance , ei.anioBase
    union all 
SELECT 
    'Total' AS Alcance,
   
    SUM(ei.Co2Aportado) AS "co2_por_año",
     ei.anioBase
FROM 
    emisioninstitucion ei 
INNER JOIN 
    institucion i ON ei.idInstitucion = i.Nit 
INNER JOIN 
    emision e ON ei.idEmision = e.IdEmision 
WHERE 
    i.NombreInstitucion = 'eafi'
GROUP BY 
    ei.anioBase

ORDER BY 
    anioBase, Alcance;
