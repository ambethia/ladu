class GameScreen < BaseScreen

  def setup
    @systems = %w[
      Input
      Player
      Enemy
      Camera
      RangedCull
      Collision
      Spawn
      Motion
      Rotation
      Animation
      Background
      Particle
      Render
    ]
  end

  def debug_text
    player = @entity_manager.component(PlayerComponent, @entity_manager.find(:player))
    super + ", Shields: #{player.shields}"
  end
end
