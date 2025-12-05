package org.oop.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.oop.api.dao.ICommentDao;
import org.oop.model.Comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CommentDaoIT {

    private ICommentDao commentDao;
    private TestDao testDao;
    private long testArticleId;
    private long testUserId;

    @Before
    public void setUp() throws SQLException {
        commentDao = new CommentDao();
        testDao = new TestDao();

        // очистка таблиц
        try (Connection conn = testDao.getConnection();
             PreparedStatement ps1 = conn.prepareStatement("DELETE FROM comments");
             PreparedStatement ps2 = conn.prepareStatement("DELETE FROM articles");
             PreparedStatement ps3 = conn.prepareStatement("DELETE FROM users")) {
            ps1.executeUpdate();
            ps2.executeUpdate();
            ps3.executeUpdate();
        }

        // создаём пользователя
        try (Connection conn = testDao.getConnection();
             PreparedStatement psUser = conn.prepareStatement(
                     "INSERT INTO users (username, password, email, role) " +
                             "VALUES ('testuser', 'pwd', 'test@example.com', 'USER')",
                     java.sql.Statement.RETURN_GENERATED_KEYS
             )) {

            psUser.executeUpdate();
            try (java.sql.ResultSet keys = psUser.getGeneratedKeys()) {
                if (keys.next()) {
                    testUserId = keys.getLong(1);
                } else {
                    throw new SQLException("Failed to get generated user id");
                }
            }
        }

        // создаём статью
        try (Connection conn = testDao.getConnection();
             PreparedStatement psArticle = conn.prepareStatement(
                     "INSERT INTO articles (title, content, author_id) " +
                             "VALUES ('Test article', 'Content', ?)",
                     java.sql.Statement.RETURN_GENERATED_KEYS
             )) {

            psArticle.setLong(1, testUserId);
            psArticle.executeUpdate();
            try (java.sql.ResultSet keys = psArticle.getGeneratedKeys()) {
                if (keys.next()) {
                    testArticleId = keys.getLong(1);
                } else {
                    throw new SQLException("Failed to get generated article id");
                }
            }
        }
    }

    @After
    public void tearDown() throws SQLException {
        try (Connection conn = testDao.getConnection();
             PreparedStatement ps1 = conn.prepareStatement("DELETE FROM comments");
             PreparedStatement ps2 = conn.prepareStatement("DELETE FROM articles");
             PreparedStatement ps3 = conn.prepareStatement("DELETE FROM users")) {
            ps1.executeUpdate();
            ps2.executeUpdate();
            ps3.executeUpdate();
        }
    }

    @Test
    public void createAndReadComment() {
        Comment toSave = new Comment(
                null,
                testArticleId,
                testUserId,
                "Integration test comment",
                LocalDateTime.now()
        );

        Comment saved = commentDao.createComment(toSave);
        assertNotNull(saved);
        assertNotNull(saved.getId());

        List<Comment> byArticle = commentDao.getCommentsByArticleId(testArticleId);
        assertEquals(1, byArticle.size());
        assertEquals("Integration test comment", byArticle.get(0).getContent());
    }
}
