package com.ambethia.ladu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObject;
import com.badlogic.gdx.math.Vector3;

public class Light {
    public Vector3 position;
    public Color color;
    public Vector3 falloff;

    public Light(Vector3 position, Color color, Vector3 falloff) {
        this.position = position;
        this.color = color;
        this.falloff = falloff;
    }

    public Light(Vector3 position, String colorString, String falloffString) {
        this.position = position;
        this.color = new Color();
        this.falloff = new Vector3();

        String colorComponents[] = colorString.split(",");
        color.r = Float.parseFloat(colorComponents[0]);
        color.g = Float.parseFloat(colorComponents[1]);
        color.b = Float.parseFloat(colorComponents[2]);
        color.a = Float.parseFloat(colorComponents[3]);

        String falloffComponents[] = falloffString.split(",");
        falloff.x = Float.parseFloat(falloffComponents[0]);
        falloff.y = Float.parseFloat(falloffComponents[1]);
        falloff.z = Float.parseFloat(falloffComponents[2]);
    }

    @Override
    public String toString() {
        return "Light{" +
                "position=" + position +
                ", color=" + color +
                ", falloff=" + falloff +
                '}';
    }
}
