import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;





class LightEmAll extends World {
  // a list of columns of GamePieces,
  // i.e., represents the board in column-major order
  ArrayList<ArrayList<GamePiece>> board;
  // a list of all nodes
  ArrayList<GamePiece> nodes;
  // a list of edges of the minimum spanning tree
  ArrayList<Edge> mst;
  // the width and height of the board
  int width;
  int height;
  int radius;

  // for randomly rotating pieces at start of game
  Random rand;

  // calculating the screenwidth and height
  int screenWidth;
  int screenHeight;


  // EXTRA CREDIT FIELDS
  // keeps track of the moves made and time so far
  int moves;
  int time;

  // is the game over? used to end and restart the game
  boolean gameOver;

  // color pallete for dimming
  HashMap<Integer, Color> palette;

  // default constructor
  LightEmAll(ArrayList<ArrayList<GamePiece>> board, ArrayList<GamePiece> nodes, ArrayList<Edge> mst,
      int width, int height, int radius, Random rand, int moves, int time, boolean gameOver,
      HashMap<Integer, Color> palette) {
    this.board = board;
    this.nodes = nodes;
    this.mst = mst;
    this.width = width;
    this.height = height;
    this.radius = radius;
    this.rand = rand;
    this.screenWidth = width * 42 + 4;
    this.screenHeight = height * 42 + 46;
    this.moves = moves;
    this.time = time;
    this.gameOver = gameOver;
    this.palette = palette;

    if (this.screenWidth < 400) {
      this.screenWidth = 400;
    }

  }

  // player generation board
  LightEmAll(int width, int height) {
    this.rand = new Random();
    this.width = width;
    this.height = height;
    this.board = initGenerateBoard(width, height);
    this.mst = initGenerateMST(this.board, width, height);
    this.nodes = initGenerateNodes(this.board, this.mst, width, height);
    this.initGenerateEdges(nodes);
    this.radius = initGenerateRadius(this.nodes);
    this.screenWidth = width * 42 + 4;
    this.screenHeight = height * 42 + 76;
    this.moves = 0;
    this.time = 0;
    this.gameOver = false;
    this.palette = new Utils().createPalette(this.radius);

    if (this.screenWidth < 300) {
      this.screenWidth = 300;
    }


    // to randomize nodes
    //this.initRandomize();
    this.initGenerateEdges(nodes);
  }

  // testing random generation
  LightEmAll(int width, int height, Random rand) {
    this.rand = rand;
    this.width = width;
    this.height = height;
    this.board = initGenerateBoard(width, height);
    this.mst = initGenerateMST(this.board, width, height);
    this.nodes = initGenerateNodes(this.board, this.mst, width, height);
    this.initGenerateEdges(nodes);
    this.radius = initGenerateRadius(this.nodes);
    this.screenWidth = width * 42 + 4;
    this.screenHeight = height * 42 + 76;
    this.moves = 0;
    this.time = 0;
    this.gameOver = false;
    this.palette = new Utils().createPalette(this.radius);

    if (this.screenWidth < 300) {
      this.screenWidth = 300;
    }


    // to randomize nodes
    //this.initRandomize();
    this.initGenerateEdges(nodes);
  }


  Utils u = new Utils();
  GraphUtils gu = new GraphUtils();

  // creates the manually generated board
  ArrayList<ArrayList<GamePiece>> initGenerateBoard(int width, int height) {

    ArrayList<ArrayList<GamePiece>> fullBoard = new ArrayList<ArrayList<GamePiece>>();



    for (int i = 1; i <= width; i++) {
      ArrayList<GamePiece> tempCol = new ArrayList<GamePiece>();
      for (int j = 1; j <= height; j++) {
        tempCol.add(new GamePiece(j, i, false, false, false, false, false, 0));
      }

      fullBoard.add(tempCol);

    }

    fullBoard.get(0).get(0).giveStation();

    return fullBoard;


  }



  // creates a random minimum spanning tree on a given board
  ArrayList<Edge> initGenerateMST(ArrayList<ArrayList<GamePiece>> board, int width, int height) {
    ArrayList<Edge> allEdges = gu.getAllEdges(board, width, height, this.rand);
    u.sortEdges(allEdges);
    Union union = new Union(board, allEdges, width * height);
    return union.createMST();

  }



