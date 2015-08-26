package render;

import java.awt.Color;
import java.awt.image.BufferedImage;

import model.Mesh;
import model.Polygon;
import util.Vector3D;

public class GouraudShader extends Shader {
	public GouraudShader(int imageWidth, int imageHeight, Mesh mesh) {
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

		// loop polygons
		for (Polygon p : mesh.getPolygons()) {
			if (p.isHidden())
				continue;

			EdgeListNormalEntry[] el = computeEdgeLists(p);

			// iterate edgelist
			for (int y = 0; y < imageHeight; y++) {
				if (!el[y].isVisited())
					continue;

				EdgeListNormalEntry e = el[y];

				float deltaX = e.getRightX() - e.getLeftX();
				float deltaZ = e.getRightZ() - e.getLeftZ();
				float zGradient = deltaZ / deltaX;
				float z = e.getLeftZ();

				Vector3D deltaNorm = e.getRightNorm().minus(e.getLeftNorm());
				Vector3D gradientNorm = deltaNorm.divideBy(deltaX);
				Vector3D norm = e.getLeftNorm();

				int startX = Math.round(e.getLeftX());
				int endx = Math.round(e.getRightX());
				for (int x = startX; x < endx; x++) {
					if (x >= 0 && x < imageWidth && z < zBuffer[x][y]) {
						bitmap[x][y] = computePixelShading(norm, p);
						zBuffer[x][y] = z;
					}
					z += zGradient;
					norm = norm.plus(gradientNorm);
				}
			}
		}
		return convertBitmapToImage(bitmap);
	}

	private EdgeListNormalEntry[] computeEdgeLists(Polygon p) {
		// initialise edgelist array
		EdgeListNormalEntry[] edgeList = new EdgeListNormalEntry[imageHeight];
		for (int i = 0; i < imageHeight; i++) {
			edgeList[i] = new EdgeListNormalEntry();
		}

		// checks each pair of vertices and interpolates between them
		for (int v = 0; v < 3; v++) {
			Vector3D va = p.getVertex(v);
			Vector3D vb = p.getVertex((v + 1) % 3);

			//find smallest y of the two vectors
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

			Vector3D aNorm = vertexNormal(va); //first vectors norm
			Vector3D bNorm = vertexNormal(vb); //second vectors norm
			Vector3D deltaNorm = bNorm.minus(aNorm);
			Vector3D normGradient = deltaNorm.divideBy(deltaY);
			Vector3D norm = aNorm;

			// generates edge lists
			int endY = Math.round(vb.y);
			int startY = Math.round(va.y);
			for (int i = startY; i < endY; i++) {
				if (i >= 0 && i < imageHeight) {
					// i is between the top and bottom of the image
					if (x < edgeList[i].getLeftX()) {
						edgeList[i].setLeft(x, z, norm);
					}
					// if x > right set right to x
					if (x > edgeList[i].getRightX()) {
						edgeList[i].setRight(x, z, norm);
					}
				}
				x += xGradient;
				z += zGradient;
				norm = norm.plus(normGradient);
			}
		}
		return edgeList;
	}

	private Color computePixelShading(Vector3D norm, Polygon p) {
		float r = 0, g = 0, b = 0;
		for (Vector3D l : mesh.getLights()) {
			r += calculateLightR(norm, l);
			g += calculateLightG(norm, l);
			b += calculateLightB(norm, l);
		}
		r = returnCheckedColour((ambientR() + r) * p.r());
		g = returnCheckedColour((ambientG() + g) * p.g());
		b = returnCheckedColour((ambientB() + b) * p.b());

		return new Color(r, g, b);
	}
}
