class InputSystem < EntitySystem::System

  def update(delta)
    player = manager.component(PlayerComponent, manager.find('player'))

    if Gdx.input.is_key_pressed(Input::Keys::Q)
      Gdx.app.exit
    elsif Gdx.input.is_key_pressed(Input::Keys::F)
      $game.toggle_fullscreen
    end

    case $game.screen
    when SplashScreen
      if Gdx.input.is_key_pressed(Input::Keys::SPACE)
        $game.set_screen($game.game_screen)
      end
    when GameScreen
      player.is_turning_right = Gdx.input.is_key_pressed(Input::Keys::RIGHT)
      player.is_turning_left = Gdx.input.is_key_pressed(Input::Keys::LEFT)
      player.is_firing = Gdx.input.is_key_pressed(Input::Keys::SPACE)
    when TestScreen
    end
  end
end
