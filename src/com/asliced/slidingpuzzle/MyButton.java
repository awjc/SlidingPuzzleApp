package com.asliced.slidingpuzzle;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

public class MyButton {
	private static final int PRESSED_BORDER_COLOR = Colors.color(150, 150, 150);
	private static final int UNPRESSED_BORDER_COLOR = Colors.color(250, 250, 250);
	
	
	private Rect buttonRect = new Rect();
	private Paint buttonPaint = new Paint();
	
	private RectF buttonRectF = new RectF();
	
	private int buttonColor = Colors.color(0, 45, 55);
	private int buttonPressedColor = Colors.color(0, 15, 20);
	
	private RectF smallerButtonRectF = new RectF();
	private String buttonText = "Button";
	
	private Paint textPaint = new Paint();
	private Paint borderPaint = new Paint();
	
	private int borderSizeBig = 2;
	private int borderSizeSmall = 1;

	private boolean pressed = false;
	
	private int x;
	private int y;
	private int width;
	private int height;
	
	private Runnable action;
	
	public MyButton(String buttonText, int x, int y, int width, int height, Runnable action){
		this.buttonText = buttonText;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		this.action = action;
	}
	
	public Rect getRect(){
		return buttonRect;
	}
	
	public void press(){
		pressed = true;
	}
	
	public void release(){
		pressed = false;
	}
	
	public void execute(){
		action.run();
	}
	
	public boolean checkForPress(MotionEvent e){
		if(e.getAction() == MotionEvent.ACTION_DOWN){
			if(buttonRectF.contains(e.getX(), e.getY())){
				pressed = true;
			}
		} else if(e.getAction() == MotionEvent.ACTION_MOVE){
			if(!buttonRectF.contains(e.getX(), e.getY())){
				pressed = false;
			}
		} else if(e.getAction() == MotionEvent.ACTION_UP){
			if(pressed){
				execute();
			} 
			
			pressed = false;
		}
		
		PlayerView.redraw(getRect());
		return pressed;
	}
	
	public void draw(Canvas c){
		borderPaint.setColor(pressed ? PRESSED_BORDER_COLOR : UNPRESSED_BORDER_COLOR);
		buttonRect.set(x, y, x + width, y + height);
		
		if(pressed){
			smallerButtonRectF.set(buttonRect.left + borderSizeBig, buttonRect.top + borderSizeBig,
					buttonRect.right - borderSizeSmall, buttonRect.bottom - borderSizeSmall);
			buttonPaint.setColor(buttonPressedColor);
		} else{
			smallerButtonRectF.set(buttonRect.left + borderSizeSmall, buttonRect.top + borderSizeSmall,
					buttonRect.right - borderSizeBig, buttonRect.bottom - borderSizeBig);
			buttonPaint.setColor(buttonColor);
		}
		buttonRectF.set(buttonRect);
		c.drawRoundRect(buttonRectF, 20f, 15f, borderPaint);
		c.drawRoundRect(smallerButtonRectF, 20f, 15f, buttonPaint);
		
		textPaint.setTextSize(32);
		textPaint.setTextScaleX(1.2f);
		textPaint.setColor(Colors.color(255, 255, 255));
		float textX = x + (width - textPaint.measureText(buttonText)) / 2;
		float textY = y + (height - textPaint.getFontMetrics().ascent - textPaint.getFontMetrics().descent) / 2;
		if(pressed){
			textX += borderSizeBig - borderSizeSmall;
			textY += borderSizeBig - borderSizeSmall;
		}
		
		c.drawText(buttonText, textX, textY, textPaint);
	}
	
	public void setY(int y){
		this.y = y;
	}
	
	public void setWidth(int w){
		this.width = w;
	}
}
