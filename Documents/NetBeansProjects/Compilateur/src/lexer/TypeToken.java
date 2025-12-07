/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lexer;

public enum TypeToken {
    // Mots-cles
    TRY, EXCEPT, FINALLY, RAISE,
    IF, ELIF, ELSE,
    WHILE, FOR, IN,
    BREAK, CONTINUE,
    DEF, RETURN, PASS,
    CLASS, AS, PRINT,
    AND, OR, NOT,
    TRUE, FALSE, NONE,
    
    // Noms speciaux
    TAGUELMIMT,      
    BADIS,           
    
    // Base
    IDENTIFIER,
    NUMBER,
    STRING,
    
    // Operateurs
    PLUS,            
    MINUS,           
    MULTIPLY,       
    DIVIDE,        
    MODULO,        
    POWER,           
    INCREMENT,       
    DECREMENT,       
    
    ASSIGN,          
    PLUS_ASSIGN,    
    MINUS_ASSIGN,    
    
    EQUAL,           
    NOT_EQUAL,       
    LESS_THAN,       
    GREATER_THAN,    
    LESS_EQUAL,      
    GREATER_EQUAL,   
    
    // Delimiteurs
    LPAREN,          
    RPAREN,          
    LBRACKET,        
    RBRACKET,        
    LBRACE,          
    RBRACE,          
    COMMA,           
    COLON,           
    SEMICOLON,       
    DOT,            
    
    // Structure
    NEWLINE,        
    INDENT,    
    DEDENT,          
    
    EOF
}