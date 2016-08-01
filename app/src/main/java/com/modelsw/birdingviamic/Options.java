package com.modelsw.birdingviamic;

import java.io.File;
import java.io.FileOutputStream;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

@TargetApi(11)
public class Options extends AppCompatActivity implements OnClickListener{
	private static final String TAG = "Options";
	public static CheckBox checkAutoFilter;
	private static Boolean isOptionAutoFilter;
	public static CheckBox checkEnhanceMic;
	private static Boolean isEnhanceMic;
	public static CheckBox checkUseSmoothing;
	private static Boolean isUseSmoothing;
	public static CheckBox checkShowDefinition;
	private static Boolean isShowDefinition;
	public static CheckBox checkShowDetail;
	private static Boolean isShowDetail;
	public static CheckBox checkShowWeb;
	private static Boolean isShowWeb;	
	public static CheckBox checkSortByName;
	private static Boolean isSortByName;	
	public static CheckBox checkUseLocation;	
	private static Boolean isUseLocation;	
	public static CheckBox checkUseAudioRecorder;
	private static Boolean isUseAudioRecorder;
    public static CheckBox checkSampleRate;
    private static Boolean isSampleRate;
	public static CheckBox checkStereo;
	private static Boolean isStereo;
	public static CheckBox checkStartRecordScreen;
	private static Boolean isStartRecordScreen;	
	public static CheckBox checkStartRecording;
	private static Boolean isStartRecording;
    //public static CheckBox checkBatchDownload;
    //private static Boolean isBatchDownload;
    public static CheckBox checkLoadDefinition;
    private static Boolean isLoadDefinition;
	public static CheckBox checkDebug;
	private static Boolean isDebug;
	public static CheckBox checkViewDistance;
	private static Boolean isViewDistance;
	public static CheckBox checkViewFrequency;
	private static Boolean isViewFrequency;
	public static CheckBox checkViewEnergy;
	private static Boolean isViewEnergy;
	public static CheckBox checkViewQuality;
	private static Boolean isViewQuality;

