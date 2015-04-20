package com.asliced.slidingpuzzle;

import java.util.Timer;
import java.util.TimerTask;

public class PuzzlePieceAnimator {
	class AnimatorTask extends TimerTask {
		private long start = -1;
		
		@Override
		public void run() {
			long time = System.currentTimeMillis();
			
			if(start < 0){
				start = time;
			}
			
			int newX = (int) (xstart + (xend - xstart)*(time - start)/TIME_TO_MOVE);
			int newY = (int) (ystart + (yend - ystart)*(time - start)/TIME_TO_MOVE);

			if(xdir > 0 && newX > xend || xdir < 0 && newX < xend ||
					ydir > 0 && newY > yend || ydir < 0 && newY < yend){
				t.cancel();
				toMove.setTimer(null);
				toMove.setX(-1);
				toMove.setY(-1);
			} else{
				toMove.setX(newX);
				toMove.setY(newY);
			}
			
			PlayerView.redraw();
		}
	}
	
	private Timer t;
	private PuzzlePiece toMove;
	private int xdir;
	private int ydir;
	
	private int xstart;
	private int ystart;
	private int xend;
	private int yend;
	
	//Time to complete the move, in milliseconds
	private int TIME_TO_MOVE = 125;
	
	private static final int TIMER_PERIOD = 20;
	
	public PuzzlePieceAnimator(Puzzle puzz, PuzzlePiece toMove, int xdir, int ydir){
		if(xdir == 0 && ydir == 0 || xdir != 0 && ydir != 0){
			throw new IllegalArgumentException();
		}
		
		t = new Timer();
		this.toMove = toMove;
		this.xdir = xdir < 0 ? -1 : (xdir == 0 ? 0 : 1);
		this.ydir = ydir < 0 ? -1 : (ydir == 0 ? 0 : 1);

		int dx = puzz.getDX();
		int dy = puzz.getDY();

		if(toMove.timer() != null){
			toMove.timer().cancel();
			toMove.setTimer(t);
		}
		
		if(!toMove.isMoving()){
			xstart = toMove.getX(dx);
			ystart = toMove.getY(dy);
			xend = xstart + dx * xdir;
			yend = ystart + dy * ydir;
		} else{
			xstart = toMove.getX(dx);
			ystart = toMove.getY(dy);
			xend = toMove.getCurrentColumn() * dx + dx * xdir;
			yend = toMove.getCurrentRow() * dy + dy * ydir;
			
			TIME_TO_MOVE *= ydir == 0 ? (double)xdir * (xend - xstart)/dx : (double)ydir * (yend - ystart)/dy;
			TIME_TO_MOVE = Math.abs(TIME_TO_MOVE) + 1;
		}
	}
	
	public void run(){
		t.schedule(new AnimatorTask(), 0, TIMER_PERIOD);
		toMove.setTimer(t);
	}
}







