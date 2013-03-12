import org.lwjgl.opengl.Display;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.IRenderer;
import com.ting.scene.HelloScene;

/**
 * A simple HelloScene using the OpenGL-renderer.
 * @author EgonOlsen
 *
 */
public class HelloWorldOGL {

	private FrameBuffer buffer;
	private HelloScene scene;

	public static void main(String[] args) throws Exception {
		if (args.length > 0) System.out.println(args[0]);
		new HelloWorldOGL().loop();
	}

	public HelloWorldOGL() throws Exception {
		scene = new HelloScene();
	}

	private void loop() throws Exception {
		buffer = new FrameBuffer(800, 600, FrameBuffer.SAMPLINGMODE_NORMAL);
		buffer.disableRenderer(IRenderer.RENDERER_SOFTWARE);
		buffer.enableRenderer(IRenderer.RENDERER_OPENGL);

		while (!Display.isCloseRequested()) {
			scene.loop();
			buffer.clear(scene.background);
			scene.world.renderScene(buffer);
			scene.world.draw(buffer);
			buffer.update();
			buffer.displayGLOnly();
			Thread.sleep(10);
		}
		buffer.disableRenderer(IRenderer.RENDERER_OPENGL);
		buffer.dispose();
		System.exit(0);
	}
}
