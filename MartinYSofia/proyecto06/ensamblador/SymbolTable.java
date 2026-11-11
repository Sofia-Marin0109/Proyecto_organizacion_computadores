package ensamblador;

import java.util.HashMap;

public class SymbolTable { 

    private HashMap<String, Integer> table; //creacion de la tabal de simbolos: Simbolo -> Direccion de memoria

    public SymbolTable() {
        table = new HashMap<String, Integer>();
        
        // agregar símbolos predefinidos como los primeros registros de R
        for (int i = 0; i <= 15; i++) {
            table.put("R" + i, i); //agrega el nombre (tipo R0), mas su valor en memoria (i)
        }

        table.put("SP", 0); //puntero del stack
        table.put("LCL", 1); // puntero de var locales
        table.put("ARG", 2); //puntero de argumentos
        table.put("THIS", 3); 
        table.put("THAT", 4);
        table.put("SCREEN", 16384);
        table.put("KBD", 24576); //teclado
    }

    //para cada variable NO PREDEFINIDA (ej: @sum) se crea una entrada en la tabla
    public void addEntry(String symbol, int address) {
        table.put(symbol, address); //var con su dir de memoria
    }

    //para verificar si la variable ya existe en la tabla 
    public boolean contains(String symbol) {
        return table.containsKey(symbol);
    }

    // para obtener la dir de memoria de una variable
    public int getAddress(String symbol) {
        return table.get(symbol);
    }
}