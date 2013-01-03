class InputSystem < EntitySystem::System

  def update(delta)
    player = manager.component(PlayerComponent, manager.find('player'))

    if Gdx.input.is_key_pressed(Input::Keys::Q)
      Gdx.app.exit
    elsif Gdx.input.is_key_pressed(Input::Keys::F)
      manager.game.toggle_fullscreen
    end

    case manager.game.screen
    when SplashScreen
      if Gdx.input.is_key_pressed(Input::Keys::SPACE)
        manager.game.set_screen(manager.game.game_screen)
      end
    when GameScreen
      player.is_turning_right = Gdx.input.is_key_pressed(Input::Keys::RIGHT)
      player.is_turning_left = Gdx.input.is_key_pressed(Input::Keys::LEFT)
      player.is_firing = Gdx.input.is_key_pressed(Input::Keys::SPACE)
    end
  end
end
