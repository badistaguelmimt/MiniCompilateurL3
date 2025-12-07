/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package errors;

import java.util.ArrayList;
import java.util.List;

public class ErrorHandler {
    private List<CompilerError> errors;
    private boolean hasErrors;
    
    public ErrorHandler() {
        this.errors = new ArrayList<>();
        this.hasErrors = false;
    }
    
    public void addError(CompilerError error) {
        errors.add(error);
        hasErrors = true;
    }
    
    public boolean hasErrors() {
        return hasErrors;
    }
    
    public List<CompilerError> getErrors() {
        return errors;
    }
    
    // Methode pour afficher le resume sans accents
    public void printSummary() {
        if (hasErrors == false) {
            System.out.println("Aucune erreur detectee.");
        } else {
            System.out.println("Nombre d'erreurs : " + errors.size());
            
            // Utilisation d'une boucle for classique avec index
            for (int i = 0; i < errors.size(); i++) {
                CompilerError err = errors.get(i);
                System.out.println(" - " + err.toString());
            }
        }
    }
}