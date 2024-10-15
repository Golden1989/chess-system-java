package CHess;

import Boardgame.Board;
import Boardgame.Piece;
import Boardgame.Position;

public abstract class ChessPiece extends Piece {

    private Color color;
    private int moveCount;


    public ChessPiece(Board board, Color color){
        super(board);//repassa a chamada para a super classe
        this.color=color;
    }

    public Color getColor(){
        return color;
    }
    public int getMoveCount(){
        return moveCount;
    }
    public void increadeMoveCount(){
        moveCount++;
    }
    public void descreaseMoveCount(){
        moveCount--;
    }

    public ChessPosition getChessPosition(){
        return ChessPosition.fromPosition(position);
    }

    protected boolean isThereOpponentPiece(Position position){
        ChessPiece p = (ChessPiece)getBoard().piece(position);

        return p != null && p.getColor() != color;
    }

    
}
