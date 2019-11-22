package oothello;

import java.io.Serializable;

/**
 * Klasse für die Spieler
 *
 */

@SuppressWarnings("serial")
public class Player implements Serializable  {

	private String name;
	private final char symbol;

	public Player(String name, char symbol) {
		this.name = name;
		this.symbol = symbol;
	}

	public String getName() {
		return name;
	}

	public char getEnemySymbol() {
		return (symbol == 'X') ? 'O' : 'X';
	}

	public char getSymbol() {
		return symbol;
	}

	public void setName(String name) {
		this.name = name;
	}
}
