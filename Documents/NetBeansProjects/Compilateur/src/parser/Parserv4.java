/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package parser;

import lexer.Token;
import lexer.TypeToken;
import errors.CompilerError;
import errors.ErrorHandler;
import java.util.List;

public class Parserv4 {
    private List<Token> tokens;
    private int index;
    private Token tokenCourant;
    private ErrorHandler errorHandler;

    public Parserv4(List<Token> tokens, ErrorHandler errorHandler) {
        this.tokens = tokens;
        this.errorHandler = errorHandler;
        this.index = 0;
        if (tokens.isEmpty() == false) {
            tokenCourant = tokens.get(index);
        }
    }

    public void analyser() {
        System.out.println("Debut de l'analyse syntaxique");
        System.out.println("-----------------------------");
        
        Programme();
        
        if (errorHandler.hasErrors() == false && tokenCourant.getType() == TypeToken.EOF) {
            System.out.println("[OK] Programme syntaxiquement correct");
        } else if (errorHandler.hasErrors() == false) {
            ajouterErreurGenerique();
        } else {
            System.out.println("[!] Analyse terminee avec erreurs");
        }
    }

    private void Programme() {
        if (errorHandler.hasErrors()) return;
        SautsLigne();
        ListeInstructions();
    }

    // ListeInstructions -> Instruction ListeInstructions | epsilon
    private void ListeInstructions() {
        if (errorHandler.hasErrors()) return;

        // CORRECTION CRITIQUE : 
        // On ignore les INDENT et NEWLINE parasites.
        // MAIS on ne touche PAS au DEDENT ici !
        // Si on croise un DEDENT, c'est que le bloc est fini, on doit laisser la main a la methode Bloc().
        while (tokenCourant.getType() == TypeToken.INDENT || 
               tokenCourant.getType() == TypeToken.NEWLINE) {
            avancerToken();
            if (tokenCourant.getType() == TypeToken.EOF) return;
        }

        // Si on tombe sur DEDENT, on arrete la liste (epsilon), on ne le consomme pas
        if (tokenCourant.getType() == TypeToken.DEDENT) {
            return;
        }

        // Si on a un token qui commence une instruction, on l'analyse
        if (tokenCourant.getType() != TypeToken.EOF) {
            Instruction();
            ListeInstructions(); // Recursivite
        }
    }
    
    private void Instruction() {
        if (errorHandler.hasErrors()) return;

        if (tokenCourant.getType() == TypeToken.TRY) {
            TryExcept();
        } 
        else if (tokenCourant.getType() == TypeToken.DEF) {
            DeclarationMethode();
        } 
        else if (tokenCourant.getType() == TypeToken.CLASS) {
            DeclarationClasse();
        } 
        else if (tokenCourant.getType() == TypeToken.IDENTIFIER) {
            Statement(); 
        } 
        else if (tokenCourant.getType() == TypeToken.IF || 
                 tokenCourant.getType() == TypeToken.WHILE || 
                 tokenCourant.getType() == TypeToken.FOR ||
                 tokenCourant.getType() == TypeToken.ELIF ||
                 tokenCourant.getType() == TypeToken.ELSE) {
            StructureIgnoree(); 
        }
        else if (tokenCourant.getType() == TypeToken.PRINT || 
                 tokenCourant.getType() == TypeToken.RETURN || 
                 tokenCourant.getType() == TypeToken.BREAK || 
                 tokenCourant.getType() == TypeToken.CONTINUE || 
                 tokenCourant.getType() == TypeToken.PASS ||
                 tokenCourant.getType() == TypeToken.RAISE) {
            InstructionSimpleIgnoree();
        } 
        else {
            ajouterErreurGenerique();
            avancerToken();
        }
    }

