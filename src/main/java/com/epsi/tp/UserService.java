package com.epsi.tp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserService {

    // Correction : utilisation d'un Logger au lieu de System.out.println
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    // Correction : le secret n'est plus dans le code source.
    // Il est lu depuis l'environnement d'exécution (variable d'environnement),
    // injecté au déploiement (ex : docker run -e DB_PASSWORD=...).
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static final String ADMIN_PASSWORD = System.getenv("ADMIN_PASSWORD");

    public void login(String username, String password) {
        // Correction : Logger paramétré au lieu de concaténation + System.out
        LOGGER.log(Level.INFO, "Tentative de connexion de l''utilisateur : {0}", username);

        // Correction : plus d'identifiants en dur, le mot de passe admin
        // vient de l'environnement. Objects.equals évite les NullPointerException.
        if ("admin".equals(username) && ADMIN_PASSWORD != null
                && Objects.equals(password, ADMIN_PASSWORD)) {
            LOGGER.info("Administrateur connecté avec succès.");
        } else {
            LOGGER.warning("Identifiants invalides.");
        }
        // Correction : suppression du code factice (division par zéro)
        // et du bloc catch vide qui avalait l'erreur silencieusement.
    }

    public void getUserDetails(String username) {
        // Correction : requête paramétrée (PreparedStatement) -> injection SQL impossible,
        // l'entrée utilisateur ne peut plus être interprétée comme du code SQL.
        String query = "SELECT username FROM users WHERE username = ?";

        // Correction : try-with-resources -> les ressources JDBC sont fermées
        // automatiquement, même en cas d'exception (plus de fuite de ressources,
        // plus de finally avec des catch vides).
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String foundUser = rs.getString("username");
                    LOGGER.log(Level.INFO, "Utilisateur trouvé : {0}", foundUser);
                }
            }
        } catch (SQLException e) {
            // Correction : exception spécifique (SQLException) au lieu d'Exception,
            // et journalisation via le Logger au lieu de printStackTrace().
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de l''utilisateur", e);
        }
    }

    // Correction : la complexité cyclomatique est réduite en extrayant la logique
    // dans une méthode privée à retours anticipés (early returns), sans imbrication.
    public void complexMethod(int a, int b, int c) {
        LOGGER.info(() -> buildSignMessage(a, b, c));
    }

    private String buildSignMessage(int a, int b, int c) {
        if (a <= 0) {
            return "A est négatif";
        }
        if (b > 0 && c > 0) {
            return "Tous positifs";
        }
        if (b > 0) {
            return "C est négatif";
        }
        if (c > 0) {
            return "B est négatif";
        }
        return "B et C sont négatifs";
    }
}