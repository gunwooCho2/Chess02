import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class Piece {
    public boolean whiteTeam;
    protected String name;
    public abstract ArrayList<Position> canMove(Map<Position, Boolean> posBool, Position pos, boolean white);
    public abstract ArrayList<Position> canHunt(Map<Position, Boolean> posBool, Position pos, boolean white);
}
class Pawn extends Piece {
    boolean firstMove = true;
    private ArrayList<Position> Hunts = new ArrayList<>();
    private ArrayList<Position> moves = new ArrayList<>();
    Position checkPosition;
    public Pawn(boolean whiteTeam) {
        name = "Pawn";
        this.whiteTeam = whiteTeam;
    }
    @Override
    public ArrayList<Position> canMove(Map<Position, Boolean> posBool, Position pos, boolean white) {
        moves.clear();
        ArrayList<Position> pawnMoves = white?
                                        firstMove?
                                        new ArrayList<>(Arrays.asList(new Position(0, 1), new Position(0, 2))):
                                        new ArrayList<>(List.of(new Position(0, 1))):
                                        firstMove?
                                        new ArrayList<>(Arrays.asList(new Position(0, -1), new Position(0, -2))):
                                        new ArrayList<>(List.of(new Position(0, -1)));
        checkPosition = white?pos.add(new Position(0, 1)):pos.add(new Position(0, -1));
        for(Position p : pawnMoves) {
            Position addPos = pos.add(p);
            if(!posBool.containsKey(checkPosition) && addPos.checkPosition()) {
                moves.add(addPos);
            }
        }
        return moves;
    }
    @Override
    public ArrayList<Position> canHunt(Map<Position, Boolean> posBool, Position pos, boolean white) {
        Hunts.clear();
        ArrayList<Position> pawnHunts = white?
                                        new ArrayList<>(Arrays.asList(new Position(1, 1), new Position(-1, 1))):
                                        new ArrayList<>(Arrays.asList(new Position(1, -1), new Position(-1, -1)));
        for(Position p : pawnHunts) {
            Position addPos = pos.add(p);
            if(posBool.containsKey(addPos) && addPos.checkPosition()) {
                if(posBool.get(addPos) != white) {
                    Hunts.add(addPos);
                }
            }
        }
        return Hunts;
    }
}

