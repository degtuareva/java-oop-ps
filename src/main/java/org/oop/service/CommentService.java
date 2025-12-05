package org.oop.service;

import org.oop.api.IArticleService;
import org.oop.api.IAuthService;
import org.oop.api.ICommentService;
import org.oop.api.dao.ICommentDao;
import org.oop.di.Injector;
import org.oop.model.Article;
import org.oop.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

public class CommentService implements ICommentService {

    private final ICommentDao commentDao;
    private final IArticleService articleService;
    private final IAuthService authService;

    // Конструктор для боевого кода (через Injector)
    public CommentService() {
        this.commentDao = Injector.getInstance().getService(ICommentDao.class);
        this.articleService = Injector.getInstance().getService(IArticleService.class);
        this.authService = Injector.getInstance().getService(IAuthService.class);
    }

    // Конструктор для тестов (внедряем моки)
    public CommentService(ICommentDao commentDao,
                          IArticleService articleService,
                          IAuthService authService) {
        this.commentDao = commentDao;
        this.articleService = articleService;
        this.authService = authService;
    }

    @Override
    public Comment addComment(long articleId, String content) {
        Article article = articleService.getArticleById(articleId);
        if (article == null) {
            return null;
        }

        long authorId = authService.getCurrentUserId();
        if (authorId <= 0) {
            return null;
        }

        Comment comment = new Comment(
                null,
                articleId,
                authorId,
                content,
                LocalDateTime.now()
        );
        return commentDao.createComment(comment);
    }

    @Override
    public Comment getCommentById(long id) {
        return commentDao.getCommentById(id);
    }

    @Override
    public List<Comment> getCommentsByArticleId(long articleId) {
        return commentDao.getCommentsByArticleId(articleId);
    }

    @Override
    public boolean deleteComment(long id) {
        return commentDao.deleteComment(id);
    }
}
