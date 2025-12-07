/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lexer;

/**
 *
 * @author USER
 */
import errors.CompilerError;
import errors.ErrorHandler;
import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private String code;
    private int i; 
    private int ligne;
    private int colonne;
    private List<Token> tokens;
    private ErrorHandler errorHandler;
    
    public Lexer(String code, ErrorHandler errorHandler) {
        this.code = code + '\0'; 
        this.i = 0;
        this.ligne = 1;
        this.colonne = 1;
        this.tokens = new ArrayList<>();
        this.errorHandler = errorHandler;
    }
    
    public List<Token> tokenize() {
        
        while (code.charAt(i) != '\0') {
            
            // pour ignorer les espaces
            if (code.charAt(i) == ' ' || code.charAt(i) == '\t' || code.charAt(i) == '\r') {
                colonne++;
                i++;
            }
            // pour une nouvelle ligne
            else if (code.charAt(i) == '\n') {
                //je pourrai ajouter une condition pour qu'il n y ait pas d'autre NEWLINE if(!hasNEWLINE)
                tokens.add(new Token(TypeToken.NEWLINE, "\\n", ligne, colonne));
                ligne++;
                colonne = 1;
                i++;
            }
            // pour un commentaire #
            else if (code.charAt(i) == '#') {
                while (code.charAt(i) != '\n' && code.charAt(i) != '\0') {
                    i++;
                }
            }
            // Nombres (0-9)
            else if (code.charAt(i) >= '0' && code.charAt(i) <= '9') {
                int debutLigne = ligne;
                int debutCol = colonne;
                String nombre = "";
                
                while ((code.charAt(i) >= '0' && code.charAt(i) <= '9') || code.charAt(i) == '.') {
                    nombre = nombre + code.charAt(i);
                    colonne++;
                    i++;
                }
                
                tokens.add(new Token(TypeToken.NUMBER, nombre, debutLigne, debutCol)); 
            }
            // Chaînes "..."
            else if (code.charAt(i) == '"') {
                int debutLigne = ligne;
                int debutCol = colonne;
                String str = "";
                i++; // sauter "
                colonne++;
                
                while (code.charAt(i) != '"' && code.charAt(i) != '\0') {
                    str = str + code.charAt(i);
                    colonne++;
                    i++;
                }
                
                if (code.charAt(i) == '"') {
                    i++; // sauter "
                    colonne++;
                }
                
                tokens.add(new Token(TypeToken.STRING, str, debutLigne, debutCol));
            }
            // Identifiants et mots-clés
            else if ((code.charAt(i) >= 'a' && code.charAt(i) <= 'z') || 
                     (code.charAt(i) >= 'A' && code.charAt(i) <= 'Z') || 
                     code.charAt(i) == '_') {
                
                int debutLigne = ligne;
                int debutCol = colonne;
                String id = "";
                
                while ((code.charAt(i) >= 'a' && code.charAt(i) <= 'z') || 
                       (code.charAt(i) >= 'A' && code.charAt(i) <= 'Z') || 
                       (code.charAt(i) >= '0' && code.charAt(i) <= '9') || 
                       code.charAt(i) == '_') {
                    id = id + code.charAt(i);
                    colonne++;
                    i++;
                }
                
                // Vérifier les mots-clés un par un
                if (id.equals("try")) {
                    tokens.add(new Token(TypeToken.TRY, id, debutLigne, debutCol));
                } else if (id.equals("except")) {
                    tokens.add(new Token(TypeToken.EXCEPT, id, debutLigne, debutCol));
                } else if (id.equals("finally")) {
                    tokens.add(new Token(TypeToken.FINALLY, id, debutLigne, debutCol));
                } else if (id.equals("raise")) {
                    tokens.add(new Token(TypeToken.RAISE, id, debutLigne, debutCol));
                } else if (id.equals("if")) {
                    tokens.add(new Token(TypeToken.IF, id, debutLigne, debutCol));
                } else if (id.equals("elif")) {
                    tokens.add(new Token(TypeToken.ELIF, id, debutLigne, debutCol));
                } else if (id.equals("else")) {
                    tokens.add(new Token(TypeToken.ELSE, id, debutLigne, debutCol));
                } else if (id.equals("while")) {
                    tokens.add(new Token(TypeToken.WHILE, id, debutLigne, debutCol));
                } else if (id.equals("for")) {
                    tokens.add(new Token(TypeToken.FOR, id, debutLigne, debutCol));
                } else if (id.equals("in")) {
                    tokens.add(new Token(TypeToken.IN, id, debutLigne, debutCol));
                } else if (id.equals("break")) {
                    tokens.add(new Token(TypeToken.BREAK, id, debutLigne, debutCol));
                } else if (id.equals("continue")) {
                    tokens.add(new Token(TypeToken.CONTINUE, id, debutLigne, debutCol));
                } else if (id.equals("def")) {
                    tokens.add(new Token(TypeToken.DEF, id, debutLigne, debutCol));
                } else if (id.equals("return")) {
                    tokens.add(new Token(TypeToken.RETURN, id, debutLigne, debutCol));
                } else if (id.equals("pass")) {
                    tokens.add(new Token(TypeToken.PASS, id, debutLigne, debutCol));
                } else if (id.equals("class")) {
                    tokens.add(new Token(TypeToken.CLASS, id, debutLigne, debutCol));
                } else if (id.equals("as")) {
                    tokens.add(new Token(TypeToken.AS, id, debutLigne, debutCol));
                } else if (id.equals("and")) {
                    tokens.add(new Token(TypeToken.AND, id, debutLigne, debutCol));
                } else if (id.equals("or")) {
                    tokens.add(new Token(TypeToken.OR, id, debutLigne, debutCol));
                } else if (id.equals("not")) {
                    tokens.add(new Token(TypeToken.NOT, id, debutLigne, debutCol));
                } else if (id.equals("True")) {
                    tokens.add(new Token(TypeToken.TRUE, id, debutLigne, debutCol));
                } else if (id.equals("False")) {
                    tokens.add(new Token(TypeToken.FALSE, id, debutLigne, debutCol));
                } else if (id.equals("None")) {
                    tokens.add(new Token(TypeToken.NONE, id, debutLigne, debutCol));
                }
                // REMPLACER PAR VOTRE NOM ET PRÉNOM
                else if (id.equals("VOTRE_NOM")) {
                    tokens.add(new Token(TypeToken.TAGUELMIMT, id, debutLigne, debutCol));
                } else if (id.equals("VOTRE_PRENOM")) {
                    tokens.add(new Token(TypeToken.BADIS, id, debutLigne, debutCol));
                } else {
                    tokens.add(new Token(TypeToken.IDENTIFIER, id, debutLigne, debutCol));
                }
            }
            // Opérateurs et délimiteurs
            else if (code.charAt(i) == '+') {
                int debutCol = colonne;
                i++;
                colonne++;
                if (code.charAt(i) == '+') {
                    tokens.add(new Token(TypeToken.INCREMENT, "++", ligne, debutCol));
                    i++;
                    colonne++;
                } else if (code.charAt(i) == '=') {
                    tokens.add(new Token(TypeToken.PLUS_ASSIGN, "+=", ligne, debutCol));
                    i++;
                    colonne++;
                } else {
                    tokens.add(new Token(TypeToken.PLUS, "+", ligne, debutCol));
                }
            }
            else if (code.charAt(i) == '-') {
                int debutCol = colonne;
                i++;
                colonne++;
                if (code.charAt(i) == '-') {
                    tokens.add(new Token(TypeToken.DECREMENT, "--", ligne, debutCol));
                    i++;
                    colonne++;
                } else if (code.charAt(i) == '=') {
                    tokens.add(new Token(TypeToken.MINUS_ASSIGN, "-=", ligne, debutCol));
                    i++;
                    colonne++;
                } else {
                    tokens.add(new Token(TypeToken.MINUS, "-", ligne, debutCol));
                }
            }
            else if (code.charAt(i) == '*') {
                int debutCol = colonne;
                i++;
                colonne++;
                if (code.charAt(i) == '*') {
                    tokens.add(new Token(TypeToken.POWER, "**", ligne, debutCol));
                    i++;
                    colonne++;
                } else {
                    tokens.add(new Token(TypeToken.MULTIPLY, "*", ligne, debutCol));
                }
            }
            else if (code.charAt(i) == '/') {
                tokens.add(new Token(TypeToken.DIVIDE, "/", ligne, colonne));
                i++;
                colonne++;
            }
            else if (code.charAt(i) == '%') {
                tokens.add(new Token(TypeToken.MODULO, "%", ligne, colonne));
                i++;
                colonne++;
            }
            else if (code.charAt(i) == '=') {
                int debutCol = colonne;
                i++;
                colonne++;
                if (code.charAt(i) == '=') {
                    tokens.add(new Token(TypeToken.EQUAL, "==", ligne, debutCol));
                    i++;
                    colonne++;
                } else {
                    tokens.add(new Token(TypeToken.ASSIGN, "=", ligne, debutCol));
                }
            }
            else if (code.charAt(i) == '!') {
                int debutCol = colonne;
                i++;
                colonne++;
                if (code.charAt(i) == '=') {
                    tokens.add(new Token(TypeToken.NOT_EQUAL, "!=", ligne, debutCol));
                    i++;
                    colonne++;
                } else {
                    errorHandler.addError(new CompilerError(
                        CompilerError.ErrorType.LEXICAL,
                        "Caractère inattendu: '!'",
                        ligne, debutCol
                    ));
                }
            }
            else if (code.charAt(i) == '<') {
                int debutCol = colonne;
                i++;
                colonne++;
                if (code.charAt(i) == '=') {
                    tokens.add(new Token(TypeToken.LESS_EQUAL, "<=", ligne, debutCol));
                    i++;
                    colonne++;
                } else {
                    tokens.add(new Token(TypeToken.LESS_THAN, "<", ligne, debutCol));
                }
            }
            else if (code.charAt(i) == '>') {
                int debutCol = colonne;
                i++;
                colonne++;
                if (code.charAt(i) == '=') {
                    tokens.add(new Token(TypeToken.GREATER_EQUAL, ">=", ligne, debutCol));
                    i++;
                    colonne++;
                } else {
                    tokens.add(new Token(TypeToken.GREATER_THAN, ">", ligne, debutCol));
                }
            }
            else if (code.charAt(i) == '(') {
                tokens.add(new Token(TypeToken.LPAREN, "(", ligne, colonne));
                i++;
                colonne++;
            }
            else if (code.charAt(i) == ')') {
                tokens.add(new Token(TypeToken.RPAREN, ")", ligne, colonne));
                i++;
                colonne++;
            }
            else if (code.charAt(i) == '[') {
                tokens.add(new Token(TypeToken.LBRACKET, "[", ligne, colonne));
                i++;
                colonne++;
            }
            else if (code.charAt(i) == ']') {
                tokens.add(new Token(TypeToken.RBRACKET, "]", ligne, colonne));
                i++;
                colonne++;
            }
            else if (code.charAt(i) == '{') {
                tokens.add(new Token(TypeToken.LBRACE, "{", ligne, colonne));
                i++;
                colonne++;
            }
            else if (code.charAt(i) == '}') {
                tokens.add(new Token(TypeToken.RBRACE, "}", ligne, colonne));
                i++;
                colonne++;
            }
            else if (code.charAt(i) == ',') {
                tokens.add(new Token(TypeToken.COMMA, ",", ligne, colonne));
                i++;
                colonne++;
            }
            else if (code.charAt(i) == ':') {
                tokens.add(new Token(TypeToken.COLON, ":", ligne, colonne));
                i++;
                colonne++;
            }
            else if (code.charAt(i) == ';') {
                tokens.add(new Token(TypeToken.SEMICOLON, ";", ligne, colonne));
                i++;
                colonne++;
            }
            else if (code.charAt(i) == '.') {
                tokens.add(new Token(TypeToken.DOT, ".", ligne, colonne));
                i++;
                colonne++;
            }
            else {
                errorHandler.addError(new CompilerError(
                    CompilerError.ErrorType.LEXICAL,
                    "Caractère inconnu: '" + code.charAt(i) + "'",
                    ligne, colonne
                ));
                i++;
                colonne++;
            }
        }
        
        tokens.add(new Token(TypeToken.EOF, "\0", ligne, colonne));
        
        return tokens;
    }
    
    public void afficherTokens() {
        System.out.println("\n========== LISTE DES TOKENS ==========\n");
        for (Token t : tokens) {
            if (t.getType() != TypeToken.NEWLINE) {
                System.out.println(t.toDisplayString());
            }
        }
        System.out.println("\n======================================\n");
    }
}
