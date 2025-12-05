package org.oop.api;

import org.oop.model.Article;
import org.oop.model.Comment;

import java.util.List;

public interface IArticleService {
    Article createArticle(String title, String content);

    Article getArticleById(long id);

    List<Article> getArticlesByTitle(String title);

    List<Article> getAllArticles();

    List<Comment> getCommentsByArticleId(int articleId);

    Comment addCommentToArticle(int articleId, String comment);

    boolean updateArticle(long id, String title, String content);

    boolean deleteArticle(long id);
}

