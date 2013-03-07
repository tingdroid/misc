import java.awt.Color;

import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;

public class HelloScene {
	World world;
	Color background = Color.BLUE;

	Object3D box;

	public HelloScene() {
		world = new World();
		world.setAmbientLight(0, 255, 0);

		TextureManager.getInstance().addTexture("box", new Texture("box.jpg"));

		box = Primitives.getBox(13f, 2f);
		box.setTexture("box");
		box.setEnvmapped(Object3D.ENVMAP_ENABLED);
		box.build();
		world.addObject(box);

		world.getCamera().setPosition(50, -50, -5);
		world.getCamera().lookAt(box.getTransformedCenter());
	}
	
	public void loop() {
		box.rotateY(0.01f);
	}
}
