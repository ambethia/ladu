class ParticleSystem < EntitySystem::System

  def setup
    @effects = {}
    [:explosion, :bullet_destruct, :shield_damage].each do |type|
      @effects[type] = ParticleEffect.new
      @effects[type].load_emitters(load_asset("#{type}.particle"))
      @effects[type].load_emitter_images($game.screen.atlas)
    end
  end

  def update(delta)
    each(ParticleComponent) do |entity, particle|
      spatial = manager.component(SpatialComponent, entity)
      if !particle.started
        @effects[particle.effect].emitters.each(&:start)
        particle.started = true
      end
      if particle.attach_to
        target = manager.component(SpatialComponent, particle.attach_to)
        spatial.px = target.px
        spatial.py = target.py
      end
      @effects[particle.effect].set_position(spatial.px, spatial.py)
      # TODO: remove finished particle components
    end
  end

  def render(delta)
    @effects.values.each do |effect|
      effect.draw($game.screen.batch, delta)
    end
  end
end