  // turns a manually generated board into a single list of gamepieces.
  // rotates each piece randomly between 0 and 3 times
  // Effect: adds fractal connection pattern
  ArrayList<GamePiece> initGenerateNodes(ArrayList<ArrayList<GamePiece>> board, ArrayList<Edge> mst,
      int width, int height) {
    ArrayList<GamePiece> nodes = new ArrayList<GamePiece>();
    int fromIdx;
    int toIdx;


    for (ArrayList<GamePiece> aList : board) {
      for (GamePiece gp : aList) {
        nodes.add(gp);
      }
    }

    for (Edge e : mst) {
      fromIdx = e.getFromIdx(height);
      toIdx = e.getToIdx(height);

      if (fromIdx + 1 == toIdx) {
        nodes.get(fromIdx).updateBottom(true);
        nodes.get(toIdx).updateTop(true);
      }

      if (fromIdx + height == toIdx) {
        nodes.get(fromIdx).updateRight(true);
        nodes.get(toIdx).updateLeft(true);
      }

      if (fromIdx - 1 == toIdx) {
        nodes.get(toIdx).updateBottom(true);
        nodes.get(fromIdx).updateTop(true);
      }

      if (fromIdx - height == toIdx) {
        nodes.get(toIdx).updateRight(true);
        nodes.get(fromIdx).updateLeft(true);
      }

    }

    return nodes;
  }




  //determines which gamepieces are connected, adds edges to the connected pieces
  ArrayList<Edge> initGenerateEdges(ArrayList<GamePiece> nodes) {
    ArrayList<Edge> mst = new ArrayList<Edge>();
    GamePiece currentGP;
    GamePiece toCheck;
    for (GamePiece gp : nodes) {
      gp.clearEdges();
    }
    for (int i = 0; i < nodes.size(); i++) {
      currentGP = nodes.get(i);
      if (currentGP.left) {
        if (currentGP.col > 1) {
          toCheck = nodes.get(i - this.height);
          if (toCheck.right) {
            mst.add(new Edge(currentGP, toCheck));
            mst.add(new Edge(toCheck, currentGP));
          }
        }
      }
      if (currentGP.right) {
        if (currentGP.col < this.width) {
          toCheck = nodes.get(i + this.height);
          if (toCheck.left) {
            mst.add(new Edge(currentGP, toCheck));
            mst.add(new Edge(toCheck, currentGP));
          }
        }
      }
      if (currentGP.top) {
        if (currentGP.row > 1) {
          toCheck = nodes.get(i - 1);
          if (toCheck.bottom) {
            mst.add(new Edge(currentGP, toCheck));
            mst.add(new Edge(toCheck, currentGP));
          }
        }
      }
      if (currentGP.bottom) {
        if (currentGP.row < this.height) {
          toCheck = nodes.get(i + 1);
          if (toCheck.top) {
            mst.add(new Edge(currentGP, toCheck));
            mst.add(new Edge(toCheck, currentGP));
          }
        }
      }

    }


    u.lightEmUp(nodes, this.radius);
    return mst;
  }

  // FOR PART 2
  // determines the radius of the powerstation by using bfs to find the diameter
  int initGenerateRadius(ArrayList<GamePiece> nodes) {
    int psIdx = u.findStationIdx(nodes);
    GamePiece furthest1 = gu.findFurthestPiece(nodes.get(psIdx));
    GamePiece furthest2 = gu.findFurthestPiece(furthest1);

    int diameter = gu.shortestPathLength(furthest1, furthest2);

    return (diameter + 3) / 2;

  }



  // rotates each piece a random amount of times
  void initRandomize() {
    for (GamePiece gp : this.nodes) {
      for (int i = rand.nextInt(3); i > 0; i--) {
        gp.rotate();
      }
    }
  }



  /*
   * 
   * 
   * BIG BANG HANDLERS
   * 
   */





  // draws this LigthEmALl
  public WorldScene makeScene() {
    WorldScene ws = new WorldScene(this.screenWidth, this.screenHeight);
    ws.placeImageXY(
        new RectangleImage(this.screenWidth, this.screenHeight, OutlineMode.SOLID, Color.black),
        this.screenWidth / 2, this.screenHeight / 2);
    u.drawNodes(ws, this.nodes, this.palette);
    u.drawScore(ws, this.moves, this.time);

    if (this.gameOver) {
      u.drawGameEnd(ws, this.screenWidth, this.screenHeight);
    }

    return ws;
  }

