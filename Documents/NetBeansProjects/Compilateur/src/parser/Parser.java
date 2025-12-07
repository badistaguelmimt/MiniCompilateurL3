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

public class Parser {
    private List<Token> tokens;
    private int position;
    private ErrorHandler errorHandler;
    
    public Parser(List<Token> tokens, ErrorHandler errorHandler) {
        this.tokens = tokens;
        this.position = 0;
        this.errorHandler = errorHandler;
    }
    
    // Obtenir le token actuel
    private Token tokenActuel() {
        if (position >= tokens.size()) {
            return tokens.get(tokens.size() - 1); // EOF
        }
        return tokens.get(position);
    }
    
    // Avancer au token suivant
    private void avancer() {
        if (position < tokens.size() - 1) {
            position++;
        }
    }
    
    // Vérifier le type du token actuel
    private boolean verifier(TypeToken type) {
        return tokenActuel().getType() == type;
    }
    
    // Consommer un token attendu
    private void consommer(TypeToken type, String message) {
        if (verifier(type)) {
            avancer();
        } else {
            errorHandler.addError(new CompilerError(
                CompilerError.ErrorType.SYNTAXIC,
                message + " (trouvé: " + tokenActuel().getValue() + ")",
                tokenActuel().getLine(),
                tokenActuel().getColumn()
            ));
        }
    }
    
    // Ignorer les NEWLINE
    private void ignorerNewlines() {
        while (verifier(TypeToken.NEWLINE)) {
            avancer();
        }
    }
    
    // === MÉTHODE PRINCIPALE ===
    public void analyser() {
        System.out.println("\n========== ANALYSE SYNTAXIQUE ==========\n");
        System.out.println("⏳ Analyse en cours...\n");
        
        analyserProgramme();
        
        if (!errorHandler.hasErrors()) {
            System.out.println("✓ Programme syntaxiquement correct");
        } else {
            System.out.println("❌ Erreurs syntaxiques détectées");
        }
        System.out.println("\n========================================\n");
    }
    
    // Programme → Instruction*
    private void analyserProgramme() {
        ignorerNewlines();
        
        while (!verifier(TypeToken.EOF)) {
            analyserInstruction();
            ignorerNewlines();
        }
    }
    
    // Instruction → TryExcept | If | While | For | Assignment | Print | Pass | Raise
    private void analyserInstruction() {
        ignorerNewlines();
        
        if (verifier(TypeToken.TRY)) {
            analyserTryExcept();
        } else if (verifier(TypeToken.IF)) {
            analyserIf();
        } else if (verifier(TypeToken.WHILE)) {
            analyserWhile();
        } else if (verifier(TypeToken.FOR)) {
            analyserFor();
        } else if (verifier(TypeToken.PRINT)) {
            analyserPrint();
        } else if (verifier(TypeToken.PASS)) {
            avancer();
        } else if (verifier(TypeToken.RAISE)) {
            analyserRaise();
        } else if (verifier(TypeToken.IDENTIFIER)) {
            analyserAffectation();
        } else if (verifier(TypeToken.BREAK) || verifier(TypeToken.CONTINUE)) {
            avancer();
        } else if (!verifier(TypeToken.EOF) && !verifier(TypeToken.DEDENT)) {
            errorHandler.addError(new CompilerError(
                CompilerError.ErrorType.SYNTAXIC,
                "Instruction non reconnue",
                tokenActuel().getLine(),
                tokenActuel().getColumn()
            ));
            avancer(); // Récupération
        }
    }
    
    // TryExcept → try : NEWLINE INDENT Instructions DEDENT except ID? : NEWLINE INDENT Instructions DEDENT Finally?
    private void analyserTryExcept() {
        System.out.println("  → Analyse try/except à la ligne " + tokenActuel().getLine());
        
        consommer(TypeToken.TRY, "Attendu 'try'");
        consommer(TypeToken.COLON, "Attendu ':' après try");
        ignorerNewlines();
        
        // Bloc try
        consommer(TypeToken.INDENT, "Attendu une indentation après try:");
        analyserBloc();
        consommer(TypeToken.DEDENT, "Attendu une désindentation après le bloc try");
        
        ignorerNewlines();
        
        // Except (peut être multiple)
        while (verifier(TypeToken.EXCEPT)) {
            avancer();
            
            // Type d'exception optionnel
            if (verifier(TypeToken.IDENTIFIER)) {
                avancer();
                
                // "as" variable optionnel
                if (verifier(TypeToken.AS)) {
                    avancer();
                    consommer(TypeToken.IDENTIFIER, "Attendu un identificateur après 'as'");
                }
            }
            
            consommer(TypeToken.COLON, "Attendu ':' après except");
            ignorerNewlines();
            
            // Bloc except
            consommer(TypeToken.INDENT, "Attendu une indentation après except:");
            analyserBloc();
            consommer(TypeToken.DEDENT, "Attendu une désindentation après le bloc except");
            
            ignorerNewlines();
        }
        
        // Finally optionnel
        if (verifier(TypeToken.FINALLY)) {
            avancer();
            consommer(TypeToken.COLON, "Attendu ':' après finally");
            ignorerNewlines();
            consommer(TypeToken.INDENT, "Attendu une indentation après finally:");
            analyserBloc();
            consommer(TypeToken.DEDENT, "Attendu une désindentation après le bloc finally");
        }
        
        System.out.println("  ✓ Try/except valide");
    }
    
