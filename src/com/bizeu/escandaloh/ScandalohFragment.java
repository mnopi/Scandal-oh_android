package com.bizeu.escandaloh;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import com.applidium.shutterbug.FetchableImageView;
import com.bizeu.escandaloh.model.Escandalo;
import com.bizeu.escandaloh.util.Audio;
import com.bizeu.escandaloh.util.ImageUtils;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class ScandalohFragment extends Fragment {

	private static final String ID = "id";
    private static final String URL = "url";
    private static final String URL_BIG = "url_big";
    private static final String TITLE = "title";
    private static final String NUM_COMMENTS = "num_comments";
    private static final String HAS_AUDIO = "has_audio";
    private static final String USER_NAME = "user_name";
    private static final String DATE = "date";
    private static final String URI_AUDIO = "uri_audio";
 
    private String id;
    private String url;
    private String url_big;
    private String title;
    private int num_comments;
    private boolean has_audio;
    private String user_name;
    private String date;
    private Bitmap bitma;
	private boolean any_error;
	private int chosen_report; // 1:Copyright      2:Ilegalcontent      3:Spam
	private String uri_audio;
    
    TextView num_com ;
 
    
    /**
     * Crea y devuelve una nueva instancia de un fragmento
     * @param escan Escandalo para dicho fragmento
     * @return Fragmento con el esc�ndalo
     */
    public static ScandalohFragment newInstance(Escandalo escan) {
        // Instanciamos el fragmento
        ScandalohFragment fragment = new ScandalohFragment();
 
        // Guardamos los datos del fragmento (del esc�ndalo)
        Bundle bundle = new Bundle();
        bundle.putString(ID, escan.getId());
        bundle.putString(URL, escan.getRouteImg());
        bundle.putString(URL_BIG, escan.getRouteImgBig());
        bundle.putString(TITLE, escan.getTitle());
        bundle.putInt(NUM_COMMENTS, escan.getNumComments());
        bundle.putBoolean(HAS_AUDIO, escan.hasAudio());
        bundle.putString(USER_NAME, escan.getUser());
        bundle.putString(DATE, escan.getDate());
        bundle.putString(URI_AUDIO, escan.getUriAudio());

        fragment.setArguments(bundle);
        fragment.setRetainInstance(true);
 
        // Devolvemos el fragmento
        return fragment;
    }
 
    /**
     * onCreate
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        // Obtenemos los valores del fragmento (del esc�ndalo)
        this.id = (getArguments() != null) ? getArguments().getString(ID) : null;
        this.url = (getArguments() != null) ? getArguments().getString(URL) : null;
        this.url_big = (getArguments() != null) ? getArguments().getString(URL_BIG) : null;
        this.title = (getArguments() != null) ? getArguments().getString(TITLE) : null;
        this.num_comments = (getArguments() != null) ? getArguments().getInt(NUM_COMMENTS) : 0;
        this.has_audio = (getArguments() != null) ? getArguments().getBoolean(HAS_AUDIO) : false;
        this.user_name = (getArguments() != null) ? getArguments().getString(USER_NAME) : null;
        this.date = (getArguments() != null) ? getArguments().getString(DATE) : null;
        this.uri_audio = (getArguments() != null) ? getArguments().getString(URI_AUDIO) : null;
    }
 
    
    
    /**
     * onCreateView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.escandalo, container, false);
             
        // Mostramos los datos del esc�ndalo
        // Foto
        FetchableImageView img = (FetchableImageView) rootView.findViewById(R.id.img_foto);
        img.setImage(this.url, R.drawable.cargando);      
     
        img.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*
				 // Mandamos el evento a Google Analytics
				 EasyTracker easyTracker = EasyTracker.getInstance(mContext);
				 easyTracker.send(MapBuilder
				      .createEvent("Acci�n UI",     // Event category (required)
				                   "Boton clickeado",  // Event action (required)
				                   "Ver foto en detalle desde carrusel",   // Event label
				                   null)            // Event value
				      .build()
				  );
				*/
				
				
				// Evitamos que se pulse dos o m�s veces en las fotos (para que no se abra m�s de una vez)
				if (!MyApplication.PHOTO_CLICKED){
					MyApplication.PHOTO_CLICKED = true;
					
					// Paramos si hubiera alg�n audio reproduci�ndose
					Audio.getInstance(getActivity().getBaseContext()).releaseResources();
					
					Intent i = new Intent(getActivity(), DetailPhotoActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					ImageView imView = (ImageView) v;
					Bitmap bitm = ((BitmapDrawable)imView.getDrawable()).getBitmap();
					byte[] bytes = ImageUtils.bitmapToBytes(bitm);
					i.putExtra("bytes", bytes);
					i.putExtra("uri_audio", uri_audio);
					
					getActivity().startActivity(i);				
				}	
			}
		});
        
        
        img.setOnLongClickListener(new View.OnLongClickListener() {
        	
			@Override
			public boolean onLongClick(View v) {			
				final View mView = v;
				
				/*
				 // Mandamos el evento a Google Analytics
				 EasyTracker easyTracker = EasyTracker.getInstance(mContext);
				 easyTracker.send(MapBuilder
				      .createEvent("Acci�n UI",     // Event category (required)
				                   "Boton clickeado prolongadamente",  // Event action (required)
				                   "Guardar foto en galer�a desde carrusel",   // Event label
				                   null)            // Event value
				      .build()
				  );
				  */
				
				// Paramos si hubiera alg�n audio reproduci�ndose
				Audio.getInstance(getActivity().getBaseContext()).releaseResources();
				
				// Guardamos la foto en la galer�a	
				final CharSequence[] items = {"Guardar foto en la galer�a"};
				 AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			        builder.setItems(items, new DialogInterface.OnClickListener() {
			            @Override
			            public void onClick(DialogInterface dialog, int item) {
			            	
			                if (items[item].equals("Guardar foto en la galer�a")) {
			                	new SaveImageTask(getActivity()).execute(url_big);     			   
			                } 			                
			            }
			        });
			        builder.show();
			        
				return true;
			}
		});
        

        
        // N�mero de comentarios
        num_com = (TextView) rootView.findViewById(R.id.txt_numero_comentarios);
        num_com.setText(Integer.toString(num_comments));
        num_com.setOnClickListener(new View.OnClickListener() {
			
        	@Override
			public void onClick(View v) {
				
        		/*
				 // Mandamos el evento a Google Analytics
				 EasyTracker easyTracker = EasyTracker.getInstance(mContext);
				 easyTracker.send(MapBuilder
				      .createEvent("Acci�n UI",     // Event category (required)
				                   "Boton clickeado",  // Event action (required)
				                   "Ver comentarios desde carrusel",   // Event label
				                   null)            // Event value
				      .build()
				  );
				  */
				
				// Paramos si hubiera alg�n audio reproduci�ndose
				Audio.getInstance(getActivity().getBaseContext()).releaseResources();
				
				Intent i = new Intent(getActivity().getBaseContext(), DetailCommentsActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra("id", id);
				//i.putExtra("id", v.getTag(R.string.id).toString());
				i.putExtra("route_image", url);
				//i.putExtra("route_image", (String) v.getTag(R.string.url_foto));
				i.putExtra("user", user_name);
				//i.putExtra("user", (String) v.getTag(R.string.user));
				i.putExtra("title", title);
				//i.putExtra("title", (String) v.getTag(R.string.title));
				getActivity().getBaseContext().startActivity(i);	
			}
		});
        
        // T�tulo
        TextView tit = (TextView) rootView.findViewById(R.id.txt_titulo);
        tit.setText(title);
        
        // Micr�fono
        ImageView aud = (ImageView) rootView.findViewById(R.id.img_escandalo_microfono);
        if(has_audio){
        	aud.setVisibility(View.VISIBLE);
        }
        else{
        	aud.setVisibility(View.INVISIBLE);
        }
        aud.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*
				 // Mandamos el evento a Google Analytics
				 EasyTracker easyTracker = EasyTracker.getInstance(mContext);
				 easyTracker.send(MapBuilder
				      .createEvent("Acci�n UI",     // Event category (required)
				                   "Boton clickeado",  // Event action (required)
				                   "Escuchar audio desde carrusel",   // Event label
				                   null)            // Event value
				      .build()
				  );
				  */
				
				// Paramos si hubiera alg�n audio reproduci�ndose
				Audio.getInstance(getActivity().getBaseContext()).releaseResources();
				
				// Lo reproducimos		
				if (uri_audio != null){
					new PlayAudioTask().execute(uri_audio);	
				}
			}
		});
        
        // Nombre de usuario
        TextView user_na = (TextView) rootView.findViewById(R.id.txt_escandalo_name_user);
        user_na.setText(user_name);
        
        // Fecha
        TextView dat = (TextView) rootView.findViewById(R.id.txt_escandalo_date);
        dat.setText(changeFormatDate(date));  
        
        // Compartir 
        ImageView share = (ImageView) rootView.findViewById(R.id.img_escandalo_compartir);
        share.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				/*
				// Mandamos el evento a Google Analytics
				 EasyTracker easyTracker = EasyTracker.getInstance(mContext);
				 easyTracker.send(MapBuilder
				      .createEvent("Acci�n UI",     // Event category (required)
				                   "Boton clickeado",  // Event action (required)
				                   "Compartir esc�ndalo desde carrusel",   // Event label
				                   null)            // Event value
				      .build()
				  );
				  */
				
				 // Creamos un menu para elegir entre compartir y denunciar foto
				 final CharSequence[] opciones_compartir = {"Compartir sc�ndalOh!", "Reportar sc�ndalOh!"};
				 AlertDialog.Builder dialog_compartir = new AlertDialog.Builder(getActivity());
				 dialog_compartir.setTitle("Selecciona opci�n");
				 dialog_compartir.setItems(opciones_compartir, new DialogInterface.OnClickListener() {
			            @Override
			            public void onClick(DialogInterface dialog, int item) {
			            	
			            	// Compartir sc�ndalOh
			                if (opciones_compartir[item].equals("Compartir sc�ndalOh!")) {
			    				// Paramos si hubiera alg�n audio reproduci�ndose
			    				Audio.getInstance(getActivity().getBaseContext()).releaseResources();
			    				
			    				// Compartimos la foto
			    				Uri screenshotUri = Uri.parse(url_big);	
			    				new ShareImageTask(getActivity().getBaseContext(), title).execute(screenshotUri.toString());	      			   
			                } 
			                
			                // Reportar foto
			                else if (opciones_compartir[item].equals("Reportar sc�ndalOh!")) {
			                	
			                	// Si el usuario est� logueado
			                	if (MyApplication.logged_user){
				                	// Creamos un menu para elegir el tipo de report
				                	final CharSequence[] opciones_reportar = {"Material ofensivo", "Spam", "Copyright"};
				                	AlertDialog.Builder dialog_report = new AlertDialog.Builder(getActivity());
				                	dialog_report.setTitle("Reportar esta foto por");
				                	dialog_report.setItems(opciones_reportar, new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int item) {

											// Material ofensivo
											if (opciones_reportar[item].equals("Material ofensivo")){
												chosen_report = 2;
											}
											// Spam
											else if (opciones_reportar[item].equals("Spam")){
												chosen_report = 3;
											}
											// Copyright
											else if (opciones_reportar[item].equals("Copyright")){
												chosen_report = 1;
											}	
											new ReportPhoto(getActivity().getBaseContext()).execute();
										}
									});
				                	dialog_report.show();
			                	}
			                	// No est� logueado
			                	else{
			    		        	Toast toast = Toast.makeText(getActivity().getBaseContext(), "Debes iniciar sesi�n para reportar", Toast.LENGTH_LONG);
			    		        	toast.show();        
			                	}

			                } 
			            }
			        });
				 dialog_compartir.show();			
			}
		});
        
        // Devolvemos la vista
        return rootView;
    }
    
   
    /**
     * Transforma una fecha con formato AAAA-MM-DDTHH:MM:SS a formato DD-MM-AAAA
     * @param date Fecha a transformar
     * @return
     */
    private String changeFormatDate(String date){
        String date_without_time = (date.split("T",2))[0];   
        String year = date_without_time.split("-",3)[0];
        String month = date_without_time.split("-",3)[1];
        String day = date_without_time.split("-",3)[2];
        String final_date = day + "-" + month + "-" + year;     
        return final_date;
    }
    
    

    
    public int getNumComments(){
    	return num_comments;
    }
    
    
    
	/**
	 * Comparte un esc�ndalo
	 *
	 */
	private class ShareImageTask extends AsyncTask<String, String, String> {
	    private Context context;
	    private ProgressDialog pDialog;
	    URL myFileUrl;
	    String title;
	    Bitmap bmImg = null;
	    Intent share;
	    File file;

	    public ShareImageTask(Context context, String title) {
	        this.context = context;
	        this.title = title;
	    }

	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();

	        pDialog = new ProgressDialog(getActivity());
	        pDialog.setMessage("Preparando para compartir ...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(false);
	        pDialog.show();		     
	    }

	    @Override
	    protected String doInBackground(String... args) {
	    	
	    	// Obtenemos la foto desde la url de amazon
	        try {
	            myFileUrl = new URL(args[0]);
	            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
	            conn.setDoInput(true);
	            conn.connect();
	            InputStream is = conn.getInputStream();
	            bmImg = BitmapFactory.decodeStream(is);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
	        try {
	            String path = myFileUrl.getPath();
	            String idStr = path.substring(path.lastIndexOf('/') + 1);
	            File filepath = Environment.getExternalStorageDirectory();
	            File dir = new File(filepath.getAbsolutePath()+ "/Sc�ndalOh/");
	            dir.mkdirs();
	            String fileName = idStr;
	            // Guardamos la ruta de la foto para m�s adelante eliminarla
	            MyApplication.FILES_TO_DELETE.add(filepath.getAbsolutePath() + "/Sc�ndalOh/" + idStr);
	            file = new File(dir, fileName);
	            FileOutputStream fos = new FileOutputStream(file);
	            bmImg.compress(CompressFormat.JPEG, 100, fos);
	            fos.flush();
	            fos.close();

	        } catch (Exception e) {
	            e.printStackTrace();
	            /*
	             // Mandamos la excepcion a Google Analytics
				EasyTracker easyTracker = EasyTracker.getInstance(context);
				easyTracker.send(MapBuilder.createException(new StandardExceptionParser(context, null) // Context and optional collection of package names to be used in reporting the exception.
				                       .getDescription(Thread.currentThread().getName(),                // The name of the thread on which the exception occurred.
				                       e),                                                             // The exception.
				                       false).build());
				                       */
	        }

	        return null;
	    }

	    @Override
	    protected void onPostExecute(String args) {
	    	// Quitamos el progress dialog
	        pDialog.dismiss();  
	        
	        // Ejecutamos el intent de compartir
	        share = new Intent(Intent.ACTION_SEND);		        
	        share.putExtra(Intent.EXTRA_SUBJECT, "Deber�as ver esto. �Qu� esc�ndalo!");
	        share.putExtra(Intent.EXTRA_TEXT, title);
	        share.putExtra(Intent.EXTRA_TITLE, title);	        
	        share.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(file));
	        share.setType("image/jpeg");
	        getActivity().startActivityForResult(Intent.createChooser(share, "Compartir sc�ndalOh! con..."), MainActivity.SHARING);
	    }
	}	
	
	
	
	/**
	 * Guarda una foto en la galer�a
	 *
	 */
	private class SaveImageTask extends AsyncTask<String, String, String> {
	    private Context context;
	    private ProgressDialog pDialog;

	    public SaveImageTask(Context context) {
	        this.context = context;
	    }

	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();

	        pDialog = new ProgressDialog(context);
	        pDialog.setMessage("Guardando ...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(false);
	        pDialog.show();		     
	    }

	    @Override
	    protected String doInBackground(String... args) {
	        try {
		    	// Obtenemos la foto desde la url
            	bitma = ImageUtils.getBitmapFromURL(args[0]);

	        } catch (Exception e) {
	            e.printStackTrace();
	            /*            
	             // Mandamos la excepcion a Google Analytics
				EasyTracker easyTracker = EasyTracker.getInstance(context);
				easyTracker.send(MapBuilder.createException(new StandardExceptionParser(context, null) // Context and optional collection of package names to be used in reporting the exception.
				                       .getDescription(Thread.currentThread().getName(),                // The name of the thread on which the exception occurred.
				                       e),                                                             // The exception.
				                       false).build());
				 */
	        }

	        return null;
	    }

	    @Override
	    protected void onPostExecute(String args) {
	    	// Quitamos el progress dialog
	        pDialog.dismiss();  
	        
	        // Guardamos la foto en la galer�a
			ImageUtils.saveBitmapIntoGallery(bitma, context);	
	    }
	}	
	
	
	
	
	
	

	/**
	 * Reporta una foto
	 *
	 */
	private class ReportPhoto extends AsyncTask<Void,Integer,Integer> {
		 
	    private ProgressDialog pDialog;
		private Context mContext;
		
	    public ReportPhoto (Context context){
	         mContext = context;
	         any_error = false;
	    }
		
		@Override
		protected void onPreExecute(){
			// Mostramos el ProgressDialog
	        pDialog = new ProgressDialog(getActivity());
	        pDialog.setMessage("Reportando sc�ndalOh! ...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(false);
	        pDialog.show();	
		}
		
		@Override
	    protected Integer doInBackground(Void... params) {
	 
	    	HttpEntity resEntity;
	    	String urlString = MyApplication.SERVER_ADDRESS + "api/v1/photocomplaint/";        

	        HttpResponse response = null;
	        
	        try{
	             HttpClient client = new DefaultHttpClient();
	             HttpPost post = new HttpPost(urlString);
	             post.setHeader("Content-Type", "application/json");
	             
	             JSONObject dato = new JSONObject();
	             
	             dato.put("user", MyApplication.resource_uri);
	             dato.put("photo", "/api/v1/photo/" + id +"/");
	             dato.put("category", chosen_report);

	             // Formato UTF-8 (�,�,�,...)
	             StringEntity entity = new StringEntity(dato.toString(),  HTTP.UTF_8);
	             post.setEntity(entity);

	             response = client.execute(post);
	             resEntity = response.getEntity();
	             final String response_str = EntityUtils.toString(resEntity);
	             
	             Log.i("WE",response_str);
	        }
	        
	        catch (Exception ex){
	             Log.e("Debug", "error: " + ex.getMessage(), ex);
	             any_error = true; // Indicamos que hubo alg�n error
	             
	             /*
				// Mandamos la excepcion a Google Analytics
				EasyTracker easyTracker = EasyTracker.getInstance(mContext);
				easyTracker.send(MapBuilder.createException(new StandardExceptionParser(mContext, null) // Context and optional collection of package names to be used in reporting the exception.
					                       .getDescription(Thread.currentThread().getName(),                // The name of the thread on which the exception occurred.
					                       ex),                                                             // The exception.
					                       false).build());  
				*/
	        }
	        
	        if (any_error){
	        	return 666;
	        }
	        else{
		        // Devolvemos el resultado 
		        return (response.getStatusLine().getStatusCode());
	        }
	    }

		
		@Override
	    protected void onPostExecute(Integer result) {
			
			// Quitamos el ProgressDialog
			if (pDialog.isShowing()) {
		        pDialog.dismiss();
		    }
			
			// Si hubo alg�n error mostramos un mensaje
			if (any_error){
				Log.v("WE","report no enviado");
				Toast toast = Toast.makeText(mContext, "Lo sentimos, hubo un error inesperado", Toast.LENGTH_SHORT);
				toast.show();
			}
			else{
				
				// Si es codigo 2xx --> OK
				if (result >= 200 && result <300){
		        	Log.v("WE","report enviado");
		        	
					Toast toast = Toast.makeText(mContext, "Report enviado correctamente", Toast.LENGTH_SHORT);
					toast.show();      	
		        }
		        else{
		        	Log.v("WE","report no enviado");
		        	Toast toast;
		        	toast = Toast.makeText(mContext, "Hubo alg�n error enviando el comentario", Toast.LENGTH_LONG);
		        	toast.show();        	
		        }	      
			}
	    }
	}
	
	
	/**
	 * Reproduce el audio
	 *
	 */
	private class PlayAudioTask extends AsyncTask<String,Integer,Boolean> {
		 
		@Override
	    protected Boolean doInBackground(String... params) {
	    	
	    	Audio.getInstance(getActivity().getBaseContext()).startPlaying("http://scandaloh.s3.amazonaws.com/" + params[0]);							
	        return false;
	    }	
	}
}
