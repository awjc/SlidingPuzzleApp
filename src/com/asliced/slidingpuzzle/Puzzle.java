package com.asliced.slidingpuzzle;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.Toast;

public class Puzzle {
	private long startingTime = 0;
	private int moves = 0;
	
	private PuzzlePiece[] pieces = null;
	private PuzzlePiece space = null;
	private int nRows;
	private int nColumns;
	
	private int dx;
	private int dy;
	
	private boolean solving = false;
	private boolean scrambling = false;
	private boolean cheated = false;
	
	private int BG_COLOR = Color.BLACK;
	private Paint BG_PAINT;
	
	public static int LEFT = 0;
	public static int RIGHT = 1;
	public static int UP = 2;
	public static int DOWN = 3;
	public static final int NONE = -1;
	
	public Puzzle(Picture p, int nRows, int nColumns){
		this.nRows = nRows;
		this.nColumns = nColumns;
	
		pieces = split(p);
		
		space = pieces[pieces.length - 1];
		space.turnOff();
		
		BG_PAINT = new Paint();
		BG_PAINT.setColor(BG_COLOR);
	}
	
	public Puzzle(Picture p){
		this(p, 3, 3);
	}
	
	public Puzzle(Board b){
		this.nRows = b.getNRows();
		this.nColumns = b.getNColumns();
		
		pieces = new PuzzlePiece[nRows * nColumns];
		for(int i=0; i < nRows; i++){
			for(int j=0; j < nColumns; j++){
				pieces[i*nColumns + j] = new PuzzlePiece(null, i, j);
				pieces[i*nColumns + j].setCurrentColumn(i);
				pieces[i*nColumns + j].setCurrentRow(j);
			}
		}
		
		int spaceidx = 0;
		for(int i=0; i < pieces.length; i++){
			if(pieces[i].getTrueColumn() == nColumns - 1 &&
					pieces[i].getTrueRow() == nRows - 1){
				spaceidx = i;
				break;
			}
		}
		space = pieces[spaceidx];
	}
	
	private PuzzlePiece[] split(Picture p){
		return split(p, nRows, nColumns);
	}
	
	public static PuzzlePiece[] split(Picture p, int nRows, int nColumns){
		PuzzlePiece[] pieces = new PuzzlePiece[nRows * nColumns];
		if(p != null){
			Bitmap img = p.getImage();
			if(img == null){
				Toaster.toast("Invalid image file!", Toast.LENGTH_SHORT);
				return null;
			}
			
			int imgw = img.getWidth();
			int imgh = img.getHeight();
			Bitmap imgScaled = Bitmap.createScaledBitmap(img, imgw, imgh, true);
			img = Bitmap.createBitmap(imgScaled, 0, 0, imgw - imgw%nColumns, imgh - imgh%nRows);
	
			int w = img.getWidth()/nColumns;
			int h = img.getHeight()/nRows;
			for(int i=0; i < nRows; i++){
				for(int j=0; j < nColumns; j++){
					pieces[i*nColumns + j] = new PuzzlePiece(Bitmap.createBitmap(imgScaled, j*w, i*h, w, h), i , j);
				}
			}
		}
		
		return pieces;
	}

	public void draw(Canvas c, int x, int y, int width, int height) {
		PuzzlePiece p = null;
		dx = width / nColumns;
		dy = height / nRows;
		
		c.drawRect(x, y, x + width, y + height, BG_PAINT);
		
		int w = dx;
		int h = dy;
		for(int i=0; i < pieces.length; i++){
			p = pieces[pieces.length - i - 1];
			if(p.isOn()){
				Rect src = new Rect(0, 0, p.getImage().getWidth(), p.getImage().getHeight());
				Rect dst = new Rect(x + p.getX(dx), y + p.getY(dy), x + p.getX(dx) + w, y + p.getY(dy) + h);
				c.drawBitmap(p.getImage(), src, dst, null);
			}
		}
	}

	public void move(PuzzlePiece active, int leftright, int updown, boolean redraw){
		if(leftright == 0 && updown == 0 || leftright != 0 && updown != 0){
			throw new IllegalArgumentException();
		}

		if(redraw){
			new PuzzlePieceAnimator(this, active, leftright, updown).run();
		}
		
		if(leftright < 0){
			active.moveLeft();
			space.moveRight();
		} else if(leftright > 0){
			active.moveRight();
			space.moveLeft();
		} else if(updown < 0){
			active.moveUp();
			space.moveDown();
		} else{
			active.moveDown();
			space.moveUp();
		}
		
		moves++;
		
		if(solving && isSolved() && !cheated){
			String time = String.format("%.1f", (System.currentTimeMillis() - startingTime)/1000.0);
			Toaster.toast("You solved the puzzle! It took you " + time + " seconds, and a total of " + moves + " moves!", Toast.LENGTH_LONG);
			solving = false;
			cheated = false;
		}
	}
	
	public void cheat(boolean cheat){
		cheated = cheat;
	}
	
	public boolean isScrambling(){
		return scrambling;
	}
	
	public boolean isSolved(){
		for(int i=0; i < pieces.length; i++){
			if(!pieces[i].isInCorrectSpot()){
				return false;
			}
		}
		
		return true;
	}
	
	public void moveUp(boolean redraw){
		int sc = space.getCurrentColumn();
		int sr = space.getCurrentRow();
		
		if(sr < nRows - 1){
			PuzzlePiece active = getPiece(sr + 1, sc);
			move(active, 0, -1, redraw);
		}	
	}
	
