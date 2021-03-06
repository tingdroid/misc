package com.ting.scene;

import java.lang.reflect.Method;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Point;
import android.graphics.Typeface;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Logger;
import com.threed.jpct.RGBColor;

/**
 * <p>creates GL renderable (blittable) font out of given AWT font. 
 * a jPCT texture is created and added to TextureManager on the fly.</p>
 *  
 * <p>in contrast with its name, this class can be used for software renderer too.
 * but to tell the truth, i would stick to Java2D for software renderer ;)</p>    
 * 
 * this class uses {@link TexturePack} behind the scenes.
 * 
 * @see TexturePack 
 * 
 * @author hakan eryargi (r a f t)
 */
public class GLFont {
	public static final int PLAIN = 0;
	public static final int BOLD = 1;
	public static final int ITALIC = 2;	
	public static final int BOLD_ITALIC = 1;

	/** standard characters */
	public static final String ENGLISH = " abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ`1234567890-=~!@#$%^&*()_+[]{}\\|:;\"'<>,.?/";
	
	/** German specific characters */
	public static final String GERMAN = new String(new char[] { 
			'\u00c4', '\u00D6', '\u00DC', '\u00E4', '\u00F6', '\u00FC', '\u00DF' });
	
	/** French specific characters */
	public static final String FRENCH = new String(new char[] {
			'\u00C0', '\u00C2', '\u00C6', '\u00C8', '\u00C9', '\u00CA', '\u00CB', 
			'\u00CE', '\u00CF', '\u00D4', '\u0152', '\u00D9', '\u00DB', '\u00DC', 
			'\u0178', '\u00C7', '\u00E0', '\u00E2', '\u00E6', '\u00E8', '\u00E9', 
			'\u00EA', '\u00EB', '\u00EE', '\u00EF', '\u00F4', '\u0153', '\u00F9', 
			'\u00FB', '\u00FC', '\u00FF', '\u00E7' });
	
	/** Turkish specific characters */
	public static final String TURKISH = new String(new char[] { 
		    '\u00e7', '\u00c7', '\u011f', '\u011e', '\u0131', '\u0130',  
		    '\u00f6', '\u00d6', '\u015f', '\u015e', '\u00fc', '\u00dc' });

	public static float density = 2f;
	public static int dp(int units) {
		return (int)(units * density);
	}		
	public static int pd(int pixels) {
		return (int)(pixels / density);
	}
		
	/** using platform-neutral Font parameters 
	 * @author olegyk 
	 */
	public static GLFont getGLFont(String face, int style, int size) {
		/*
		Context context = getApplication();
		WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);

		DisplayMetrics metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);	
		density = metrics.density;
		*/

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTypeface(Typeface.create(face, style));
		paint.setTextSize(dp(size));
		return new GLFont(paint);
	}

	/** using platform-neutral Font parameters and default face 
	 * @author olegyk 
	 */
	public static GLFont getGLFont(int style, int size) {
		return getGLFont((String)null, style, size);
	}	
	
	/** characters this GLFont is created for */
	public final String alphabet;
	/** regular font height. note some special characters may not fit into this height.
	 * see {@link FontMetrics} for a discussion */
	public final int fontHeight;
	private final int baseline;
	
	private final int[] charWidths;

	public final TexturePack pack = new TexturePack();

	/** 
	 * creates a GLFont for given awt Font consists of default characters.
	 * @see #ENGLISH 
	 */
	public GLFont(Paint paint) {
		this(paint, ENGLISH);
	}
	
