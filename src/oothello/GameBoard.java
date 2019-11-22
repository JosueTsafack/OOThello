package oothello;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

/**
 * Klasse für das Spielbrett enthält die Steine Spieler und Richtungen
 *
 */

@SuppressWarnings("serial")
public class GameBoard implements Serializable {

	private final int size;
	private Stone stones[][];
	private int turn = 0;
	private Player player1, player2 = null;
	private Direction direction;
	private final String botnames[] = { "Jerry", "Morty", "Rick", "Cartman", "Hanzo", "Negan", "Sven Klaus" };
	private long timer;

	/**
	 * Konstruktor zum erstellen eines neuen Spielfeldes
	 *
	 * @param size
	 *            die Größe des zu erstellenden Spielfeldes
	 */

	public GameBoard(int size) {
		this.size = size;
		stones = new Stone[size][size]; // Erstellen des steine Arrays
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				stones[j][i] = new Stone('_', j, i, (j == 0 || j == size - 1 || i == 0 || i == size - 1));
		this.timer = System.currentTimeMillis();
	}

	/**
	 * Speichert den Spielstand
	 *
	 * @param file
	 *            Speicherdatei
	 */

	protected void save(String file) {
		checkPath();
		long temp = timer;
		timer = System.currentTimeMillis() - timer;
		turn--;
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("D:\\OOThello\\" + file + ".ser"))) {
			try {
				oos.writeObject(this);
			} catch (IOException e) {
				System.out.println("Fehler beim speichern.");
			}
		} catch (IOException e1) {
			System.out.println("Fehler beim speichern.");
		}
		timer = temp;
		turn++;
	}

	public void timer() {
		timer = System.currentTimeMillis() - timer;
	}

	/**
	 * Methode um die Startsteine zu setzen
	 */

	private void setStart() {
		stones[0][size - 1].SetSymbol(' '); // Die unteren Eckfelder werden mit
		stones[size - 1][size - 1].SetSymbol(' '); // Leerzeichen besetzt
		stones[size / 2 - 1][size / 2 - 1].SetSymbol('O');
		stones[size / 2][size / 2].SetSymbol('O');
		stones[size / 2][size / 2 - 1].SetSymbol('X');
		stones[size / 2 - 1][size / 2].SetSymbol('X');
	}

	protected void start() {
		System.out.println(getPlayer().getName() + " (" + getPlayer().getSymbol() + ") ist am Zug:");
		if (getPlayer() instanceof GameBoard.AI)
			((AI) getPlayer()).place();
	}

	/**
	 * Methode für die Ausgabe des Spielfeldes in der Konsole
	 */

	protected void draw() {
		System.out.print("\n     " + ((size > 9) ? " " : ""));
		for (int k = 0; k < size - 2; k++) // "Deckel" des Spielfeldes
			System.out.print("_ ");
		System.out.println();
		for (int i = 0; i < size; i++) { // Schleife für jede Zeile
			System.out.print(i + 1 + " "); // Nummerierung
			if (size > 9 && i < 9)
				System.out.print(" ");
			if (0 < i && i < size - 1)
				System.out.print("|");
			else
				System.out.print(" ");
			for (int j = 0; j < size; j++) { // für jedes Element der Zeile
				System.out.print(stones[j][i].getSymbol());
				if (!(j == size - 1 && (i == 0 || i == size - 1)))
					System.out.print("|");
			}
			System.out.println();
		}
		System.out.print("\n   " + ((size > 9) ? " " : ""));
		for (int c = 65; c < size + 65; c++) // Koordinatenbelegung A-Z
			System.out.print((char) c + " ");
		long time = System.currentTimeMillis() - timer;
		System.out.println("\n\nZeit: " + time / 360000 + ":" + time / 60000 + ":" + time / 1000 + "\n");
		turn++;
	}

	/**
	 * Setzt einen Stein des Spielers der am zug ist auf die angegebene
	 * Koordinate
	 *
	 * @param x
	 *            Die x-Koordinate
	 * @param y
	 *            Die y-Koordinate
	 */

	protected void setStone(int x, int y) {
		stones[x][y].SetSymbol(getPlayer().getSymbol());
		checkTurn(stones[x][y]);
		draw();
		if (isRunning()) {
			if (!hasOptions()) {
				System.out.println(getPlayer().getName() + " (" + getPlayer().getSymbol() + ")  ist optionslos");
				turn++;
			}
			start();
		} else if (getScore(player1) != getScore(player2)) {
			highScore((isWinner(player1)) ? player1 : player2);
		} else
			System.out.println("Unentschieden mit: " + getScore(player1) + ":" + getScore(player2));
	}

	/**
	 * Methode die den Highscore verwaltet und speichert.
	 *
	 * @param p
	 *            Der Spieler der gewonnen hat
	 */

	private void highScore(Player p) {
		System.out.println(p.getName() + " (" + p.getSymbol() + ") gewinnt mit: " + getScore(p) + ":"
				+ (p.equals(player1) ? getScore(player2) : getScore(player1)));
		String s = p.getName() + ": " + getScore(p);
		String[] list = { s, "Hanzo: 0", "Hanzo: 0" };
		try (BufferedReader reader = Files.newBufferedReader(
				Paths.get("D:\\OOThello\\HighScore Größe" + (size - 2) + ".txt"), Charset.defaultCharset())) {
			for (int i = 0; i < 3; i++)
				list[i] = reader.readLine();
			String temp;
			for (int i = 0; i < 3; i++) {
				String[] newScore = s.split(" ");
				String[] oldScore = list[i].split(" ");
				if (Integer.valueOf(newScore[newScore.length - 1]) > Integer.valueOf(oldScore[oldScore.length - 1])) {
					temp = list[i];
					list[i] = s;
					s = temp;
				}
			}
		} catch (IOException e) {
			checkPath();
		}
		try {
			PrintWriter writer = new PrintWriter("D:\\OOThello\\HighScore Größe" + (size - 2) + ".txt", "UTF-8");
			for (int i = 0; i < 3; i++)
				writer.println(list[i]);
			writer.close();
		} catch (IOException e) {
			System.out.println("Fehler beim speichern des Highscores.");
		}
	}

	/**
	 * Methode zum überprüfen/erstellen des Speicherordners
	 */

	private void checkPath() {
		File p = new File("D:\\OOThello\\");
		if (!p.exists())
			p.mkdirs();
	}

	/**
	 * Methode zum erstellen der Spieler
	 *
	 * @param name
	 *            der Name des Spielers
	 * @param difficulty
	 *            Schwierigkeitsgrad des Computers, bei Mensch = 0
	 */

	protected void setPlayer(String name, char symbol, int difficulty) {
		if (symbol == 'O')
			player1 = (difficulty == 0) ? new Player(name, 'O') : new AI('O', difficulty);
		else 
			player2 = (difficulty == 0) ? new Player(name, 'X') : new AI('X', difficulty);
		if (player1 != null && player2 != null) {
			setStart();
			draw();
			start();
		}
	}

	/**
	 * Gibt den Spieler zurück der am Zug ist
	 *
	 * @return der Spieler
	 */

	private Player getPlayer() {
		return (turn % 2 == 1) ? player1 : player2;
	}

	/**
	 * Überprüft ob ein Zug möglich ist
	 *
	 * @param x
	 *            x-Koordinate des Feldes
	 * @param y
	 *            y-Koordinate des Feldes
	 * @return true - zug möglich, false - zug nicht möglich
	 */

	protected boolean isPossible(int x, int y) {
		try {
			if (stones[x][y].getSymbol() == '_')
				if (turn > size) {
					if ((!stones[x][y].isOuterCircle())
							&& (checkOptions(x, y, '_', getPlayer().getEnemySymbol(), getPlayer().getSymbol()) > 0))
						return true;
				} else
					return stones[x][y].isOuterCircle() && !(y == 0 && (x == 0 || x == size - 1));// abfangen
		} catch (ArrayIndexOutOfBoundsException e) {// Ungültige Eingaben fangen
		}
		return false;
	}

	/**
	 * Überprüft ob ein Spieler einen Zug machen kann
	 *
	 * @return true - zug möglich, false - zug nicht möglich
	 */

	private boolean hasOptions() {
		if (turn < size + 1)
			return true;
		for (int i = 1; i < size - 1; i++)
			for (int j = 1; j < size - 1; j++)
				if (isPossible(j, i))
					return true;
		return false;
	}

	/**
	 * Überprüft ob einer der Spieler noch einen Zug machen kann
	 *
	 * @return true das Spiel geht weiter, false es bricht ab
	 */

	protected boolean isRunning() {
		if (!hasOptions()) {
			turn++;
			if (!hasOptions()) {
				return false;
			}
			turn--;
		}
		return true;
	}

	/**
	 * Ermittlet ob ein Spieler gewonnen hat
	 *
	 * @param p
	 *            der zu untersuchende Spieler
	 * @return true bei Sieg, false bei Niederlage und Unentschieden
	 */

	private boolean isWinner(Player p) {
		return (p.equals(player1)) ? getScore(player1) > getScore(player2) : getScore(player2) > getScore(player1);
	}

	/**
	 * Methode um zu überprüfen ob Steine übernommen werden
	 *
	 * @param master
	 *            Der stein der gelegt wurde
	 */

	@SuppressWarnings("static-access")
	private void checkTurn(Stone master) {
		if (turn > size)
			for (Direction d : direction.values()) {// Schleife für alle
													// Richtungen
				int i = checkDirection(master, d, master.getSymbol(), getPlayer().getEnemySymbol(), master.getSymbol());
				if (i > 0)
					turn(master, d, i);
			}
	}

	/**
	 * Funktion zum prüfen eines Übernhame Musters
	 *
	 * @param master
	 *            Das zu überprüfende Feld
	 * @param d
	 *            Die zu überprüfende Richtung
	 * @param start
	 *            Das Symbol des Startfeldes
	 * @param mid
	 *            Das Symbol der eingeschlossenen Felder
	 * @param end
	 *            Das Symbol des Endfeldes
	 * @return Anzahl potenzieller Übernahmen/Verhinderungen
	 */

	private int checkDirection(Stone master, Direction d, char start, char mid, char end) {
		if (master.getSymbol() == start) {
			int i;
			Stone neighbour = getNeighbour(master, d);
			for (i = 0; neighbour.getSymbol() == mid; i++)
				neighbour = getNeighbour(neighbour, d);
			if (neighbour.getSymbol() == end && !(neighbour.isOuterCircle() && end == '_'))
				return i;
		}
		return 0;
	}

	/**
	 * Funktion zum prüfen eines Übernhamemusters in alle Richtungen
	 *
	 * @param x
	 *            x-koordinate
	 * @param y
	 *            y-koordinate
	 * @param start
	 *            Das Symbol des Startfeldes
	 * @param mid
	 *            Das Symbol der eingeschlossenen Felder
	 * @param end
	 *            Das Symbol des Endfeldes
	 * @return Anzahl potenzieller Übernahmen/Verhinderungen
	 */

	private int checkOptions(int x, int y, char start, char mid, char end) {
		int turns = 0;
		for (Direction d : Direction.values())
			turns += checkDirection(stones[x][y], d, start, mid, end);
		return turns;
	}

	/**
	 * Funktion um Nachbarsteine zu erhalten
	 *
	 * @param s
	 *            Stein dessen Nachbar bestimmt werden soll
	 * @param d
	 *            Richtung des Nachbarsteins
	 * @return der Nachbarstein
	 */

	private Stone getNeighbour(Stone s, Direction d) {
		try {
			return stones[s.getX() + d.getX()][s.getY() + d.getY()];
		} catch (ArrayIndexOutOfBoundsException e) {
			return new Stone('!', -1, -1, false);
		}
	}

	/**
	 * Methode zum übernehmen von Steinen
	 *
	 * @param master
	 *            der Stein der die anderen übernimmt
	 * @param d
	 *            Die Richtung in die übernommen wird
	 * @param j
	 *            Anzahl der Übernahmen
	 */

	private void turn(Stone master, Direction d, int j) {
		Stone neighbour = getNeighbour(master, d);
		for (int i = 0; i < j; i++) {
			neighbour.turn();
			System.out.println("Übernahme bei: " + ((char) (neighbour.getX() + 65)) + (neighbour.getY() + 1));
			neighbour = getNeighbour(neighbour, d);
		}
		System.out.println();
	}

	/**
	 * Funktion um den Punktestand eines Spielers zu erhalten
	 *
	 * @param p
	 *            der Spieler
	 * @return der Punktestand
	 */

	private int getScore(Player p) {
		int score = 0;
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				if (stones[j][i].getSymbol() == p.getSymbol())
					score++;
		return score;
	}

	/**
	 * Klasse für den Computergegner
	 *
	 */

	public class AI extends Player {

		private final int difficulty;

		public AI(char symbol, int difficulty) {
			super(botnames[new Random().nextInt(6)], symbol);
			this.difficulty = difficulty;
		}

		/**
		 * Methode für den Zug der KI
		 */

		public void place() {
			if (turn < size + 1)
				startPhase();
			else if (difficulty == 2)
				medium();
			else if (difficulty == 3)
				hard();
			else
				easy();
		}

		/**
		 * Methode zum setzen in Setzphase
		 */

		private void startPhase() {
			if (isPossible(1, size - 1))
				setStone(1, size - 1);
			else if (isPossible(0, size - 2))
				setStone(0, size - 2);
			else if (isPossible(size - 1, size - 2))
				setStone(size - 1, size - 2);
			else if (isPossible(size - 2, size - 1))
				setStone(size - 2, size - 1);
			else if (isPossible(size - 2, 0))
				setStone(size - 2, 0);
			else if (isPossible(size - 1, 1))
				setStone(size - 1, 1);
			else if (isPossible(0, 1))
				setStone(0, 1);
			else if (isPossible(1, 0))
				setStone(1, 0);
			else
				easy();
		}

		/**
		 * Einfache Zugberechnung auschließlich per Zufall
		 */

		private void easy() {
			int x = 0;
			int y = 0;
			Random rnd = new Random();
			do {
				x = rnd.nextInt(size - 1);
				y = rnd.nextInt(size - 1);
			} while (!isPossible(x, y));
			setStone(x, y);
		}

		/**
		 * Mittlere Zugberechnung: Es wird immer der Zug ausgeführt der am
		 * meisten Steine übernimmt
		 */

		private void medium() {
			int x = 0;
			int y = 0;
			int t = 0;
			for (int i = 1; i < size - 1; i++)
				for (int j = 1; j < size - 1; j++)
					if (isPossible(j, i)) {
						int direct = checkOptions(j, i, '_', getEnemySymbol(), getSymbol());
						if (direct > t) {
							t = direct;
							x = j;
							y = i;
						}
					}
			setStone(x, y);
		}

		/**
		 * Fortgeschrittene Zugberechnung: Wägt ab unter Direktübernahmen,
		 * Vorbereitung von Übernahmen und verhindern von gegnerischen
		 * Übernahmen
		 */

		private void hard() {
			int x = 0;
			int y = 0;
			int t = 0;
			for (int i = 1; i < size - 1; i++)
				for (int j = 1; j < size - 1; j++)
					if (isPossible(j, i)) {
						int direct = checkOptions(j, i, '_', getEnemySymbol(), getSymbol()) * 5;
						int later = checkOptions(j, i, '_', getEnemySymbol(), '_')*4;
						int prevent = prevent(j, i) * 10;
						int total = direct + later + prevent;
						if (total > t) {
							t = total;
							x = j;
							y = i;
						}
					}
			setStone(x, y);
		}

		/**
		 * Funktion die die Anzahl an möglichen gegenerischen Übernhamen
		 * ermittelt
		 *
		 * @param x
		 *            Die x-Koordinate
		 * @param y
		 *            Die y-Koordinate
		 * @return Anzahl von verhinderbaren Übernahmen
		 */

		@SuppressWarnings("static-access")
		private int prevent(int x, int y) {
			int prevents = 0;
			for (Direction d : direction.values()) {
				Stone n = getNeighbour(stones[x][y], d);
				if (stones[x][y].getSymbol() == '_' && n.getSymbol() == getEnemySymbol()) {
					n = getNeighbour(n, d);
					int pd;
					for (pd = 0; n.getSymbol() == getSymbol(); pd++)
						n = getNeighbour(n, d);
					if (n.getSymbol() == '_')
						prevents += pd;
				}
			}
			return prevents;
		}

	}
}
