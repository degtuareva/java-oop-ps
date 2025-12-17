package org.oop;

public class Article {
    //Класс содержит публичные поля -это нарушает инкапсуляцию и нарушает принцип KISS-открывает
    // внутреннее состояние наружу. Лучше сделать поля private и предоставить доступ к полям через
    // геттеры и сеттеры
    public Long id;
    public String title;
    public String content;
    public Long authorId;

    // Конструктор по умолчанию
    public Article() {
    }

    // Конструктор с параметрами
    public Article(Long id, String title, String content, Long authorId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Заголовок: " + title + "\nСодержимое: " + content;
    }
}
