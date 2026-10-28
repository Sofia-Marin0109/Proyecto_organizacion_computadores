package ensamblador;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;                 
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class HackAssembler {
    public static void main(String[] args) {
       
        if (args.length != 1) {
            System.out.println("Para utilizarlo: java HackAssembler.src.HackAssembler <archivo.asm>");
            System.exit(1);
        }
        //Rutas del archivo a leer y a escribie
        String inputFilePath = args[0];
        // el archivo de salida va en la misma carpeta y con el mismo nombre pero .hack
        String outputFilePath; 
        if (inputFilePath.endsWith(".asm")) { //le quita la extensión para rescatar el nombre exacto y la ruta
            outputFilePath = inputFilePath.substring(0, inputFilePath.length() - 4) + ".hack";
        } else { //le agrega al archivo el .hack si no era un .asm
            outputFilePath = inputFilePath + ".hack";
        }
        try {
            File inputFile = new File(inputFilePath);

            SymbolTable symbolTable = new SymbolTable(); //Inicializamos la tabla de simbls
            
            //Realiza la primera pasada, donde busca simbolos predefinifos y labels
            Parser parserPasada1 = new Parser(inputFile);
            int romAddress = 0; //contador de las lineas de instruccion reales

            //bucle para recorrer cada linea del archivo y descomponerla
            while (parserPasada1.hasMoreLines()) {
                parserPasada1.advance();
                
                if (parserPasada1.instructionType() == null) continue;
        
                try {
                    parserPasada1.validate(); 
                    //si es comp realiza todas las validaciones
                    if (parserPasada1.instructionType() == Parser.CommandType.C_INSTRUCTION) {
                        Code.dest(parserPasada1.dest());
                        Code.comp(parserPasada1.comp());
                        Code.jump(parserPasada1.jump());
                    }
                    //si es label realiza sus validaciones
                    if (parserPasada1.instructionType() == Parser.CommandType.LABEL) {
                        String label = parserPasada1.symbol();
                        if (symbolTable.contains(label)) { // ya declarada, o es un simbolo predefinido
                            throw new IllegalArgumentException("etiqueta '" + label + "' ya declarada o es un símbolo predefinido");
                        }
                        symbolTable.addEntry(label, romAddress);
                    } else {
                        romAddress++; //solo se suma para instrucciones tipo A o C, los labels no ocupan espacio 
                    }
                } catch (IllegalArgumentException e) { // cualquier error de validacion queda aqui
                    reportError(parserPasada1.lineNumber(), e.getMessage(), outputFilePath);
                }
            }

            //Segunda pasada, se traducen las variables y el codigo a binario
            Parser parserPasada2 = new Parser(inputFile);
            PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath));

            int ramAddress = 16; //a partir de la dirección 16 de la RAM comienzan a guardarse las nuevas variables
            boolean primeraLinea = true; // para revisar si es la primera linea y poner espacio o no
            
            while (parserPasada2.hasMoreLines()) {
                parserPasada2.advance();
                
                if (parserPasada2.instructionType() == null) continue;
                if (parserPasada2.instructionType() == Parser.CommandType.LABEL) continue;
                    
                if (parserPasada2.instructionType() == Parser.CommandType.A_INSTRUCTION) {
                    String symbol = parserPasada2.symbol(); //extraemos lo que va despúes del @
                    int addressValue; //aca va la dirección de memoria correspondiente a el símbolo

                    //no sabemos si el simbolo de la instrucción tipo A es un número, una label o una variable nueva
                    if (Character.isDigit(symbol.charAt(0))) { //comenzamos revisando si es un número
                        addressValue = Integer.parseInt(symbol); //si es un numero se guarda en la dirección de memoria de ese num (ej, @100, addressValue = 100)
                    } else { //si no es numero, es texto
                        if (!symbolTable.contains(symbol)) { //revisamos si el simbolo no existe en la tabla
                            symbolTable.addEntry(symbol, ramAddress); //como no existe es varable nueva para la tabla
                            ramAddress++; //sumamos 1 a la dirección de memoria en la RAM para la siguiente var
                        }
                        addressValue = symbolTable.getAddress(symbol); // en caso de ver un símbolo que si esta en la tabla, buscamos su valor
                    }    
                        String binaryString = Integer.toBinaryString(addressValue); //se convierte el numero en un binario
                        String formattedBinary = String.format("%16s", binaryString).replace(' ', '0');  //rellena con ceros a la izquierda para lograr los 16 bits
                        if (primeraLinea==false) {
                            writer.print("\n");
                        }
                        writer.print(formattedBinary); //se escribe la instrucción ya final en el archivo
                        primeraLinea = false;

                // caso de instrucción tipo c
                }else if (parserPasada2.instructionType() == Parser.CommandType.C_INSTRUCTION) {
                    
                    //recortamos las 3 partes de una instruccion tipo C y las tarducimos a binario
                    String destBinary = Code.dest(parserPasada2.dest());
                    String compBinary = Code.comp(parserPasada2.comp());
                    String jumpBinary = Code.jump(parserPasada2.jump());

                    //se forma la instruccion completa que siempre comienza con 111
                    String cInstruction = "111" + compBinary + destBinary + jumpBinary;
                    if (!primeraLinea) {
                        writer.print("\n");
                    }
                    writer.print(cInstruction);
                    primeraLinea = false;
                }
            }

            writer.close();
            System.out.println("Archivo.hack generado: " + outputFilePath);
        } catch (IOException e) {
            System.out.println("Error procesando el archivo: " + e.getMessage());
            System.exit(1);
        }
    }
    //formate de error
    private static void reportError(int lineNumber, String message, String outputFilePath) {
        //para mantener un formato sin importar la consola
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.println("ERROR *** Línea " + lineNumber + ": " + message); //asegura la tilde y el formato exacto
        new File(outputFilePath).delete(); // por si quedo un .hack de una ejecucion anterior
        System.exit(1);
    }
    
}
