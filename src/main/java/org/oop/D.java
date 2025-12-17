package org.oop;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class D {
    //Принцип: DRY.
    //В этом и других методах класса повторяется один и тот же код создания Connection со строкой подключения к БД. При изменении URL/логина/пароля придётся править каждое место, что увеличивает риск ошибок. Рекомендация: вынести создание подключения в отдельный приватный метод или отдельный сервис (getConnection() / DatabaseConfig) и переиспользовать его во всех CRUD‑методах.
    //Принцип: KISS.
    //В initializeDatabase в одной строке объединены несколько SQL‑операций (CREATE TABLE и INSERT), плюс используется длинная конкатенация строк. Такой код сложно читать, отлаживать и модифицировать. Рекомендация: разбить каждую команду SQL на отдельную константу/строку и вызывать executeUpdate для каждой операции отдельно, либо вынести SQL в отдельный файл/скрипт и упростить инициализацию.
    //Принцип: KISS.
    //Названия класса (D) и методов (ca, ga, gai, cu, gubu, uu, cp и т.п.) не дают понять назначение кода без чтения реализации. Это снижает читаемость и усложняет сопровождение. Рекомендация: использовать говорящие имена, отражающие действие: createArticle, getAllArticles, getArticleById, createUser, getUserByUsername, updateUser, changePassword и т.д., а класс D переименовать, например, в DatabaseService или UserArticleRepository.
    //Принцип: YAGNI.
    //В классе присутствуют методы, которые сейчас нигде не вызываются. Они увеличивают объём кода и усложняют чтение, при этом не принося пользы. Рекомендация: удалить неиспользуемые методы, либо подключить их к реальному сценарию использования (меню/сервис) и покрыть тестами, когда функциональность действительно понадобится.
    public void initializeDatabase() {
        {
            String[] initializationQueries = new String[]{
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id SERIAL PRIMARY KEY," +
                            "username VARCHAR(50) NOT NULL," +
                            "password VARCHAR(255) NOT NULL," +
                            "email VARCHAR(100) NOT NULL," +
                            "role VARCHAR(50) NOT NULL DEFAULT 'USER')",

                    "CREATE TABLE IF NOT EXISTS articles (" +
                            "id SERIAL PRIMARY KEY," +
                            "title VARCHAR(255) NOT NULL," +
                            "content TEXT NOT NULL," +
                            "author_id INTEGER," +
                            "FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE);",

                    "CREATE TABLE IF NOT EXISTS comments (" +
                            "id SERIAL PRIMARY KEY," +
                            "article_id INTEGER," +
                            "user_id INTEGER," +
                            "comment_text TEXT NOT NULL," +
                            "FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE," +
                            "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE);" +

                            "INSERT INTO users (username, password, email, role) " +
                            "SELECT 'admin', '$2a$10$ELqr66UvJgnkkN9e6hrYGO.brljJ//Y2MTpMpVfhdmgEUB0wmS2cC', 'admin@example.com', 'ADMIN' " +
                            "WHERE NOT EXISTS (" +
                            "SELECT * FROM users WHERE username = 'admin'" +
                            ");"
            };

            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }

            try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/oopshop", "exampleuser", "examplepass");
                 Statement stmt = conn.createStatement()) {
                for (String sql : initializationQueries) {
                    stmt.execute(sql);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public String hashPassword(String plainTextPassword) {
        // Возвращает хешированный пароль
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }
    public Article ca(Article article) {
        String query = "INSERT INTO articles (title, content, author_id) VALUES (?, ?, ?)";
        try (Connection connection =  DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/oopshop",
                "exampleuser",
                "examplepass"
        );
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, article.title);
            preparedStatement.setString(2, article.content);
            preparedStatement.setLong(3, article.authorId);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating article failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    article.id = generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creating article failed, no ID obtained.");
                }
            }

            return article;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Article gai(long id) {
        String query = "SELECT id, title, content, author_id FROM articles WHERE id = ?";
        try (Connection connection =  DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/oopshop",
                "exampleuser",
                "examplepass"
        );
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Article(
                            resultSet.getLong("id"),
                            resultSet.getString("title"),
                            resultSet.getString("content"),
                            resultSet.getLong("author_id")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Article> ga(String title) {
        List<Article> articles = new ArrayList<>();
        String query = "SELECT id, title, content, author_id FROM articles WHERE title LIKE ?";
        try (Connection connection =  DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/oopshop",
                "exampleuser",
                "examplepass"
        );
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, "%" + title + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    articles.add(new Article(
                            resultSet.getLong("id"),
                            resultSet.getString("title"),
                            resultSet.getString("content"),
                            resultSet.getLong("author_id")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articles;
    }

    public List<Article> ga() {
        List<Article> articles = new ArrayList<>();
        String query = "SELECT id, title, content, author_id FROM articles";
        try (Connection connection =  DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/oopshop",
                "exampleuser",
                "examplepass"
        );
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Article article = new Article(
                        resultSet.getLong("id"),
                        resultSet.getString("title"),
                        resultSet.getString("content"),
                        resultSet.getLong("author_id")

                );
                articles.add(article);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articles;
    }

    //Принцип: KISS.
    //В запросе UPDATE articles есть лишняя запятая перед WHERE, а порядок параметров в PreparedStatement не соответствует полям (author_id и id перепутаны местами). Из‑за этого код либо не выполнится, либо будет обновлять неверные данные. Рекомендация: убрать запятую перед WHERE и выровнять порядок параметров (author_id → article.authorId, id → article.id) так, чтобы он совпадал с порядком ? в запросе.
    public boolean ua(Article article) {
        String query = "UPDATE articles SET title = ?, content = ?, author_id = ?, WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/oopshop",
                "exampleuser",
                "examplepass"
        );
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, article.title);
            preparedStatement.setString(2, article.content);
            preparedStatement.setLong(3, article.id);
            preparedStatement.setLong(4, article.authorId);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean da(long id) {
        String query = "DELETE FROM articles WHERE id = ?";
        try (Connection connection =  DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/oopshop",
                "exampleuser",
                "examplepass"
        );
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, id);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public User cu(User user) {
        // Хеширование пароля перед сохранением в базу данных
        user.password = hashPassword(user.password);

        // SQL запрос для добавления нового пользователя
        String query = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";
        // Прямое соединение с базой данных в методе - нарушает SRP
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/oopshop", "exampleuser", "examplepass");
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, user.username);
            preparedStatement.setString(2, user.password);
            preparedStatement.setString(3, user.email);
            preparedStatement.setString(4, user.role.toString());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Создание пользователя не удалось, изменений не произошло.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.id = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Создание пользователя не удалось, ID не получен.");
                }
            }

            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User gubi(int userId) {
        User user = null;
        String query = "SELECT id, username, password, email, role FROM users WHERE id = ?";

        try (Connection connection =  DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/oopshop",
                "exampleuser",
                "examplepass"
        );
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = new User(
                            resultSet.getInt("id"),
                            resultSet.getString("username"),
                            resultSet.getString("password"), // Пароль должен быть хешированным
                            resultSet.getString("email"),
                            Role.valueOf(resultSet.getString("role"))
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User gubu(String username) {
        User user = null;
        String query = "SELECT id, username, password, email, role FROM users WHERE username = ?";

        try (Connection connection =  DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/oopshop",
                "exampleuser",
                "examplepass"
        );
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = new User(
                            resultSet.getInt("id"),
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getString("email"),
                            Role.valueOf(resultSet.getString("role"))
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public List<User> gau() {
        List<User> users = new ArrayList<>();
        String query = "SELECT id, username, password, email, role FROM users";
        try (Connection connection =  DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/oopshop",
                "exampleuser",
                "examplepass"
        );
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                User user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"), // Напоминание: пароль должен быть каким-то образом захеширован
                        resultSet.getString("email"),
                        Role.valueOf(resultSet.getString("role"))
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean uu(User user) {
        String query = "UPDATE users SET username = ?, password = ?, email = ?, role = ? WHERE id = ?";
        try (Connection connection =  DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/oopshop",
                "exampleuser",
                "examplepass"
        );
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user.username);
            preparedStatement.setString(2, user.password);
            preparedStatement.setString(3, user.email);
            preparedStatement.setString(4, user.role.toString());
            preparedStatement.setInt(5, user.id);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean du(int userId) {
        String query = "DELETE FROM users WHERE id = ?";
        try (Connection connection =  DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/oopshop",
                "exampleuser",
                "examplepass"
        );
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean cur(int userId, Role newRole) {
        String query = "UPDATE users SET role = ? WHERE id = ?";
        try (Connection connection =  DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/oopshop",
                "exampleuser",
                "examplepass"
        );
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, newRole.toString());
            preparedStatement.setInt(2, userId);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean cp(int userId, String newPassword) {
        // Хеширование нового пароля
        String hashedPassword = hashPassword(newPassword);
        String query = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/oopshop",
                "exampleuser",
                "examplepass"
        );
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, hashedPassword);
            preparedStatement.setInt(2, userId);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
