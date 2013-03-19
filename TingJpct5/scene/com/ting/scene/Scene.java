package com.ting.scene;

import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.World;
import com.ting.scene.BaseScene;

public class Scene extends BaseScene {
	public World world = new World();
	public RGBColor background = RGBColor.BLUE;
	public RGBColor ambient = new RGBColor(0, 255, 0);

	Object3D box;

	public Scene() {
		world.setAmbientLight(ambient.getRed(), ambient.getGreen(), ambient.getBlue());

		box = Primitives.getBox(13f, 2f);
		box.calcTextureWrapSpherical();
		box.setTexture(addTexture("box.jpg"));
		box.setEnvmapped(Object3D.ENVMAP_ENABLED);
		box.build();
		world.addObject(box);

		world.getCamera().setPosition(50, -50, -5);
		world.getCamera().lookAt(box.getTransformedCenter());
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
