package com.ting.scene;

import com.threed.jpct.Light;
import com.threed.jpct.Object3D;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;

public class Scene extends BaseScene {
	public RGBColor background = RGBColor.BLUE;
	public RGBColor ambient = new RGBColor(0, 255, 0);

	Object3D box;
	SimpleVector sunOffset = new SimpleVector(-100, -100, -75);

	public Scene() {
		//world.setAmbientLight(ambient.getRed(), ambient.getGreen(), ambient.getBlue());

		box = loadOBJ("cube1", 16);
		box.calcTextureWrapSpherical();
		box.setTexture(addTexture("box.jpg"));
		box.build();
		world.addObject(box);

		world.getCamera().setPosition(50, -50, -5);
		world.getCamera().lookAt(box.getTransformedCenter());

	    Light sun = new Light(world);
		sun.setIntensity(250, 250, 250);
		sun.setPosition(box.getTransformedCenter().calcAdd(sunOffset));
	}

	public void loop() {
		box.rotateY(0.02f);
	}

	public void move(float dx, float dy) {
		if (dx != 0) {
			box.rotateY(dx/-100f);
		}
		if (dy != 0) {
			box.rotateX(dy/-100f);
		}
	}

}
