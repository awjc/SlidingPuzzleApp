package com.asliced.slidingpuzzle;

import java.util.Timer;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class PlayerView extends View {
	private final class UIHandler extends Handler {
	    public static final int DISPLAY_UI_TOAST = 0;
	    public static final int DISPLAY_UI_DIALOG = 1;
	
	    public UIHandler(Looper looper) {
	        super(looper);
	    }
	
	    @Override
	    public void handleMessage(Message msg){
	        switch(msg.what){
	        case UIHandler.DISPLAY_UI_TOAST:
	            Context context = getContext();
	            Toast t = Toast.makeText(context, (String)msg.obj, msg.arg1);
	            t.show();
	            break;
	        case UIHandler.DISPLAY_UI_DIALOG:
	        	
	        default:
	            break;
	        }
	    }
	}

	public static void handleUIRequest(String message, int duration){
	    Message msg = uiHandler.obtainMessage(UIHandler.DISPLAY_UI_TOAST);
	    msg.obj = message;
	    msg.arg1 = duration;
	    uiHandler.sendMessage(msg);
	}
	
	public static void toast(String text, int duration){
		if(pv != null){
			Toast.makeText(pv.getContext(), text, duration).show();
		}
	}
	
	private Player player;
	private boolean gotSizes = false;
	private static Rect puzzRect = new Rect();

	private String currentPic;
	
	private static PlayerView pv;
	public static boolean solved = false;
	public static boolean solving = false;
	public static ProgressDialog progressDialog;
	private static long lastToast = 0;
	
	private static final int BORDER_PAD = 0;
	private static final int BUTTON_HEIGHT = 60;
	private static final int BG_COLOR = Colors.color(0, 0, 85);
	
	private MyButton scramble;
	private MyButton solve;
	private MyButton changePic;
	
	private SlidingPuzzleAppActivity activity;
	
	private static UIHandler uiHandler;
	
	public PlayerView(Context context, String img) {
		super(context);
		activity = (SlidingPuzzleAppActivity) context;
		pv = this;

		init(img);
		invalidate();
	}
	
	public void init(String img){
		HandlerThread uiThread = new HandlerThread("UIHandler");
	    uiThread.start();
	    uiHandler = new UIHandler(uiThread.getLooper());
		
	    if(img == null){
	    	img = "Numbers";
	    }
	    
		currentPic = "";
		changePic(img);
		
		scramble = new MyButton("Scramble Puzzle", BORDER_PAD / 2, 0, 0, BUTTON_HEIGHT, new Runnable() {
			@Override
			public void run() {
				if(!solving && !player.getPuzzle().isScrambling()){
					player.scramble();
				}
			}
		});
		
		changePic = new MyButton("Change Picture", BORDER_PAD / 2, 0, 0, BUTTON_HEIGHT, new Runnable() {
			@Override
			public void run() {
				activity.choosePic();
			}
		});
		
		solve = new MyButton("Solve Puzzle", BORDER_PAD / 2, 0, 0, BUTTON_HEIGHT, new Runnable() {
			@Override
			public void run() {
				if(solving){
					return;
				}
				
				solved = false;
				if(!player.getPuzzle().isScrambling()){
					player.getPuzzle().cheat(true);
						
					if(Board.isSolved(puzzle.getBoard().getBoardData())){
						if(System.currentTimeMillis() - lastToast > 2500){
							Toaster.toast("The puzzle is already solved!", Toast.LENGTH_SHORT);
							lastToast = System.currentTimeMillis();
						}
					} else if(!solved){
						progressDialog = ProgressDialog.show(getContext(),
			                "Please wait...", "Finding a solution to the puzzle...", true);
					}
					
					new Thread(new Runnable() {
						@Override
						public void run() {
							int moves = Solver.solve(puzzle);
							String str = "";
							if(moves == 0){
								return;
							} else if(moves == 1){
								str = "Solution found! 1 move in total.";
							} else {
								str = "Solution found! " + moves + " moves in total.";
							}
							
							Toaster.toast(str, Toast.LENGTH_SHORT);
						}
					}).start();
				}
			}
		});
	}
	
	public static void changePic(String picName){
		if(pv == null || picName == null || pv.currentPic == null || picName.equals(pv.currentPic)){
			return;
		}
		
		Picture pic = null;
		if(picName.equals("Numbers")){
			pic = new Picture(pv.getResources(), R.drawable.numbers);
		} else if(picName.equals("Moon Man")){
			pic = new Picture(pv.getResources(), R.drawable.moonman);
		} else if(picName.equals("Barn")){
			pic = new Picture(pv.getResources(), R.drawable.barn);
		} else if(picName.equals("Melons")){
			pic = new Picture(pv.getResources(), R.drawable.melons);
		} else if(picName.equals("Cliff")){
			pic = new Picture(pv.getResources(), R.drawable.cliff);
		} else if(picName.equals("Fountain")){
			pic = new Picture(pv.getResources(), R.drawable.fountain);
		} else if(picName.equals("Antelope")){
			pic = new Picture(pv.getResources(), R.drawable.antelope);
		} else if(picName.equals("Bridge")){
			pic = new Picture(pv.getResources(), R.drawable.bridge);
		} else if(picName.equals("Flag")){
			pic = new Picture(pv.getResources(), R.drawable.flag);
		} else if(picName.equals("Fireworks")){
			pic = new Picture(pv.getResources(), R.drawable.fireworks);
		} else if(picName.equals("Clock")){
			pic = new Picture(pv.getResources(), R.drawable.clock);
		} 

		if(pic != null){
			pv.player = new Player(pic, 3, 3, 300, 300);
			pv.gotSizes = false;
			pv.currentPic = picName;
		}
	}
	
	public void updateSizes(){
		int width = getWidth();
		int height = getHeight();
		
		player.setX(BORDER_PAD / 2);
		player.setY(BORDER_PAD / 2);
	
		player.setWidth((width - BORDER_PAD) - ((width - BORDER_PAD)%3));
		player.setHeight(player.getWidth());
		
		puzzRect.set(0, 0, player.getWidth(), player.getHeight());
		
		int leftoverHeight = height - player.getHeight();
	
		int scrambleY = player.getHeight() + leftoverHeight / 4 - BUTTON_HEIGHT / 2;
		scramble.setY(scrambleY);
		scramble.setWidth(width - BORDER_PAD);
		
		int solveY = player.getHeight() + leftoverHeight / 2 - BUTTON_HEIGHT / 2;
		solve.setY(solveY);
		solve.setWidth(width - BORDER_PAD);
		
		int changePicY = player.getHeight() + leftoverHeight * 3 / 4 - BUTTON_HEIGHT / 2;
		changePic.setY(changePicY);
		changePic.setWidth(width - BORDER_PAD);
	}

	public static void redraw(){
		pv.postInvalidate();
	}
	
	public static void redraw(Rect r){
		pv.postInvalidate(r.left, r.top, r.right, r.bottom);
	}
	
	public static void redrawPuzzle() {
		redraw(puzzRect);
	}
	
	@Override
	public void onDraw(Canvas c) {
		if(c == null){
			return;
		}
		
		if(!gotSizes){
			updateSizes();
			gotSizes = true;
		}
		
		c.drawColor(BG_COLOR);
		
		player.draw(c);
		scramble.draw(c);
		solve.draw(c);
		changePic.draw(c);
	}

	int dir = Puzzle.NONE;
	float touchStartX = 0;
	float touchStartY = 0;
	boolean down = false;
	Timer t = new Timer();
	int WAIT_TIME = 25;
	PuzzlePiece cur;
	float mx;
	float my;
	float pieceStartX = 0;
	float pieceStartY = 0;
	int dx = 0;
	int dy = 0;
	Puzzle puzzle;
	int prevx;
	int ddx;
	int prevy;
	int ddy;
	int FLICK_THRESHOLD = 5;
	boolean flick = false;
	boolean doit;
	long time;
	
	@Override
	public boolean onTouchEvent(MotionEvent e){
		int action = e.getAction();
		mx = e.getX();
		my = e.getY();
		
		puzzle = player.getPuzzle();
		dx = puzzle.getDX();
		dy = puzzle.getDY();
		
		if(action == MotionEvent.ACTION_DOWN){
			time = System.currentTimeMillis();
			if(!down){
				touchStartX = mx;
				touchStartY = my;
				
				dir = player.getPieceDirection(mx, my);
				if(dir != Puzzle.NONE){
					cur = player.getPieceFromTouchCoords(mx, my);
					if(cur != null){
						cur.setX(-1);
						cur.setY(-1);
						pieceStartX = cur.getX(dx);
						pieceStartY = cur.getY(dy);
						
						down = true;
						prevx = -11;
						prevy = -11;
					}
				}
			} else{
				doit = false;
			}
		}
		
		else if(action == MotionEvent.ACTION_UP){
			long dt = System.currentTimeMillis() - time;
//			t.schedule(new TimerTask() {
//				@Override
//				public void run() {
//					if(doit){
						down = false;
						if(cur != null){
							if(dt < 50 || flick || isHalfway()){
								if(dir == Puzzle.RIGHT){
									puzzle.moveRight(false);
								} else if(dir == Puzzle.LEFT){
									puzzle.moveLeft(false);
								} else if(dir == Puzzle.DOWN){
									puzzle.moveDown(false);
								} else if(dir == Puzzle.UP){
									puzzle.moveUp(false);
								}
							}
							
							cur.setX(-1);
							cur.setY(-1);
							
							redraw();
						}
						
						prevx = -11;
						prevy = -11;
						flick = false;
//					}
//				}
//			}, WAIT_TIME);
			
//			doit = true;
		}
		
		else if(down && action == MotionEvent.ACTION_MOVE){
			if(prevx != -11){
				ddx = (int) (mx - prevx);
			}
			
			if(prevy != -11){
				ddy = (int) (my - prevy);
			}

			prevx = (int) mx;
			prevy = (int) my;
			
			if(dir == Puzzle.LEFT && -ddx > FLICK_THRESHOLD ||
					dir == Puzzle.RIGHT && ddx > FLICK_THRESHOLD ||
					dir == Puzzle.DOWN && ddy > FLICK_THRESHOLD ||
					dir == Puzzle.UP && -ddy > FLICK_THRESHOLD){
				flick = true;
			} else{
				flick = false;
			}
			
			if(dirHoriz()){
				int newX = (int) (pieceStartX + (mx - touchStartX));
				if(dir == Puzzle.RIGHT){
					if(newX > pieceStartX + dx){
						cur.setX((int) pieceStartX + dx);
						touchStartX = mx - dx;
					} else if(newX < pieceStartX){
						cur.setX((int) pieceStartX);
						touchStartX = mx;
					} else{
						cur.setX(newX);
					}
				} else {
					if(newX < pieceStartX - dx){
						cur.setX((int) pieceStartX - dx);
						touchStartX = mx + dx;
					} else if(newX > pieceStartX){
						cur.setX((int) pieceStartX);
						touchStartX = mx;
					} else{
						cur.setX(newX);
					}
				}
			} else if(dirVert()){
				int newY = (int) (pieceStartY + (my - touchStartY));
				if(dir == Puzzle.UP){
					if(newY < pieceStartY - dy){
						cur.setY((int) pieceStartY - dy);
						touchStartY = my + dy;
					} else if(newY > pieceStartY){
						cur.setY((int) pieceStartY);
						touchStartY = my;
					} else{
						cur.setY(newY);
					}
				} else{
					if(newY > pieceStartY + dy){
						cur.setY((int) pieceStartY + dy);
						touchStartY = my - dy;
					} else if(newY < pieceStartY){
						cur.setY((int) pieceStartY);
						touchStartY = my;
					} else{
						cur.setY(newY);
					}
				}
			}
			
			redraw();
		}
		
		if(!changePic.checkForPress(e)){
			if(!solve.checkForPress(e)){
				scramble.checkForPress(e);
			}
		}
		
		return true;
	}
	
	private boolean dirHoriz(){
		return dir == Puzzle.LEFT || dir == Puzzle.RIGHT;
	}
	
	private boolean dirVert(){
		return dir == Puzzle.DOWN || dir == Puzzle.UP;
	}
	
	public boolean isHalfway(){
		int FRACTION = 2;
		
		if(dir == Puzzle.UP){
			return cur.getY(dy) <= pieceStartY - dy / FRACTION;
		} else if(dir == Puzzle.DOWN){
			return cur.getY(dy) >= pieceStartY + dy / FRACTION;
		} else if(dir == Puzzle.LEFT){
			return cur.getX(dx) <= pieceStartX - dx / FRACTION;
		} else if(dir == Puzzle.RIGHT){
			return cur.getX(dx) >= pieceStartX + dx / FRACTION;
		} else{
			return false;
		}
	}
}
