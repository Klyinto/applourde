package aled;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class Main extends JFrame {
    private JTextField tailleField;
    private JTextArea resultArea;
    private SecretKeySpec secretKey;  // Clé secrète pour crypter et décrypter les mots de passe
    private String decrypted;  // Mot de passe décrypté
    private String password;  // Mot de passe généré
    private byte[] encrypted;  // Mot de passe crypté

    public Main() {
        super("Générateur de mot de passe");  // Titre de la fenêtre

        // Création d'un nouveau panneau avec une grille de 3 lignes et 1 colonne
        JPanel panel = new JPanel(new GridLayout(3, 1));

        // Création d'un nouveau label pour afficher le texte "Taille du mot de passe:"
        JLabel label1 = new JLabel("Taille du mot de passe:");
        panel.add(label1);  // Ajout du label au panneau

        // Création d'un nouveau champ de texte pour permettre à l'utilisateur de saisir la taille du mot de passe
        tailleField = new JTextField();
        panel.add(tailleField);  // Ajout du champ de texte au panneau

        // Création d'un nouveau bouton pour permettre à l'utilisateur de générer un nouveau mot de passe
        JButton generateButton = new JButton("Générer");
        panel.add(generateButton);  // Ajout du bouton au panneau

        // Ajout d'un écouteur d'événements pour le bouton "Générer"
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Récupération de la taille du mot de passe saisie par l'utilisateur
                    int tailleP = Integer.parseInt(tailleField.getText());

                    // Génération d'un nouveau mot de passe aléatoire
                    password = generatePassword(tailleP);

                    // Cryptage du mot de passe généré
                    encrypted = encrypt(password);

                    // Décryptage du mot de passe crypté
                    decrypted = decrypt(encrypted);

                    // Affichage du mot de passe généré et du mot de passe crypté dans le champ de texte "resultArea"
                    resultArea.setText("Mot de passe généré: " + password + "\n"
                            + "Mot de passe crypté: " + new String(encrypted, "UTF-8"));

                    // Enregistrement du mot de passe crypté dans un fichier texte
                    savePasswordToFile(new String(encrypted, StandardCharsets.UTF_8));
                } catch (Exception ex) {
                    // Affichage d'un message d'erreur en cas de problème lors de la génération du mot de passe
                    JOptionPane.showMessageDialog(null, "Erreur: " + ex.getMessage());
                }
            }
        });
        
     // Crée un nouveau JButton avec le texte "Décrypter"
        JButton decryptButton = new JButton("Décrypter");

        // Ajoute le bouton à un JPanel appelé "panel"
        panel.add(decryptButton);

        // Ajoute un ActionListener au bouton qui se déclenchera lorsque l'utilisateur cliquera dessus
        decryptButton.addActionListener(new ActionListener() {

            // Implémente la méthode actionPerformed() de l'interface ActionListener
            @Override
            public void actionPerformed(ActionEvent e) {

                // Essaye d'exécuter le code suivant
                try {
                    // Met à jour le texte dans un JTextArea appelé "resultArea"
                    resultArea.setText("Mot de passe généré: " + password + "\n"
                        + "Mot de passe crypté: " + new String(encrypted, "UTF-8") + "\n"
                        + "Mot de passe décrypté: " + decrypted);

                // Attrape une exception si elle se produit pendant l'exécution du code ci-dessus
                } catch (Exception ex) {
                    // Affiche une boîte de dialogue avec un message d'erreur contenant le message d'erreur de l'exception
                    JOptionPane.showMessageDialog(null, "Erreur: " + ex.getMessage());
                }
            }
        });

     // Création d'une JTextArea pour afficher les résultats
        resultArea = new JTextArea();
        resultArea.setEditable(false);

        // Création d'un JScrollPane pour permettre le défilement dans la JTextArea
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Ajout du panel en haut de la fenêtre et du JScrollPane au centre
        getContentPane().add(panel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Configuration de la fenêtre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setVisible(true);
    }
    
    public static void main(String[] args) {
        new Main();
    }
    

    private void savePasswordToFile(String password) {
        try {
            // Ouvre une boîte de dialogue pour permettre à l'utilisateur de sélectionner un dossier
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Sélectionnez un dossier pour enregistrer le fichier");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int option = fileChooser.showSaveDialog(null);

            if (option == JFileChooser.APPROVE_OPTION) {
                // Récupère le dossier sélectionné par l'utilisateur
                File selectedFolder = fileChooser.getSelectedFile();
                String fileName = selectedFolder.getAbsolutePath() + "/passwords.txt";
                FileWriter writer = new FileWriter(fileName, true);
                writer.write(password + "\n"); // Écriture du mot de passe dans le fichier avec un saut de ligne
                writer.close();
                // Affichage d'un message de confirmation
                JOptionPane.showMessageDialog(null, "Le mot de passe a été enregistré dans le fichier " + fileName);
            }
        } catch (IOException ex) {
            // Affichage d'un message d'erreur en cas de problème lors de l'écriture dans le fichier
            JOptionPane.showMessageDialog(null, "Erreur lors de l'enregistrement du mot de passe dans le fichier: " + ex.getMessage());
        }
    }
     // Génère un mot de passe aléatoire avec une longueur donnée
        private String generatePassword(int taille) {
            String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+{}:\"<>?[];',./\\`~|";
            StringBuilder sb = new StringBuilder();
            SecureRandom random = new SecureRandom();
            for (int i = 0; i < taille; i++) {
                int index = random.nextInt(characters.length());
                sb.append(characters.charAt(index));
            }
            return sb.toString();
        }

        // Chiffre un mot de passe en utilisant l'algorithme AES
        private byte[] encrypt(String password) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            SecureRandom random = new SecureRandom();
            byte[] key = new byte[16];
            random.nextBytes(key);
            secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(password.getBytes());
        }

        // Déchiffre un mot de passe chiffré avec l'algorithme AES
        private String decrypt(byte[] encrypted) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(encrypted);
            return new String(decryptedBytes);
        }
}