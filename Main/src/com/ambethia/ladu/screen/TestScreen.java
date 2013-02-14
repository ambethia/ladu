package com.ambethia.ladu.screen;

import com.ambethia.ladu.Ladu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class TestScreen extends LaduScreen {
    Texture diffuse;
    Texture normals;
    OrthographicCamera camera;
    ShaderProgram shader;

    //our constants...
    public static final float DEFAULT_LIGHT_Z = 0.075f;
    public static final float AMBIENT_INTENSITY = 0.5f;
    public static final float LIGHT_INTENSITY = 0.5f;

    public static final Vector3 LIGHT_POS = new Vector3(0f,0f,DEFAULT_LIGHT_Z);

    //Light RGB and intensity (alpha)
    public static final Vector3 LIGHT_COLOR = new Vector3(1f, 0.8f, 0.6f);

    //Ambient RGB and intensity (alpha)
    public static final Vector3 AMBIENT_COLOR = new Vector3(0.6f, 0.6f, 0.6f);

    //Attenuation coefficients for light falloff
    public static final Vector3 FALLOFF = new Vector3(.4f, 3f, 20f);

    @Override
    public void show() {
        super.show();
        diffuse = new Texture(Gdx.files.internal("data/maps/basic-tiles1.png"));
        normals = new Texture(Gdx.files.internal("data/maps/basic-tiles-n.png"));

        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(Gdx.files.internal("data/vertex.glsl"), Gdx.files.internal("data/fragment.glsl"));
        if (!shader.isCompiled())
            throw new GdxRuntimeException("Could not compile shader: "+shader.getLog());
        if (shader.getLog().length()!=0)
            System.out.println(shader.getLog());

        batch.setShader(shader);

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void draw(float delta) {
        batch.begin();
        batch.setProjectionMatrix(camera.combined);

        //update light position, normalized to screen resolution
        float x = Gdx.input.getX() / (float)Gdx.graphics.getWidth();
        float y = 1f - Gdx.input.getY() / (float)Gdx.graphics.getHeight();

        LIGHT_POS.x = x;
        LIGHT_POS.y = y;

        shader.setUniformi("u_normals", 15);
        shader.setUniformf("ambientLight", AMBIENT_COLOR.x, AMBIENT_COLOR.y, AMBIENT_COLOR.z, AMBIENT_INTENSITY);
        shader.setUniformf("lights[0].color", LIGHT_COLOR.x, LIGHT_COLOR.y, LIGHT_COLOR.z, LIGHT_INTENSITY);
        shader.setUniformf("lights[0].falloff", FALLOFF);
        shader.setUniformf("lights[0].position", LIGHT_POS);

        //bind normal map to texture unit 1
        normals.bind(15);

        //bind diffuse color to texture unit 0
        //important that we specify 0 otherwise we'll still be bound to glActiveTexture(GL_TEXTURE1)
        diffuse.bind(0);

        //draw the texture unit 0 with our shader effect applied
        batch.draw(diffuse, camera.position.x - diffuse.getWidth() / 2, camera.position.y - diffuse.getHeight() / 2);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        shader.begin();
        shader.setUniformf("resolution", width, height);
        shader.end();
    }

    @Override
    public boolean keyUp(int keycode) {
        super.keyUp(keycode);
        if (keycode == Input.Keys.ESCAPE)
        {
            game.transitionTo(Ladu.Screens.MENU);
        }

//        case Input.Keys.Q:
//        mainLight.color.r -= 0.1f;
//        Gdx.app.log("Light", mainLight.toString());
//        break;
//        case Input.Keys.W:
//        mainLight.color.r += 0.1f;
//        Gdx.app.log("Light", mainLight.toString());
//        break;
//        case Input.Keys.E:
//        mainLight.color.g -= 0.1f;
//        Gdx.app.log("Light", mainLight.toString());
//        break;
//        case Input.Keys.R:
//        mainLight.color.g += 0.1f;
//        Gdx.app.log("Light", mainLight.toString());
//        break;
//        case Input.Keys.T:
//        mainLight.color.b -= 0.1f;
//        Gdx.app.log("Light", mainLight.toString());
//        break;
//        case Input.Keys.Y:
//        mainLight.color.b += 0.1f;
//        Gdx.app.log("Light", mainLight.toString());
//        break;
//        case Input.Keys.U:
//        mainLight.color.a -= 0.1f;
//        Gdx.app.log("Light", mainLight.toString());
//        break;
//        case Input.Keys.I:
//        mainLight.color.a += 0.1f;
//        Gdx.app.log("Light", mainLight.toString());
//        break;
//        case Input.Keys.A:
//        mainLight.falloff.x -= 0.1f;
//        Gdx.app.log("Light", mainLight.toString());
//        break;
//        case Input.Keys.S:
//        mainLight.falloff.x += 0.1f;
//        Gdx.app.log("Light", mainLight.toString());
//        break;
//        case Input.Keys.D:
//        mainLight.falloff.y -= 0.1f;
//        Gdx.app.log("Light", mainLight.toString());
//        break;
//        case Input.Keys.F:
//        mainLight.falloff.y += 0.1f;
//        Gdx.app.log("Light", mainLight.toString());
//        break;
//        case Input.Keys.G:
//        mainLight.falloff.z -= 1f;
//        Gdx.app.log("Light", mainLight.toString());
//        break;
//        case Input.Keys.H:
//        mainLight.falloff.z += 1f;
//        Gdx.app.log("Light", mainLight.toString());
//        break;
//        case Input.Keys.J:
//        mainLight.position.z -= 0.1f;
//        Gdx.app.log("Light", mainLight.toString());
//        break;
//        case Input.Keys.K:
//        mainLight.position.z += 0.1f;
//        Gdx.app.log("Light", mainLight.toString());
//        break;

        return true;
    }
}
