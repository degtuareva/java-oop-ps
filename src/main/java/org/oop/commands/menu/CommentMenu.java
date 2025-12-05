package org.oop.commands.menu;

import org.oop.api.ICommand;
import org.oop.commands.AddCommentCommand;
import org.oop.commands.ShowCommentCommand;

public class CommentMenu extends BaseCommand {

    public CommentMenu() {
        initializeMenu();
    }

    private void initializeMenu() {
        commandSuppliers.put(1, AddCommentCommand::new);
        commandSuppliers.put(2, ShowCommentCommand::new);
        // по желанию: команду удаления комментария
        // commandSuppliers.put(3, DeleteCommentCommand::new);
        commandSuppliers.put(0, MainMenu::new); // если в проекте 0 = назад/в главное
    }

    @Override
    public ICommand execute() {
        return selectMenu();
    }

    @Override
    public String getDescription() {
        return "Управление комментариями";
    }
}
