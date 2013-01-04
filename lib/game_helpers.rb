module GameHelpers

  def load_asset(filename)
    Gdx.files.internal(RELATIVE_ROOT + "assets/#{filename}")
  end
end
