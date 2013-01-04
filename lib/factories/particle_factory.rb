class ParticleFactory < EntitySystem::Factory::Base
  build :particle
  using :px, :py, :type

  def construct
    entity = manager.create(:particle)
    manager.attach(entity, ParticleComponent.new({
      effect: type
    }))
    manager.attach(entity, SpatialComponent.new({
      px: px , py: py, bearing: 0, speed: 0
    }))
    entity
  end
end