	public void moveDown(boolean redraw){
		int sc = space.getCurrentColumn();
		int sr = space.getCurrentRow();
		
		if(sr > 0){
			PuzzlePiece active = getPiece(sr - 1, sc);
			move(active, 0, 1, redraw);
		}	
	}
	
	public void moveLeft(boolean redraw) {
		int sc = space.getCurrentColumn();
		int sr = space.getCurrentRow();
		
		if(sc < nColumns - 1){
			PuzzlePiece active = getPiece(sr, sc + 1);
			move(active, -1, 0, redraw);
		}
	}
	
	public void moveRight(boolean redraw){
		int sc = space.getCurrentColumn();
		int sr = space.getCurrentRow();
		
		if(sc > 0){
			PuzzlePiece active = getPiece(sr, sc - 1);
			move(active, 1, 0, redraw);
		}
	}

	public Rect getEnclosingPieceRect(float mx, float my, int width, int height, boolean includeSpace){
		int dx = width / nColumns;
		int dy = height / nRows;
		
		for(int i=0; i < pieces.length; i++){
			int px = pieces[i].getCurrentColumn() * dx;
			int py = pieces[i].getCurrentRow() * dy;
			
			if(mx >= px && mx < px + dx && my >= py && my < py + dy){
				if(includeSpace && pieces[i].isAdjacentTo(space)){
					if(pieces[i].isRightOf(space)){
						return new Rect(px - dx, py, px + dx, py + dy);
					} else if(pieces[i].isLeftOf(space)){
						return new Rect(px, py, px + 2*dx, py + dy);
					} else if(pieces[i].isAbove(space)){
						return new Rect(px, py, px + dx, py + 2*dy);
					} else{
						return new Rect(px, py - dy, px + dx, py + dy);
					}
				} else{
					return new Rect(px, py, px + dx, py + dy);
				}
			}
		}
		
		return null;
	}
	
	public void moveFromTouchCoords(float mx, float my, int width, int height) {
		PuzzlePiece active = getPieceFromTouchCoords(mx, my, width, height);
		
		if(active != null){
			if(active.isAdjacentTo(space)){
				if(active.isLeftOf(space)){
					move(active, 1, 0, true);
				} else if(active.isRightOf(space)){
					move(active, -1, 0, true);
				} else if(active.isAbove(space)){
					move(active, 0, 1, true);
				} else{
					move(active, 0, -1, true);
				}
			}
		}
	}
	
	public PuzzlePiece getPiece(int row, int column){
		if(row >= nRows || row < 0 || column >= nColumns || column < 0){
			throw new IllegalArgumentException();
		}
		
		for(int i=0; i < pieces.length; i++){
			if(pieces[i].getCurrentRow() == row && pieces[i].getCurrentColumn() == column){
				return pieces[i];
			}
		}
		
		return null;
	}

	public void scramble() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(scrambling){
					return;
				}
				
				scrambling = true;
				solving = false;
				for(int i=0; i < pieces.length; i++){
					pieces[i].setCurrentColumn(i % nColumns);
					pieces[i].setCurrentRow(i / nRows);
				}
				
				Random rand = new Random();
				long startTime = System.currentTimeMillis();
				long duration = 1000;	
				int i=0;
				do{
					while(System.currentTimeMillis() < startTime + duration){
						i++;
						int dir = rand.nextInt(4);
						switch(dir){
							case 0:
								moveLeft(false);
								break;
							case 1:
								moveRight(false);
								break;
							case 2:
								moveUp(false);
								break;
							case 3:
								moveDown(false);
								break;
						}

						if(i % 1000 == 0){
							PlayerView.redraw();
						}
					}
				} while(isSolved());
				
				solving = true;
				PlayerView.redraw();
				scrambling = false;
				
				startingTime = System.currentTimeMillis();
				moves = 0;
			}
		}).start();
	}
	
	public int getDX(){
		return dx;
	}
	
	public int getDY(){
		return dy;
	}

	public Board getBoard() {
		int[] data = new int[nRows*nColumns];
		for(int i=0; i < nRows; i++){
			for(int j=0; j < nColumns; j++){
				PuzzlePiece p = pieces[i*nColumns + j];
				data[p.getCurrentRow()*nColumns + p.getCurrentColumn()] = i * nColumns + j;
			}
		}
		
		return new Board(data, nRows, nColumns);
	}

	public int getPieceDirection(float mx, float my, int width, int height) {
		PuzzlePiece p = getPieceFromTouchCoords(mx, my, width, height);
		if(p != null && p.isAdjacentTo(space)){
			if(p.isAbove(space)){
				return DOWN;
			} else if(p.isBelow(space)){
				return UP;
			} else if(p.isLeftOf(space)){
				return RIGHT; 
			} else {
				return LEFT;
			}
		} else{
			return NONE;
		}
	}

	public PuzzlePiece getPieceFromTouchCoords(float mx, float my, int width, int height) {
		int dx = width / nColumns;
		int dy = height / nRows;
		
		for(int i=0; i < pieces.length; i++){
			int px = pieces[i].getCurrentColumn() * dx;
			int py = pieces[i].getCurrentRow() * dy;
			
			if(mx >= px && mx < px + dx && my >= py && my < py + dy){
				return pieces[i];
			}
		}
		
		return null;
	}
}
