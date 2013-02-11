package com.ambethia.ladu;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class LaduDesktop {
    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "The Ladu";
        cfg.useGL20 = true;
        cfg.width = 768;
        cfg.height = 432;
        cfg.resizable = true;

        new LwjglApplication(new Ladu(), cfg);
        Ladu.toggleFullscreen();
    }
}
