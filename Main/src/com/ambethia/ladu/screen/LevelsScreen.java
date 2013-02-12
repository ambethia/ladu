package com.ambethia.ladu.screen;

import com.ambethia.ladu.Ladu;
import com.ambethia.ladu.MenuButton;
import com.ambethia.ladu.MenuInputListener;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

public class LevelsScreen extends LaduScreen {
    private int selectedLevel = 1;

    @Override
    public void show() {
        super.show();
        final Table table = new Table();
        table.setFillParent(true);
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                int level = (y * 10 + x) + 1;
                MenuButton button;
                if (level <= game.numLevels) {
                    if (level <= game.unlockedLevel) {
                        button = createButton("level-cleared", new MenuInputListener(this) {
                            @Override
                            public void call() {
                                game.transitionTo(Ladu.Screens.GAME);
                            }
                        });
                        if (level == game.currentLevel) {
                            selectedLevel = level;
                            selectedButton = button;
                            selectedButton.focus();
                        }
                        buttons.add(button);
                    } else {
                        button = createButton("level-locked", new MenuInputListener(this));
                        button.disable();
                    }
                } else {
                    button = createButton("level-locked", new MenuInputListener(this));
                    button.disable();
                }
                table.add(button).expand();
            }
            table.row();
        }
        stage.addActor(table);
    }

    @Override
    public boolean keyUp(int keycode) {
        super.keyUp(keycode);
        switch (keycode) {
            case Input.Keys.ESCAPE:
                game.transitionTo(Ladu.Screens.MENU);
                break;
            case Input.Keys.ENTER:
            case Input.Keys.SPACE:
                game.setCurrentLevel(selectedLevel);
                game.transitionTo(Ladu.Screens.GAME);
                break;
            case Input.Keys.LEFT:
            case Input.Keys.DOWN:
                selectPreviousMenuItem();
                break;
            case Input.Keys.RIGHT:
            case Input.Keys.UP:
                selectNextMenuItem();
                break;
            default:
                break;
        }
        return true;
    }

    private MenuButton createButton(String textureName, InputListener inputListener) {
        TextureAtlas.AtlasRegion region = getAtlas().findRegion(textureName);
        MenuButton button = new MenuButton(new TextureRegionDrawable(region));
        button.setOrigin(button.getWidth() / 2, button.getHeight() / 2);
        button.addListener(inputListener);
        return button;
    }

    private void selectPreviousMenuItem() {
        int index = buttons.indexOf(selectedButton, true) - 1;
        buttons.get(((index % buttons.size) + buttons.size) % buttons.size).focus();
        selectedLevel = index + 1;
    }

    private void selectNextMenuItem() {
        int index = buttons.indexOf(selectedButton, true) + 1;
        buttons.get(index % buttons.size).focus();
        selectedLevel = index + 1;
    }
}
