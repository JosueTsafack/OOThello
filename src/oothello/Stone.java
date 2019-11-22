package oothello;

import java.io.Serializable;

/**
 * Klasse für die Spielsteine symbol = '_' repräsentieren ungesetzte felder
 */

@SuppressWarnings("serial")
public class Stone implements Serializable {
	private char symbol;
	private final int x, y;
	private final boolean isOuterCircle;

	public Stone(char symbol, int x, int y, boolean isOuterCircle) {
		this.symbol = symbol;
		this.x = x;
		this.y = y;
		this.isOuterCircle = isOuterCircle;
	}

	/**
	 * "Dreht" einen Stein um
	 */

	public void turn() {
		symbol = (symbol == 'X') ? 'O' : 'X';
	}

	public boolean isOuterCircle() {
		return isOuterCircle;
	}

	public void SetSymbol(char symbol) {
		this.symbol = symbol;
	}

	public char getSymbol() {
		return symbol;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