  // handles when the player clicks
  // Effect : if player clicks a piece adds one to the moves and rotates the 
  // clicked gamepiece, updates the edges
  public void onMousePressed(Posn pos) {
    if (!this.gameOver) {
      if (pos.y > 76) {
        this.moves += 1;
        u.handleClick(this.nodes, pos.x, pos.y, this.width, this.height);
        u.lightEmUp(this.nodes, this.radius);
      }
    }
  }

  // FOR PART 2
  // handles arrow keys to move the powerstation, space to restart
  // Effect : moves the powerstation or restarts game
  public void onKeyEvent(String key) {

    if (key.contentEquals("enter")) {
      this.initRandomize();
      for (GamePiece gp : nodes) {
        gp.clearEdges();
        gp.powerStation = false;
      }
      this.nodes.get(0).giveStation();
      this.initGenerateEdges(this.nodes);
      this.moves = 0;
      this.time = 0;
      this.gameOver = false;
      u.lightEmUp(this.nodes, this.radius);
    }
    if (!this.gameOver) {
      this.moves = u.movePowerStation(key, this.nodes, this.width, this.height, this.moves,
          this.radius);
    }
  }

  // FOR EXTRA CREDIT
  // handles ticking the game clock
  // Effect: adds one to this time
  public void onTick() {
    if (!this.gameOver) {
      this.time += 1;
    }
    if (u.checkGameEnd(this.nodes)) {
      this.gameOver = true;
    }
  }


}



/*
 * 
 * 
 *  GAME PIECE
 * 
 * 
 * 
 */



// represents a node in lightemall
class GamePiece {
  // in logical coordinates, with the origin
  // at the top-left corner of the screen
  int row;
  int col;
  // whether this GamePiece is connected to the
  // adjacent left, right, top, or bottom pieces
  boolean left;
  boolean right;
  boolean top;
  boolean bottom;
  // whether the power station is on this piece
  boolean powerStation;

  // whether or not this piece is powered
  int power;

  // the edges connected to this gamepiece
  ArrayList<Edge> edges;

  // default constructor
  GamePiece(int row, int col, boolean left, boolean right, boolean top, boolean bottom,
      boolean powerStation, int powered, ArrayList<Edge> edges) {
    this.row = row;
    this.col = col;
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
    this.powerStation = powerStation;
    this.power = powered;
    this.edges = edges;
  }

  // no edge constructor
  GamePiece(int row, int col, boolean left, boolean right, boolean top, boolean bottom,
      boolean powerStation, int powered) {
    this.row = row;
    this.col = col;
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
    this.powerStation = powerStation;
    this.power = powered;
    this.edges = new ArrayList<Edge>();
  }

  // Effect : adds an edge to this list of edges
  void addEdge(Edge e) {
    this.edges.add(e);
  }


  // Effect : removes the given edge from this list of edges
  void removeEdge(Edge e) {
    ArrayList<Edge> temp = new ArrayList<Edge>();

    for (Edge check : this.edges) {
      if (!check.equals(e)) {
        temp.add(check);
      }
    }

    this.edges.clear();
    for (Edge putBack : temp) {
      this.edges.add(putBack);
    }
  }

  // Effect deletes all the edges in this list of edges
  void clearEdges() {
    while (this.edges.size() > 0) {
      this.edges.get(0).delete();
    }
  }



  // Effect: rotates this game piece 90 degrees clockwise
  public void rotate() {
    boolean temp = this.top;
    this.top = this.left;
    this.left = this.bottom;
    this.bottom = this.right;
    this.right = temp;
  }

  // is the given piece the same as this piece
  public boolean samePiece(GamePiece other) {
    return this.row == other.row && this.col == other.col;
  }

  // FOR PART 2
  // Effect: makes this gamepiece the powerstation
  public void giveStation() {
    this.powerStation = true;
  }

  // FOR PART 2
  // Effect: makes this gamepiece not the powerstation
  public void removeStation() {
    this.powerStation = false;
  }

  // Effect: makes this piece powered
  public void powerUp(int power) {
    this.power = power;
  }

  // Effect: turns this piece off
  public void powerDown() {
    this.power = 0;
  }

