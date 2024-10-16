package CHess;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import Boardgame.Board;
import Boardgame.Piece;
import Boardgame.Position;
import chess_pieces.Bishop;
import chess_pieces.King;
import chess_pieces.Knight;
import chess_pieces.Pawn;
import chess_pieces.Queen;
import chess_pieces.Rook;

public class ChessMatch {


    private int turn;
    private Color currentPlayer;
    private Board board;
    private List<Piece> piecesOnTheboard = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();
    private boolean check;
    private boolean checkMate;
    private ChessPiece enpassantVulnerable;
    private ChessPiece promoted;


    public boolean getCheckMate(){
        return checkMate;
    }

    public ChessMatch(){
       
        board = new Board(8,8);
        turn = 1;
        currentPlayer = Color.WHITE;

        initialSetup();
    }

    public int getTurn(){
        return turn;
    }

    public Color getCurrentPlayer(){
        return currentPlayer;
    }

    public boolean getCheck(){
        return check;
    }

    public ChessPiece[][] getPieces(){
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for(int i=0;i<board.getRows();i++){
            for(int j=0;j<board.getColumns();j++){
                mat[i][j] = (ChessPiece)board.piece(i,j);

            }
        }

        return mat;

    }

    public ChessPiece getEnpassantVulnerable(){
        return enpassantVulnerable;
    }
    public ChessPiece getPromoted(){
        return promoted;
    }

