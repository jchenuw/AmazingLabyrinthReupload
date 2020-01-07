public class Treasure {

	public static final int TREASURE_AMOUNT = 24;

    private final int TREASURE_NUM;
    private boolean collected = false;

	/**
	 * Treasure constructor
	 *
	 * @param treasureNum integer id of this treasure
	 */
	public Treasure(int treasureNum) {
    	TREASURE_NUM = treasureNum;
	}

	// Setters and getters
	public int getTreasureNum() {
		return this.TREASURE_NUM;
	}

	public void setCollected(boolean collected) {
		this.collected = collected;
	}
	public boolean isCollected() {
		return this.collected;
	}

}
