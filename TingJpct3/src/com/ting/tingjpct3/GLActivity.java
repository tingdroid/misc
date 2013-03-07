package com.ting.tingjpct3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Logger;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;
import com.ting.tingjpct3.R;

import java.lang.reflect.Field;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

/**
 * A simple demo. This shows more how to use jPCT-AE than it shows how to write
 * a proper application for Android. It includes basic activity management to
 * handle pause and resume...
 * 
 * @author EgonOlsen
 * 
 */
public class GLActivity extends Activity {

	// Used to handle pause and resume...
	private static Object master = null;

	private GLSurfaceView mGLView;
	private MyRenderer renderer = null;
	private FrameBuffer fb = null;

	protected World world = null;
	protected RGBColor back = new RGBColor(50, 50, 100);

	private float xpos = -1;
	private float ypos = -1;

	private int fps = 0;

	protected void onCreate(Bundle savedInstanceState) {

		Logger.log("onCreate");

		if (master != null) {
			copy(master);
		}

		super.onCreate(savedInstanceState);
		mGLView = new GLSurfaceView(getApplication());

		mGLView.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
			public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
				// Ensure that we get a 16bit framebuffer. Otherwise, we'll fall
				// back to Pixelflinger on some device (read: Samsung I7500)
				int[] attributes = new int[] { EGL10.EGL_DEPTH_SIZE, 16,
						EGL10.EGL_NONE };
				EGLConfig[] configs = new EGLConfig[1];
				int[] result = new int[1];
				egl.eglChooseConfig(display, attributes, configs, 1, result);
				return configs[0];
			}
		});

		renderer = new MyRenderer();
		mGLView.setRenderer(renderer);
		setContentView(mGLView);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLView.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private void save() {
		if (master == null) {
			Logger.log("Saving master Activity!");
			master = this;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	boolean toggleChecked(MenuItem item) {
		if (item.isChecked()) {
			item.setChecked(false);
			item.setIcon(R.drawable.check_0);
		} else {
			item.setChecked(true);
			item.setIcon(R.drawable.check_1);					
		}
		return item.isChecked();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle item selection
		switch (item.getItemId())
		{
			case R.id.settings:
				showSettings();
				return true;
			case R.id.about:
				alert(getResources().getString(R.string.about_msg));
				return true;
			case R.id.mute:
			    boolean muted = toggleChecked(item);
				return true;
			case R.id.alert:
			    boolean alerting = toggleChecked(item);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void showSettings()
	{
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}
	
	public void alert(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder
			.setTitle(this.getClass().getSimpleName())
		    .setMessage(message)
			.setNeutralButton("OK", null);

		builder.show();		
	}	

	private void copy(Object src) {
		try {
			if (super.getClass().getSuperclass().equals(GLActivity.class)) {
				Field[] fs = super.getClass().getSuperclass().getDeclaredFields();
				for (Field f : fs) {
					f.setAccessible(true);
					f.set(this, f.get(src));
				}

			}
			Logger.log("Copying data from master Activity!");
			Field[] fs = src.getClass().getDeclaredFields();
			for (Field f : fs) {
				f.setAccessible(true);
				f.set(this, f.get(src));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean onTouchEvent(MotionEvent me) {

		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			xpos = me.getX();
			ypos = me.getY();
			return true;
		}

		if (me.getAction() == MotionEvent.ACTION_UP) {
			xpos = -1;
			ypos = -1;
			return true;
		}

		if (me.getAction() == MotionEvent.ACTION_MOVE) {
			move(me.getX() - xpos, me.getY() - ypos);

			xpos = me.getX();
			ypos = me.getY();
			return true;
		}

		try {
			Thread.sleep(15);
		} catch (Exception e) {
			// No need for this...
		}

		return super.onTouchEvent(me);
	}

	protected boolean isFullscreenOpaque() {
		return true;
	}

    // Helpers	

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

	String addTexture(String name, int index, int width, int height) {
        // Create a texture out of the icon
		Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(index)), width, height));
		TextureManager.getInstance().addTexture(name, texture);
		return name;
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

	// Overrides

	private Object3D cube1 = null;

	void init() {
		Light sun = null;

		world = new World();
		world.setAmbientLight(20, 20, 20);

		sun = new Light(world);
		sun.setIntensity(250, 250, 250);

		cube1 = Primitives.getCube(10);
		cube1.calcTextureWrapSpherical();
		cube1.setTexture(addTexture("texture", R.drawable.icon, 64, 64));
		cube1.strip();
		cube1.build();

		world.addObject(cube1);

		Camera cam = world.getCamera();
		cam.moveCamera(Camera.CAMERA_MOVEOUT, 50);
		cam.lookAt(cube1.getTransformedCenter());

		SimpleVector sv = new SimpleVector();
		sv.set(cube1.getTransformedCenter());
		sv.y -= 100;
		sv.z -= 100;
		sun.setPosition(sv);
	}

	void move(float dx, float dy) {
		if (dx != 0) {
			cube1.rotateY(dx/-100f);
		}

		if (dy != 0) {
			cube1.rotateX(dy/-100f);
		}
	}

	class MyRenderer implements GLSurfaceView.Renderer {

		private long time = System.currentTimeMillis();

		public MyRenderer() {
		}

		public void onSurfaceChanged(GL10 gl, int w, int h) {
			if (fb != null) {
				fb.dispose();
			}
			fb = new FrameBuffer(gl, w, h);

			if (master == null) {
				init();

				MemoryHelper.compact();

				save();
			}
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		}

		public void onDrawFrame(GL10 gl) {
			// draw();

			fb.clear(back);
			try {
				world.renderScene(fb);
			} catch (Exception e) {
				Logger.log(e);
			}
			world.draw(fb);
			fb.display();

			if (System.currentTimeMillis() - time >= 1000) {
				// Logger.log(fps + "fps");
				fps = 0;
				time = System.currentTimeMillis();
			}
			fps++;
		}
	}
}