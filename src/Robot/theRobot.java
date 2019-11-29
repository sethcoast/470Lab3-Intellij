package Robot;
import java.awt.event.*;
import java.awt.Color;
import java.awt.Graphics;
import java.lang.*;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


// This class draws the probability map and value iteration map that you create to the window
// You need only call updateProbs() and updateValues() from your theRobot class to update these maps
class mySmartMap extends JComponent implements KeyListener {
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int STAY = 4;

    int currentKey;

    int winWidth, winHeight;
    double sqrWdth, sqrHght;
    Color gris = new Color(170,170,170);
    Color myWhite = new Color(220, 220, 220);
    World mundo;

    int gameStatus;

    double[][] probs;
    double[][] vals;

    public mySmartMap(int w, int h, World wld) {
        mundo = wld;
        probs = new double[mundo.width][mundo.height];
        vals = new double[mundo.width][mundo.height];
        winWidth = w;
        winHeight = h;

        sqrWdth = (double)w / mundo.width;
        sqrHght = (double)h / mundo.height;
        currentKey = -1;

        addKeyListener(this);

        gameStatus = 0;
    }

    public void addNotify() {
        super.addNotify();
        requestFocus();
    }

    public void setWin() {
        gameStatus = 1;
        repaint();
    }

    public void setLoss() {
        gameStatus = 2;
        repaint();
    }

    public void updateProbs(double[][] _probs) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                probs[x][y] = _probs[x][y];
            }
        }

        repaint();
    }

    public void updateValues(double[][] _vals) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                vals[x][y] = _vals[x][y];
            }
        }

        repaint();
    }

    public void paint(Graphics g) {
        paintProbs(g);
//        paintValues(g);
    }

    public void paintProbs(Graphics g) {
        double maxProbs = 0.0;
        int mx = 0, my = 0;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (probs[x][y] > maxProbs) {
                    maxProbs = probs[x][y];
                    mx = x;
                    my = y;
                }
                if (mundo.grid[x][y] == 1) {
                    g.setColor(Color.black);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 0) {
                    //g.setColor(myWhite);

                    int col = (int)(255 * Math.sqrt(probs[x][y]));
                    if (col > 255)
                        col = 255;
                    g.setColor(new Color(255-col, 255-col, 255));
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 2) {
                    g.setColor(Color.red);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 3) {
                    g.setColor(Color.green);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }

            }
            if (y != 0) {
                g.setColor(gris);
                g.drawLine(0, (int)(y * sqrHght), (int)winWidth, (int)(y * sqrHght));
            }
        }
        for (int x = 0; x < mundo.width; x++) {
            g.setColor(gris);
            g.drawLine((int)(x * sqrWdth), 0, (int)(x * sqrWdth), (int)winHeight);
        }

        //System.out.println("repaint maxProb: " + maxProbs + "; " + mx + ", " + my);

        g.setColor(Color.green);
        g.drawOval((int)(mx * sqrWdth)+1, (int)(my * sqrHght)+1, (int)(sqrWdth-1.4), (int)(sqrHght-1.4));

        if (gameStatus == 1) {
            g.setColor(Color.green);
            g.drawString("You Won!", 8, 25);
        }
        else if (gameStatus == 2) {
            g.setColor(Color.red);
            g.drawString("You're a Loser!", 8, 25);
        }
    }

    public void paintValues(Graphics g) {
        double maxVal = -99999, minVal = 99999;
        int mx = 0, my = 0;

        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] != 0)
                    continue;

                if (vals[x][y] > maxVal)
                    maxVal = vals[x][y];
                if (vals[x][y] < minVal)
                    minVal = vals[x][y];
            }
        }
        if (minVal == maxVal) {
            maxVal = minVal+1;
        }

        int offset = winWidth+20;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 1) {
                    g.setColor(Color.black);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 0) {
                    //g.setColor(myWhite);

                    //int col = (int)(255 * Math.sqrt((vals[x][y]-minVal)/(maxVal-minVal)));
                    int col = (int)(255 * (vals[x][y]-minVal)/(maxVal-minVal));
                    if (col > 255)
                        col = 255;
                    g.setColor(new Color(255-col, 255-col, 255));
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 2) {
                    g.setColor(Color.red);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 3) {
                    g.setColor(Color.green);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }

            }
            if (y != 0) {
                g.setColor(gris);
                g.drawLine(offset, (int)(y * sqrHght), (int)winWidth+offset, (int)(y * sqrHght));
            }
        }
        for (int x = 0; x < mundo.width; x++) {
            g.setColor(gris);
            g.drawLine((int)(x * sqrWdth)+offset, 0, (int)(x * sqrWdth)+offset, (int)winHeight);
        }
    }


    public void keyPressed(KeyEvent e) {
        //System.out.println("keyPressed");
    }
    public void keyReleased(KeyEvent e) {
        //System.out.println("keyReleased");
    }
    public void keyTyped(KeyEvent e) {
        char key = e.getKeyChar();
        //System.out.println(key);

        switch (key) {
            case 'i':
                currentKey = NORTH;
                break;
            case ',':
                currentKey = SOUTH;
                break;
            case 'j':
                currentKey = WEST;
                break;
            case 'l':
                currentKey = EAST;
                break;
            case 'k':
                currentKey = STAY;
                break;
        }
    }
}


