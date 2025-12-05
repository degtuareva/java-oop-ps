package org.oop.model;

import java.time.LocalDateTime;

public class Comment {
    private Long id;
    private Long articleId;
    private Long authorId;
    private String content;
    private LocalDateTime createdAt;

    public Comment() {
    }

    public Comment(Long id, Long articleId, Long authorId, String content, LocalDateTime createdAt) {
        this.id = id;
        this.articleId = articleId;
        this.authorId = authorId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getArticleId() {
        return articleId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Комментарий #" + id +
                " (articleId=" + articleId + ", authorId=" + authorId + ")\n" +
                createdAt + "\n" +
                content;
    }
}
