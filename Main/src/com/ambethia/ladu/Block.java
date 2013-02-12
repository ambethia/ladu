package com.ambethia.ladu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Block {
    static final float DEFAULT_Z = 0.075f;
    static final Vector3 DEFAULT_FALLOFF = new Vector3(.4f, 3f, 20f);
    static final Color INACTIVE_COLOR = new Color(0.7f, 0.6f, 0.5f, 0.8f);
    static final Color ACTIVE_COLOR = new Color(0.6f, 0.7f, 1.0f, 2.0f);

	public final Vector2 position = new Vector2();
	public boolean isActive = false;
    public Light light;
	
	public Block (int x, int y) {
		position.x = x;
		position.y = y;
        light = new Light(new Vector3(), INACTIVE_COLOR, DEFAULT_FALLOFF);
        light.position.z = DEFAULT_Z;
	}

    public void update() {
        light.position.x = position.x + 0.5f;
        light.position.y = position.y + 0.5f;
    }

	public void activate() {
        if (!isActive) {
            light.color = ACTIVE_COLOR;
            Ladu.getInstance().getSound("activate").play();
        }
		isActive = true;
	}
	
	public void deactivate() {
		if (isActive) {
            light.color = INACTIVE_COLOR;
            Ladu.getInstance().getSound("deactivate").play();
        }
        isActive = false;
	}
}
