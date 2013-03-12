package com.ting.common;

import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;

public class SceneHelper {

	public static String addTexture(String name, int width, int height) {
		String shortName = name.contains(".") ? 
				name.substring(0, name.indexOf(".") - 1) : name;

		TextureManager textureManager = TextureManager.getInstance();
		if (!textureManager.containsTexture(shortName)) {
			Texture texture = new Texture(name);
			if (width != 0 && height != 0) {
				// bitmap = BitmapHelper.rescale(bitmap, width, height);
			}
			textureManager.addTexture(shortName, texture);
		}

		return shortName;
	}

	public static String addTexture(String name) {
		return addTexture(name, 0, 0);
	}

}
