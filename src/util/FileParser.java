package util;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import model.Polygon;

public class FileParser {
	private File file;

	public FileParser(File file) {
		this.file = file;
	}

	public Vector3D readLightSource() {
		Vector3D lightSource = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String[] vector = reader.readLine().split(" ");

			// convert strings to floats
			float x = Float.parseFloat(vector[0]);
			float y = Float.parseFloat(vector[1]);
			float z = Float.parseFloat(vector[2]);

			lightSource = new Vector3D(x, y, z);
			reader.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		return lightSource;
	}

	public Set<Polygon> readPolygons() {
		Set<Polygon> polys = new HashSet<Polygon>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			// remove light line;
			String line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				Vector3D[] vertices = new Vector3D[3];
				Scanner sc = new Scanner(line);
				for (int i = 0; i < 3; i++) {
					vertices[i] = new Vector3D(sc.nextFloat(), sc.nextFloat(),
							sc.nextFloat());
				}
				// set reflectivity
				Color reflectance = new Color(sc.nextInt(), sc.nextInt(),
						sc.nextInt());
				sc.close();
				polys.add(new Polygon(vertices, reflectance));
			}
			reader.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		return polys;
	}
}