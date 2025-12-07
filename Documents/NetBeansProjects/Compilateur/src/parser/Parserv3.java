/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package parser;

import lexer.Token;
import lexer.TypeToken;
import java.util.List;

public class Parserv3 {
    private List<Token> tokens;
    private int i = 0;
    private Token tc;
    private boolean erreur = false;

    public Parserv3(List<Token> tokens) {
        this.tokens = tokens;
        if (!tokens.isEmpty()) {
            tc = tokens.get(i);
        }
    }

    // Z -> S #
    public void Z() {
        System.out.println("\n========== ANALYSE SYNTAXIQUE ==========");
        System.out.println("Sujet : Try/Except (Python)");
        System.out.println("----------------------------------------");
        
        Programme();
        
        if (!erreur && tc.getType() == TypeToken.EOF) {
            System.out.println("Programme syntaxiquement correct");
        } else if (!erreur) {
            System.out.println("Erreur : Fin de fichier non atteinte (Reste: " + tc.getValue() + ")");
            erreur = true;
        } else {
            System.out.println("Erreurs détectées");
        }
        System.out.println("========================================\n");
    }

    // Grammaire : Programme -> SautsLigne ListeInstructions
    private void Programme() {
        if (erreur) return;
        SautsLigne(); // On consomme les lignes vides au début
        ListeInstructions();
    }

    // Grammaire : ListeInstructions -> Instruction ListeInstructions | ε
    private void ListeInstructions() {
        if (erreur) return;

        // Gestion des lignes vides entre les instructions
        if (tc.getType() == TypeToken.NEWLINE) {
            i++;
            tc = tokens.get(i);
            ListeInstructions(); // Appel récursif
            return;
        }

        // PREMIERS(Instruction)
        // On vérifie si le token actuel commence une instruction valide
        if (tc.getType() == TypeToken.TRY ||         // Sujet principal
            tc.getType() == TypeToken.DEF ||         // Déclaration méthode
            tc.getType() == TypeToken.CLASS ||       // Déclaration classe
            tc.getType() == TypeToken.IDENTIFIER ||  // Affectation ou Expression
            tc.getType() == TypeToken.PRINT ||       // Fonction native courante
            tc.getType() == TypeToken.RAISE ||       // Instruction liée aux exceptions
            tc.getType() == TypeToken.IF ||          // Ignoré (générique)
            tc.getType() == TypeToken.WHILE ||       // Ignoré (générique)
            tc.getType() == TypeToken.FOR ||         // Ignoré (générique)
            tc.getType() == TypeToken.PASS ||
            tc.getType() == TypeToken.BREAK ||
            tc.getType() == TypeToken.CONTINUE) {
            
            Instruction();
            ListeInstructions(); // Récursivité à droite pour continuer la liste
        }
        // Sinon ε (Epsilon) : On ne fait rien si on tombe sur DEDENT ou EOF
    }

    // Instruction -> TryExcept | Declaration | Affectation | StructureIgnoree | ...
    private void Instruction() {
        if (erreur) return;

        // --- SUJET PRINCIPAL : TRY / EXCEPT ---
        if (tc.getType() == TypeToken.TRY) {
            TryExcept();
        } 
        // --- DÉCLARATIONS (Exigence du PDF) ---
        else if (tc.getType() == TypeToken.DEF) {
            DeclarationMethode();
        } else if (tc.getType() == TypeToken.CLASS) {
            DeclarationClasse();
        } 
        // --- AFFECTATIONS & APPELS ---
        else if (tc.getType() == TypeToken.IDENTIFIER) {
            Statement(); // Gère Affectation (x=1) et Expression (x+1)
        } 
        // --- STRUCTURES IGNORÉES (Exigence du PDF : while, if, for sont ignorés) ---
        else if (tc.getType() == TypeToken.IF || tc.getType() == TypeToken.WHILE || tc.getType() == TypeToken.FOR) {
            StructureIgnoree(); 
        }
        // --- AUTRES INSTRUCTIONS SIMPLES ---
        else if (tc.getType() == TypeToken.PRINT) {
            Print();
        } else if (tc.getType() == TypeToken.RAISE) {
            Raise();
        } else if (tc.getType() == TypeToken.PASS || tc.getType() == TypeToken.BREAK || tc.getType() == TypeToken.CONTINUE) {
            i++; tc = tokens.get(i); // Instruction simple, on consomme juste
        } else {
            System.out.println("Erreur : Instruction non reconnue -> " + tc.getValue());
            erreur = true;
        }
    }

    // ==========================================
    // 1. LE SUJET PRINCIPAL : TRY / EXCEPT
    // ==========================================
    // Grammaire : TryExcept -> 'try' ':' Bloc Excepts FinallyOpt
    private void TryExcept() {
        System.out.println(" [Analyse] Instruction Try/Except détectée");
        i++; tc = tokens.get(i); // consume try

        if (tc.getType() == TypeToken.COLON) {
            i++; tc = tokens.get(i); // consume :
            Bloc();       // Analyse du contenu du try
            Excepts();    // Analyse des blocs except (récursif)
            FinallyOpt(); // Analyse du finally optionnel
        } else {
            System.out.println("Erreur : ':' manquant après 'try'");
            erreur = true;
        }
    }

