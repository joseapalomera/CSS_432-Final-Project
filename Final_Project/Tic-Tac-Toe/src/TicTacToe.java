import java.util.*;
import java.io.*;
import java.io.Serializable;

/**
 * This class creates a tic tac toe board for the players to play on
 * and allows the players to play on the board, and it will check if players have won
 * This program has methods to to create the tic tac toe board, check if a player has won, and place pieces on the board
 * This program assumes that the players know how to play tic tac toe.
 *
 */
public class TicTacToe implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //instance variable for the tic tac toe table
    private char[][] table;
    private Player first = null;
    private Player second = null;
    public boolean goFirst = true;

    /**
     * This constructor creates the tic tac toe game
     * 
     */
    public TicTacToe() {

        setUpGame();

    }

    /**
     * The following is a number of basci getters and setters used for the game.
     * 
     * 
     */
    public void setPlayerOne(Player p)
    {
        this.first = p;
    }

    public String getPlayerOneName()
    {
        return this.first.getName();
    }

    public Player getPlayerOne()
    {
        return this.first;
    }

    public Player getPlayerTwo()
    {
        return this.second;
    }

    public void setPlayerTwo(Player p)
    {
        this.second = p;
    }

    public String getPlayerTwoName()
    {
        return this.second.getName();
    }

    /**
     * End of getters and setters
     */
    
    /**
     * Determined whether there is a second player or not
     * 
     * @return boolean  Second player open or not
     */
    public boolean noSecond()
    {
        return this.second == null;
    }

    @Override
    /**
     * A different form to represent the TicTacToe game. Holds 2 strings we can return depending
     * if we are waiting for a second player or not
     * 
     * @return String
     */
    public String toString()
    {
        if(noSecond() == true)
        {
            return "Player needed!\n";
        }

        return " vs. " + second + ".\n";

    }


    /**
     * This method will help instantiate the table for use to get ready for the game 
     */
    public void setUpGame()
    {
        //Char array will hold all the elements in the (X and O's)
        //as the game is being played
        table = new char[3][3];

        //Assigns every element to N meaning there is nothing 
        for(int i = 0; i < table.length; i++) {
            for(int j = 0; j < table[i].length; j++) {
                table[i][j] = '-';
            }
        }
    }

    /**
     * This method prints the tic tac toe table 
     * It also prints out indicators for the rows and columns so the user knows the corresponding number to the box
     */
    public void showTheTable(PrintWriter out)   throws IOException
     { 
        //System.out.println("   [1][2][3]");
        out.println("-------------");
        //int rowNum = 3;

        for(int i = 0; i < table.length; i++) {

            out.print("| ");

            for(int j = 0; j < table[i].length; j++) {
                out.print(table[i][j] + " | ");
            }
            out.println();
            out.println("-------------");

        }

    }

    /**
     * This method checks if the spot that the tic-tac-toe piece to be put into is already filled
     * and if it is it allows the user to continue to keep trying to place a piece
     * 
     * @param row       The row that the piece is trying to be placed in. int
     * @param column    The column that the piece is trying to be placed in. int
     * @param sign      The symble of the player. char
     */
    public boolean spotAvailable(int row, int column, char sign)
    {

        if (table[row][column] == '-')
        {
            table[row][column] = sign;
            return true;
        }
        else 
        {
            return false;
        }
    }
    /**
     * This method checks if the tic tac toe board is full of pieces.
     * 
     * @return true     returns true if the board is full. boolean
     * @return false    returns false if the board is not full. boolean
     */
    public boolean isFull() 
    {

        for(int i = 0; i < table.length; i++) {
            for(int j = 0; j < table.length; j++) {
                if(table[i][j] == '-') {
                    return false;
                }
            }
        }

        return true;

    }

    /**
     * This method checks if a player has won the game of tic tac toe.
     * 
     * @param piece     the piece of the player that's being checked if they won. int
     * 
     * @return true     returns true if the player has won. boolean
     * @return false    returns false if the player has not won. boolean
     */
    public boolean anyWin(char piece) 
    {

        //Checks diagonally
        if(table[0][0] == piece && table[1][1] == piece && table[2][2] == piece) {
            return true;
        }else if(table[0][2] == piece && table[1][1] == piece && table[2][0] == piece){
            return true;
        }

        //Checks across
        for(int i = 0; i < table.length; i++) {
            if(piece == table[i][0] && piece == table[i][1] && piece == table[i][2]) {
                return true;
            }
        }
        //Checks up/down
        for(int i = 0; i < table.length; i++) {
            if(piece == table[0][i] && piece == table[1][i] && piece == table[2][i]) {
                return true;
            }
        }

        //Returns false if there isn't 3 in a row
        return false;

    }

    /**
     * This method shifts the number for the row entered in by the player.
     * This shift is needed to allow the game to be more intuitive for the player
     * 
     * @param row       The row enetered in by the player. int
     * 
     * @return row      The row number after it has been shifted. int
     */
    public int shiftRows(int row) 
    {

        //Adjusts the rows to make it more intuitive for the user
        if(row == 1) {
            return 2;
        }else if(row == 2){
            return 1;
        }else if(row == 3){
            return 0;
        }

        return row;
    }

    public boolean checkRowColumn(int row, int column)
    {
        if(row != 1 && row != 2 && row != 3)
        {
            return false;
        }

        if(column != 1 && column != 2 && column != 3)
        {
            return false;
        }

        return true;
    }

}
