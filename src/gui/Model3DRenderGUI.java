package gui;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JSlider;

import model.Mesh;
import render.GouraudShader;
import util.FileParser;
import util.Transform;

public class Model3DRenderGUI extends GUI {
	private Mesh mesh;
	private final int imageWidth = 600;
	private final int imageHeight = 600;

	private float xRotation = 0;
	private float yRotation = 0;
	private float zRotation = 0;

	//y rotation of light
	private float lightRotation = 0;

	private static int rotateVal = 5;

	private boolean initialized = false;

	@Override
	protected void onLoad(File file) {
		FileParser reader = new FileParser(file);
		int[] ambient = getAmbientLight();
		int[] externalLight = getExternalLight();
		//create mesh of shape
		mesh = new Mesh(
				reader.readPolygons(),
				reader.readLightSource(),
				new Color(externalLight[0], externalLight[1], externalLight[2]),
				new Color(ambient[0], ambient[1], ambient[2]));
		initialized = true;
		// reset Sliders
		reset();
	}

	@Override
	protected void onKeyPress(KeyEvent ev) {
		if (ev.getKeyCode() == KeyEvent.VK_RIGHT) {
			addxAxis(rotateVal);
			rotateY(getxAxis());
		} else if (ev.getKeyCode() == KeyEvent.VK_LEFT) {
			addxAxis(-rotateVal);
			rotateY(getxAxis());
		} else if (ev.getKeyCode() == KeyEvent.VK_UP) {
			addyAxis(rotateVal);
			rotateX(getyAxis());
		} else if (ev.getKeyCode() == KeyEvent.VK_DOWN) {
			addyAxis(-rotateVal);
			rotateX(getyAxis());
		}
	}

	@Override
	protected void rotateX(JSlider source) {
		float val = -source.getValue() / 100.0f; // -3.14 - 3.14
		float toRotate = val - xRotation;
		mesh.transform(Transform.newXRotation(toRotate), true);
		xRotation = val;
	}

	@Override
	protected void rotateY(JSlider source) {
		float val = -source.getValue() / 100.0f;
		float toRotate = val - yRotation;
		mesh.transform(Transform.newYRotation(toRotate), true);
		yRotation = val;
	}

	@Override
	protected void rotateZ(JSlider source) {
		float val = source.getValue() / 100.0f;
		float toRotate = val - zRotation;
		mesh.transform(Transform.newZRotation(toRotate), true);
		zRotation = val;
	}

	protected void setLightDirection(JSlider source) {
		float val = -source.getValue() / 100.0f;
		mesh.transformLights(Transform.newYRotation(val - lightRotation));
		lightRotation = val;
	}

	@Override
	protected BufferedImage render() {
		if (initialized) {
			adjustView();
			int[] ambient = getAmbientLight();
			int[] externalLight = getExternalLight();
			mesh.setAmbient(new Color(ambient[0], ambient[1], ambient[2]));
			mesh.setExternalLight(new Color(externalLight[0], externalLight[1],
					externalLight[2]));
			BufferedImage image = new GouraudShader(imageWidth, imageHeight,
					mesh).createImage();
			redraw();
			return image;
		} else {
			return null;
		}
	}

	private void adjustView() {
		Rectangle bounds;
		// scale mesh
		bounds = mesh.getBounds();
		float sX = imageWidth / (float) (bounds.getWidth());
		float sY = imageHeight / (float) (bounds.getHeight());
		float s = Math.min(sX, sY);
		Transform scale = Transform.newScale(s, s, s);
		mesh.transform(scale);

		// translate to top left
		bounds = mesh.getBounds();
		float tX = (float) -bounds.getX();
		float tY = (float) -bounds.getY();
		Transform translate = Transform.newTranslation(tX, tY, 0);
		mesh.transform(translate);
	}

	private void reset() {
		xRotation = 0;
		yRotation = 0;
		zRotation = 0;
		lightRotation = 0;
		resetSliders();
	}

	public static void main(String[] args) {
		new Model3DRenderGUI();
	}
}
