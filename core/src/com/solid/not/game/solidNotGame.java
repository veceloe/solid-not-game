package com.solid.not.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;


public class solidNotGame extends ApplicationAdapter {

	public PerspectiveCamera cam;
	public ModelBatch modelBatch;
	public Model model;
	public Array<ModelInstance> instances = new Array<ModelInstance>();
	public Environment environment;
	public FirstPersonCameraController cic;
	public Player player;
	public Boolean loading;
	public ModelInstance ground;

	btCollisionShape groundShape;
	btCollisionShape playerShape;

	btCollisionObject groundObject;
	btCollisionObject playerObject;

	btCollisionConfiguration collisionConfig;
	btDispatcher dispatcher;
	boolean collision;

	protected Stage stage;
	protected Label label;
	protected BitmapFont font;
	protected StringBuilder stringBuilder;

	@Override
	public void create () {
		Bullet.init();

		stage = new Stage();
		font = new BitmapFont();
		label = new Label(" ", new Label.LabelStyle(font, Color.WHITE));
		stage.addActor(label);
		stringBuilder = new StringBuilder();

		modelBatch = new ModelBatch();
		cam = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(10f, 10f, 10f);
		cam.lookAt(0,0,0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		cic = new FirstPersonCameraController(cam);
		Gdx.input.setInputProcessor(cic);

		player = new Player();
		player.create();
		loading = true;

		ModelBuilder mb = new ModelBuilder();
		mb.begin();
		mb.node().id = "ground";
		mb.part("box", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.GOLD))).box(5f, 1f, 5f);
		model = mb.end();

		ground = new ModelInstance(model, "ground");

		instances = new Array<ModelInstance>();
		instances.add(ground);
		instances.add(player.instance);

		playerShape = new btBoxShape(new Vector3(0.05f, 0.05f, 1.5f));
		groundShape = new btBoxShape(new Vector3(2.5f, 0.5f, 2.5f));

		groundObject = new btCollisionObject();
		groundObject.setCollisionShape(groundShape);
		groundObject.setWorldTransform(ground.transform);

		playerObject = new btCollisionObject();
		playerObject.setCollisionShape(playerShape);

		collisionConfig = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfig);
	}

	public void camFollow(){
		player.instance.transform.setTranslation(cam.position);
		playerObject.setWorldTransform(player.instance.transform);
		cam.position.set(cam.position.x,cam.position.y-0.01f, cam.position.z);
	}

	@Override
	public void render() {
		collision = checkCollision();
		if (collision) {
			cam.position.set(ground.transform.getScaleX(), ground.transform.getScaleY(), ground.transform.getScaleZ());

		}

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(cam);
		modelBatch.render(instances, environment);
		modelBatch.end();

		cic.update();
		camFollow();

		stringBuilder.setLength(0);
		stringBuilder.append(" FPS: ").append(Gdx.graphics.getFramesPerSecond()).append(" ");
		stringBuilder.append("coord: ").append(cam.position);
		label.setText(stringBuilder);
		stage.draw();
	}

	boolean checkCollision () {
		CollisionObjectWrapper co0 = new CollisionObjectWrapper(playerObject);
		CollisionObjectWrapper co1 = new CollisionObjectWrapper(groundObject);

		btCollisionAlgorithmConstructionInfo ci = new btCollisionAlgorithmConstructionInfo();
		ci.setDispatcher1(dispatcher);
		btCollisionAlgorithm algorithm = new btBoxBoxCollisionAlgorithm(null, ci, co0.wrapper, co1.wrapper);

		btDispatcherInfo info = new btDispatcherInfo();
		btManifoldResult result = new btManifoldResult(co0.wrapper, co1.wrapper);

		algorithm.processCollision(co0.wrapper, co1.wrapper, info, result);

		boolean r = result.getPersistentManifold().getNumContacts() > 0;

		result.dispose();
		info.dispose();
		algorithm.dispose();
		ci.dispose();
		co1.dispose();
		co0.dispose();

		return r;
	}

	@Override
	public void dispose() {
		groundObject.dispose();
		groundShape.dispose();

		playerObject.dispose();
		playerShape.dispose();

		dispatcher.dispose();
		collisionConfig.dispose();

		modelBatch.dispose();
		model.dispose();
		modelBatch.dispose();
		model.dispose();
		instances.clear();
	}
	@Override
	public void resume () {
	}

	@Override
	public void resize (int width, int height) {
	}

	@Override
	public void pause () {
	}
}