  // Effect: updates boolean connections to this piece
  public void updateConnections(boolean left, boolean right, boolean top, boolean bottom) {
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
  }

  // Effect: makes this left true or false
  public void updateLeft(boolean left) {
    this.left = left;
  }

  // Effect: makes this left true or false
  public void updateRight(boolean right) {
    this.right = right;
  }

  // Effect: makes this left true or false
  public void updateTop(boolean top) {
    this.top = top;
  }

  // Effect: makes this left true or false
  public void updateBottom(boolean bottom) {
    this.bottom = bottom;
  }

  //draws this piece
  public WorldImage drawPiece(HashMap<Integer, Color> pallete) {

    WorldImage soFar = new RectangleImage(40, 40, OutlineMode.SOLID, Color.darkGray);

    WorldImage leftConnect;
    WorldImage rightConnect;
    WorldImage topConnect;
    WorldImage bottomConnect;

    if (this.power == 0) {
      leftConnect = new RectangleImage(20, 4, OutlineMode.SOLID, Color.lightGray).movePinhole(10,
          0);
      rightConnect = new RectangleImage(20, 4, OutlineMode.SOLID, Color.lightGray).movePinhole(-10,
          0);
      topConnect = new RectangleImage(4, 20, OutlineMode.SOLID, Color.lightGray).movePinhole(0, 10);
      bottomConnect = new RectangleImage(4, 20, OutlineMode.SOLID, Color.lightGray).movePinhole(0,
          -10);
    } else {

      Color currentColor = pallete.get(this.power);

      leftConnect = new RectangleImage(20, 4, OutlineMode.SOLID, currentColor).movePinhole(10, 0);
      rightConnect = new RectangleImage(20, 4, OutlineMode.SOLID, currentColor).movePinhole(-10, 0);
      topConnect = new RectangleImage(4, 20, OutlineMode.SOLID, currentColor).movePinhole(0, 10);
      bottomConnect = new RectangleImage(4, 20, OutlineMode.SOLID, currentColor).movePinhole(0,
          -10);
    }

    if (this.left) {
      soFar = new OverlayImage(leftConnect, soFar);
    }
    if (this.right) {
      soFar = new OverlayImage(rightConnect, soFar);
    }
    if (this.top) {
      soFar = new OverlayImage(topConnect, soFar);
    }
    if (this.bottom) {
      soFar = new OverlayImage(bottomConnect, soFar);
    }

    if (this.powerStation) {
      soFar = new OverlayImage(new StarImage(12, 8, 2, OutlineMode.SOLID, Color.green), soFar);
    }

    return soFar;
  }

  // returns the idx of this node
  public int getIdx(int height) {
    return ((this.col - 1) * height) + this.row - 1;
  }
}


/*
 * 
 * 
 * EDGE
 * 
 * 
 * 
 */




// represents a weighted connection between two game pieces
class Edge {
  GamePiece fromNode;
  GamePiece toNode;
  int weight;

  // default constructor
  Edge(GamePiece fromNode, GamePiece toNode, int weight) {
    this.fromNode = fromNode;
    this.toNode = toNode;
    this.weight = weight;

    this.fromNode.addEdge(this);

  }

  /// returns the int index of the to node
  public int getToIdx(int height) {
    return this.toNode.getIdx(height);
  }

  /// returns the int index of the from node
  public int getFromIdx(int height) {
    return this.fromNode.getIdx(height);
  }

  // automatically sets weight to 1
  Edge(GamePiece fromNode, GamePiece toNode) {
    this(fromNode, toNode, 1);

    this.fromNode.addEdge(this);

  }

  // removes this edge from the lists of edges in the from and to nodes
  void delete() {
    this.fromNode.removeEdge(this);
    this.toNode.removeEdge(this);
  }


  // does this edge contain the two given game pieces?
  public boolean hasPieces(GamePiece one, GamePiece two) {
    return (this.fromNode.samePiece(one) && this.toNode.samePiece(two))
        || (this.fromNode.samePiece(two) && this.toNode.samePiece(one));
  }


}


/*
 * 
 * UTILS CLASS
 * 
 * 
 */




// Utility Class
class Utils {