    private void Statement() {
        avancerToken(); // On passe l'identifiant (ex: x)
        
        // --- PATCH DE SECURITE ---
        // Si par hasard il y a un INDENT parasite ici (ton bug), on l'ignore !
        if (tokenCourant.getType() == TypeToken.INDENT) {
            avancerToken();
        }
        // -------------------------

        if (tokenCourant.getType() == TypeToken.ASSIGN || 
            tokenCourant.getType() == TypeToken.PLUS_ASSIGN || 
            tokenCourant.getType() == TypeToken.MINUS_ASSIGN) {
            avancerToken(); 
            Expression();
        } else {
            // C'est une expression, on recule pour la relire correctement
            reculerToken(); 
            Expression();
        }
    }

    // --- Structures ---

    private void TryExcept() {
        System.out.println("Instruction Try/Except");
        avancerToken(); 
        if (tokenCourant.getType() == TypeToken.COLON) {
            avancerToken(); 
            Bloc(); Excepts(); FinallyOpt(); 
        } else { ajouterErreurGenerique(); }
    }

    private void Excepts() {
        if (errorHandler.hasErrors()) return;
        SautsLigne();
        if (tokenCourant.getType() == TypeToken.EXCEPT) {
            avancerToken(); TypeExceptOpt(); 
            if (tokenCourant.getType() == TypeToken.COLON) {
                avancerToken(); Bloc(); Excepts(); 
            } else { ajouterErreurGenerique(); }
        }
    }

    private void TypeExceptOpt() {
        if (tokenCourant.getType() == TypeToken.IDENTIFIER) {
            avancerToken();
            if (tokenCourant.getType() == TypeToken.AS) {
                avancerToken();
                if (tokenCourant.getType() == TypeToken.IDENTIFIER) avancerToken();
            }
        }
    }

    private void FinallyOpt() {
        SautsLigne();
        if (tokenCourant.getType() == TypeToken.FINALLY) {
            avancerToken();
            if (tokenCourant.getType() == TypeToken.COLON) {
                avancerToken(); Bloc();
            } else { ajouterErreurGenerique(); }
        }
    }

    private void DeclarationMethode() {
        System.out.println("Declaration fonction");
        avancerToken();
        if (tokenCourant.getType() == TypeToken.IDENTIFIER) {
            avancerToken();
            if (tokenCourant.getType() == TypeToken.LPAREN) {
                avancerToken();
                while(tokenCourant.getType() != TypeToken.RPAREN && tokenCourant.getType() != TypeToken.EOF) {
                     avancerToken(); 
                }
                if (tokenCourant.getType() == TypeToken.RPAREN) {
                    avancerToken();
                    if (tokenCourant.getType() == TypeToken.COLON) {
                        avancerToken(); Bloc();
                    } else { ajouterErreurGenerique(); }
                } else { ajouterErreurGenerique(); }
            } else { ajouterErreurGenerique(); }
        } else { ajouterErreurGenerique(); }
    }

    private void DeclarationClasse() {
        System.out.println("Declaration classe");
        avancerToken();
        if (tokenCourant.getType() == TypeToken.IDENTIFIER) {
            avancerToken();
            if (tokenCourant.getType() == TypeToken.COLON) {
                avancerToken(); Bloc();
            } else { ajouterErreurGenerique(); }
        } else { ajouterErreurGenerique(); }
    }

    private void StructureIgnoree() {
        avancerToken();
        while (tokenCourant.getType() != TypeToken.COLON && tokenCourant.getType() != TypeToken.EOF && tokenCourant.getType() != TypeToken.NEWLINE) {
            avancerToken();
        }
        if (tokenCourant.getType() == TypeToken.COLON) {
            avancerToken(); Bloc(); 
        } else { ajouterErreurGenerique(); }
    }

    private void InstructionSimpleIgnoree() {
        while (tokenCourant.getType() != TypeToken.NEWLINE && tokenCourant.getType() != TypeToken.EOF) {
            avancerToken();
        }
    }

    // --- Outils ---

