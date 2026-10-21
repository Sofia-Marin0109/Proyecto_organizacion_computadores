package HackDisassembler.src;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class HackDisassembler {

    public static void main(String[] args) {
        // entra un .hack y sale un .asm
        String inputFilePath = "tests/max/Max.hack"; 
        String outputFilePath = "results/max/MaxReconstruido.asm";

        try {
            File inputFile = new File(inputFilePath);
            Scanner scanner = new Scanner(inputFile);
            PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath));
            boolean primeraLinea = true;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                // ignoramos líneas vacías por si
                if (line.isEmpty()) continue;

                // INSTRUCCIÓN TIPO A, empieza con 0
                if (line.startsWith("0")) {
                    // se toman los 15 bits despues del 0 (desde la posición 1 hasta el final)
                    String binaryValue = line.substring(1);
                    
                    // convertimos ese binario a un número entero decimal (base 2)
                    int decimalValue = Integer.parseInt(binaryValue, 2);
                    
                    // agregamos a la instruccion el @
                    writer.println("@" + decimalValue);
                } 
                
                // INSTRUCCIÓN TIPO C, empieza con 1
                else if (line.startsWith("1")) {
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

                    // escribimos la instrucción .asm final
                    if (!primeraLinea) {
                        writer.print("\n");
                    }
                    writer.print(finalInstruction);
                    primeraLinea = false;
                }
            }

            scanner.close();
            writer.close();
            System.out.println("Archivo .asm reconstruido en: " + outputFilePath);

        } catch (IOException e) {
            System.out.println("Error procesando el archivo: " + e.getMessage());
        }
    }
}