    // Grammaire : Excepts -> 'except' TypeExceptOpt ':' Bloc Excepts | ε
    private void Excepts() {
        if (erreur) return;
        SautsLigne(); // Ignorer les lignes vides

        if (tc.getType() == TypeToken.EXCEPT) {
            i++; tc = tokens.get(i); // consume except
            
            TypeExceptOpt(); // Gestion du type d'erreur (ex: ValueError)

            if (tc.getType() == TypeToken.COLON) {
                i++; tc = tokens.get(i); // consume :
                Bloc();
                Excepts(); // Récursivité (pour avoir plusieurs except)
            } else {
                System.out.println("Erreur : ':' manquant après 'except'");
                erreur = true;
            }
        }
        // Sinon Epsilon (pas d'autres except)
    }

    // Grammaire : TypeExceptOpt -> ID AsOpt | ε
    private void TypeExceptOpt() {
        if (tc.getType() == TypeToken.IDENTIFIER) {
            i++; tc = tokens.get(i);
            if (tc.getType() == TypeToken.AS) {
                i++; tc = tokens.get(i);
                if (tc.getType() == TypeToken.IDENTIFIER) {
                    i++; tc = tokens.get(i);
                } else {
                    System.out.println("Erreur : Identifiant attendu après 'as'");
                    erreur = true;
                }
            }
        }
        // Sinon Epsilon
    }

    // Grammaire : FinallyOpt -> 'finally' ':' Bloc | ε
    private void FinallyOpt() {
        SautsLigne();
        if (tc.getType() == TypeToken.FINALLY) {
            i++; tc = tokens.get(i);
            if (tc.getType() == TypeToken.COLON) {
                i++; tc = tokens.get(i);
                Bloc();
            } else {
                System.out.println("Erreur : ':' manquant après 'finally'");
                erreur = true;
            }
        }
    }

    // ==========================================
    // 2. LES DÉCLARATIONS (CLASSES / METHODES)
    // ==========================================
    
    // Grammaire : DeclarationMethode -> 'def' ID '(' ArgsDef ')' ':' Bloc
    private void DeclarationMethode() {
        System.out.println(" [Analyse] Déclaration de méthode");
        i++; tc = tokens.get(i); // def
        if (tc.getType() == TypeToken.IDENTIFIER) {
            i++; tc = tokens.get(i);
            if (tc.getType() == TypeToken.LPAREN) {
                i++; tc = tokens.get(i);
                if (tc.getType() != TypeToken.RPAREN) {
                    // Ici on pourrait analyser les arguments, simplifié pour l'exemple
                    while(tc.getType() != TypeToken.RPAREN && tc.getType() != TypeToken.EOF) {
                         i++; tc = tokens.get(i); 
                    }
                }
                if (tc.getType() == TypeToken.RPAREN) {
                    i++; tc = tokens.get(i);
                    if (tc.getType() == TypeToken.COLON) {
                        i++; tc = tokens.get(i);
                        Bloc();
                    } else { erreur = true; System.out.println("':' manquant fin def"); }
                } else { erreur = true; System.out.println("')' manquant"); }
            } else { erreur = true; System.out.println("'(' manquant"); }
        } else { erreur = true; System.out.println("Nom de fonction manquant"); }
    }

    // Grammaire : DeclarationClasse -> 'class' ID ':' Bloc
    private void DeclarationClasse() {
        System.out.println(" [Analyse] Déclaration de classe");
        i++; tc = tokens.get(i); // class
        if (tc.getType() == TypeToken.IDENTIFIER) {
            i++; tc = tokens.get(i);
            if (tc.getType() == TypeToken.COLON) {
                i++; tc = tokens.get(i);
                Bloc();
            } else { erreur = true; System.out.println("':' manquant après class"); }
        } else { erreur = true; System.out.println("Nom de classe manquant"); }
    }

    // ==========================================
    // 3. AFFECTATIONS ET EXPRESSIONS
    // ==========================================
    
    // Grammaire : Statement -> ID SuiteStatement
    // SuiteStatement -> '=' Expression | ExpressionSuite (si c'est juste un calcul)
    private void Statement() {
        // On a un ID. Est-ce une affectation (x = 1) ou une expression (x + 1) ?
        i++; tc = tokens.get(i); // On consomme l'ID

        if (tc.getType() == TypeToken.ASSIGN || 
            tc.getType() == TypeToken.PLUS_ASSIGN || 
            tc.getType() == TypeToken.MINUS_ASSIGN) {
            
            // C'est une AFFECTATION (Demandé dans le PDF)
            i++; tc = tokens.get(i); // consume =
            Expression();
        } else {
            // C'est une expression simple ou un appel de fonction
            // Petit hack pour "revenir en arrière" et laisser la méthode Expression gérer
            i--; tc = tokens.get(i); 
            Expression();
        }
    }

