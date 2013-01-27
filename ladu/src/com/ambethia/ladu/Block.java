package com.ambethia.ladu;

import com.badlogic.gdx.math.Vector2;

public class Block {
	public final Vector2 position = new Vector2();
	public boolean isActive = false;
	
	public Block (int x, int y) {
		position.x = x;
		position.y = y;
	}

	public void activate() {
		isActive = true;
	}
	
	public void deactivate() {
		isActive = false;
	}
}
