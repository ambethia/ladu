package com.ambethia.ladu;

import com.ambethia.ladu.screen.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;

public class GameRenderer {
	private GameScreen screen;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	public TiledMap tiledMap;
	public TileAtlas tileAtlas;
	public TileMapRenderer tileMapRenderer;
	private TextureRegion playerTexture;
	private TextureRegion blockTexture;
	private TextureRegion activeBlockTexture;
	public static int ROOM_LAYER = 0;
	public static int PIXELS_PER_METER = 64;

	public GameRenderer(GameScreen gameScreen) {
		screen = gameScreen;
		batch = new SpriteBatch();		

		tiledMap = TiledLoader.createMap(Gdx.files.internal("maps/test-level.tmx"));
		tileAtlas = new TileAtlas(tiledMap, Gdx.files.internal("maps"));
		tileMapRenderer = new TileMapRenderer(tiledMap, tileAtlas, 8, 8);

		blockTexture = tileAtlas.getRegion(4);
		activeBlockTexture = tileAtlas.getRegion(5);
		playerTexture = tileAtlas.getRegion(6);
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(tileMapRenderer.getMapWidthUnits() / 2, tileMapRenderer.getMapHeightUnits() / 2, 0);
	}

	public void update(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		tileMapRenderer.render(camera);

		batch.begin();

		for (Block block : screen.blocks) {
			float x = block.position.x * PIXELS_PER_METER;
			float y = block.position.y * PIXELS_PER_METER;
			
			if (block.isActive) {
				batch.draw(activeBlockTexture, x, y);
			} else {
				batch.draw(blockTexture, x, y);
			}
		}

		float x = screen.player.position.x * PIXELS_PER_METER;
		float y = screen.player.position.y * PIXELS_PER_METER;
		batch.draw(playerTexture, x, y);

		batch.end();
	}

	public void dispose() {
		tileMapRenderer.dispose();
		tileAtlas.dispose();
		batch.dispose();
	}
}
