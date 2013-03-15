package com.ting.common;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.threed.jpct.Logger;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.util.BitmapHelper;

public class SceneHelper {

	public static Resources mResources;
	public static String mPackage;

	public static Resources getResources() {
		return mResources;
	}
	public static void init(Resources resources, String resourcePackage) {
		mResources = resources;
		mPackage = resourcePackage;
	}
	
	public static String addTexture(String name, int width, int height) {
        // Create a texture out of the named icon...:-)
		String shortName = name.contains(".") ? 
				name.substring(0, name.indexOf(".")) : name;
		int index = mResources.getIdentifier(shortName, "drawable", mPackage);
		return addTexture(shortName, index, width, height);
	}

	public static String addTexture(String name) {
		return addTexture(name, 0, 0);
	}
	
	// hidden specifics

	private static String addTexture(String name, int index, int width, int height) {
        // Create a texture out of resource icon...:-)
		TextureManager textureManager = TextureManager.getInstance();
		if (!textureManager.containsTexture(name)) {
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

	private static String addTexture(String name, String fileName, int width, int height) {
        // Create a texture out of asset file...:-)
		TextureManager textureManager = TextureManager.getInstance();
		if (!textureManager.containsTexture(name)) {
			Texture texture;
			try {
				InputStream istream = getResources().getAssets().open(fileName);
				texture = new Texture(istream);
			} catch (IOException e) {
				Logger.log(e);
				return null;
			}
			if (width != 0 && height != 0) {
				// bitmap = BitmapHelper.rescale(bitmap, width, height);
			}
			textureManager.addTexture(name, texture);
		}
		return name;
	}

}
