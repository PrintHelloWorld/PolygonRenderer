package render;

public class EdgeListEntry {
	private float leftX;
	private float leftZ;

	private float rightX;
	private float rightZ;

	public EdgeListEntry() {
		this.setLeftX(Float.POSITIVE_INFINITY);
		this.setLeftZ(Float.POSITIVE_INFINITY);
		this.setRightX(Float.NEGATIVE_INFINITY);
		this.setRightZ(Float.POSITIVE_INFINITY);
	}

	public float getLeftX() {
		return leftX;
	}

	public void setLeftX(float leftX) {
		this.leftX = leftX;
	}

	public float getLeftZ() {
		return leftZ;
	}

	public void setLeftZ(float leftZ) {
		this.leftZ = leftZ;
	}

	public float getRightX() {
		return rightX;
	}

	public void setRightX(float rightX) {
		this.rightX = rightX;
	}

	public float getRightZ() {
		return rightZ;
	}

	public void setRightZ(float rightZ) {
		this.rightZ = rightZ;
	}

	public void setLeft(float x, float z) {
		leftX = x;
		leftZ = z;
	}

	public void setRight(float x, float z) {
		rightX = x;
		rightZ = z;
	}

	public boolean isVisited() {
		if (leftX == Float.POSITIVE_INFINITY
				|| leftZ == Float.POSITIVE_INFINITY
				|| rightX == Float.NEGATIVE_INFINITY
				|| rightZ == Float.POSITIVE_INFINITY)
			return false;
		return true;
	}
}
