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


public class SpeciesAdapter extends BaseAdapter implements SectionIndexer {
	private static final String TAG = "SpeciesAdapter";    
    private Activity activity;
    private String[] data;
	public static ViewHolder holder;
    private static LayoutInflater inflater=null;
	private String mSections = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    
    public SpeciesAdapter(Activity a, String[] d) {
        activity = a;
        data = d;        
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return SpeciesList.speciesDbLen;  
        
    }

    public Object getItem(int position) {
        Log.d(TAG, "getItem:" + position + " data[position]:" +  data[position]);
        return data[position];  // modified 12/31/13 -- was return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
 //   	Log.d(TAG, "getView outside of OnClickListener position:" + position);
        if(vi==null) {
//        	Log.d(TAG, "getView vi is null");
            vi = inflater.inflate(R.layout.species_list, null);
        	holder = new ViewHolder();
            holder.text = (TextView) vi.findViewById(R.id.text);
            holder.check = (CheckBox) vi.findViewById(R.id.check);
            vi.setTag(holder);
        } else {
 //       	Log.d(TAG, "getView vi is NOT null");
            holder = (ViewHolder) vi.getTag();        	
        }
        holder.text.setText(SpeciesList.speciesCombined[position]);
        holder.check.setChecked(SpeciesList.chk[position]);
        holder.check.setId(position);  // this setId position is different than getId below ???
        holder.check.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
		    	int pos = v.getId();
		    	SpeciesList.chk[pos] = !SpeciesList.chk[pos]; 
		    	Log.d(TAG, "getView onClick position:" + pos + " ck:" + SpeciesList.chk[pos]);
		    	for (int i=0; i<SpeciesList.speciesDbLen; i++) {
		    		if (SpeciesList.chk[i] == true) {
		    			SpeciesList.selectedSpecies = i;  // do i use this ??
     	    	   	 	Main.existingRef = Main.speciesRef[i];     	    	   	 	
     	    	   	 	Main.specOffset = i;
     	    	   	 	Log.d(TAG, "selectedSpecies:" + SpeciesList.speciesCombined[i] + " Ref:" + Main.existingRef);
		    		}    		    		
		    	}

            }

        }); 
        return vi;
    }

		CheckBox check;
		TextView text;

		public int getPositionForSection(int section) {
			// If there is no item for current section, previous section will be selected
			for (int i = section; i >= 0; i--) {
				for (int j = 0; j < getCount(); j++) {
					if (StringMatcher.match(String.valueOf(SpeciesList.speciesCombined[j].charAt(0)), String.valueOf(mSections.charAt(i))))
						return j;
				}
			}
			return 0;
		}

		public int getSectionForPosition(int position) {
			// TODO Auto-generated method stub
 	   	 	Log.d(TAG, "getSectionForPosition position:" + position);
//			EditSpecies.chk[position] = !EditSpecies.chk[position];  // this doesn't do anything
			return 0;
		}

		public Object[] getSections() {
			String[] sections = new String[mSections.length()];
			for (int i = 0; i < mSections.length(); i++)
				sections[i] = String.valueOf(mSections.charAt(i));
			return sections;
		}

}


