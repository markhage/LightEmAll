import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import javalib.impworld.WorldScene;
import javalib.worldimages.CircleImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.OverlayImage;
import javalib.worldimages.Posn;
import javalib.worldimages.TextImage;
import javalib.worldimages.WorldImage;
import tester.Tester;

// Examples Class
class ExamplesLightEmAll {

  Utils u = new Utils();
  GraphUtils gu = new GraphUtils();

  ArrayList<ArrayList<GamePiece>> board1;
  ArrayList<ArrayList<GamePiece>> boardmt;
  ArrayList<GamePiece> nodes1;
  ArrayList<Edge> mst1;

  GamePiece gp1;
  GamePiece gp2;
  GamePiece gp3;
  GamePiece gp4;

  ArrayList<GamePiece> neighbours;

  HashMap<Integer, Color> palette1;

  Edge e1;
  Edge e2;
  Edge e3;
  Edge e4;

  ArrayList<Edge> edges1;
  ArrayList<Edge> edges2;
  ArrayList<Edge> mt;

  LightEmAll game1;

  Union u1;
  Union u2;
  Union u3;

  HashMap<GamePiece, GamePiece> map1;

  void initExamples() {

    HashMap<Integer, Color> palette1 = new HashMap<Integer, Color>();

    palette1.put(5, Color.BLACK);

    mt = new ArrayList<Edge>();

    mst1 = new ArrayList<Edge>(Arrays.asList(e1, e3));

    gp1 = new GamePiece(5, 2, true, false, true, false, false, 5);

    gp2 = new GamePiece(6, 6, false, true, true, true, false, 3);

    gp3 = new GamePiece(1, 2, true, false, false, false, true, 7);

    gp4 = new GamePiece(7, 2, false, true, true, false, true, 6);

    e1 = new Edge(gp1, gp2);
    e2 = new Edge(gp3, gp4);
    e3 = new Edge(gp2, gp3);
    e4 = new Edge(gp1, gp4);

    edges1 = new ArrayList<Edge>(Arrays.asList(e1, e2, e3));

    nodes1 = new ArrayList<GamePiece>(Arrays.asList(gp1, gp2, gp3));

    board1 = new ArrayList<ArrayList<GamePiece>>(Arrays.asList(nodes1));
    boardmt = new ArrayList<ArrayList<GamePiece>>();

    game1 = new LightEmAll(20, 20);

    edges2 = new ArrayList<Edge>(Arrays.asList(e1, e2));

    neighbours = new ArrayList<GamePiece>(Arrays.asList(gp1, gp2, gp3));

    map1 = new HashMap<GamePiece, GamePiece>();

    u1 = new Union(board1, edges1, 4);
    u2 = new Union(map1, edges1, mt, 4);
    u3 = new Union(boardmt, mt, 0);
  }


  void testUnion(Tester t) {
    initExamples();


    // testing generate representatives
    t.checkExpect(u1.generateRepresentatives(boardmt), map1);

    map1.put(gp1, gp1);
    map1.put(gp2, gp2);
    map1.put(gp3, gp3);

    t.checkExpect(u1.generateRepresentatives(board1), map1);


    // testing find
    map1.put(gp1, gp1);
    map1.put(gp2, gp1);
    map1.put(gp3, gp2);

    t.checkExpect(u2.find(gp1), gp1);
    t.checkExpect(u2.find(gp3), gp1);


    // testing union
    initExamples();
    map1.put(gp1, gp1);
    map1.put(gp2, gp1);
    map1.put(gp3, gp3);

    t.checkExpect(u2.find(gp3), gp3);

    u2.union(gp2, gp3);

    t.checkExpect(u2.find(gp3), gp1);
    t.checkExpect(u2.find(gp2), gp1);


  }

