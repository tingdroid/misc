package com.ting.app;

import com.threed.jpct.Camera;
import com.threed.jpct.Light;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;
import android.widget.SimpleCursorAdapter;

/**
 * A simple demo. 
 * 
 */
public class MyActivity extends GLActivity {

	private Object3D cube = null;
	SimpleVector sunOffset = new SimpleVector(-100, -100, -75);

	@Override
    void init() {		
		world = new World();
		world.setAmbientLight(20, 20, 20);

		cube = Primitives.getCube(10);
		cube.calcTextureWrapSpherical();
		cube.setTexture(addTexture("texture", R.drawable.icon, 64, 64));
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

	@Override
	void move(float dx, float dy) {
		if (dx != 0) {
			cube.rotateY(dx/-100f);
		}

		if (dy != 0) {
			cube.rotateX(dy/-100f);
		}
	}
}

