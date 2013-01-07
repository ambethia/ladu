class CollisionSystem < EntitySystem::System
  ENEMY_RADIUS = 8
  PLAYER_RADIUS = 8

  def update(delta)
    each(CollisionComponent) do |entity, component|
      collider_s = manager.component(SpatialComponent, entity)
      next unless collider_s # incase the collider was suddenly removed
      collider_c = Circle.new(collider_s.px, collider_s.py, component.radius)
      player_e = manager.find(:player)
      player_s = manager.component(SpatialComponent, player_e)
      player_c = Circle.new(player_s.px, player_s.py, PLAYER_RADIUS)
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
              $game.screen.sounds[:enemy_a_death].play(0.6)
              manager.destroy(entity)
              manager.destroy(enemy)
            end
          end
        end
        manager.all(:bullet).each do |bullet|
          # dont't destroy our own bullets
          next if manager.component(CollisionComponent, bullet).owner == player_e

          bullet_s = manager.component(SpatialComponent, bullet)
          bullet_c = Circle.new(bullet_s.px, bullet_s.py, component.radius)
          if Intersector.overlapCircles(bullet_c, collider_c)
            manager.factory.particle do |particle|
              particle.type = :bullet_destruct
              particle.px = bullet_s.px
              particle.py = bullet_s.py
            end
            $game.screen.sounds[:bullet_destruct].play
            manager.destroy(entity)
            manager.destroy(bullet)
          end
        end
      when :enemy
        if Intersector.overlapCircles(player_c, collider_c)
          manager.factory.particle do |particle|
            particle.type = :shield_damage # TODO: Shield damage
            particle.attach_to = player_e
            particle.px = player_s.px
            particle.py = player_s.py
          end
          # sustain damage
          manager.component(PlayerComponent, player_e).shields -= 1
          $game.screen.sounds[:shield_damage].play
          manager.destroy(entity)
        end
      end
    end
  end
end
