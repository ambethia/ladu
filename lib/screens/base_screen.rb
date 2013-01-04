class BaseScreen
  include Screen
  include GameHelpers

  attr_reader :atlas, :batch

  def initialize(game)
    @game = game
    setup
  end

  def setup
    @systems = []
  end

  def show
    @batch = SpriteBatch.new
    @font = BitmapFont.new(load_asset("alterebro.fnt"), load_asset("alterebro.png"), false)
    @atlas = TextureAtlas.new(load_asset("sprites.pack"))

    @entity_manager = EntitySystem::Manager.new(@game)

    @initialized_systems = @systems.map { |n|
      Object.const_get("#{n}System").new(@entity_manager) }

    @buffer_object = FrameBuffer.new(Pixmap::Format::RGB888, @game.width, @game.height, false)
    @buffer_texture = @buffer_object.get_color_buffer_texture
    @buffer_texture.set_filter(Texture::TextureFilter::Nearest, Texture::TextureFilter::Nearest)
    @buffer_texture_region = TextureRegion.new(@buffer_texture)
    @buffer_texture_region.flip(false, true)
    @scale_factor, @elapsed = 0

    load_shader
  end

  def render(delta)
    @initialized_systems.each do |system|
      system.update(delta)
    end

    Gdx.gl.gl_viewport(@viewport.x, @viewport.y, @viewport.width, @viewport.height)

    @buffer_object.begin
    @batch.begin
    if @shader_program
      @batch.shader = @original_shader
    end
    Gdx.gl.gl_clear(GL20.GL_COLOR_BUFFER_BIT)
    @initialized_systems.each do |system|
      system.render(delta)
    end

    @batch.end
    render_debug if ENV['SCREEN']
    @buffer_object.end

    @batch.get_projection_matrix.set_to_ortho2_d(0, 0, @game.width, @game.height)
    Gdx.gl.gl_viewport(@viewport.x, @viewport.y, @viewport.width, @viewport.height)
    Gdx.gl.gl_clear(GL20.GL_COLOR_BUFFER_BIT)

    @batch.begin
    if @shader_program
      @batch.shader = @shader_program
      @shader_program.set_uniformf("u_deltaTime", delta)
      @shader_program.set_uniformf("u_scaleFactor", @scale_factor.to_f)
    end
    @batch.draw(@buffer_texture_region, 0, 0, @game.width, @game.height)
    @batch.end
  end

  def resize(width, height)
    @scale_factor = 1

    while @game.width * (@scale_factor + 1) <= width && @game.height * (@scale_factor + 1) <= height
      @scale_factor += 1
    end

    new_width = @game.width * @scale_factor
    new_height = @game.height * @scale_factor
    crop_x = (width - new_width) / 2
    crop_y = (height - new_height) / 2

    @viewport = Rectangle.new(crop_x, crop_y, new_width, new_height)
  end

  def pause
  end

  def hide
    @systems = []
  end

  def dispose
    @batch.dispose
    @atlas.dispose
    @font.dispose
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

  def render_debug
    @batch.begin
    @font.draw(@batch, debug_text, 8, 16)
    @batch.end
  end

  def debug_text
    "FPS: #{Gdx.graphics.get_frames_per_second}"
  end
end
