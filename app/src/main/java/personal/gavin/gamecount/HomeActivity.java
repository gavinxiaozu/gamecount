/**
 * 
 */
package personal.gavin.gamecount;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

/**
 * @author Gavin
 * @version  2013-3-13 下午3:25:24
 */
public class HomeActivity extends Activity {
	protected final String TAG = getClass().getSimpleName();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_game_list);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent i = new Intent(this, GameListActivity.class);
        startActivity(i);
        finish();
        Log.i(TAG, "In Home");
        checkScreen();
    }
	
	private void checkScreen() {
		DisplayMetrics metrics = new DisplayMetrics();
		Display display = this.getWindowManager().getDefaultDisplay();
		display.getMetrics(metrics);
		
		float density  = metrics.density;        // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）  
    	int densityDPI = metrics.densityDpi;     // 屏幕密度（每寸像素：120/160/240/320）  (l,m,h,xh)
    	float xdpi = metrics.xdpi;             
    	float ydpi = metrics.ydpi; 
    	
    	int screenWidthDip = metrics.widthPixels;        // 屏幕宽（dip，如：320dip）  
    	int screenHeightDip = metrics.heightPixels;      // 屏幕宽（dip，如：533dip）  
    	
		String dpi = "dpi";
		dpi = (densityDPI <= 120 ? "l" :
			(densityDPI <= 160 ? "m" :
				(densityDPI <= 240 ? "h" :
					(densityDPI <= 320 ? "xh" : "less")))) + dpi;

		Log.d("DisplayMetrics", "xdpi=" + xdpi + "; ydpi=" + ydpi);  
    	Log.d("DisplayMetrics", "density=" + density + "; densityDPI=" + densityDPI);
    	Log.d("DisplayMetrics", "DPI_type=" + dpi);
    	Log.d("DisplayMetrics", "screenWidthDip=" + screenWidthDip + "; screenHeightDip=" + screenHeightDip);
    	
	}
}
