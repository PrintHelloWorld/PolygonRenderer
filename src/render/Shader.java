package render;

import java.awt.Color;
import java.awt.image.BufferedImage;

import model.Mesh;
import model.Polygon;
import util.Vector3D;

public abstract class Shader {
	protected int imageWidth;
	protected int imageHeight;
	protected Mesh mesh;

	public Shader(int imageWidth, int imageHeight, Mesh mesh) {
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.mesh = mesh;
	}

	/**
	 * Converts a 2D array of Colors to a BufferedImage. Assumes that bitmap is
	 * indexed by column then row and has imageHeight rows and imageWidth
	 * columns.
	 */
	protected BufferedImage convertBitmapToImage(Color[][] bitmap) {
		BufferedImage image = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				image.setRGB(x, y, bitmap[x][y].getRGB());
			}
		}
		return image;
	}

	// used for gouraud
	public Vector3D vertexNormal(Vector3D va) {
		Vector3D v = new Vector3D(0, 0, 0);
		for (Polygon p : mesh.polygonsAdjacentTo(va)) {
			v = v.plus(p.getNormal());
		}
		int polyCount = mesh.polygonsAdjacentTo(va).size();
		return v.divideBy(polyCount);
	}

	protected float calculateLightR(Vector3D normal, Vector3D l) {
		return Math.max(0.0f, normal.cosTheta(l) * lightR());
	}

	protected float calculateLightG(Vector3D normal, Vector3D l) {
		return Math.max(0.0f, normal.cosTheta(l) * lightG());
	}

	protected float calculateLightB(Vector3D normal, Vector3D l) {
		return Math.max(0.0f, normal.cosTheta(l) * lightB());
	}

	protected Color makeColor(float r, float g, float b) {
		r = returnCheckedColour(r);
		g = returnCheckedColour(g);
		b = returnCheckedColour(b);
		return new Color(r, g, b);
	}

	protected float returnCheckedColour(float fl) {
		if (fl < 0)
			return 0f;
		if (fl > 1)
			return 255f;
		return fl;
	}

	public float lightR() {
		return mesh.getExternalLight().getRed() / 255f;
	}

	public float lightG() {
		return mesh.getExternalLight().getGreen() / 255f;
	}

	public float lightB() {
		return mesh.getExternalLight().getBlue() / 255f;
	}

	public float ambientR() {
		return mesh.getAmbient().getRed() / 255f;
	}

	public float ambientG() {
		return mesh.getAmbient().getGreen() / 255f;
	}

	public float ambientB() {
		return mesh.getAmbient().getBlue() / 255f;
	}

	public abstract BufferedImage createImage();
}
