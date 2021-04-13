import java.io.Serializable;

/**
 * This class is intended to represent a player for Tic Tac Toe.
 * Each player contains a name, statistics for their games played, and the symbol they will use.
 * 
 */
public class Player implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String name;
    private int gamesWon;
    private char signal;

    /**
     * No arg constructor for objects of class Player
     */
    public Player() {

        name = "Player One";
        gamesWon = 0;

    }

    /**
     * Constructor for objects of class Player that takes in a string
     * 
     * @param name      The name of the player. String
     */
    public Player(String name) {

        this.name = name;
        gamesWon = 0;
    }

    /**
     * Increments the amount of wins the player has.
     */
    public void wonAGame() {
        gamesWon++;
    }

    /**
     * Returns the amount of games the player has won
     * 
     * @return gamesWon     The amount of games the amount of games the player has won. int
     */
    public int getGamesWon() {
        return gamesWon;
    }

    /**
     * Returns the name of the player.
     * 
     * @return this.name     The name of the player. String
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the symbol for the player
     * 
     * @param signal     The symbol of the player will have
     */
    public void setSignal(char signal) {
        this.signal = signal;
    }

    /**
     * Returns the the symbol of the player.
     * 
     * @return this.signal     The symbol of the player. char
     */
    public char getSignal() {
        return this.signal;
    }

    @Override
    /**
     * Prints out the name of the player
     * 
     * @return this.name     The name of the player. String
     * 
     * Example:
     * If the player was named "Steve Jobs"
     * 
     * then this.name would return "Steve Jobs"
     */
    public String toString() {
        return this.name;
    }

}
