package com.bizeu.escandaloh;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTabHost;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bizeu.escandaloh.adapters.EscandaloAdapter;
import com.bizeu.escandaloh.model.Escandalo;
import com.bizeu.escandaloh.users.MainLoginActivity;
import com.bizeu.escandaloh.util.Connectivity;
import com.zed.adserver.BannerView;
import com.zed.adserver.onAdsReadyListener;

public class MainActivity extends SherlockFragmentActivity implements onAdsReadyListener, OnClickListener {

	public static final String ANGRY = "Enfadado";
	public static final String HAPPY = "Feliz";
	public static final String BOTH = "Ambos";
	private final static String APP_ID = "d83c1504-0e74-4cd6-9a6e-87ca2c509506";
	
	public static final int SHOW_CAMERA = 10;
    private static final int CREATE_ESCANDALO = 11;
    public static final int FROM_GALLERY = 12;
	private File photo;
	public static ArrayList<Escandalo> escandalos;
	EscandaloAdapter escanAdapter;
	private Uri mImageUri;
	Bitmap taken_photo;
	AmazonS3Client s3Client;
	private FrameLayout banner;
	private BannerView adM;
	private SharedPreferences prefs;
	private FragmentTabHost mTabHost;
	private Context context;
	
	private ImageView img_logout;
	private ImageView img_update_list;
	private ImageView img_take_photo;
	
	/**
	 * onCreate
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
			
		context = this;
		
		// Si el usuario no est� logueado mostramos la pantalla de registro/login
		if (!MyApplication.logged_user){
	        Intent i = new Intent(MainActivity.this, MainLoginActivity.class);
	        startActivity(i);
		}
	
		// Action Bar
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		View view = getLayoutInflater().inflate(R.layout.action_bar, null);
		getSupportActionBar().setCustomView(view);
			
		// Listeners del action bar
		img_logout = (ImageView) findViewById(R.id.img_actionbar_logout);
		img_logout.setOnClickListener(this);
		img_update_list = (ImageView) findViewById(R.id.img_actionbar_updatelist);
		img_update_list.setOnClickListener(this);
		img_take_photo = (ImageView) findViewById(R.id.img_actionbar_takephoto);
		img_take_photo.setOnClickListener(this);
		

		
		
		// Tab Host (FragmentTabHost)
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.tabcontent);
       
        mTabHost.setBackgroundColor(getResources().getColor(R.color.gris_claro));

        // A�adimos los tabs para cada uno de los 3 fragmentos
        mTabHost.addTab(mTabHost.newTabSpec(HAPPY).setIndicator(HAPPY),ListEscandalosFragmentHappy.class, null);  
        mTabHost.addTab(mTabHost.newTabSpec(ANGRY).setIndicator(ANGRY),ListEscandalosFragmentAngry.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(BOTH).setIndicator(BOTH),ListEscandalosFragmentBoth.class, null);
 
        // Almacenamos el alto del FragmentTabHost
        Display display = getWindowManager().getDefaultDisplay();
        mTabHost.measure(display.getWidth(), display.getHeight());
        MyApplication.ALTO_TABS = mTabHost.getMeasuredHeight();
	             
		// Ten
		//AdsSessionController.setApplicationId(getApplicationContext(),APP_ID);
       // AdsSessionController.registerAdsReadyListener(this);	
	}
	
	
	@Override
	public void onStart(){
		super.onStart();

		prefs = this.getSharedPreferences("com.bizeu.escandaloh", Context.MODE_PRIVATE);
	}

	
	/**
	 * onResume
	 */
	@Override
	public void onResume(){
		super.onResume();
		
	   // AdsSessionController.enableTracking();
		
		if (MyApplication.logged_user){
			img_logout.setVisibility(View.VISIBLE);
			img_take_photo.setImageResource(R.drawable.camara);
		}
		else{
			img_logout.setVisibility(View.INVISIBLE);
		}
	}
	
	

	/**
	 * onPause
	 */
	@Override
	protected void onPause() {
	    super.onPause();
	   // AdsSessionController.pauseTracking();
	}
	
	
	


	
	/**
	 * It will be called when the ads are ready to be shown
	 */
	@Override
	public void onAdsReady(){ 
		/*
	       //The banner will be show inside this view.
        banner = (FrameLayout) findViewById(R.id.banner);
     
        //BannerView initialization
        adM = new BannerView( this, getApplicationContext());
 
        banner.removeAllViews();
 
        //Add the bannerView to the container view
        banner.addView( adM );
 
        //Set the visibility to VISIBLE.
        banner.setVisibility( FrameLayout.VISIBLE );	
        */	
	}
	 

	
	
	/**
	 * It will be called when any errors ocurred.
	 */
	@Override
	public void onAdsReadyFailed(){
		Log.e("ZedAdServerLog","Error loading ads");
	}
	
	
	/**
	 * onKeyDown
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    // TODO Auto-generated method stub
	    if (keyCode == KeyEvent.KEYCODE_BACK){
	       // AdsSessionController.stopTracking();
	    }
	    return super.onKeyDown(keyCode, event);
	}
	 
	
	/**
	 * onUserLeaveHint
	 */
	@Override
	protected void onUserLeaveHint() {
	    // TODO Auto-generated method stub
	    super.onUserLeaveHint();
	    //AdsSessionController.detectHomeButtonEvent();
	}
	

