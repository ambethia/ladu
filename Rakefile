desc "Build LaduTilePacker"
task :packer do
  sh %[javac -classpath Main/libs/gdx.jar:Ext/gdx-tools.jar:Ext/gdx-tiled-preprocessor.jar Ext/LaduTilePacker.java]
end

desc "Preprocess Tiled Maps"
task :maps do
  cmd = "rm Main/data/maps/*; java -classpath Main/libs/gdx.jar:Ext/gdx-tools.jar:Ext/gdx-tiled-preprocessor.jar:Ext LaduTilePacker Assets/Maps Main/data/maps"
  tilesets = ["tiles"]
  tilesets.each do |tileset|
     sh %[mv Assets/Maps/#{tileset}.png Assets/Maps/#{tileset}-d.png]
  end
  tilesets.each do |tileset|
    sh %[mv Assets/Maps/#{tileset}-n.png Assets/Maps/#{tileset}.png]
  end
  sh cmd
  tilesets.each do |tileset|
    sh %[mv Assets/Maps/#{tileset}.png Assets/Maps/#{tileset}-n.png]
  end
  tilesets.each do |tileset|
    sh %[mv Main/data/maps/#{tileset}1.png ./#{tileset}-n.png]
  end
  tilesets.each do |tileset|
    sh %[mv Assets/Maps/#{tileset}-h.png Assets/Maps/#{tileset}.png]
  end
  sh cmd
  tilesets.each do |tileset|
    sh %[mv Assets/Maps/#{tileset}.png Assets/Maps/#{tileset}-h.png]
  end
  tilesets.each do |tileset|
    sh %[mv Main/data/maps/#{tileset}1.png ./#{tileset}-h.png]
  end
  tilesets.each do |tileset|
    sh %[mv Assets/Maps/#{tileset}-d.png Assets/Maps/#{tileset}.png]
  end
  sh cmd
  tilesets.each do |tileset|
    sh %[mv ./#{tileset}-n.png Main/data/maps/]
    sh %[mv ./#{tileset}-h.png Main/data/maps/]
  end
end

desc "Pack textures"
task :pack do
  sh %[java -classpath Main/libs/gdx.jar:Ext/gdx-tools.jar com.badlogic.gdx.tools.imagepacker.TexturePacker2 Assets/Textures Main/data]
end