import java.net.*;
import java.io.*;
import java.util.*;


// used https://www.geeksforgeeks.org/multithreaded-servers-in-java/
public class Server {

    private static Map<String, TicTacToe> gamesList = new HashMap<>();
    private static HashSet<String> clients = new HashSet<>();

    private static TicTacToe tempObj;

    /**
     * Serves as the main method for the Program, this will keep the server running as long as
     * clients are requesting to join the server.
     * 
     * @param args      No arguments were needed to begin this program
     */
    public static void main(String[] args) throws IOException 
    {
        System.out.println("Server is up and running...");

        try
        {
            ServerSocket sSocket = new ServerSocket(7999);
            sSocket.setReuseAddress(true);

            //Run an infinite loop to accept client requests
            while (true) 
            {
                //accept connection to client socket
                Socket cSocket = sSocket.accept();
                System.out.println("New client connected" );

                //creates thread object
                ClientThread clientSocket = new ClientThread(cSocket);
                //separate thread per client
                new Thread(clientSocket).start();
          
            }
            
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        //sSocket.close();
     }

    private static class ClientThread implements Runnable
    {
        private final Socket clientSocket;

        /**
         * Constructor for our client thread which establshes a socket we can use
         * 
         * @param socket    A new client socket
         */
        public ClientThread(Socket socket) 
        {
            this.clientSocket = socket;
        }

        /**
         * A function that helps run the client socket so we can connect to the client program
         */
        public void run() 
        {
            try 
            {
                //output stream of client
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
               
                //inputstream of client
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream objectIn = new ObjectInputStream(clientSocket.getInputStream());

                String name;

                //We will ask for a username at least once
                do
                {
                    name = in.readLine();

                    //If the name already exists, then alert the client
                    if(clients.contains(name))
                    {
                        objectOut.writeObject(true);
                        out.println("Username already exists, please enter some other name: ");
                    }
                    //Add the new name to our list of clients
                    else
                    {
                        objectOut.writeObject(false);
                        out.println("Welcome " + name + "!");
                        clients.add(name);
                        break;
                    }

                }
                //If the username already exists, then we must ask again to enter a new username
                while(true);


                String line;
                
                //While the client is in the main menu of the program, then we will ask for which option they want
                while ((line = in.readLine()) != null)
                {
                    System.out.println("line is " + line);
                    //If the line is 1, then they want to see the list
                    if(line.equals("1"))
                    {
                        showList(objectOut);
                    }
                    //If the line is 2, then they want to create a new game
                    if(line.equals("2"))
                    {
                        createGame(out, objectIn);
                        String key = waitForPlayer(out, in);

                        /**
                         * 
                         * 
                         * 
                         */
                        while(true)
                        {
                            String done = playerOne(out, in);

                            out.println(done);

                            if(done == "EXIT")
                            {
                                break;
                            }
                        }
                         /*
                         * 
                         * 
                         * 
                         * 
                         */

                        //exitGame(clientSocket, in);
                        
                        
                    }
                    //If the line is 3, then they want to join a game
                    if(line.equals("3"))
                    {
                        String EXIT = "EXIT";
                        boolean exit = false;
                        String DOES_NOT_EXIST = "DOES_NOT_EXIST";
                        String FULL = "FULL";
                        String key = "";

                        while(true)
                        {
                            //Show the list first
                            boolean temp = showList(objectOut);

                            //If the game is empty tp begin with, then return to the main menu
                            if(temp == true)
                            {
                                break;
                            }

                            //Request to join an available game
                            key = joinGame(out, objectOut, in, objectIn);

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
                            out.println("You've joined the game!!!!!");
                        }
                        
                        /**
                         * 
                         * 
                         * 
                         */
                        //while(true)
                        //{
                            //String done = playerTwo(out, in);

                            //out.println(done);

                            //if(done == "EXIT")
                            //{
                                //break;
                            //}
                        //}
                         /*
                         * 
                         * 
                         * 
                         * 
                         */
                        
                        //exitGame(clientSocket, in);
                        
                    }
                    //If the line is 4, then they want to exit the program
                    if(line.equals("4"))
                    {
                        exitGame(clientSocket, in);
                    }
                }
             
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                    try 
                    {
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
    }

    /**
     * Creates a new TicTactoe game that will then be placed into a list of games so every client can see.
     * 
     * @param out       The PrintWriter for the client
     * @param objectIn  The ObjectInputStream for the client
     * @param in        The bufferedReader for the client
     * @param objectOut  The ObjectOutputStream for the client
     */
    public static void createGame(PrintWriter out, ObjectInputStream objectIn) throws ClassNotFoundException ,IOException
    {

        TicTacToe aGame = new TicTacToe();
        
        Player p = (Player)objectIn.readObject();
        p.setSignal('X');

        aGame.setPlayerOne(p);
        
        gamesList.put(p.getName(),aGame);

        out.println("You created a game! ");
        
        System.out.println(p.getName() + " created a game.");


    }

    /**
     * Serves a way for the client to wait for a second player and is alerted when a game is about to begin.
     * 
     * @param out       The PrintWriter for the client
     * @param in        The bufferedReader for the client
     */
    public static String waitForPlayer(PrintWriter out, BufferedReader in) throws IOException
    {
        //Recieve the key from the client to access the desired game
        String key = in.readLine();

        tempObj = gamesList.get(key);   //Get the game they created
        
        //We want to keep this client waiting until another client actually joins the game
        synchronized(tempObj)
        {
            try
            {
                tempObj.wait();
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }

            //Alert the client that another player has joined
            out.println("Player joined!");
        }

        return key;

    }

    public static String joinGame(PrintWriter out, ObjectOutputStream objectOut, BufferedReader in, ObjectInputStream objectIn) throws ClassNotFoundException, IOException
    {
        String EXIT = "EXIT";
        String DOES_NOT_EXIST = "DOES_NOT_EXIST";
        String FULL = "FULL";

        boolean noSecond = true;
        String choice = "";

        choice = in.readLine();   //Recieve the game choice they wanted

        //If the player wanted to return to the main menu, then we exit this method
        if(choice.compareToIgnoreCase("Exit") == 0)
        {
            return EXIT;
        }

        //Check and see if the game exists in our game list
        boolean exists = gamesList.containsKey(choice);

        //If the game doesn't exist in our list
        if(exists == false)
        {
            objectOut.writeObject(false);
            return DOES_NOT_EXIST;
        }
        //If the game exists in our list
        else
        {
            objectOut.writeObject(true);    //Send a message saying the game exists

            //The next steps decide whether or not we can join the game (Max 2 players)
            tempObj = gamesList.get(choice);

            noSecond = tempObj.noSecond();

            //if there is a second player, then the game is full of players
             if(noSecond == false)
            {
                //Send a message saying the game is full
                objectOut.writeObject(false);
                return FULL;
            }

            //Send a message saying the player can join
            objectOut.writeObject(true);

            out.println("You joined a game! \n");

            Player p = (Player)objectIn.readObject();   //Receive the player's details
            p.setSignal('O');

            tempObj.setPlayerTwo(p);

            gamesList.replace(choice, tempObj);    //Update the game with the second player for the list

            synchronized(tempObj)
            {
                tempObj.notify();
                
            }
            
        }

        //tempObj = null;

        //Return the key used to access the TicTacToe game
        return choice;
    }

    public static String playerOne(PrintWriter out, BufferedReader in) throws ClassNotFoundException, IOException
    {

        synchronized(tempObj)
        {
            while(true)
            {
                tempObj.showTheTable(out);

                out.println("Player One");

                out.println("Pick your row(1-3), and column(1-3). Example [ 1 3] ");

                System.out.println("Before reading");
                String row = in.readLine();
                System.out.println(row);
                String column = in.readLine();
                System.out.println(column);

                int r = Integer.parseInt(row);
                int c = Integer.parseInt(column);

                c -= 1;

                boolean inputGood = tempObj.checkRowColumn(r, c);

                if(inputGood == false)
                {
                    out.println("INVALID_INPUT");
                    continue;
                }

                out.println();

                r = tempObj.shiftRows(r);

                boolean available = tempObj.spotAvailable(r, c, 'O');

                if(available == false)
                {
                    out.println("NOT_AVAILABLE");
                    continue;
                }

                out.println();

                boolean win = tempObj.anyWin('X');

                if(win == true)
                {
                    out.println("WINNER_ONE!");
                    return "EXIT";
                }

                out.println();

                boolean full = tempObj.isFull();

                if(full == true)
                {
                    out.println("DRAW!");
                    return "EXIT";
                }

                out.println("Waiting for next turn...");
                out.println();

                try
                {
                    tempObj.wait();
                    return "NEXT";
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
                

            }
        }  
        
    }

    public static String playerTwo(PrintWriter out, BufferedReader in) throws ClassNotFoundException, IOException
    {
        synchronized(tempObj)
        {
            while(true)
            {
                
                tempObj.showTheTable(out);

                out.println("Player Two");

                out.println("Pick your row(1-3), and column(1-3). Example [ 1 3] ");

                int row = Integer.parseInt( (String) in.readLine());
                int column = Integer.parseInt( (String) in.readLine());

                column -= 1;

                boolean inputGood = tempObj.checkRowColumn(row, column);

                if(inputGood == false)
                {
                    out.println("Invalid input, please try again...");
                    continue;
                }

                out.println();

                row = tempObj.shiftRows(row);

                boolean available = tempObj.spotAvailable(row, column, 'O');

                if(available == false)
                {
                    out.println("Spot not available, please try again...");
                    continue;
                }

                out.println();

                boolean win = tempObj.anyWin('O');

                if(win == true)
                {
                    out.println("WINNER_TWO!");
                    return "EXIT";
                }

                out.println();

                boolean full = tempObj.isFull();

                if(full == true)
                {
                    out.println("DRAW!");
                    return "EXIT";
                }

                out.println("Waiting for next turn...");
                out.println();

                tempObj.notify();
                return "NEXT";

            }
        }
    }

    /**
     * Provides the procedure to exit the program and closes all sockets necessary. 
     */
    public static void exitGame(Socket clientSocket, BufferedReader in) throws IOException
    {
        String key = in.readLine();

        //Removes the name of the client so it can be used by someone else
        clients.remove(key);
        clientSocket.close();
    }

    /**
     * This method serves as a function that returns the list of games that are being played in the server.
     * These games can be either in sesssion, or in need of some player.
     * 
     * @param objectOut     The ObjectOutputStream for the Client
     * 
     * @return boolean      Whether or not there are any games in the list
     */
    public static boolean showList(ObjectOutputStream objectOut) throws IOException
    {
        //Check and see if there are any current games at the moment
        if(gamesList.isEmpty())
        {
            objectOut.writeObject(true);
            return true;
        }
        else
        {
            //If the list is not empty, then we return the actual list
            objectOut.writeObject(false);
            objectOut.writeObject(gamesList);
            return false;
        }

    }


} 
    

      

      

       

    
    

