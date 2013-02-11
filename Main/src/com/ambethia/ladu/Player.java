package com.ambethia.ladu;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Cubic;

import aurelienribon.tweenengine.equations.Quad;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.math.Vector2;

public class Player {
	public final Vector2 position = new Vector2();
    public float rotation;
	public boolean isMoving = false;
	private final TweenManager manager = new TweenManager();
	private Block pushedBlock;
	public Vector2 direction;
    private Animation idleAnimation;
    private Animation walkAnimation;
    private TextureRegion[] animationFrames;

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
            float newRotation = (direction.angle() + 90) % 360;
            // Some special cases to make sure he always turns in a logical manner
            // (1/4 turn instead of whipping around 3/4 turns)
            if (rotation == 270 && newRotation == 0) {
                newRotation = 360;
            }
            if (rotation == 360 && newRotation == 90) {
                rotation = 0;
            }
            if (rotation == 0 && newRotation == 270) {
                rotation = 360;
            }

            System.out.println("old: " + rotation + ", new: " + newRotation);

            Tween.to(this, PlayerTween.POSITION_XY, 0.33f)
				.target(x, y)
				.ease(Cubic.INOUT)
				.setCallback(moveEndCallback)
				.start(manager);
            Tween.to(this, PlayerTween.ROTATION, 0.2f)
                    .target(newRotation)
                    .ease(Quad.INOUT)
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

    public void setupAnimation(TileAtlas atlas) {
        animationFrames = new TextureRegion[] {
            atlas.getRegion(7),
            atlas.getRegion(4),
            atlas.getRegion(8),
        };

        idleAnimation = new Animation(2f, animationFrames[0], animationFrames[2], animationFrames[1]);
        walkAnimation = new Animation(0.2f, animationFrames[0], animationFrames[1], animationFrames[0], animationFrames[2]);
    }

    public void draw(SpriteBatch batch, int scale, float elapsedTime) {
        Animation currentAnimation;
        float x = position.x * scale;
        float y = position.y * scale;
        currentAnimation = isMoving ? walkAnimation : idleAnimation;
        TextureRegion playerSprite = currentAnimation.getKeyFrame(elapsedTime, true);
        batch.draw(playerSprite, x, y,
                playerSprite.getRegionWidth()/2,
                playerSprite.getRegionHeight()/2,
                playerSprite.getRegionWidth(),
                playerSprite.getRegionHeight(),
                1f, 1f,
                rotation);
    }
}
