package Application;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import Boardgame.Board;
import Boardgame.Position;
import CHess.ChessExeption;
import CHess.ChessMatch;
import CHess.ChessPiece;
import CHess.ChessPosition;

public class Program{


    public static void main(String[] args){


        Scanner sc = new Scanner(System.in);

      ChessMatch chessMatch = new ChessMatch();
      List<ChessPiece> captured = new ArrayList<>();




      while(!chessMatch.getCheckMate()){

        try{
           UI.clearScreen();
           UI.printMatch(chessMatch, captured);
           System.out.println();
           System.out.print("Source: ");
           ChessPosition source = UI.readChessPosition(sc);

           boolean[][] possibleMoves = chessMatch.possibleMoves(source);
           UI.clearScreen();
           UI.printBoard(chessMatch.getPieces(), possibleMoves);


           System.out.println();
           System.out.println("Target: ");
           ChessPosition target = UI.readChessPosition(sc);

           

           ChessPiece capturedPiece = chessMatch.performChessmove(source, target);

           if(capturedPiece != null){
               captured.add(capturedPiece);

           }

           if(chessMatch.getPromoted() != null){
              System.out.println("Enter Piece for promotion (B /N /R /Q): ");
              String type = sc.nextLine().toUpperCase();
              while(!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q") ){
                  System.out.println("Inavlid value! Enter piece of promotion(B/N/R/Q): ");
                  type = sc.nextLine().toUpperCase();
              }
              chessMatch.replacePromotedPiece(type);
           }
        }
        catch(ChessExeption e){
            System.out.println(e.getMessage());
            sc.nextLine();
        }
        catch(InputMismatchException e){
            System.out.println(e.getMessage());
            sc.nextLine();

        }

      }

      UI.clearScreen();
      UI.printMatch(chessMatch, captured);
     

    }
}