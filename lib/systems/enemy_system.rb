class EnemySystem < EntitySystem::System

  def process(delta)
    each(EnemyComponent) do |entity, component|
      player = manager.component(SpatialComponent, manager.find('player'))
      camera = manager.component(SpatialComponent, manager.find('camera'))
      enemy = manager.component(SpatialComponent, entity)

      # enemy.px += (player.px - enemy.px + (Math.cos(player.px - enemy.px) * delta)) * delta * 4
      # enemy.py += (player.px - enemy.py + (Math.sin(player.py - enemy.py) * delta)) * delta * 4

      ellipse_w = 50
      ellipse_h = 30
      @angle ||= 0
      @angle += (2 * delta) % 360

      target_x = player.px - ((player.px - enemy.px) * 0.25)
      target_y = player.py - ((player.py - enemy.py) * 0.25)

      x = target_x - (ellipse_w / 2) + (Math.cos(@angle) * ellipse_w)
      y = target_y - (ellipse_h / 2) + (Math.sin(@angle) * ellipse_h)

      enemy.px = x
      enemy.py = y

      # face player
      # enemy.bearing += ((player.bearing - 180 % 360) - enemy.bearing) * delta * 2

    end
  end

  private

  def follow(a, b, m = 0.5)
    (a * (1 - m) + b * m)
  end
end