    // If → if Expression : NEWLINE INDENT Instructions DEDENT Elif* Else?
    private void analyserIf() {
        System.out.println("  → Analyse if à la ligne " + tokenActuel().getLine());
        
        consommer(TypeToken.IF, "Attendu 'if'");
        analyserExpression();
        consommer(TypeToken.COLON, "Attendu ':' après la condition if");
        ignorerNewlines();
        
        // Bloc if
        consommer(TypeToken.INDENT, "Attendu une indentation après if:");
        analyserBloc();
        consommer(TypeToken.DEDENT, "Attendu une désindentation après le bloc if");
        
        ignorerNewlines();
        
        // Elif
        while (verifier(TypeToken.ELIF)) {
            avancer();
            analyserExpression();
            consommer(TypeToken.COLON, "Attendu ':' après la condition elif");
            ignorerNewlines();
            consommer(TypeToken.INDENT, "Attendu une indentation après elif:");
            analyserBloc();
            consommer(TypeToken.DEDENT, "Attendu une désindentation après le bloc elif");
            ignorerNewlines();
        }
        
        // Else
        if (verifier(TypeToken.ELSE)) {
            avancer();
            consommer(TypeToken.COLON, "Attendu ':' après else");
            ignorerNewlines();
            consommer(TypeToken.INDENT, "Attendu une indentation après else:");
            analyserBloc();
            consommer(TypeToken.DEDENT, "Attendu une désindentation après le bloc else");
        }
        
        System.out.println("  ✓ If valide");
    }
    
    // While → while Expression : NEWLINE INDENT Instructions DEDENT
    private void analyserWhile() {
        System.out.println("  → Analyse while à la ligne " + tokenActuel().getLine());
        
        consommer(TypeToken.WHILE, "Attendu 'while'");
        analyserExpression();
        consommer(TypeToken.COLON, "Attendu ':' après la condition while");
        ignorerNewlines();
        
        consommer(TypeToken.INDENT, "Attendu une indentation après while:");
        analyserBloc();
        consommer(TypeToken.DEDENT, "Attendu une désindentation après le bloc while");
        
        System.out.println("  ✓ While valide");
    }
    
    // For → for ID in Expression : NEWLINE INDENT Instructions DEDENT
    private void analyserFor() {
        System.out.println("  → Analyse for à la ligne " + tokenActuel().getLine());
        
        consommer(TypeToken.FOR, "Attendu 'for'");
        consommer(TypeToken.IDENTIFIER, "Attendu un identificateur");
        consommer(TypeToken.IN, "Attendu 'in'");
        analyserExpression();
        consommer(TypeToken.COLON, "Attendu ':'");
        ignorerNewlines();
        
        consommer(TypeToken.INDENT, "Attendu une indentation après for:");
        analyserBloc();
        consommer(TypeToken.DEDENT, "Attendu une désindentation après le bloc for");
        
        System.out.println("  ✓ For valide");
    }
    
    // Bloc → Instruction+
    private void analyserBloc() {
        ignorerNewlines();
        
        while (!verifier(TypeToken.DEDENT) && !verifier(TypeToken.EOF)) {
            analyserInstruction();
            ignorerNewlines();
        }
    }
    
    // Affectation → ID = Expression
    private void analyserAffectation() {
        consommer(TypeToken.IDENTIFIER, "Attendu un identificateur");
        
        // Opérateurs d'affectation
        if (verifier(TypeToken.ASSIGN)) {
            avancer();
        } else if (verifier(TypeToken.PLUS_ASSIGN)) {
            avancer();
        } else if (verifier(TypeToken.MINUS_ASSIGN)) {
            avancer();
        } else {
            errorHandler.addError(new CompilerError(
                CompilerError.ErrorType.SYNTAXIC,
                "Attendu un opérateur d'affectation",
                tokenActuel().getLine(),
                tokenActuel().getColumn()
            ));
        }
        
        analyserExpression();
    }
    
