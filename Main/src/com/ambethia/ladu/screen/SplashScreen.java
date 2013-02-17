package com.ambethia.ladu.screen;

import com.ambethia.ladu.Ladu;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;


public class SplashScreen extends LaduScreen {
    private Image splashImage;

    @Override
    public void show() {
        Ladu.toggleFullscreen();
        super.show();
        TextureAtlas.AtlasRegion splashRegion = getAtlas().findRegion("splash");
        Drawable splashDrawable = new TextureRegionDrawable(splashRegion);
        splashImage = new Image(splashDrawable);
        stage.addActor(splashImage);
        stage.addAction(sequence(fadeIn(1.0f), delay(1.0f), new Action() {
            @Override
            public boolean act(float delta) {
                game.transitionTo(Ladu.Screens.MENU);
                return true;
            }
        }));
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        splashImage.setPosition(Math.round(width/2 - splashImage.getWidth()/2), Math.round(height/2 - splashImage.getHeight()/2));

    }
}
