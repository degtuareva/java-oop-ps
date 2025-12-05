package org.oop.commands;

import org.oop.api.ICommand;
import org.oop.api.ICommentService;
import org.oop.commands.menu.CommentMenu;
import org.oop.commands.menu.BaseCommand;
import org.oop.di.Injector;
import org.oop.model.Comment;

import java.util.List;

public class ShowCommentCommand extends BaseCommand {

    private final ICommentService commentService;

    public ShowCommentCommand() {
        this.commentService = Injector.getInstance().getService(ICommentService.class);
    }

    @Override
    public ICommand execute() {
        long articleId = Long.parseLong(
                ioService.prompt("Введите ID статьи, для которой нужно показать комментарии: ")
        );

        List<Comment> comments = commentService.getCommentsByArticleId(articleId);

        if (comments.isEmpty()) {
            ioService.printLine("Для статьи с ID " + articleId + " пока нет комментариев.");
        } else {
            ioService.printLine("Комментарии к статье " + articleId + ":");
            for (Comment c : comments) {
                ioService.printLine("------------------------------");
                ioService.printLine(c.toString());
                ioService.printLine("ID: " + c.getId());
                ioService.printLine("Author(user_id): "+c.getAuthorId());
                ioService.printLine("Text: "+c.getContent());
                ioService.printLine("Created at: "+c.getCreatedAt());
            }
        }

        return new CommentMenu();
    }

    @Override
    public String getDescription() {
        return "Показать комментарии к статье";
    }

}
