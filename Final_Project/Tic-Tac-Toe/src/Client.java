import java.net.*;
//import java.nio.CharBuffer;
import java.io.*;
import java.util.*;



//used https://www.geeksforgeeks.org/multithreaded-servers-in-java/
public class Client 
{

    private static Player player;               //Current player
    private static Socket cSocket;              //Client Socket
    private static Scanner scnr;                //Scanner

    //Main method will instantiate Buffers, Writers, and crete a new player so we can play a game.
    //It will then take us to the homescreen right after we've created a new Client(player)
    public static void main(String[] args) throws ClassNotFoundException 
    {
        try
        {
            //create socket
            cSocket = new Socket("csslab10.uwb.edu", 7999);
            // create scanner
            scnr = new Scanner(System.in);

            // writes to server
            PrintWriter out = new PrintWriter(cSocket.getOutputStream(), true);
        
            // reads from server
            BufferedReader in = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));

            ObjectOutputStream objectOut = new ObjectOutputStream(cSocket.getOutputStream());
            ObjectInputStream objectIn = new ObjectInputStream(cSocket.getInputStream());
       
            System.out.println("Enter a username: ");

            do
            {
                String userName = scnr.nextLine();

                out.println(userName);

                boolean isUsed = (Boolean) objectIn.readObject();

                if(isUsed == true)
                {
                    System.out.println(in.readLine());
                }
                else
                {
                    System.out.println(in.readLine());
                    System.out.println();
                
                    //Establish the player's details they will use to display a name, games won and such
                    player = new Player(userName);
                    break;
                }

            }
            while(true);
        

            homescreen(out, objectOut, in, objectIn);

            scnr.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    //contains menu to game that gives user options
    public static void homescreen(PrintWriter out, ObjectOutputStream objectOut, BufferedReader in, ObjectInputStream objectIn) throws IOException,
            ClassNotFoundException
    {

        String line = null;
        
        while(true)
        {
            //menu of choices
            System.out.println("Select an Option :");
            System.out.println("[1] List Games");
            System.out.println("[2] Create Game");
            System.out.println("[3] Join Game");
            System.out.println("[4] Exit game");
            System.out.println();

            //reads in user input
            line = scnr.nextLine();

            if (line.equals("1")) 
            {
                //Request to see the list of games available
                out.println(line);
                boolean temp = listGames(objectIn); 
            }
            if (line.equals("2")) 
            {
                //Request to create a new game
                createGame(line, out, in, objectOut);
                //Wait for a second player to arrive
                waitForPlayer(out, in);

                /**
                 * 
                 * 
                 * 
                 */
                while(true)
                {
                    playerTurn(out, objectOut, in);

                    String done = in.readLine();

                    if(done == "EXIT")
                    {
                        break;
                    }
                }
                
                 /* 
                 * 
                 * 
                 * 
                 */

                exitGame("4", out);
            }
            if (line.equals("3")) 
            {
                out.println(line);

                String EXIT = "EXIT";
                boolean exit = false;
                String DOES_NOT_EXIST = "DOES_NOT_EXIST";
                String FULL = "FULL";

                while(true)
                {

                    //Show the list first
                    boolean temp = listGames(objectIn);

                    //If the game list is empty to begin with, then return to the main menu
                    if(temp == true)
                    {
                        exit = true;
                        break;
                    }

                    //Request to join an available game
                    String key = joinGame(out, objectOut, in, objectIn);

                    //If there are no games, or if the player wants to return to the menu
                    if(key.compareTo(EXIT) == 0)
                    {
                        exit = true;
                        break;
                    }
                    //If the game we tried to search for does not exist, or is already full
                    else if(key.compareTo(DOES_NOT_EXIST) == 0 || key.compareTo(FULL) == 0)
                    {
                        continue;
                    }

                    //If we've reached here then that means we were able to find a game
                    break;
                }
                
                if(exit == false)
                {
                    System.out.println(in.readLine());
                }

                /**
                 * 
                 * 
                 * 
                 */
                //while(true)
                //{   
                    //playerTurn(out, objectOut, in);

                    //String done = in.readLine();

                    //if(done == "EXIT")
                    //{
                        //break;
                    //}
                //}
                 /* 
                 * 
                 * 
                 */

                //exitGame("4", out);

            }
            if (line.equals("4"))
            {   
                //Request to exit
                out.println(line);
                exitGame(line, out);  
                scnr.close();
            }
        }
    }
    
    //allows user to announce they created a game
    public static void createGame(String line, PrintWriter out, BufferedReader in, ObjectOutputStream objectOut) throws IOException
    {
        out.println(line);  //Sends the choice they wanted to the Server

        objectOut.writeObject(player);  //Sends the Players details to the Server as an object

        String game = in.readLine();    //Recieved the acknowledgement that we've created a game and displays it to the player
        System.out.println(game);

    }

