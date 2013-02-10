package com.ambethia.ladu;

import com.ambethia.ladu.screen.LaduScreen;
import com.ambethia.ladu.screen.MenuScreen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class MenuInputListener extends InputListener {
    private LaduScreen screen;

    public MenuInputListener(LaduScreen screen) {
        this.screen = screen;
    }

    public void focus(MenuButton button) {
        screen.selectedButton = button;
        for (MenuButton otherActor : screen.buttons) {
            if (!otherActor.equals(button)) {
                otherActor.stopAnimation();
            }
        }
        button.startAnimation();
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        call();
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        Actor actor = event.getTarget();
        if (actor instanceof MenuButton) {
            focus((MenuButton) actor);
        }
        return true; // so that touchUp will be called.
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor toActor) {
        Actor actor = event.getTarget();
        if (actor instanceof MenuButton) {
            focus((MenuButton) actor);
        }
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {

    }

    public void call() {
    }
}
