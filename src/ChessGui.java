import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ChessGui extends JFrame {
    private ChessShell selectShell = null;
    private ArrayList<Position> canMoves;
    private ArrayList<Position> canHunts;
    private Map<Piece, Position> piecePos = new HashMap<>();
    private Map<Position, ChessShell> shellPos = new HashMap<>();
    private boolean whiteTurn = true;
    private ArrayList<Position> changedShellPos = new ArrayList<>();
    private boolean check = false;
    private King whiteKing;
    private King blakeKing;

    public ChessGui() {
        boolean white = true;
        setTitle("Chess");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLayout(new BorderLayout());
        JPanel chessPane = new JPanel(new GridLayout(8, 8));
        chessPane.setSize(600, 600);
        for (int i = 8; i > 0; i--) {
            for (int j = 1; j < 9; j++) {
                Position pos = new Position(j, i);
                ChessShell chessShell = new ChessShell(pos, white);
                shellPos.put(pos, chessShell);
                chessShell.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        Piece piece = chessShell.getPiece();
                        if (selectShell != null) {
                            if (!check || piece instanceof King){
                                if (canMoves.contains(chessShell.getPosition())){
                                    move(selectShell, chessShell);
                                    selectShell = null;
                                }
                                else if (canHunts.contains(chessShell.getPosition())){
                                    hunt(selectShell, chessShell);
                                    selectShell = null;
                                }
                                else {
                                    selectShell = null;
                                    for (Position changedPos: changedShellPos) {
                                        shellPos.get(changedPos).setbackGround();
                                    }
                                    changedShellPos.clear();
                                }
                            }
                        }
                        else {
                            if (piece != null) {
                                if (piece.whiteTeam == whiteTurn) {
                                    System.out.println("마이턴 드로");
                                    Map<Position, Boolean> posBool = new HashMap<>();
                                    for(Map.Entry<Piece, Position> entry : piecePos.entrySet()) {
                                        posBool.put(entry.getValue(), entry.getKey().whiteTeam);
                                    }
                                    canMoves = piece.canMove(posBool, chessShell.getPosition(), whiteTurn);
                                    for (Position canMove: canMoves) {
                                        shellPos.get(canMove).setBackground(Color.blue);
                                        changedShellPos.add(canMove);
                                    }
                                    canHunts = piece.canHunt(posBool, chessShell.getPosition(), whiteTurn);
                                    for (Position canHunt: canHunts) {
                                        shellPos.get(canHunt).setBackground(Color.red);
                                        changedShellPos.add(canHunt);
                                    }
                                    selectShell = chessShell;
                                }
                            }
                        }
                    }
                });
                chessPane.add(chessShell);
                white = !white;
            }
            white = !white;
        }
        add(chessPane, BorderLayout.CENTER);
        setVisible(true);
        for (int i = 0; i<2; i++){
            int y = white?2:9-2;
            for (int j = 1; j<9; j++){
                Position pos = new Position(j, y);
                ChessShell chessShell = shellPos.get(pos);
                Pawn pawn = new Pawn(white);
                piecePos.put(pawn, pos);
                chessShell.setPiece(pawn);
            }
            y = white?1:9-1;
            for (int j = 1; j<9; j+=7){
                Position pos = new Position(j, y);
                ChessShell chessShell = shellPos.get(pos);
                Look look = new Look(white);
                piecePos.put(look, pos);
                chessShell.setPiece(look);
                int x = j < 5?2:7;
                pos = new Position(x, y);
                chessShell = shellPos.get(pos);
                Knight knight = new Knight(white);
                piecePos.put(knight, pos);
                chessShell.setPiece(knight);
                x = j < 5?3:6;
                pos = new Position(x, y);
                chessShell = shellPos.get(pos);
                Bishop bishop = new Bishop(white);
                piecePos.put(bishop, pos);
                chessShell.setPiece(bishop);
            }
            Position pos = new Position(4, y);
            ChessShell chessShell = shellPos.get(pos);
            Queen queen = new Queen(white);
            piecePos.put(queen, pos);
            chessShell.setPiece(queen);
            pos = new Position(5, y);
            chessShell = shellPos.get(pos);
            King king = new King(white);
            if (white){
                whiteKing = king;
            }
            else{
                blakeKing = king;
            }
            piecePos.put(king, pos);
            chessShell.setPiece(king);
            white = !white;
        }
    }
    public void move(ChessShell departShell, ChessShell arrivalShell) {
        Piece piece = departShell.getPiece();
        piecePos.put(piece, arrivalShell.getPosition());
        departShell.setPiece();
        arrivalShell.setPiece(piece);
        System.out.println(piece.name + "무빙완료.");
        if (piece instanceof Pawn) {
            ((Pawn) piece).firstMove = false;
        }
        whiteTurn = !whiteTurn;
        for (Position changedPos: changedShellPos) {
            shellPos.get(changedPos).setbackGround();
        }
        changedShellPos.clear();
    }
    public void hunt(ChessShell departShell, ChessShell arrivalShell) {
        Piece departPiece = departShell.getPiece();
        Piece arrivalPiece = arrivalShell.getPiece();
        piecePos.put(departPiece, arrivalShell.getPosition());
        piecePos.remove(arrivalPiece);
        departShell.setPiece();
        arrivalShell.setPiece(departPiece);
        System.out.println(departPiece.name + "헌팅" + arrivalPiece.name + ".");
        if (departPiece instanceof Pawn) {
            ((Pawn) departPiece).firstMove = false;
        }
        whiteTurn = !whiteTurn;
        for (Position changedPos: changedShellPos) {
            shellPos.get(changedPos).setbackGround();
        }
        changedShellPos.clear();
    }
    public void checkCheck(){
        King king = whiteTurn?whiteKing:blakeKing;
        Map<Piece, Position>checkMap = piecePos.entrySet()
                .stream()
                .filter(entry -> entry.getKey().whiteTeam != king.whiteTeam)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }
}
class ChessShell extends JPanel {
    private final Position position;
    private Piece piece;
    private boolean backGround;
    public ChessShell(Position position, boolean white) {
        this.position = position;
        backGround = white;
        setbackGround();
    }
    public Position getPosition() {
        return position;
    }
    public void setPiece(){
        setName("");
        piece = null;
        repaint();
    }
    public void setPiece(Piece piece){
        setName(piece.name);
        this.piece = piece;
        repaint();
    }
    public Piece getPiece() {
        return piece;
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (piece != null) {
            // 텍스트의 색상을 말의 팀에 따라 설정
            g.setColor(piece.whiteTeam ? new Color(200, 200, 200) : new Color(50, 50, 50));
            // 폰트 설정 (예: Arial, Bold, 16pt)
            g.setFont(new Font("Arial", Font.BOLD, 16));
            // 텍스트의 크기 계산
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(piece.name);
            int textHeight = fm.getAscent();
            // 텍스트를 중앙에 위치시키기 위한 좌표 계산
            int x = (getWidth() - textWidth) / 2;
            int y = (getHeight() + textHeight) / 2 - fm.getDescent();
            // 텍스트 그리기
            g.drawString(piece.name, x, y);
        }
    }
    public void setbackGround(){
        if (backGround) {
            setBackground(Color.WHITE);
        }
        else {
            setBackground(Color.BLACK);
        }
    }
}
