package com.ting.common;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.threed.jpct.Logger;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.util.BitmapHelper;
import com.ting.tingjpct1.R;

public class SceneHelper {

	public static Resources mResources;
	public static Resources getResources() {
		return mResources;
	}
	public static void setResources(Resources resources) {
		mResources = resources;
	}
	
	public static String addTexture(String name, int index, int width, int height) {
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

	public static String addTexture(String name, int width, int height) {
        // Create a texture out of the named icon...:-)
		int index;
		try {
			String shortName = name.contains(".") ? 
					name.substring(0, name.indexOf(".")) : name;
			index = R.drawable.class.getField(shortName).getInt(null);
		} catch (Exception e) {
			Logger.log(e);
			return null;
		}
		return addTexture(name, index, width, height);
	}

	public static String addTexture(String name) {
		return addTexture(name, 0, 0);
	}
}
