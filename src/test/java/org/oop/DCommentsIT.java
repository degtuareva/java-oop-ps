package org.oop;

import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DCommentsIT {

    private D db;
    private long userId;
    private long articleId;

    @Before
    public void setUp() throws Exception {
        db = new D();
        db.initializeDatabase();

        try (Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/oopshop",
                "exampleuser",
                "examplepass"
        );
             Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM comments");
            st.executeUpdate("DELETE FROM articles");
            st.executeUpdate("DELETE FROM users");
        }

        User u = new User(0, "testuser", "pwd", "t@example.com", Role.USER);
        u = db.cu(u);
        assertNotNull(u);
        userId = u.id;

        Article a = new Article(0L, "Test article", "Content", (long) userId);
        a = db.ca(a);
        assertNotNull(a);
        articleId = a.id;
    }

    @Test
    public void addAndReadComment() {
        Comment c = new Comment(null, articleId, (long) userId, "Hello from test");
        Comment saved = db.cc(c);
        assertNotNull(saved);
        assertNotNull(saved.id);

        List<Comment> comments = db.gcByArticleId(articleId);
        assertEquals(1, comments.size());
        assertEquals("Hello from test", comments.get(0).text);
    }
}
