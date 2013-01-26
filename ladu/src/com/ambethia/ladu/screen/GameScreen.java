package com.ambethia.ladu.screen;

import java.util.ArrayList;

import com.ambethia.ladu.Block;
import com.ambethia.ladu.Direction;
import com.ambethia.ladu.Player;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObject;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObjectGroup;
import com.badlogic.gdx.math.Vector2;

public class GameScreen extends LaduScreen {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private TiledMap tiledMap;
	private TileAtlas tileAtlas;
	private TileMapRenderer tileMapRenderer;
	private TextureRegion playerTexture;
	private TextureRegion blockTexture;
	private final Player player = new Player();	
	
	public ArrayList<Block> blocks = new ArrayList<Block>();

	public static int ROOM_LAYER = 0;
	public static int PIXELS_PER_METER = 64;

	public GameScreen (Game game) {
		super(game);
	}
	
	@Override
	public void render(float delta) {
		handleInput();
		
		player.update(delta);
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		tileMapRenderer.render(camera);

		batch.begin();

		for (Block block : blocks) {
			float x = block.position.x * PIXELS_PER_METER;
			float y = block.position.y * PIXELS_PER_METER;
			batch.draw(blockTexture, x, y);
		}

		float x = player.position.x * PIXELS_PER_METER;
		float y = player.position.y * PIXELS_PER_METER;
		batch.draw(playerTexture, x, y);

		batch.end();
	}

	@Override
	public void show() {
		batch = new SpriteBatch();		

		tiledMap = TiledLoader.createMap(Gdx.files.internal("maps/test-level.tmx"));
		tileAtlas = new TileAtlas(tiledMap, Gdx.files.internal("maps"));
		tileMapRenderer = new TileMapRenderer(tiledMap, tileAtlas, 8, 8);

		playerTexture = tileAtlas.getRegion(6);
		blockTexture = tileAtlas.getRegion(4);
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(tileMapRenderer.getMapWidthUnits() / 2, tileMapRenderer.getMapHeightUnits() / 2, 0);

		for (TiledObjectGroup group : tiledMap.objectGroups) {
			for (TiledObject object : group.objects) {
				if (object.type.equals("Player")) {
					int x = object.x / PIXELS_PER_METER;
					int y = (tileMapRenderer.getMapHeightUnits() - object.y) / PIXELS_PER_METER;
					player.position.set(x, y);
				}
				if (object.type.equals("Block")) {
					int x = object.x / PIXELS_PER_METER;
					int y = (tileMapRenderer.getMapHeightUnits() - object.y) / PIXELS_PER_METER;
					blocks.add(new Block(x, y));
				}
			}
		}
	}

	@Override
	public void resize(int width, int height) {
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
		tileMapRenderer.dispose();
		tileAtlas.dispose();
		batch.dispose();
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
				if (isWalkable(x + (int)direction.x, y + (int)direction.y)) {
					Block block = blockAt(x, y);
					player.push(block, direction);
				}
			}
		}
	}
	
	private boolean isWalkable(int x, int y) {
		int tileType = tiledMap.layers.get(ROOM_LAYER).tiles[tiledMap.height - y - 1][x];
		if (tiledMap.getTileProperty(tileType, "obstacle").equals("1")) {
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
}
