package com.bizeu.escandaloh.model;

import android.graphics.Bitmap;

public class Scandaloh {
	public static final String HAPPY_CATEGORY = "/api/v1/category/1/";
	public static final String ANGRY_CATEGORY = "/api/v1/category/2/";
	public static final String ANGRY = "Angry";
	public static final String HAPPY = "Happy";
	
	private String id;
	private String title;
	private String category;
	private Bitmap picture;
	private String picture_url;
	private int num_comments;
	private String resource_uri; // Uri de la foto
	private String route_img; // Ruta de la foto peque�a sin marca de agua
	private String route_img_big; // Ruta de lda foto grande con marca de agua
	private String resource_audio; // Uri del audio. Null es que la imagen no tiene audio
	private boolean has_audio;
	private String date;
	private String user;

	
	
	/**
	 * Constructor
	 */
	public Scandaloh(){
		super();
	}
	
	/**
	 * Constructor por par�metros
	 * @param foto
	 * @param titulo
	 * @param numero_comentarios
	 */
	public Scandaloh(String id, String title, String category, Bitmap picture, int num_comments, String resource_uri, String route_img, String route_img_big, String uri_audio, String user, String date){
		this.id = id;
		this.title = title;
		if (category.equals(HAPPY_CATEGORY)){
	        	this.category = HAPPY;
		}
		else{
			this.category = ANGRY;
		}
		this.picture = picture;
		this.num_comments = num_comments;
		this.resource_uri = resource_uri;
		this.route_img = route_img;
		this.route_img_big = route_img_big;
		this.resource_audio = uri_audio;
		this.has_audio = uri_audio.equals("null") ? false : true;
		this.user = user;
		this.date = date;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public String getId(){
		return id;
	}
	
	
	public void setTitle(String new_title){
		this.title = new_title;
	}
	
	public String getTitle(){
		return title;
	}
	
	public void setCategory(String new_category){
		this.category = new_category;
	}
	
	public String getCategory(){
		return category;
	}
	
	public void setPicture(Bitmap new_picture){
		this.picture = new_picture;
	}
	
	public Bitmap getPicture(){
		return picture;
	}
	
	public void setNumComments(int new_num_comments){
		this.num_comments = new_num_comments;
	}
	
	public int getNumComments(){
		return num_comments;
	}
	
	public void setResourceUri(String new_resource){
		this.resource_uri = new_resource;
	}
	
	public String getResourceUri(){
		return this.resource_uri;
	}
	
	public void setRouteImg(String new_route){
		this.route_img = new_route;
	}
	
	public String getRouteImg(){
		return this.route_img;
	}
	
	public void setRouteImgBig(String new_route_big){
		this.route_img_big = new_route_big;
	}
	
	public String getRouteImgBig(){
		return this.route_img_big;
	}
	
	public void setUriAudio(String new_uri_audio){
		this.resource_audio = new_uri_audio;
	}
	
	public String getUriAudio(){
		return this.resource_audio;
	}
	
	public void setHasAudio(boolean new_has_audio){
		this.has_audio = new_has_audio;
	}
	
	public boolean hasAudio(){
		return this.has_audio;
	}	
	
	public void setPictureUrl(String new_image_url){
		this.picture_url = new_image_url;
	}
	
	public String getPictureUrl(){
		return this.picture_url;
	}
	
	public void setUser(String new_user){
		this.user = new_user;
	}
	
	public String getUser(){
		return this.user;
	}
	
	public void setDate(String new_date){	
	}
	
	public String getDate(){
		return this.date;
	}
	
}