package Boardgame;

public class Board {

    private int rows;
    private int columns;
    private Piece[][] pieces;

    public Board(int rows, int columns){
        if (rows < 1 || columns < 1){
            throw new BoardExeption("Error creating board: there must be at least 1 row and 1 column" );

        }
        this.rows=rows;
        this.columns=columns;
        pieces = new Piece[rows][columns];

    }

    public int getRows(){
        return rows;
    }
    
    public int getColumns(){
        return columns;
    }
    

    public Piece piece(int row,int column){
        if(!posistionExists(row, column)){
            throw new BoardExeption("Position not on the board");
        }
        return pieces[row][column];
    }
    public Piece piece(Position position){
        if(!positionExists(position)){
            throw new BoardExeption("Position not on the board");

        }
        return pieces[position.getRow()][position.getColumn()];

    }

    public void placePiece(Piece piece, Position position){

        if(thereIsApiece(position)){
            throw new BoardExeption("There is already a piece on position");
        }

        pieces[position.getRow()][position.getColumn()] = piece;
        piece.position = position;

    }

    public Piece removePiece(Position position){
        if(!positionExists(position)){
            throw new BoardExeption("Position not on the board");

        }

        if(piece(position) == null){
            return null;
        }
        Piece aux = piece(position);
        aux.position = null;
        pieces[position.getRow()][position.getColumn()] = null;
        return aux;

    }



    private boolean posistionExists(int row, int column){
        return row >= 0 && row < rows && column >= 0 && column < columns;

    }

    public boolean positionExists(Position position){

        return posistionExists(position.getRow(), position.getColumn());

    }

    public boolean thereIsApiece(Position position){
        if(!positionExists(position)){
            throw new BoardExeption("Position not on the board");
        }

        return piece(position) != null;
    }








    
}