    // ==========================================
    // 4. INSTRUCTIONS "IGNOREES" (IF, WHILE, FOR)
    // ==========================================
    // Le PDF dit : "Les autres instructions sont ignorées lors de l'analyse syntaxique"
    // On vérifie juste la structure de base (MotClé + Condition + : + Bloc) pour ne pas planter.
    
    private void StructureIgnoree() {
        // Consomme le mot clé (if, while, ou for)
        String typeStructure = tc.getValue(); 
        i++; tc = tokens.get(i); 

        // On consomme tout jusqu'au deux-points ':' (On ignore la logique de la condition)
        Expression(); 

        if (tc.getType() == TypeToken.COLON) {
            i++; tc = tokens.get(i);
            // On parse quand même le bloc pour gérer l'indentation correctement
            Bloc();
        } else {
            System.out.println("Erreur syntaxique dans structure " + typeStructure + " (':' manquant)");
            erreur = true;
        }
    }

    // ==========================================
    // OUTILS DE BASE (BLOC & SAUTS)
    // ==========================================

    // Grammaire : Bloc -> NEWLINE INDENT ListeInstructions DEDENT
    private void Bloc() {
        SautsLigne();
        if (tc.getType() == TypeToken.INDENT) {
            i++; tc = tokens.get(i);
            
            ListeInstructions(); // Contenu du bloc
            
            if (tc.getType() == TypeToken.DEDENT) {
                i++; tc = tokens.get(i);
            } else {
                System.out.println("Erreur : Problème d'indentation (DEDENT manquant)");
                erreur = true;
            }
        } else {
            System.out.println("Erreur : Bloc indenté attendu (INDENT manquant)");
            erreur = true;
        }
    }

    private void SautsLigne() {
        // Consomme récursivement les lignes vides
        while (tc.getType() == TypeToken.NEWLINE) {
            i++; tc = tokens.get(i);
        }
    }

    private void Print() {
        i++; tc = tokens.get(i);
        if (tc.getType() == TypeToken.LPAREN) {
            i++; tc = tokens.get(i);
            // Arguments simplifiés (Expression, ...)
            if (tc.getType() != TypeToken.RPAREN) {
                Expression();
                while(tc.getType() == TypeToken.COMMA) { i++; tc = tokens.get(i); Expression(); }
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
    // GESTION DES EXPRESSIONS (SIMPLIFIÉE MAIS COMPLETE)
    // ==========================================
    // Inclut Comparaisons, Arithmétique, Logique

    private void Expression() {
        Terme();
        ExpPrime();
    }

    // ExpPrime -> OpComp Terme ExpPrime | ε
    // Gère les comparaisons (Demandé dans le PDF) et les additions
    private void ExpPrime() {
        if (erreur) return;
        if (tc.getType() == TypeToken.PLUS || tc.getType() == TypeToken.MINUS ||
            tc.getType() == TypeToken.EQUAL || tc.getType() == TypeToken.NOT_EQUAL ||
            tc.getType() == TypeToken.LESS_THAN || tc.getType() == TypeToken.GREATER_THAN ||
            tc.getType() == TypeToken.AND || tc.getType() == TypeToken.OR) {
            
            i++; tc = tokens.get(i);
            Terme();
            ExpPrime();
        }
    }

    private void Terme() {
        Facteur();
        TermePrime();
    }

    private void TermePrime() {
        if (erreur) return;
        if (tc.getType() == TypeToken.MULTIPLY || tc.getType() == TypeToken.DIVIDE || tc.getType() == TypeToken.MODULO) {
            i++; tc = tokens.get(i);
            Facteur();
            TermePrime();
        }
    }

    private void Facteur() {
        if (erreur) return;
        
        if (tc.getType() == TypeToken.LPAREN) {
            i++; tc = tokens.get(i);
            Expression();
            if (tc.getType() == TypeToken.RPAREN) {
                i++; tc = tokens.get(i);
            } else { erreur = true; }
        } 
        else if (tc.getType() == TypeToken.NUMBER || tc.getType() == TypeToken.STRING || 
                 tc.getType() == TypeToken.TRUE || tc.getType() == TypeToken.FALSE || tc.getType() == TypeToken.IDENTIFIER) {
            i++; tc = tokens.get(i);
            // Si c'est un ID, ça peut être un appel de fonction id()
            if (tc.getType() == TypeToken.LPAREN) {
                i++; tc = tokens.get(i);
                if (tc.getType() != TypeToken.RPAREN) Expression(); // Arguments simples
                if (tc.getType() == TypeToken.RPAREN) i++; tc = tokens.get(i);
            }
        } 
        else if (tc.getType() == TypeToken.NOT) {
             i++; tc = tokens.get(i);
             Facteur();
        }
    }
}