package model;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import util.Transform;
import util.Vector3D;

public class Mesh {
	private Set<Polygon> polygons;
	// map of vectors to polygon to make gouraud shading easier
	private Map<Vector3D, Set<Polygon>> vectorToPolygons = new HashMap<Vector3D, Set<Polygon>>();
	//external light vectors - only one atm.
	private Set<Vector3D> lights = new HashSet<Vector3D>();
	//external light colour
	private Color externalLight;
	private Color ambient;

	public Mesh(Set<Polygon> polys, Vector3D lightDir, Color externalLight,
			Color amb) {
		this.setPolygons(polys);
		this.addLight(lightDir);
		this.setExternalLight(externalLight);
		this.setAmbient(amb);

		/*
		 * for each polygon take its 3 vectors and place a set of polygons at
		 * each vector
		 */
		for (Polygon p : polygons) {
			for (int i = 0; i < 3; i++) {
				vectorToPolygons.put(p.getVertex(i), new HashSet<Polygon>());
			}
		}

		/* add all polygons that share a vertex */
		for (Polygon p : polygons) {
			for (int i = 0; i < 3; i++) {
				Vector3D v1 = p.getVertex(i);
				vectorToPolygons.get(v1).add(p);
				/*
				 * for every other polygons if it shares a vertex with the first
				 * vertex add it to the set
				 */
				for (Polygon p2 : polygons) {
					for (int j = 0; j < 3; j++) {
						Vector3D v2 = p2.getVertex(j);
						if (v2.equals(v1)) {
							vectorToPolygons.get(v1).add(p2);
						}
					}
				}
			}
		}
	}

	public Rectangle getBounds() {
		int top = Integer.MAX_VALUE;
		int bottom = Integer.MIN_VALUE;
		int left = Integer.MAX_VALUE;
		int right = Integer.MIN_VALUE;
		for (Polygon p : polygons) {
			Rectangle b = p.getBounds();
			if (b.getMaxX() > right)
				right = (int) b.getMaxX();
			if (b.getMinX() < left)
				left = (int) b.getMinX();
			if (b.getMaxY() > bottom)
				bottom = (int) b.getMaxY();
			if (b.getMinY() < top)
				top = (int) b.getMinY();
		}
		return new Rectangle(left, top, right - left, bottom - top);
	}

	public void transform(Transform t, boolean applyToLight) {
		if (applyToLight) {
			transformLights(t);
		}
		transform(t);
	}

	public void transform(Transform t) {
		Map<Vector3D, Set<Polygon>> newMap = new HashMap<Vector3D, Set<Polygon>>();
		for (Polygon p : polygons) {
			Set<Polygon> p0 = vectorToPolygons.get(p.getVertex(0));
			Set<Polygon> p1 = vectorToPolygons.get(p.getVertex(1));
			Set<Polygon> p2 = vectorToPolygons.get(p.getVertex(2));
			// transform polygon
			p.transform(t);
			newMap.put(p.getVertex(0), p0);
			newMap.put(p.getVertex(1), p1);
			newMap.put(p.getVertex(2), p2);
		}
		// inialize map with new values
		vectorToPolygons = newMap;
	}

	public void transformLights(Transform t) {
		Set<Vector3D> newLights = new HashSet<Vector3D>();
		// add the new lights to the new lights set
		for (Vector3D l : lights) {
			newLights.add(t.multiply(l));
		}
		lights = newLights;
	}

	public Set<Polygon> getPolygons() {
		return polygons;
	}

	public void setPolygons(Set<Polygon> polys) {
		this.polygons = polys;
	}

	public Color getExternalLight() {
		return externalLight;
	}

	public void setExternalLight(Color lightCol) {
		this.externalLight = lightCol;
	}

	public Color getAmbient() {
		return ambient;
	}

	public void setAmbient(Color ambient) {
		this.ambient = ambient;
	}

	public Set<Vector3D> getLights() {
		return lights;
	}

	public void addLight(Vector3D light) {
		lights.add(new Vector3D(light.x, light.y, light.z));
	}

	public Set<Polygon> polygonsAdjacentTo(Vector3D v) {
		return vectorToPolygons.get(v);
	}
}