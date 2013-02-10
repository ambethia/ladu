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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.sun.javaws.ui.LaunchErrorDialog;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class GameScreen extends LaduScreen {
    private GameRenderer renderer;
    public final Player player = new Player();
    public ArrayList<Block> blocks = new ArrayList<Block>();
    public ArrayList<Light> lights = new ArrayList<Light>();
    public int numActiveBlocks = 0;
    public int numGoals = 0;
    private Label levelLabel;
    private boolean isLevelComplete = false;
    private Light mainLight;
    private Image backButton;
    private Image resetButton;
    private final int buttonPadding = 20;

    public GameScreen(Ladu game) {
        super(game);
        super.fadeInDelay = 2f;
        super.fadeOutDelay = 1f;
        renderer = new GameRenderer(this);
    }

    public void update(float delta) {
        if (!isLevelComplete) {
            handleInput();
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

        backButton = createButton("icon-back", new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                exitLevel();
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(backButton);

        resetButton = createButton("icon-reset", new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                resetLevel();
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(resetButton);

        // Center Light
        mainLight = new Light(new Vector3(11f, 6.75f, 0.2f), new Color(1.0f, 1.0f, 1.0f, 1.0f), new Vector3(0.4f, 3f, 30f));
        lights.add(mainLight);
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

    private void handleInput() {
        if (!player.isMoving) {
            if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
                move(Direction.EAST);
            }
            if (Gdx.input.isKeyPressed(Keys.UP)) {
                move(Direction.NORTH);
            }
            if (Gdx.input.isKeyPressed(Keys.LEFT)) {
                move(Direction.WEST);
            }
            if (Gdx.input.isKeyPressed(Keys.DOWN)) {
                move(Direction.SOUTH);
            }
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Keys.ESCAPE:
                exitLevel();
                break;
            case Keys.X:
                resetLevel();
                break;
            case Keys.Q:
                mainLight.color.r -= 0.1f;
                Gdx.app.log("Light", mainLight.toString());
                break;
            case Keys.W:
                mainLight.color.r += 0.1f;
                Gdx.app.log("Light", mainLight.toString());
                break;
            case Keys.E:
                mainLight.color.g -= 0.1f;
                Gdx.app.log("Light", mainLight.toString());
                break;
            case Keys.R:
                mainLight.color.g += 0.1f;
                Gdx.app.log("Light", mainLight.toString());
                break;
            case Keys.T:
                mainLight.color.b -= 0.1f;
                Gdx.app.log("Light", mainLight.toString());
                break;
            case Keys.Y:
                mainLight.color.b += 0.1f;
                Gdx.app.log("Light", mainLight.toString());
                break;
            case Keys.U:
                mainLight.color.a -= 0.1f;
                Gdx.app.log("Light", mainLight.toString());
                break;
            case Keys.I:
                mainLight.color.a += 0.1f;
                Gdx.app.log("Light", mainLight.toString());
                break;
            case Keys.A:
                mainLight.falloff.x -= 0.1f;
                Gdx.app.log("Light", mainLight.toString());
                break;
            case Keys.S:
                mainLight.falloff.x += 0.1f;
                Gdx.app.log("Light", mainLight.toString());
                break;
            case Keys.D:
                mainLight.falloff.y -= 0.1f;
                Gdx.app.log("Light", mainLight.toString());
                break;
            case Keys.F:
                mainLight.falloff.y += 0.1f;
                Gdx.app.log("Light", mainLight.toString());
                break;
            case Keys.G:
                mainLight.falloff.z -= 1f;
                Gdx.app.log("Light", mainLight.toString());
                break;
            case Keys.H:
                mainLight.falloff.z += 1f;
                Gdx.app.log("Light", mainLight.toString());
                break;
            case Keys.J:
                mainLight.position.z -= 0.1f;
                Gdx.app.log("Light", mainLight.toString());
                break;
            case Keys.K:
                mainLight.position.z += 0.1f;
                Gdx.app.log("Light", mainLight.toString());
                break;
            default:
                break;
        }
        return true;
    }

    private void resetLevel() {
        isLevelComplete = true;
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
                }
            }
        }
    }

    private boolean isWalkable(int x, int y) {
        if (tilePropertyAt(x, y, "obstacle", "1")) {
            return false;
        } else {
            if (isBlocked(x, y)) {
                return false;
            }
            return true;
        }
    }

    private boolean isBlocked(int x, int y) {
        if (null == blockAt(x, y)) {
            return false;
        } else {
            return true;
        }
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