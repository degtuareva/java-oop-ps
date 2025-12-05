package org.oop.commands.menu;

import org.oop.commands.*;
import org.oop.api.ICommand;


public class ArticleMenu extends BaseCommand {

    public ArticleMenu() {
        initializeMenu();
    }

    private void initializeMenu() {
        commandSuppliers.put(1, CreateArticleCommand::new);
        commandSuppliers.put(2, DeleteArticleCommand::new);
        commandSuppliers.put(3, MainMenu::new);
        commandSuppliers.put(4, CommentMenu::new); // новый пункт
        commandSuppliers.put(0, MainMenu::new);
        commandSuppliers.put(5,AddCommentCommand::new);
        commandSuppliers.put(6,ShowCommentCommand::new);
    }

    @Override
    public ICommand execute() {
        return selectMenu();
    }


    @Override
    public String getDescription() {
        return "Управление статьями";
    }
}
