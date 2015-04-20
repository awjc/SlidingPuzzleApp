package com.asliced.slidingpuzzle;

import java.net.URL;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Picture {
	private Bitmap img = null;
	
	public Picture(Resources rc, int resourceId){
		img = BitmapFactory.decodeResource(rc, resourceId);
	}
	
	public Picture(String fileName){
		try{
			URL url = Class.class.getResource(fileName);
			img = BitmapFactory.decodeFile(url.toURI().toString());
			
			if(img == null){
				img = BitmapFactory.decodeFile(fileName);
			}
			
			if(img == null){
//				System.out.println("Invalid image file name: " + fileName);
			}
		} catch(Exception e){}
	}
	
	public Bitmap getImage(){
		return img;
	}
}
