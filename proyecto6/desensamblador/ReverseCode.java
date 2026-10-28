package desensamblador;

//recibe instrucciones de ceros y unos y los devuelve en texto asm
public class ReverseCode {
    // dest: de 3 bits de binario a texto
    public static String dest(String binary) {
        switch (binary) {
            case "000": return ""; // null
            case "001": return "M";
            case "010": return "D";
            case "011": return "MD";
            case "100": return "A";
            case "101": return "AM";
            case "110": return "AD";
            case "111": return "AMD";
            default: throw new IllegalArgumentException("destino binario desconocido: " + binary);
        }
    }

    // comp: de 7 bits de binario a texto
    public static String comp(String binary) {
        switch (binary) {
            // Cuando 'a' es 0 (empiezan con 0)
            case "0101010": return "0";
            case "0111111": return "1";
            case "0111010": return "-1";
            case "0001100": return "D";
            case "0110000": return "A";
            case "0001101": return "!D";
            case "0110001": return "!A";
            case "0001111": return "-D";
            case "0110011": return "-A";
            case "0011111": return "D+1";
            case "0110111": return "A+1";
            case "0001110": return "D-1";
            case "0110010": return "A-1";
            case "0000010": return "D+A";
            case "0010011": return "D-A";
            case "0000111": return "A-D";
            case "0000000": return "D&A";
            case "0010101": return "D|A";
            
            // Cuando 'a' es 1 (empiezan con 1)
            case "1110000": return "M";
            case "1110001": return "!M";
            case "1110011": return "-M";
            case "1110111": return "M+1";
            case "1110010": return "M-1";
            case "1000010": return "D+M";
            case "1010011": return "D-M";
            case "1000111": return "M-D";
            case "1000000": return "D&M";
            case "1010101": return "D|M";
            
            default: throw new IllegalArgumentException("código de cómputo inválido '" + binary + "'");
        }
    }

    // jump: de 3 bits de binario a texto
    public static String jump(String binary) {
        switch (binary) {
            case "000": return ""; // null
            case "001": return "JGT";
            case "010": return "JEQ";
            case "011": return "JGE";
            case "100": return "JLT";
            case "101": return "JNE";
            case "110": return "JLE";
            case "111": return "JMP";
            default: throw new IllegalArgumentException("salto binario desconocido: " + binary);
        }
    }
}
