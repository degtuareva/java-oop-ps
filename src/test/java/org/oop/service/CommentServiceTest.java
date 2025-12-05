package org.oop.service;

import org.junit.Before;
import org.junit.Test;
import org.oop.api.IArticleService;
import org.oop.api.IAuthService;
import org.oop.api.dao.ICommentDao;
import org.oop.model.Article;
import org.oop.model.Comment;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CommentServiceTest {

    private ICommentDao commentDao;
    private IArticleService articleService;
    private IAuthService authService;
    private CommentService commentService;

    @Before
    public void setUp() {
        commentDao = mock(ICommentDao.class);
        articleService = mock(IArticleService.class);
        authService = mock(IAuthService.class);

        // используем тестовый конструктор
        commentService = new CommentService(commentDao, articleService, authService);
    }

    @Test
    public void addComment_createsComment_whenArticleExistsAndUserAuthorized() {
        long articleId = 1L;
        long userId = 10L;

        when(articleService.getArticleById(articleId))
                .thenReturn(new Article(articleId, "Title", "Content", userId));
        when(authService.getCurrentUserId()).thenReturn(userId);

        Comment saved = new Comment();
        saved.setId(100L);
        when(commentDao.createComment(any(Comment.class))).thenReturn(saved);

        Comment result = commentService.addComment(articleId, "Test comment");

        assertNotNull(result);
        assertEquals(Long.valueOf(100L), result.getId());
        verify(commentDao, times(1)).createComment(any(Comment.class));
    }

    @Test
    public void addComment_returnsNull_whenArticleNotFound() {
        long articleId = 1L;

        when(articleService.getArticleById(articleId)).thenReturn(null);

        Comment result = commentService.addComment(articleId, "Test comment");

        assertNull(result);
        verify(commentDao, never()).createComment(any(Comment.class));
    }

    @Test
    public void addComment_returnsNull_whenUserNotAuthorized() {
        long articleId = 1L;

        when(articleService.getArticleById(articleId))
                .thenReturn(new Article(articleId, "Title", "Content", 1L));
        when(authService.getCurrentUserId()).thenReturn(-1L);

        Comment result = commentService.addComment(articleId, "Test comment");

        assertNull(result);
        verify(commentDao, never()).createComment(any(Comment.class));
    }

    @Test
    public void getCommentsByArticleId_delegatesToDao() {
        long articleId = 1L;
        when(commentDao.getCommentsByArticleId(articleId))
                .thenReturn(Collections.singletonList(new Comment()));

        List<Comment> comments = commentService.getCommentsByArticleId(articleId);

        assertEquals(1, comments.size());
        verify(commentDao, times(1)).getCommentsByArticleId(articleId);
    }
}
