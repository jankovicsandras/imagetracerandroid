package jankovicsandras.imagetracerapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import jankovicsandras.imagetracerandroid.ImageTracerAndroid;

public class ImagetracerActivity extends Activity {

	File imageTracerAppFolder; // sdcard/ImageTracerApp/ directory
	DisplayMetrics displaymetrics;
	
	Optionselector os;
	TextView tv;
	WebView wv;
	
	private static final int CAMERA_REQUEST = 1888; // field
	String mimeType = "text/html"; 
	String encoding = "utf-8";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Window style and metrics
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		
		// Creating sdcard/ImageTracerAndroid/ folder and copying all assets to it
		checkassets();

		// SVG options range
		final HashMap<String,Float[]> optionsrange = new HashMap<String,Float[]>();
		optionsrange.put("ltres",new Float[]{1f,0f,10f});
		optionsrange.put("qtres",new Float[]{1f,0f,10f});
		optionsrange.put("pathomit",new Float[]{8f,0f,64f});// int
		optionsrange.put("numberofcolors",new Float[]{16f,2f,64f});// int
		optionsrange.put("mincolorratio",new Float[]{0.02f,0f,0.1f});
		optionsrange.put("colorquantcycles",new Float[]{3f,1f,10f});// int
		optionsrange.put("scale",new Float[]{1f,0.01f,100f});
		
		// UI
		ScrollView sv = new ScrollView(getApplicationContext());
		
		// Layout
		LinearLayout ll = new LinearLayout(getApplicationContext());
		ll.setOrientation(LinearLayout.VERTICAL);
		sv.addView(ll);
		
		// TextView for log
		// tv = new TextView(getApplicationContext()); tv.setText("Hi! ☺");ll.addView(tv);
		
		// WebView to show SVG
		wv = new WebView(getApplicationContext());
		ll.addView(wv);
		
