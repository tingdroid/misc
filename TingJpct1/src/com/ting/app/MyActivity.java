package com.ting.app;

import com.threed.jpct.Camera;
import com.threed.jpct.Light;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;

/**
 * A simple demo. 
 * 
 */
public class MyActivity extends GLActivity {

//	private World world = null;

	private Object3D cube = null;

	@Override
    void init() {
		Light sun = null;
		
		world = new World();
		world.setAmbientLight(20, 20, 20);

		sun = new Light(world);
		sun.setIntensity(250, 250, 250);

		// Create a texture out of the icon...:-)
		Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.icon)), 64, 64));
		TextureManager.getInstance().addTexture("texture", texture);

		cube = Primitives.getCube(10);
		cube.calcTextureWrapSpherical();
		cube.setTexture("texture");
		cube.strip();
		cube.build();

		world.addObject(cube);

		Camera cam = world.getCamera();
		cam.moveCamera(Camera.CAMERA_MOVEOUT, 50);
		cam.lookAt(cube.getTransformedCenter());

		SimpleVector sv = new SimpleVector();
		sv.set(cube.getTransformedCenter());
		sv.y -= 100;
		sv.z -= 100;
		sun.setPosition(sv);
    }

	@Override
    void draw() {
		if (shiftX != 0) {
			cube.rotateY(shiftX);
			shiftX = 0;
		}

		if (shiftY != 0) {
			cube.rotateX(shiftY);
			shiftY = 0;
		}
    }

}