	/** 
	 * creates a GLFont for given awt Font consists of characters in given alphabet 
	 * @param typeFace the awt font 
	 * @param alphabet characters of our alphabet 
	 */
	public GLFont(Paint paint, String alphabet) {
		this.alphabet = eliminateDuplicates(alphabet);
		this.charWidths = new int[alphabet.length()];

		Bitmap.Config config = Bitmap.Config.ARGB_8888; 

		paint = new Paint(paint); 
		paint.setColor(Color.WHITE);
		
		FontMetricsInt fontMetrics = paint.getFontMetricsInt();
		
		this.fontHeight = fontMetrics.leading - fontMetrics.ascent + fontMetrics.descent;
		this.baseline = -fontMetrics.top;
		int height = fontMetrics.bottom - fontMetrics.top;

		for (int i = 0; i < alphabet.length(); i++) {
			String c = alphabet.substring(i, i + 1);
			int width = (int)paint.measureText(c);
			charWidths[i] = width;

			Bitmap charImage = Bitmap.createBitmap(width, height, config);
			Canvas canvas = new Canvas(charImage);

			canvas.drawText(c, 0, baseline, paint);
			
			pack.addImage(charImage);
		}
		pack.pack(TexturePack.ALPHA_USE);
	}

	private String eliminateDuplicates(String s) {
		StringBuilder sb = new StringBuilder(s);

		for (int i = 0; i < sb.length(); i++) {
			String c = sb.substring(i, i + 1);
			int next = -1;
			while ((next = sb.indexOf(c, i + 1)) != -1) {
				sb.deleteCharAt(next);
			}
		}
		return sb.toString();
	}

	/**
	 * returns how much area given string occupies. 
	 */
	public Rectangle getStringBounds(String s, Rectangle store) {
		if (store == null)
			store = new Rectangle();
		
		int width = 0;

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			int index = alphabet.indexOf(c);
			if (index == -1)
				index = alphabet.indexOf('?');
			if (index != -1) {
				width += charWidths[index];
			}
		}
		store.width = width;
		store.height = fontHeight;
		return store;
	}

	/**
	 * returns how much area given string occupies. 
	 */
	public Rectangle getStringBounds(char[] s, Rectangle store) {
		if (store == null)
			store = new Rectangle();
		
		int width = 0;

		for (int i = 0; i < s.length; i++) {
			char c = s[i];
			int index = alphabet.indexOf(c);
			if (index == -1)
				index = alphabet.indexOf('?');
			if (index != -1) {
				width += charWidths[index];
			}
		}
		store.width = width;
		store.height = fontHeight;
		return store;
	}
	
	/**
	 * blits given string to frame buffer. works very similar to
	 * awt.Graphics#drawString(..) that is: x coordinate is left most point in
	 * string, y is baseline
	 * 
	 * @param buffer
	 *            buffer to blit into
	 * @param s
	 *            string to blit
	 * @param x
	 *            leftmost point
	 * @param transparency
	 *            transparency value, make sure >= 0
	 * @param color
	 *            text color
	 * @param y
	 *            baseline
	 * @return    next x offset. Useful for chaining calls.
	 */
	public int blitString(FrameBuffer buffer, String s, int x, int y, int transparency, RGBColor color) {
		y = dp(y) - baseline;
		x = dp(x);

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			int index = alphabet.indexOf(c);
			if (index == -1)
				index = alphabet.indexOf('?');
			if (index != -1) {
				Point size = pack.blit(buffer, index, x, y, transparency, false, color);
				x += size.x;
			}
		}
		return pd(x);
	}
	
	public int blitString(FrameBuffer buffer, char[] s, int x, int y, int transparency, RGBColor color) {
		y = dp(y) - baseline;
		x = dp(x);

		for (int i = 0; i < s.length; i++) {
			char c = s[i];
			int index = alphabet.indexOf(c);
			if (index == -1)
				index = alphabet.indexOf('?');
			if (index != -1) {
				Point size = pack.blit(buffer, index, x, y, transparency, false, color);
				x += size.x;
			}
		}
		return pd(x);
	}
	
	public int getCharImageId(char c) {
		return alphabet.indexOf(c);
	}

	private static Application getApplication() {
		try {
		    final Class<?> activityThreadClass =
		            Class.forName("android.app.ActivityThread");
		    final Method method = activityThreadClass.getMethod("currentApplication");
		    Application app = (Application) method.invoke(null, (Object[]) null);
		    return app;
		} catch (final Exception e) {
			Logger.log(e);
		}
		return null;
	}
	
}