		// Button 1
		Button b1 = new Button(getApplicationContext());
		b1.setText("Take a picture");
		b1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				try {
					// Starting an Intent to take a picture
					Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
					startActivityForResult(cameraIntent, CAMERA_REQUEST); 
				}catch(Exception e){ e.printStackTrace(); }
			}
		});
		ll.addView(b1);
		
		// Button 2
		Button b2 = new Button(getApplicationContext());
		b2.setText("Test panda.png");
		b2.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				try {
					
					// Tracing panda.png to an SVG string
					String svgstring = ImageTracerAndroid.imageToSVG( imageTracerAppFolder.getAbsolutePath()+"/"+"panda.png" , os.getvals());
					
					// Saving SVG string as panda.svg
					ImageTracerAndroid.saveString(imageTracerAppFolder.getAbsolutePath()+"/"+"panda.svg", svgstring );
					
					// Displaying SVG in the WebView
					wv.loadDataWithBaseURL("", svgstring, mimeType, encoding,""); 
					
				}catch(Exception e){ log(" Error tracing panda.png "+e.toString()); e.printStackTrace(); }
			}
		});
		ll.addView(b2);
		
		// Optionselector widgets
		os = new Optionselector(optionsrange);
		ll.addView(os.getView());
		
		// Displaying UI
		setContentView(sv);
		
	}// End of onCreate()
	
	public void log(String msg){ System.out.println(msg); }
	
	private void checkassets(){
		// Creating App folder if it does not 
		imageTracerAppFolder = new File((Environment.getExternalStorageDirectory()).getAbsolutePath()+"/ImageTracerApp/");
		if(!imageTracerAppFolder.exists()){
			log("Creating: "+imageTracerAppFolder.getAbsolutePath());
			imageTracerAppFolder.mkdirs();
		}
		
		AssetManager assetManager = getAssets();
		String[] filelist = {"panda.png","smiley.png"};
		
		// filelist check then copy
		if (filelist != null){
			
			byte[] buffer = new byte[16*1024];
			InputStream in = null;
			OutputStream out = null;
			int read;
			
			// Copying files
			for (String filename : filelist) {
				try {
					File outfile = new File(imageTracerAppFolder, filename);
					if(!outfile.exists()){
						in = assetManager.open(filename);
						out = new FileOutputStream(outfile);
						while((read = in.read(buffer)) != -1){ out.write(buffer, 0, read); }
						log("Successfully copied: "+filename);
					}
				}catch(Exception e){
					log("!!!! ERROR: failed to copy: "+filename+" "+e.toString());
				}finally{
					if(in != null){try{ in.close();}catch(Exception e){}}
					if(out!= null){try{out.close();}catch(Exception e){}}
				}
			}// End of file loop
		}// End of filelist check
		
	}// End of checkassets()
	
	public class Optionselector{
		String name;
		HashMap<String,Float[]> opt;
		TableLayout tl;
		HashMap<String,TableRow> trs;
		HashMap<String,TextView> tvs;
		HashMap<String,EditText> ets;
		HashMap<String,SeekBar> sbs;
		
		public Optionselector(HashMap<String,Float[]> mopt){
			opt = mopt;
			
			tl = new TableLayout(getApplicationContext());
			
			trs = new HashMap<String,TableRow>();
			tvs = new HashMap<String,TextView>();
			ets = new HashMap<String,EditText>();
			sbs = new HashMap<String,SeekBar>();
			
			for(String s : opt.keySet()){
				
				final String thiss = s;
				
				trs.put(thiss,new TableRow(getApplicationContext()));
				trs.get(thiss).setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.MATCH_PARENT));
				
				tvs.put(thiss,new TextView(getApplicationContext()));
				tvs.get(thiss).setText(s);
				trs.get(thiss).addView(tvs.get(thiss),new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));
				
				ets.put(thiss, new EditText(getApplicationContext()));
				ets.get(thiss).setText(Float.toString(opt.get(thiss)[0]));
				trs.get(thiss).addView(ets.get(thiss),new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
				
				sbs.put(thiss, new SeekBar(getApplicationContext()));
				sbs.get(thiss).setProgress(50);
				sbs.get(thiss).setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
						float thisval=0;
						if(progress<50){
							thisval = opt.get(thiss)[1] + (float)(progress) * ((opt.get(thiss)[0]-opt.get(thiss)[1])/50f);
						}else{
							thisval = opt.get(thiss)[0] + (float)(progress-50) * ((opt.get(thiss)[2]-opt.get(thiss)[0])/50f);
						}
						ets.get(thiss).setText(String.format("%.2f", thisval));
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar){}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar){}
					
				});
				trs.get(thiss).addView(sbs.get(thiss),new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));
				
				tl.addView(trs.get(thiss));
			}// End of options loop
			
		}// End of constructor
		
		public View getView(){ return tl; }
		
		public HashMap<String,Float> getvals(){
			HashMap<String,Float> o = new HashMap<String,Float>();
			for(String s : opt.keySet()){
				float v = opt.get(s)[0];
				try{ v = Float.parseFloat(ets.get(s).getText().toString()); }catch(Exception e){log("Error parsing float. "+s+" ["+ets.get(s).getText().toString()+"]");}
				o.put(s,v);
			}
			return o;
		}
		
	}// End of optionselector class
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
		super.onActivityResult(requestCode, resultCode, data); 
		if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {  
			Bitmap picture = (Bitmap) data.getExtras().get("data");
			String svgstring = "";
			try {
				svgstring = ImageTracerAndroid.imageToSVG( picture , os.getvals());
				ImageTracerAndroid.saveString(imageTracerAppFolder.getAbsolutePath()+"/"+timestamp()+".svg", svgstring );
				wv.loadDataWithBaseURL("", svgstring, mimeType, encoding,"");
			} catch (Exception e) { log(" Error tracing photo "+e.toString()); e.printStackTrace(); }
		}
	} 
	
	public String timestamp(){ return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date()); }
	
}// End of ImagetracerActivity