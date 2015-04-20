package com.asliced.slidingpuzzle;

public class Toaster {
	public static void toast(String text, int duration){
		PlayerView.handleUIRequest(text, duration);
	}
}
