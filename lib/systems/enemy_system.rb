class EnemySystem < EntitySystem::System

  def process(delta)
    each(EnemyComponent) do |entity, component|
      player_s = manager.component(SpatialComponent, manager.find('player'))
      player_c = manager.component(PlayerComponent, manager.find('player'))
      camera = manager.component(SpatialComponent, manager.find('camera'))
      enemy = manager.component(SpatialComponent, entity)

      # enemy.px += (player_s.px - enemy.px + (Math.cos(player_s.px - enemy.px) * delta)) * delta * 4
      # enemy.py += (player_s.px - enemy.py + (Math.sin(player_s.py - enemy.py) * delta)) * delta * 4

      ellipse_w, ellipse_h = 50, 30

      @angle ||= 0
      if player_c.is_turning_left || player_c.is_turning_right
        @angle += (2 * delta) % 360
      end

      delta_x = player_s.px - enemy.px
      delta_y = player_s.py - enemy.py
      target_x = player_s.px - (delta_x * 0.25)
      target_y = player_s.py - (delta_y * 0.25)

      enemy.px = target_x + (Math.cos(@angle) * ellipse_w)
      enemy.py = target_y + (Math.sin(@angle) * ellipse_h)

      # face player
      # enemy.bearing += ((player_s.bearing - 180 % 360) - enemy.bearing) * delta * 2

      angle = Math.atan2(delta_y , delta_x) * 180 / Math::PI
      enemy.bearing = angle
    end
  end

  private

  def follow(a, b, m = 0.5)
    (a * (1 - m) + b * m)
  end
end
