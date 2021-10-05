import java.util.ArrayList;
import java.util.HashMap;

// represents a structure to create a minimum spanning tree
class Union {

  HashMap<GamePiece, GamePiece> reps;
  ArrayList<Edge> worklist;
  ArrayList<Edge> mst;
  int size;

  // default constructor
  Union(HashMap<GamePiece, GamePiece> reps, ArrayList<Edge> worklist,
      ArrayList<Edge> mst, int size) {
    this.reps = reps;
    this.worklist = worklist;
    this.mst = mst;
    this.size = size;
  }


  // game constructor
  Union(ArrayList<ArrayList<GamePiece>> board, ArrayList<Edge> worklist, int size) {
    this.reps = this.generateRepresentatives(board);
    this.worklist = worklist;
    this.mst = new ArrayList<Edge>();
    this.size = size;
  }

  HashMap<GamePiece, GamePiece> generateRepresentatives(ArrayList<ArrayList<GamePiece>> board) {
    HashMap<GamePiece, GamePiece> representatives = new HashMap<GamePiece, GamePiece>();

    for (ArrayList<GamePiece> aList : board) {
      for (GamePiece gp : aList) {
        representatives.put(gp, gp);
      }
    }
    return representatives;
  }


  ArrayList<Edge> createMST() {

    Edge current;

    while (this.mst.size() < size - 1) {
      current = this.worklist.remove(0);

      if (!find(current.fromNode).equals(find(current.toNode))) {
        mst.add(current);
        this.union(current.fromNode, current.toNode);
      }
    }

    return mst;
  }

  GamePiece find(GamePiece node) {

    if (this.reps.get(node).equals(node)) {
      return node;
    } else {
      return find(this.reps.get(node));
    }
  }

  void union(GamePiece gp1, GamePiece gp2) {
    this.reps.put(this.find(gp2), this.find(gp1));
  }

}
