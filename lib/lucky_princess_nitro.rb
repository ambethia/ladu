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

  attr_reader :is_running

  def initialize
    @is_running = true
  end

  def create
    ShaderProgram.pedantic = false
    set_screen GameScreen.new(self)
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

