package com.asliced.slidingpuzzle;

public class Colors {
	public static final int OPAQUE = 255 << 24;
	public static final int TRANSPARENT = 0;
	public static final int BLACK = OPAQUE;
	public static final int WHITE = BLACK | 255 << 16 | 255 << 8 | 255;
			
	public static int color(int r, int g, int b){
		return color(r, g, b, 255);
	}
	
	public static int color(int r, int g, int b, int a){
		if(a > 255 || r > 255 || g > 255 || b > 255 || a < 0 || r < 0 || g < 0 || b < 0){
			return 0;
		}
		
		return a << 24 | r << 16 | g << 8 | b;
	}
	
	public static int color(float r, float g, float b){
		return color(r, g, b, 1.0f);
	}
	
	public static int color(float r, float g, float b, float a){
		return color((int) (r * 255), (int) (g * 255), (int) (b * 255), (int) (a * 255)); 
	}
}