    public boolean[][] possibleMoves(ChessPosition sourcePosition){
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position);
        return board.piece(position).possibleMoves();
    }

    public ChessPiece performChessmove(ChessPosition sourcePosition, ChessPosition targetPosition){

        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();

        validateSourcePosition(source);
        validateTargetPosition(source, target);
        Piece capturedPiece = makemove(source, target);

        if(testCheck(currentPlayer)){
            undoMove(source, target, capturedPiece);
            throw new ChessExeption("You can´t put yourself in check");
        }

        ChessPiece movedPiece = (ChessPiece)board.piece(target);

        //# specialmove promotion
        promoted = null;
        if(movedPiece instanceof Pawn){
            if((movedPiece.getColor() == Color.WHITE && target.getRow() == 0) || (movedPiece.getColor() == Color.BLACK && target.getRow() == 7) ){
                promoted = (ChessPiece)board.piece(target);
                promoted = replacePromotedPiece("Q");
            
            
            }
        }
        




        check = (testCheck(opponent(currentPlayer))) ? true : false;
        
        
        
        
        if(testcheckMate(opponent(currentPlayer))){
            checkMate = true;

        }else{

            nextTurn();

        }

        //#special move en passant
        if(movedPiece instanceof Pawn && (target.getRow() == source.getRow()-2 || target.getRow() == source.getRow()+2)){
            enpassantVulnerable = movedPiece;
        }else{
            enpassantVulnerable = null;
        }
        
        

        return (ChessPiece) capturedPiece;
              




    }
    public ChessPiece replacePromotedPiece(String type){
        if(promoted == null){
            throw new IllegalStateException ("There is no piece to be promoted");
        }
        if(!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q") ){
           return promoted;

        }

        Position pos = promoted.getChessPosition().toPosition();
        Piece p = board.removePiece(pos);
        piecesOnTheboard.remove(p);

        ChessPiece newPiece = newPiece (type, promoted.getColor());
        board.placePiece(newPiece, pos);
        piecesOnTheboard.add(newPiece);


        return newPiece;



    }

    private ChessPiece newPiece(String type, Color color){
        if(type.equals("B")) return new Bishop(board, color); 
        if(type.equals("N")) return new Knight(board, color);
        if(type.equals("Q")) return new Queen(board, color);
        return new Rook(board, color);
    }

    private Piece makemove(Position source, Position target ){
         ChessPiece p = (ChessPiece)board.removePiece(source);
         p.increadeMoveCount();
         Piece capturedPiece = board.removePiece(target);
         board.placePiece(p, target);


         if(capturedPiece != null){
            piecesOnTheboard.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
         }
         // #special move castling king side move
         if(p instanceof King && target.getColumn() == source.getColumn() +2){
            Position sourceT = new Position(source.getRow(), source.getColumn() +3);
            Position targetT = new Position(source.getRow(), source.getColumn() +1);
            ChessPiece rook = (ChessPiece)board.removePiece(sourceT);
            board.placePiece(rook, targetT);
            rook.increadeMoveCount();
         }
          // #special move castling queen side move
          if(p instanceof King && target.getColumn() == source.getColumn() -2){
            Position sourceT = new Position(source.getRow(), source.getColumn() -4);
            Position targetT = new Position(source.getRow(), source.getColumn() -1);
            ChessPiece rook = (ChessPiece)board.removePiece(sourceT);
            board.placePiece(rook, targetT);
            rook.increadeMoveCount();
         }

         //#special move en passant 
         if(p instanceof Pawn){
            if(source.getColumn() != target.getColumn() && capturedPiece == null){
                Position pawnPosition;
                if(p.getColor() == Color.WHITE){
                    pawnPosition = new Position(target.getRow() +1, target.getColumn());

                }else{

                    pawnPosition = new Position(target.getRow() -1, target.getColumn());

                }
                capturedPiece = board.removePiece(pawnPosition);
                if(capturedPiece == enpassantVulnerable){
                    capturedPieces.add(capturedPiece);
                    piecesOnTheboard.remove(capturedPiece);
                }else{
                    throw new ChessExeption("No en passant capture avaliable");
                }
                

            }
         }

         return capturedPiece;


    }

    private void undoMove(Position source, Position target, Piece capturedPiece){

        ChessPiece p = (ChessPiece)board.removePiece(target);
        p.descreaseMoveCount();
        board.placePiece(p,source);

        if(capturedPiece != null){
            board.placePiece(capturedPiece, target);
            capturedPieces.remove(capturedPiece);
            piecesOnTheboard.add(capturedPiece);

        }


          // #special move castling king side move
          if(p instanceof King && target.getColumn() == source.getColumn() +2){
            Position sourceT = new Position(source.getRow(), source.getColumn() +3);
            Position targetT = new Position(source.getRow(), source.getColumn() +1);
            ChessPiece rook = (ChessPiece)board.removePiece(targetT);
            board.placePiece(rook, sourceT);
            rook.descreaseMoveCount();
         }
          // #special move castling queen side move
          if(p instanceof King && target.getColumn() == source.getColumn() -2){
            Position sourceT = new Position(source.getRow(), source.getColumn() -4);
            Position targetT = new Position(source.getRow(), source.getColumn() -1);
            ChessPiece rook = (ChessPiece)board.removePiece(targetT);
            board.placePiece(rook, sourceT);
            rook.descreaseMoveCount();
         }

           //#special move en passant 
           if(p instanceof Pawn){
            if(source.getColumn() != target.getColumn() && capturedPiece == enpassantVulnerable){
                ChessPiece pawn = (ChessPiece)board.removePiece(target);
                Position pawnPosition;
                if(p.getColor() == Color.WHITE){
                    pawnPosition = new Position(3, target.getColumn());

                }else{

                    pawnPosition = new Position(4, target.getColumn());

                }

                board.placePiece(pawn, pawnPosition);
                
               

            }
         }



    }

    private void validateSourcePosition(Position position){
        if(!board.thereIsApiece(position)){
            throw new ChessExeption("there is no piece on source position");


        }
        if(currentPlayer != ((ChessPiece)board.piece(position)).getColor()){
            throw new ChessExeption("The choosen piece is not yours");
        }
        if(!board.piece(position).isThereAnyPossibleMove()){
            throw new ChessExeption("Does not exist moves for the choosen piece");

        }
    }

    private void validateTargetPosition(Position source, Position target){
        if(!board.piece(source).possibleMove(target)){

            throw new ChessExeption("Rhe choosen piece can´t move to target position");

        }
    }

    private Color opponent(Color color){
        return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private ChessPiece king(Color color){
       
        List<Piece> list = piecesOnTheboard.stream().filter(x -> ((ChessPiece)x).getColor()==color).collect(Collectors.toList());
        for (Piece p: list){
            if(p instanceof King){
                return (ChessPiece)p;
            }
        }

        throw new IllegalStateException("There is no "+ color + "King on the board");
    
    
    }

    private boolean testCheck(Color color){

        Position kingPosition = king(color).getChessPosition().toPosition();
        List<Piece> opponentPieces = piecesOnTheboard.stream().filter(x -> ((ChessPiece)x).getColor()== opponent(color)).collect(Collectors.toList());
        
        for(Piece p: opponentPieces){
            boolean[][] mat = p.possibleMoves();
            if(mat[kingPosition.getRow()][kingPosition.getColumn()]){
                return true;
            }

        }

        return false;
    
    
    
    
    
    
    }

    private boolean testcheckMate(Color color){

        if(!testCheck(color)){
            return false;
        }

        List<Piece> list = piecesOnTheboard.stream().filter(x -> ((ChessPiece)x).getColor()==color).collect(Collectors.toList());
        for(Piece p: list){
            boolean [][] mat = p.possibleMoves();
            for(int i=0; i<board.getRows(); i++ ){
                for(int j=0; j<board.getColumns();j++){
                    if(mat[i][j]){
                        Position source = ((ChessPiece)p).getChessPosition().toPosition();
                        Position target = new Position(i,j);
                        Piece capturedPiece = makemove(source, target);
                        boolean testCheck = testCheck(color);
                        undoMove(source, target, capturedPiece);
                        if(!testCheck){
                            return false;
                        }

                    }

                }
            }
        }

        return true;
    }





    private void placeNewPiece(char column, int row, ChessPiece piece){
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheboard.add(piece);

    }

    private void nextTurn(){
        turn++;
        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private void initialSetup(){

       placeNewPiece('a', 1, new Rook(board, Color.WHITE));
       placeNewPiece('b', 1, new Knight(board, Color.WHITE));
       placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
       placeNewPiece('d', 1, new Queen(board, Color.WHITE));
       placeNewPiece('e', 1, new King(board, Color.WHITE, this));
       placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
       placeNewPiece('g', 1, new Knight(board, Color.WHITE));
       placeNewPiece('h', 1, new Rook(board, Color.WHITE));
       placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
       placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
       placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
       placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
       placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
       placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
       placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
       placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));



       placeNewPiece('a', 8, new Rook(board, Color.BLACK));
       placeNewPiece('b', 8, new Knight(board, Color.BLACK));
       placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
       placeNewPiece('d', 8, new Queen(board, Color.BLACK));
       placeNewPiece('e', 8, new King(board, Color.BLACK, this));
       placeNewPiece('g', 8, new Knight(board, Color.BLACK));
       placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
       placeNewPiece('h', 8, new Rook(board, Color.BLACK));
       placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
       placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
       placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
       placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
       placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
       placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
       placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
       placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));
}
       
      
    }


    
    

