package com.ambethia.ladu;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.math.Vector2;

public class Player {
	public final Vector2 position = new Vector2();
	public boolean isMoving = false;
	private final TweenManager manager = new TweenManager();
	private Block pushedBlock;
	private Vector2 direction;
	
	public Player () {
		Tween.registerAccessor(Player.class, new PlayerTween());
		direction = Direction.SOUTH;
	}
	
	public void update(float delta) {
		manager.update(delta);
		if (pushedBlock != null) {
			pushedBlock.position.set(position.cpy().add(direction));
		}
	}
	
	public void move(Vector2 newDirection) {
		direction = newDirection;
		if (!isMoving) {
			float x = position.x + direction.x;
			float y = position.y + direction.y;
			Tween.to(this, PlayerTween.POSITION_XY, 0.33f)
				.target(x, y)
				.ease(Cubic.INOUT)
				.setCallback(moveEndCallback)
				.start(manager);
			isMoving = true;
		}
	}

	private final TweenCallback moveEndCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween source) {
			isMoving = false;
			if (pushedBlock != null) {
				// Rounding off the position makes it easy to check later
				pushedBlock.position.x = Math.round(pushedBlock.position.x);
				pushedBlock.position.y = Math.round(pushedBlock.position.y);
				pushedBlock = null;				
			}
		}
	};

	public void push(Block block, Vector2 direction) {
		pushedBlock = block;
		move(direction);
	}
}