  // tests addEdge
  void testAddEdge(Tester t) {
    initExamples();

    t.checkExpect(edges2.contains(e3), false);
    t.checkExpect(e1.fromNode == gp1, true);

    t.checkExpect(gp2.edges.contains(e2), false);

    gp2.addEdge(e2);

    t.checkExpect(gp2.edges.contains(e2), true);

    gp2.addEdge(e4);

    t.checkExpect(gp2.edges.contains(e4), true);
    t.checkExpect(gp2.edges.size(), 4);
    t.checkExpect(gp2.edges.get(1), e3);

  }

  // tests removeEdge
  void testRemoveEdge(Tester t) {
    initExamples();

    t.checkExpect(edges1.contains(e1), true);

    gp1.addEdge(e1);

    t.checkExpect(gp1.edges.contains(e1), true);

    gp1.removeEdge(e1);

    t.checkExpect(gp1.edges.contains(e1), false);
    t.checkExpect(gp1.edges.size(), 2);

  }

  // tests rotate
  void testRotate(Tester t) {
    initExamples();

    gp1.rotate();

    t.checkExpect(gp1.left, false);
    t.checkExpect(gp1.right, true);
    t.checkExpect(gp1.top, true);
    t.checkExpect(gp1.bottom, false);

  }

  // tests SamePiece
  boolean testSamePiece(Tester t) {
    initExamples();

    return t.checkExpect(gp1.samePiece(gp2), false) &

        t.checkExpect(gp1.samePiece(gp1), true) & t.checkExpect(gp3.samePiece(gp4), false);

  }

  // tests giveStation
  void testGiveStation(Tester t) {
    initExamples();

    t.checkExpect(gp1.powerStation, false);

    gp1.giveStation();

    t.checkExpect(gp1.powerStation, true);

    t.checkExpect(gp3.powerStation, true);

    gp3.giveStation();

    t.checkExpect(gp3.powerStation, true);

  }

  // tests hasPieces
  boolean testHasPieces(Tester t) {
    initExamples();

    return t.checkExpect(e1.hasPieces(gp4, gp3), false)
        & t.checkExpect(e2.hasPieces(gp3, gp4), true) & t.checkExpect(e4.hasPieces(gp1, gp2), false)
        & t.checkExpect(e3.hasPieces(gp1, gp4), false);

  }

  // test for drawing the nodes
  void testDrawNodes(Tester t) {
    initExamples();

    GamePiece gp = new GamePiece(0, 1, true, true, true, true, false, 2);

    ArrayList<GamePiece> nodes = new ArrayList<GamePiece>(Arrays.asList(gp));

    HashMap<Integer, Color> palette = new HashMap<Integer, Color>();
    palette = u.createPalette(2);

    t.checkExpect(palette.get(2), Color.CYAN);

    WorldScene ws = new WorldScene(200, 200);
    WorldScene ws2 = new WorldScene(200, 200);
    WorldScene ws3 = new WorldScene(200, 200);

    WorldImage current = gp.drawPiece(palette);

    ws2.placeImageXY(current, 23, 53);

    t.checkExpect(ws, ws3);

    u.drawNodes(ws, nodes, palette);

    t.checkExpect(ws, ws2);

  }

