package model;

import java.awt.Color;
import java.awt.Rectangle;

import util.Transform;
import util.Vector3D;

public class Polygon {
	private Vector3D[] vertices = new Vector3D[3];
	private Vector3D normal;
	private Color reflectance;

	public Polygon(Vector3D[] vertices, Color reflectance) {
		setVertex(0, vertices[0]);
		setVertex(1, vertices[1]);
		setVertex(2, vertices[2]);
		setReflectance(reflectance);
		calculateNormal();
	}

	private void calculateNormal() {
		setNormal(((vertices[1].minus(vertices[0])).crossProduct(vertices[2]
				.minus(vertices[1]))).unitVector());
	}

	public void transform(Transform t) {
		for (int i = 0; i < 3; i++) {
			vertices[i] = t.multiply(vertices[i]);
		}
		calculateNormal();
	}

	public Rectangle getBounds() {
		int left = Integer.MAX_VALUE;
		int right = Integer.MIN_VALUE;
		int top = Integer.MAX_VALUE;
		int bottom = Integer.MIN_VALUE;

		for (int i = 0; i < 3; i++) {
			left = Math.min(left, (int) vertices[i].x);
			right = Math.max(right, (int) vertices[i].x);
			top = Math.min(top, (int) vertices[i].y);
			bottom = Math.max(bottom, (int) vertices[i].y);
		}
		return new Rectangle(left, top, right - left, bottom - top);
	}

	public Vector3D getVertex(int index) {
		return vertices[index];
	}

	public void setVertex(int index, Vector3D vertex) {
		vertices[index] = vertex;
	}

	public float r() {
		return getReflectance().getRed() / 255f;
	}

	public float g() {
		return getReflectance().getGreen() / 255f;
	}

	public float b() {
		return getReflectance().getBlue() / 255f;
	}

	public Color getReflectance() {
		return reflectance;
	}

	public void setReflectance(Color reflectance) {
		this.reflectance = reflectance;
	}

	public boolean isHidden() {
		return getNormal().z > 0;
	}

	public float miny() {
		return Math.min(vertices[0].y, Math.min(vertices[1].y, vertices[2].y));
	}

	public float maxy() {
		return Math.max(vertices[0].y, Math.max(vertices[1].y, vertices[2].y));
	}

	public Vector3D getNormal() {
		return normal;
	}

	public void setNormal(Vector3D normal) {
		this.normal = normal;
	}

	@Override
	public String toString() {
		StringBuilder ans = new StringBuilder("Vect:");
		for (int i = 0; i < 3; i++) {
			ans.append(vertices[i].toString());
		}
		String cont = "Norm:" + normal.toString() + " Reflect:"
				+ reflectance.toString();
		return ans + " " + cont;
	}
}