package com.asliced.slidingpuzzle;

import java.util.ArrayList;

public class Board {
	private int board;
	private int nRows;
	private int nColumns;
	
	private static int[] factorial = new int[Start.nColumns * Start.nRows];
	private static int[] ten = new int[Start.nColumns * Start.nRows];
	
	public Board(int[] data, int nRows, int nColumns){
		this.nRows = nRows;
		this.nColumns = nColumns;
		
		factorial[0] = 1;
		ten[Start.nColumns * Start.nRows - 1] = 1;
		for(int i=1; i < Start.nColumns * Start.nRows; i++){
			factorial[i] = factorial(i);
			ten[Start.nColumns * Start.nRows - 1 - i] = 10 * ten[Start.nColumns * Start.nRows - i];
		}
		
		if(data != null){
			for(int i=0; i < data.length; i++){
				board += ten[i] * data[i];
			}
		}
	}
	
	public int getNRows(){
		return nRows;
	}
	
	public int getNColumns(){
		return nColumns;
	}
	
	public int getBoardData(){
		return board;
	}

	public Board getBoard(ArrayList<Integer> solution) {
		Puzzle puzz = new Puzzle(this);
		
		for(int i : solution){
			switch(i){
				case 0:
					puzz.moveLeft(false);
					break;
				case 1:
					puzz.moveRight(false);
					break;
				case 2:
					puzz.moveUp(false);
					break;
				case 3:
					puzz.moveDown(false);
					break;
			}
		}
		
		return puzz.getBoard();
	}

	public static int getBoard(int bb, int dir){
		int nb = 0;
		
		int space = -1;
		int spacen = 0;
		for(int i=Start.nColumns * Start.nRows - 1; i >= 0; i--){
			int n = bb % 10;
			nb += n * ten[i];
			bb /= 10;
			if(n == Start.nColumns * Start.nRows - 1){
				space = i;
				spacen = n;
			}
		}
		
		int swap = space;
		int swapn = 0;
		int x = (dir / 2)*Start.nColumns + 1 - dir / 2;
		swap = space + x - (dir % 2)*2*x;
		
		swapn = (nb / ten[swap]) % 10;
		nb -= (spacen - swapn) * ten[space];
		nb += (spacen - swapn) * ten[swap];
		
		return nb;
	}
	
	public static boolean isSolved(int bb) {
		for(int i=Start.nColumns * Start.nRows - 1; i >= 0; i--){
			if(bb % 10 != i){
				return false;
			}
			
			bb /= 10;
		}
		
		return true;
	}

//	public int getDistance() {
//		int distance = 0;
//		for(int i=0; i < board.length; i++){
//			int n = board[i];
//			while(n % nColumns != i % nColumns){
//				if(n % nColumns > i % nColumns){
//					n--;
//				} else{
//					n++;
//				}
//				
//				distance++;
//			}
//			
//			while(n / nRows != i / nRows){
//				if(n / nRows > i / nRows){
//					n -= nColumns;
//				} else{
//					n += nColumns;
//				}
//				
//				distance++;
//			}
//		}
//		
//		return distance;
//	}

	public static int getLexicographicPosition(int bb) {
		int pos = 0;
		for(int i=0; i < Start.nColumns * Start.nRows; i++){
			int smaller = 0;
			int bbi = bb / ten[i] % 10;
			for(int j = i + 1; j < Start.nColumns * Start.nRows; j++){
				if(bb / ten[j] % 10 < bbi){
					smaller++;
				}
			}
			pos += smaller * factorial[Start.nColumns * Start.nRows - i - 1];
		}
		
		return pos;
	}

	public static int factorial(int n){
		int fact = 1;
		for(int i=n; i > 1; i--){
			fact *= i;
		}
		
		return fact;
	}
}
