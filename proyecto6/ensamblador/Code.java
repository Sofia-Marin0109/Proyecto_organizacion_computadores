package ensamblador;

public class Code {

    // Método dest: traduce el simbolo predefinido (A,M,D, etc) de destino a 3 bits
    public static String dest(String predefsymbol) {
        if (predefsymbol == null) {
            return "000"; // cuando es null la var debe estar vacia porque no habia igual
        }
        if (predefsymbol.isEmpty()) {
            throw new IllegalArgumentException("destino vacío");
        }
        
        switch (predefsymbol) { //identifica el simbolo predefinido y devuelve su binario 
            case "M":   return "001"; // Guarda en RAM[A]
            case "D":   return "010"; // Guarda en registro D
            case "MD":  return "011"; // Hack acepta MD o DM
            case "A":   return "100"; // Guarda en registro A
            case "AM":  return "101"; // Guarda en A y RAM[A]
            case "AD":  return "110"; // Guarda en A y D
            case "ADM": return "111"; // Guarda en A, D y RAM[A]
            default:    throw new IllegalArgumentException("destino inválido '" + predefsymbol + "'"); 
        }
    }

    // Método comp: traduce el simbolo predefinido o numero a 7 bits
    public static String comp(String predefsymbol) {
        // tiene el bit 'a' y 6 bits de 'c'
        // retorno: "a c1 c2 c3 c4 c5 c6"
        if(predefsymbol.isEmpty()){
            throw new IllegalArgumentException("comp vacío");
        }
        switch (predefsymbol) {
            // si se usa D o A, a==0
            case "0":   return "0101010";
            case "1":   return "0111111";
            case "-1":  return "0111010";
            case "D":   return "0001100";
            case "A":   return "0110000";
            case "!D":  return "0001101";
            case "!A":  return "0110001";
            case "-D":  return "0001111";
            case "-A":  return "0110011";
            case "D+1": return "0011111";
            case "A+1": return "0110111";
            case "D-1": return "0001110";
            case "A-1": return "0110010";
            case "D+A": return "0000010";
            case "D-A": return "0010011";
            case "A-D": return "0000111";
            case "D&A": return "0000000";
            case "D|A": return "0010101";
            
            // si es M es a==1
            case "M":   return "1110000";
            case "!M":  return "1110001";
            case "-M":  return "1110011";
            case "M+1": return "1110111";
            case "M-1": return "1110010";
            case "D+M": return "1000010";
            case "D-M": return "1010011";
            case "M-D": return "1000111";
            case "D&M": return "1000000";
            case "D|M": return "1010101";
            
            default: throw new IllegalArgumentException("Comp inválido '" + predefsymbol + "'"); // por si
        }
    }

    // Método jump: traduce el simbolo predefinido de jump a 3 bits
    public static String jump(String predefsymbol) {
        if (predefsymbol ==null){
            return "000";
        }
        if (predefsymbol.isEmpty()) {
            throw new IllegalArgumentException("Salto vacío");
        }
        
        switch (predefsymbol) {
            case "JGT": return "001"; // Salta si > 0
            case "JEQ": return "010"; // Salta si = 0
            case "JGE": return "011"; // Salta si >= 0
            case "JLT": return "100"; // Salta si < 0
            case "JNE": return "101"; // Salta si != 0
            case "JLE": return "110"; // Salta si <= 0
            case "JMP": return "111"; // Salto incondicional
            default: throw new IllegalArgumentException("salto inválido '" + predefsymbol + "'");  
        }
    }
}