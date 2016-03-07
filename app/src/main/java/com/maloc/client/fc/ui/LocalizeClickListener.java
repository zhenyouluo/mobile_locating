package com.maloc.client.fc.ui;

import com.maloc.client.R;
import com.maloc.client.localization.IndoorLocalizor;
import com.maloc.client.localization.LocalizationMessageType;
import com.maloc.client.localizationService.Position;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
/**
 * 定位控制类
 * @author xhw Email:xxyx66@126.com
 */
public class LocalizeClickListener implements OnClickListener{

	public static int DEFAULT_RECONNECT=3;
	
	private State state;
	private IndoorMapImageView indoorMap;
	private ProgressDialog pd; 
	private IndoorLocalizor localizor;
	private ImageView button;
	
	private int restart=0;
	
	
	private Handler handler=new Handler(){
		
		@Override  
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法  
			super.handleMessage(msg);
			//Log.i("msg",msg.toString());
			Position position=null;
			
			switch(msg.what)
            {
            case LocalizationMessageType.INIT_SUCCESS:
            	if(pd!=null)
    			{
    				pd.dismiss();
    				//pd=null;
    			}
            	position=(Position)msg.getData().getSerializable("position");
            	indoorMap.setPosition(position);
            	indoorMap.invalidate();
            	restart=0;
            	localizor.start();
            	Toast.makeText(button.getContext(), "Init success. Walk to get your position!",Toast.LENGTH_LONG).show();
            	
            	//Log.i("LocalizationListenr", "position:"+position.getX()+","+position.getY());
            	/*new AlertDialog.Builder(indoorMap.getContext()) 
        		.setTitle("Hint")
        		.setMessage("Init success. Walk to get your position!")
        		.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

						localizor.start();
					}
        			
        		}).show();*/
            	break;
            case LocalizationMessageType.INIT_FAIL:
            	if(pd!=null)
    			{
    				pd.dismiss();
    				//pd=null;
    			}
                onFailed("Can't Access to Localization Server.");
            	restart--;
            	if(restart>0)
            	{
            		reconnect();
            	}
            	break;
            case LocalizationMessageType.BAD_WIFI_SCAN:
            	if(pd!=null)
    			{
    				pd.dismiss();
    				//pd=null;
    			}
            	restart=0;
            	onStop();
            	new AlertDialog.Builder(indoorMap.getContext()) 
        		.setTitle("Warn")
        		.setMessage("You are too far away from this building. Try agian.")
        		.setPositiveButton("OK", null).show();
            	break;
            case LocalizationMessageType.OUT_SERVICE_RANGE:
            	if(pd!=null)
    			{
    				pd.dismiss();
    				//pd=null;
    			}
            	restart=0;
            	onStop();
            	new AlertDialog.Builder(indoorMap.getContext()) 
        		.setTitle("Warn")
        		.setMessage("You are not in this building.")
        		.setPositiveButton("OK", null).show();
            	break;
            case LocalizationMessageType.MAG_SUCCESS:
            	
            	position=(Position)msg.getData().getSerializable("position");
            	indoorMap.setPosition(position);
            	indoorMap.invalidate();
            	break;
            	
            case LocalizationMessageType.MAG_TIMEOUT:
            	//Toast.makeText(button.getContext(), "Magnetci Localization Failed.",Toast.LENGTH_SHORT).show();
            	onFailed("Can't Access to Localization Server.");
            	if(restart<=0)
            	{
            		restart=DEFAULT_RECONNECT;
            		reconnect();
            	}
            	break;
            case LocalizationMessageType.MAG_FAIL:
            	onFailed("LocalizationEngine Internal Error.");
            	if(restart<=0)
            	{
            		restart=1;
            		restart();
            	}
            	break;
            case LocalizationMessageType.WIFI_SUCCESS:
            	position=(Position)msg.getData().getSerializable("position");
            	indoorMap.setPosition(position);
            	indoorMap.invalidate();
            	break;
            case LocalizationMessageType.WIFI_FAIL:
            	//do nothing
            	break;
            case LocalizationMessageType.WIFI_NOT_READY:
            	break;
            default:
            	onStop();
            	Log.i("LocalizaListener",""+msg.what);
            }
        }
		
	};
	
	
	
	
	public LocalizeClickListener(Activity activity,State state,IndoorMapImageView imageView,ImageView button) {

		this.state=state;
		this.indoorMap=imageView;
		this.button=button;
		localizor=new IndoorLocalizor(activity,handler);
	}

	@Override
	public void onClick(View v) {

		//Log.i("LocalizeListern Click", ""+state.curentControlState);
		if(state.curentControlState!=ControlState.LOCALIZE
				&&state.curentControlState!=ControlState.NONE
				&&state.curentControlState!=ControlState.STOP_VALIDATE)
		{
			Toast.makeText(v.getContext(), "Current Collecting is not completed!",Toast.LENGTH_SHORT).show();
			return ;
		}
		else if(state.curentControlState==ControlState.LOCALIZE)
		{
			this.onStop();
			return;
		}
		button.setImageDrawable(button.getResources().getDrawable(R.drawable.stop));
		state.curentControlState=ControlState.LOCALIZE;
		pd = ProgressDialog.show(v.getContext(), "Initializing Location", "Wait……");
		restart=0;
		localizor.init();
		indoorMap.invalidate();
	}
	
	public void onStop()
	{
		Toast.makeText(indoorMap.getContext(), "Stop Localize.",Toast.LENGTH_SHORT).show();
		localizor.stop();
		state.curentControlState = ControlState.NONE;
		button.setImageDrawable(button.getResources().getDrawable(R.drawable.localize));
	}
	
	private void onFailed(String msg)
	{
		Toast.makeText(button.getContext(), msg,Toast.LENGTH_LONG).show();
		onStop();
    	/*new AlertDialog.Builder(indoorMap.getContext()) 
		.setTitle("Warning")
		.setMessage("Localization Failed. Maybe can't access to loclization server. Check your network.")
		.setPositiveButton("OK", null)
		.show();*/
	}

	private void restart() {

		button.setImageDrawable(button.getResources().getDrawable(R.drawable.stop));
		state.curentControlState=ControlState.LOCALIZE;
		pd = ProgressDialog.show(button.getContext(), "Restart Localization", "Wait……"); 	
		localizor.init();
	}
	
	private void reconnect() {

		button.setImageDrawable(button.getResources().getDrawable(R.drawable.stop));
		state.curentControlState=ControlState.LOCALIZE;
		pd = ProgressDialog.show(button.getContext(), "Reconnect to Server "+(DEFAULT_RECONNECT-restart+1)+"/"+DEFAULT_RECONNECT, "Wait……"); 	
		localizor.init();
	}
}
