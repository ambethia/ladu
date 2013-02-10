package com.ambethia.ladu;

import com.ambethia.ladu.screen.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.tiled.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class GameRenderer {
    private GameScreen screen;
    private OrthographicCamera camera;
    public TiledMap tiledMap;
    public TileAtlas tileAtlas;
    public TileMapRenderer tileMapRenderer;
    private TextureRegion blockTexture;
    private TextureRegion activeBlockTexture;
    public static int ROOM_LAYER = 1;
    public static int PIXELS_PER_METER = 128;
    private float elapsedTime = 0f;
    private Texture normalMap;
    private Texture heightMap;
    private ShaderProgram shader;
    private boolean isShaderEnabled = false;
    private static final float VIRTUAL_WIDTH = 24f;
    private static final float VIRTUAL_HEIGHT = 13.5f;
    private static final float ASPECT_RATIO = VIRTUAL_WIDTH / VIRTUAL_HEIGHT;
    public static final Color AMBIENT_LIGHT = new Color(0.6f, 0.6f, 0.6f, 0.6f);
    private Rectangle viewport;

    public GameRenderer(GameScreen gameScreen) {
        screen = gameScreen;

        if (Gdx.graphics.isGL20Available()) {
            shader = createShader();
        }

        if (isShaderEnabled) {
            screen.batch.setShader(shader);
            normalMap = new Texture(Gdx.files.internal("data/maps/tiles-n.png"));
            heightMap = new Texture(Gdx.files.internal("data/maps/tiles-h.png"));
        }

        tiledMap = TiledLoader.createMap(Gdx.files.internal(String.format("data/maps/level-%d.tmx", screen.game.currentLevel)));
        tileAtlas = new TileAtlas(tiledMap, Gdx.files.internal("data/maps"));
        tileMapRenderer = new TileMapRenderer(tiledMap, tileAtlas, 8, 8, shader);

        blockTexture = tileAtlas.getRegion(5);
        activeBlockTexture = tileAtlas.getRegion(6);

        screen.player.setupAnimation(tileAtlas);

        camera = new OrthographicCamera(VIRTUAL_WIDTH * PIXELS_PER_METER, VIRTUAL_HEIGHT * PIXELS_PER_METER);
        camera.position.set(tileMapRenderer.getMapWidthUnits() / 2, tileMapRenderer.getMapHeightUnits() / 2, 0);
    }

    private ShaderProgram createShader() {
        ShaderProgram.pedantic = false;
        FileHandle vertexShader = Gdx.files.internal("data/vertex.glsl");
        FileHandle fragmentShader = Gdx.files.internal("data/fragment.glsl");
        ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (shader.isCompiled()) {
            isShaderEnabled = true;
        } else {
            throw new GdxRuntimeException("Could not compile shader: " + shader.getLog());
        }
        if (shader.getLog().length() != 0)
            System.out.println(shader.getLog());
        return shader;
    }

    public void draw(float delta) {
        camera.update();
        Gdx.gl.glViewport((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);

        screen.batch.begin(); // Beginning this batch here lets me use the shader in the tileMapRenderer.
        screen.batch.setProjectionMatrix(camera.combined);

        if (isShaderEnabled) {

            shader.setUniformi("u_normalMap", 1);
            shader.setUniformi("u_heightMap", 2);
            shader.setUniformf("ambientLight", AMBIENT_LIGHT.r, AMBIENT_LIGHT.g, AMBIENT_LIGHT.b, AMBIENT_LIGHT.a);

            for (int i = 0; i < screen.lights.size(); i++) {
                Light light = screen.lights.get(i);
                Vector3 position = light.position.cpy().mul(PIXELS_PER_METER);
                camera.project(position);
                position.x = position.x / (float) Gdx.graphics.getWidth();
                position.y = position.y / (float) Gdx.graphics.getHeight();
                position.z = light.position.z;
                shader.setUniformf("lights[" + i + "].color", light.color);
                shader.setUniformf("lights[" + i + "].falloff", light.falloff);
                shader.setUniformf("lights[" + i + "].position", position);
            }

            normalMap.bind(1);
            heightMap.bind(2);
            tileAtlas.getRegion(1).getTexture().bind(0);
        }

        tileMapRenderer.render(camera);
        screen.batch.end();

        screen.batch.begin();

        for (Block block : screen.blocks) {
            float x = block.position.x * PIXELS_PER_METER;
            float y = block.position.y * PIXELS_PER_METER;

            if (block.isActive) {
                screen.batch.draw(activeBlockTexture, x, y);
            } else {
                screen.batch.draw(blockTexture, x, y);
            }
        }

        screen.player.draw(screen.batch, PIXELS_PER_METER, elapsedTime);

        screen.batch.end();

        // Restore viewport for GUI stuff
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        elapsedTime += delta;
    }

    public void dispose() {
        tileMapRenderer.dispose();
        tileAtlas.dispose();
        screen.batch.dispose();
        if (isShaderEnabled) {
            normalMap.dispose();
            heightMap.dispose();
            shader.dispose();
        }
    }

    public void resize(int width, int height) {
        float aspectRatio = (float) width / (float) height;
        float scale;
        Vector2 crop = new Vector2(0f, 0f);

        if (aspectRatio > ASPECT_RATIO) {
            scale = (float) height / VIRTUAL_HEIGHT;
            crop.x = (width - VIRTUAL_WIDTH * scale) / 2f;
        } else if (aspectRatio < ASPECT_RATIO) {
            scale = (float) width / VIRTUAL_WIDTH;
            crop.y = (height - VIRTUAL_HEIGHT * scale) / 2f;
        } else {
            scale = (float) width / VIRTUAL_WIDTH;
        }

        float w = VIRTUAL_WIDTH * scale;
        float h = VIRTUAL_HEIGHT * scale;
        viewport = new Rectangle(crop.x, crop.y, w, h);


        if (isShaderEnabled) {
            shader.begin();
            shader.setUniformf("resolution", width, height);
            shader.end();
        }
    }
}
