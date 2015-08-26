package render;

import util.Vector3D;


public class EdgeListNormalEntry extends EdgeListEntry {
	private Vector3D leftNorm;
	private Vector3D rightNorm;

	public EdgeListNormalEntry() {
		this.setLeftX(Float.POSITIVE_INFINITY);
		this.setLeftZ(Float.POSITIVE_INFINITY);
		this.setRightX(Float.NEGATIVE_INFINITY);
		this.setRightZ(Float.POSITIVE_INFINITY);
	}

	public Vector3D getLeftNorm() {
		return leftNorm;
	}

	public Vector3D getRightNorm() {
		return rightNorm;
	}

	public void setLeft(float x, float z, Vector3D norm) {
		setLeftX(x);
		setLeftZ(z);
		leftNorm = norm;
	}

	public void setRight(float x, float z, Vector3D norm) {
		setRightX(x);
		setRightZ(z);
		rightNorm = norm;
	}
}