    // Print → print ( Expression )
    private void analyserPrint() {
        consommer(TypeToken.PRINT, "Attendu 'print'");
        consommer(TypeToken.LPAREN, "Attendu '('");
        
        // Arguments (peut être vide)
        if (!verifier(TypeToken.RPAREN)) {
            analyserExpression();
            
            // Arguments multiples
            while (verifier(TypeToken.COMMA)) {
                avancer();
                analyserExpression();
            }
        }
        
        consommer(TypeToken.RPAREN, "Attendu ')'");
    }
    
    // Raise → raise Expression?
    private void analyserRaise() {
        consommer(TypeToken.RAISE, "Attendu 'raise'");
        
        // Expression optionnelle
        if (!verifier(TypeToken.NEWLINE) && !verifier(TypeToken.EOF)) {
            analyserExpression();
        }
    }
    
    // Expression → Terme (Opérateur Terme)*
    private void analyserExpression() {
        analyserTerme();
        
        // Opérateurs binaires
        while (verifier(TypeToken.PLUS) || verifier(TypeToken.MINUS) ||
               verifier(TypeToken.LESS_THAN) || verifier(TypeToken.GREATER_THAN) ||
               verifier(TypeToken.LESS_EQUAL) || verifier(TypeToken.GREATER_EQUAL) ||
               verifier(TypeToken.EQUAL) || verifier(TypeToken.NOT_EQUAL) ||
               verifier(TypeToken.AND) || verifier(TypeToken.OR)) {
            avancer();
            analyserTerme();
        }
    }
    
    // Terme → Facteur ((*|/|%|**) Facteur)*
    private void analyserTerme() {
        analyserFacteur();
        
        while (verifier(TypeToken.MULTIPLY) || verifier(TypeToken.DIVIDE) ||
               verifier(TypeToken.MODULO) || verifier(TypeToken.POWER)) {
            avancer();
            analyserFacteur();
        }
    }
    
    // Facteur → (Expression) | ID | NUMBER | STRING | TRUE | FALSE | NONE | NOT Facteur | -Facteur | ID(Args) | ID++  | ID--
    private void analyserFacteur() {
        // Parenthèses
        if (verifier(TypeToken.LPAREN)) {
            avancer();
            analyserExpression();
            consommer(TypeToken.RPAREN, "Attendu ')'");
        }
        // Nombre
        else if (verifier(TypeToken.NUMBER)) {
            avancer();
        }
        // Chaîne
        else if (verifier(TypeToken.STRING)) {
            avancer();
        }
        // Booléens et None
        else if (verifier(TypeToken.TRUE) || verifier(TypeToken.FALSE) || verifier(TypeToken.NONE)) {
            avancer();
        }
        // Opérateur NOT
        else if (verifier(TypeToken.NOT)) {
            avancer();
            analyserFacteur();
        }
        // Opérateur - unaire
        else if (verifier(TypeToken.MINUS)) {
            avancer();
            analyserFacteur();
        }
        // Identificateur (peut être suivi d'un appel de fonction ou ++/--)
        else if (verifier(TypeToken.IDENTIFIER)) {
            avancer();
            
            // Appel de fonction
            if (verifier(TypeToken.LPAREN)) {
                avancer();
                
                // Arguments
                if (!verifier(TypeToken.RPAREN)) {
                    analyserExpression();
                    
                    while (verifier(TypeToken.COMMA)) {
                        avancer();
                        analyserExpression();
                    }
                }
                
                consommer(TypeToken.RPAREN, "Attendu ')'");
            }
            // Incrémentation/Décrémentation
            else if (verifier(TypeToken.INCREMENT) || verifier(TypeToken.DECREMENT)) {
                avancer();
            }
        }
        // Mots-clés personnalisés
        else if (verifier(TypeToken.TAGUELMIMT) || verifier(TypeToken.BADIS)) {
            avancer();
        }
        else {
            errorHandler.addError(new CompilerError(
                CompilerError.ErrorType.SYNTAXIC,
                "Expression attendue",
                tokenActuel().getLine(),
                tokenActuel().getColumn()
            ));
        }
    }
    
    // Afficher l'état du parsing
    public void afficherEtat() {
        System.out.println("\n========== ÉTAT DU PARSER ==========");
        System.out.println("Position actuelle : " + position + "/" + tokens.size());
        System.out.println("Token actuel      : " + tokenActuel().toDisplayString());
        System.out.println("====================================\n");
    }
}