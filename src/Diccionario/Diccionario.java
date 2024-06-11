package Diccionario;

import java.util.HashMap;
import java.util.Map;

public class Diccionario {
    Map<String, Double> factorEmision;

    public Diccionario(){
        factorEmision = new HashMap<>();
        factorEmision.put("ACPM", 10.15);
        factorEmision.put("COMBUSTOLEO",11.76);
        factorEmision.put("CRUDO DE CASTILLA", 11.72);
        factorEmision.put("DIESEL GENERICO",10.15);
        factorEmision.put("GASOLINA GENERICO",8.15);
        factorEmision.put("KEROSENE COL",9.71);
        factorEmision.put("KEROSENE GENERICO",9.72);
        factorEmision.put("OIL CRUDE",11.54);
        factorEmision.put("BIODISEL GENERICO",9.44);
        factorEmision.put("BIOGASOLINA GENERICA",7.17);
        factorEmision.put("BAGAZO",1.68);
        factorEmision.put("CARBON GENERICO",2.45);
        factorEmision.put("FIBRE PALMA DE ACEITE",1.93);
        factorEmision.put("LEÑA",1.84);
        factorEmision.put("MADERA",1.15);
        factorEmision.put("BIOGAS CENTRAL",1.97);
        factorEmision.put("COKE GAS D",0.77);
        factorEmision.put("GAS DOMACI",1.86);
        factorEmision.put("GAS LIQUIDO D",7.11);
        factorEmision.put("GAS NATURAL GENERICO",1.86);
        factorEmision.put("LNG GENERICO",1.86);
        factorEmision.put("LPG GENERICO",7.11);
        factorEmision.put("LPG PROPANO ",8.21);
        factorEmision.put("OIL GAS",2.68);
        factorEmision.put("ENERGIA",0.136);

    }
    public Double getFactorEmision(Double clave) {
        return factorEmision.get(clave);
    }

    public void setFactorEmision(String tipos, Double Factor) {
        factorEmision.put(tipos,Factor);
    }

    public Double buscarClave(String texto) {
        String textoMayus = texto.toUpperCase(); // Convertir el texto a mayúsculas
        for (String clave : factorEmision.keySet()) {
            if (clave.toUpperCase().equals(textoMayus)) {
                return factorEmision.get(clave);
            }
        }
        return null; // Retornar null si no se encuentra la clave
    }


}