// This is the main class that you will add to in order to complete the lab
public class theRobot extends JFrame {
    // Mapping of actions to integers
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int STAY = 4;

    Color bkgroundColor = new Color(230,230,230);

    static mySmartMap myMaps; // instance of the class that draw everything to the GUI
    String mundoName;

    World mundo; // mundo contains all the information about the world.  See World.java
    double moveProb, sensorAccuracy;  // stores probabilies that the robot moves in the intended direction
    // and the probability that a sonar reading is correct, respectively

    // variables to communicate with the Server via sockets
    public Socket s;
    public BufferedReader sin;
    public PrintWriter sout;

    // variables to store information entered through the command-line about the current scenario
    boolean isManual = false; // determines whether you (manual) or the AI (automatic) controls the robots movements
    boolean knownPosition = false;
    int startX = -1, startY = -1;
    int decisionDelay = 250;

    // store your probability map (for position of the robot in this array
    double[][] probs;

    double[][] utilities;
    double[][] rewards;
    double[][] max_utilities_after_action;
    int[][] best_actions;

    // store your computed value of being in each state (x, y)
    double[][] Vs;

    // used as indices in computing probability matrices
    private List<State> states;
    // used for look up in computing probability matrices
    State[][] validStateGrid;
    // probability matrices
    // These map (given state, compute state) to their probabilities
    double[][] stateTransitionWest;
    double[][] stateTransitionEast;
    double[][] stateTransitionNorth;
    double[][] stateTransitionSouth;
    double[][] stateTransitionStay;
    double[][] measurementProbabilityMatrix;

    double[][] msmtProbability;

    public theRobot(String _manual, int _decisionDelay) {
        states = new ArrayList<>();
        // initialize variables as specified from the command-line
        if (_manual.equals("automatic"))
            isManual = false;
        else
            isManual = true;
        decisionDelay = _decisionDelay;

        // get a connection to the server and get initial information about the world
        initClient();

        // Read in the world
        mundo = new World(mundoName);

        // set up the GUI that displays the information you compute
        int width = 500;
        int height = 500;
        int bar = 20;
        setSize(width,height+bar);
        getContentPane().setBackground(bkgroundColor);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, width, height+bar);
        myMaps = new mySmartMap(width, height, mundo);
        getContentPane().add(myMaps);

        setVisible(true);
        setTitle("Probability and Value Maps");

