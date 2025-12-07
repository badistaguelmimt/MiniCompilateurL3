/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package parser;

import lexer.Token;
import lexer.TypeToken;
import errors.Error;
import errors.ErrorService;
import java.util.List;

public class Parser {
    private List<Token> tokens;
    private int index;
    private Token tc;
    private ErrorService errorService;

    public Parser(List<Token> tokens, ErrorService errorService) {
        this.tokens = tokens;
        this.errorService = errorService;
        this.index = 0;
        tc = tokens.get(index);
    }

    public void Z() {
        System.out.println("Debut de l'analyse syntaxique");
        System.out.println("-----------------------------");
        
        Programme();
        
        if (errorService.hasErrors() == false) {
            if (tc.getType() == TypeToken.EOF) {
                System.out.println("[Programme syntaxiquement correct]");
            } else {
                ajouterErreur();
            }
        } else {
            System.out.println("[Analyse terminee avec erreurs!]");
        }
    }

    private void Programme() {
        if (errorService.hasErrors()) return;
        SautsLigne();
        ListeInstructions();
    }

    private void ListeInstructions() {
        if (errorService.hasErrors()) return;

        while (tc.getType() == TypeToken.INDENT || 
               tc.getType() == TypeToken.NEWLINE) {
            avancerToken();
        }
        
        if (tc.getType() == TypeToken.DEDENT || tc.getType() == TypeToken.EOF) {
            return;
        }

        Instruction();
        ListeInstructions();
    }
    
    private void Instruction() {
        if (errorService.hasErrors()) return;

        if (tc.getType() == TypeToken.TRY) {
            TryExcept();
        } 
        else if (tc.getType() == TypeToken.DEF) {
            DeclarationMethode();
        } 
        else if (tc.getType() == TypeToken.CLASS) {
            DeclarationClasse();
        } 
        else if (tc.getType() == TypeToken.IDENTIFIER) {

            Token prochain = tokens.get(index + 1);
            
            if (prochain.getType() == TypeToken.ASSIGN || 
                prochain.getType() == TypeToken.PLUS_ASSIGN || 
                prochain.getType() == TypeToken.MINUS_ASSIGN) {
                Affectation();
            } else {
                Expression();
            }
        } 
        else if (tc.getType() == TypeToken.IF || 
                 tc.getType() == TypeToken.WHILE || 
                 tc.getType() == TypeToken.FOR ||
                 tc.getType() == TypeToken.ELIF ||
                 tc.getType() == TypeToken.ELSE) {
            StructureIgnoree(); 
        }
        else if (tc.getType() == TypeToken.PRINT || 
                 tc.getType() == TypeToken.RETURN || 
                 tc.getType() == TypeToken.BREAK || 
                 tc.getType() == TypeToken.CONTINUE || 
                 tc.getType() == TypeToken.PASS ||
                 tc.getType() == TypeToken.RAISE) {
            InstructionSimpleIgnoree();
        } 
        else {
            ajouterErreur();
            avancerToken(); 
        }
    }

    private void Affectation() {
        avancerToken(); 
        avancerToken(); 
        Expression();
    }


    private void TryExcept() {
        System.out.println("Instruction Try/Except");
        avancerToken();
        if (tc.getType() == TypeToken.COLON) {
            avancerToken(); 
            Bloc(); Excepts(); FinallyOpt(); 
        } else { ajouterErreur(); }
    }

    private void Excepts() {
        if (errorService.hasErrors()) return;
        SautsLigne();
        if (tc.getType() == TypeToken.EXCEPT) {
            avancerToken(); TypeExceptOpt(); 
            if (tc.getType() == TypeToken.COLON) {
                avancerToken(); Bloc(); Excepts(); 
            } else { ajouterErreur(); }
        }
    }

    private void TypeExceptOpt() {
        if (tc.getType() == TypeToken.IDENTIFIER) {
            avancerToken();
            if (tc.getType() == TypeToken.AS) {
                avancerToken();
                if (tc.getType() == TypeToken.IDENTIFIER) avancerToken();
            }
        }
    }

    private void FinallyOpt() {
        SautsLigne();
        if (tc.getType() == TypeToken.FINALLY) {
            avancerToken();
            if (tc.getType() == TypeToken.COLON) {
                avancerToken(); Bloc();
            } else { ajouterErreur(); }
        }
    }

