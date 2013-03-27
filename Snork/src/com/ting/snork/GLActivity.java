package com.ting.snork;

import java.lang.reflect.Field;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Logger;
import com.threed.jpct.util.MemoryHelper;
import com.ting.scene.Pointer;
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
	private Pointer pointer = new Pointer();

    private GestureDetector mGestureDetector;

	private int fps = 0;

	protected void onCreate(Bundle savedInstanceState) {

		Logger.log("onCreate");

		Scene.init(getResources(), getPackageName());

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
		
        mGestureDetector = new GestureDetector(this, mGestureListener);
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

	private void copy(Object src) {
		try {
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

	private final GestureDetector.SimpleOnGestureListener mGestureListener = 
		new GestureDetector.SimpleOnGestureListener() {

		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			//pointer.moveBy(-distanceX, -distanceY);
			Logger.log(String.format("onScroll %s %s", distanceX, distanceY));
			return true;
		}

		public boolean onSingleTapUp(MotionEvent e) {
			Logger.log("onSingleTapUp");
			return super.onSingleTapUp(e);
		}

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Logger.log("onFling");
			return super.onFling(e1, e2, velocityX, velocityY);
		}

		public boolean onDown(MotionEvent e) {
			Logger.log("onDown");
			return super.onDown(e);
		}
	};

	public boolean onTouchEvent(MotionEvent me) {

        //boolean retVal = mScaleGestureDetector.onTouchEvent(event);
		boolean retVal = mGestureDetector.onTouchEvent(me); // || retVal;
				
		final int action = me.getActionMasked();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			pointer.down(me.getX(), me.getY());
			return true;
		case MotionEvent.ACTION_UP:
			pointer.up();
			return true;
		case MotionEvent.ACTION_MOVE:
			pointer.move(me.getX(), me.getY());
			return true;
		}

		try {
			Thread.sleep(15);
		} catch (Exception e) {
			// No need for this...
		}

        return retVal || super.onTouchEvent(me);
	}

	protected boolean isFullscreenOpaque() {
		return true;
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
				scene = new Scene();

				MemoryHelper.compact();

				save();
			}
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		}

		public void onDrawFrame(GL10 gl) {
			if (pointer.isDown()) {
				scene.move(pointer.getDX(), pointer.getDY());
			} else {
				scene.loop();
			}
			scene.hud.setText(0, "Position: %s %s", pointer.getX(),
					pointer.getY());
			scene.hud.setText(1, "Scale: %s", (int) 0);

			fb.clear(scene.background);
			try {
				scene.world.renderScene(fb);
			} catch (Exception e) {
				Logger.log(e);
			}
			scene.world.draw(fb);

			scene.hud.draw(fb);
			scene.hud.draw(fb, "Snork", -50, 28);

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
