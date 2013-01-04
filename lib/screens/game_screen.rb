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
    super + ", Enities: #{@entity_manager.size}"
  end
end
