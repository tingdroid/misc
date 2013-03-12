package com.ting.tingjpct4;

import java.lang.reflect.Field;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Logger;
import com.threed.jpct.Object3D;
import com.threed.jpct.util.MemoryHelper;
import com.ting.common.SceneHelper;
import com.ting.scene.Scene;

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

	protected Scene scene = null;

	private float xpos = -1;
	private float ypos = -1;

	private int fps = 0;

	protected void onCreate(Bundle savedInstanceState) {

		Logger.log("onCreate");

		SceneHelper.init(getResources(), R.drawable.class);
		
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

	private void save() {
		if (master == null) {
			Logger.log("Saving master Activity!");
			master = this;
		}
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
			scene.move(me.getX() - xpos, me.getY() - ypos);

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

	// Overrides

	private Object3D cube1 = null;

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
				scene = new Scene();

				MemoryHelper.compact();

				save();
			}
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		}

		public void onDrawFrame(GL10 gl) {
			// draw();
			if (xpos < 0)
				scene.move(1, 1);  // animate

			fb.clear(scene.background);
			try {
				scene.world.renderScene(fb);
			} catch (Exception e) {
				Logger.log(e);
			}
			scene.world.draw(fb);
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
