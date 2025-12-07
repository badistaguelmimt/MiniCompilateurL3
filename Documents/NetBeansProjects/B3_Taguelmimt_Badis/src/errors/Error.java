/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package errors;

public class Error {
    public enum ErrorType {
        LEXICAL,
        SYNTAXIC
    }
    
    private ErrorType type;
    private String message;
    private int ligne;
    private int colonne;
    
    public Error(ErrorType type, String message, int ligne, int colonne) {
        this.type = type;
        this.message = message;
        this.ligne = ligne;
        this.colonne = colonne;
    }
    
    public ErrorType getType() {
        return type;
    }
    
    @Override
    public String toString() {
        String x = "";
        
        if (type == ErrorType.LEXICAL) {
            x = "[ERREUR LEXICALE]";
        } else {
            x = "[ERREUR SYNTAXIQUE]";
        }
        
        return x + " ligne " + ligne + ", colonne " + colonne + " -> " + message;
    }
}