	/**
	 * onActivityResult
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == SHOW_CAMERA) {
			if (resultCode == RESULT_OK) {
				Intent i = new Intent(MainActivity.this, CreateEscandaloActivity.class);
				i.putExtra("photo_from", SHOW_CAMERA);
				i.putExtra("photoUri", mImageUri.toString());
				startActivityForResult(i, CREATE_ESCANDALO);					
			}
			else if (resultCode == RESULT_CANCELED) {	           
	        }	
		}
			
		else if (requestCode == FROM_GALLERY) {
			if (data != null){
	            Uri selectedImageUri = data.getData();
	            Intent i = new Intent(MainActivity.this, CreateEscandaloActivity.class);
	            i.putExtra("photo_from", FROM_GALLERY);
	            i.putExtra("photoUri", selectedImageUri.toString());
	            startActivityForResult(i, CREATE_ESCANDALO);
			}

        }
		
		else if (requestCode == CREATE_ESCANDALO){
		}
	}



	/**
	 * Crea un archivo en una ruta con un formato espec�fico
	 * @param part
	 * @param ext
	 * @return
	 * @throws Exception
	 */
	private File createFile(String part, String ext) throws Exception
	{
	    File scandaloh_dir= Environment.getExternalStorageDirectory();
	    scandaloh_dir=new File(scandaloh_dir.getAbsolutePath()+"/Scandaloh/");
	    if(!scandaloh_dir.exists())
	    {
	    	scandaloh_dir.mkdir();
	    }
	    return File.createTempFile(part, ext, scandaloh_dir);
	}
	
	
	
	
	/**
	 * Comprueba si el dispositivo dispone de c�mara
	 * @param context
	 * @return
	 */
	private boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        return true;
	    } else {
	        return false;
	    }
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.img_actionbar_takephoto:
			
			Log.v("WE","Take photo");
			
		
			
			// Si dispone de conexi�n
			if (Connectivity.isOnline(context)){
				// Si est� logueado iniciamos la c�mara
				if (MyApplication.logged_user){ 
					
					// Creamos un menu para elegir entre hacer foto con la c�mara o cogerla de la galer�a
					final CharSequence[] items = { "Tomar desde la c�mara", "Cogerla desde la galer�a"};
					 AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				        builder.setTitle("A�adir foto");
				        builder.setItems(items, new DialogInterface.OnClickListener() {
				            @Override
				            public void onClick(DialogInterface dialog, int item) {
				                if (items[item].equals("Tomar desde la c�mara")) {
				                	if (checkCameraHardware(context)){
										Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
										File photo = null;
										try{
									        photo = createFile("picture", ".png");
									        photo.delete();
									    }
									    catch(Exception e){
									        Log.v("WE", "Can't create file to take picture!");
									    }
										
									    mImageUri = Uri.fromFile(photo);
									    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
										startActivityForResult(takePictureIntent, SHOW_CAMERA);
									}
									// El dispositivo no dispone de camara
									else{
										Toast toast = Toast.makeText(context, "Este dispositivo no dispone de c�mara", Toast.LENGTH_LONG);
										toast.show();
									}
				                } 
				                else if (items[item].equals("Cogerla desde la galer�a")) {
									Intent intent = new Intent();
			                        intent.setType("image/*");
			                        intent.setAction(Intent.ACTION_GET_CONTENT);                   
			                        startActivityForResult(Intent.createChooser(intent,"Select Picture"), FROM_GALLERY);
				                } 
				            }
				        });
				        builder.show();                 
				}
				// Si no, iniciamos la pantalla de login
				else{
					Intent i = new Intent(this, MainLoginActivity.class);
					startActivity(i);
				}
			}
			else{
				Toast toast = Toast.makeText(context, "No dispone de una conexi�n a internet", Toast.LENGTH_LONG);
				toast.show();
			}
			
			break;
			
		case R.id.img_actionbar_logout:
			if (MyApplication.logged_user){
				AlertDialog.Builder alert_logout = new AlertDialog.Builder(this);
				alert_logout.setTitle("Cerrar sesi�n usuario");
				alert_logout.setMessage("�Seguro que desea cerrar la sesi�n actual?");
				alert_logout.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {  
			            public void onClick(DialogInterface dialogo1, int id) {  
							// Deslogueamos al usuario
							prefs.edit().putString(MyApplication.USER_URI, null).commit();
							MyApplication.logged_user = false;
							// Cabiamos el icono de la c�mara
							img_take_photo.setImageResource(R.drawable.mas);
			            }  
			        });  
				alert_logout.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {  
			        	public void onClick(DialogInterface dialogo1, int id) {  
			            }  
			        });            
			     alert_logout.show(); 
			}
			break;
			
		case R.id.img_actionbar_updatelist:
			Intent ic = new Intent(MainActivity.this, LoginActivity.class);
			startActivity(ic);
			/*
			Bundle arguments = new Bundle();
			
			// Obtenemos cu�l es el tab activo
			String current_tab = mTabHost.getCurrentTabTag();
			
			// Volvemos a mostrar los escandalos seg�n el tab en el que estemos
			if (current_tab.equals(HAPPY)){
				ListEscandalosFragmentHappy fragment = new ListEscandalosFragmentHappy();
				fragment.setArguments(arguments);
	            getSupportFragmentManager().beginTransaction()
	                    .replace(R.id.tabcontent, fragment)
	                    .commit(); 
			}
			else if (current_tab.equals(ANGRY)){
				ListEscandalosFragmentAngry fragment = new ListEscandalosFragmentAngry();
				fragment.setArguments(arguments);
	            getSupportFragmentManager().beginTransaction()
	                    .replace(R.id.tabcontent, fragment)
	                    .commit(); 
			}
			else if (current_tab.equals(BOTH)){
				ListEscandalosFragmentBoth fragment = new ListEscandalosFragmentBoth();
				fragment.setArguments(arguments);
	            getSupportFragmentManager().beginTransaction()
	                    .replace(R.id.tabcontent, fragment)
	                    .commit(); 
			}
	        Log.v("WE","es: " + mTabHost.getCurrentTabTag());
	        break;
	        */
			
		}
		
	}
	

}
