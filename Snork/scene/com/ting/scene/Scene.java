package com.ting.scene;

import com.threed.jpct.Camera;
import com.threed.jpct.Config;
import com.threed.jpct.GenericVertexController;
import com.threed.jpct.Light;
import com.threed.jpct.Mesh;
import com.threed.jpct.Object3D;
import com.threed.jpct.PolygonManager;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.TextureInfo;

public class Scene extends BaseScene {
	public RGBColor background = RGBColor.BLUE;
	public RGBColor ambient = new RGBColor(0, 255, 0);

	Object3D center;
	Object3D plane;
	Object3D rock;
	Object3D snork;
	Object3D dome;
	SimpleVector lightOffset = new SimpleVector(-100, -100, -75);

	public Scene() {
		Config.glAvoidTextureCopies = true;
		Config.maxPolysVisible = 1000;
		Config.glColorDepth = 24;
		Config.glFullscreen = false;
		Config.farPlane = 4000;
		Config.glShadowZBias = 0.8f;
		Config.lightMul = 1;
		Config.collideOffset = 500;
		Config.glTrilinear = true;
		
		// world.setAmbientLight(ambient.getRed(), ambient.getGreen(),
		// ambient.getBlue());

		center = Primitives.getSphere(1);
		add(center, null);

		plane = Primitives.getPlane(20, 30);
		plane.rotateX((float) Math.PI / 2f);
		plane.setSpecularLighting(true);
		plane.setTexture(addTexture("GrassSample2.jpg"));
		plane.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);

		// Deform the plane
		Mesh planeMesh = plane.getMesh();
		planeMesh.setVertexController(new Mod(), false);
		planeMesh.applyVertexController();
		planeMesh.removeVertexController();

		add(plane, center);

		rock = load3DS("rock", 15f)[0];
		rock.build();
		rock.translate(0, -5, -90);
		rock.rotateX(-(float) Math.PI / 2);
		rock.setTexture(addTexture("rock.jpg", "normals.jpg", TextureInfo.MODE_MODULATE));
		rock.setSpecularLighting(true);
		add(rock, center);

		snork = loadMD2("snork", 1);
		snork.translate(0, -35, 0);
		snork.setTexture(addTexture("disco.jpg"));
		add(snork, center);

		dome = Object3D.mergeAll(load3DS("dome", 2));
		dome.build();
		dome.rotateX(-(float)Math.PI / 2f);
		dome.setTexture(addTexture("sky.jpg"));
		dome.calcTextureWrap();
		tileTexture(dome, 3);
		dome.translate(center.getTransformedCenter().calcSub(dome.getTransformedCenter()));
		dome.setLighting(Object3D.LIGHTING_NO_LIGHTS);
		dome.setAdditionalColor(RGBColor.WHITE);
		add(dome, center);
		
		Camera cam = world.getCamera();
		cam.setPosition(100, -50, -20);
		//cam.moveCamera(Camera.CAMERA_MOVEOUT, 150);
		//cam.moveCamera(Camera.CAMERA_MOVEUP, 100);
		cam.lookAt(snork.getTransformedCenter());
		cam.setFOV(1.5f);		
		
		Light light = new Light(world);
		light.setIntensity(250, 250, 250);
		light.setPosition(snork.getTransformedCenter().calcAdd(lightOffset));
	}

	// Navigation

	public void loop() {
		center.rotateY(0.002f);
	}

	public void move(float dx, float dy) {
		if (dx != 0) {
			center.rotateY(dx / -100f);
		}
		if (dy != 0) {
			center.rotateX(dy / -100f);
		}
	}

	// Helper Methods

	void add(Object3D obj, Object3D parent) {
		if (parent != null)
			parent.addChild(obj);
		obj.strip();
		obj.build();
		world.addObject(obj);
	}

	private void tileTexture(Object3D obj, float tileFactor) {
		PolygonManager pm = obj.getPolygonManager();

		int end = pm.getMaxPolygonID();
		for (int i = 0; i < end; i++) {
			SimpleVector uv0 = pm.getTextureUV(i, 0);
			SimpleVector uv1 = pm.getTextureUV(i, 1);
			SimpleVector uv2 = pm.getTextureUV(i, 2);

			uv0.scalarMul(tileFactor);
			uv1.scalarMul(tileFactor);
			uv2.scalarMul(tileFactor);

			int id = pm.getPolygonTexture(i);

			TextureInfo ti = new TextureInfo(id, uv0.x, uv0.y, uv1.x, uv1.y,
					uv2.x, uv2.y);
			pm.setPolygonTexture(i, ti);
		}
	}
	
	private static class Mod extends GenericVertexController {
		private static final long serialVersionUID = 1L;

		public void apply() {
			SimpleVector[] s = getSourceMesh();
			SimpleVector[] d = getDestinationMesh();
			for (int i = 0; i < s.length; i++) {
				d[i].z = s[i].z
						- (10f * ((float) Math.sin(s[i].x / 50f) + (float) Math
								.cos(s[i].y / 50f)));
				d[i].x = s[i].x;
				d[i].y = s[i].y;
			}
		}
	}

}
