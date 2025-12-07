/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compilateur;

import lexer.Lexer;
import lexer.Token;
import lexer.TypeToken;
import errors.ErrorService;
import errors.Error;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;
import parser.Parser;

public class Interface extends JFrame {

    private JTextArea inputArea;
    private JTextArea outputArea;
    private JButton loadButton;
    private JButton clearButton;
    private JButton compileButton;

    public Interface() {
        setTitle("Mini-Compilateur Python L3 - Taguelmimt Badis");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(245, 245, 245));
        
        loadButton = new JButton("Charger un fichier");
        clearButton = new JButton("Effacer");
        compileButton = new JButton("Lancer la Compilation");

        topPanel.add(loadButton);
        topPanel.add(clearButton);
        topPanel.add(compileButton);
        add(topPanel, BorderLayout.NORTH);

        inputArea = new JTextArea();
        JScrollPane scrollInput = new JScrollPane(inputArea);
        scrollInput.setBorder(BorderFactory.createTitledBorder(" Code Source "));

        outputArea = new JTextArea();
        outputArea.setBackground(Color.WHITE);
        outputArea.setForeground(Color.BLACK);
        outputArea.setEditable(false);
        JScrollPane scrollOutput = new JScrollPane(outputArea);
        scrollOutput.setBorder(BorderFactory.createTitledBorder(" Console "));
        scrollOutput.setPreferredSize(new Dimension(900, 350));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollInput, scrollOutput);
        splitPane.setResizeWeight(0.5); 
        add(splitPane, BorderLayout.CENTER);

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chargerFichier();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputArea.setText("");
                outputArea.setText("");
            }
        });
        
        compileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compilerCode();
            }
        });
    }

    private void chargerFichier() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(".")); 
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                inputArea.read(reader, null);
                reader.close();
                outputArea.setText("Fichier charge : " + selectedFile.getName() + "\n");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lecture fichier");
            }
        }
    }

    private void compilerCode() {
        outputArea.setText(""); 
        String code = inputArea.getText();
        
        if (code.trim().isEmpty()) {
            outputArea.append("Aucun code a compiler.\n");
            return;
        }

        PrintStream oldOut = System.out;
        PrintStream guiOut = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                outputArea.append(String.valueOf((char) b));
            }
        });

        System.setOut(guiOut); 

        try {
            System.out.println("ETAPE 1 : ANALYSE LEXICALE");
            System.out.println("--------------------------");
            
            ErrorService errorService = new ErrorService();
            Lexer lexer = new Lexer(code, errorService);
            List<Token> tokens = lexer.tokenize();
            
            if (errorService.hasErrors()) {
                System.out.println("Echec lexical. Liste des erreurs :");
                for (int i = 0; i < errorService.getErrors().size(); i++) {
                    System.out.println(errorService.getErrors().get(i));
                }
                return; 
            } 
            
            System.out.println("Succes. Liste des tokens :");
            
            for (int i = 0; i < tokens.size(); i++) {
                Token t = tokens.get(i);
                
                String type = t.getType().toString();
                String val = t.getValeur();
                
                while(type.length() < 15) type = type + " ";
                
                if (val.equals("")) val = " "; 
                
                System.out.println(" " + type + " : " + val);
            }
            
            System.out.println("Nombre de tokens : " + tokens.size());
            System.out.println("");


            System.out.println("ETAPE 2 : ANALYSE SYNTAXIQUE");
            System.out.println("----------------------------");
            
            Parser parser = new Parser(tokens, errorService);
            parser.Z(); 
            
            if (errorService.hasErrors()) {
                System.out.println("Echec syntaxique. Liste des erreurs :");
                for (int i = 0; i < errorService.getErrors().size(); i++) {
                    Error err = errorService.getErrors().get(i);
                    if (err.getType() == Error.ErrorType.SYNTAXIC) {
                        System.out.println(err);
                    }
                }
            } else {
                System.out.println("COMPILATION REUSSIE");
            }
            
        } catch (Exception e) {
            System.out.println("Erreur critique : " + e.getMessage());
        } finally {
            System.setOut(oldOut);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Interface().setVisible(true);
            }
        });
    }
}