/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lexer;

import errors.CompilerError;
import errors.ErrorHandler;
import java.util.ArrayList;
import java.util.List;

public class LexerV2 {
    private String code;
    private int i; 
    private int ligne;
    private int colonne;
    private List<Token> tokens;
    private ErrorHandler errorHandler;
    private List<Integer> niveauxIndent; 
    private boolean debutLigne; 
    
    public LexerV2(String code, ErrorHandler errorHandler) {
        this.code = code + '\0'; 
        this.i = 0;
        this.ligne = 1;
        this.colonne = 1;
        this.tokens = new ArrayList<>();
        this.errorHandler = errorHandler;
        this.niveauxIndent = new ArrayList<>();
        this.niveauxIndent.add(0); 
        this.debutLigne = true;
    }
    
    public List<Token> tokenize() {
        
        while (code.charAt(i) != '\0') {
            
            // GESTION INDENTATION AMELIOREE
            if (debutLigne) {
                // On mange les espaces de debut de ligne
                int espaceCount = 0;
                int saveI = i;
                int saveCol = colonne;
                
                // Compter les espaces sans avancer definitivement (lookahead)
                while (code.charAt(saveI) == ' ' || code.charAt(saveI) == '\t') {
                    if (code.charAt(saveI) == ' ') espaceCount++;
                    else espaceCount += 4;
                    saveI++;
                }
                
                // Si la ligne est vide ou un commentaire, ON NE GENERE PAS D'INDENT
                if (code.charAt(saveI) == '\n' || code.charAt(saveI) == '#' || code.charAt(saveI) == '\r') {
                    // On ne fait rien, on laisse la boucle principale gerer le saut de ligne
                    debutLigne = false; 
                } 
                else {
                    // C'est une vraie ligne de code, on valide l'indentation
                    debutLigne = false;
                    colonne = colonne + espaceCount; // Mise a jour colonne reelle
                    i = saveI; // On avance le curseur
                    
                    int niveauPrec = niveauxIndent.get(niveauxIndent.size() - 1);
                    
                    if (espaceCount > niveauPrec) {
                        niveauxIndent.add(espaceCount);
                        tokens.add(new Token(TypeToken.INDENT, "", ligne, 1));
                    } else if (espaceCount < niveauPrec) {
                        while (niveauxIndent.size() > 0 && niveauxIndent.get(niveauxIndent.size() - 1) > espaceCount) {
                            niveauxIndent.remove(niveauxIndent.size() - 1);
                            tokens.add(new Token(TypeToken.DEDENT, "", ligne, 1));
                        }
                        if (niveauxIndent.get(niveauxIndent.size() - 1) != espaceCount) {
                            errorHandler.addError(new CompilerError(CompilerError.ErrorType.LEXICAL, "Erreur Indentation", ligne, 1));
                        }
                    }
                }
            }
            
            if (code.charAt(i) == '\0') break;

            // Analyse standard
            char c = code.charAt(i);
            
            if (c == ' ' || c == '\t' || c == '\r') {
                if (c == ' ') colonne++;
                else if (c == '\t') colonne += 4;
                i++;
            }
            else if (c == '\n') {
                tokens.add(new Token(TypeToken.NEWLINE, "\\n", ligne, colonne));
                ligne++;
                colonne = 1;
                i++;
                debutLigne = true;
            }
            else if (c == '#') {
                while (code.charAt(i) != '\n' && code.charAt(i) != '\0') {
                    i++;
                }
            }
            else if (Character.isDigit(c)) {
                int debutC = colonne;
                String num = "";
                while (Character.isDigit(code.charAt(i)) || code.charAt(i) == '.') {
                    num += code.charAt(i);
                    i++; colonne++;
                }
                tokens.add(new Token(TypeToken.NUMBER, num, ligne, debutC));
            }
            else if (c == '"' || c == '\'') {
                char quote = c;
                int debutC = colonne;
                String str = "";
                i++; colonne++;
                while (code.charAt(i) != quote && code.charAt(i) != '\0') {
                    str += code.charAt(i);
                    i++; colonne++;
                }
                if (code.charAt(i) == quote) { i++; colonne++; }
                tokens.add(new Token(TypeToken.STRING, str, ligne, debutC));
            }
            else if (Character.isLetter(c) || c == '_') {
                int debutC = colonne;
                String id = "";
                while (Character.isLetterOrDigit(code.charAt(i)) || code.charAt(i) == '_') {
                    id += code.charAt(i);
                    i++; colonne++;
                }
                // Mots cles
                if (id.equals("try")) tokens.add(new Token(TypeToken.TRY, id, ligne, debutC));
                else if (id.equals("except")) tokens.add(new Token(TypeToken.EXCEPT, id, ligne, debutC));
                else if (id.equals("finally")) tokens.add(new Token(TypeToken.FINALLY, id, ligne, debutC));
                else if (id.equals("raise")) tokens.add(new Token(TypeToken.RAISE, id, ligne, debutC));
                else if (id.equals("if")) tokens.add(new Token(TypeToken.IF, id, ligne, debutC));
                else if (id.equals("else")) tokens.add(new Token(TypeToken.ELSE, id, ligne, debutC));
                else if (id.equals("elif")) tokens.add(new Token(TypeToken.ELIF, id, ligne, debutC));
                else if (id.equals("while")) tokens.add(new Token(TypeToken.WHILE, id, ligne, debutC));
                else if (id.equals("for")) tokens.add(new Token(TypeToken.FOR, id, ligne, debutC));
                else if (id.equals("in")) tokens.add(new Token(TypeToken.IN, id, ligne, debutC));
                else if (id.equals("def")) tokens.add(new Token(TypeToken.DEF, id, ligne, debutC));
                else if (id.equals("return")) tokens.add(new Token(TypeToken.RETURN, id, ligne, debutC));
                else if (id.equals("class")) tokens.add(new Token(TypeToken.CLASS, id, ligne, debutC));
                else if (id.equals("print")) tokens.add(new Token(TypeToken.PRINT, id, ligne, debutC));
                else if (id.equals("pass")) tokens.add(new Token(TypeToken.PASS, id, ligne, debutC));
                else if (id.equals("break")) tokens.add(new Token(TypeToken.BREAK, id, ligne, debutC));
                else if (id.equals("continue")) tokens.add(new Token(TypeToken.CONTINUE, id, ligne, debutC));
                else if (id.equals("as")) tokens.add(new Token(TypeToken.AS, id, ligne, debutC));
                else if (id.equals("True")) tokens.add(new Token(TypeToken.TRUE, id, ligne, debutC));
                else if (id.equals("False")) tokens.add(new Token(TypeToken.FALSE, id, ligne, debutC));
                else if (id.equals("None")) tokens.add(new Token(TypeToken.NONE, id, ligne, debutC));
                else if (id.equals("and")) tokens.add(new Token(TypeToken.AND, id, ligne, debutC));
                else if (id.equals("or")) tokens.add(new Token(TypeToken.OR, id, ligne, debutC));
                else if (id.equals("not")) tokens.add(new Token(TypeToken.NOT, id, ligne, debutC));
                else if (id.equalsIgnoreCase("taguelmimt")) tokens.add(new Token(TypeToken.TAGUELMIMT, id, ligne, debutC));
                else if (id.equalsIgnoreCase("badis")) tokens.add(new Token(TypeToken.BADIS, id, ligne, debutC));
                else tokens.add(new Token(TypeToken.IDENTIFIER, id, ligne, debutC));
            }
            else {
                // Operateurs simples
                int debutC = colonne;
                if (c == '=') {
                    i++; colonne++;
                    if (code.charAt(i) == '=') { tokens.add(new Token(TypeToken.EQUAL, "==", ligne, debutC)); i++; colonne++; }
                    else tokens.add(new Token(TypeToken.ASSIGN, "=", ligne, debutC));
                }
                else if (c == '+') {
                    i++; colonne++;
                    if (code.charAt(i) == '=') { tokens.add(new Token(TypeToken.PLUS_ASSIGN, "+=", ligne, debutC)); i++; colonne++; }
                    else tokens.add(new Token(TypeToken.PLUS, "+", ligne, debutC));
                }
                else if (c == '-') {
                    i++; colonne++;
                    if (code.charAt(i) == '=') { tokens.add(new Token(TypeToken.MINUS_ASSIGN, "-=", ligne, debutC)); i++; colonne++; }
                    else tokens.add(new Token(TypeToken.MINUS, "-", ligne, debutC));
                }
                else if (c == '*') {
                    i++; colonne++;
                    if (code.charAt(i) == '*') { tokens.add(new Token(TypeToken.POWER, "**", ligne, debutC)); i++; colonne++; }
                    else tokens.add(new Token(TypeToken.MULTIPLY, "*", ligne, debutC));
                }
                else if (c == '/') { tokens.add(new Token(TypeToken.DIVIDE, "/", ligne, colonne)); i++; colonne++; }
                else if (c == '<') { 
                    i++; colonne++;
                    if (code.charAt(i) == '=') { tokens.add(new Token(TypeToken.LESS_EQUAL, "<=", ligne, debutC)); i++; colonne++; }
                    else tokens.add(new Token(TypeToken.LESS_THAN, "<", ligne, debutC)); 
                }
                else if (c == '>') { 
                    i++; colonne++;
                    if (code.charAt(i) == '=') { tokens.add(new Token(TypeToken.GREATER_EQUAL, ">=", ligne, debutC)); i++; colonne++; }
                    else tokens.add(new Token(TypeToken.GREATER_THAN, ">", ligne, debutC)); 
                }
                else if (c == '(') { tokens.add(new Token(TypeToken.LPAREN, "(", ligne, colonne)); i++; colonne++; }
                else if (c == ')') { tokens.add(new Token(TypeToken.RPAREN, ")", ligne, colonne)); i++; colonne++; }
                else if (c == ':') { tokens.add(new Token(TypeToken.COLON, ":", ligne, colonne)); i++; colonne++; }
                else if (c == ',') { tokens.add(new Token(TypeToken.COMMA, ",", ligne, colonne)); i++; colonne++; }
                else if (c == '.') { tokens.add(new Token(TypeToken.DOT, ".", ligne, colonne)); i++; colonne++; }
                else {
                    errorHandler.addError(new CompilerError(CompilerError.ErrorType.LEXICAL, "Inconnu: " + c, ligne, colonne));
                    i++; colonne++;
                }
            }
        }
        
        while (niveauxIndent.size() > 1) {
            niveauxIndent.remove(niveauxIndent.size() - 1);
            tokens.add(new Token(TypeToken.DEDENT, "", ligne, colonne));
        }
        tokens.add(new Token(TypeToken.EOF, "", ligne, colonne));
        
        return tokens;
    }
}