package desensamblador;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;                 
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;   
import java.util.ArrayList;                 
import java.util.HashMap;                  
import java.util.List;     
import java.util.Map;    
import java.util.Scanner;
import java.util.TreeSet;  

public class HackDisassembler {

    public static void main(String[] args) {
        
        if (args.length != 1) {
            System.out.println("Uso: java HackDisassembler.src.HackDisassembler <archivo.hack>");
            System.exit(1);
        }

        // entra un .hack y sale un .asm
        String inputFilePath = args[0];

        //la salida va en la misma carpeta solo que con un cambio de nombre: Prog.hack -> ProgDis.asm
        String outputFilePath;
        if (inputFilePath.endsWith(".hack")) {
            outputFilePath = inputFilePath.substring(0, inputFilePath.length() - 5) + "Dis.asm";
        } else {
            outputFilePath = inputFilePath + "Dis.asm";
        }

        try {
            File inputFile = new File(inputFilePath);
            Scanner scanner = new Scanner(inputFile);

            List<String> instructions = new ArrayList<String>();
            int lineNumber = 0;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineNumber++;

                try {
                    validateLine(line);
                } catch (IllegalArgumentException e) {
                    reportError(lineNumber, e.getMessage(), outputFilePath);
                } catch (Exception e) { // [NUEVO] Atrapa cualquier otro error inesperado de Java
                    reportError(lineNumber, e.getMessage(), outputFilePath);
                }

                instructions.add(line);
            }
            scanner.close();
                
            int n = instructions.size();
            
            //buscar a que dirección salta un el código, para reescribir las labels
            TreeSet<Integer> targets = new TreeSet<Integer>(); // direcciones destino, sin repetir y ordenadas
            for (int i = 0; i < n; i++) {
                if (isJumpTarget(instructions, i)) {
                    targets.add(Integer.parseInt(instructions.get(i).substring(1), 2));
                }
            }

            Map<Integer, String> labels = new HashMap<>();
            int labelCount = 0;
            for (int address : targets) {
                labels.put(address, "L" + labelCount);
                labelCount++;
            }

            List<String> output = new ArrayList<String>(); // escribe las lineas del .asm, en orden

            for (int i = 0; i < n; i++) {
                String line = instructions.get(i);

                // si alguna instruccion salta aqui, la etiqueta se declara justo antes de la instruccion i
                if (labels.containsKey(i)) {
                    output.add("(" + labels.get(i) + ")");
                }
                
                // INSTRUCCIÓN TIPO A, empieza con 0 
                if (line.startsWith("0")) {
                    // se toman los 15 bits despues del 0 (desde la posición 1 hasta el final)
                    String binaryValue = line.substring(1);
                    
                    // convertimos ese binario a un número entero decimal (base 2)
                    int decimalValue = Integer.parseInt(binaryValue, 2);
                    
                    // agregamos a la instruccion el @
                    if (isJumpTarget(instructions, i)) { // [NUEVO] va antes de un salto: se escribe con etiqueta
                        output.add("@" + labels.get(decimalValue));
                    } else {
                        output.add("@" + decimalValue); // [CAMBIO] antes se escribia directo al archivo con println
                    }
                } 

                // INSTRUCCIÓN TIPO C, empieza con 1
                if (line.startsWith("1")) {
                    // la instrucción en .asm siempre es: 1 1 1 a c c c c c c d d d j j j
                    // recortamos cada bloque exacto de bits binarios:
                    
                    // comp son 7 bits que van desde la posición 3 hasta la 10
                    String compBin = line.substring(3, 10);
                    // dest son 3 bits que van desde la posición 10 hasta la 13
                    String destBin = line.substring(10, 13);
                    // jump son 3 bits finales, desde la posición 13 hasta la 16
                    String jumpBin = line.substring(13, 16);

                    // se traduce cada bloque de binario a texto usando ReverseCode
                    String compText = ReverseCode.comp(compBin);
                    String destText = ReverseCode.dest(destBin);
                    String jumpText = ReverseCode.jump(jumpBin);

                    // se reconstruye la instrucción juntando todo
                    String finalInstruction = compText; // siempre hay comp

                    if (!destText.isEmpty()) {
                        // si hay destino, se lo pegamos al inicio con un =
                        finalInstruction = destText + "=" + finalInstruction;
                    }
                    
                    if (!jumpText.isEmpty()) {
                        // si hay salto, se lo pegamos al final con un ;
                        finalInstruction = finalInstruction + ";" + jumpText;
                    }
                    output.add(finalInstruction);
                }
            }
            if (labels.containsKey(n)) {
                output.add("(" + labels.get(n) + ")");
            }

            PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath));
            boolean primeraLinea = true;
            for (String outputLine : output) {
                if (!primeraLinea) {
                    writer.print("\n");
                }
                writer.print(outputLine);
                primeraLinea = false;
            }
            writer.close();
            System.out.println("Archivo .asm reconstruido en: " + outputFilePath);

        } catch (IOException e) {
            System.out.println("Error procesando el archivo: " + e.getMessage());
            System.exit(1); 
        }
    }

    private static void validateLine(String line) {
        // longitud: exactamente 16 (sin trim, un espacio al final tambien es error)
        if (line.length() != 16) {
            throw new IllegalArgumentException("longitud inválida: tiene " + line.length() + " caracteres y deben ser 16");
        }

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c != '0' && c != '1') {
                throw new IllegalArgumentException("carácter inválido '" + c + "' en la posición " + (i + 1));
            }
        }

        if (line.startsWith("1")) {
            if (!line.substring(1, 3).equals("11")) {
                throw new IllegalArgumentException("los bits 14 y 13 de una instrucción C deben ser 11");
            }
            ReverseCode.comp(line.substring(3, 10)); // solo para validar: lanza error si el codigo no existe
            ReverseCode.dest(line.substring(10, 13)); // [NUEVO] validar el destino también aquí
            ReverseCode.jump(line.substring(13, 16)); // [NUEVO] validar el salto también aquí
        }
    }
    
    private static boolean isJumpTarget(List<String> instructions, int i) {
        int n = instructions.size();
        if (i + 1 >= n) {
            return false; // no hay instruccion siguiente
        }

        String current = instructions.get(i);
        String next = instructions.get(i + 1);

        boolean currentIsA = current.startsWith("0");
        boolean nextIsCWithJump = next.startsWith("1") && !next.substring(13, 16).equals("000");

        if (!currentIsA || !nextIsCWithJump) {
            return false;
        }

        int value = Integer.parseInt(current.substring(1), 2);
        return value <= n;
    }

    private static void reportError(int lineNumber, String message, String outputFilePath) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.println("ERROR *** L\u00EDnea " + lineNumber + ": " + message);
        new File(outputFilePath).delete(); // por si quedo un .asm de una ejecucion anterior
        System.exit(1);
    }
}