/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package parser;

import lexer.Token;
import lexer.TypeToken;
import java.util.List;

public class Parserv2 {
    private List<Token> tokens;
    private int i = 0;
    private Token tc;
    private boolean erreur = false;

    public Parserv2(List<Token> tokens) {
        this.tokens = tokens;
        if (!tokens.isEmpty()) {
            tc = tokens.get(i);
        }
    }

    // Z -> S #
    public void analyser() {
        System.out.println("\n========== DEBUT ANALYSE COMPLETE ==========");
        
        Programme();
        
        if (!erreur && tc.getType() == TypeToken.EOF) {
            System.out.println("✓ Programme syntaxiquement correct");
        } else if (!erreur) {
            System.out.println("❌ Erreur : Fin de fichier non atteinte (Reste: " + tc.getValue() + ")");
            erreur = true;
        } else {
            System.out.println("❌ Erreurs détectées");
        }
        System.out.println("============================================\n");
    }

    // Programme -> ListeInstructions
    private void Programme() {
        if (erreur) return;
        // On ignore les newlines au début du fichier
        while (!erreur && tc.getType() == TypeToken.NEWLINE) {
            i++; tc = tokens.get(i);
        }
        ListeInstructions();
    }

    // ListeInstructions -> Instruction ListeInstructions | ε
    private void ListeInstructions() {
        if (erreur) return;

        // Sauter les lignes vides
        if (tc.getType() == TypeToken.NEWLINE) {
            i++; tc = tokens.get(i);
            ListeInstructions();
            return;
        }

        // PREMIERS(Instruction)
        if (tc.getType() == TypeToken.TRY || 
            tc.getType() == TypeToken.IF || 
            tc.getType() == TypeToken.WHILE || 
            tc.getType() == TypeToken.FOR || 
            tc.getType() == TypeToken.PRINT || 
            tc.getType() == TypeToken.RAISE || 
            tc.getType() == TypeToken.IDENTIFIER ||
            tc.getType() == TypeToken.DEF ||
            tc.getType() == TypeToken.CLASS ||
            tc.getType() == TypeToken.RETURN ||
            tc.getType() == TypeToken.BREAK ||
            tc.getType() == TypeToken.CONTINUE ||
            tc.getType() == TypeToken.PASS) {
            
            Instruction();
            ListeInstructions();
        }
        // Sinon ε (pour DEDENT ou EOF)
    }

    private void Instruction() {
        if (erreur) return;

        if (tc.getType() == TypeToken.DEF) {
            Fonction();
        } else if (tc.getType() == TypeToken.CLASS) {
            Classe();
        } else if (tc.getType() == TypeToken.RETURN) {
            Return();
        } else if (tc.getType() == TypeToken.BREAK || tc.getType() == TypeToken.CONTINUE || tc.getType() == TypeToken.PASS) {
            i++; tc = tokens.get(i); // Instruction simple
        } else if (tc.getType() == TypeToken.TRY) {
            TryExcept();
        } else if (tc.getType() == TypeToken.IF) {
            If();
        } else if (tc.getType() == TypeToken.WHILE) {
            While();
        } else if (tc.getType() == TypeToken.FOR) {
            For();
        } else if (tc.getType() == TypeToken.PRINT) {
            Print();
        } else if (tc.getType() == TypeToken.RAISE) {
            Raise();
        } else if (tc.getType() == TypeToken.IDENTIFIER) {
            // Un ID peut commencer une affectation (x = 1) ou une expression (x + 1 ou x())
            StatementOrExpression();
        } else {
            System.out.println("Erreur : Instruction inattendue : " + tc.getValue());
            erreur = true;
        }
    }

