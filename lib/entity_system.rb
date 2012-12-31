require 'securerandom'

module EntitySystem

  class Manager
    UNTAGGED = ''

    attr_reader :game

    def initialize(game = nil)
      @game = game
      @entities = []
      @ids_to_tags = Hash.new
      @tags_to_ids = Hash.new
      @components = Hash.new
    end

    def create(tag = UNTAGGED)
      entity = SecureRandom.uuid
      @entities << entity
      @ids_to_tags[entity] = tag
      if @tags_to_ids.has_key?(tag)
        @tags_to_ids[tag] << entity
      else
        @tags_to_ids[tag] = [entity]
      end
      entity
    end

    def destroy(entity)
      @components.each_value do |store|
        store.delete(entity)
      end
      @tags_to_ids.each_key do |tag|
        if @tags_to_ids[tag].include?(entity)
          @tags_to_ids[tag].delete(entity)
        end
      end
      @ids_to_tags.delete(entity)
      @entities.delete(entity)
    end

    def attach(entity, component)
      store = component_store(component.class)
      store[entity] = component
    end

    def remove(entity, component)
      store = component_store(component.class)
      store[entity] = nil
    end

    def component(component_class, entity)
      component_store(component_class)[entity]
    end

    def components(component_class)
      component_store(component_class).values
    end

    def entities(component_class)
      component_store(component_class).keys
    end

    def all(tag)
      @tags_to_ids[tag] || []
    end

    def find(tag)
      all(tag).first
    end

    def size
      @entities.size
    end

    private

    def component_store(component_class)
      @components[component_class] ||= Hash.new
    end
  end

  class Component

    def self.provides(*attribute_list)
      attribute_list.each do |key|
        attr_accessor key
      end
    end

    def initialize(attributes = {})
      attributes.each do |key, value|
        instance_variable_set("@#{key}", value)
      end
    end
  end

  class System

    attr_reader :manager

    def initialize(manager)
      @manager = manager
      setup
    end

    def each(component_class)
      manager.entities(component_class).each do |entity|
        yield(entity, manager.component(component_class, entity))
      end
    end

    def process(delta)
      raise RuntimeError, "Systems must `process()`."
    end

    # Optionally overriden in subclasses
    def setup
    end
  end
end
