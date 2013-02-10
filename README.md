# Ladu

A simple Sokoban game.

## Pre-process Maps

rake maps

## Texture Pack

rake pack

## Task list

### Code (Jason)

- Fix the player rotation bug (the 360 % 360 == 0 thing)
- Block animations (turning gears or anything?)
- Push block animation/particle effect
- Integrate sound effects

### Art (Mostly James)

- Player
- Normal maps for Player
- Blocks
- Normal maps for Blocks
- I, T and L tracks
- Normal maps for I, T, and L tracks
- Background variations
  - There will need to be a background tile that fades to black on the edge, and a matching corner tile, only two
    because we can rotate them to fill all sides.
- Screens
  - Menu
  - Level Select
  - Credits (maybe just font change?)
- Wall tiles
  - Variations here are a good way to introduce variety to the levels with cool doodads.

### Sound (Mostly Jason)

- Player move forward
- Player rotate
- Player pushing
- Block activate
- Block deactivate
- Block activated humming (increase volume as more blocks are plugged in)?
- Level cleared
- Level reset (suicide)
- Compose music (I'm thinking 3-4 pieces to rotate between on levels?)
