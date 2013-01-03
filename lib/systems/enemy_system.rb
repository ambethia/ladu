class EnemySystem < EntitySystem::System

  def process(delta)
    each(EnemyComponent) do |entity, component|
      player_s = manager.component(SpatialComponent, manager.find('player'))
      player_c = manager.component(PlayerComponent, manager.find('player'))
      camera = manager.component(SpatialComponent, manager.find('camera'))
      enemy = manager.component(SpatialComponent, entity)

      ellipse_w, ellipse_h = 50, 30

      if player_c.is_turning_left
        component.data[:t_angle] -= (2 * delta) % 360
      elsif player_c.is_turning_right
        component.data[:t_angle] += (2 * delta) % 360
      end
      component.data[:c_angle] = weighted_average(component.data[:c_angle], component.data[:t_angle])

      delta_x = player_s.px - enemy.px
      delta_y = player_s.py - enemy.py
      target_x = player_s.px - (delta_x * 0.25)
      target_y = player_s.py - (delta_y * 0.25)
      enemy_x = target_x + (Math.cos(component.data[:c_angle]) * ellipse_w)
      enemy_y = target_y + (Math.sin(component.data[:c_angle]) * ellipse_h)

      enemy.px = weighted_average(enemy.px, enemy_x, 12)
      enemy.py = weighted_average(enemy.py, enemy_y, 12)

      # face player
      angle = (Math.atan2(delta_y , delta_x) * 180 / Math::PI) % 360
      enemy.bearing = angle
    end
  end

  private

  def weighted_average(v, w, n = 40)
    ((v * (n - 1)) + w) / n;
  end
end