        doStuff(); // Function to have the robot move about its world until it gets to its goal or falls in a stairwell
    }

    // this function establishes a connection with the server and learns
    //   1 -- which world it is in
    //   2 -- it's transition model (specified by moveProb)
    //   3 -- it's sensor model (specified by sensorAccuracy)
    //   4 -- whether it's initial position is known.  if known, its position is stored in (startX, startY)
    public void initClient() {
        int portNumber = 3333;
        String host = "localhost";

        try {
            s = new Socket(host, portNumber);
            sout = new PrintWriter(s.getOutputStream(), true);
            sin = new BufferedReader(new InputStreamReader(s.getInputStream()));

            mundoName = sin.readLine();
            moveProb = Double.parseDouble(sin.readLine());
            sensorAccuracy = Double.parseDouble(sin.readLine());
            System.out.println("Need to open the mundo: " + mundoName);
            System.out.println("moveProb: " + moveProb);
            System.out.println("sensorAccuracy: " + sensorAccuracy);

            // find out of the robots position is know
            String _known = sin.readLine();
            if (_known.equals("known")) {
                knownPosition = true;
                startX = Integer.parseInt(sin.readLine());
                startY = Integer.parseInt(sin.readLine());
                System.out.println("Robot's initial position is known: " + startX + ", " + startY);
            }
            else {
                System.out.println("Robot's initial position is unknown");
            }
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }

    // function that gets human-specified actions
    // 'i' specifies the movement up
    // ',' specifies the movement down
    // 'l' specifies the movement right
    // 'j' specifies the movement left
    // 'k' specifies the movement stay
    int getHumanAction() {
        System.out.println("Reading the action selected by the user");
        while (myMaps.currentKey < 0) {
            try {
                Thread.sleep(50);
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        int a = myMaps.currentKey;
        myMaps.currentKey = -1;

        System.out.println("Action: " + a);

        return a;
    }

    private void valueIteration() {
        double[][] new_utils = new double[mundo.width][mundo.height];
        double epsilon = 0.1;
        double gamma = 0.995;
        double delta;

        do {
            delta = 0;
            // loop over all states
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (isValidState(x,y)) {
                        double max_a_utility = Double.NEGATIVE_INFINITY;
                        // calculate the utility of each action, given the current state, and save the max
                        for (int action = 0; action < 5; action++) {
                            double a_utility = get_utility_for_action(x,y,action);
                            if (a_utility > max_a_utility) {
                                max_a_utility = a_utility;
                            }
                        }
                        new_utils[x][y] = rewards[x][y] + gamma * max_a_utility;
                        double diff = Math.abs(new_utils[x][y]-utilities[x][y]);
                        if (diff > delta) {
                            delta = diff;
                        }
                    } else {
                        new_utils[x][y] = rewards[x][y];
                    }
                }
            }
            update_utilities(new_utils);
            myMaps.updateValues(utilities);
        } while (delta >= (epsilon*(1-gamma))/gamma);

        System.out.println("value iteration done");
    }

    private void update_utilities(double[][] new_utils) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                utilities[x][y] = new_utils[x][y];
            }
        }
    }

    private double get_utility_for_action(int x, int y, int action) {
        double missProb = (1-moveProb)/4;
        double northProb = action == NORTH ? moveProb : missProb;
        double southProb = action == SOUTH ? moveProb : missProb;
        double eastProb = action == EAST ? moveProb : missProb;
        double westProb = action == WEST ? moveProb : missProb;
        double stayProb = action == STAY ? moveProb : missProb;

        double utility = 0;
        utility += northProb*utilities[x][y-1];
        utility += southProb*utilities[x][y+1];
        utility += stayProb*utilities[x][y];
        utility += eastProb*utilities[x+1][y];
        utility += westProb*utilities[x-1][y];
        return utility;
    }

    // initializes the probabilities of where the AI is
    void initializeProbabilities() {
        probs = new double[mundo.width][mundo.height];
        Vs = new double[mundo.width][mundo.height];
        validStateGrid = new State[mundo.height][mundo.width];

        // if the robot's initial position is known, reflect that in the probability map
        if (knownPosition) {
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if ((x == startX) && (y == startY))
                        probs[x][y] = 1.0;
                    else
                        probs[x][y] = 0.0;

                    // create valid state matrix
                    if (mundo.grid[x][y] == 0) {
                        State newState = new State(x, y);
                        states.add(newState);
                        validStateGrid[x][y] = newState;
                    }
                }
            }
        }
        else {  // otherwise, set up a uniform prior over all the positions in the world that are open spaces
            int count = 0;

            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0) {
                        count++;
                        State newState = new State(x, y);
                        states.add(newState);
                        validStateGrid[x][y] = newState;
                    }
                }
            }

            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0)
                        probs[x][y] = 1.0 / count;
                    else
                        probs[x][y] = 0;
                }
            }
        }

        myMaps.updateProbs(probs);
    }

    private double[][] initStateTransitionMatrix(int action) {
        double[][] transitionMatrix = new double[states.size()][states.size()];
        double missProb = (1-moveProb)/4;
        for (int i = 0; i < states.size(); i++) {
            int x = states.get(i).x;
            int y = states.get(i).y;
            // left state transition
            double selfProb = action == STAY ? moveProb : missProb;;
            // prob of visiting left state given self and action
            double prob = action == WEST ? moveProb : missProb;
            if (isValidState(x-1, y)) {
                transitionMatrix[i][states.indexOf(validStateGrid[x-1][y])] = prob;
            } else if (isWall(x-1, y)) {
                selfProb += prob;
            }
            // prob of visiting right state given self and action
            prob = action == EAST ? moveProb : missProb;
            if (isValidState(x+1, y)) {
                transitionMatrix[i][states.indexOf(validStateGrid[x+1][y])] = prob;
            } else if (isWall(x+1, y)) {
                selfProb += prob;
            }
            // prob of visiting up state given self and action
            prob = action == NORTH ? moveProb : missProb;
            if (isValidState(x, y-1)) {
                transitionMatrix[i][states.indexOf(validStateGrid[x][y-1])] = prob;
            } else if (isWall(x, y-1)) {
                selfProb += prob;
            }
            // prob of visiting down state given self and action
            prob = action == SOUTH ? moveProb : missProb;
            if (isValidState(x, y+1)) {
                transitionMatrix[i][states.indexOf(validStateGrid[x][y+1])] = prob;
            } else if (isWall(x, y+1)) {
                selfProb += prob;
            }
            transitionMatrix[i][i] = selfProb;
        }
        return transitionMatrix;
    }

    private void initMsmtProbabilityMatrix() {
        //Treat order of measurements as Up, Down, Left, Right
        String[] measurementPossibilities = {"0000","0001","0010","0100","1000","1100","1010","1001","0110","0101","0011","0111","1011","1101","1110","1111"};
        measurementProbabilityMatrix = new double[states.size()][16];
        double correctMeasurement = sensorAccuracy;
        double incorrectMeasurement = (1-sensorAccuracy);
        for(int i =0; i < states.size(); i++) {
            //Loop through every state
            int x = states.get(i).x;
            int y = states.get(i).y;
            //Loop through every possible measurement
            for(int j=0; j<16; j++) {
                String possibleMeasurement = measurementPossibilities[j];
                double probabilityIJ = 0;
                //Up
                if (isWall(x, y-1)) {
                    if (possibleMeasurement.charAt(0) == '1') {
                        probabilityIJ += correctMeasurement;
                    }
                    else {
                        probabilityIJ += incorrectMeasurement;
                    }
                }
                else {
                    if (possibleMeasurement.charAt(0) == '0') {
                        probabilityIJ += correctMeasurement;
                    }
                    else {
                        probabilityIJ += incorrectMeasurement;
                    }
                }
                //Down
                if (isWall(x, y+1)) {
                    if (possibleMeasurement.charAt(1) == '1') {
                        probabilityIJ *= correctMeasurement;
                    }
                    else {
                        probabilityIJ *= incorrectMeasurement;
                    }
                }
                else {
                    if (possibleMeasurement.charAt(1) == '0') {
                        probabilityIJ *= correctMeasurement;
                    }
                    else {
                        probabilityIJ *= incorrectMeasurement;
                    }
                }
                //Left
                if (isWall(x-1, y)) {
                    if (possibleMeasurement.charAt(3) == '1') {
                        probabilityIJ *= correctMeasurement;
                    }
                    else {
                        probabilityIJ *= incorrectMeasurement;
                    }
                }
                else {
                    if (possibleMeasurement.charAt(3) == '0') {
                        probabilityIJ *= correctMeasurement;
                    }
                    else {
                        probabilityIJ *= incorrectMeasurement;
                    }
                }
                //Right
                if (isWall(x+1, y)) {
                    if (possibleMeasurement.charAt(2) == '1') {
                        probabilityIJ *= correctMeasurement;
                    }
                    else {
                        probabilityIJ *= incorrectMeasurement;
                    }
                }
                else {
                    if (possibleMeasurement.charAt(2) == '0') {
                        probabilityIJ *= correctMeasurement;
                    }
                    else {
                        probabilityIJ *= incorrectMeasurement;
                    }
                }
                //Add probability of I, Jth entry to probability matrix
                measurementProbabilityMatrix[i][j] = probabilityIJ;
            }
        }
        /*
        System.out.println("Measurement Probability Matrix: ");
        for (int i=0; i<states.size(); i++)
        {
            for (int j=0; j<16; j++){
                System.out.print(measurementProbabilityMatrix[i][j]);
                System.out.print(",");
            }
            System.out.print("\n");
        }
         */
    }


    boolean isValidState(int x, int y) {
        return mundo.grid[x][y] == 0;
    }

    boolean isWall(int x, int y) {
        return mundo.grid[x][y] == 1;
    }


    // TODO: update the probabilities of where the AI thinks it is based on the action selected and the new sonar readings
    //       To do this, you should update the 2D-array "probs"
    // Note: sonars is a bit string with four characters, specifying the sonar reading in the direction of North, South, East, and West
    //       For example, the sonar string 1001, specifies that the sonars found a wall in the North and West directions, but not in the South and East directions
    void updateProbabilities(int action, String sonars) {
        // your code
        String[] measurementPossibilities = {"0000","0001","0010","0100","1000","1100","1010","1001","0110","0101","0011","0111","1011","1101","1110","1111"};
        List<String> measurementList = Arrays.asList(measurementPossibilities);

        double[][] belPrime = new double[mundo.width][mundo.height];
        double[][] newBels = new double[mundo.width][mundo.height];
        for (int st = 0; st < states.size(); st++)
        {
            double belPrimeSt = 0;
            for (int _1 = 0; _1 < states.size(); _1++)
            {
                if (action == NORTH)
                {
                    double temp = (stateTransitionNorth[_1][st])*probs[states.get(_1).x][states.get(_1).y];
                    belPrimeSt += temp;
                }
                else if (action == SOUTH)
                {
                    double temp = (stateTransitionSouth[_1][st])*probs[states.get(_1).x][states.get(_1).y];
                    belPrimeSt += temp;
                }
                else if (action == EAST)
                {
                    double temp = (stateTransitionEast[_1][st])*probs[states.get(_1).x][states.get(_1).y];
                    belPrimeSt += temp;
                }
                else if (action == WEST)
                {
                    double temp = (stateTransitionWest[_1][st])*probs[states.get(_1).x][states.get(_1).y];
                    belPrimeSt += temp;
                }
                else if (action == STAY)
                {
                    double temp = (stateTransitionStay[_1][st])*probs[states.get(_1).x][states.get(_1).y];
                    belPrimeSt += temp;
                }
            }
            belPrime[states.get(st).x][states.get(st).y]= belPrimeSt;

            int indexOfMeasurement = measurementList.indexOf(sonars);
            if(indexOfMeasurement != -1)
            {
                double belSt = measurementProbabilityMatrix[st][indexOfMeasurement] * belPrimeSt;
                newBels[states.get(st).x][states.get(st).y] = belSt;
            }
            else
            {
                newBels[states.get(st).x][states.get(st).y] = 0;
            }
        }

        double sumNewBels = 0;
        for (int i =0; i < newBels.length; i++)
        {
            for (int j =0; j < newBels.length; j++)
            {
                sumNewBels += newBels[i][j];
            }
        }
        for (int i =0; i < newBels.length; i++)
        {
            for (int j =0; j < newBels.length; j++)
            {
                probs[i][j] = newBels[i][j]/sumNewBels;
            }
        }
        myMaps.updateProbs(probs); // call this function after updating your probabilities so that the
        //  new probabilities will show up in the probability map on the GUI
    }

    // This is the function you'd need to write to make the robot move using your AI;
    // You do NOT need to write this function for this lab; it can remain as is
    int automaticAction() {

        //Algorithm 1

        /*
        // loop over all states, finding the state with the maximum (state utility * probability)
        double max_util = Double.NEGATIVE_INFINITY;
        int best_action = STAY;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (isValidState(x, y)) {
                    double max_util_from_state = max_utilities_after_action[x][y] * probs[x][y];
                    if (max_util_from_state > max_util) {
                        max_util = max_util_from_state;
                        best_action = best_actions[x][y];
                    }
                }
            }
        }

        return best_action;  // default action for now
        */

        //Algorithm 2
        //Find max prob
        double max_prob = 0;
        int mp_x = 0;
        int mp_y = 0;
        int best_action = NORTH;
        for (int y = 0; y < mundo.height; y++)
        {
            for (int x = 0; x < mundo.width; x++)
            {
                double prob = probs[x][y];
                if(prob > max_prob)
                {
                    max_prob = prob;
                    mp_x = x;
                    mp_y = y;
                }
            }
        }
        if(isValidState(mp_x, mp_y))
        {
            best_action = best_actions[mp_x][mp_y];
        }

        return best_action;
    }

    private void initializeBestActionsAndMaxUtilities() {
        max_utilities_after_action = new double[mundo.width][mundo.height];
        best_actions = new int[mundo.width][mundo.height];
        // loop over all states, computing "best action" and the utility of said action for each state
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (isValidState(x, y)) {
                    double max_a_utility = 0;
                    int best_action_for_state = -1;
                    // calculate the utility of each action, given the current state, and save the max
                    for (int action = 0; action < 5; action++) {
                        double a_utility = get_utility_for_action(x, y, action);
                        if (a_utility > max_a_utility) {
                            max_a_utility = a_utility;
                            best_action_for_state = action;
                        }
                    }
                    best_actions[x][y] = best_action_for_state;
                    max_utilities_after_action[x][y] = max_a_utility;
                }
            }
        }
    }

    private void initializeRewards() {
        rewards = new double[mundo.width][mundo.height];
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 3) {
                    rewards[x][y] = 600;
                } else if (mundo.grid[x][y] == 0) {
                    rewards[x][y] = -1;
                } else if (mundo.grid[x][y] == 2) {
                    rewards[x][y] = -10;
                }
            }
        }
    }

    void initializeUtilities()
    {
        utilities = new double[mundo.width][mundo.height];
        for(int i =0; i < mundo.width; i++)
        {
            for(int j=0; j<mundo.height; j++)
            {
                Random r = new Random();
                double randomValue = 10 * r.nextDouble();
                utilities[i][j] = randomValue;
            }
        }
    }

    void doStuff() {
        int action;

        initializeProbabilities();  // Initializes the location (probability) map
        // initialize state transition probabilities matrices
        stateTransitionWest = initStateTransitionMatrix(WEST);
        stateTransitionEast = initStateTransitionMatrix(EAST);
        stateTransitionNorth = initStateTransitionMatrix(NORTH);
        stateTransitionSouth = initStateTransitionMatrix(SOUTH);
        stateTransitionStay = initStateTransitionMatrix(STAY);

        // These 4 methods dictate decision policy
        initializeRewards();
        initializeUtilities();
//        utilities = rewards.clone();
        valueIteration();  // TODO: function you will write in Part II of the lab
        initializeBestActionsAndMaxUtilities();

        // initialize measurement probability
        initMsmtProbabilityMatrix();

        while (true) {
            try {
                if (isManual)
                    action = getHumanAction();  // get the action selected by the user (from the keyboard)
                else
                    action = automaticAction(); // TODO: get the action selected by your AI;
                // you'll need to write this function for part III

                sout.println(action); // send the action to the Server

                // get sonar readings after the robot moves
                String sonars = sin.readLine();
                //System.out.println("Sonars: " + sonars);

                updateProbabilities(action, sonars); // TODO: this function should update the probabilities of where the AI thinks it is

                if (sonars.length() > 4) {  // check to see if the robot has reached its goal or fallen down stairs
                    if (sonars.charAt(4) == 'w') {
                        System.out.println("I won!");
                        myMaps.setWin();
                        break;
                    }
                    else if (sonars.charAt(4) == 'l') {
                        System.out.println("I lost!");
                        myMaps.setLoss();
                        break;
                    }
                }
                else {
                    // here, you'll want to update the position probabilities
                    // since you know that the result of the move as that the robot
                    // was not at the goal or in a stairwell
                }
                Thread.sleep(decisionDelay);  // delay that is useful to see what is happening when the AI selects actions
                // decisionDelay is specified by the send command-line argument, which is given in milliseconds
            }
            catch (IOException e) {
                System.out.println(e);
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // java theRobot [manual/automatic] [delay]
    public static void main(String[] args) {
        theRobot robot = new theRobot(args[0], Integer.parseInt(args[1]));  // starts up the robot
    }

    class State {
        int x;
        int y;

        State(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            State state = (State) obj;
            return obj.getClass() == State.class && this.x == state.x && this.y == state.y;
        }
    }
}