package CHess;

import Boardgame.Position;

public class ChessPosition {

    private char column;
    private int row;

    public ChessPosition(char column, int row){

        if(column < 'a' || column > 'h' || row < 1 || row > 8){

            throw new ChessExeption("Error instantianing ChessPosition. Valid values are from a1 to h8 ");

        }
        this.column = column;
        this.row = row;
    }

    public char getColumn(){
        return column;
    }

    public int getRow(){
        return row;
    }

    //matrix_row = 8 -chess_row;
    // 'a' - 'a' = 0
    //'b' - 'a' = 1;
    //matrix-column = chess_column - 'a';

    //# quer dizer protected
    protected Position toPosition(){

        return new Position(8-row, column - 'a');

    }
    //sublinhado embaixo quer dizer que tem static
    protected static ChessPosition fromPosition(Position position){
        return new ChessPosition((char)('a'+ position.getColumn()), 8-position.getRow());

    }

    @Override
    public String toString(){
        return " "+ column + row;
    }




    
}
