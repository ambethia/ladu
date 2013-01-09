class InterfaceSystem < EntitySystem::System

  def render(delta)
    player_id = manager.find(:player)
    if player_id
      player = manager.component(PlayerComponent, player_id)
      ox, oy = 8, $game.height - 16
      player.shields.times do |i|
        x = 8 + (ox * i)
        $game.screen.batch.draw($game.screen.sprites[:player_shield], x, oy)
      end
    end
    @font.draw($game.screen.batch, $game.screen.debug_text, 8, 16) if ENV['SCREEN']
  end

  def setup
    $game.screen.sprites[:player_shield] = $game.screen.atlas.create_sprite("shield")
    @font = BitmapFont.new(load_asset("alterebro.fnt"), load_asset("alterebro.png"), false)
  end
end
