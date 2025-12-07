/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lexer;

public class Token {
    private TypeToken type;
    private String valeur;
    private int ligne;
    private int colonne;
    
    public Token(TypeToken type, String valeur, int ligne, int colonne) {
        this.type = type;
        this.valeur = valeur;
        this.ligne = ligne;
        this.colonne = colonne;
    }
    
    public TypeToken getType() {
        return type; 
    }
    
    public String getValeur() {
        return valeur;
    }
    
    public int getLigne() {
        return ligne;
    }
    
    public int getColonne() {
        return colonne;
    }
    
    @Override
    public String toString() {
        return "Token [type=" + type + ", value=" + valeur + ", ligne=" + ligne + ", colonne=" + colonne + "]";
    }
}