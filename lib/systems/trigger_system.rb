class TriggerSystem < EntitySystem::System

  def process(delta)
    each(TriggerComponent) do |entity, component|
      spatial = manager.component(SpatialComponent, entity)
    end
  end
end
