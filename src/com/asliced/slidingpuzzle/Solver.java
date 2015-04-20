package com.asliced.slidingpuzzle;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Solver {
	private static Puzzle puzzle;
	public static int nMoves = 0;
	
	private static GameTree gameTree;
	
	public static int solve(Puzzle puzz){
		PlayerView.solving = true;
		puzzle = puzz;
		
		final ArrayList<Byte> solution = getSolutionGameTree();
		PlayerView.solved = true;
		
		if(PlayerView.progressDialog != null){
			PlayerView.progressDialog.dismiss();
			PlayerView.progressDialog = null;
		}
		
//		final String[] dir = {"Left", "Right", "Up", "Down"};
		final Timer t = new Timer();
		t.schedule(new TimerTask() {
			int i = 0;
			
			@Override
			public void run() {
				if(i >= solution.size() || puzzle.isSolved()){
//					System.out.println("Number of actual moves: " + i);
					t.cancel();
					PlayerView.solving = false;
					return;
				}
				
				switch(solution.get(i)){
					case 0: 
						Player.getPlayer().getPuzzle().moveLeft(false);
						break;
					case 1: 
						Player.getPlayer().getPuzzle().moveRight(false);
						break;
					case 2: 
						Player.getPlayer().getPuzzle().moveUp(false);
						break;
					case 3: 
						Player.getPlayer().getPuzzle().moveDown(false);
						break;
				}
				PlayerView.redraw();
				
				i++;
			}
		}, 0, 1000/5);
		
		return solution.size();
	}
	
	private static ArrayList<Byte> getSolutionGameTree(){
		gameTree = new GameTree();
		return gameTree.solve(puzzle.getBoard());
	}
	
	public static boolean areSameLine(int a, int b){
		return a % 2 == 0 ? b == a + 1 : b == a - 1;
	}
	
	public static int otherDirection(int direction){
		return (direction + 1) % 2 + 2 * (direction / 2);
	}
}
