/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lexer;

public class Token {
    private TypeToken type;
    private String value;
    private int line;
    private int column;
    
    public Token(TypeToken type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }
    
    public TypeToken getType() {
        return type;
    }
    
    public String getValue() {
        return value;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
    
    @Override
    public String toString() {
        return "Token [type=" + type + ", value=" + value + ", ligne=" + line + "]";
    }
    
    // Methode simple pour l'affichage console
    public String toDisplayString() {
        return "Type: " + type + " | Valeur: " + value + " (Ligne " + line + ")";
    }
}