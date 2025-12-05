package org.oop.service;

import org.oop.api.IArticleService;
import org.oop.api.IAuthService;
import org.oop.api.dao.IArticleDao;
import org.oop.di.Injector;
import org.oop.model.Article;
import org.oop.model.Comment;

import java.util.List;

public class ArticleService implements IArticleService {
    private final IArticleDao articleDao;
    private final IAuthService authService;

    public ArticleService() {
        this.articleDao = Injector.getInstance().getService(IArticleDao.class);
        this.authService = Injector.getInstance().getService(IAuthService.class);
    }

    // конструктор для тестов
    public ArticleService(IArticleDao articleDao, IAuthService authService) {
        this.articleDao = articleDao;
        this.authService = authService;
    }

    @Override
    public Article createArticle(String title, String content) {
        long authorId = authService.getCurrentUserId();
        Article newArticle = new Article(0L, title, content, authorId == -1 ? 0L : authorId);
        return articleDao.createArticle(newArticle);
    }

    @Override
    public Article getArticleById(long id) {
        return articleDao.getArticleById(id);
    }

    @Override
    public List<Article> getArticlesByTitle(String title) {
        return articleDao.getArticlesByTitle(title);
    }

    @Override
    public List<Article> getAllArticles() {
        return articleDao.getAllArticles();
    }

    @Override
    public List<Comment> getCommentsByArticleId(int articleId) {
        return List.of();
    }

    @Override
    public Comment addCommentToArticle(int articleId, String comment) {
        return null;
    }

    @Override
    public boolean updateArticle(long id, String title, String content) {
        Article article = articleDao.getArticleById(id);
        if (article == null) {
            return false;
        }
        long authorId = authService.getCurrentUserId();

        article = new Article(id, title, content, authorId);
        return articleDao.updateArticle(article);
    }

    @Override
    public boolean deleteArticle(long id) {
        return articleDao.deleteArticle(id);
    }
}
