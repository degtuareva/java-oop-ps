package org.oop.api;

import org.oop.model.Comment;

import java.util.List;

public interface ICommentService {
    Comment addComment(long articleId, String content);

    Comment getCommentById(long id);

    List<Comment> getCommentsByArticleId(long articleId);

    boolean deleteComment(long id);
}
