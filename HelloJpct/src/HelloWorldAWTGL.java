import com.threed.jpct.*;
import com.ting.scene.HelloScene;

import javax.swing.*;
import java.awt.*;

/**
 * A simple HelloWorld using the AWTGL-Renderer and rendering into a frame.
 * @author EgonOlsen
 * 
 */
public class HelloWorldAWTGL {

	private JFrame frame;
	private FrameBuffer buffer;
	private HelloScene scene;

	public static void main(String[] args) throws Exception {
		new HelloWorldAWTGL().loop();
	}

	public HelloWorldAWTGL() throws Exception {
		
		frame=new JFrame("Hello world");
		frame.setSize(800, 600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		scene = new HelloScene();
	}

	private void loop() throws Exception {
		buffer = new FrameBuffer(800, 600, FrameBuffer.SAMPLINGMODE_NORMAL);
		Canvas canvas=buffer.enableGLCanvasRenderer();
		buffer.disableRenderer(IRenderer.RENDERER_SOFTWARE);
		frame.add(canvas);

		while (frame.isShowing()) {
			scene.loop();
			buffer.clear(scene.background);
			scene.world.renderScene(buffer);
			scene.world.draw(buffer);
			buffer.update();
			buffer.displayGLOnly();
			canvas.repaint();
			Thread.sleep(10);
		}
		buffer.disableRenderer(IRenderer.RENDERER_OPENGL);
		buffer.dispose();
		frame.dispose();
		System.exit(0);
	}
}
