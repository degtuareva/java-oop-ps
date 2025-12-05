package org.oop.commands;

import org.oop.api.ICommand;
import org.oop.api.ICommentService;
import org.oop.commands.menu.CommentMenu;
import org.oop.commands.menu.BaseCommand;
import org.oop.di.Injector;
import org.oop.model.Comment;

public class AddCommentCommand extends BaseCommand {

    private final ICommentService commentService;

    public AddCommentCommand() {
        this.commentService = Injector.getInstance().getService(ICommentService.class);
    }

    @Override
    public ICommand execute() {
        long articleId = Long.parseLong(
                ioService.prompt("Введите ID статьи, к которой хотите добавить комментарий: ")
        );
        String content = ioService.prompt("Введите текст комментария: ");

        Comment comment = commentService.addComment(articleId, content);

        if (comment == null) {
            ioService.printLine("Не удалось добавить комментарий. Проверьте ID статьи и авторизацию.");
        } else {
            ioService.printLine("Комментарий добавлен с ID: " + comment.getId());
        }

        return new CommentMenu();
    }

    @Override
    public String getDescription() {
        return "Добавить комментарий к статье";
    }
}
