package com.ambethia.ladu.screen;

import com.ambethia.ladu.Ladu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class CreditsScreen extends LaduScreen {
    private Label creditsLabel;

    @Override
    public void show() {
        super.show();
        String credits = "THE LADU\n\nProgramming & Audio by\nJason L Perry\n\nArt by\nJames Basom Seaman\n\nhttp://ambethia.com";
        creditsLabel = new Label(credits, new Label.LabelStyle(font, Color.WHITE));
        creditsLabel.setAlignment(Align.center, Align.center);
        stage.addActor(creditsLabel);
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            game.transitionTo(Ladu.Screens.MENU);
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        creditsLabel.setPosition(width/2 - creditsLabel.getWidth()/2, height/2 - creditsLabel.getHeight()/2);
    }
}
