class SpawnSystem < EntitySystem::System
  SPAWN_TIME = 1
  MAX_ENEMIES = 3

  def process(delta)
    @elapsed ||= 0
    @elapsed += delta

    @active_enemy_count = manager.all('enemy').size

    if @active_enemy_count < MAX_ENEMIES
      if @elapsed > SPAWN_TIME
        spawn_enemy
        @elapsed = 0
      end
    else
      @elapsed = 0
    end

  end

  def setup
    @enemy_sprites = {}
    atlas = manager.game.screen.atlas

    @enemy_sprites['a'] = {
        0 => atlas.create_sprite("enemy_a", 1),
       45 => atlas.create_sprite("enemy_a", 2),
       90 => atlas.create_sprite("enemy_a", 3),
      135 => atlas.create_sprite("enemy_a", 4),
      180 => atlas.create_sprite("enemy_a", 5),
      225 => atlas.create_sprite("enemy_a", 6),
      270 => atlas.create_sprite("enemy_a", 7),
      315 => atlas.create_sprite("enemy_a", 8)
    }
  end

  private

  def spawn_enemy
    camera = manager.component(SpatialComponent, manager.find('camera'))
    entity = manager.create('enemy')
    entry_angle = rand(360)
    manager.attach(entity, EnemyComponent.new({
      type: 'a',
      data: {
        c_angle: entry_angle,
        t_angle: entry_angle
      }
    }))
    manager.attach(entity, SpatialComponent.new({
      px: camera.px + (Math.cos(entry_angle) * (manager.game.width * 1.2)),
      py: camera.py + (Math.sin(entry_angle) * (manager.game.height * 1.2)),
      bearing: 0, speed: 100
    }))
    manager.attach(entity, RenderableComponent.new)
    manager.attach(entity, RotatedComponent.new({
      mapping: @enemy_sprites['a']
    }))
  end
end
