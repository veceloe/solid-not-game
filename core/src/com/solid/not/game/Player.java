package com.solid.not.game;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class Player extends solidNotGame{
        public void create () {
            manager.load("assets/player.obj", Model.class);

        }
    private void load() {
        Model player = manager.get("data/ship.obj", Model.class);
        ModelInstance PlayerInstance = new ModelInstance(player);

    }
    }