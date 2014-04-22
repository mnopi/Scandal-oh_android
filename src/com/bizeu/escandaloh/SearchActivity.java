package com.bizeu.escandaloh;

import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.bizeu.escandaloh.adapters.SearchAdapter;
import com.bizeu.escandaloh.model.Notification;
import com.bizeu.escandaloh.model.Search;
import com.bizeu.escandaloh.notifications.NotificationsActivity;
import com.bizeu.escandaloh.util.Connectivity;
import com.mnopi.scandaloh_escandalo_humor_denuncia_social.R;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class SearchActivity extends SherlockActivity {

	private static int NUM_SEARCHS_TO_LOAD = 20;
	public static String PHOTO_ID = "photo_id";
	
	private EditText edit_search;
	private LinearLayout ll_clean_text;
	private LinearLayout ll_screen;
	private ListView list_searches;
	private LinearLayout ll_loading;
	private LinearLayout ll_list_searchs;
	
	private ArrayList<Search> array_search = new ArrayList<Search>();
	private SearchAdapter searchAdapter;
	private boolean any_error = false;
	private boolean there_are_more_searchs = true;
	private Context mContext;
	private String meta_next_search = null;
	private SearchScandalsTask getSearchsAsync;
	
	/**
	 * onCreate
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.searches);
		
		mContext = this;
		
		// ACTION BAR
		ActionBar actBar = getSupportActionBar();
		actBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
		View view = getLayoutInflater().inflate(R.layout.action_bar_search, null);
		actBar.setCustomView(view);
		// Activamos el logo del menu para el menu lateral
		actBar.setHomeButtonEnabled(true);
		actBar.setDisplayHomeAsUpEnabled(true);
		actBar.setIcon(R.drawable.logo_blanco);
		
		edit_search = (EditText) findViewById(R.id.edit_actionbarsearch_search);
		ll_clean_text = (LinearLayout) findViewById(R.id.ll_actionbarsearch_cancel);
		ll_screen = (LinearLayout) findViewById(R.id.ll_search_screen);
		list_searches = (ListView) findViewById(R.id.list_search_scandaloh);
		ll_loading = (LinearLayout) findViewById(R.id.ll_search_loading);
		ll_list_searchs = (LinearLayout) findViewById(R.id.ll_search_list);
		
		searchAdapter = new SearchAdapter(mContext, R.layout.search, array_search);
		list_searches.setAdapter(searchAdapter);
		
		// Mostramos el teclado
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		
		// Si hay algo escrito mostramos la "x" para limpiar el texto
		edit_search.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) {
				if (s.length() == 0){
					ll_clean_text.setVisibility(View.INVISIBLE);
				}
				else{
					ll_clean_text.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		// Al pulsar el bot�n de buscar
		edit_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					// Si hay conexi�n
					if (Connectivity.isOnline(mContext)) {
						if (edit_search.getText().toString().length() > 0){
							// Cancelamos si se estuviesen obteniendo otros searchs
							cancelGetSearchs();
							// Mostramos el loading
							showLoading();
							// Ocultamos el teclado
							hideKeyboard();
							// Iniciamos la b�squeda
							array_search = new ArrayList<Search>();
							searchAdapter = new SearchAdapter(mContext, R.layout.search, array_search);
							list_searches.setAdapter(searchAdapter);
							getSearchsAsync = new SearchScandalsTask(edit_search.getText().toString());
							getSearchsAsync.execute();
						}
					}

					// No hay conexi�n
					else {
						Toast toast = Toast.makeText(mContext,
								R.string.no_dispones_de_conexion, Toast.LENGTH_SHORT);
						toast.show();
					}
					
					return true;
				}
				
				return false;
			}
		});

		
		// Al pulsar la "x" limpiamos el texto introducido
		ll_clean_text.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String written_text = edit_search.getText().toString();
				if (written_text.length() >0){
					edit_search.setText("");
				}
				// Ocultamos la X
				ll_clean_text.setVisibility(View.GONE);				
			}
		});
		
		
		// Obtener siguientes b�squedas
		list_searches.setOnScrollListener(new OnScrollListener() {
			
	        @Override
	        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	           
	        	if ((firstVisibleItem + visibleItemCount == searchAdapter.getCount() - 3) && there_are_more_searchs) {
	        		
	            	if (Connectivity.isOnline(mContext)){
						getSearchsAsync = new SearchScandalsTask(edit_search.getText().toString());
						getSearchsAsync.execute();
	            	}
	            	else{
	            		Toast toast = Toast.makeText(mContext, R.string.no_dispones_de_conexion, Toast.LENGTH_LONG);
	            		toast.show();
	            	}	     			    		
	            }   		
	        }
	        
	        @Override
	        public void onScrollStateChanged(AbsListView view, int scrollState) {
	            // TODO Auto-generated method stub
	        }
	    });
		
		// Al seleccionar un search mostramos el esc�ndalo al que referencia
		list_searches.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			  @Override
			  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				  
				  Search s = (Search) list_searches.getItemAtPosition(position);
				  Intent i = new Intent(SearchActivity.this, ScandalActivity.class);
				  i.putExtra(PHOTO_ID, s.getPhotoId());
				  startActivity(i);	     
			  }
		});
	}


	
	
	/**
	 * onPause
	 */
	@Override
	public void onPause() {
		super.onPause();
		// Ocultamos el teclado
		hideKeyboard();
	}
	
	
	/**
	 * onDestroy
	 */
	@Override
	public void onDestroy(){
		super.onDestroy();
		cancelGetSearchs();
	}
	
	
	/**
	 * onOptionsItemSelected
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
	    	break;
		}
		return true;
	}
	
	
	
	/**
	 * onActivityResult
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	

	/**
	 * B�squeda de esc�ndalos
	 * 
	 */
	
	private class SearchScandalsTask extends AsyncTask<Void, Integer, Integer> {

		String search;

		public SearchScandalsTask(String search) {
			super();
			this.search = search;
	    }
		
		@Override
		protected void onPreExecute() {
			any_error = false;		
		}

		@Override
		protected Integer doInBackground(Void... params) {

			String url = null;
			
			// No hay searchs: obtenemos los primeros
			if (array_search.size() == 0){
				
				url = MyApplication.SERVER_ADDRESS + "/api/v1/photo/";
				url += "?q=" + search;	
				url += "&limit=" + NUM_SEARCHS_TO_LOAD;
			}
			
			// Obtenemos los siguientes searchs
			else{
				// Fin del carrusel: meta nulo indica que no hay m�s searchs
				if (meta_next_search.equals("null")){
					there_are_more_searchs = false;
					return 5;
				}
				url = MyApplication.SERVER_ADDRESS + meta_next_search;
			}

			HttpResponse response = null;

			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet getSearchs = new HttpGet(url);
				getSearchs.setHeader("content-type", "application/json");
				
				// Hacemos la petici�n al servidor
				response = httpClient.execute(getSearchs);
				String respStr = EntityUtils.toString(response.getEntity());
				Log.i("WE", respStr);
				
				// Parseamos los esc�ndalos devueltos
				JSONObject respJson = new JSONObject(respStr);

				// Obtenemos el meta
				JSONObject respMetaJson = respJson.getJSONObject("meta");
				meta_next_search = respMetaJson.getString("next");

				JSONArray searchsObject = respJson.getJSONArray("objects");
				
				// Obtenemos los datos de los esc�ndalos
				for (int i = 0; i < searchsObject.length(); i++) {

					JSONObject searchObject = searchsObject.getJSONObject(i);

					final String id = searchObject.getString("id");
					final String title = new String(searchObject.getString("title").getBytes("ISO-8859-1"), HTTP.UTF_8);
					final String username = searchObject.getString("username");	
					final String img_small = searchObject.getString("img_small");

					runOnUiThread(new Runnable() {
						@Override
						public void run() {							
							// A�adimos el search al ArrayList
							Search search_aux = new Search(id, img_small, title, username);
							array_search.add(search_aux);
						}
					});		
				}
				
			} catch (Exception ex) {
				Log.e("ServicioRest", "Error obteniendo esc�ndalos o comentarios", ex);
				// Hubo alg�n error inesperado
				any_error = true;
			}

			// Si hubo alg�n error devolvemos 666
			if (any_error) {
				return 666;
			} else {
				// Devolvemos el c�digo resultado
				return (response.getStatusLine().getStatusCode());
			}
		}

		@Override
		protected void onPostExecute(Integer result) {

			// Mostramos la lista de searchs
			showListSearchs();
			
			// Si hubo alg�n error inesperado mostramos un mensaje
			if (result == 666) {
				Toast toast = Toast.makeText(mContext,
						R.string.lo_sentimos_hubo, Toast.LENGTH_SHORT);
				toast.show();
			}
			// No hubo ning�n error extra�o
			else {
				// Si es codigo 2xx --> OK 
				searchAdapter.notifyDataSetChanged();
			}
		}
	}
	
	
	/**
	 * Muestra el loading en pantalla
	 */
	private void showLoading(){
		ll_list_searchs.setVisibility(View.GONE);
		ll_loading.setVisibility(View.VISIBLE);
	}
	
	
	/**
	 * Oculta el loading y muestra el listado de searchs
	 */
	private void showListSearchs(){
		ll_list_searchs.setVisibility(View.VISIBLE);
		ll_loading.setVisibility(View.GONE);
	}
	
	/**
	 * Oculta el teclado
	 */
	private void hideKeyboard(){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(ll_screen.getWindowToken(), 0);
	}
	
	
	/**
	 * Cancela si hubiese alguna hebra obteniendo searchs
	 */
	private void cancelGetSearchs() {
		if (getSearchsAsync != null) {
			if (getSearchsAsync.getStatus() == AsyncTask.Status.PENDING|| getSearchsAsync.getStatus() == AsyncTask.Status.RUNNING) {
				getSearchsAsync.cancel(true);
			}
		}
	}
	
}