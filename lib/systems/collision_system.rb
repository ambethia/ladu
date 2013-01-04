class CollisionSystem < EntitySystem::System
  ENEMY_RADIUS = 8
  PLAYER_RADIUS = 8

  def update(delta)
    each(CollisionComponent) do |entity, component|
      collider_s = manager.component(SpatialComponent, entity)
      collider_c = Circle.new(collider_s.px, collider_s.py, component.radius)
      case manager.tag(component.owner)
      when :player
        manager.all(:enemy).each do |enemy|
          enemy_s = manager.component(SpatialComponent, enemy)
          enemy_c = Circle.new(enemy_s.px, enemy_s.py, ENEMY_RADIUS)
          if Intersector.overlapCircles(enemy_c, collider_c)
            if manager.tag(entity) == :bullet
              manager.factory.particle do |particle|
                particle.type = :explosion
                particle.px = enemy_s.px
                particle.py = enemy_s.py
              end
              manager.destroy(entity)
              manager.destroy(enemy)
            end
          end
        end
      when :enemy

      end
    end
  end
end
