class GameScreen
  include Screen

  VIRTUAL_WIDTH = 320
  VIRTUAL_HEIGHT = 200
  DEFAULT_SCALE = 3
  ASPECT_RATIO = VIRTUAL_WIDTH.to_f / VIRTUAL_HEIGHT.to_f
  CAMERA_TRACKING_SPEED = 0.075
  ROTATION_SPEED = 120
  PLAYER_MAX_SPEED = 120
  PLAYER_MIN_SPEED = -90
  PLAYER_ACCELERATION = 50
  DECELERATION = 60
  STARFIELD_WIDTH = VIRTUAL_WIDTH
  STARFIELD_HEIGHT = VIRTUAL_HEIGHT
  STARFIELD_DENSITY = 100
  RATE_OF_FIRE = 0.1
  BULLET_SPEED = PLAYER_MAX_SPEED * 2

  attr_reader :batch, :camera

  def initialize(game)
    @game = game
    @entity_manager = EntitySystem::Manager.new(game)
    @renderer = RenderSystem.new(@entity_manager)
    @update_systems = [
      RangedCullSystem.new(@entity_manager),
      MotionSystem.new(@entity_manager),
      RotationSystem.new(@entity_manager),
      AnimationSystem.new(@entity_manager)
    ]
  end

  def show
    @buffer_object = FrameBuffer.new(Pixmap::Format::RGB888, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, false)
    @buffer_texture = @buffer_object.get_color_buffer_texture
    @buffer_texture.set_filter(Texture::TextureFilter::Nearest, Texture::TextureFilter::Nearest)
    @buffer_texture_region = TextureRegion.new(@buffer_texture)
    @buffer_texture_region.flip(false, true)
    @batch = SpriteBatch.new
    @font = BitmapFont.new(load_asset("alterebro.fnt"), load_asset("alterebro.png"), false)
    @atlas = TextureAtlas.new(load_asset("sprites.txt"))
    @scale_factor, @elapsed = 0, 0

    setup_camera
    setup_background
    setup_player
    load_shader
  end

  def render(delta)
    @elapsed += delta

    handle_input(delta)
    update_background(delta)
    update_player(delta)
    update_camera(delta)

    @update_systems.each do |system|
      system.process(delta)
    end

    reset_viewport

    @buffer_object.begin
    @batch.begin
    if @shader_program
      @batch.shader = @original_shader
    end

    draw_background
    @renderer.process(delta)
    draw_player

    @batch.end
    render_fps
    @buffer_object.end

    reset_viewport

    @batch.begin
    if @shader_program
      @batch.shader = @shader_program
      @shader_program.set_uniformf("u_deltaTime", delta)
      @shader_program.set_uniformf("u_scaleFactor", @scale_factor.to_f)
    end

    @batch.draw(@buffer_texture_region, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT)
    @batch.end
  end

  def resize(width, height)
    @scale_factor = 1

    while VIRTUAL_WIDTH * (@scale_factor + 1) <= width && VIRTUAL_HEIGHT * (@scale_factor + 1) <= height
      @scale_factor += 1
    end

    new_width = VIRTUAL_WIDTH * @scale_factor
    new_height = VIRTUAL_HEIGHT * @scale_factor
    crop_x = (width - new_width) / 2
    crop_y = (height - new_height) / 2

    @viewport = Rectangle.new(crop_x, crop_y, new_width, new_height)
  end

  def pause
  end

  def hide
  end

  def dispose
  end

  def setup_player
    frames = []

    # 0
    frames[0] = @atlas.create_sprite("player_c0")
    frames[1] = @atlas.create_sprite("player_c1")
    # 45
    frames[2] = @atlas.create_sprite("player_b0")
    frames[3] = @atlas.create_sprite("player_b1")
    # 90
    frames[4] = @atlas.create_sprite("player_a0")
    frames[5] = @atlas.create_sprite("player_a1")
    # 135
    frames[6] = @atlas.create_sprite("player_b0")
    frames[6].flip(true, false)
    frames[7] = @atlas.create_sprite("player_b1")
    frames[7].flip(true, false)
    # 180
    frames[8] = @atlas.create_sprite("player_c0")
    frames[8].flip(true, false)
    frames[9] = @atlas.create_sprite("player_c1")
    frames[9].flip(true, false)
    # 225
    frames[10] = @atlas.create_sprite("player_b0")
    frames[10].flip(true, true)
    frames[11] = @atlas.create_sprite("player_b1")
    frames[11].flip(true, true)
    # 270
    frames[12] = @atlas.create_sprite("player_a0")
    frames[12].flip(false, true)
    frames[13] = @atlas.create_sprite("player_a1")
    frames[13].flip(false, true)
    # 315
    frames[14] = @atlas.create_sprite("player_b0")
    frames[14].flip(false, true)
    frames[15] = @atlas.create_sprite("player_b1")
    frames[15].flip(false, true)

    @player_animations = {
        0 => Animation.new(0.25, *frames[0..1]),
       45 => Animation.new(0.25, *frames[2..3]),
       90 => Animation.new(0.25, *frames[4..5]),
      135 => Animation.new(0.25, *frames[6..7]),
      180 => Animation.new(0.25, *frames[8..9]),
      225 => Animation.new(0.25, *frames[10..11]),
      270 => Animation.new(0.25, *frames[12..13]),
      315 => Animation.new(0.25, *frames[14..15])
    }

    @player_bullet_sprites = {
        0 => @atlas.create_sprite("bullet", 0),
       45 => @atlas.create_sprite("bullet", 1),
       90 => @atlas.create_sprite("bullet", 2),
      135 => @atlas.create_sprite("bullet", 1),
      180 => @atlas.create_sprite("bullet", 0),
      225 => @atlas.create_sprite("bullet", 1),
      270 => @atlas.create_sprite("bullet", 2),
      315 => @atlas.create_sprite("bullet", 1)
    }
    @player_bullet_sprites[90].flip(false, false)
    @player_bullet_sprites[135].flip(true, false)
    @player_bullet_sprites[180].flip(true, false)
    @player_bullet_sprites[225].flip(true, true)
    @player_bullet_sprites[270].flip(false, true)
    @player_bullet_sprites[315].flip(false, true)

    @x_pos, @y_pos = VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2
    @x_vel, @y_vel, @rotation, @speed = 0, 0, 0, 0
    @current_animation = 0
  end

  def update_player(delta)
    @speed += PLAYER_ACCELERATION * delta
    if @speed > PLAYER_MAX_SPEED
      @speed = PLAYER_MAX_SPEED
    elsif @speed < PLAYER_MIN_SPEED
      @speed = PLAYER_MIN_SPEED
    end

    @x_vel = Math.cos(@rotation * Math::PI/180)
    @y_vel = Math.sin(@rotation * Math::PI/180)

    @x_pos += @x_vel * delta * @speed
    @y_pos += @y_vel * delta * @speed

    @current_animation = @player_animations[nearest_angle(@rotation)]
  end

  def draw_player
    frame = @current_animation.get_key_frame(@elapsed, true)
    @batch.draw(frame, @x_pos - frame.width/2, @y_pos - frame.height/2)
  end

  def setup_camera
    @camera = OrthographicCamera.new
    @camera.set_to_ortho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT)
    @cam_x_pos, @cam_y_pos = VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2
    @camera.position.set(@cam_x_pos, @cam_y_pos, 0)
    @viewport = Rectangle.new(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT)
  end

  def update_camera(delta)
    x_offset = @x_pos + (@x_vel * VIRTUAL_WIDTH * 0.0035 * @speed.abs)
    y_offset = @y_pos + (@y_vel * VIRTUAL_HEIGHT * 0.003 * @speed.abs)
    @cam_x_pos = follow(@cam_x_pos, x_offset, CAMERA_TRACKING_SPEED)
    @cam_y_pos = follow(@cam_y_pos, y_offset, CAMERA_TRACKING_SPEED)
    @camera.position.set(@cam_x_pos, @cam_y_pos, 0)
    @camera.update
    @batch.set_projection_matrix(@camera.combined)
  end

  def reset_viewport
    Gdx.gl.gl_viewport(@viewport.x, @viewport.y, @viewport.width, @viewport.height)
    Gdx.gl.gl_clear(GL20.GL_COLOR_BUFFER_BIT)
  end

  def handle_input(delta)
    if Gdx.input.is_key_pressed(Input::Keys::Q)
      Gdx.app.exit
    elsif Gdx.input.is_key_pressed(Input::Keys::F)
      toggle_fullscreen
    end

    if Gdx.input.is_key_pressed(Input::Keys::RIGHT)
      @rotation += ROTATION_SPEED * delta
      @rotation = @rotation % 360
      @speed -= DECELERATION * delta
    end

    if Gdx.input.is_key_pressed(Input::Keys::LEFT)
      @rotation += (ROTATION_SPEED * -1) * delta
      @rotation = @rotation % 360
      @speed -= DECELERATION * delta
    end

    if Gdx.input.is_key_pressed(Input::Keys::SPACE)
      @last_fired ||= 0
      @last_fired += delta
      if @last_fired >= RATE_OF_FIRE
        @last_fired = 0
        fire_bullet
      end
    end
  end

  def setup_background
    # dark grey fill
    @bg_pixmap = Pixmap.new(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, Pixmap::Format::RGB888)
    @bg_pixmap.set_color(0.1,0.1,0.1,1)
    @bg_pixmap.fill
    @background = Texture.new(@bg_pixmap)

    # stars
    @star_sprites = []
    3.times do |i|
      @star_sprites << @atlas.create_sprite("star", i)
    end

    @stars = []
    STARFIELD_DENSITY.times do |i|
      @stars << [rand(STARFIELD_WIDTH), rand(STARFIELD_HEIGHT), rand(20)+1, rand(3)]
    end
  end

  def update_background(delta)

  end

  def draw_background
    world = screen_to_world(0, 0)

    @background.draw(@bg_pixmap, 0, 0)
    @batch.draw(@background, world.x, world.y - VIRTUAL_HEIGHT)

    @stars.each do |star|
      # translate relative to the camera, parallax based on depth and wrap them around the screen
      x = (@cam_x_pos + (((star[0] - STARFIELD_WIDTH) - (@x_pos / (star[2] * 0.25))) % STARFIELD_WIDTH) - STARFIELD_WIDTH / 2)
      y = (@cam_y_pos + (((star[1] - STARFIELD_HEIGHT) - (@y_pos / (star[2] * 0.25))) % STARFIELD_HEIGHT) - STARFIELD_HEIGHT / 2)

      # culll stars outside the viewport
      if x > world.x && x < world.x + VIRTUAL_WIDTH &&
         y < world.y && y > world.y - VIRTUAL_HEIGHT
        @batch.draw(@star_sprites[star[3]], x, y)
      end
    end
  end

  def fire_bullet
    bullet = @entity_manager.create
    image = @atlas.create_sprite("bullet", 0)
    @entity_manager.attach(bullet, SpatialComponent.new({
      px: @x_pos, py: @y_pos,
      bearing: @rotation, speed: BULLET_SPEED
    }))
    @entity_manager.attach(bullet, RenderableComponent.new)
    @entity_manager.attach(bullet, RangedCullComponent.new({
      range: VIRTUAL_WIDTH * 2
    }))
    @entity_manager.attach(bullet, RotatedComponent.new({
      mapping: @player_bullet_sprites
    }))
  end

  def nearest_angle(angle)
    ((angle.round + 45 / 2) / 45 * 45) % 360
  end

  def follow(a, b, m = 0.5)
    (a * (1 - m) + b * m)
  end

  def load_shader
    vert = load_asset('post_vertex.glsl').read_string
    frag = load_asset('post_fragment.glsl').read_string
    @original_shader = SpriteBatch.create_default_shader
    @shader_program = ShaderProgram.new(vert, frag)
    unless @shader_program.is_compiled
      Gdx.app.log("Shader Error:", @shader_program.get_log)
      @shader_program = nil
    end
  end

  def load_asset(filename)
    Gdx.files.internal(RELATIVE_ROOT + "assets/#{filename}")
  end

  def toggle_fullscreen
    mode = Gdx.graphics.get_desktop_display_mode
    if Gdx.graphics.is_fullscreen
      Gdx.graphics.set_display_mode(VIRTUAL_WIDTH * DEFAULT_SCALE, VIRTUAL_HEIGHT * DEFAULT_SCALE, false)
    else
      Gdx.graphics.set_display_mode(mode.width, mode.height, true)
    end
  end

  def screen_to_world(x, y)
    world = Vector3.new(x, y, 0)
    @camera.unproject(world)
    world
  end

  def render_fps
    @batch.get_projection_matrix.set_to_ortho2_d(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT)
    @batch.begin
    @font.draw(@batch,
      "FPS: #{Gdx.graphics.get_frames_per_second}, Enities: #{@entity_manager.size}", 8, 16)
    @batch.end
  end
end
