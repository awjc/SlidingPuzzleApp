package com.asliced.slidingpuzzle;

import java.util.ArrayList;
import java.util.Stack;

public class GameTree {
	private Board board = null;

	public static int LEFT = 0;
	public static int RIGHT = 1;
	public static int UP = 2;
	public static int DOWN = 3;
	
	int roots;
	int rootp;
	
	private boolean[] dirs = new boolean[4];
	private static byte[] done;
	
	public ArrayList<Byte> solve(Board initialState){
		board = initialState;
		if(done == null){
			done = new byte[Board.factorial(board.getNColumns() * board.getNRows())];
		}
		
		for(int i=0; i < done.length; i++){
			done[i] = -1;
		}
		
		roots = initialState.getBoardData();
		rootp = -1;
		
		ArrayList<Byte> solution = new ArrayList<Byte>();
		if(Board.isSolved(initialState.getBoardData())){
//			System.out.println(initialState.getBoardData());
			return solution;
		}

		Stack<Integer> todo = new Stack<Integer>();
		Stack<Integer> todo1 = new Stack<Integer>();
		
		Stack<Integer> todo2 = new Stack<Integer>();
		Stack<Integer> todo3 = new Stack<Integer>();
		
		todo.push(roots);
		todo1.push(rootp);
		while(!(todo.isEmpty() || todo1.isEmpty()) || !(todo2.isEmpty() || todo3.isEmpty())){	
			while(!todo.isEmpty()){
				int state = todo.pop();
				int prevDir = todo1.pop();
				
				getPossibleDirections(state, prevDir);
				for(int i=0; i < dirs.length; i++){
					if(dirs[i]){
						int bb = Board.getBoard(state, i);
						int pos = Board.getLexicographicPosition(bb);
						if(done[pos] == -1){
							done[pos] = (byte) Solver.otherDirection(i);
							
							if(Board.isSolved(bb)){
								solution = getPath(bb, Board.getLexicographicPosition(roots));
//								System.out.println(solution.size());
								return solution;
							}
							
							todo2.push(bb);
							todo3.push(i);
						}
					}
				}
			}
			
			while(!(todo2.empty() || todo3.empty())){
				todo.push(todo2.pop());
				todo1.push(todo3.pop());
			}
		}
		
		return solution;
	}
	
	public ArrayList<Byte> getPath(int pos, int goal){
		ArrayList<Byte> s = new ArrayList<Byte>();
		int board = pos;
		int lpos = -1;
		while((lpos = Board.getLexicographicPosition(board)) != goal){
			board = Board.getBoard(board, done[lpos]);
			s.add(done[lpos]);
		}
		
		for(int i=0; i < s.size() / 2; i++){
			byte temp = s.get(i);
			s.set(i, s.get(s.size() - 1 - i));
			s.set(s.size() - 1 - i, temp);
		}
		
		for(int i=0; i < s.size(); i++){
			s.set(i, (byte) Solver.otherDirection(s.get(i)));
		}
		
		return s;
	}
	
	public void getPossibleDirections(int state, int prevDirection){
		int nColumns = board.getNColumns();
		int nRows = board.getNRows();
	
		int space = -1;
		for(int i=Start.nColumns * Start.nRows - 1; i >= 0; i--){
			int n = state % 10;
			state /= 10;
			if(n == Start.nColumns * Start.nRows - 1){
				space = i;
			}
		}
		
		dirs[LEFT] = space % nColumns < nColumns - 1;
		dirs[RIGHT] = space % nColumns > 0;
		dirs[UP] = space / nColumns < nRows - 1;
		dirs[DOWN] = space / nColumns > 0;
		
		if(prevDirection >= 0){
			dirs[Solver.otherDirection(prevDirection)] = false;
		}
	}
}
