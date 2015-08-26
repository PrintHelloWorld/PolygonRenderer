package render;

import java.awt.Color;
import java.awt.image.BufferedImage;

import model.Mesh;
import model.Polygon;
import util.Vector3D;

public class FlatShader extends Shader {

	public FlatShader(int imageWidth, int imageHeight, Mesh mesh) {
		super(imageWidth, imageHeight, mesh);
	}

	public BufferedImage createImage() {
		// initialise bitmap and zbuffer to gray, infinite depth
		Color[][] bitmap = new Color[imageWidth][imageHeight];
		float[][] zBuffer = new float[imageWidth][imageHeight];
		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				bitmap[x][y] = Color.LIGHT_GRAY;
				zBuffer[x][y] = Float.POSITIVE_INFINITY;
			}
		}

		for (Polygon p : mesh.getPolygons()) {
			// don't draw polygons facing away
			if (p.isHidden())
				continue;

			// get the edge lists for this polygon
			EdgeListEntry[] el = computeEdgeLists(p);
			Color shade = computeShading(p);

			// iterate y of the polygons edgelist
			for (int y = 0; y < imageHeight; y++) {
				if (!el[y].isVisited())
					continue;
				EdgeListEntry e = el[y];

				float deltaX = e.getRightX() - e.getLeftX();
				float deltaZ = e.getRightZ() - e.getLeftZ();
				float zGradient = deltaZ / deltaX;
				float z = e.getLeftZ();

				int startX = Math.round(e.getLeftX());
				int endX = Math.round(e.getRightX());

				// populate bitmap && zBuffer
				for (int x = startX; x < endX; x++) {
					if (x >= 0 && x < imageWidth && z < zBuffer[x][y]) {
						bitmap[x][y] = shade;
						zBuffer[x][y] = z;
					}
					z += zGradient;
				}
			}
		}

		return convertBitmapToImage(bitmap);
	}

	private EdgeListEntry[] computeEdgeLists(Polygon p) {
		EdgeListEntry[] edgeList = new EdgeListEntry[imageHeight];
		for (int i = 0; i < imageHeight; i++) {
			edgeList[i] = new EdgeListEntry();
		}

		// checks each pair of vertices and interpolates between them
		for (int v = 0; v < 3; v++) {
			Vector3D va = p.getVertex(v);
			Vector3D vb = p.getVertex((v + 1) % 3);

			if (va.y > vb.y) {
				Vector3D temp = va;
				va = vb;
				vb = temp;
			}
			float deltaY = vb.y - va.y;
			float deltaX = vb.x - va.x;
			float deltaZ = vb.z - va.z;
			float xGradient = deltaX / deltaY;
			float zGradient = deltaZ / deltaY;
			float x = va.x;
			float z = va.z;

			// main loop, generates edge lists
			int endY = Math.round(vb.y);
			int startY = Math.round(va.y);
			for (int i = startY; i < endY; i++) {
				// i is between the top and bottom of the image
				if (i >= 0 && i < imageHeight) {
					// if x < left set left to x
					if (x < edgeList[i].getLeftX()) {
						edgeList[i].setLeft(x, z);
					}
					// if x > right set right to x
					if (x > edgeList[i].getRightX()) {
						edgeList[i].setRight(x, z);
					}
				}
				// increment variables
				x += xGradient;
				z += zGradient;
			}
		}
		return edgeList;
	}

	private Color computeShading(Polygon p) {
		float r = 0, g = 0, b = 0;
		for (Vector3D l : mesh.getLights()) {
			r += calculateLightR(p.getNormal(), l);
			g += calculateLightG(p.getNormal(), l);
			b += calculateLightB(p.getNormal(), l);
		}
		r = returnCheckedColour((ambientR() + r) * p.r());
		g = returnCheckedColour((ambientG() + g) * p.g());
		b = returnCheckedColour((ambientB() + b) * p.b());

		return new Color(r, g, b);
	}
}
