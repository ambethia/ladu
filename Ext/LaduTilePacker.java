import java.io.File;
import java.io.IOException;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tiledmappacker.TiledMapPacker;
import com.badlogic.gdx.tiledmappacker.TiledMapPacker.TiledMapPackerSettings;
import com.badlogic.gdx.tools.imagepacker.TexturePacker.Settings;

public class LaduTilePacker {

    public static void main (String[] args) {
        File inputDir = null;
        File outputDir = null;

        Settings texturePackerSettings = new Settings();

        texturePackerSettings.padding = 2;
        texturePackerSettings.duplicatePadding = true;
        texturePackerSettings.alias = false;
        texturePackerSettings.defaultFilterMag = Texture.TextureFilter.Linear;
        texturePackerSettings.defaultFilterMin = Texture.TextureFilter.Linear;

        TiledMapPackerSettings packerSettings = new TiledMapPackerSettings();

        switch (args.length) {
            case 3: {
                inputDir = new File(args[0]);
                outputDir = new File(args[1]);
                if ("--strip-unused".equals(args[2])) {
                    packerSettings.stripUnusedTiles = true;
                }
                break;
            }
            case 2: {
                inputDir = new File(args[0]);
                outputDir = new File(args[1]);
                break;
            }
            case 1: {
                inputDir = new File(args[0]);
                outputDir = new File(inputDir, "output/");
                break;
            }
            default: {
                System.out.println("Usage: INPUTDIR [OUTPUTDIR] [--strip-unused]");
                System.exit(0);
            }
        }

        TiledMapPacker packer = new TiledMapPacker(packerSettings);

        if (!inputDir.exists()) {
            throw new RuntimeException("Input directory does not exist");
        }

        try {
            packer.processMap(inputDir, outputDir, texturePackerSettings);
        } catch (IOException e) {
            throw new RuntimeException("Error processing map: " + e.getMessage());
        }
    }
}
