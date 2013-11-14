package com.bizeu.escandaloh.users;

import com.bizeu.escandaloh.CreateEscandaloActivity;
import com.bizeu.escandaloh.MainActivity;
import com.bizeu.escandaloh.R;
import com.zed.adserver.AdsSessionController;
import com.zed.adserver.BannerView;
import com.zed.adserver.onAdsReadyListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainLoginActivity extends Activity{
	
	public static final String FIRST_TIME = "First_time";
	public static int LOG_IN = 1;
	public static int REGISTRATION = 2;
	
	private TextView txt_pasar;
	private Button but_registro;
	private Button but_login;
	
	private boolean esta_logeado;
	private SharedPreferences prefs;
	
	private boolean first_time = false;
	
	
	/**
	 * onCreate
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_login);	
		
		/*
		if (getIntent() != null){
			first_time = getIntent().getExtras().getBoolean(FIRST_TIME);
		}
		*/
		
		txt_pasar = (TextView) findViewById(R.id.txt_pasar_registro);
		but_registro = (Button) findViewById(R.id.but_registro_usuario);
		but_login = (Button) findViewById(R.id.but_log_in);
		
		but_registro.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getBaseContext(), RegistrationActivity.class);
				startActivityForResult(i, REGISTRATION);		
			}
		});
		
		but_login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Mostramos la pantalla de log in
				Intent i = new Intent(getBaseContext(), LoginActivity.class);
				startActivityForResult(i, LOG_IN);	
			}
		});
		
		txt_pasar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();	
			}
		});
	}
	
	
	
	 

	
	/**
	 * onActivityResult
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		// Si viene de hacer log in o registro
		if (requestCode == LOG_IN || requestCode == REGISTRATION) {
			// Y lo ha hecho exitosamente
			if (resultCode == RESULT_OK) {
				// Cerramos directamente la pantalla
				finish();					
			}		 
		}	
	}
}
