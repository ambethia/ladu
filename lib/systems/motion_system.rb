class MotionSystem < EntitySystem::System

  def process(delta)
    each(SpatialComponent) do |entity, component|
      x_vel = Math.cos(component.bearing * Math::PI/180) * component.speed
      y_vel = Math.sin(component.bearing * Math::PI/180) * component.speed
      component.px += x_vel * delta
      component.py += y_vel * delta
    end
  end
end
