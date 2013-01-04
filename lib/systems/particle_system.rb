class ParticleSystem < EntitySystem::System

  def setup
    @effects = {}
    @effects['explosion'] = ParticleEffect.new
    @effects['explosion'].load_emitters(load_asset("explosion.particle"))
    @effects['explosion'].load_emitter_images($game.screen.atlas)

  end

  def update(delta)
    each(ParticleComponent) do |entity, particle|
      spatial = manager.component(SpatialComponent, entity)
      if !particle.started
        @effects[particle.effect].emitters.each(&:start)
        @effects[particle.effect].set_position(spatial.px, spatial.py)
        particle.started = true
      end
    end
  end

  def render(delta)
    @effects.values.each do |effect|
      effect.draw($game.screen.batch, delta)
    end
  end
end
