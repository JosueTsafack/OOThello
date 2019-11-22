package oothello;

import java.io.Serializable;

/**
 * Enumeration für die verschiedenen Richtungen in welcher Nachbarsteine
 * berechnet werden
 */

public enum Direction implements Serializable  {
	Left(-1, 0), TopLeft(-1, -1), Top(0, -1), TopRight(1, -1), Right(1, 0), BottomRight(1, 1), Bottom(0,
			1), BottomLeft(-1, 1);

	private final int x, y;

	Direction(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