    private void DeclarationMethode() {
        System.out.println("Declaration fonction");
        avancerToken(); 
        if (tc.getType() == TypeToken.IDENTIFIER) {
            avancerToken();
            if (tc.getType() == TypeToken.LPAREN) {
                avancerToken();
                while(tc.getType() != TypeToken.RPAREN && tc.getType() != TypeToken.EOF) {
                     avancerToken(); 
                }
                if (tc.getType() == TypeToken.RPAREN) {
                    avancerToken();
                    if (tc.getType() == TypeToken.COLON) {
                        avancerToken();
                        Bloc();
                    } else { ajouterErreur(); }
                } else { ajouterErreur(); }
            } else { ajouterErreur(); }
        } else { ajouterErreur(); }
    }

    private void DeclarationClasse() {
        System.out.println("Declaration classe");
        avancerToken(); 
        if (tc.getType() == TypeToken.IDENTIFIER) {
            avancerToken();
            if (tc.getType() == TypeToken.COLON) {
                avancerToken(); Bloc();
            } else { ajouterErreur(); }
        } else { ajouterErreur(); }
    }

    private void StructureIgnoree() {
        avancerToken();
        while (tc.getType() != TypeToken.COLON && tc.getType() != TypeToken.EOF && tc.getType() != TypeToken.NEWLINE) {
            avancerToken();
        }
        if (tc.getType() == TypeToken.COLON) {
            avancerToken(); Bloc(); 
        } else { ajouterErreur(); }
    }

    private void InstructionSimpleIgnoree() {

        while (tc.getType() != TypeToken.NEWLINE && 
               tc.getType() != TypeToken.EOF && 
               tc.getType() != TypeToken.DEDENT) { 
            avancerToken();
        }
    }


    private void Bloc() {
        SautsLigne();
        if (tc.getType() == TypeToken.INDENT) {
            avancerToken();
            ListeInstructions();
            if (tc.getType() == TypeToken.DEDENT) {
                avancerToken();
            } else { ajouterErreur(); }
        } else { ajouterErreur(); }
    }

    private void SautsLigne() {
        while (tc.getType() == TypeToken.NEWLINE) {
            avancerToken();
        }
    }


    private void Expression() {
        Terme(); ExpPrime();
    }
    private void ExpPrime() {
        if (errorService.hasErrors()) return;
        if (tc.getType() == TypeToken.PLUS || tc.getType() == TypeToken.MINUS ||
            tc.getType() == TypeToken.EQUAL || tc.getType() == TypeToken.NOT_EQUAL ||
            tc.getType() == TypeToken.LESS_THAN || tc.getType() == TypeToken.GREATER_THAN ||
            tc.getType() == TypeToken.LESS_EQUAL || tc.getType() == TypeToken.GREATER_EQUAL ||
            tc.getType() == TypeToken.AND || tc.getType() == TypeToken.OR) {
            avancerToken(); Terme(); ExpPrime();
        }
    }
    private void Terme() {
        Facteur(); TermePrime();
    }
    private void TermePrime() {
        if (errorService.hasErrors()) return;
        if (tc.getType() == TypeToken.MULTIPLY || tc.getType() == TypeToken.DIVIDE || tc.getType() == TypeToken.MODULO) {
            avancerToken(); Facteur(); TermePrime();
        }
    }
    private void Facteur() {
        if (errorService.hasErrors()) return;
        
        if (tc.getType() == TypeToken.LPAREN) {
            avancerToken(); Expression();
            if (tc.getType() == TypeToken.RPAREN) { avancerToken(); } 
            else { ajouterErreur(); }
        } 
        else if (tc.getType() == TypeToken.NUMBER || tc.getType() == TypeToken.STRING || 
                 tc.getType() == TypeToken.TRUE || tc.getType() == TypeToken.FALSE || 
                 tc.getType() == TypeToken.TAGUELMIMT || tc.getType() == TypeToken.BADIS ||
                 tc.getType() == TypeToken.IDENTIFIER) {
            avancerToken();
            
            if (tc.getType() == TypeToken.LPAREN) {
                avancerToken();
                while(tc.getType() != TypeToken.RPAREN && tc.getType() != TypeToken.EOF) { avancerToken(); }
                if (tc.getType() == TypeToken.RPAREN) { avancerToken(); }
            }
        } 
        else if (tc.getType() == TypeToken.NOT) { avancerToken(); Facteur(); }
    }
    
    
    private void avancerToken() {
        index = index + 1;
        tc = tokens.get(index);
    }
    
    private void ajouterErreur() {
        
        String valeur = tc.getValeur();
        if (valeur.equals("\\n")) {
            valeur = "Fin de ligne"; 
        }
        
        errorService.addError(new Error(Error.ErrorType.SYNTAXIC,"Token inattendu : " + valeur, tc.getLigne(), tc.getColonne()));
    }
}