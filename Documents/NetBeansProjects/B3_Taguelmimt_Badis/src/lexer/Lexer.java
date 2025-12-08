/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lexer;

import errors.Error;
import errors.ErrorService;
import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private String code;
    private int i;
    private int ligne;
    private int colonne;
    private List<Token> tokens;
    private ErrorService errorService;
    private List<Integer> niveauxIndent;
    private boolean debutLigne;

    public Lexer(String code, ErrorService errorService) {
        this.code = code + '\0';
        this.i = 0;
        this.ligne = 1;
        this.colonne = 1;
        this.tokens = new ArrayList<>();
        this.errorService = errorService;
        this.niveauxIndent = new ArrayList<>();
        this.niveauxIndent.add(0);
        this.debutLigne = true;
    }

    public List<Token> tokenize() {

        while (code.charAt(i) != '\0') {

            if (debutLigne) {
                int espaceCount = 0;
                int saveI = i;

                while (code.charAt(saveI) == ' ' || code.charAt(saveI) == '\t') {
                    if (code.charAt(saveI) == ' ') {
                        espaceCount++;
                    } else {
                        espaceCount += 4;
                    }
                    saveI++;
                }

                if (code.charAt(saveI) == '\n' || code.charAt(saveI) == '#' || code.charAt(saveI) == '\r') {
                    debutLigne = false;
                } else {
                    debutLigne = false;
                    colonne = colonne + espaceCount;
                    i = saveI;

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
                            errorService.addError(new Error(Error.ErrorType.LEXICAL, "Erreur Indentation", ligne, 1));
                        }
                    }
                }
            }

            if (code.charAt(i) == '\0') break;

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
            else if (c >= '0' && c <= '9') {
                int debutC = colonne;
                String num = "";
                while ((code.charAt(i) >= '0' && code.charAt(i) <= '9') || code.charAt(i) == '.') {
                    num += code.charAt(i);
                    i++;
                    colonne++;
                }
                tokens.add(new Token(TypeToken.NUMBER, num, ligne, debutC));
            } 
            else if (c == '"' || c == '\'') {
                char quote = c;
                int debutC = colonne;
                String str = "";
                i++;
                colonne++;
                while (code.charAt(i) != quote && code.charAt(i) != '\0') {
                    str += code.charAt(i);
                    i++;
                    colonne++;
                }
                if (code.charAt(i) == quote) {
                    i++;
                    colonne++;
                }
                tokens.add(new Token(TypeToken.STRING, str, ligne, debutC));
            } 
            else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_') {
                int debutC = colonne;
                String id = "";
                while ((code.charAt(i) >= 'a' && code.charAt(i) <= 'z') ||
                        (code.charAt(i) >= 'A' && code.charAt(i) <= 'Z') ||
                        (code.charAt(i) >= '0' && code.charAt(i) <= '9') ||
                        code.charAt(i) == '_') {
                    id += code.charAt(i);
                    i++;
                    colonne++;
                }

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
                int debutC = colonne;
                if (c == '=') {
                    i++; colonne++;
                    if (code.charAt(i) == '=') {
                        tokens.add(new Token(TypeToken.EQUAL, "==", ligne, debutC));
                        i++; colonne++;
                    } else {
                        tokens.add(new Token(TypeToken.ASSIGN, "=", ligne, debutC));
                    }
                } else if (c == '+') {
                    i++; colonne++;
                    if (code.charAt(i) == '=') {
                        tokens.add(new Token(TypeToken.PLUS_ASSIGN, "+=", ligne, debutC));
                        i++; colonne++;
                    } else {
                        tokens.add(new Token(TypeToken.PLUS, "+", ligne, debutC));
                    }
                } else if (c == '-') {
                    i++; colonne++;
                    if (code.charAt(i) == '=') {
                        tokens.add(new Token(TypeToken.MINUS_ASSIGN, "-=", ligne, debutC));
                        i++; colonne++;
                    } else {
                        tokens.add(new Token(TypeToken.MINUS, "-", ligne, debutC));
                    }
                } else if (c == '*') {
                    i++; colonne++;
                    if (code.charAt(i) == '*') {
                        tokens.add(new Token(TypeToken.POWER, "**", ligne, debutC));
                        i++; colonne++;
                    } else {
                        tokens.add(new Token(TypeToken.MULTIPLY, "*", ligne, debutC));
                    }
                } else if (c == '/') {
                    tokens.add(new Token(TypeToken.DIVIDE, "/", ligne, colonne));
                    i++; colonne++;
                } else if (c == '<') {
                    i++; colonne++;
                    if (code.charAt(i) == '=') {
                        tokens.add(new Token(TypeToken.LESS_EQUAL, "<=", ligne, debutC));
                        i++; colonne++;
                    } else {
                        tokens.add(new Token(TypeToken.LESS_THAN, "<", ligne, debutC));
                    }
                } else if (c == '>') {
                    i++; colonne++;
                    if (code.charAt(i) == '=') {
                        tokens.add(new Token(TypeToken.GREATER_EQUAL, ">=", ligne, debutC));
                        i++; colonne++;
                    } else {
                        tokens.add(new Token(TypeToken.GREATER_THAN, ">", ligne, debutC));
                    }
                } 
                else if (c == '!') {
                    i++; colonne++; // On passe le '!'
                    if (code.charAt(i) == '=') {
                        tokens.add(new Token(TypeToken.NOT_EQUAL, "!=", ligne, debutC));
                        i++; colonne++;
                    } else {
                        errorService.addError(new Error(Error.ErrorType.LEXICAL, "Caractère inattendu '!', attendu '!='", ligne, debutC));
                    }
                } 
                else if (c == '(') {
                    tokens.add(new Token(TypeToken.LPAREN, "(", ligne, colonne));
                    i++; colonne++;
                } else if (c == ')') {
                    tokens.add(new Token(TypeToken.RPAREN, ")", ligne, colonne));
                    i++; colonne++;
                } else if (c == ':') {
                    tokens.add(new Token(TypeToken.COLON, ":", ligne, colonne));
                    i++; colonne++;
                } else if (c == ',') {
                    tokens.add(new Token(TypeToken.COMMA, ",", ligne, colonne));
                    i++; colonne++;
                } else if (c == '.') {
                    tokens.add(new Token(TypeToken.DOT, ".", ligne, colonne));
                    i++; colonne++;
                } else {
                    errorService.addError(new Error(Error.ErrorType.LEXICAL, "Inconnu: " + c, ligne, colonne));
                    i++; colonne++;
                }
            }
        }

        while (niveauxIndent.size() > 1) {
            niveauxIndent.remove(niveauxIndent.size() - 1);
            tokens.add(new Token(TypeToken.DEDENT, "", ligne, colonne));
        }
        tokens.add(new Token(TypeToken.EOF, "[FIN]", ligne, colonne));

        return tokens;
    }
}