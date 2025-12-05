package org.oop.service;

import org.junit.Before;
import org.junit.Test;
import org.oop.api.IAuthService;
import org.oop.api.dao.IArticleDao;
import org.oop.model.Article;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ArticleServiceTest {

    private IArticleDao articleDao;
    private IAuthService authService;
    private ArticleService articleService;

    @Before
    public void setUp() {
        articleDao = mock(IArticleDao.class);
        authService = mock(IAuthService.class);
        articleService = new ArticleService(articleDao, authService);
    }

    @Test
    public void createArticle_setsAuthorFromAuthService() {
        when(authService.getCurrentUserId()).thenReturn(10L);

        Article saved = new Article(1L, "Title", "Content", 10L);
        when(articleDao.createArticle(any(Article.class))).thenReturn(saved);

        Article result = articleService.createArticle("Title", "Content");

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getId());
        verify(articleDao, times(1)).createArticle(any(Article.class));
    }

    @Test
    public void updateArticle_updatesExistingArticle() {
        long articleId = 1L;
        Article existing = new Article(articleId, "Old", "Old", 5L);
        when(articleDao.getArticleById(articleId)).thenReturn(existing);
        when(authService.getCurrentUserId()).thenReturn(5L);
        when(articleDao.updateArticle(any(Article.class))).thenReturn(true);

        boolean updated = articleService.updateArticle(articleId, "New", "New content");

        assertTrue(updated);
        verify(articleDao, times(1)).updateArticle(any(Article.class));
    }

    @Test
    public void updateArticle_returnsFalse_whenArticleNotFound() {
        long articleId = 1L;
        when(articleDao.getArticleById(articleId)).thenReturn(null);

        boolean updated = articleService.updateArticle(articleId, "New", "New content");

        assertFalse(updated);
        verify(articleDao, never()).updateArticle(any(Article.class));
    }

    @Test
    public void getAllArticles_delegatesToDao() {
        when(articleDao.getAllArticles())
                .thenReturn(Arrays.asList(
                        new Article(1L, "A", "C", 1L),
                        new Article(2L, "B", "C2", 2L)
                ));

        List<Article> articles = articleService.getAllArticles();

        assertEquals(2, articles.size());
        verify(articleDao, times(1)).getAllArticles();
    }
}
