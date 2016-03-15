package com.modelsw.birdingviamic;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class RegionAdapter extends BaseAdapter implements SectionIndexer {
	private static final String TAG = "RegionAdapter";    
    

    private Activity activity;
    private String[] data;
	public static ViewHolder holder;
    private static LayoutInflater inflater=null;
	

	public RegionAdapter(Activity a, String[] d) {
        activity = a;
        data = d;        
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public int getCount() {
		return RegionList.regionDbLen;
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
            vi = inflater.inflate(R.layout.region_list, parent, false);
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
        holder.text.setText(RegionList.regionCombined[position]);
        holder.check.setChecked(RegionList.chk[position]);
        holder.check.setId(position);  // this setId position is different than getId below ???

        holder.check.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		int pos = v.getId();
        		RegionList.chk[pos] = !RegionList.chk[pos]; 
        		Log.d(TAG, "getView onClick position:" + pos + " ck:" + RegionList.chk[pos]);
        		for (int i=0; i<RegionList.regionDbLen; i++) {
        			if (RegionList.chk[i] == true) {
        				RegionList.selectedRegion = i;
        				RegionList.regionLink = RegionList.regionCombined[i];
     	   	   	 		Log.d(TAG, "selectedRegion:" + RegionList.regionLink );
     	   	   	 		RegionList.isRegionSelected = true;
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
