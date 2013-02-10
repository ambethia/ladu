package com.ambethia.ladu;

import com.ambethia.ladu.screen.*;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Action;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class Ladu extends Game {
    public int currentLevel;
    public int unlockedLevel;
    public int numLevels = 3;
    private Preferences prefs;

    public Ladu() {
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

    public enum Screens {
        SPLASH, MENU, LEVELS, GAME, CREDITS, TEST
    }

    public void transitionTo(Screens screenId) {
        LaduScreen currentScreen = (LaduScreen) getScreen();
        LaduScreen nextScreen;
        switch (screenId) {
            case SPLASH:
                nextScreen = new SplashScreen(this);
                break;
            case MENU:
                nextScreen = new MenuScreen(this);
                break;
            case LEVELS:
                nextScreen = new LevelsScreen(this);
                break;
            case GAME:
                nextScreen = new GameScreen(this);
                break;
            case CREDITS:
                nextScreen = new CreditsScreen(this);
                break;
            case TEST:
                nextScreen = new TestScreen(this);
                break;
            default:
                nextScreen = new SplashScreen(this);
        }
        if (currentScreen != null) {
            currentScreen.transitionTo(nextScreen);
        } else {
            setScreen(nextScreen);
        }
    }

    @Override
    public void create() {
        prefs = Gdx.app.getPreferences("Ladu");
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
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
}
