class GameScreen < BaseScreen

  def setup
    @update_systems = %w[
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
    ]

    @render_systems = %w[
      Background
      Render
    ]
  end

  def debug_text
    super + ", Enities: #{@entity_manager.size}"
  end
end
