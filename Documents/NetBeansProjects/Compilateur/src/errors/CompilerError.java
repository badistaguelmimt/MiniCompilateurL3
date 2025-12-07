/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package errors;

public class CompilerError {
    public enum ErrorType {
        LEXICAL,
        SYNTAXIC
    }
    
    private ErrorType type;
    private String message;
    private int line;
    private int column;
    
    public CompilerError(ErrorType type, String message, int line, int column) {
        this.type = type;
        this.message = message;
        this.line = line;
        this.column = column;
    }
    
    public ErrorType getType() {
        return type;
    }
    
    @Override
    public String toString() {
        String prefixe = "";
        
        // Pas d'operateur ternaire, utilisation de if/else simple
        if (type == ErrorType.LEXICAL) {
            prefixe = "ERREUR LEXICALE";
        } else {
            prefixe = "ERREUR SYNTAXIQUE";
        }
        
        return prefixe + " ligne " + line + ", colonne " + column + " : " + message;
    }
}