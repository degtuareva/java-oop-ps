package org.oop.api.dao;

import org.oop.model.Comment;

import java.util.List;

public interface ICommentDao {
    Comment createComment(Comment comment);

    Comment getCommentById(long id);

    List<Comment> getCommentsByArticleId(long articleId);

    boolean deleteComment(long id);
}
