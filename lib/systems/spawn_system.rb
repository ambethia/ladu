class SpawnSystem < EntitySystem::System
  SPAWN_TIME = 1
  MAX_ENEMIES = 1

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
    manager.attach(entity, EnemyComponent.new(type: 'a'))
    manager.attach(entity, SpatialComponent.new({
      px: camera.px,
      py: camera.py,
      bearing: 0, speed: 100
    }))
    manager.attach(entity, RenderableComponent.new)
    manager.attach(entity, RotatedComponent.new({
      mapping: @enemy_sprites['a']
    }))
  end
end
