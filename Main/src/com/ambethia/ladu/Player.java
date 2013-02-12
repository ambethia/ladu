package com.ambethia.ladu;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Cubic;

import aurelienribon.tweenengine.equations.Quad;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.*;
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
    private ParticleEffect effect;
    private ParticleEmitter emitter;
    private boolean isSparking = false;
    private long moveSoundId = 0;
    private long pushSoundId = 0;
    private float idleTime = 0;

    public Player(TextureAtlas atlas) {
        Tween.registerAccessor(Player.class, new PlayerTween());
        direction = Direction.SOUTH;
        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("data/sparks.p"), atlas);
        emitter = effect.findEmitter("Sparks");
    }

    public void update(float delta) {
        manager.update(delta);

        // Sound
        Sound move = Ladu.getInstance().getSound("move");
        Sound push = Ladu.getInstance().getSound("push");
        if (isMoving) {
            idleTime = 0;
            if (0 == moveSoundId)
                moveSoundId = move.loop();
            if (pushSoundId == 0 && isSparking)
                pushSoundId = push.loop();
            if (pushSoundId != 0 && !isSparking) {
                push.stop(pushSoundId);
                pushSoundId = 0;
            }
        } else {
            idleTime += Gdx.graphics.getDeltaTime();
            if (0 != moveSoundId && idleTime > 0.2f) {
                move.stop(moveSoundId);
                moveSoundId = 0;
            }
            if (pushSoundId != 0 && idleTime > 0.05f) {
                push.stop(pushSoundId);
                pushSoundId = 0;
            }
        }

        if (pushedBlock != null) {
            pushedBlock.position.set(position.cpy().add(direction));
            if (!isSparking) {
                emitter.start();
                isSparking = true;
            }
        } else {
            if (isSparking) {
                emitter.allowCompletion();
                isSparking = false;
            }
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
            if (rotation == 270 && newRotation == 0)
                newRotation = 360;
            if (rotation == 360 && newRotation == 90)
                rotation = 0;
            if (rotation == 0 && newRotation == 270)
                rotation = 360;

            Tween.to(this, PlayerTween.POSITION_XY, 0.33f)
                    .target(x, y)
                    .ease(Cubic.INOUT)
                    .setCallback(moveEndCallback)
                    .start(manager);

            Tween.to(this, PlayerTween.ROTATION, 0.2f)
                    .target(newRotation)
                    .ease(Quad.INOUT)
                    .start(manager);

            // Sounds
            if (rotation > newRotation) {
                Ladu.getInstance().getSound("turn_a").play();
            } else if (rotation < newRotation) {
                Ladu.getInstance().getSound("turn_b").play();
            }

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
        animationFrames = new TextureRegion[]{
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
                playerSprite.getRegionWidth() / 2,
                playerSprite.getRegionHeight() / 2,
                playerSprite.getRegionWidth(),
                playerSprite.getRegionHeight(),
                1f, 1f,
                rotation);

        if (isSparking) {
            Vector2 offset = new Vector2();

            if (direction == Direction.EAST) {
                offset.x = playerSprite.getRegionWidth();
                offset.y = playerSprite.getRegionHeight() / 2;
            }

            if (direction == Direction.NORTH) {
                offset.x = playerSprite.getRegionWidth() / 2;
                offset.y = playerSprite.getRegionHeight();
            }
            if (direction == Direction.WEST) {
                offset.y = playerSprite.getRegionHeight() / 2;
            }

            if (direction == Direction.SOUTH) {
                offset.x = playerSprite.getRegionWidth() / 2;
            }

            effect.setPosition(offset.x + x, offset.y + y);
            effect.draw(batch, Gdx.graphics.getDeltaTime());
        }
    }
}