  // Effect : updates the world scene to contain the game pieces
  void drawNodes(WorldScene ws, ArrayList<GamePiece> pieces, HashMap<Integer, Color> palette) {
    WorldImage currentImage;
    for (GamePiece gp : pieces) {
      currentImage = gp.drawPiece(palette);
      ws.placeImageXY(currentImage, ((gp.col - 1) * 42) + 23, (gp.row * 42) + 53);
    }
  }

  // checks if any numbers are the same between two lists
  boolean anySame(ArrayList<Integer> arrayList, ArrayList<Integer> arrayList2) {
    for (Integer i : arrayList) {
      for (Integer j : arrayList2) {
        if (i == j) {
          return true;
        }
      }
    }

    return false;

  }

  // FOR EXTRA CREDIT
  // creates a hash map with colors depending on the size of given radius
  HashMap<Integer, Color> createPalette(int radius) {
    HashMap<Integer, Color> palette = new HashMap<Integer, Color>();
    float[] hsb;
    if (radius > 1) {
      int numSteps = radius - 1;
      // 0, 255, 255 <- 102, 53, 0

      int redStep = -100 / numSteps;
      int greenStep = 200 / numSteps;
      int blueStep = 250 / numSteps;
      int currentRed;
      int currentGreen;
      int currentBlue;

      for (int i = 0; i < radius; i++) {
        currentRed = 100 + (redStep * i);
        currentGreen = 55 + (greenStep * i);
        currentBlue = 5 + (blueStep * i);

        hsb = Color.RGBtoHSB(currentRed, currentGreen, currentBlue, null);

        palette.put(i + 1, Color.getHSBColor(hsb[0], hsb[1], hsb[2]));
      }

    } else {
      hsb = Color.RGBtoHSB(0, 255, 255, null);
      palette.put(1, Color.getHSBColor(hsb[0], hsb[1], hsb[2]));
    }
    return palette;
  }

  // Effect : rotates the clicked cells
  void handleClick(ArrayList<GamePiece> nodes, int x, int y, int width, int height) {
    if (x / 42 < width) {
      x = x / 42;
      y = (y - 76) / 42;

      int currentIdx = x * height + y;
      GamePiece toCheck;
      GamePiece currentGP = nodes.get(currentIdx);
      currentGP.rotate();

      Edge currentEdge;

      // deletes the edges pointing to the current game piece
      for (GamePiece gp : this.getNeighbors(currentGP)) {
        for (int i = 0; i < gp.edges.size(); i++) {
          currentEdge = gp.edges.get(i);
          if (currentEdge.hasPieces(gp, currentGP)) {
            currentEdge.delete();
          }
        }
      }

      // deletes the edges from the current gp
      nodes.get(currentIdx).clearEdges();

      // adds back the new edges in both directions
      if (currentGP.left) {
        if (currentGP.col > 1) {
          toCheck = nodes.get(currentIdx - height);
          if (toCheck.right) {
            currentGP.edges.add(new Edge(currentGP, toCheck));
            toCheck.edges.add(new Edge(toCheck, currentGP));
          }
        }
      }
      if (currentGP.right) {
        if (currentGP.col < width) {
          toCheck = nodes.get(currentIdx + height);
          if (toCheck.left) {
            currentGP.edges.add(new Edge(currentGP, toCheck));
            toCheck.edges.add(new Edge(toCheck, currentGP));
          }
        }
      }
      if (currentGP.top) {
        if (currentGP.row > 1) {
          toCheck = nodes.get(currentIdx - 1);
          if (toCheck.bottom) {
            currentGP.edges.add(new Edge(currentGP, toCheck));
            toCheck.edges.add(new Edge(toCheck, currentGP));
          }
        }
      }
      if (currentGP.bottom) {
        if (currentGP.row < height) {
          toCheck = nodes.get(currentIdx + 1);
          if (toCheck.top) {
            currentGP.edges.add(new Edge(currentGP, toCheck));
            toCheck.edges.add(new Edge(toCheck, currentGP));
          }
        }
      }
    }
  }



