package com.modelsw.birdingviamic;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class WebAdapter extends BaseAdapter implements SectionIndexer {
	private static final String TAG = "WebAdapter";    
    

    private Activity activity;
    private String[] data;
	public static ViewHolder holder;
    private static LayoutInflater inflater=null;
	

	public WebAdapter(Activity a, String[] d) {
        activity = a;
        data = d;        
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public int getCount() {
		return WebList.webDbLen;
	}

	public Object getItem(int position) {
        //Log.d(TAG, "getItem:" + position + " data[position]:" +  data[position]);
        return data[position]; 
	}

	public long getItemId(int position) {
        return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        //Log.d(TAG, "getView in WebAdapter position:" + position);
        if(vi==null) {
        	//Log.d(TAG, "getView vi is null");
            vi = inflater.inflate(R.layout.web_list, parent, false);
        	holder = new ViewHolder();
            holder.text = (TextView) vi.findViewById(R.id.text);
            holder.check = (CheckBox) vi.findViewById(R.id.check);
            vi.setTag(holder);
        	//Log.d(TAG, "vi now has a tag:" + vi.getTag().toString() + " holder.text:" + holder.text.toString() 
    		//		+ " holder.check:" + holder.check.toString());
        } else {
            holder = (ViewHolder) vi.getTag();        	
        	//Log.d(TAG, "getView vi is NOT null:" + vi.getTag().toString());
        }
        holder.text.setText(WebList.webCombined[position]);
        holder.check.setChecked(WebList.chk[position]);
        holder.check.setId(position);  // this setId position is different than getId below ???

        holder.check.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		int pos = v.getId();
        		WebList.chk[pos] = !WebList.chk[pos]; 
        		Log.d(TAG, "getView onClick position:" + pos + " ck:" + WebList.chk[pos]);
        		for (int i=0; i<WebList.webDbLen; i++) {
        			if (WebList.chk[i] == true) {
        				WebList.selectedWeb = i;
        				WebList.webLink = WebList.webCombined[i];
     	   	   	 		Log.d(TAG, "selectedWeb:" + WebList.webLink );
     	   	   	 		WebList.isWebSiteSelected = true;
        			}    		    		
        		}
        	}
        }); 
        return vi;
    }



	public int getPositionForSection(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Object[] getSections() {
		// TODO Auto-generated method stub
		return null;
	}

}
