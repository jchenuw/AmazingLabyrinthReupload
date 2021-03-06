package model;

import java.util.*;

public abstract class Player extends Piece {

	private String colour;
	private Stack<Card> hand;
	private Card currentCard;

	// Home tile information
	private Tile homeTile;
	private boolean returnedHome = false;

	// Constructor
	public Player(int row, int col, String colour) {
		super(row, col);

		this.colour = colour;
	}

	/**
	 * Draws the next card for player
	 */
	public void goToNextCard() {

		if(hand.size() != 0) {
			// remove obtained treasure
			hand.pop();
		}

		if(hand.size() != 0) {
			// go to next treasure to collect
			currentCard = hand.peek();
		} else {
			currentCard = null;
		}
	}

	/**
	 * sets the top card of hand to this new card
	 * @param card The card to be sent to the top of the stack
	 */
	public void addToHand(Card card){
		hand.push(card);
	}

	/**
	 * Checks if the player has won by checking if player still has cards left
	 * @return If the player has won
	 */
	public boolean hasCollectedAll() {
		return(currentCard == null);
	}
	public boolean hasWon() {
		return returnedHome && hasCollectedAll();
	}

	// Getters and setters
	public void setHand(Stack<Card> hand) {
		this.hand = hand;

		if(hand.size() != 0) {
			currentCard = hand.peek();
		}
	}
	public Stack<Card> getHand() {
		return this.hand;
	}

	/**
	 * sets the current card and pushes the old current card to top of hand
	 * @param card The card to be the new current card
	 */
	public void setCurrentCard(Card card){
		hand.push(currentCard);
		currentCard = card;
	}

	public Card getCurrentCard() {
		return this.currentCard;
	}

	public void setReturnedHome(boolean returnedHome) {
		this.returnedHome = returnedHome;
	}
	public boolean hasReturnedHome() {
		return this.returnedHome;
	}

	public void setHomeTile(Tile homeTile) {
		this.homeTile = homeTile;
	}
	public Tile getHomeTile() {
		return this.homeTile;
	}
}
