package com.ambethia.ladu.screen;

import com.ambethia.ladu.Ladu;
import com.ambethia.ladu.MenuButton;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class LaduScreen implements Screen, InputProcessor {
    public Ladu game;
    protected final Stage stage;
    public final SpriteBatch batch;
    public final BitmapFont font;
    private TextureAtlas atlas;
    public Image modal;
    private InputMultiplexer inputMultiplexer;
    protected float fadeInTime = 0.75f;
    protected float fadeInDelay = 0.0f;
    protected float fadeOutTime = 0.75f;
    protected float fadeOutDelay = 0.0f;
    public Array<MenuButton> buttons = new Array();
    public MenuButton selectedButton;

    public LaduScreen(Ladu game) {
        inputMultiplexer = new InputMultiplexer(this);
        this.game = game;
        this.stage = new Stage(0, 0, true);
        Drawable modalDrawable = new TextureRegionDrawable(getAtlas().findRegion("modal"));
        this.modal = new Image(modalDrawable, Scaling.stretch);
        modal.setFillParent(true);
        stage.addActor(modal);
        this.batch = new SpriteBatch();
        this.font = new BitmapFont(Gdx.files.internal("data/btfr.fnt"), getAtlas().findRegion("btfr"), false);
    }

    public TextureAtlas getAtlas() {
        if (atlas == null) {
            atlas = new TextureAtlas(Gdx.files.internal("data/pack.atlas"));
        }
        return atlas;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        if (modal.isVisible()) {
            modal.toFront();
        }
        update(delta);
        draw(delta);
        stage.act(delta);
        stage.draw();
        Table.drawDebug(stage);
    }

    public void draw(float delta) {

    }

    public void update(float delta) {

    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height, true);
    }

    @Override
    public void show() {
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(inputMultiplexer);
        transitionIn();
    }

    public void transitionIn() {
        modal.addAction(sequence(delay(fadeInDelay), fadeOut(fadeInTime), new Action() {
            @Override
            public boolean act(float delta) {
                modal.setVisible(false);
                return true;
            }
        }));
    }

    public void transitionTo(final LaduScreen nextScreen) {
        modal.getColor().a = 0f;
        modal.setVisible(true);
        modal.addAction(sequence(delay(fadeOutDelay), fadeIn(fadeOutTime), new Action() {
            @Override
            public boolean act(float delta) {
                game.setScreen(nextScreen);
                return true;
            }
        }));
    }
    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
