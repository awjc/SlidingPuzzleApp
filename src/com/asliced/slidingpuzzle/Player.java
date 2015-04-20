package com.asliced.slidingpuzzle;

import android.graphics.Canvas;
import android.graphics.Rect;

public class Player {
	private static Player player;
	private int width;
	private int height;
	private int x;
	private int y;
	private Puzzle puzzle;
	
	public Player(Picture p){
		this(p, 3, 3, 200, 200);
	}
	
	public Player(Picture p, int nRows, int nColumns, int width, int height){
		puzzle = new Puzzle(p, nRows, nColumns);
		player = this;
		
		this.width = width;
		this.height = height;
	}
	
	public static Player getPlayer(){
		return player;
	}
	
	public void moveLeft(){
		puzzle.moveLeft(true);
	}
	
	public void moveRight(){
		puzzle.moveRight(true);
	}
	
	public void moveUp(){
		puzzle.moveUp(true);
	}
	
	public void moveDown(){
		puzzle.moveDown(true);
	}
	
	public Rect getEnclosingPieceRect(float mx, float my, boolean includeSpace){
		return puzzle.getEnclosingPieceRect(mx, my, width, height, includeSpace);
	}
	
	public void moveFromTouchCoords(float mx, float my){
		puzzle.moveFromTouchCoords(mx, my, width, height);
	}
	
	public void scramble() {
		puzzle.scramble();
	}

	public Board getBoard() {
		return puzzle.getBoard();
	}
	
	public Puzzle getPuzzle(){
		return puzzle;
	}
	
	public void draw(Canvas c){
		puzzle.draw(c, x, y, width, height);
	}
	
	public void setWidth(int w){
		this.width = w;
	}
	
	public void setHeight(int h){
		this.height = h;
	}
	
	public void setX(int x){
		this.x = x;
	}
	
	public void setY(int y){
		this.y = y;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}

	public int getPieceDirection(float mx, float my) {
		return puzzle.getPieceDirection(mx, my, width, height);
	}

	public PuzzlePiece getPieceFromTouchCoords(float mx, float my) {
		return puzzle.getPieceFromTouchCoords(mx, my, width, height);
	}
}
