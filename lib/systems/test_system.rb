class TestSystem < EntitySystem::System

  def setup
    @effect = ParticleEffect.new
    @effect.load(load_asset("explosion.particle"), load_asset(""))
  end

  def update(delta)
    if !@has_run
      @has_run = true
      @effect.emitters.each(&:start)
    end

    if Gdx.input.is_key_pressed(Input::Keys::SPACE)
      @has_run = false
    end

    @effect.set_position(manager.game.width/2, manager.game.height/2)
  end

  def render(delta)
    @effect.draw(manager.game.screen.batch, delta)
  end
end
