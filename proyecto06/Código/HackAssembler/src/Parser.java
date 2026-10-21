package HackAssembler.src;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Parser {
    private Scanner scanner;
    private String currentInstruction; //modificada por advance

    
    //menu de opciones de tipo de instrucciones
    public enum CommandType {
        A_INSTRUCTION, C_INSTRUCTION, LABEL
    }

    //el constructor se encarga de abrir el archivo y cargarlo para ser leido
    public Parser(File file) throws FileNotFoundException {
        scanner = new Scanner(file);
        currentInstruction = null;
    }

    //revisa si el archivo tiene mas lineas
    public boolean hasMoreLines() {
        return scanner.hasNextLine();
    }

    //va leyendo cada instrucción y guardandola
    public void advance() {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            
            // limpia comentarios (ignorar todo desde // en adelante)
            int commentIndex = line.indexOf("//"); //busca los dos lineas de comentarios y devuelve el indice de su posición
            if (commentIndex != -1) { // devuelve -1 si no existen, entonces si es diferente a -1 si hay comentarios
                line = line.substring(0, commentIndex); //recorta la linea desde el inicio (0) hasta el indice del comentario (eliminandolo)
            }
            
            // limpia espacios en blanco
            line = line.trim(); //elimina tabulaciones o espacios en blanco al inicio o fin de una linea
            line = line.replaceAll(" ", ""); //reemplaza todos los espacios en blanco que haya en la mitad del texto

            // guarda la instrucción final 
            if (!line.isEmpty()) { // si después de limpiar todo la línea no quedó vacía es porque hay instrucción
                currentInstruction = line; //la instrucción actual es la linea depurada
                return; // terminamos de buscar la instrucción actual
            }
        }
    }

    //devuelve el tipo de instrucción de la linea encintrada
    public CommandType instructionType() {
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

}

