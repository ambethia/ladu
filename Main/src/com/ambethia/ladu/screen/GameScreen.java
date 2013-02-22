package com.ambethia.ladu.screen;

import java.util.ArrayList;

import com.ambethia.ladu.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObject;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObjectGroup;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.moribitotech.mtx.InputIntent;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class GameScreen extends LaduScreen {
    private GameRenderer renderer;
    public final Player player;
    public ArrayList<Block> blocks = new ArrayList<Block>();
    public ArrayList<Light> lights = new ArrayList<Light>();
    public int numActiveBlocks = 0;
    public int numGoals = 0;
    private Label levelLabel;
    private boolean isLevelComplete = false;
    private Image backButton;
    private Image resetButton;
    private final int buttonPadding = 20;
    private InputIntent inputIntent;

    // Pinch/Zoom stuff
    int numberOfFingers = 0;
    int fingerOnePointer;
    int fingerTwoPointer;
    float lastDistance = 0;
    Vector3 fingerOne = new Vector3();
    Vector3 fingerTwo = new Vector3();

    public GameScreen() {
        super();
        super.fadeInDelay = 2f;
        super.fadeOutDelay = 1f;
        player = new Player(getAtlas());
        renderer = new GameRenderer(this);
        inputIntent = new InputIntent();
        inputIntent.setTouchDragIntervalRange(GameRenderer.PIXELS_PER_METER);
    }

    public void update(float delta) {
        if (!isLevelComplete) {
            handleKeyboardInput();
            updateBlocks();
            checkForWinCondition();
        }
        player.update(delta);
    }

    public void draw(float delta) {
        levelLabel.toFront();
        renderer.draw(delta);
    }

    private void updateBlocks() {
        int count = 0;
        for (Block block : blocks) {
            block.update();
            int x = Math.round(block.position.x);
            int y = Math.round(block.position.y);
            if (tilePropertyAt(x, y, "goal", "1")) {
                block.activate();
                count++;
            } else {
                if (block.isActive) {
                    block.deactivate();
                }
            }
        }
        numActiveBlocks = count;
    }

    private void checkForWinCondition() {
        if (numGoals > 0 && numActiveBlocks >= numGoals) {
            levelCompleted();
        }
    }

    private void levelCompleted() {
        Ladu.getInstance().getSound("bell").play();
        if (!isLevelComplete) {
            isLevelComplete = true;
            if (game.currentLevel < game.numLevels) {
                game.setCurrentLevel(game.currentLevel + 1);
                game.transitionTo(Ladu.Screens.GAME);
            } else {
                game.setCurrentLevel(1);
                game.transitionTo(Ladu.Screens.CREDITS);
            }
        }
    }

    @Override
    public void show() {
        super.show();
        levelLabel = new Label(String.format("Level %d", game.currentLevel), new Label.LabelStyle(font, Color.WHITE));
        stage.addActor(levelLabel);
        levelLabel.getColor().a = 0;
        levelLabel.addAction(sequence(fadeIn(0.5f), delay(1.0f), fadeOut(0.5f), new Action() {
            @Override
            public boolean act(float delta) {
                levelLabel.setVisible(false);
                return true;
            }
        }));
        loadMapObjects();

        backButton = createButton("icon-back", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                exitLevel();
            }
        });
        stage.addActor(backButton);

        resetButton = createButton("icon-reset", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resetLevel();
            }
        });
        stage.addActor(resetButton);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        levelLabel.setPosition(width / 2 - levelLabel.getWidth() / 2, height / 2 - levelLabel.getHeight() / 2);
        float backButtonY = height - backButton.getHeight() - buttonPadding;
        backButton.setPosition(buttonPadding, backButtonY);
        resetButton.setPosition(buttonPadding, backButtonY - resetButton.getHeight() - buttonPadding);
        renderer.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        renderer.dispose();
    }

    @Override
    public void pause() {
        super.pause();
        numberOfFingers = 0;
    }

    private void handleKeyboardInput() {
        if (!player.isMoving) {
            if (anyKeyPressed(Keys.RIGHT, Keys.L, Keys.D)) {
                move(Direction.EAST);
            } else if (anyKeyPressed(Keys.UP, Keys.K, Keys.W)) {
                move(Direction.NORTH);
            } else if (anyKeyPressed(Keys.LEFT, Keys.H, Keys.A)) {
                move(Direction.WEST);
            } else if (anyKeyPressed(Keys.DOWN, Keys.J, Keys.S)) {
                move(Direction.SOUTH);
            }
        }
    }

    private boolean anyKeyPressed(int... keys) {
        for (int key : keys) {
            if (Gdx.input.isKeyPressed(key)) {
                return true;
            }
        }
        return false;
    }

    private void handleTouchInput() {
        if (!player.isMoving && inputIntent.isTouchDragInterval()) {
            if (inputIntent.getDirectionIntent() == InputIntent.DirectionIntent.TOUCH_D_RIGHT) {
                move(Direction.EAST);
            } else if (inputIntent.getDirectionIntent() == InputIntent.DirectionIntent.TOUCH_D_DOWN) {
                move(Direction.NORTH);
            } else if (inputIntent.getDirectionIntent() == InputIntent.DirectionIntent.TOUCH_D_LEFT) {
                move(Direction.WEST);
            } else if (inputIntent.getDirectionIntent() == InputIntent.DirectionIntent.TOUCH_D_UP) {
                move(Direction.SOUTH);
            }
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        super.keyUp(keycode);
        switch (keycode) {
            case Keys.ESCAPE:
                exitLevel();
                break;
            case Keys.X:
                resetLevel();
                break;
            default:
                player.unstuck();
                break;
        }
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if ((screenX < (resetButton.getX() + resetButton.getWidth() + buttonPadding)) &&
           (Gdx.graphics.getHeight() - screenY > resetButton.getY())) {
            return false;
        } else {
            inputIntent.setTouchInitials(screenX, screenY);

            numberOfFingers++;
            if(numberOfFingers == 1)
            {
                fingerOnePointer = pointer;
                fingerOne.set(screenX, screenY, 0);
            }
            else if(numberOfFingers == 2)
            {
                fingerTwoPointer = pointer;
                fingerTwo.set(screenX, screenY, 0);

                float distance = fingerOne.dst(fingerTwo);
                lastDistance = distance;
            }

            return true;
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        inputIntent.reset();
        player.unstuck();

        numberOfFingers--;
        if(numberOfFingers < 0){
            numberOfFingers = 0;
        }

        lastDistance = 0;

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // for pinch-to-zoom
        if (pointer == fingerOnePointer) {
            fingerOne.set(screenX, screenY, 0);
        }
        if (pointer == fingerTwoPointer) {
            fingerTwo.set(screenX, screenY, 0);
        }

        if (numberOfFingers > 1) {
            float distance = fingerOne.dst(fingerTwo);
            float factor = distance / lastDistance;
            if (lastDistance > distance) {
                float zoom = renderer.getZoom() + (factor * Gdx.graphics.getDeltaTime() / 6);
                renderer.setZoom(Math.max(Math.min(zoom, 1.0f), 0.25f));
            } else if (lastDistance < distance) {
                float zoom = renderer.getZoom() - (factor * Gdx.graphics.getDeltaTime() / 6);
                renderer.setZoom(Math.max(Math.min(zoom, 1.0f), 0.25f));
            }
            lastDistance = distance;
        } else {
            inputIntent.setTouchCurrents(screenX, screenY);
            handleTouchInput();
        }

        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        float zoom = renderer.getZoom() + (amount * Gdx.graphics.getDeltaTime()) / 2;
        renderer.setZoom(Math.max(Math.min(zoom, 1.0f), 0.25f));
        return false;
    }

    private void resetLevel() {
        isLevelComplete = true;
        Ladu.getInstance().getSound("die").play();
        game.transitionTo(Ladu.Screens.GAME);
    }

    private void exitLevel() {
        isLevelComplete = true;
        game.transitionTo(Ladu.Screens.LEVELS);
    }

    private void move(Vector2 direction) {
        int x = Math.round(player.position.x + direction.x);
        int y = Math.round(player.position.y + direction.y);

        // Can we walk into this space?
        if (isWalkable(x, y)) {
            player.move(direction);
        } else {
            // If not, then is it because there's a block here?
            if (isBlocked(x, y)) {
                // It's a block, so can that block be pushed?
                if (isWalkable(x + (int) direction.x, y + (int) direction.y)) {
                    Block block = blockAt(x, y);
                    player.push(block, direction);
                } else {
                    player.stuck(direction);
                }
            } else {
                player.stuck(direction);
            }
        }
    }

    private boolean isWalkable(int x, int y) {
        if (tilePropertyAt(x, y, "obstacle", "1")) {
            return false;
        } else {
            return !isBlocked(x, y);
        }
    }

    private boolean isBlocked(int x, int y) {
        return null != blockAt(x, y);
    }

    private Block blockAt(int x, int y) {
        for (Block block : blocks) {
            if (block.position.x == x && block.position.y == y) {
                return block;
            }
        }
        return null;
    }

    private boolean tilePropertyAt(int x, int y, String property, String value) {
        int tileType = renderer.tiledMap.layers.get(GameRenderer.ROOM_LAYER).tiles[renderer.tiledMap.height - y - 1][x];
        return value.equals(renderer.tiledMap.getTileProperty(tileType, property));
    }

    private void loadMapObjects() {
        // Load the Player and Block objects
        for (TiledObjectGroup group : renderer.tiledMap.objectGroups) {
            for (TiledObject object : group.objects) {
                int x = object.x / GameRenderer.PIXELS_PER_METER;
                int y = (renderer.tileMapRenderer.getMapHeightUnits() - object.y) / GameRenderer.PIXELS_PER_METER;
                if (object.type.equals("Player")) {
                    player.position.set(x, y);
                }
                if (object.type.equals("Block")) {
                    Block block = new Block(x, y);
                    blocks.add(block);
                    lights.add(block.light);
                }
                if (object.type.equals("Light")) {
                    float z = Float.parseFloat(object.properties.get("Z"));
                    String color = object.properties.get("Color");
                    String falloff = object.properties.get("Falloff");
                    Light light = new Light(new Vector3(x, y, z), color, falloff);
                    lights.add(light);
                }
            }
        }
        // Set the number of goals in the map
        numGoals = 0;
        for (int x = 0; x < renderer.tiledMap.width; x++) {
            for (int y = 0; y < renderer.tiledMap.height; y++) {
                if (tilePropertyAt(x, y, "goal", "1")) {
                    numGoals++;
                }
            }
        }
    }

    private Image createButton(String textureName, InputListener inputListener) {
        TextureAtlas.AtlasRegion region = getAtlas().findRegion(textureName);
        Image button = new Image(new TextureRegionDrawable(region));
        button.addListener(inputListener);
        return button;
    }
}