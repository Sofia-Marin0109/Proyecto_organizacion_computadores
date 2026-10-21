package HackAssembler.src;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class HackAssembler {
    public static void main(String[] args) {
        //Rutas del archivo a leer y a escribir
        String inputFilePath = "HackAssembler/tests/pong/pongL.asm"; 
        String outputFilePath = "HackAssembler/tests/pong/pongL.hack";
        
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

                //cuando es etiqueta se guarda el simbolo con la dirección de memoria siguiente
                if (parserPasada1.instructionType() == Parser.CommandType.LABEL) {
                    String label = parserPasada1.symbol();
                    symbolTable.addEntry(label, romAddress);
                } else {
                    romAddress++; //solo se suma para instrucciones tipo A o C, los labels no ocupan espacio 
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
        }
    }    
}