    // Gère le cas ambigu : ID = ... (Affectation) vs ID(...) (Expression)
    private void StatementOrExpression() {
        // On mémorise l'ID mais on ne le consomme pas tout de suite pour la logique expression
        // Mais pour simplifier en LL(1), on consomme l'ID et on voit la suite
        i++; tc = tokens.get(i); // Consomme l'ID
        
        if (tc.getType() == TypeToken.ASSIGN || 
            tc.getType() == TypeToken.PLUS_ASSIGN || 
            tc.getType() == TypeToken.MINUS_ASSIGN) {
            // C'est une affectation
            i++; tc = tokens.get(i); // Consomme =, +=, -=
            Expression(); // Analyse la partie droite
        } else {
            // C'était juste le début d'une expression (ex: appel de fonction ou calcul perdu)
            // On a déjà mangé l'ID, donc on doit analyser la "suite" d'une expression
            // Astuce : Expression -> ... -> Facteur. 
            // Ici, on est "après" le premier facteur.
            
            // Pour faire simple dans ce TP : on revient en arrière d'un cran (backtrack léger)
            // pour relancer Expression() proprement.
            i--; tc = tokens.get(i); 
            Expression();
        }
    }

    // Def -> def ID ( Args ) : Bloc
    private void Fonction() {
        i++; tc = tokens.get(i); // def
        if (tc.getType() == TypeToken.IDENTIFIER) {
            i++; tc = tokens.get(i);
            if (tc.getType() == TypeToken.LPAREN) {
                i++; tc = tokens.get(i);
                if (tc.getType() != TypeToken.RPAREN) {
                    ArgsDef(); // Arguments de définition
                }
                if (tc.getType() == TypeToken.RPAREN) {
                    i++; tc = tokens.get(i);
                    if (tc.getType() == TypeToken.COLON) {
                        i++; tc = tokens.get(i);
                        Bloc();
                    } else { erreur = true; System.out.println("':' attendu après def"); }
                } else { erreur = true; System.out.println("')' attendu"); }
            } else { erreur = true; System.out.println("'(' attendu"); }
        } else { erreur = true; System.out.println("Nom de fonction attendu"); }
    }

    // ArgsDef -> ID SuiteArgsDef | ε
    private void ArgsDef() {
        if (tc.getType() == TypeToken.IDENTIFIER) {
            i++; tc = tokens.get(i);
            if (tc.getType() == TypeToken.COMMA) {
                i++; tc = tokens.get(i);
                ArgsDef();
            }
        }
    }

    // Class -> class ID : Bloc
    private void Classe() {
        i++; tc = tokens.get(i); // class
        if (tc.getType() == TypeToken.IDENTIFIER) {
            i++; tc = tokens.get(i);
            if (tc.getType() == TypeToken.COLON) {
                i++; tc = tokens.get(i);
                Bloc();
            } else { erreur = true; System.out.println("':' attendu après class"); }
        } else { erreur = true; System.out.println("Nom de classe attendu"); }
    }

    private void Return() {
        i++; tc = tokens.get(i); // return
        // Expression optionnelle
        if (tc.getType() != TypeToken.NEWLINE && tc.getType() != TypeToken.EOF) {
            Expression();
        }
    }

    // ==========================================
    // STRUCTURES DE CONTROLE
    // ==========================================

    private void If() {
        i++; tc = tokens.get(i); // if
        Expression();
        if (tc.getType() == TypeToken.COLON) {
            i++; tc = tokens.get(i);
            Bloc();
            SuiteIf();
        } else { erreur = true; System.out.println("':' attendu après if"); }
    }

    private void SuiteIf() {
        if (erreur) return;
        while(tc.getType() == TypeToken.NEWLINE) { i++; tc = tokens.get(i); } // Sauts de ligne

        if (tc.getType() == TypeToken.ELIF) {
            i++; tc = tokens.get(i);
            Expression();
            if (tc.getType() == TypeToken.COLON) {
                i++; tc = tokens.get(i);
                Bloc();
                SuiteIf();
            } else { erreur = true; }
        } else if (tc.getType() == TypeToken.ELSE) {
            i++; tc = tokens.get(i);
            if (tc.getType() == TypeToken.COLON) {
                i++; tc = tokens.get(i);
                Bloc();
            } else { erreur = true; }
        }
    }