  // PART TWO
  // adds one to the moves if the powerstation is moved
  // Effect : moves the powerstation based on the given string(key) input
  int movePowerStation(String key, ArrayList<GamePiece> pieces, int width, int height, int moves,
      int radius) {
    int currentIdx = this.findStationIdx(pieces);

    if (key.equals("left") && currentIdx - height >= 0
        && this.hasEdge(pieces.get(currentIdx), pieces.get(currentIdx - height))) {
      pieces.get(currentIdx - height).giveStation();
      pieces.get(currentIdx).removeStation();
      moves++;
    }
    if (key.equals("right") && currentIdx + height < pieces.size()
        && this.hasEdge(pieces.get(currentIdx), pieces.get(currentIdx + height))) {
      pieces.get(currentIdx + height).giveStation();
      pieces.get(currentIdx).removeStation();
      moves++;
    }
    if (key.equals("up") && currentIdx % height != 0
        && this.hasEdge(pieces.get(currentIdx), pieces.get(currentIdx - 1))) {
      pieces.get(currentIdx - 1).giveStation();
      pieces.get(currentIdx).removeStation();
      moves++;
    }
    if (key.equals("down") && (currentIdx + 1) % height != 0
        && this.hasEdge(pieces.get(currentIdx), pieces.get(currentIdx + 1))) {
      pieces.get(currentIdx + 1).giveStation();
      pieces.get(currentIdx).removeStation();
      moves++;
    }

    this.lightEmUp(pieces, radius);
    return moves;
  }

  // does this list of edges contain an edge with both the given pieces
  boolean hasEdge(GamePiece current, GamePiece next) {
    for (Edge e : current.edges) {
      if (e.hasPieces(current, next)) {
        return true;
      }
    }
    return false;
  }



  // PART TWO
  // returns the index of the piece with the station
  int findStationIdx(ArrayList<GamePiece> pieces) {
    GamePiece currentGP;
    for (int i = 0; i < pieces.size(); i++) {
      currentGP = pieces.get(i);
      if (currentGP.powerStation) {
        return i;
      }
    }
    throw new RuntimeException("there is no power station");
  }


  // Effect: powers up the pieces connected to the power station
  void lightEmUp(ArrayList<GamePiece> nodes, int radius) {
    int powerStationIdx = this.findStationIdx(nodes);
    int distance;
    GraphUtils gu = new GraphUtils();


    for (GamePiece gp : nodes) {
      distance = gu.shortestPathLength(gp, nodes.get(powerStationIdx));
      if (distance == -1 || distance > radius) {
        gp.powerDown();
      } else {
        gp.powerUp(radius - distance);
      }
    }
  }

  // returns all the neighbors of a given gamepiece
  ArrayList<GamePiece> getNeighbors(GamePiece gp) {

    ArrayList<GamePiece> neighbors = new ArrayList<GamePiece>();

    for (Edge e : gp.edges) {
      neighbors.add(e.toNode);
    }

    return neighbors;
  }

  // EXTRA CREDIT
  // Effect : draws the score onto the world scene
  void drawScore(WorldScene ws, int moves, int time) {
    String text = "Moves: " + String.valueOf(moves) + " Time: " + String.valueOf(time / 10);
    String text2 = "Press enter to restart";
    WorldImage textImage = new TextImage(text, 20, Color.lightGray)
        .movePinholeTo(new Posn(-120, -30));
    WorldImage textImage2 = new TextImage(text2, 15, Color.lightGray)
        .movePinholeTo(new Posn(-120, -60));
    ws.placeImageXY(textImage, 0, 0);
    ws.placeImageXY(textImage2, 0, 0);
  }


  // returns true if all the game pieces are powered
  boolean checkGameEnd(ArrayList<GamePiece> pieces) {
    for (GamePiece gp : pieces) {
      if (gp.power == 0) {
        return false;
      }
    }
    return true;
  }

  // Effect: adds a "you win" to the world scene
  void drawGameEnd(WorldScene ws, int screenWidth, int screenHeight) {

    WorldImage endText = new TextImage("You win!", 40, Color.pink);
    WorldImage endBackground = new CircleImage(40, OutlineMode.SOLID, Color.blue);

    WorldImage endImage = new OverlayImage(endText, endBackground);

    ws.placeImageXY(endImage, screenWidth / 2, screenHeight / 2);
  }





  //Effect : sorts a list of edges from low to high using heapsort
  public void sortEdges(ArrayList<Edge> edges) {
    int size = edges.size();

    for (int i = size / 2 - 1; i >= 0; i--) {
      heapify(edges, size, i);
    }

    for (int i = size - 1; i >= 0; i--) {
      this.swap(edges, i, 0);

      heapify(edges, i, 0);
    }
  }


