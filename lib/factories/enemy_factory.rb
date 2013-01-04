class EnemyFactory < EntitySystem::Factory::Base
  build :enemy
  using :px, :py

  def construct
    entity = manager.create(:enemy)
    camera = manager.component(SpatialComponent, manager.find(:camera))
    entry_angle = rand(360)
    manager.attach(entity, EnemyComponent.new({
      type: 'a',
      data: {
        c_angle: entry_angle,
        t_angle: entry_angle
      }
    }))
    manager.attach(entity, SpatialComponent.new({
      px: camera.px + (Math.cos(entry_angle) * ($game.width * 1.2)),
      py: camera.py + (Math.sin(entry_angle) * ($game.height * 1.2)),
      bearing: 0, speed: 100
    }))
    manager.attach(entity, RenderableComponent.new)
    manager.attach(entity, RotatedComponent.new({
      mapping: $game.screen.sprites[:enemy_a]
    }))
  end
end
