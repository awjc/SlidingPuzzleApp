package com.asliced.slidingpuzzle;

import java.util.Timer;

import android.graphics.Bitmap;

public class PuzzlePiece {
	private int trueRow;
	private int trueColumn;
	
	private int currentRow;
	private int currentColumn;
	
	private int x = -1;
	private int y = -1;
	
	private Timer timer;
	
	private boolean isOn = true;
	
//	private BufferedImage img;
	private Bitmap img;
	
	public String toString(){
		return "Cur: [" + currentRow + ", " + currentColumn + "]  True: [" +
						trueRow + ", " + trueColumn + "]";
	}
	
	public PuzzlePiece(Bitmap img, int row, int column) {
		this.img = img;
		this.trueRow = row;
		this.trueColumn = column;
		this.currentRow = row;
		this.currentColumn = column;
	}
	
	public void turnOff(){
		isOn = false;
	}
	
	public void turnOn(){
		isOn = true;
	}
	
	public boolean isOn(){
		return isOn;
	}
	
	public Bitmap getImage(){
		return img;
	}

	public int getTrueRow() {
		return trueRow;
	}

	public int getTrueColumn() {
		return trueColumn;
	}

	public int getCurrentRow() {
		return currentRow;
	}

	public void setCurrentRow(int currentRow) {
		this.currentRow = currentRow;
	}

	public int getCurrentColumn() {
		return currentColumn;
	}

	public void setCurrentColumn(int currentColumn) {
		this.currentColumn = currentColumn;
	}
	
	public void moveLeft(){
		setCurrentColumn(currentColumn - 1);
	}
	
	public void moveRight(){
		setCurrentColumn(currentColumn + 1);
	}
	
	public void moveUp(){
		setCurrentRow(currentRow - 1);
	}
	
	public void moveDown(){
		setCurrentRow(currentRow + 1);
	}

	public boolean isAdjacentTo(PuzzlePiece other) {
		int columnDif = currentColumn - other.currentColumn;
		int rowDif = currentRow - other.currentRow;
		
		return Math.abs(columnDif) == 1 && Math.abs(rowDif) == 0
				|| Math.abs(columnDif) == 0 && Math.abs(rowDif) == 1;
	}
	
	public boolean isLeftOf(PuzzlePiece other){
		return currentColumn < other.currentColumn;
	}
	
	public boolean isRightOf(PuzzlePiece other){
		return currentColumn > other.currentColumn;
	}
	
	public boolean isAbove(PuzzlePiece other){
		return currentRow < other.currentRow;
	}
	
	public boolean isBelow(PuzzlePiece other){
		return currentRow > other.currentRow;
	}

	public int getX(int dx) {
		return x < 0 ? dx * currentColumn : x;
	}
	
	public int getY(int dy){
		return y < 0 ? dy * currentRow : y;
	}
	
	public boolean isMoving(){
		return x > 0 || y > 0;
	}
	
	public void setX(int x){
		this.x = x;
	}	
	
	public void setY(int y){
		this.y = y;
	}

	public Timer timer() {
		return timer;
	}
	
	public void setTimer(Timer t){
		timer = t;
	}

	public void moveY(float my) {
		y += my;
	}
	
	public void moveX(float mx){
		x += mx;
	}
	
	public boolean isInCorrectSpot(){
		return trueRow == currentRow && trueColumn == currentColumn;
	}
}
