import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Board {
	public static final int NUM_TILES_SIDE = 7;

	/**
	 * Helper class to encapsulate tile {@code type} and {@code treasureNum} pair
	 */
	private static class TypeAndTreasureNum {

		private char type;
		private int treasureNum;

		public TypeAndTreasureNum (char type, int treasureNum) {
			this.type = type;
			this.treasureNum = treasureNum;
		}

		// getters
		public char getType() {
			return this.type;
		}

		public int getTreasureNum() {
			return this.treasureNum;
		}

		public String toString(){
			return String.valueOf(type);
		}
	}

	// Stationary board tiles information
	private static final char[][] typeOfStationaryTiles = new char[][] {
			{'L',' ','T',' ','T',' ','L'},
			{' ',' ',' ',' ',' ',' ',' '},
			{'T',' ','T',' ','T',' ','T'},
			{' ',' ',' ',' ',' ',' ',' '},
			{'T',' ','T',' ','T',' ','T'},
			{' ',' ',' ',' ',' ',' ',' '},
			{'L',' ','T',' ','T',' ','L'}
	};
	private static final int[][] orientationOfStationaryTiles = new int[][] {
			{'1',' ','2',' ','2',' ','2'},
			{' ',' ',' ',' ',' ',' ',' '},
			{'1',' ','1',' ','2',' ','3'},
			{' ',' ',' ',' ',' ',' ',' '},
			{'1',' ','0',' ','3',' ','3'},
			{' ',' ',' ',' ',' ',' ',' '},
			{'0',' ','0',' ','0',' ','3'},
	};

	// Board tiles
	// Goes from left to right, top to bottom.
	private Tile[][] tiles = new Tile[7][7];
	private Tile extraTile;


	// Players pieces
	private Player[] players;

	// Treasures
	private Treasure[] treasures;

	public Board(Player[] players, Treasure[] treasures) {
		this.players = players;
		this.treasures = treasures;
		init();
	}

	public void init() {
		setupTiles();
		connectTiles(0, tiles.length, 0, tiles.length);
		connectPlayersToTiles();
	}

	public void setupTiles() {

		final int TOTAL_TILE_AMOUNT = TTile.TILE_AMOUNT + LTile.TILE_AMOUNT + ITile.TILE_AMOUNT;
		final int MAX_SHIFTABLE_TREASURE = 6;
		final int STATIONARY_TILE_AMOUNT = 12;

		// Initial tile data generation
		// -------------------------------------------------------------
		ArrayList<TypeAndTreasureNum> shiftableTilesData = new ArrayList<TypeAndTreasureNum>();
		int shiftableTreasureCounter = STATIONARY_TILE_AMOUNT;

		for(int i = 0; i < TTile.TILE_AMOUNT; i++) {
			shiftableTilesData.add(new TypeAndTreasureNum('T', (i < MAX_SHIFTABLE_TREASURE ? shiftableTreasureCounter : -1)));
			shiftableTreasureCounter++;
		}

		for(int i = 0; i < LTile.TILE_AMOUNT; i++) {
			shiftableTilesData.add(new TypeAndTreasureNum('L', (i < MAX_SHIFTABLE_TREASURE ? shiftableTreasureCounter : -1)));
			shiftableTreasureCounter++;
		}

		for(int i = 0; i < ITile.TILE_AMOUNT; i++) {
			shiftableTilesData.add(new TypeAndTreasureNum('I', -1));
		}
		// -------------------------------------------------------------

		// Randomize shiftable tiles data
		Collections.shuffle(shiftableTilesData, new Random());
		System.out.println(shiftableTilesData);

		// Setup board tiles
		// -------------------------------------------------------------
		int stationaryTreasureCounter = 0;
		int dataCounter = 0;

		for(int row = 0; row < tiles.length; row++) {
			for(int col = 0; col < tiles[row].length; col++) {

				char tileType = typeOfStationaryTiles[row][col];

				// set-up preset/stationary tiles
				if(tileType != ' ') {

					// create tile object
					tiles[row][col] = generateTile(row, col, tileType, stationaryTreasureCounter, true);

					stationaryTreasureCounter++;
				}
				// setup movable tiles
				else {
					TypeAndTreasureNum currentTileData = shiftableTilesData.get(dataCounter);

					// create tile object
					tiles[row][col] = generateTile(row, col, currentTileData.getType(), currentTileData.getTreasureNum(),false);

					dataCounter++;
				}
			}
		}
		// -------------------------------------------------------------

		// Setup extraTile
		// -------------------------------------------------------------
		TypeAndTreasureNum extraTileData = shiftableTilesData.get(dataCounter);

		// create extra tile object
		extraTile = generateTile(-1, -1, extraTileData.getType(), extraTileData.getTreasureNum(), false);
		extraTile.setExtra(true);
		// -------------------------------------------------------------

		// debug
		printBoard();
		System.out.println();
		printOrientation();
	}

	private Tile generateTile(int row, int col, char tileType, int treasureNum, boolean isStationary) {
		Tile newTile;
		int orientation;
		Random rand = new Random();

		// generate orientation
		if(isStationary) {
			orientation = (orientationOfStationaryTiles[row][col] - '0');
		} else {
			orientation = (rand.nextInt(4));
		}

		// generate new tile object
		if(tileType == 'T') {
			newTile = new TTile(row, col,  orientation);
		} else if (tileType == 'L') {
			newTile = new LTile(row, col,  orientation);
		} else {
			newTile = new ITile(row, col, orientation);
		}

		//add treasure
		if(treasureNum != -1) {
			newTile.setTreasure(treasures[treasureNum]);
		}

		return newTile;
	}


	/**
	 * Connects each tile, within certain rows and columns, to its neighbouring tiles
	 *
	 * @param rowStart beginning of row constraint
	 * @param rowEnd end of row constraint
	 * @param colStart beginning of column constraint
	 * @param colEnd end of column constraint
	 */
	public void connectTiles(int rowStart, int rowEnd, int colStart, int colEnd) {
		for(int row = rowStart; row < rowEnd; row++) {
			for(int col = colStart; col < colEnd; col++) {

				// Connects to the tile row above
				if(row != 0 && tiles[row][col].getOpening(0) && tiles[row - 1][col].getOpening(2)) {
					tiles[row][col].addAdjTile(tiles[row - 1][col]);
				}

				// Connects to the tile column right
				if(col != 6 && tiles[row][col].getOpening(1) && tiles[row][col + 1].getOpening(3)) {
					tiles[row][col].addAdjTile(tiles[row][col + 1]);
				}

				// Connects to the tile row below
				if(row != 6 && tiles[row][col].getOpening(2) && tiles[row + 1][col].getOpening(0)) {
					tiles[row][col].addAdjTile(tiles[row + 1][col]);
				}

				// Connects tot the tile column left
				if(col != 0 && tiles[row][col].getOpening(3) && tiles[row][col - 1].getOpening(1)) {
					tiles[row][col].addAdjTile(tiles[row][col - 1]);
				}
			}
		}
	}

	public void connectPlayersToTiles() {
		for(int i = 0; i < players.length; i++) {
			tiles[players[i].getRow()][players[i].getCol()].addPlayerOnTile(players[i]);
		}
	}

	private void printBoard(){
		for(int r = 0; r < tiles.length; r++) {
			for (int c = 0; c < tiles[r].length; c++) {
				System.out.print(tiles[r][c].getType() + " ");
			}
			System.out.println();
		}
	}

	private void printOrientation(){
		for(int r = 0; r < tiles.length; r++) {
			for (int c = 0; c < tiles[r].length; c++) {
				System.out.print(tiles[r][c].getOrientation() + " ");
			}
			System.out.println();
		}
	}

	private void becomeExtraTile(Tile tile) {
		tile.setRow(-1);
		tile.setCol(-1);
		tile.setExtra(true);
	}
	private void becomeBoardTile(Tile extraTile, int row, int col) {
		tiles[row][col] = extraTile;
		tiles[row][col].setRow(row);
		tiles[row][col].setCol(col);
		tiles[row][col].setExtra(false);
	}

	public void shiftRowLeft(int row) {
		// Hold new extra tile
		Tile newExtraTile = tiles[row][0];
		becomeExtraTile(newExtraTile);

		// Shift row
		for (int col = 0; col < tiles[row].length - 1; col++) {
			tiles[row][col] = tiles[row][col + 1];
			tiles[row][col].setCol(col);
		}

		// Insert previous extra tile to the end of row
		becomeBoardTile(extraTile, row, tiles.length - 1);

		// Set the new extra tile
		extraTile = newExtraTile;
	}

	public void shiftRowRight(int row) {
		// Hold new extra tile
		Tile newExtraTile = tiles[row][tiles.length - 1];
		becomeExtraTile(newExtraTile);

		// Shift row
		for (int col = tiles[row].length - 1; col > 0; col--) {
			tiles[row][col] = tiles[row][col - 1];
			tiles[row][col].setCol(col);
		}

		// Insert previous extra tile to the start of row
		becomeBoardTile(extraTile, row, 0);

		// Set the new extra tile
		extraTile = newExtraTile;
	}

	public void shiftColUp(int col) {
		// Hold new extra tile
		Tile newExtraTile = tiles[0][col];
		becomeExtraTile(newExtraTile);

		// Shift column
		for (int row = 0; row < tiles.length - 1; row++) {
			tiles[row][col] = tiles[row + 1][col];
			tiles[row][col].setRow(row);
		}

		// Insert previous extra tile to the end of column
		becomeBoardTile(extraTile, tiles.length - 1, col);

		// Set the new extra tile
		extraTile = newExtraTile;
	}

	public void shiftRowDown(int col) {
		// Hold new extra tile
		Tile newExtraTile = tiles[tiles.length - 1][col];
		becomeExtraTile(newExtraTile);

		// Shift column
		for (int row = tiles.length - 1; row > 0; row--) {
			tiles[row][col] = tiles[row - 1][col];
			tiles[row][col].setRow(row);
		}

		// Insert previous extra tile to the start of column
		becomeBoardTile(extraTile, 0, col);

		// Set the new extra tile
		extraTile = newExtraTile;
	}

	// Setters and getters
	public Tile[][] getTiles() {
		return tiles;
	}

	public Tile getExtraTile() {
		return this.extraTile;
	}
}
