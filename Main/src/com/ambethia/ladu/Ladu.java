package com.ambethia.ladu;

import com.ambethia.ladu.screen.*;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Preferences ;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Action;

import java.util.HashMap;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class Ladu extends Game {
    public int currentLevel;
    public int unlockedLevel;
    public int numLevels = 3;
    private Preferences prefs;
    private final HashMap<String, Sound> sounds = new HashMap<String, Sound>();
    private static Ladu instance;
    private boolean soundsLoaded = false;

    public Ladu() {
    }

    public static Ladu getInstance() {
        if (null == instance) {
            instance = new Ladu();
        }
        return instance;
    }

    public void quit() {
        prefs.flush();
        LaduScreen currentScreen = (LaduScreen) getScreen();
        currentScreen.modal.setVisible(true);
        currentScreen.modal.addAction(sequence(fadeIn(1.5f), new Action() {
            @Override
            public boolean act(float delta) {
                Gdx.app.exit();
                return true;
            }
        }));
    }

    public static void toggleFullscreen() {
        if (Gdx.app.getType() == ApplicationType.Desktop) {
            if (Gdx.graphics.isFullscreen()) {
                Gdx.graphics.setDisplayMode(768, 432, false);
            } else {
                Graphics.DisplayMode displayMode = Gdx.graphics.getDesktopDisplayMode();
                Gdx.graphics.setDisplayMode(displayMode.width, displayMode.height, true);
            }
        }
    }

    public enum Screens {
        SPLASH, MENU, LEVELS, GAME, CREDITS, TEST
    }

    public void transitionTo(Screens screenId) {
        LaduScreen currentScreen = (LaduScreen) getScreen();
        LaduScreen nextScreen;
        switch (screenId) {
            case SPLASH:
                nextScreen = new SplashScreen();
                break;
            case MENU:
                nextScreen = new MenuScreen();
                break;
            case LEVELS:
                nextScreen = new LevelsScreen();
                break;
            case GAME:
                loadSounds();
                nextScreen = new GameScreen();
                break;
            case CREDITS:
                nextScreen = new CreditsScreen();
                break;
            case TEST:
                nextScreen = new TestScreen();
                break;
            default:
                nextScreen = new SplashScreen();
        }
        if (currentScreen != null) {
            currentScreen.transitionTo(nextScreen);
        } else {
            setScreen(nextScreen);
        }
    }

    private void loadSounds() {
        if (!soundsLoaded) {
            sounds.put("activate", Gdx.audio.newSound(Gdx.files.internal("data/sound/activate.ogg")));
            sounds.put("bell", Gdx.audio.newSound(Gdx.files.internal("data/sound/bell.ogg")));
            sounds.put("deactivate", Gdx.audio.newSound(Gdx.files.internal("data/sound/deactivate.ogg")));
            sounds.put("die", Gdx.audio.newSound(Gdx.files.internal("data/sound/die.ogg")));
            sounds.put("move", Gdx.audio.newSound(Gdx.files.internal("data/sound/move.ogg")));
            sounds.put("push", Gdx.audio.newSound(Gdx.files.internal("data/sound/push.ogg")));
            sounds.put("turn_a", Gdx.audio.newSound(Gdx.files.internal("data/sound/turn_a.ogg")));
            sounds.put("turn_b", Gdx.audio.newSound(Gdx.files.internal("data/sound/turn_b.ogg")));
            sounds.put("bump", Gdx.audio.newSound(Gdx.files.internal("data/sound/bump.ogg")));
            soundsLoaded = true;
        }
    }

    public Sound getSound(String name) {
        return sounds.get(name);
    }

    @Override
    public void create() {
        prefs = Gdx.app.getPreferences("Ladu");
        restoreLevel();
//        transitionTo(Screens.SPLASH);
        transitionTo(Screens.GAME);
    }

    public void restoreLevel() {
        Integer level;

        level = prefs.getInteger("currentLevel");
        if (level != null && level > 0) {
            currentLevel = Math.min(numLevels, level);
        } else {
            currentLevel = 1;
        }

        level = prefs.getInteger("unlockedLevel");
        if (level != null && level > 0) {
            unlockedLevel = Math.min(numLevels, level);
        } else {
            unlockedLevel = 1;
        }
    }

    public void setCurrentLevel(int level) {
        currentLevel = Math.min(numLevels, level);
        unlockedLevel = Math.max(unlockedLevel, currentLevel);
        prefs.putInteger("currentLevel", currentLevel);
        prefs.putInteger("unlockedLevel", unlockedLevel);
        prefs.flush();
    }

    @Override
    public void dispose() {
        super.dispose();
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }
    }
}
