package com.ambethia.ladu;

import com.ambethia.ladu.screen.GameScreen;
import com.badlogic.gdx.Game;

public class Ladu extends Game {

	@Override
	public void create() {		
		setScreen(new GameScreen(this));
	}
}
