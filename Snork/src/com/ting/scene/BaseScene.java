package com.ting.scene;

import java.io.InputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.threed.jpct.Loader;
import com.threed.jpct.Logger;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureInfo;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;

public class BaseScene {

	public static Resources mResources;
	public static String mPackage;

	public World world = new World();
	public HUD hud = new HUD();

	public static Resources getResources() {
		return mResources;
	}

	public static void init(Resources resources, String resourcePackage) {
		mResources = resources;
		mPackage = resourcePackage;
	}

	public static String addTexture(String name, int width, int height) {
		// Create a texture out of the named icon...:-)
		String shortName = name.contains(".") ? name.substring(0,
				name.indexOf(".")) : name;
		// int index = mResources.getIdentifier(shortName, "drawable",
		// mPackage);
		// return addTexture(shortName, index, width, height);
		return addTexture(shortName, name, width, height);
	}

	public static String addTexture(String name) {
		return addTexture(name, 0, 0);
	}

	// hidden specifics

	private static String addTexture(String name, int index, int width,
			int height) {
		// Create a texture out of resource icon...:-)
		TextureManager textureManager = TextureManager.getInstance();
		if (!textureManager.containsTexture(name)) {
			// gets scaled image with 4/3 dimensions
			Drawable drawable = getResources().getDrawable(index);
			Bitmap bitmap = BitmapHelper.convert(drawable);
			if (width != 0 && height != 0) {
				bitmap = BitmapHelper.rescale(bitmap, width, height);
			}
			Texture texture = new Texture(bitmap);
			textureManager.addTexture(name, texture);
		}
		return name;
	}

	private static String addTexture(String name, String fileName, int width,
			int height) {
		// Create a texture out of asset file...:-)
		TextureManager textureManager = TextureManager.getInstance();
		if (!textureManager.containsTexture(name)) {
			Texture texture;
			try {
				// gets unscaled image
				InputStream istream = getResources().getAssets().open(fileName);
				Bitmap bitmap = BitmapHelper.loadImage(istream);
				if (width != 0 && height != 0) {
					bitmap = BitmapHelper.rescale(bitmap, width, height);
				}
				texture = new Texture(bitmap);
			} catch (Exception e) {
				Logger.log(e);
				return null;
			}
			textureManager.addTexture(name, texture);
		}
		return name;
	}

	public static Object3D loadOBJ(String name, float scale) {
		try {
			// gets unscaled image
			InputStream isObj = getResources().getAssets().open(name + ".obj");
			InputStream isMtl = getResources().getAssets().open(name + ".mtl");
			Object3D[] arr = Loader.loadOBJ(isObj, isMtl, scale);
			Object3D temp = arr[0];
			temp.setCenter(SimpleVector.ORIGIN);
			temp.rotateX((float) (Math.PI));
			temp.rotateMesh();
			temp.setRotationMatrix(new Matrix());
			return temp;
		} catch (Exception e) {
			Logger.log(e);
			return null;
		}
	}

	public static TextureInfo addTexture(String name, String mod, int mode) {
		TextureManager tm = TextureManager.getInstance();
		TextureInfo ti = new TextureInfo(tm.getTextureID(addTexture(name)));
		ti.add(tm.getTextureID(addTexture(mod)), mode);
		return ti;
	}

	public static Object3D loadMD2(String name, float scale) {
		try {
			InputStream is = getResources().getAssets().open(name + ".md2");
			Object3D temp = Loader.loadMD2(is, scale);
			return temp;
		} catch (Exception e) {
			Logger.log(e);
			return null;
		}
	}

	public static Object3D[] load3DS(String name, float scale) {
		try {
			InputStream is = getResources().getAssets().open(name + ".3ds");
			Object3D[] arr = Loader.load3DS(is, scale);
			return arr;
		} catch (Exception e) {
			Logger.log(e);
			return null;
		}
	}

}