  // test movePowerStation
  void testMovePowerStation(Tester t) {
    initExamples();

    GamePiece gamepiece = new GamePiece(0, 0, true, false, false, false, true, 3);
    GamePiece gamepiece2 = new GamePiece(1, 0, true, false, true, false, false, 2);
    GamePiece gamepiece3 = new GamePiece(0, 1, true, true, true, false, false, 3);
    GamePiece gamepiece4 = new GamePiece(1, 1, true, false, false, false, false, 3);


    ArrayList<GamePiece> pieces1 = new ArrayList<GamePiece>(
        Arrays.asList(gamepiece, gamepiece2, gamepiece3, gamepiece4));
    ArrayList<GamePiece> pieces2 = new ArrayList<GamePiece>(
        Arrays.asList(gamepiece, gamepiece3, gamepiece2, gamepiece4));
    ArrayList<GamePiece> pieces3 = new ArrayList<GamePiece>(
        Arrays.asList(gamepiece, gamepiece4, gamepiece2, gamepiece3));

    // automatic constructors for gamepieces, so they are needed
    Edge e = new Edge(gamepiece, gamepiece2);
    Edge e2 = new Edge(gamepiece, gamepiece3);
    Edge e3 = new Edge(gamepiece2, gamepiece4);
    Edge e4 = new Edge(gamepiece4, gamepiece3);

    t.checkExpect(u.movePowerStation("right", pieces1, 2, 1, 0, 1), 1);
    t.checkExpect(u.movePowerStation("right", pieces3, 2, 2, 1, 2), 1);
    t.checkExpect(u.movePowerStation("right", pieces2, 2, 2, 1, 2), 1);
    t.checkExpect(u.movePowerStation("b", pieces1, 2, 2, 200, 2), 200);
    t.checkExpect(u.movePowerStation("down", pieces2, 2, 2, 0, 2), 1);
    t.checkExpect(u.movePowerStation("down", pieces1, 2, 2, 0, 2), 0);
    t.checkExpect(u.movePowerStation("down", pieces3, 2, 2, 0, 2), 0);
    t.checkExpect(u.movePowerStation("right", pieces2, 2, 2, 1, 2), 1);
    t.checkExpect(u.movePowerStation("left", pieces3, 2, 2, 1, 2), 1);
    t.checkExpect(u.movePowerStation("left", pieces1, 2, 2, 1, 2), 1);
    t.checkExpect(u.movePowerStation("left", pieces2, 2, 2, 1, 2), 2);
    t.checkExpect(u.movePowerStation("up", pieces1, 2, 2, 1, 2), 1);
    t.checkExpect(u.movePowerStation("up", pieces2, 2, 2, 1, 2), 1);
    t.checkExpect(u.movePowerStation("up", pieces3, 2, 2, 1, 2), 1);

  }

  // tests onTick
  void testOnTick(Tester t) {
    initExamples();

    LightEmAll lem1 = new LightEmAll(board1, nodes1, mst1, 3, 3, 3, new Random(), 2, 5, false,
        palette1);

    lem1.onTick();

    t.checkExpect(lem1.gameOver, true);
    t.checkExpect(lem1.time, 6);

    lem1.gameOver = true;

    lem1.onTick();

    t.checkExpect(lem1.gameOver, true);
    t.checkExpect(lem1.time, 6);

    lem1.onTick();

    t.checkExpect(lem1.time, 6);

  }

  // tests onKeyEvent
  void testOnKeyEvent(Tester t) {
    initExamples();

    LightEmAll lem1 = new LightEmAll(board1, nodes1, mst1, 3, 3, 3, new Random(), 2, 5, false,
        palette1);

    lem1.onKeyEvent("");

    t.checkExpect(lem1.u.movePowerStation("", nodes1, 10, 10, 1, 3), 1);

    lem1.onKeyEvent("b");

    t.checkExpect(lem1.u.movePowerStation("b", nodes1, 10, 10, 5, 3), 5);

  }

  // tests clearEdges
  void testClearEdges(Tester t) {
    initExamples();

    GamePiece gp = new GamePiece(0, 1, true, true, true, true, false, 2);

    ArrayList<Edge> edges = new ArrayList<Edge>(
        Arrays.asList(new Edge(gp, new GamePiece(1, 0, true, true, true, false, true, 1))));

    gp.edges = edges;

    gp.clearEdges();

    t.checkExpect(edges.size(), 0);

    ArrayList<Edge> edges2 = new ArrayList<Edge>(
        Arrays.asList(new Edge(gp, new GamePiece(1, 0, true, true, true, false, true, 1))));

    gp.edges = edges2;

    gp.clearEdges();

    t.checkExpect(edges.size(), 2);
    t.checkExpect(edges2.size(), 0);


  }

  // tests removeStation
  void testRemoveStation(Tester t) {
    initExamples();

    GamePiece gp = new GamePiece(0, 1, true, true, true, true, false, 2);

    gp.removeStation();

    t.checkExpect(gp.powerStation, false);

    gp.powerStation = true;

    gp.removeStation();

    t.checkExpect(gp.powerStation, false);


  }

