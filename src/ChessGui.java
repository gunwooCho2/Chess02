import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ChessGui extends JFrame {
	private final String[] piecesString = new String[] { "Rook", "Knight", "Bishop", "Queen" };
	private ChessShell selectShell = null;
	private ArrayList<Position> canMoves;
	private ArrayList<Position> canHunts;
	private Map<Piece, Position> piecePos = new HashMap<>();
	private final Map<Position, ChessShell> shellPos = new HashMap<>();
	private boolean whiteTurn = true;
	private final ArrayList<Position> changedShellPos = new ArrayList<>();
	private boolean checkForEnemy = false;
	private boolean checkForMe = false;
	private King whiteKing;
	private King blackKing;
	private boolean check = false;
	private String selectPiece = null;
	private Rook whiteKingSideRook;
	private Rook whiteQueenSideRook;
	private Rook blackKingSideRook;
	private Rook blackQueenSideRook;
	private Map<Position, String> castlingMap = new HashMap<>();
	private Map<Position, Boolean> enPassantMap;
	private boolean mateCheck = false;

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
							if (canMoves.contains(chessShell.getPosition())) {
								move(selectShell, chessShell);
							} else if (canHunts.contains(chessShell.getPosition())) {
								hunt(selectShell, chessShell);
							} else if (castlingMap.containsKey(chessShell.getPosition())) {
								String castlingString = castlingMap.get(chessShell.getPosition());
								switch (castlingString) {
									case "WhiteKingSideCastling": {
										whiteKingSideCastling();
										break;
									 }case "WhiteQueenSideCastling": {
										whiteQueenSideCastling();
										break;
									 }case "BlackKingSideCastling": {
										blackKingSideCastling();
										break;
									 }case "BlackQueenSideCastling": {
										blackQueenSideCastling();
										break;
										}
									}
								} 
							for (Position changedPos : changedShellPos) {
								shellPos.get(changedPos).setbackGround();
							}
							changedShellPos.clear();
						selectShell = null;
						}
						 else {
							if (piece != null) {
								if (piece.whiteTeam == whiteTurn) {
									if (piece instanceof King) {
										checkCastling(chessShell);
									}
									Map<Position, Boolean> posBool = new HashMap<>();
									for (Map.Entry<Piece, Position> entry : piecePos.entrySet()) {
										posBool.put(entry.getValue(), entry.getKey().whiteTeam);
									}
									canMoves = piece.canMove(posBool, chessShell.getPosition(), whiteTurn);
									for (Position canMove : canMoves) {
										shellPos.get(canMove).setBackground(Color.blue);
										changedShellPos.add(canMove);
									}
									canHunts = piece.canHunt(posBool, chessShell.getPosition(), whiteTurn);
									for (Position canHunt : canHunts) {
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
		for (int i = 0; i < 2; i++) {
			int y = white ? 2 : 9 - 2;
			for (int j = 1; j < 9; j++) {
				Position pos = new Position(j, y);
				ChessShell chessShell = shellPos.get(pos);
				Pawn pawn = new Pawn(white);
				piecePos.put(pawn, pos);
				chessShell.setPiece(pawn);
			}
			y = white ? 1 : 9 - 1;
			for (int j = 1; j < 9; j += 7) {
				int x = j < 5 ? 2 : 7;
				Position pos = new Position(x, y);
				ChessShell chessShell = shellPos.get(pos);
				Knight knight = new Knight(white);
				piecePos.put(knight, pos);
				chessShell.setPiece(knight);
				x = j < 5 ? 3 : 6;
				pos = new Position(x, y);
				chessShell = shellPos.get(pos);
				Bishop bishop = new Bishop(white);
				piecePos.put(bishop, pos);
				chessShell.setPiece(bishop);
			}
			if (white) {
				Position pos = new Position(8, y);
				ChessShell chessShell = shellPos.get(pos);
				whiteKingSideRook = new Rook(white);
				piecePos.put(whiteKingSideRook, pos);
				chessShell.setPiece(whiteKingSideRook);
				pos = new Position(1, y);
				chessShell = shellPos.get(pos);
				whiteQueenSideRook = new Rook(white);
				piecePos.put(whiteQueenSideRook, pos);
				chessShell.setPiece(whiteQueenSideRook);
			} else {
				Position pos = new Position(8, y);
				ChessShell chessShell = shellPos.get(pos);
				blackKingSideRook = new Rook(white);
				piecePos.put(blackKingSideRook, pos);
				chessShell.setPiece(blackKingSideRook);
				pos = new Position(1, y);
				chessShell = shellPos.get(pos);
				blackQueenSideRook = new Rook(white);
				piecePos.put(blackQueenSideRook, pos);
				chessShell.setPiece(blackQueenSideRook);
			}
			Position pos = new Position(4, y);
			ChessShell chessShell = shellPos.get(pos);
			Queen queen = new Queen(white);
			piecePos.put(queen, pos);
			chessShell.setPiece(queen);
			pos = new Position(5, y);
			chessShell = shellPos.get(pos);
			King king = new King(white);
			if (white) {
				whiteKing = king;
			} else {
				blackKing = king;
			}
			piecePos.put(king, pos);
			chessShell.setPiece(king);
			white = !white;
		}
	}

	public void move(ChessShell departShell, ChessShell arrivalShell) {
	    Map<Piece, Position> testPos = new HashMap<>(piecePos);
	    Piece piece = departShell.getPiece();
	    Position arrivaPos = arrivalShell.getPosition();
	    
	    testPos.put(piece, arrivaPos);
	    checkCheck(testPos);
	    if (checkForMe) {
	        check = true;
	        System.out.println("체크를 당하는 움직임입니다.");
	        return;
	    } else {
	        piecePos = new HashMap<>(testPos);
	    }

	    departShell.setPiece();
	    arrivalShell.setPiece(piece);
	    
	    if (piece instanceof Pawn) {
	        ((Pawn) piece).firstMove = false;
	        if (arrivaPos.getY() == 8 || arrivaPos.getY() == 1) {
	            selectDialog();
	            Piece promotionsPiece = null;
	            switch (selectPiece) {
	                case "Knight":
	                    promotionsPiece = new Knight(piece.whiteTeam);
	                    break;
	                case "Bishop":
	                    promotionsPiece = new Bishop(piece.whiteTeam);
	                    break;
	                case "Rook":
	                    promotionsPiece = new Rook(piece.whiteTeam);
	                    break;
	                case "Queen":
	                    promotionsPiece = new Queen(piece.whiteTeam);
	                    break;
	                default:
	                    System.out.println("Unknown piece.");
	            }
	            if (promotionsPiece != null) {
	                arrivalShell.setPiece(promotionsPiece);
	                piecePos.remove(piece);
	                piecePos.put(promotionsPiece, arrivaPos);
	                checkCheck(piecePos);
	            }
	        }
	    } else if (piece instanceof Rook) {
	        ((Rook) piece).firstMove = false;
	    } else if (piece instanceof King) {
	        ((King) piece).firstMove = false;
	    }
		mateCheck = mateCheck();
		if (mateCheck) {
			checkCheck(piecePos);
			if (checkForEnemy) {
				System.out.println("체크메이트!!");
			}
			else{
				System.out.println("스테일메이트...");
			}
		}
		else if (checkForEnemy){
			System.out.println("체크!");
		}
	    whiteTurn = !whiteTurn;
	}

	public void hunt(ChessShell departShell, ChessShell arrivalShell) {
	    Map<Piece, Position> testPos = new HashMap<>(piecePos);
	    Position arrivalPos = arrivalShell.getPosition();
	    Piece departPiece = departShell.getPiece();
	    Piece arrivalPiece = arrivalShell.getPiece();

	    testPos.put(departPiece, arrivalPos);
	    testPos.remove(arrivalPiece);
	    checkCheck(testPos);
	    if (checkForMe) {
	        check = true;
	        System.out.println("체크를 당하는 움직임입니다");
	        return;
	    } else {
	        piecePos = new HashMap<>(testPos);
	    }

	    departShell.setPiece();
	    arrivalShell.setPiece(departPiece);
	    
	    if (departPiece instanceof Pawn) {
	        ((Pawn) departPiece).firstMove = false;
	        if (arrivalPos.getY() == 8 || arrivalPos.getY() == 1) {
	            selectDialog();
	            Piece promotionsPiece = null;
	            switch (selectPiece) {
	                case "Knight":
	                    promotionsPiece = new Knight(departPiece.whiteTeam);
	                    break;
	                case "Bishop":
	                    promotionsPiece = new Bishop(departPiece.whiteTeam);
	                    break;
	                case "Rook":
	                    promotionsPiece = new Rook(departPiece.whiteTeam);
	                    break;
	                case "Queen":
	                    promotionsPiece = new Queen(departPiece.whiteTeam);
	                    break;
	                default:
	                    System.out.println("Unknown piece.");
	            }
	            if (promotionsPiece != null) {
	                arrivalShell.setPiece(promotionsPiece);
	                piecePos.remove(departPiece);
	                piecePos.put(promotionsPiece, arrivalPos);
					checkCheck(piecePos);
	            }
	        }
	    } else if (departPiece instanceof Rook) {
	        ((Rook) departPiece).firstMove = false;
	    } else if (departPiece instanceof King) {
	        ((King) departPiece).firstMove = false;
	    }
		mateCheck = mateCheck();
		System.out.println(mateCheck);
		if (mateCheck) {
			if (checkForEnemy) {
				System.out.println("체크메이트!!");
			}
			else{
				System.out.println("스테일메이트...");
			}
		}
		else if (checkForEnemy){
			System.out.println("체크!");
		}
	    whiteTurn = !whiteTurn;
	}

	public void checkCheck(Map<Piece, Position> piecePos) {
		checkForMe = false;
		checkForEnemy = false;
		King king = whiteTurn ? whiteKing : blackKing;
		King enemyKing = !whiteTurn ? whiteKing : blackKing;
		Position kingPos = piecePos.get(king);
		Position enemyKingPos = piecePos.get(enemyKing);
		Map<Position, Boolean> posBool = new HashMap<>();
		for (Map.Entry<Piece, Position> entry : piecePos.entrySet()) {
			posBool.put(entry.getValue(), entry.getKey().whiteTeam);
		}
		Map<Piece, Position> enemyMap = piecePos.entrySet().stream()
				.filter(entry -> entry.getKey().whiteTeam != king.whiteTeam)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		Map<Piece, Position> ourMap = piecePos.entrySet().stream()
				.filter(entry -> entry.getKey().whiteTeam == king.whiteTeam)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		for (Map.Entry<Piece, Position> enemy : enemyMap.entrySet()) {
			canMoves = enemy.getKey().canMove(posBool, enemy.getValue(), !whiteTurn);
			canHunts = enemy.getKey().canHunt(posBool, enemy.getValue(), !whiteTurn);
			if (canHunts.contains(kingPos)) {
				checkForMe = true;
			}
		}
		for (Map.Entry<Piece, Position> our : ourMap.entrySet()) {
			canMoves = our.getKey().canMove(posBool, our.getValue(), whiteTurn);
			canHunts = our.getKey().canHunt(posBool, our.getValue(), whiteTurn);
			if (canHunts.contains(enemyKingPos)) {
				checkForEnemy = true;
			}
		}
	}
	public Boolean mateCheck(){
		boolean hasLegalMove = false;
		Map<Position, Boolean> posBool = new HashMap<>();
		Map<Piece, Position> piecePosCopy = new HashMap<>(piecePos);
		for (Map.Entry<Piece, Position> entry : piecePosCopy.entrySet()) {
			posBool.put(entry.getValue(), entry.getKey().whiteTeam);
		}
		for (Map.Entry<Piece, Position> mapEntry : piecePosCopy.entrySet()) {
			Piece piece = mapEntry.getKey();
			Position pos = mapEntry.getValue();

			if (piece.whiteTeam == whiteTurn) continue;

			ArrayList<Position> possibleMoves = piece.canMove(posBool, pos, !whiteTurn);
			ArrayList<Position> possibleHunts = piece.canHunt(posBool, pos, !whiteTurn);

			ArrayList<Position> localCanMoves = new ArrayList<>(possibleMoves);
			ArrayList<Position> localCanHunts = new ArrayList<>(possibleHunts);

			for (Position move : localCanMoves) {
				Map<Piece, Position> testPos = new HashMap<>(piecePos);
				testPos.put(piece, move);
				checkCheck(testPos);

				if (!checkForEnemy) {
					hasLegalMove = true;
					break;
				}
			}

			if (hasLegalMove) break;

			for (Position hunt : localCanHunts) {
				Map<Piece, Position> testPos = new HashMap<>(piecePos);
				Piece capturedPiece = shellPos.get(hunt).getPiece();
				if (capturedPiece != null) {
					testPos.remove(capturedPiece);
				}
				testPos.put(piece, hunt);
				checkCheck(testPos);

				if (!checkForEnemy) {
					hasLegalMove = true;
					break;
				}
			}
		}
		if (!hasLegalMove) {
			return true;
		}
		return false;
	}

	public void selectDialog() {
		selectPiece = null;
		JDialog dialog = new JDialog(this, "Promotions!", true);
		dialog.setSize(100, 200);
		JPanel selectPanel = new JPanel();
		selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.Y_AXIS));
		selectPanel.setBackground(Color.white);
		selectPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		for (String pieceString : piecesString) {
			JLabel pieceLabel = new JLabel(pieceString);
			pieceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			pieceLabel.setOpaque(true);
			pieceLabel.setBackground(Color.WHITE);
			pieceLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			pieceLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					selectPiece = pieceString;
					dialog.dispose();
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					pieceLabel.setBackground(Color.LIGHT_GRAY);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					pieceLabel.setBackground(Color.WHITE);
				}
			});
			selectPanel.add(pieceLabel);
		}

		dialog.add(selectPanel);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}
	public void checkCastling(ChessShell chessShell) {
		castlingMap = new HashMap<>();
		Piece piece = chessShell.getPiece();
		King king = (King) piece;
		Position kingPos = chessShell.getPosition();
		if (king.firstMove) {
			if (king == whiteKing) {
				if (piecePos.containsKey(whiteKingSideRook) && whiteKingSideRook.firstMove &&
					shellPos.get(kingPos.add(new Position(1, 0))).getPiece() == null &&
					shellPos.get(kingPos.add(new Position(2, 0))).getPiece() == null) {
					castlingMap.put(kingPos.add(new Position(2, 0)), "WhiteKingSideCastling");
				}
				if (piecePos.containsKey(whiteQueenSideRook) && whiteQueenSideRook.firstMove &&
					shellPos.get(kingPos.add(new Position(-1, 0))).getPiece() == null &&
					shellPos.get(kingPos.add(new Position(-2, 0))).getPiece() == null &&
					shellPos.get(kingPos.add(new Position(-3, 0))).getPiece() == null) {
					castlingMap.put(kingPos.add(new Position(-2, 0)), "WhiteQueenSideCastling");
				}
			}
			else {
				if (piecePos.containsKey(blackKingSideRook) && blackKingSideRook.firstMove &&
					shellPos.get(kingPos.add(new Position(1, 0))).getPiece() == null &&
					shellPos.get(kingPos.add(new Position(2, 0))).getPiece() == null) {
					castlingMap.put(kingPos.add(new Position(2, 0)), "BlackKingSideCastling");
				}
				if (piecePos.containsKey(blackQueenSideRook) && blackQueenSideRook.firstMove &&
					shellPos.get(kingPos.add(new Position(-1, 0))).getPiece() == null &&
					shellPos.get(kingPos.add(new Position(-2, 0))).getPiece() == null &&
					shellPos.get(kingPos.add(new Position(-3, 0))).getPiece() == null) {
					castlingMap.put(kingPos.add(new Position(-2, 0)), "BlackQueenSideCastling");
				}
			}
		}
		for (Map.Entry<Position, String>changedPosPiece : castlingMap.entrySet()) {
			Position changedPos = changedPosPiece.getKey();
			changedShellPos.add(changedPos);
			shellPos.get(changedPos).setBackground(Color.orange);
		}
	}
	public void whiteKingSideCastling() {
		Map<Piece, Position> testPos = new HashMap<>(piecePos);
		Position whiteKingPos = testPos.get(whiteKing);
		for (int i = 0; i < 3; i++) {
			testPos.put(whiteKing, whiteKingPos.add(new Position(i, 0)));
			checkCheck(testPos);
			if (checkForMe) {
				check = true;
				System.out.println("캐슬링 불가 조건입니다");
				return;
			}
		}
		shellPos.get(whiteKingPos).setPiece();
		shellPos.get(whiteKingPos.add(new Position(3, 0))).setPiece();
		Position rookPos = whiteKingPos.add(new Position(1, 0));
		whiteKingPos = whiteKingPos.add(new Position(2, 0));
		shellPos.get(whiteKingPos).setPiece(whiteKing);
		shellPos.get(rookPos).setPiece(whiteKingSideRook);
		piecePos.put(whiteKing, whiteKingPos);
		piecePos.put(whiteKingSideRook, rookPos);
		mateCheck = mateCheck();
		checkCheck(piecePos);
		if (mateCheck) {
			if (checkForEnemy) {
				System.out.println("체크메이트!!");
			}
			else{
				System.out.println("스테일메이트...");
			}
		}
		else if (checkForEnemy){
			System.out.println("체크!");
		}
		whiteKing.firstMove = false;
		whiteKingSideRook.firstMove = false;
		castlingMap.clear();
		whiteTurn = !whiteTurn;
	}
	public void whiteQueenSideCastling() {
		Map<Piece, Position> testPos = new HashMap<>(piecePos);
		Position whiteKingPos = testPos.get(whiteKing);
		for (int i = 0; i < 4; i++) {
			testPos.put(whiteKing, whiteKingPos.add(new Position(-i, 0)));
			checkCheck(testPos);
			if (checkForMe) {
				check = true;
				System.out.println("캐슬링 불가 조건입니다");
				return;
			}
		}
		shellPos.get(whiteKingPos).setPiece();
		shellPos.get(whiteKingPos.add(new Position(-4, 0))).setPiece();
		Position rookPos = whiteKingPos.add(new Position(-1, 0));
		whiteKingPos = whiteKingPos.add(new Position(-2, 0));
		shellPos.get(whiteKingPos).setPiece(whiteKing);
		shellPos.get(rookPos).setPiece(whiteQueenSideRook);
		piecePos.put(whiteKing, whiteKingPos);
		piecePos.put(whiteQueenSideRook, rookPos);
		mateCheck = mateCheck();
		checkCheck(piecePos);
		if (mateCheck) {
			if (checkForEnemy) {
				System.out.println("체크메이트!!");
			}
			else{
				System.out.println("스테일메이트...");
			}
		}
		else if (checkForEnemy){
			System.out.println("체크!");
		}
		whiteKing.firstMove = false;
		whiteQueenSideRook.firstMove = false;
		castlingMap.clear();
		whiteTurn = !whiteTurn;
	}
	public void blackKingSideCastling() {
		Map<Piece, Position> testPos = new HashMap<>(piecePos);
		Position blackKingPos = testPos.get(blackKing);
		for (int i = 0; i < 3; i++) {
			testPos.put(blackKing, blackKingPos.add(new Position(i, 0)));
			checkCheck(testPos);
			if (checkForMe) {
				check = true;
				System.out.println("캐슬링 불가 조건입니다");
				return;
			}
		}
		shellPos.get(blackKingPos).setPiece();
		shellPos.get(blackKingPos.add(new Position(3, 0))).setPiece();
		Position rookPos = blackKingPos.add(new Position(1, 0));
		blackKingPos = blackKingPos.add(new Position(2, 0));
		shellPos.get(blackKingPos).setPiece(blackKing);
		shellPos.get(rookPos).setPiece(blackKingSideRook);
		piecePos.put(blackKing, blackKingPos);
		piecePos.put(blackKingSideRook, rookPos);
		mateCheck = mateCheck();
		checkCheck(piecePos);
		if (mateCheck) {
			if (checkForEnemy) {
				System.out.println("체크메이트!!");
			}
			else{
				System.out.println("스테일메이트...");
			}
		}
		else if (checkForEnemy){
			System.out.println("체크!");
		}
		blackKing.firstMove = false;
		blackKingSideRook.firstMove = false;
		castlingMap.clear();
		whiteTurn = !whiteTurn;
	}
	public void blackQueenSideCastling() {
		Map<Piece, Position> testPos = new HashMap<>(piecePos);
		Position blackKingPos = testPos.get(blackKing);
		for (int i = 0; i < 4; i++) {
			testPos.put(blackKing, blackKingPos.add(new Position(-i, 0)));
			checkCheck(testPos);
			if (checkForMe) {
				check = true;
				System.out.println("캐슬링 불가 조건입니다");
				return;
			}
		}
		shellPos.get(blackKingPos).setPiece();
		shellPos.get(blackKingPos.add(new Position(-4, 0))).setPiece();
		Position rookPos = blackKingPos.add(new Position(-1, 0));
		blackKingPos = blackKingPos.add(new Position(-2, 0));
		shellPos.get(blackKingPos).setPiece(blackKing);
		shellPos.get(rookPos).setPiece(blackQueenSideRook);
		piecePos.put(blackKing, blackKingPos);
		piecePos.put(blackQueenSideRook, rookPos);
		mateCheck = mateCheck();
		checkCheck(piecePos);
		if (mateCheck) {
			if (checkForEnemy) {
				System.out.println("체크메이트!!");
			}
			else{
				System.out.println("스테일메이트...");
			}
		}
		else if (checkForEnemy){
			System.out.println("체크!");
		}
		blackKing.firstMove = false;
		blackQueenSideRook.firstMove = false;
		castlingMap.clear();
		whiteTurn = !whiteTurn;
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

	public void setPiece() {
		setName("");
		piece = null;
		repaint();
	}

	public void setPiece(Piece piece) {
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
			g.setColor(piece.whiteTeam ? new Color(200, 200, 200) : new Color(50, 50, 50));
			g.setFont(new Font("Arial", Font.BOLD, 16));
			FontMetrics fm = g.getFontMetrics();
			int textWidth = fm.stringWidth(piece.name);
			int textHeight = fm.getAscent();
			int x = (getWidth() - textWidth) / 2;
			int y = (getHeight() + textHeight) / 2 - fm.getDescent();
			g.drawString(piece.name, x, y);
		}
	}

	public void setbackGround() {
		if (backGround) {
			setBackground(Color.WHITE);
		} else {
			setBackground(Color.BLACK);
		}
	}
}
