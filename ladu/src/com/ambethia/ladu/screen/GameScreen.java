package com.ambethia.ladu.screen;

import java.util.ArrayList;

import com.ambethia.ladu.Block;
import com.ambethia.ladu.Direction;
import com.ambethia.ladu.GameRenderer;
import com.ambethia.ladu.Player;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObject;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObjectGroup;
import com.badlogic.gdx.math.Vector2;

public class GameScreen extends LaduScreen {
	private GameRenderer renderer;
	public final Player player = new Player();	
	public ArrayList<Block> blocks = new ArrayList<Block>();

	public int numActiveBlocks = 0;
	public int numGoals = 0;
	
	public GameScreen (Game game) {
		super(game);
		renderer = new GameRenderer(this);
	}
	
	@Override
	public void render(float delta) {
		handleInput();
		player.update(delta);
		renderer.update(delta);
		updateBlocks();
		checkForWinCondition();
	}

	private void updateBlocks() {
		int count = 0;
		for (Block block : blocks) {
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		loadMapObjects();
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
					blocks.add(new Block(x, y));
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
}
