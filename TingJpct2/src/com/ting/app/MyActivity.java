package com.ting.app;

import com.threed.jpct.Camera;
import com.threed.jpct.Light;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;
import android.widget.SimpleCursorAdapter;
import com.threed.jpct.RGBColor;

/**
 * A simple demo. 
 * 
 */
public class MyActivity extends GLActivity {

	private Object3D cube = null;
	private Object3D center = null;
	SimpleVector sunOffset = new SimpleVector(-100, -100, -75);

	void add(Object3D obj) {
		obj.strip();
		obj.build();
		world.addObject(obj);		
	}
	
	@Override
    void init() {		
		world = new World();
		world.setAmbientLight(20, 20, 20);

		cube = Primitives.getCube(10);
		cube.calcTextureWrapSpherical();
		cube.setTexture(addTexture("texture", R.drawable.icon, 64, 64));
		add(cube);

		center = Primitives.getSphere(1);
		add(center);
		center.addChild(cube);

		Object3D coneX = Primitives.getCone(1);
		coneX.translate(15, 0, 0);
		coneX.setAdditionalColor(new RGBColor(255, 0, 0));
		center.addChild(coneX);
		add(coneX);
		
		Object3D coneY = Primitives.getCone(1);
		coneY.translate(0, 15, 0);
		coneY.setAdditionalColor(new RGBColor(0, 255, 0));
		center.addChild(coneY);
		add(coneY);

		Object3D coneZ = Primitives.getCone(1);
		coneZ.translate(0, 0, 15);
		coneZ.setAdditionalColor(new RGBColor(0, 0, 255));
		center.addChild(coneZ);
		add(coneZ);

		Camera cam = world.getCamera();
		cam.moveCamera(Camera.CAMERA_MOVEOUT, 50);
		cam.lookAt(cube.getTransformedCenter());

	    Light sun = new Light(world);
		sun.setIntensity(250, 250, 250);
		sun.setPosition(cube.getTransformedCenter().calcAdd(sunOffset));
    }

	@Override
	void move(float dx, float dy) {
		if (dx != 0) {
			center.rotateY(dx/-100f);
		}

		if (dy != 0) {
			center.rotateX(dy/-100f);
		}
	}
}