  // tests powerUp
  void testPowerUp(Tester t) {
    initExamples();

    t.checkExpect(gp1.powerStation, false);
    t.checkExpect(gp1.power, 5);

    gp1.giveStation();
    gp1.powerUp(1);


    t.checkExpect(gp1.powerStation, true);
    t.checkExpect(gp1.power, 1);

  }

  // tests powerDown
  void testPowerDown(Tester t) {
    initExamples();

    t.checkExpect(gp2.powerStation, false);
    t.checkExpect(gp2.power, 3);

    gp2.giveStation();
    gp2.powerDown();

    t.checkExpect(gp2.powerStation, true);
    t.checkExpect(gp2.power, 0);

  }

  // tests createPalette, creating a hashmap with colors depending on radius
  void testCreatePalette(Tester t) {
    initExamples();

    HashMap<Integer, Color> palette = new HashMap<Integer, Color>();

    palette = u.createPalette(2);

    t.checkExpect(palette.get(2), Color.CYAN);

    palette = u.createPalette(0);

    t.checkExpect(palette.get(1), Color.cyan);
    t.checkExpect(palette.get(2), null);
    t.checkExpect(palette.get(3), null);

  }

  // tests findStationIdx
  void testFindStationIdx(Tester t) {
    initExamples();

    GamePiece gp = new GamePiece(0, 1, true, true, true, true, false, 2);
    GamePiece gp2 = new GamePiece(1, 0, false, true, true, false, true, 3);

    ArrayList<GamePiece> nodes = new ArrayList<GamePiece>(Arrays.asList(gp, gp2));

    u.findStationIdx(nodes);

    t.checkExpect(u.findStationIdx(nodes), 1);
    t.checkExpect(nodes.get(1), gp2);


  }

  // tests lightEmUp
  void testLightEmUp(Tester t) {
    initExamples();

    GamePiece gp = new GamePiece(0, 1, true, true, true, true, false, 2);
    GamePiece gp2 = new GamePiece(1, 0, false, true, true, false, true, 3);

    ArrayList<GamePiece> nodes = new ArrayList<GamePiece>(Arrays.asList(gp, gp2));

    u.lightEmUp(nodes, 3);

    t.checkExpect(gp.power, 0);
    t.checkExpect(gp2.power, 3);

    gp.power = 5;
    gp.power = 0;

    u.lightEmUp(nodes, 2);

    t.checkExpect(gp.power, 0);
    t.checkExpect(gp2.power, 2);

    gp.powerStation = true;

    u.lightEmUp(nodes, 7);

    t.checkExpect(gp.power, 7);

  }

  // tests getNeighbors
  void testGetNeighbors(Tester t) {

    GamePiece gp = new GamePiece(0, 1, true, true, true, true, false, 2);
    GamePiece gp2 = new GamePiece(1, 0, false, true, true, false, true, 3);

    // edge constructor automatically updates gp.
    Edge edge = new Edge(gp, gp2);
    ArrayList<GamePiece> neighbors = u.getNeighbors(gp);

    t.checkExpect(neighbors.get(0), gp2);
    t.checkExpect(u.getNeighbors(gp2), new ArrayList<GamePiece>());

  }

  // tests drawScore
  void testDrawScore(Tester t) {

    String text = "Moves: " + String.valueOf(2) + " Time: " + String.valueOf(0);
    String text2 = "Press enter to restart";

    WorldImage textImage = new TextImage(text, 20, Color.LIGHT_GRAY)
        .movePinholeTo(new Posn(-120, -30));
    WorldImage textImage2 = new TextImage(text2, 15, Color.LIGHT_GRAY)
        .movePinholeTo(new Posn(-120, -60));

    WorldScene ws = new WorldScene(200, 200);

    WorldScene ws2 = new WorldScene(200, 200);
    WorldScene ws3 = new WorldScene(200, 200);

    ws2.placeImageXY(textImage, 0, 0);
    ws2.placeImageXY(textImage2, 0, 0);


    t.checkExpect(ws, ws3);

    u.drawScore(ws, 2, 0);

    t.checkExpect(ws, ws2);

  }

