package com.ting.scene;

import java.awt.Color;

import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.World;
import com.ting.common.SceneHelper;

public class HelloScene {
	public World world;
	public Color background = Color.BLUE;

	Object3D box;

	public HelloScene() {
		world = new World();
		world.setAmbientLight(0, 255, 0);

		box = Primitives.getBox(13f, 2f);
		box.setTexture(SceneHelper.addTexture("box.jpg"));
		box.setEnvmapped(Object3D.ENVMAP_ENABLED);
		box.build();
		world.addObject(box);

		world.getCamera().setPosition(50, -50, -5);
		world.getCamera().lookAt(box.getTransformedCenter());
	}

	public void loop() {
		box.rotateY(0.01f);
	}
}
