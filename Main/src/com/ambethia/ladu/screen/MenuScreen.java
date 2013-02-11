package com.ambethia.ladu.screen;

import com.ambethia.ladu.Ladu;
import com.ambethia.ladu.MenuButton;
import com.ambethia.ladu.MenuInputListener;
import com.badlogic.gdx.Input.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MenuScreen extends LaduScreen {

    public MenuScreen(Ladu game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        final Image title = createImage("menu-title");
        final Image ladu = createImage("menu-ladu");

        final MenuButton playButton = createButton("menu-play", new MenuInputListener(this) {
            @Override
            public void call() {
                if (game.currentLevel > 1) {
                    // Continuing? Show the level select screen.
                    game.transitionTo(Ladu.Screens.LEVELS);
                } else {
                    // New game? Go straight to the first level.
                    game.transitionTo(Ladu.Screens.GAME);
                }

            }
        });
        buttons.add(playButton);

        final MenuButton creditsButton = createButton("menu-credits", new MenuInputListener(this) {
            @Override
            public void call() {
                game.transitionTo(Ladu.Screens.CREDITS);
            }
        });
        buttons.add(creditsButton);

        final MenuButton quitButton = createButton("menu-quit", new MenuInputListener(this) {
            @Override
            public void call() {
                game.quit();
            }
        });
        buttons.add(quitButton);

        final Table table = new Table();
        table.setFillParent(true);
        table.add(ladu).colspan(3).expand().bottom();
        table.row();
        table.add(title).colspan(3).expandY().top().padTop(20);
        table.row();
        for (Actor button : buttons) { table.add(button).expandY(); }
        stage.addActor(table);

        // Pre-select play button
        playButton.focus();
    }

    private Image createImage(String textureName) {
        TextureAtlas.AtlasRegion region = getAtlas().findRegion(textureName);
        Image image = new Image(new TextureRegionDrawable(region));
        image.setOrigin(image.getWidth() / 2, image.getHeight() / 2);
        return image;
    }

    private MenuButton createButton(String textureName, InputListener inputListener) {
        TextureAtlas.AtlasRegion region = getAtlas().findRegion(textureName);
        MenuButton button = new MenuButton(new TextureRegionDrawable(region));
        button.setOrigin(button.getWidth() / 2, button.getHeight() / 2);
        button.addListener(inputListener);
        return button;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public boolean keyUp(int keycode) {
        super.keyUp(keycode);
        switch (keycode) {
            case Keys.ESCAPE:
                game.quit();
                break;
            case Keys.SPACE:
            case Keys.ENTER:
                selectedButton.call();
                break;
            case Keys.LEFT:
            case Keys.DOWN:
                selectNextMenuItem();
                break;
            case Keys.RIGHT:
            case Keys.UP:
                selectPreviousMenuItem();
                break;
            case Keys.T:
                game.transitionTo(Ladu.Screens.TEST);
                break;
            default:
                break;
        }
        return true;
    }

    // Some shenanigans here because Java can return a negative number instead of wrapping with modulo if the left operand is negative.
    private void selectPreviousMenuItem() {
        buttons.get((((buttons.indexOf(selectedButton, true) - 1) % buttons.size) + buttons.size) % buttons.size).focus();
    }

    private void selectNextMenuItem() {
        buttons.get((buttons.indexOf(selectedButton, true) + 1) % buttons.size).focus();
    }
}