class Rook extends Piece {
    static Position[] rookMoves = {new Position(1, 0), new Position(-1, 0), new Position(0, 1), new Position(0, -1)};
    private ArrayList<Position> Hunts = new ArrayList<>();
    private ArrayList<Position> moves = new ArrayList<>();
    boolean firstMove = true;
    public Rook(boolean whiteTeam) {
        name = "Look";
        this.whiteTeam = whiteTeam;
    }
    @Override
    public ArrayList<Position> canMove(Map<Position, Boolean> posBool, Position pos, boolean white) {
        moves.clear();
        Hunts.clear();
        for (Position rookMove : rookMoves) {
            Position addpos = pos;
            while (true){
                addpos = addpos.add(rookMove);
                if (addpos.checkPosition()) {
                    if (!posBool.containsKey(addpos)) {
                        moves.add(addpos);
                    }
                    else if (posBool.get(addpos) != white) {
                        Hunts.add(addpos);
                        break;
                    }
                    else{
                        break;
                    }
                }
                else {
                    break;
                }
            }
        }
        return moves;
    }
    @Override
    public ArrayList<Position> canHunt(Map<Position, Boolean> posBool, Position pos, boolean white) {
        return Hunts;
    }
}
class Knight extends Piece {
    static Position[] knightMoves = {new Position(1, 2), new Position(-1, 2), new Position(1, -2), new Position(-1, -2),
                                   new Position(2, 1), new Position(2, -1), new Position(-2, 1), new Position(-2, -1)};
    private ArrayList<Position> Hunts = new ArrayList<>();
    private ArrayList<Position> moves = new ArrayList<>();
    public Knight(boolean whiteTeam) {
        name = "Knight";
        this.whiteTeam = whiteTeam;
    }
    @Override
    public ArrayList<Position> canMove(Map<Position, Boolean> posBool, Position pos, boolean white) {
        moves.clear();
        Hunts.clear();
        for (Position knightMove : knightMoves) {
            Position addpos = pos.add(knightMove);
            if (addpos.checkPosition()) {
                if (!posBool.containsKey(addpos)) {
                    moves.add(addpos);
                }
                else if (posBool.get(addpos) != white) {
                    Hunts.add(addpos);
                }
            }
        }
        return moves;
    }
    @Override
    public ArrayList<Position> canHunt(Map<Position, Boolean> posBool, Position pos, boolean white) {
        return Hunts;
    }
}
class Bishop extends Piece {
    static Position[] bishopMoves = {new Position(1, 1), new Position(-1, 1), new Position(1, -1), new Position(-1, -1)};
    private ArrayList<Position> Hunts = new ArrayList<>();
    private ArrayList<Position> moves = new ArrayList<>();
    public Bishop(boolean whiteTeam) {
        name = "Bishop";
        this.whiteTeam = whiteTeam;
    }
    @Override
    public ArrayList<Position> canMove(Map<Position, Boolean> posBool, Position pos, boolean white) {
        moves.clear();
        Hunts.clear();
        for (Position bishopMove : bishopMoves) {
            Position addpos = pos;
            while (true){
                addpos = addpos.add(bishopMove);
                if (addpos.checkPosition()) {
                    if (!posBool.containsKey(addpos)) {
                        moves.add(addpos);
                    }
                    else if (posBool.get(addpos) != white) {
                        Hunts.add(addpos);
                        break;
                    }
                    else{
                        break;
                    }
                }
                else {
                    break;
                }
            }
        }
        return moves;
    }
    @Override
    public ArrayList<Position> canHunt(Map<Position, Boolean> posBool, Position pos, boolean white) {
        return Hunts;
    }
}
class Queen extends Piece {
    static Position[] queenMoves = {new Position(1, 1), new Position(-1, 1), new Position(1, -1), new Position(-1, -1),
                                    new Position(1, 0), new Position(-1, 0), new Position(0, 1), new Position(0, -1)};
    private ArrayList<Position> Hunts = new ArrayList<>();
    private ArrayList<Position> moves = new ArrayList<>();
    public Queen(boolean whiteTeam) {
        name = "Queen";
        this.whiteTeam = whiteTeam;
    }
    @Override
    public ArrayList<Position> canMove(Map<Position, Boolean> posBool, Position pos, boolean white) {
        moves.clear();
        Hunts.clear();
        for (Position queenMove : queenMoves) {
            Position addpos = pos;
            while (true){
                addpos = addpos.add(queenMove);
                if (addpos.checkPosition()) {
                    if (!posBool.containsKey(addpos)) {
                        moves.add(addpos);
                    }
                    else if (posBool.get(addpos) != white) {
                        Hunts.add(addpos);
                        break;
                    }
                    else{
                        break;
                    }
                }
                else {
                    break;
                }
            }
        }
        return moves;
    }
    @Override
    public ArrayList<Position> canHunt(Map<Position, Boolean> posBool, Position pos, boolean white) {
        return Hunts;
    }
}

class King extends Piece {
    static Position[] kingMoves = {new Position(1, 1), new Position(-1, 1), new Position(1, -1), new Position(-1, -1),
                                   new Position(1, 0), new Position(-1, 0), new Position(0, 1), new Position(0, -1)};
    private ArrayList<Position> Hunts = new ArrayList<>();
    private ArrayList<Position> moves = new ArrayList<>();
    boolean firstMove = true;
    public King(boolean whiteTeam) {
        name = "King";
        this.whiteTeam = whiteTeam;
    }
    @Override
    public ArrayList<Position> canMove(Map<Position, Boolean> posBool, Position pos, boolean white) {
    	moves.clear();
    	Hunts.clear();
        for (Position kingMove : kingMoves) {
            Position addpos = pos.add(kingMove);
            if (addpos.checkPosition()) {
                if (!posBool.containsKey(addpos)) {
                    moves.add(addpos);
                }
                else if (posBool.get(addpos) != white) {
                    Hunts.add(addpos);
                }
            }
        }
        return moves;
    }
    @Override
    public ArrayList<Position> canHunt(Map<Position, Boolean> posBool, Position pos, boolean white) {
        return Hunts;
    }
}