	EditText critMult;
    Toolbar toolbar;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.options);

        // action bar toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setLogo(R.drawable.treble_clef_linen);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.teal));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "Navigation Icon tapped");
				finish();
			}
		});

        checkAutoFilter = (CheckBox) findViewById(R.id.autoFilter_check);
        checkAutoFilter.setOnClickListener(this);
	    Options.isOptionAutoFilter = Main.isOptionAutoFilter;
	    checkAutoFilter.setChecked(isOptionAutoFilter);

        checkEnhanceMic = (CheckBox) findViewById(R.id.enhance_mic_check);		
        checkEnhanceMic.setOnClickListener(this);
	    Options.isEnhanceMic = Main.isEnhanceQuality;
	    checkEnhanceMic.setChecked(isEnhanceMic);

        checkUseSmoothing = (CheckBox) findViewById(R.id.use_smoothing_check);
        checkUseSmoothing.setOnClickListener(this);
	    Options.isUseSmoothing = Main.isUseSmoothing;
	    checkUseSmoothing.setChecked(isUseSmoothing);

	    checkShowDefinition = (CheckBox) findViewById(R.id.show_definition_check);		
	    checkShowDefinition.setOnClickListener(this);
	    Options.isShowDefinition = Main.isShowDefinition;
	    checkShowDefinition.setChecked(isShowDefinition);

		checkViewDistance = (CheckBox) findViewById(R.id.view_distance_check);
		checkViewDistance.setOnClickListener(this);
		Options.isViewDistance = Main.isViewDistance;
		checkViewDistance.setChecked(isViewDistance);

		checkViewEnergy = (CheckBox) findViewById(R.id.view_energy_check);
		checkViewEnergy.setOnClickListener(this);
		Options.isViewEnergy = Main.isViewEnergy;
		checkViewEnergy.setChecked(isViewEnergy);

		checkViewFrequency = (CheckBox) findViewById(R.id.view_frequency_check);
		checkViewFrequency.setOnClickListener(this);
		Options.isViewFrequency = Main.isViewFrequency;
		checkViewFrequency.setChecked(isViewFrequency);

		checkViewQuality = (CheckBox) findViewById(R.id.view_quality_check);
		checkViewQuality.setOnClickListener(this);
		Options.isViewQuality = Main.isViewQuality;
		checkViewQuality.setChecked(isViewQuality);

		checkShowDetail = (CheckBox) findViewById(R.id.show_detail_check);
	    checkShowDetail.setOnClickListener(this);
	    Options.isShowDetail = Main.isShowDetail;
	    checkShowDetail.setChecked(isShowDetail);

	    checkShowWeb = (CheckBox) findViewById(R.id.show_web_check);		
	    checkShowWeb.setOnClickListener(this);
	    Options.isShowWeb = Main.isShowWeb;
	    checkShowWeb.setChecked(isShowWeb);

	    checkSortByName = (CheckBox) findViewById(R.id.sort_by_name_check);		
        checkSortByName.setOnClickListener(this);
	    Options.isSortByName = Main.isSortByName;
	    checkSortByName.setChecked(isSortByName);

	    checkUseLocation = (CheckBox) findViewById(R.id.use_location_check);		
        checkUseLocation.setOnClickListener(this);
	    Options.isUseLocation = Main.isUseLocation;
	    checkUseLocation.setChecked(isUseLocation);

	    checkUseAudioRecorder = (CheckBox) findViewById(R.id.use_audio_recorder_check);		
	    checkUseAudioRecorder.setOnClickListener(this);
	    Options.isUseAudioRecorder = Main.isUseAudioRecorder;
	    checkUseAudioRecorder.setChecked(isUseAudioRecorder);

        checkSampleRate = (CheckBox) findViewById(R.id.sample_rate_check);
        checkSampleRate.setOnClickListener(this);
        Options.isSampleRate = Main.isSampleRate;
        checkSampleRate.setChecked(isSampleRate);

		checkStereo = (CheckBox) findViewById(R.id.stereo_check);
		checkStereo.setOnClickListener(this);
		Options.isStereo = Main.isStereo;
		checkStereo.setChecked(isStereo);

		checkStartRecordScreen = (CheckBox) findViewById(R.id.start_record_screen_check);
	    checkStartRecordScreen.setOnClickListener(this);
	    Options.isStartRecordScreen = Main.isStartRecordScreen;
	    checkStartRecordScreen.setChecked(isStartRecordScreen);

	    checkStartRecording = (CheckBox) findViewById(R.id.start_recording_check);		
	    checkStartRecording.setOnClickListener(this);
	    Options.isStartRecording = Main.isStartRecording;
	    checkStartRecording.setChecked(isStartRecording);

        //checkBatchDownload = (CheckBox) findViewById(R.id.batch_download_check);
        //checkBatchDownload.setOnClickListener(this);
        //Options.isBatchDownload = Main.isBatchDownload;
        //checkBatchDownload.setChecked(isBatchDownload);

        checkLoadDefinition = (CheckBox) findViewById(R.id.load_definition_check);
        checkLoadDefinition.setOnClickListener(this);
        Options.isLoadDefinition = Main.isLoadDefinition;
        checkLoadDefinition.setChecked(isLoadDefinition);

	    checkDebug = (CheckBox) findViewById(R.id.debug_check);
	    checkDebug.setOnClickListener(this);
	    Options.isDebug = Main.isDebug;
	    checkDebug.setChecked(isDebug);

