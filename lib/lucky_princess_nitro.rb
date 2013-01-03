require 'entity_system'

%w[screens components systems].each do |dir|
  Dir[ROOT_DIR + "/lib/#{dir}/*.rb"].each do |file|
    require File.basename(file)
  end
end

class LuckyPrincessNitro < Game
  VIRTUAL_WIDTH = 320
  VIRTUAL_HEIGHT = 200
  DEFAULT_SCALE = 3

  include ApplicationListener

  attr_reader :is_running,
    :game_screen,
    :splash_screen,
    :test_screen

  def initialize
    @is_running = true
  end

  def create
    ShaderProgram.pedantic = false
    @game_screen = GameScreen.new(self)
    @splash_screen = SplashScreen.new(self)
    @test_screen = TestScreen.new(self)

    if ENV['SCREEN']
      # for test modes
      set_screen send("#{ENV['SCREEN']}_screen")
    else
      toggle_fullscreen
      set_screen splash_screen
    end
  end

  def dispose
    @is_running = false
  end

  def toggle_fullscreen
    mode = Gdx.graphics.get_desktop_display_mode
    if Gdx.graphics.is_fullscreen
      Gdx.graphics.set_display_mode(width * DEFAULT_SCALE, height * DEFAULT_SCALE, false)
    else
      Gdx.graphics.set_display_mode(mode.width, mode.height, true)
    end
  end

  def width
    VIRTUAL_WIDTH
  end

  def height
    VIRTUAL_HEIGHT
  end
end

