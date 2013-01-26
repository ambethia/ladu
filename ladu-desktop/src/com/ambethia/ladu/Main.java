package com.ambethia.ladu;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Ladu";
		cfg.useGL20 = false;
		cfg.width = 1136;
		cfg.height = 640;
		cfg.resizable = false;
		
		new LwjglApplication(new Ladu(), cfg);
	}
}