  // tests checkGameEnd
  void testCheckGameEnd(Tester t) {
    initExamples();


    ArrayList<GamePiece> nodes = new ArrayList<GamePiece>(Arrays.asList(gp1, gp2));

    GamePiece gp = new GamePiece(0, 1, true, true, true, true, false, 0);
    GamePiece gptwo = new GamePiece(1, 0, false, true, true, false, true, 3);

    ArrayList<GamePiece> nodes2 = new ArrayList<GamePiece>(Arrays.asList(gp, gptwo));

    t.checkExpect(u.checkGameEnd(nodes), true);
    t.checkExpect(u.checkGameEnd(nodes2), false);


  }

  // tests drawGameEnd
  void testDrawGameEnd(Tester t) {

    WorldImage endText = new TextImage("You win!", 40, Color.pink);
    WorldImage endBackground = new CircleImage(40, OutlineMode.SOLID, Color.blue);

    WorldImage endImage = new OverlayImage(endText, endBackground);

    WorldScene ws = new WorldScene(200, 200);
    WorldScene ws2 = new WorldScene(200, 200);
    WorldScene ws3 = new WorldScene(200, 200);

    ws2.placeImageXY(endImage, 100, 100);

    t.checkExpect(ws, ws3);

    u.drawGameEnd(ws, 200, 200);

    t.checkExpect(ws, ws2);

  }

  // tests hasEdge
  void testHasEdge(Tester t) {

    GamePiece gp = new GamePiece(0, 1, true, true, true, true, false, 0);
    GamePiece gp2 = new GamePiece(1, 0, false, true, true, false, true, 3);

    t.checkExpect(u.hasEdge(gp, gp2), false);

    Edge e = new Edge(gp, gp2);

    t.checkExpect(u.hasEdge(gp, gp2), true);

  }

  // tests shortestPathLength in GraphUtils
  void testShortestPathLength(Tester t) {
    initExamples();

    GamePiece gamepiece = new GamePiece(0, 0, true, false, false, false, true, 3);
    GamePiece gamepiece2 = new GamePiece(1, 0, true, false, true, false, false, 2);

    Edge e = new Edge(gamepiece, gamepiece2);

    ArrayList<GamePiece> unvisited = new ArrayList<GamePiece>();
    HashMap<GamePiece, Integer> distances = new HashMap<GamePiece, Integer>();

    t.checkExpect(unvisited.size(), 0);
    unvisited.add(gamepiece);
    distances.put(gamepiece, 0);

    t.checkExpect(distances.get(gamepiece), 0);
    t.checkExpect(unvisited.size(), 1);
    t.checkExpect(gu.shortestPathLength(gamepiece, gamepiece2), 1);
    t.checkExpect(gu.shortestPathLength(gamepiece2, gamepiece), -1);
    t.checkExpect(distances.get(gamepiece2), null);

    distances.clear();
    distances.put(gamepiece2, 0);

    t.checkExpect(distances.get(gamepiece), null);
    t.checkExpect(gu.shortestPathLength(gamepiece2, gamepiece), -1);
    t.checkExpect(gu.shortestPathLength(gamepiece, gamepiece2), 1);

  }

  // tests findFurthestPiece in GraphUtils
  void testFindFurthestPiece(Tester t) {
    initExamples();

    GamePiece gamepiece = new GamePiece(0, 0, true, false, false, false, true, 3);
    GamePiece gamepiece2 = new GamePiece(1, 0, true, false, true, false, false, 2);
    GamePiece gamepiece3 = new GamePiece(5, 0, true, false, true, false, false, 2);

    // these are automatic constructors for each gamepiece, so they are needed
    Edge edge = new Edge(gamepiece, gamepiece2);
    Edge edge1 = new Edge(gamepiece, gamepiece3);
    Edge edge2 = new Edge(gamepiece2, gamepiece3);


    Queue<GamePiece> worklist = new Queue<GamePiece>(new Deque<GamePiece>());
    ArrayList<GamePiece> seen = new ArrayList<GamePiece>();

    t.checkExpect(worklist.size(), 0);

    worklist.add(gamepiece);

    t.checkExpect(gu.findFurthestPiece(gamepiece), gamepiece3);

    worklist.remove();
    worklist.add(gamepiece2);

    t.checkExpect(gu.findFurthestPiece(gamepiece2), gamepiece3);

  }

