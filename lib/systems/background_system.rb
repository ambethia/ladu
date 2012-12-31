class BackgroundSystem < EntitySystem::System
  STARFIELD_WIDTH = 320
  STARFIELD_HEIGHT = 200
  STARFIELD_DENSITY = 100

  def setup
    # dark grey fill
    @pixmap = Pixmap.new(manager.game.width, manager.game.height, Pixmap::Format::RGB888)
    @pixmap.set_color(0.1,0.1,0.1,1)
    @pixmap.fill
    @background = Texture.new(@pixmap)

    # stars
    @star_sprites = []
    3.times do |i|
      @star_sprites << manager.game.screen.atlas.create_sprite("star", i)
    end

    @stars = []
    STARFIELD_DENSITY.times do |i|
      @stars << [rand(STARFIELD_WIDTH), rand(STARFIELD_HEIGHT), rand(20)+1, rand(3)]
    end
  end

  def process(delta)
    world = Vector3.new(0, 0, 0)
    manager.component(CameraComponent, manager.find('camera')).object.unproject(world)
    @background.draw(@pixmap, 0, 0)
    manager.game.screen.batch.draw(@background, world.x, world.y - manager.game.height)

    player = manager.component(SpatialComponent,
      manager.find('player'))

    camera = manager.component(SpatialComponent,
      manager.find('camera'))

    @stars.each do |star|
      # translate relative to the camera, parallax based on depth and wrap them around the screen
      x = (camera.px + (((star[0] - STARFIELD_WIDTH) - (player.px / (star[2] * 0.25))) % STARFIELD_WIDTH) - STARFIELD_WIDTH / 2)
      y = (camera.py + (((star[1] - STARFIELD_HEIGHT) - (player.py / (star[2] * 0.25))) % STARFIELD_HEIGHT) - STARFIELD_HEIGHT / 2)

      # culll stars outside the viewport
      if x > world.x && x < world.x + manager.game.width &&
         y < world.y && y > world.y - manager.game.height
        manager.game.screen.batch.draw(@star_sprites[star[3]], x, y)
      end
    end
  end
end
