package ensamblador;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.math.BigInteger; 

public class Parser {
    private Scanner scanner;
    private String currentInstruction; //modificada por advance
    private int currentLine;

    
    //menu de opciones de tipo de instrucciones
    public enum CommandType {
        A_INSTRUCTION, C_INSTRUCTION, LABEL
    }

    //el constructor se encarga de abrir el archivo y cargarlo para ser leido
    public Parser(File file) throws FileNotFoundException {
        scanner = new Scanner(file);
        currentInstruction = null;
        currentLine = 0;
    }

    //revisa si el archivo tiene mas lineas
    public boolean hasMoreLines() {
        return scanner.hasNextLine();
    }

    //va leyendo cada instrucción y guardandola
    public void advance() {
        currentInstruction = null;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            currentLine++;
            
            // limpia comentarios (ignorar todo desde // en adelante)
            int commentIndex = line.indexOf("//"); //busca los dos lineas de comentarios y devuelve el indice de su posición
            if (commentIndex != -1) { // devuelve -1 si no existen, entonces si es diferente a -1 si hay comentarios
                line = line.substring(0, commentIndex); //recorta la linea desde el inicio (0) hasta el indice del comentario (eliminandolo)
            }
            
            // limpia espacios en blanco
            line = line.trim(); //elimina tabulaciones o espacios en blanco al inicio o fin de una linea
            line = line.replaceAll("\\s+", ""); //reemplaza todos los espacios en blanco que haya en la mitad del texto o cualquier tipo de espacio 

            // guarda la instrucción final 
            if (!line.isEmpty()) { // si después de limpiar todo la línea no quedó vacía es porque hay instrucción
                currentInstruction = line; //la instrucción actual es la linea depurada
                return; // terminamos de buscar la instrucción actual
            }
        }
    }

    //devuelve el tipo de instrucción de la linea encintrada
    public CommandType instructionType() {
        if (currentInstruction == null) { // [NUEVO] no hay instruccion (lineas vacias al final del archivo)
            return null;
        }
        if (currentInstruction.startsWith("@")) {
            return CommandType.A_INSTRUCTION;
        } else if (currentInstruction.startsWith("(")) {
            return CommandType.LABEL;
        } else {
            return CommandType.C_INSTRUCTION;
        }
    }

    //devuelve la parte sin @ y sin () de un label y una variable tipo A
    public String symbol() {
        if (instructionType() == CommandType.A_INSTRUCTION) {
            return currentInstruction.substring(1); //quita el @
        } else if (instructionType() == CommandType.LABEL) {
            return currentInstruction.substring(1, currentInstruction.length() - 1); //quita los ()
        }
        return null;
    }

    //devuelve la parte de destino de una instrucción tipo c (antes del =)
    public String dest() {
        int equalIndex = currentInstruction.indexOf("=");
        if (equalIndex != -1) {
            return currentInstruction.substring(0, equalIndex);
        }
        return null;
    }

    //devuelve el componente de una instrucción tipo c (antes del ; despues del =)
    public String comp() {
        int equalIndex = currentInstruction.indexOf("=");
        int semiIndex = currentInstruction.indexOf(";");

        if (equalIndex != -1 && semiIndex != -1) {
            return currentInstruction.substring(equalIndex + 1, semiIndex);
        } else if (equalIndex != -1) {
            return currentInstruction.substring(equalIndex + 1); //sin posición de finalización corta desde ahí hasta le final de la palabra
        } else if (semiIndex != -1) {
            return currentInstruction.substring(0, semiIndex); //para instrucciones sin =, tipo D;JMP o 0;JGT
        } else {
            return currentInstruction; //si no tiene ni ; ni =, toda la instruccion es el comp
        }
    }

    //devuelve el jump en una instrucción tipo c (después del ;)
    public String jump() {
        int semiIndex = currentInstruction.indexOf(";");
        if (semiIndex != -1) {
            return currentInstruction.substring(semiIndex + 1);
        }
        return null;
    }

    //devuelve la linea de instruccion actual del archivo
    public int lineNumber() {
        return currentLine;
    }

    //revisa que la instrucción actual este bien escrita y si no manda el memsaje de error
    public void validate() {
        if (currentInstruction == null) return; //no hay instruccion que revisar

        if (instructionType() == CommandType.A_INSTRUCTION) {
            validateA();
        } else if (instructionType() == CommandType.LABEL) {
            validateLabel();
        } else {
            validateC();
        }
    }
    
    //define que simbolos son válidos
    private boolean isValidSymbol(String name) {
        return name.matches("[A-Za-z_.$:][A-Za-z0-9_.$:]*");
    }

    private void validateA(){
        String symbol = symbol(); //lo que va después del @

        if (symbol.isEmpty()) { // si fuera @
            throw new IllegalArgumentException("instrucción A sin valor");
        }

        char first = symbol.charAt(0); //para seleccionar el primer caracter
        if (first >= '0' && first <= '9') {
            if (!symbol.matches("[0-9]+")){
                throw new IllegalArgumentException("un símbolo no puede iniciar con un número '" + symbol + "'");
            }
            if (new BigInteger(symbol).compareTo(BigInteger.valueOf(32767)) > 0) { //BigInteger en caso de que sea un número muy grande
                throw new IllegalArgumentException("valor fuera de rango '" + symbol + "' (máximo 32767)");
            }
        } else if (!isValidSymbol(symbol)) { //si cosas como  @-1, @VAR#1, @LOOP)
            throw new IllegalArgumentException("símbolo inválido '" + symbol + "'");
        }
    }
    
    //revisa labels
    private void validateLabel() {
        if (!currentInstruction.endsWith(")")) { // si es (LOOP
            throw new IllegalArgumentException("etiqueta sin ')' de cierre");
        }

        String name = symbol(); //lo que hay entre los parentesis

        if (name.isEmpty()) { // si no hay contenido ()
            throw new IllegalArgumentException("etiqueta vacía");
        }
        if (!isValidSymbol(name)) { // cosas como (1LOOP)
            throw new IllegalArgumentException("nombre de etiqueta inválido '" + name + "'");
        }
    }

    //revisa si la instrucción c tiene una estructura correcta
    private void validateC() {
        //largo original menos largo sin ese simbolo
        int equalCount = currentInstruction.length() - currentInstruction.replace("=", "").length(); 
        int semiCount = currentInstruction.length() - currentInstruction.replace(";", "").length();

        if (equalCount > 1) { // cosas como D=M=A, dan en la resta anterior una diferencia mayor a 1
            throw new IllegalArgumentException("la instrucción tiene más de un '='");
        }
        if (semiCount > 1) { // por ejemplo D;JGT;JMP , esta resta daria mayor que 1 y ahi se evidencia que hy mas de un ; (error)
            throw new IllegalArgumentException("la instrucción tiene más de un ';'");
        }

        int equalIndex = currentInstruction.indexOf("=");
        int semiIndex = currentInstruction.indexOf(";");
        if (equalIndex != -1 && semiIndex != -1 && semiIndex < equalIndex) { // primero revisa que ambos existan y luego si el indice del ; es menor al de = (esta antes)
            throw new IllegalArgumentException("el '=' debe ir antes del ';'");
        }
    }
}