  // tests delete method
  void testDelete(Tester t) {

    GamePiece gp1 = new GamePiece(0, 0, true, false, false, false, true, 3);
    GamePiece gp2 = new GamePiece(1, 0, true, false, true, false, false, 2);
    GamePiece gp3 = new GamePiece(5, 0, true, false, true, false, false, 2);
    GamePiece gp4 = new GamePiece(1, 1, true, false, false, false, false, 3);

    Edge edgeOne = new Edge(gp1, gp2);
    Edge edgeTwo = new Edge(gp3, gp4);


    ArrayList<Edge> edges1 = new ArrayList<Edge>(Arrays.asList(edgeOne, edgeTwo));

    gp1.edges = edges1;
    gp2.edges = edges1;
    gp3.edges = edges1;
    gp4.edges = edges1;

    t.checkExpect(edgeOne.fromNode, gp1);
    t.checkExpect(edgeOne.toNode, gp2);
    t.checkExpect(edges1.get(0), edgeOne);
    t.checkExpect(edges1.contains(edgeOne), true);

    edgeOne.delete();

    t.checkExpect(edges1.get(0), edgeTwo);
    t.checkExpect(edges1.contains(edgeOne), false);

  }

  // tests initRandomize
  void testInitRandomize(Tester t) {
    initExamples();

    GamePiece gp1 = new GamePiece(0, 0, true, false, false, false, true, 3);
    GamePiece gp2 = new GamePiece(1, 0, true, false, true, false, false, 2);

    Edge e1 = new Edge(gp1, gp2);

    ArrayList<Edge> mst = new ArrayList<Edge>(Arrays.asList(e1));

    ArrayList<GamePiece> nodes = new ArrayList<GamePiece>(Arrays.asList(gp1, gp2));

    ArrayList<ArrayList<GamePiece>> board = new ArrayList<ArrayList<GamePiece>>(
        Arrays.asList(nodes));

    HashMap<Integer, Color> palette = new HashMap<Integer, Color>();

    palette = u.createPalette(2);

    LightEmAll lem1 = new LightEmAll(board, nodes, mst, 3, 3, 3, new Random(), 2, 5, false,
        palette);

    t.checkExpect(nodes.get(0), gp1);
    t.checkExpect(nodes.get(1), gp2);
    t.checkExpect(gp1.left, true);

    lem1.initRandomize();

    t.checkExpect(nodes.contains(gp1), true);

  }

  // tests initGenerateNodes
  void testInitGenerateNodes(Tester t) {
    initExamples();

    GamePiece gp1 = new GamePiece(1, 1, true, false, false, false, true, 3);
    GamePiece gp2 = new GamePiece(2, 1, true, false, true, false, false, 2);

    Edge e1 = new Edge(gp1, gp2);

    ArrayList<Edge> mst = new ArrayList<Edge>(Arrays.asList(e1));

    ArrayList<GamePiece> nodes = new ArrayList<GamePiece>(Arrays.asList(gp1, gp2));

    ArrayList<ArrayList<GamePiece>> board = new ArrayList<ArrayList<GamePiece>>(
        Arrays.asList(nodes));

    HashMap<Integer, Color> palette = new HashMap<Integer, Color>();

    palette = u.createPalette(2);

    LightEmAll lem1 = new LightEmAll(board, nodes, mst, 1, 2, 1, new Random(), 2, 5, false,
        palette);

    lem1.initGenerateNodes(board, mst, 1, 2);

    t.checkExpect(board.get(0), nodes);
  }




}