//		EditText critMult = (EditText) findViewById(R.id.crit_mult);
//		critMult.setText(String.valueOf(Main.criteriaMultiply)); // loaded in main at startup
//		Log.d(TAG, "onCreate critMult:" + critMult.getText().toString() + " as loaded from main.");
	}


	public void onClick(View v) {
    	switch (v.getId()) {
            case R.id.autoFilter_check: {
                Log.d(TAG, "onClick autoFilter_check");
                isOptionAutoFilter = checkAutoFilter.isChecked();
                Main.isOptionAutoFilter = isOptionAutoFilter;
				break;
            }
            case R.id.enhance_mic_check: {
                Log.d(TAG, "onClick enhance_mic_check");
                isEnhanceMic = checkEnhanceMic.isChecked();
                Main.isEnhanceQuality = isEnhanceMic;
                break;
            }
            case R.id.use_smoothing_check: {
                Log.d(TAG, "onClick use_smoothing_check");
                isUseSmoothing = checkUseSmoothing.isChecked();
                Main.isUseSmoothing = isUseSmoothing;
                break;
            }
            case R.id.show_definition_check: {
                Log.d(TAG, "onClick show_definition_check");
                isShowDefinition = checkShowDefinition.isChecked();
                Main.isShowDefinition = isShowDefinition;
				break;
            }
			case R.id.view_distance_check: {
				Log.d(TAG, "onClick view_distance_check");
				isViewDistance = checkViewDistance.isChecked();
				Main.isViewDistance = isViewDistance;
				break;
			}
			case R.id.view_energy_check: {
				Log.d(TAG, "onClick view_energy_check");
				isViewEnergy = checkViewEnergy.isChecked();
				Main.isViewEnergy = isViewEnergy;
				break;
			}
			case R.id.view_frequency_check: {
				Log.d(TAG, "onClick view_frequency_check");
				isViewFrequency = checkViewFrequency.isChecked();
				Main.isViewFrequency = isViewFrequency;
				break;
			}
			case R.id.view_quality_check: {
				Log.d(TAG, "onClick view_quality_check");
				isViewQuality = checkViewQuality.isChecked();
				Main.isViewQuality = isViewQuality;
				break;
			}
            case R.id.show_detail_check: {
                Log.d(TAG, "onClick show_detail_check");
                isShowDetail = checkShowDetail.isChecked();
                Main.isShowDetail = isShowDetail;
                break;
            }
            case R.id.show_web_check: {
                Log.d(TAG, "onClick web_show_check");
                isShowWeb = checkShowWeb.isChecked();
                Main.isShowWeb = isShowWeb;
                break;
            }
            case R.id.sort_by_name_check: {
                Log.d(TAG, "onClick sort_by_name_check");
                isSortByName = checkSortByName.isChecked();
                Main.isSortByName = isSortByName;
                break;
            }
            case R.id.use_location_check: {
                Log.d(TAG, "onClick use_location_check");
                isUseLocation = checkUseLocation.isChecked();
                Main.isUseLocation = isUseLocation;
                break;
            }
            case R.id.use_audio_recorder_check: {
                Log.d(TAG, "onClick use_audio_recorder_check");
                isUseAudioRecorder = checkUseAudioRecorder.isChecked();
                Main.isUseAudioRecorder = isUseAudioRecorder;
                break;
            }
            case R.id.sample_rate_check: {
                Log.d(TAG, "onClick sample_rate_check");
                isSampleRate = checkSampleRate.isChecked();
                Main.isSampleRate = isSampleRate;
                break;
            }
			case R.id.stereo_check: {
				Log.d(TAG, "onClick stereo_check");
				isStereo = checkStereo.isChecked();
				Main.isStereo = isStereo;
				break;
			}
            case R.id.start_record_screen_check: {
                Log.d(TAG, "onClick start_record_screen_check");
                isStartRecordScreen = checkStartRecordScreen.isChecked();
                Main.isStartRecordScreen = isStartRecordScreen;
                break;
    	    }
    	    case R.id.start_recording_check: {
		        Log.d(TAG, "onClick start_recording_check");
    		    isStartRecording = checkStartRecording.isChecked();
    		    Main.isStartRecording = isStartRecording;
    		    break;
    	    }
            //case R.id.batch_download_check: {
            //    Log.d(TAG, "onClick batch_download_check");
            //    isBatchDownload = checkBatchDownload.isChecked();
            //    Main.isBatchDownload = isBatchDownload; break;
            //}
            case R.id.load_definition_check: {
                Log.d(TAG, "onClick load_definition_check");
                isLoadDefinition = checkLoadDefinition.isChecked();
                Main.isLoadDefinition = isLoadDefinition; break;
            }
    	    case R.id.debug_check: {
		        Log.d(TAG, "onClick debug_check");
    		    isDebug = checkDebug.isChecked();
    		    Main.isDebug = isDebug;
    		    if (isDebug == false) {
               		File file = new File(Main.definepath + "id.txt");
               		boolean deleted = file.delete();
               		Log.d(TAG, "deleteFile id.txt:" + deleted);
           	    	file = new File(Main.definepath + "KernelInFreqDomain.txt");
           		    deleted = file.delete();
               		Log.d(TAG, "deleteFile KernelInFreqDomain.txt:" + deleted);
               		file = new File(Main.definepath + "SignalNoise.txt");
               		deleted = file.delete();
           	    	Log.d(TAG, "deleteFile SignalNoise.txt:" + deleted);
           		    file = new File(Main.definepath + "melFilter.txt");
               		deleted = file.delete();
               		Log.d(TAG, "deleteFile melFilter.txt:" + deleted);
               		file = new File(Main.definepath + "mfcc.txt");
           	    	deleted = file.delete();
           		    Log.d(TAG, "deleteFile mfcc.txt:" + deleted);
               		file = new File(Main.definepath + "voiced.txt");
               		deleted = file.delete();
               		Log.d(TAG, "deleteFile voiced.txt:" + deleted);
					file = new File(Main.definepath + "AudioLen.csv");
					deleted = file.delete();
					Log.d(TAG, "deleteFile AudioLen.csv:" + deleted);

    		    }
    		break;
    	    }
    	} //switch	
	} 	
	
	protected void onPause() {
    	super.onPause();
	    Log.d(TAG, "saveTheOptions");
//		EditText critMult = (EditText) findViewById(R.id.crit_mult);
//		String crit = critMult.getText().toString();
//		Log.d(TAG, "crit:" + crit);
//		Main.criteriaMultiply = Integer.parseInt(crit);
//		Log.d(TAG, "Main.criteriaMultiply:" + Main.criteriaMultiply);

		Main.db.beginTransaction();
		int temp = Main.isOptionAutoFilter ? 1 : 0;
		String qry = "UPDATE Options SET Value = " + temp + " WHERE Name =  'AutoFilter'";
		Main.db.execSQL(qry);
		temp = Main.isEnhanceQuality ? 1 : 0;
		qry = "UPDATE Options SET Value = " + temp + " WHERE Name =  'EnhanceQuality'";
		Main.db.execSQL(qry);
		temp = Main.isUseSmoothing ? 1 : 0;
		qry = "UPDATE Options SET Value = " + temp + " WHERE Name =  'UseSmoothing'";
		Main.db.execSQL(qry);
		temp = Main.isShowDefinition ? 1 : 0;
		qry = "UPDATE Options SET Value = " + temp + " WHERE Name =  'ShowDefinition'";
		Main.db.execSQL(qry);
		temp = Main.isViewDistance ? 1 : 0;
		qry = "UPDATE Options SET Value = " + temp + " WHERE Name =  'ViewDistance'";
		Main.db.execSQL(qry);
		temp = Main.isViewEnergy ? 1 : 0;
		qry = "UPDATE Options SET Value = " + temp + " WHERE Name =  'ViewEnergy'";
		Main.db.execSQL(qry);
		temp = Main.isViewFrequency ? 1 : 0;
		qry = "UPDATE Options SET Value = " + temp + " WHERE Name =  'ViewFrequency'";
		Main.db.execSQL(qry);
		temp = Main.isViewQuality ? 1 : 0;
		qry = "UPDATE Options SET Value = " + temp + " WHERE Name =  'ViewQuality'";
		Main.db.execSQL(qry);
		temp = Main.isShowDetail ? 1 : 0;
		qry = "UPDATE Options SET Value = " + temp + " WHERE Name =  'ShowDetail'";
		Main.db.execSQL(qry);
		temp = Main.isShowWeb ? 1 : 0;
		qry = "UPDATE Options SET Value = " + temp + " WHERE Name =  'ShowWeb'";
		Main.db.execSQL(qry);
		temp = Main.isSortByName ? 1 : 0;
		qry = "UPDATE Options SET Value = " + temp + " WHERE Name =  'SortByName'";
		Main.db.execSQL(qry);
		temp = Main.isUseLocation ? 1 : 0;
		qry = "UPDATE Options SET Value = " + temp + " WHERE Name =  'UseLocation'";
		Main.db.execSQL(qry);
		temp = Main.isUseAudioRecorder ? 1 : 0;
		qry = "UPDATE Options SET Value = " + temp + " WHERE Name =  'UseAudioRecorder'";
		Main.db.execSQL(qry);
		temp = Main.isSampleRate ? 1 : 0;
		qry = "UPDATE Options SET Value = " + temp + " WHERE Name =  'SampleRate'";
		Main.db.execSQL(qry);
		temp = Main.isStereo ? 1 : 0;
		qry = "UPDATE Options SET Value = " + temp + " WHERE Name =  'Stereo'";
		Main.db.execSQL(qry);
		temp = Main.isStartRecordScreen ? 1 : 0;
		qry = "UPDATE Options SET Value = " + temp + " WHERE Name =  'StartRecordScreen'";
		Main.db.execSQL(qry);
		temp = Main.isStartRecording ? 1 : 0;
		qry = "UPDATE Options SET Value = " + temp + " WHERE Name =  'StartRecording'";
		Main.db.execSQL(qry);
		//temp = Main.isBatchDownload ? 1 : 0;
		//qry = "UPDATE Options SET Value = " + temp + " WHERE Name =  'BatchDownload'";
		//Main.db.execSQL(qry);
		temp = Main.isLoadDefinition ? 1 : 0;
		qry = "UPDATE Options SET Value = " + temp + " WHERE Name =  'LoadDefinition'";
		Main.db.execSQL(qry);
		temp = Main.isDebug ? 1 : 0;
		qry = "UPDATE Options SET Value = " + temp + " WHERE Name =  'Debug'";
		Main.db.execSQL(qry);

//		temp = (int) Main.criteriaMultiply;
//		qry = "UPDATE Options SET Value = " + temp + " WHERE Name =  'CriteriaMultiply'";
//		Main.db.execSQL(qry);

		Main.db.setTransactionSuccessful();
		Main.db.endTransaction();

   }


}
