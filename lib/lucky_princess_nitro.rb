require 'entity_system'

%w[screens components systems].each do |dir|
  Dir[ROOT_DIR + "/lib/#{dir}/*.rb"].each do |file|
    require File.basename(file)
  end
end

class LuckyPrincessNitro < Game
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
end
