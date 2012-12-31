class CameraSystem < EntitySystem::System
  CAMERA_TRACKING_SPEED = 0.075

  def setup
    entity = manager.create('camera')
    @spatial = manager.attach(entity, SpatialComponent.new({
      px: manager.game.width / 2, py: manager.game.height / 2
    }))
    @camera = manager.attach(entity, CameraComponent.new({
      object: OrthographicCamera.new
    }))

    @camera.object.set_to_ortho(false, manager.game.width, manager.game.height)
    @camera.object.position.set(@spatial.px, @spatial.py, 0)
    @viewport = Rectangle.new(0, 0, manager.game.width, manager.game.height)
  end

  def process(delta)
    player = manager.component(SpatialComponent, manager.find('player'))
    x_vector = Math.cos(player.bearing * Math::PI/180)
    y_vector = Math.sin(player.bearing * Math::PI/180)
    x_offset = player.px + (x_vector * manager.game.width * 0.0035 * player.speed.abs)
    y_offset = player.py + (y_vector * manager.game.height * 0.003 * player.speed.abs)
    @spatial.px = follow(@spatial.px, x_offset, CAMERA_TRACKING_SPEED)
    @spatial.py = follow(@spatial.py, y_offset, CAMERA_TRACKING_SPEED)
    @camera.object.position.set(@spatial.px, @spatial.py, 0)
    @camera.object.update
    manager.game.screen.batch.set_projection_matrix(@camera.object.combined)
  end

  private

  def follow(a, b, m = 0.5)
    (a * (1 - m) + b * m)
  end
end