    private void While() {
        i++; tc = tokens.get(i); // while
        Expression();
        if (tc.getType() == TypeToken.COLON) {
            i++; tc = tokens.get(i);
            Bloc();
        } else { erreur = true; }
    }

    private void For() {
        i++; tc = tokens.get(i); // for
        if (tc.getType() == TypeToken.IDENTIFIER) {
            i++; tc = tokens.get(i);
            if (tc.getType() == TypeToken.IN) {
                i++; tc = tokens.get(i);
                Expression();
                if (tc.getType() == TypeToken.COLON) {
                    i++; tc = tokens.get(i);
                    Bloc();
                } else { erreur = true; }
            } else { erreur = true; }
        } else { erreur = true; }
    }

    private void TryExcept() {
        i++; tc = tokens.get(i); // try
        if (tc.getType() == TypeToken.COLON) {
            i++; tc = tokens.get(i);
            Bloc();
            Excepts();
            // Finally optionnel
            while(tc.getType() == TypeToken.NEWLINE) { i++; tc = tokens.get(i); }
            if (tc.getType() == TypeToken.FINALLY) {
                i++; tc = tokens.get(i);
                if (tc.getType() == TypeToken.COLON) {
                    i++; tc = tokens.get(i);
                    Bloc();
                } else { erreur = true; }
            }
        } else { erreur = true; }
    }

    private void Excepts() {
        if (erreur) return;
        while(tc.getType() == TypeToken.NEWLINE) { i++; tc = tokens.get(i); }

        if (tc.getType() == TypeToken.EXCEPT) {
            i++; tc = tokens.get(i);
            // Type exception optionnel
            if (tc.getType() == TypeToken.IDENTIFIER) {
                i++; tc = tokens.get(i);
                if (tc.getType() == TypeToken.AS) {
                     i++; tc = tokens.get(i);
                     if (tc.getType() == TypeToken.IDENTIFIER) {
                         i++; tc = tokens.get(i);
                     }
                }
            }
            if (tc.getType() == TypeToken.COLON) {
                i++; tc = tokens.get(i);
                Bloc();
                Excepts();
            } else { erreur = true; }
        }
    }

    private void Bloc() {
        while (tc.getType() == TypeToken.NEWLINE) { i++; tc = tokens.get(i); }
        if (tc.getType() == TypeToken.INDENT) {
            i++; tc = tokens.get(i);
            ListeInstructions();
            if (tc.getType() == TypeToken.DEDENT) {
                i++; tc = tokens.get(i);
            } else { erreur = true; System.out.println("DEDENT manquant"); }
        } else { erreur = true; System.out.println("INDENT manquant"); }
    }
    
    private void Print() {
        i++; tc = tokens.get(i);
        if (tc.getType() == TypeToken.LPAREN) {
            i++; tc = tokens.get(i);
            if (tc.getType() != TypeToken.RPAREN) {
                ArgsCall();
            }
            if (tc.getType() == TypeToken.RPAREN) {
                i++; tc = tokens.get(i);
            } else { erreur = true; }
        } else { erreur = true; }
    }

    private void Raise() {
        i++; tc = tokens.get(i);
        if (tc.getType() != TypeToken.NEWLINE) Expression();
    }

    // ==========================================
    // EXPRESSIONS (Hiérarchie de priorité)
    // ==========================================
    // Priorité : OR < AND < NOT < COMP < ADD < MULT < POW < ATOM

    // 1. Expression (Gère OR)
    private void Expression() {
        TermeLogique();
        ExpPrime();
    }
    private void ExpPrime() {
        if (erreur) return;
        if (tc.getType() == TypeToken.OR) {
            i++; tc = tokens.get(i);
            TermeLogique();
            ExpPrime();
        }
    }

    // 2. Terme Logique (Gère AND)
    private void TermeLogique() {
        FacteurNot();
        TermeLogiquePrime();
    }
    private void TermeLogiquePrime() {
        if (erreur) return;
        if (tc.getType() == TypeToken.AND) {
            i++; tc = tokens.get(i);
            FacteurNot();
            TermeLogiquePrime();
        }
    }

