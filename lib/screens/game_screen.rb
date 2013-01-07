class GameScreen < BaseScreen

  def setup
    @systems = %w[
      Input
      Player
      Enemy
      Camera
      Music
      RangedCull
      Collision
      Spawn
      Motion
      Rotation
      Animation
      Background
      Render
      Particle
    ]
  end

  def debug_text
    player = @entity_manager.component(PlayerComponent, @entity_manager.find(:player))
    super + ", Shields: #{player.shields}"
  end
end
