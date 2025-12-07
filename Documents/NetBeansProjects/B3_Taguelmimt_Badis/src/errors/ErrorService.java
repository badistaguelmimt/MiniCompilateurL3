/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package errors;

import java.util.ArrayList;
import java.util.List;

public class ErrorService {
    private List<Error> errors;
    private boolean hasErrors;
    
    public ErrorService() {
        this.errors = new ArrayList<>();
        this.hasErrors = false;
    }
    
    public void addError(Error error) {
        errors.add(error);
        hasErrors = true;
    }
    
    public boolean hasErrors() {
        return hasErrors;
    }
    
    public List<Error> getErrors() {
        return errors;
    }
}