package com.ambethia.ladu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.color;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;

public class MenuButton extends Image {
    private RepeatAction animationAction;
    private boolean isAnimating = false;
    private boolean isEnabled = true;

    private Color focusedColor;
    private Color enabledColor;
    private Color disabledColor;

    public MenuButton(TextureRegionDrawable region) {
        super(region);
        enabledColor = new Color(0.50f, 0.48f, 0.46f, 1f);
        focusedColor = Color.WHITE;
        disabledColor = Color.WHITE;
        setColor(enabledColor);
    }

    public void stopAnimation() {
        if (isAnimating) {
            removeAction(animationAction);
            addAction(scaleTo(1, 1, 1f, Interpolation.bounceOut));
            addAction(color(enabledColor, 0.25f));
        }
        isAnimating = false;
    }

    public void startAnimation() {
        if (isEnabled) {
            if (!isAnimating) {
                animationAction = forever(sequence(scaleTo(1.2f, 1.2f, 1f, Interpolation.bounceIn), scaleTo(1, 1, 1f, Interpolation.bounceOut)));
                addAction(animationAction);
                addAction(color(focusedColor, 0.25f));
            }
            isAnimating = true;
        }
    }

    public void focus() {
        for (EventListener listener : getListeners()) {
            if (listener instanceof MenuInputListener) {
                ((MenuInputListener) listener).focus(this);
            }
        }
    }

    public void disable() {
        setColor(disabledColor);
        isEnabled = false;
    }

    public void call() {
        for (EventListener listener : getListeners()) {
            if (listener instanceof MenuInputListener) {
                ((MenuInputListener) listener).call();
            }
        }
    }
}
