class SpawnSystem < EntitySystem::System
  SPAWN_TIME = 1
  MAX_ENEMIES = 5

  def update(delta)
    @elapsed ||= 0
    @elapsed += delta

    @active_enemy_count = manager.all(:enemy).size

    if @active_enemy_count < MAX_ENEMIES
      if @elapsed > SPAWN_TIME
        spawn_enemy
        @elapsed = 0
      end
    else
      @elapsed = 0
    end

  end

  private

  def spawn_enemy
    manager.factory.enemy do |enemy|
      enemy.type = :a
    end
    $game.screen.sounds[:enemy_a_spawn].play
  end
end
