class PlayerSystem < EntitySystem::System
  ROTATION_SPEED = 180
  MAX_SPEED = 120
  MIN_SPEED = -90
  ACCELERATION = 50
  DECELERATION = 30
  RATE_OF_FIRE = 0.1
  BULLET_SPEED = 240
  BULLET_RADIUS = 6

  def update(delta)
    entity = manager.find('player')
    spatial = manager.component(SpatialComponent, entity)
    player = manager.component(PlayerComponent, entity)

    # Update speed. We automatically accelerate up the the max speed,
    # except when turning, when we lose some speed
    speed = spatial.speed
    speed += ACCELERATION * delta
    if speed > MAX_SPEED
      speed = MAX_SPEED
    elsif speed < MIN_SPEED
      speed = MIN_SPEED
    end
    spatial.speed = speed

    if player.is_turning_right
      spatial.bearing += ROTATION_SPEED * delta
      spatial.bearing = spatial.bearing % 360
      spatial.speed -= DECELERATION * delta
    end

    if player.is_turning_left
      spatial.bearing += (ROTATION_SPEED * -1) * delta
      spatial.bearing = spatial.bearing % 360
      spatial.speed -= DECELERATION * delta
    end

    if player.is_firing
      @last_fired ||= 0
      @last_fired += delta
      if @last_fired >= RATE_OF_FIRE
        @last_fired = 0
        fire_bullet
      end
    end
  end

  def setup
    screen = manager.game.screen
    atlas = screen.atlas
    frames = []

    # 0
    frames[0] = atlas.create_sprite("player_c0")
    frames[1] = atlas.create_sprite("player_c1")
    # 45
    frames[2] = atlas.create_sprite("player_b0")
    frames[3] = atlas.create_sprite("player_b1")
    # 90
    frames[4] = atlas.create_sprite("player_a0")
    frames[5] = atlas.create_sprite("player_a1")
    # 135
    frames[6] = atlas.create_sprite("player_b0")
    frames[6].flip(true, false)
    frames[7] = atlas.create_sprite("player_b1")
    frames[7].flip(true, false)
    # 180
    frames[8] = atlas.create_sprite("player_c0")
    frames[8].flip(true, false)
    frames[9] = atlas.create_sprite("player_c1")
    frames[9].flip(true, false)
    # 225
    frames[10] = atlas.create_sprite("player_b0")
    frames[10].flip(true, true)
    frames[11] = atlas.create_sprite("player_b1")
    frames[11].flip(true, true)
    # 270
    frames[12] = atlas.create_sprite("player_a0")
    frames[12].flip(false, true)
    frames[13] = atlas.create_sprite("player_a1")
    frames[13].flip(false, true)
    # 315
    frames[14] = atlas.create_sprite("player_b0")
    frames[14].flip(false, true)
    frames[15] = atlas.create_sprite("player_b1")
    frames[15].flip(false, true)

    animations = {
        0 => Animation.new(0.25, *frames[0..1]),
       45 => Animation.new(0.25, *frames[2..3]),
       90 => Animation.new(0.25, *frames[4..5]),
      135 => Animation.new(0.25, *frames[6..7]),
      180 => Animation.new(0.25, *frames[8..9]),
      225 => Animation.new(0.25, *frames[10..11]),
      270 => Animation.new(0.25, *frames[12..13]),
      315 => Animation.new(0.25, *frames[14..15])
    }

    @bullet_sprites = {
        0 => atlas.create_sprite("bullet", 0),
       45 => atlas.create_sprite("bullet", 1),
       90 => atlas.create_sprite("bullet", 2),
      135 => atlas.create_sprite("bullet", 1),
      180 => atlas.create_sprite("bullet", 0),
      225 => atlas.create_sprite("bullet", 1),
      270 => atlas.create_sprite("bullet", 2),
      315 => atlas.create_sprite("bullet", 1)
    }
    @bullet_sprites[90].flip(false, false)
    @bullet_sprites[135].flip(true, false)
    @bullet_sprites[180].flip(true, false)
    @bullet_sprites[225].flip(true, true)
    @bullet_sprites[270].flip(false, true)
    @bullet_sprites[315].flip(false, true)

    entity = manager.create('player')
    manager.attach(entity, PlayerComponent.new)
    manager.attach(entity, MotionComponent.new)
    manager.attach(entity, SpatialComponent.new({
      px: manager.game.width / 2,
      py: manager.game.height / 2,
      bearing: 0, speed: 0
    }))
    manager.attach(entity, AnimatedComponent.new({
      is_looped: true
    }))
    manager.attach(entity, RenderableComponent.new)
    manager.attach(entity, RotatedComponent.new({
      mapping: animations, is_animated: true
    }))
  end

  private

  def fire_bullet
    spatial = manager.component(SpatialComponent, manager.find('player'))
    bullet = manager.create('bullet')
    manager.attach(bullet, SpatialComponent.new({
      px: spatial.px, py: spatial.py,
      bearing: spatial.bearing, speed: BULLET_SPEED
    }))
    manager.attach(bullet, CollisionComponent.new({
      owner: manager.find('player'),
      radius: BULLET_RADIUS
    }))
    manager.attach(bullet, MotionComponent.new)
    manager.attach(bullet, RenderableComponent.new)
    manager.attach(bullet, RangedCullComponent.new({
      range: 400
    }))
    manager.attach(bullet, RotatedComponent.new({
      mapping: @bullet_sprites
    }))
  end
end
