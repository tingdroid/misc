package com.ting.scene;

import java.util.ArrayList;
import java.util.List;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.RGBColor;

public class HUD {

	int size = 12;
	int style = GLFont.BOLD;
	int xOffset = 20;
	int yOffset = 10;
	int transparency = 10;
	RGBColor color = new RGBColor(200, 200, 200);
	RGBColor color2 = new RGBColor(255, 255, 0);

	GLFont glFont;
	List<String> strings = new ArrayList<String>();

	public GLFont getGLFont() {
		if (glFont == null) {
			glFont = GLFont.getGLFont(style, size);
		}
		return glFont;
	}

	public int addText() {
		strings.add("");
		return strings.size() - 1;
	}

	public void setText(int index, String format, Object... args) {
		String str = String.format(format, args);
		while (index + 1 > strings.size()) {
			addText();
		}
		strings.set(index, str);
	}

	public void draw(FrameBuffer buffer) {
		int x = xOffset;
		int y = yOffset;
		for (int i = 0; i < strings.size(); i++) {
			y += (float) size * 1.5f;
			String s = strings.get(i);
			if (s != null && s.length() > 0)
				draw(buffer, s, x, y);
		}
	}

	public void draw(FrameBuffer buffer, String s, int x, int y) {
		if (s == null || s.length() <= 0)
			return;
		getGLFont();
		if (x < 0) {
			x += GLFont.pd(buffer.getWidth());
		}
		
		int sep = s.indexOf(':');
		if (sep == -1) {
			glFont.blitString(buffer, s, x, y, 10, color);
		} else {
			x = glFont.blitString(buffer, s.substring(0, sep), x, y,
					transparency, color);
			glFont.blitString(buffer, s.substring(sep+1), x, y,
					transparency, color2);
		}
	}

}
