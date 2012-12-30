require 'securerandom'
require 'set'

module EntitySystem

  class Manager

    attr_reader :game

    def initialize(game = nil)
      @components = Hash.new
      @entities = []
      @game = game
    end

    def create
      entity = SecureRandom.uuid
      @entities << entity
      entity
    end

    def destroy(entity)
      @components.each_value do |store|
        store.delete(entity)
      end
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
    end

    def each(component_class)
      manager.entities(component_class).each do |entity|
        yield(entity, manager.component(component_class, entity))
      end
    end

    def process(delta)
      raise RuntimeError, "Systems must `process()`."
    end
  end
end
