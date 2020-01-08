import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Board {

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
			{1, 0, 3, 0, 3, 0, 3},
			{0, 0, 0, 0, 0, 0, 0},
			{2, 0, 2, 0, 3, 0, 4},
			{0, 0, 0, 0, 0, 0, 0},
			{2, 0, 1, 0, 4, 0, 4},
			{0, 0, 0, 0, 0, 0, 0},
			{2, 0, 1, 0, 1, 0, 4},
	};

	// Board tiles
	// Goes from left to right, top to bottom.
	private Tile[][] tiles = new Tile[7][7];

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
	}

	public void setupTiles() {

		final int TOTAL_TILE_AMOUNT = TTile.TILE_AMOUNT + LTile.TILE_AMOUNT + ITile.TILE_AMOUNT;
		final int MAX_SHIFTABLE_TREASURE = 6;
		final int STATIONARY_TILE_AMOUNT = 16;
		Random rand = new Random();

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

		// Setup board tiles
		// -------------------------------------------------------------
		int stationaryTreasureCounter = 0;
		int dataCounter = 0;

		for(int row = 0; row < tiles.length; row++) {
			for(int col = 0; col < tiles.length; col++) {

				char tileType = typeOfStationaryTiles[row][col];

				// set-up preset/stationary tiles
				if(tileType != ' ') {

					// create tile object
					if(tileType == 'T') {
						tiles[row][col] = new TTile(row, col, orientationOfStationaryTiles[row][col]);
					} else if (tileType == 'L') {
						tiles[row][col] = new LTile(row, col, orientationOfStationaryTiles[row][col]);
					} else {
						tiles[row][col] = new ITile(row, col, orientationOfStationaryTiles[row][col]);
					}

					// add treasure
					tiles[row][col].setTreasure(treasures[stationaryTreasureCounter]);

					stationaryTreasureCounter++;
				}
				// setup movable tiles
				else {
					TypeAndTreasureNum currentTileData = shiftableTilesData.get(dataCounter);
					int currentTreasureNum = currentTileData.getTreasureNum();
					tileType = currentTileData.getType();

					// create tile object
					if(tileType == 'T') {
						tiles[row][col] = new TTile(row, col, (rand.nextInt(4) + 1));
					} else if (tileType == 'L') {
						tiles[row][col] = new LTile(row, col, (rand.nextInt(4) + 1));
					} else {
						tiles[row][col] = new ITile(row, col, (rand.nextInt(4) + 1));
					}

					//add treasure
					if(currentTreasureNum != -1) {
						tiles[row][col].setTreasure(treasures[currentTreasureNum]);
					}
				}
			}
		}
	}

	public void shiftRowLeft(int row, Tile extraTile) {
		Tile newExtraTile = tiles[row][0];
		System.arraycopy(tiles[row], 1, tiles[row], 0, tiles[row].length - 1);
		tiles[row][tiles.length - 1] = extraTile;
		extraTile = newExtraTile;
	}

	public void shiftRowRight(int row, Tile extraTile) {
		Tile newExtraTile = tiles[row][tiles.length - 1];
		System.arraycopy(tiles[row], 0, tiles[row], 1, tiles[row].length - 1);
		tiles[row][0] = extraTile;
		extraTile = newExtraTile;
	}

	public void shiftColUp(int col, Tile extraTile) {
		Tile newExtraTile = tiles[0][col];
		System.arraycopy(tiles, 1, tiles, 0,  tiles.length - 1);
		tiles[tiles.length - 1][col] = extraTile;
		extraTile = newExtraTile;
	}

	public void shiftRowDown(int col, Tile extraTile) {
		Tile newExtraTile = tiles[tiles.length - 1][col];
		System.arraycopy(tiles, 0, tiles, 1,  tiles.length - 1);
		tiles[0][col] = extraTile;
		extraTile = newExtraTile;
	}
}

