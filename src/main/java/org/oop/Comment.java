package org.oop;

public class Comment {
    public Long id;
    public Long articleId;
    public Long userId;
    public String text;

    public Comment() {
    }

    public Comment(Long id, Long articleId, Long userId, String text) {
        this.id = id;
        this.articleId = articleId;
        this.userId = userId;
        this.text = text;
    }

    @Override
    public String toString() {
        return "Comment{id=" + id +
                ", articleId=" + articleId +
                ", userId=" + userId +
                ", text='" + text + '\'' +
                '}';
    }
}

