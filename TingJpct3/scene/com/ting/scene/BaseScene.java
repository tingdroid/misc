package com.ting.scene;

import com.threed.jpct.Object3D;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;

public class BaseScene {
	public World world = new World();

	interface AXIS {
		public static final SimpleVector X = v3(1,0,0);
		public static final SimpleVector Y = v3(0,1,0);
		public static final SimpleVector Z = v3(0,0,1);
	}

	public static SimpleVector v3(float x, float y, float z) {
		return SimpleVector.create(x, y, z);
	}

	void add(Object3D obj, Object3D parent) {
		if (parent != null) parent.addChild(obj);
		obj.strip();
		obj.build();
		world.addObject(obj);		
	}

	String colorTexture(String name) {
        // Create a texture out of the color
		if (!TextureManager.getInstance().containsTexture(name)) {
			int r = Integer.valueOf(name.substring(1,3), 16);
			int g = Integer.valueOf(name.substring(3,5), 16);
			int b = Integer.valueOf(name.substring(5,7), 16);
			RGBColor color = new RGBColor(r, g, b);
			Texture texture = new Texture(8, 8, color);
			TextureManager.getInstance().addTexture(name, texture);			
		}
		return name;
	}

}
