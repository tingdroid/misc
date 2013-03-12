package com.ting.scene;

import com.threed.jpct.Camera;
import com.threed.jpct.Light;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;
import com.ting.common.SceneHelper;

public class Scene {
	public World world;
	public RGBColor background = new RGBColor(50, 50, 100);
	public RGBColor ambient = new RGBColor(20, 20, 20);

	Object3D cube;
	SimpleVector sunOffset = new SimpleVector(-100, -100, -75);

	public Scene() {
		world = new World();
		world.setAmbientLight(ambient.getRed(), ambient.getGreen(), ambient.getBlue());

		cube = Primitives.getCube(10);
		cube.calcTextureWrapSpherical();
		cube.setTexture(SceneHelper.addTexture("icon.png", 64, 64));
		cube.strip();
		cube.build();
		world.addObject(cube);

		Camera cam = world.getCamera();
		cam.moveCamera(Camera.CAMERA_MOVEOUT, 50);
		cam.lookAt(cube.getTransformedCenter());

	    Light sun = new Light(world);
		sun.setIntensity(250, 250, 250);
		sun.setPosition(cube.getTransformedCenter().calcAdd(sunOffset));
	}

	public void move(float dx, float dy) {
		if (dx != 0) {
			cube.rotateY(dx/-100f);
		}
		if (dy != 0) {
			cube.rotateX(dy/-100f);
		}
	}

	public void loop() {
		cube.rotateY(0.01f);
	}
}
