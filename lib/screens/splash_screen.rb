class SplashScreen < BaseScreen

  def setup
    @update_systems = %w[
      Input
    ]

    @render_systems = %w[
      Splash
    ]
  end
end