    //Makes a user wait to be alerted that someone has entered their game
    public static void waitForPlayer(PrintWriter out, BufferedReader in)throws IOException
    {
        System.out.println("Waiting for player...");

        //Send over the key to access the TicTacToe game
        out.println(player.getName());

        //The incoming message will be that another player has joined our game
        System.out.println(in.readLine());

    }

    //allows user to join a game
    public static String joinGame(PrintWriter out, ObjectOutputStream objectOut, BufferedReader in, ObjectInputStream objectIn) throws IOException, ClassNotFoundException
    {
        String key = "";
        String EXIT = "EXIT";
        String DOES_NOT_EXIST = "DOES_NOT_EXIST";
        String FULL = "FULL";

        boolean notReady, exists;

        System.out.println("Enter the player you want to join, or type 'Exit': ");
        key = scnr.nextLine();

        //Check and see if the player wants to go back to the main menu
        if(key.compareToIgnoreCase(EXIT) == 0)
        {
            out.println(key);   //Send a message saying we want to return to the main menu
            System.out.println("Exiting to main menu...");
            return EXIT;
        }

        out.println(key);    //Sends the game number they want to join

        exists = (Boolean) objectIn.readObject();   //Check and see if the game exists

        //The game does not exist
        if(exists == false)
        {
            System.out.println("Game does not exist, please try again...");
            return DOES_NOT_EXIST;
        }
        //The game exists, so let's see if we can join
        else
        {
            notReady = (Boolean) objectIn.readObject();

            //If the game is already ready, then the game is full of players
            if(notReady == false)
            {
                System.out.println("Cannot join, game is already in session. Please try again...");
                return FULL;
            }
            
        }
        //If the game is not ready, then we can send our details
        objectOut.writeObject(player);

        //Return the key used to access the TicTacToe game
        return key;

    }

    /**
     * 
     * 
     * 
     */
    public static void playerTurn(PrintWriter out, ObjectOutputStream objectOut, BufferedReader in) throws IOException, ClassNotFoundException
    {
        out.flush();

        while(true)
        {
            printGameTable(in);

            System.out.println(in.readLine());

            System.out.println(in.readLine());  //Read the "Pick your row and column"

            System.out.println("Before send");
            out.println(scnr.nextLine());   //Send the row
            out.println(scnr.nextLine());   //Send the column
            System.out.println("After send");

            String input = in.readLine();
            if(input == "INVALID_INPUT")
            {
                System.out.println("Invalid input, please try again...");
                continue;
            }

            //in.readLine();

            String spot = in.readLine();
            if(spot == "NOT_AVAILABLE")
            {
                System.out.println("Spot not available, please try again...");
                continue;
            }

            //in.readLine();

            String anyWin = in.readLine();
            if(anyWin == "WINNER_ONE!")
            {
                System.out.println("Player One has won the game!");
                break;
            }
            else if(anyWin == "WINNER_TWO")
            {
                System.out.println("Player Two has won the game!");
                break;
            }

            String draw = in.readLine();
            if(draw == "DRAW!")
            {
                System.out.println("DRAW!!!");
                break;
            }

            System.out.println(in.readLine());  //Waiting for next turn
            System.out.println();

            //break;  //Break here since there was nothing wrong with the choices
        }

    }
    
    public static void printGameTable(BufferedReader in)    throws IOException
    {
        System.out.println(in.readLine());  //-------------

        for(int i = 0; i < 3; i++)
        {
            System.out.println(in.readLine());  // " | "
            /*
            for(int j = 0; j < 3; j++)
            {
                System.out.println(in.readLine());  // spot + |
            }
            System.out.println(in.readLine());  //Space
            */
            System.out.println(in.readLine());  //-------------
        }
    }

    //allows user to exit the game
    public static void exitGame(String line, PrintWriter out) throws IOException
    {
        out.println(line);
        out.println(player.getName());

        System.out.println("Exiting game");
    
        cSocket.close();
        System.exit(0);
      
    }

    //lists the possible games the user can join
    public static boolean listGames(ObjectInputStream objectIn) throws ClassNotFoundException, IOException 
    {

        System.out.println("Current Games: ");

        boolean isEmpty = (Boolean) objectIn.readObject();

        if(isEmpty == true)
        {
            System.out.println("No games going on currently...");
            System.out.println();
            return isEmpty;
        }
        else
        {
            Map<String, TicTacToe> recieved = (Map<String, TicTacToe>) objectIn.readObject();
            System.out.println(Arrays.asList(recieved));
            System.out.println();
            return isEmpty;
        }
       
    }
    
}
