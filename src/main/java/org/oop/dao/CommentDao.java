package org.oop.dao;

import org.oop.api.dao.ICommentDao;
import org.oop.model.Comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentDao extends Dao implements ICommentDao {

    @Override
    public Comment createComment(Comment comment) {
        String sql = "INSERT INTO comments (article_id, author_id, content, created_at) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, comment.getArticleId());
            ps.setLong(2, comment.getAuthorId());
            ps.setString(3, comment.getContent());
            ps.setTimestamp(4, Timestamp.valueOf(
                    comment.getCreatedAt() != null ? comment.getCreatedAt() : LocalDateTime.now()
            ));

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Creating comment failed, no rows affected.");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    comment.setId(keys.getLong(1));
                } else {
                    throw new SQLException("Creating comment failed, no ID obtained.");
                }
            }

            return comment;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Comment getCommentById(long id) {
        String sql = "SELECT id, article_id, author_id, content, created_at " +
                "FROM comments WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToComment(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Comment> getCommentsByArticleId(long articleId) {
        List<Comment> result = new ArrayList<>();
        String sql = "SELECT id, article_id, author_id, content, created_at " +
                "FROM comments WHERE article_id = ? ORDER BY created_at";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, articleId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRowToComment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean deleteComment(long id) {
        String sql = "DELETE FROM comments WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Comment mapRowToComment(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        Long articleId = rs.getLong("article_id");
        Long authorId = rs.getLong("author_id");
        String content = rs.getString("content");
        Timestamp ts = rs.getTimestamp("created_at");
        LocalDateTime createdAt = ts != null ? ts.toLocalDateTime() : null;

        return new Comment(id, articleId, authorId, content, createdAt);
    }
}