    private void Bloc() {
        SautsLigne();
        if (tokenCourant.getType() == TypeToken.INDENT) {
            avancerToken();
            ListeInstructions();
            if (tokenCourant.getType() == TypeToken.DEDENT) {
                avancerToken();
            } else { ajouterErreurGenerique(); }
        } else { ajouterErreurGenerique(); }
    }

    private void SautsLigne() {
        while (tokenCourant.getType() == TypeToken.NEWLINE) {
            avancerToken();
        }
    }

    private void Expression() {
        Terme(); ExpPrime();
    }
    private void ExpPrime() {
        if (errorHandler.hasErrors()) return;
        if (tokenCourant.getType() == TypeToken.PLUS || tokenCourant.getType() == TypeToken.MINUS ||
            tokenCourant.getType() == TypeToken.EQUAL || tokenCourant.getType() == TypeToken.NOT_EQUAL ||
            tokenCourant.getType() == TypeToken.LESS_THAN || tokenCourant.getType() == TypeToken.GREATER_THAN ||
            tokenCourant.getType() == TypeToken.AND || tokenCourant.getType() == TypeToken.OR) {
            avancerToken(); Terme(); ExpPrime();
        }
    }
    private void Terme() {
        Facteur(); TermePrime();
    }
    private void TermePrime() {
        if (errorHandler.hasErrors()) return;
        if (tokenCourant.getType() == TypeToken.MULTIPLY || tokenCourant.getType() == TypeToken.DIVIDE || tokenCourant.getType() == TypeToken.MODULO) {
            avancerToken(); Facteur(); TermePrime();
        }
    }
    private void Facteur() {
        if (errorHandler.hasErrors()) return;
        
        if (tokenCourant.getType() == TypeToken.LPAREN) {
            avancerToken();
            Expression();
            if (tokenCourant.getType() == TypeToken.RPAREN) { avancerToken(); } 
            else { ajouterErreurGenerique(); }
        } 
        // MODIFICATION ICI : Ajout de TAGUELMIMT et BADIS dans la liste des valeurs acceptees
        else if (tokenCourant.getType() == TypeToken.NUMBER || 
                 tokenCourant.getType() == TypeToken.STRING || 
                 tokenCourant.getType() == TypeToken.TRUE || 
                 tokenCourant.getType() == TypeToken.FALSE || 
                 tokenCourant.getType() == TypeToken.NONE || 
                 tokenCourant.getType() == TypeToken.TAGUELMIMT || 
                 tokenCourant.getType() == TypeToken.BADIS || 
                 tokenCourant.getType() == TypeToken.IDENTIFIER) {
            
            avancerToken();
            
            // Si c'est un identifiant, ca peut etre un appel de fonction
            if (tokenCourant.getType() == TypeToken.LPAREN) {
                avancerToken();
                while(tokenCourant.getType() != TypeToken.RPAREN && tokenCourant.getType() != TypeToken.EOF) { avancerToken(); }
                if (tokenCourant.getType() == TypeToken.RPAREN) { avancerToken(); }
            }
        } 
        else if (tokenCourant.getType() == TypeToken.NOT) { avancerToken(); Facteur(); }
        
        // Si aucun cas ne correspond (erreur)
        else {
            // Optionnel : ajouter une erreur ici si on veut etre strict
        }
    }
    
    private void avancerToken() {
        index = index + 1;
        if (index < tokens.size()) {
            tokenCourant = tokens.get(index);
        }
    }
    
    private void reculerToken() {
        index = index - 1;
        if (index >= 0) {
            tokenCourant = tokens.get(index);
        }
    }
    
    private void ajouterErreurGenerique() {
        errorHandler.addError(new CompilerError(
            CompilerError.ErrorType.SYNTAXIC,
            "Erreur Syntaxique (Token: " + tokenCourant.getValue() + ")", 
            tokenCourant.getLine(), tokenCourant.getColumn()
        ));
    }
}