package com.ting.scene;

import com.threed.jpct.Camera;
import com.threed.jpct.Light;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;

public class Scene extends VectorScene {
	public RGBColor background = new RGBColor(50, 50, 100);
	public RGBColor ambient = new RGBColor(20, 20, 20);

	private Object3D cube = null;
	private Object3D center = null;
	SimpleVector sunOffset = new SimpleVector(-100, -100, -75);

	Object3D addCone(Object3D parent, float size, SimpleVector axis, double angle, SimpleVector offset, String texture) {
		Object3D cone = Primitives.getCone(size);
		cone.rotateAxis(axis, (float)angle);
		cone.translate(offset);
		cone.setTexture(texture);
		//coneX.setAdditionalColor(RGBColor.RED);
		add(cone, parent);
		return cone;
	}

	public Scene() {
		world.setAmbientLight(ambient.getRed(), ambient.getGreen(), ambient.getBlue());

		center = Primitives.getSphere(1);
		add(center, null);

		cube = Primitives.getCube(10);
		cube.calcTextureWrapSpherical();
		// cube.setTexture(addTexture("texture", R.drawable.icon, 64, 64));
		cube.setTexture(colorTexture("#00a0a0"));
		add(cube, center);

		addCone(center, 1, AXIS.Z, -Math.PI/2, v3(15,0,0), colorTexture("#ff0000"));
		addCone(center, 1, AXIS.X, -Math.PI,   v3(0,15,0), colorTexture("#00ff00"));
		addCone(center, 1, AXIS.X,  Math.PI/2, v3(0,0,15), colorTexture("#0000ff"));
		
		Camera cam = world.getCamera();
		cam.moveCamera(Camera.CAMERA_MOVEOUT, 50);
		cam.lookAt(cube.getTransformedCenter());

	    Light sun = new Light(world);
		sun.setIntensity(250, 250, 250);
		sun.setPosition(cube.getTransformedCenter().calcAdd(sunOffset));
	}

	public void move(float dx, float dy) {
		if (dx != 0) {
			center.rotateY(dx/-100f);
		}
		if (dy != 0) {
			center.rotateX(dy/-100f);
		}
	}

	public void loop() {
		center.rotateY(0.01f);
	}
}
