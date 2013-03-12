package com.ting.common;

import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;

public class SceneHelper {
	public static String addTexture(String name) {
		String shortName = name.contains(".") ? 
				name.substring(0, name.indexOf(".") - 1) : name;

		TextureManager textureManager = TextureManager.getInstance();
		if (!textureManager.containsTexture(shortName)) {
			Texture texture = new Texture(name);
			textureManager.addTexture(shortName, texture);
		}

		return shortName;
	}


}
