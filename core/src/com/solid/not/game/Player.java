package com.solid.not.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;

public class Player{

    public Model model;
    public ObjLoader loader;
    public ModelInstance instance;

    public void create () {
        loader = new ObjLoader();
        model = loader.loadModel(Gdx.files.internal("core/assets/player.obj"));
        instance = new ModelInstance(model);
    }

}