  // Effect : heaps a list of edges
  void heapify(ArrayList<Edge> edges, int size, int i) {
    int largest = i;
    int left = (2 * i) + 1;
    int right = (2 * i) + 2;

    if (left < size && edges.get(left).weight > edges.get(largest).weight) {
      largest = left;
    }

    if (right < size && edges.get(right).weight > edges.get(largest).weight) {
      largest = right;
    }

    if (largest != i) {
      this.swap(edges, i, largest);

      heapify(edges, size, largest);
    }
  }

  // swaps the two items at the given indices
  <T> void swap(ArrayList<T> arr, int index1, int index2) {
    T oldValueAtIndex2 = arr.get(index2);

    arr.set(index2, arr.get(index1));
    arr.set(index1, oldValueAtIndex2);
  }

}




// represents a way to hold data
interface ICollection<T> {
  void add(T t);

  T remove();

  int size();
}

// add and remove items at the head
class Stack<T> implements ICollection<T> {
  Deque<T> items;

  Stack(Deque<T> items) {
    this.items = items;
  }

  public void add(T t) {
    this.items.addAtHead(t);
  }

  public T remove() {
    return this.items.removeFromHead();
  }

  public int size() {
    return this.items.size();
  }
}

// add items to the head, remove from the tail
class Queue<T> implements ICollection<T> {
  Deque<T> items;

  Queue(Deque<T> items) {
    this.items = items;
  }

  public void add(T t) {
    this.items.addAtHead(t);
  }

  public T remove() {
    return this.items.removeFromTail();
  }

  public int size() {
    return this.items.size();
  }
}


/*
 * 
 *  GRAPH UTILS
 * 
 * 
 * 
 */





// utilities class for graph related functions
class GraphUtils {
  //gets the shortest path (in cost/weight) from one given node to another
  int shortestPathLength(GamePiece source, GamePiece target) {
    ArrayList<GamePiece> worklist = new ArrayList<GamePiece>();
    HashMap<GamePiece, Integer> distances = new HashMap<GamePiece, Integer>();

    worklist.add(source);
    distances.put(source, 0);

    while (worklist.size() > 0) {
      GamePiece gp = worklist.remove(0);
      for (Edge e : gp.edges) {
        if (distances.get(e.toNode) == null
            || distances.get(e.toNode) > distances.get(gp) + e.weight) {
          distances.put(e.toNode, distances.get(gp) + e.weight);
          worklist.add(e.toNode);
        }
      }
    }


    if (distances.get(target) == null) {
      return -1;
    } else {
      return distances.get(target);
    }

  }

  //  finds the furthest game piece from a given game piece
  GamePiece findFurthestPiece(GamePiece start) {
    Queue<GamePiece> worklist = new Queue<GamePiece>(new Deque<GamePiece>());
    ArrayList<GamePiece> seen = new ArrayList<GamePiece>();
    Utils u = new Utils();

    worklist.add(start);

    while (worklist.size() > 0) {
      GamePiece next = worklist.remove();
      if (seen.contains(next)) {
        //does nothing, next has been processed already
      } else {
        for (GamePiece gp : u.getNeighbors(next)) {
          worklist.add(gp);
        }
        seen.add(next);
      }
    }
    return seen.get(seen.size() - 1);
  }


  // produces a list of all possible undirected edges in a game board
  ArrayList<Edge> getAllEdges(ArrayList<ArrayList<GamePiece>> board, int width, int height,
      Random rand) {
    ArrayList<Edge> edges = new ArrayList<Edge>();
    GamePiece current;
    GamePiece below;
    GamePiece right;

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        current = board.get(i).get(j);

        if (j < height - 1) {
          below = board.get(i).get(j + 1);
          edges.add(new Edge(current, below, rand.nextInt()));
        }
        if (i < width - 1) {
          right = board.get(i + 1).get(j);
          edges.add(new Edge(current, right, rand.nextInt()));
        }
      }
    }

    return edges;
  }
}


/* 
 * 
 * EXAMPLES CLASS
 * 
 * 
 * 
 */




class RunLightEmAll {
  void testLightEmAll(Tester t) {
    LightEmAll lea = new LightEmAll(10, 10);
    lea.bigBang(lea.screenWidth, lea.screenHeight, 0.1);
  }
}

