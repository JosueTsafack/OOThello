package oothello;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.InputMismatchException;
import java.util.Scanner;

public class GameMain {

	static GameBoard board = null;

	public static void main(String[] args) {
		System.out.println(" ");
		info();
		inputLoop();
	}

	/**
	 * Schleife die die Eingaben überwacht
	 */

	public static void inputLoop() {
		while (true) {
			@SuppressWarnings("resource")
			String input = new Scanner(System.in).nextLine().toUpperCase();
			if (board != null && board.isRunning() && input.length() > 1 && input.length() < 4) { // Annahme
				// Koordinate
				int x = 0;
				int y = 0;
				if (input.length() == 2) { // 2-stellige Koordinate
					x = input.charAt(0) - 64;
					y = input.charAt(1) - 48;
				} else { // 3-stellige Koordinate
					x = input.charAt(0) - 64;
					y = (input.charAt(1) - 48) * 10 + (input.charAt(2) - 48);
				}
				if (board.isPossible(x - 1, y - 1)){
					board.setStone(x - 1, y - 1);
				}else{
					System.out.println("Ungültigebbbbbbbbbbbb Koordinate."); // warum  wird das immer angezeigt?????
				}
			} else if (input.equals("START")) {
				setBoard();
				setPlayers();
			} else if (board != null && input.equals("SPEICHER"))
				board.save("StandardSave");
			else if (input.equals("LADE"))
				load("StandardSave");
			else if (input.equals("INFO"))
				info();
			else if (input.equals("ENDE"))
				System.exit(0);
			else if (board != null && input.contains("SPEICHER "))
				board.save(input.substring(9));
			else if (input.contains("LADE "))
				load(input.substring(5));
			else
				System.out.println("Ungültige Eingabe.");
		}
	}

	/**
	 * Gibt die Befehlsübersicht aus
	 */

	public static void info() {
		System.out.println("Befehlsübersicht: \n\n" + "start				startet ein neues Spiel\n"
				+ "speicher			speichert den Standardspielstand\n"
				+ "speicher + Pfad			speichert das aktuelle Spiel unter dem angegebenen Namen\n"
				+ "lade				lädt den Standardspielstand\n"
				+ "lade + Pfad			lädt den Spielstand unter dem angegebenen Namen\n"
				+ "info				zeigt die Befehlsübersicht\n" + "ende				beendet das Spiel \n");
	}

	/**
	 * Methode zum erstellen des Spielfeldes
	 */

	public static void setBoard() {
		System.out.println("Wähle die Spielfeldgröße: ");
		try { // Überprüfung auf Zahlen
			@SuppressWarnings("resource")
			int size = new Scanner(System.in).nextInt();
			if (size > 5 && size < 11 && size % 2 == 0) // Wertebereich
				board = new GameBoard(size + 2);
			else
				throw new InputMismatchException();
		} catch (InputMismatchException e) {
			System.out.println("Bitte nur gerade Zahlen zwischen 6 und 10 eingeben:");
			setBoard();
		}
	}

	/**
	 * Methode zum erstellen des Spieler
	 */

	public static void setPlayers() {
		char symbol = ' ';
		for (int i = 1; i < 3; i++) {
			System.out.println("Spieler " + i + " gib einen Namen ein: (Für einen Computergegner 1-3) ");
			@SuppressWarnings("resource")
			String name = new Scanner(System.in).nextLine();
			if (i == 1)
				do {
					System.out.println("Wähle eine Farbe {O;X}, O beginnt.");
					symbol = new Scanner(System.in).next().toUpperCase().charAt(0);
				} while (!(symbol == 'O'| symbol == 'X'));
			else
				symbol = (symbol == 'O') ? 'X' : 'O';
			if (name.equals("1") || name.equals("2") || name.equals("3"))
				board.setPlayer("", symbol, Integer.valueOf(name));
			else
				board.setPlayer(name, symbol, 0);
		}
	}

	/**
	 * Methode zum laden eines Spielstandes
	 *
	 * @param file
	 *            Speicherdatei
	 * @throws IOException
	 *
	 */

	public static void load(String file) {
		try (FileInputStream fis = new FileInputStream("D:\\OOThello\\" + file + ".ser");
				ObjectInputStream ois = new ObjectInputStream(fis)) {
			board = (GameBoard) ois.readObject();
			board.timer();
			board.draw();
			board.start();
		} catch (ClassNotFoundException | IOException e) {
			System.out.println("Datei fehlerhaft oder nicht vorhanden.");
		}
	}
}