    // 3. Facteur Not (Gère NOT unaire)
    private void FacteurNot() {
        if (tc.getType() == TypeToken.NOT) {
            i++; tc = tokens.get(i);
            FacteurNot(); // Récursif (not not x)
        } else {
            Comparaison();
        }
    }

    // 4. Comparaison (==, !=, <, >, <=, >=)
    private void Comparaison() {
        ExpArith();
        CompPrime();
    }
    private void CompPrime() {
        if (erreur) return;
        if (tc.getType() == TypeToken.EQUAL || tc.getType() == TypeToken.NOT_EQUAL ||
            tc.getType() == TypeToken.LESS_THAN || tc.getType() == TypeToken.GREATER_THAN ||
            tc.getType() == TypeToken.LESS_EQUAL || tc.getType() == TypeToken.GREATER_EQUAL) {
            
            i++; tc = tokens.get(i);
            ExpArith();
            CompPrime();
        }
    }

    // 5. Expression Arithmétique (+, -)
    private void ExpArith() {
        TermeArith();
        ExpArithPrime();
    }
    private void ExpArithPrime() {
        if (erreur) return;
        if (tc.getType() == TypeToken.PLUS || tc.getType() == TypeToken.MINUS) {
            i++; tc = tokens.get(i);
            TermeArith();
            ExpArithPrime();
        }
    }

    // 6. Terme Arithmétique (*, /, %)
    private void TermeArith() {
        Puissance();
        TermeArithPrime();
    }
    private void TermeArithPrime() {
        if (erreur) return;
        if (tc.getType() == TypeToken.MULTIPLY || tc.getType() == TypeToken.DIVIDE || tc.getType() == TypeToken.MODULO) {
            i++; tc = tokens.get(i);
            Puissance();
            TermeArithPrime();
        }
    }

    // 7. Puissance (**)
    private void Puissance() {
        Facteur();
        if (tc.getType() == TypeToken.POWER) {
            i++; tc = tokens.get(i);
            Puissance(); // Récursif droite pour la puissance (2**3**4)
        }
    }

    // 8. Facteur (Atomes, Parenthèses, Appels de fonction)
    private void Facteur() {
        if (erreur) return;

        if (tc.getType() == TypeToken.LPAREN) {
            i++; tc = tokens.get(i);
            Expression();
            if (tc.getType() == TypeToken.RPAREN) {
                i++; tc = tokens.get(i);
            } else { erreur = true; System.out.println("Parenthèse fermante manquante"); }
        } 
        else if (tc.getType() == TypeToken.MINUS) { // Unaire négatif (-5)
            i++; tc = tokens.get(i);
            Facteur();
        }
        else if (tc.getType() == TypeToken.NUMBER || tc.getType() == TypeToken.STRING || 
                 tc.getType() == TypeToken.TRUE || tc.getType() == TypeToken.FALSE || 
                 tc.getType() == TypeToken.NONE) {
            i++; tc = tokens.get(i);
        }
        else if (tc.getType() == TypeToken.IDENTIFIER || tc.getType() == TypeToken.TAGUELMIMT || tc.getType() == TypeToken.BADIS) {
            i++; tc = tokens.get(i);
            // Gestion des appels de fonction : id(args)
            if (tc.getType() == TypeToken.LPAREN) {
                i++; tc = tokens.get(i);
                if (tc.getType() != TypeToken.RPAREN) {
                    ArgsCall();
                }
                if (tc.getType() == TypeToken.RPAREN) {
                    i++; tc = tokens.get(i);
                } else { erreur = true; System.out.println("')' manquant après arguments"); }
            }
        } 
        else {
            erreur = true;
            System.out.println("Erreur expression : token inattendu " + tc.getValue());
        }
    }

    // ArgsCall -> Exp, Exp...
    private void ArgsCall() {
        Expression();
        while (tc.getType() == TypeToken.COMMA) {
            i++; tc = tokens.get(i);
            Expression();
        }
    }
}