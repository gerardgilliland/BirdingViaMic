package com.modelsw.birdingviamic;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodecList;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import android.media.MediaCodecInfo.CodecProfileLevel;

@TargetApi(16)
public class DecodeFileJava {
	private static final String TAG = "DecodeFile";
	private static String mFileName = null;
	private static FileDescriptor mFileDescr = null; 
	Context ctx;
	boolean sawInputEOS;
	boolean sawOutputEOS;
	BufferInfo info;
	long TIMEOUT_US;
	AudioTrack mAudioTrack;
	ByteBuffer dstBuf;
    int sampleSize;
    int byteLoc = 0;
    int bitloc = 0; // bit location within bitbuf
    int bitbuf = 0; // buffer to pull bits from 
	int common_window;
    int audioObjectType;
    int window_sequence;
    int num_window_groups;
    int num_windows;
    int scale_flag;   
    int last_sf;
    int max_sfb;  // number of scalefactor bands transmitted per group
    int fs_index; // sampling frequency index
    int[] ltp_long_used;
    int[] num_sec;
    int[] window_group_length;
    public static float skipAdj = 0; // used in LineRenderer
    int[] swb_offset;
    int scale_factor_grouping;
    //int samplingFrequencyIndex[] = {96000, 88200, 64000, 48000, 44100, 32000, 24000, 22050, 16000, 12000, 11025, 8000, 7350, 0, 0, 0xf};
	int num_swb;
	int fileLoc;
    FileInputStream fis = null;
    // the following three flags are not read in unless extension flag exists and  
    // AudioObjectType is 17,19, 20, or 23  (i'm looking at type 2)
    int srate;
    int numBytesDecoded;
    int chancnt;
    
    public void DecodeFileJava () {
    	// empty constructor
    }
    
    public int getPcmData() {    	
        mFileName = Main.songpath + Main.existingName;
    	Log.d(TAG, "mFileName:" + mFileName);
	    TIMEOUT_US = -1;
    	try {
            fis = new FileInputStream(mFileName);
    		MediaExtractor extractor;
    		MediaCodec codec;
    		
    		ByteBuffer[] codecInputBuffers;
    		ByteBuffer[] codecOutputBuffers; 
    		extractor = new MediaExtractor(); 
    		extractor.setDataSource(mFileName);
    		Log.d(TAG, "new extractor working with file:" + mFileName);
    		int tc = extractor.getTrackCount(); // MediaExtractor.cpp    		
    		extractor.selectTrack(0); // <= You must select a track. You will read samples from the media from this track!
    		
    		int tracInx = extractor.getSampleTrackIndex();    		
    		Log.d(TAG, "trackCount:" + tc + " trackIndex:" + tracInx);
    		MediaFormat format = extractor.getTrackFormat(0);    		
    		String mime = format.getString(MediaFormat.KEY_MIME);
    		srate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE); // 22050 but I need 7 
    		chancnt = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT); //
    		Log.d(TAG, "AAC_PROFILE:" + audioObjectType + " channelCount:" + chancnt );
    		Log.d(TAG, "MIME TYPE:" + mime);  // "audio/mp4a-latm" -- Low-overhead MPEG-4 Audio Transport Multiplex (LATM)
    		codec = MediaCodec.createDecoderByType(mime);
            codec.configure(format, null /* surface */, null /* crypto */, 0 /* flags */);
    		
    		codec.start();
    		codecInputBuffers = codec.getInputBuffers();
    		codecOutputBuffers = codec.getOutputBuffers();
            numBytesDecoded = 0;
            int incr = 0;
            int sr = 4; // unknown sample rate
            if (srate == 22050) {
                incr = chancnt * (srate / 22050) * 2; // 1, 2, or 4 records ==> 2,4,8 bytes
                sr = 0;
            }
            if (srate == 44100) {
                incr = chancnt * (srate / 22050) * 2; // 1, 2, or 4 records ==> 2,4,8 bytes
                sr = 1;
            }
            if (srate == 24000) {
                incr = chancnt * (srate / 24000) * 2; // 1, 2, or 4 records ==> 2,4,8 bytes
                sr = 2;
                // 22050/256=86.13281 -- 24000/256=93.75 -- 24000/86.13281=278.6495 -- 278.6495-256=22.63946 -- 278.6456/256=1.088435 -- 1/(1.088435-1)=11.30769
                skipAdj = 0.088435f;  // when this exceeds 1 then skip a record -- appprox every 11 to 12 records
            }
            if (srate == 48000) {
                incr = chancnt * (srate / 24000) * 2; // 1, 2, or 4 records ==> 2,4,8 bytes
                sr = 3;
                // 22050/256=86.13281 -- 24000/256=93.75 -- 24000/86.13281=278.6495 -- 278.6495-256=22.63946 -- 278.6456/256=1.088435 -- 1/(1.088435-1)=11.30769
                skipAdj = 0.088435f;  // when this exceeds 1 then skip a record -- appprox every 11 to 12 records
            }
    		Log.d(TAG, "incr:" + incr);  // "audio/mp4a-latm" -- Low-overhead MPEG-4 Audio Transport Multiplex (LATM)
            if (incr == 0) {
                Log.d(TAG, " INVALID FREQUENCY:" + srate );
                //Main.sampleRate = srate;
                fis.close();
                return -1;
            }
            char q = 34;
            Main.db.beginTransaction();
            String qry = "UPDATE SongList SET SampleRate =" + sr  + " WHERE FileName=" + q + Main.existingName + q;
            Main.db.execSQL(qry);
            Main.db.setTransactionSuccessful();
            Main.db.endTransaction();

            final long kTimeOutUs = 5000;
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            int x = info.offset;  
            int sz = info.size;
            int rec = 0;  // records to be stored in
            // *************************** dimension audioData need to save rec (counter below) instead of Main.bytesDecoded
            Log.d(TAG, "audioDataSeek:" + Main.audioDataSeek + " audioDataLength:" + Main.audioDataLength);            
            Main.audioData = new short[Main.audioDataLength];  // do i need a little spare? I've added 1024
            long tm = info.presentationTimeUs;
            int flg = info.flags;
            Log.d(TAG, "bufferInfo:" + info.toString() + " offset:" + x);
            Log.d(TAG, "bufferInfo flags:" + flg);            
            boolean sawInputEOS = false;
            boolean sawOutputEOS = false;
            int skipBytes = 0;
   			if (Main.audioDataSeek > 0) {  // samples	
   				skipBytes = Main.audioDataSeek*2; 
   				fis.skip(skipBytes); // bytes
   				Log.d(TAG, "seek bytes:" + skipBytes);
   			}
            int endData = Main.audioDataLength; // - incr;
            while (!sawOutputEOS) {
                if (!sawInputEOS) {
                    int inputBufIndex = codec.dequeueInputBuffer(kTimeOutUs);
                    if (inputBufIndex >= 0) {
                        dstBuf = codecInputBuffers[inputBufIndex];
                        sampleSize = extractor.readSampleData(dstBuf, 0 /* offset */);  // MediaExtractor.java
                        int sf = extractor.getSampleFlags();
                        int stx = extractor.getSampleTrackIndex();                        
                		extractor.selectTrack(0); // I keep getting stereo ?? why
//                        Log.d(TAG, "sampleFlags:" + Integer.toHexString(sf) + " sampleTrackIndex:" + Integer.toHexString(stx));                        
//                        Log.d(TAG, "fileLoc:0x" + Integer.toHexString(fileLoc) + " sampleSize:0x" + Integer.toHexString(sampleSize) );
                        fileLoc += sampleSize; 
                        long presentationTimeUs = 0;
                        if (sampleSize < 0) {
                            Log.d(TAG, "saw input EOS.");
                            sawInputEOS = true;
                            sampleSize = 0;
                        } else {
                            presentationTimeUs = extractor.getSampleTime();
                        }
                        // Log.d(TAG, "input inx" + inputBufIndex + ", sampleSize" + sampleSize);
                        codec.queueInputBuffer(
                                inputBufIndex,
                                0 /* offset */,
                                sampleSize,
                                presentationTimeUs,
                                sawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);

                        if (!sawInputEOS) {
                            extractor.advance();
                        }
                    }
                }
                
                int res = codec.dequeueOutputBuffer(info, kTimeOutUs);
                if (res >= 0) {
                    int outputBufIndex = res;
                    ByteBuffer buf = codecOutputBuffers[outputBufIndex];
                    short max = 0;
                    int maxI = 0;
                    // see incr above
            		// if 1 channel and 22050 then read every short 
            		// if 2 channels and 22050 or 1 channel and 44100 then read every other short
            		// if 2 channels and 44100 then read every 4th short
                    // *** was average instead of skipping *** WRONG loses high freq -- and increases noise
                    // back to skipping by incr each record plus by incr each skipAdj
                    float skipCnt = 0f;
                    for (int i = 0; i < info.size; i+=incr) {
                   		int lo = buf.get(i);
                   		int hi = buf.get(i+1);
                   		int hilo = ((hi << 8) & 0xff00) | (lo & 0xff);
                    	Main.audioData[rec] = (short) hilo;
                        skipCnt += skipAdj; // record or short not byte
                        if (skipCnt >= 1f) {
                            i+=incr;
                            //numBytesDecoded += 2; -- NOT HERE because this does not add length
                            skipCnt -= 1f;
                        }
                    	if (numBytesDecoded > skipBytes) {  // poor man's version of finding the right place to start in the file 
                    		rec++;
                    	}
                    	numBytesDecoded += 2;
               			if (rec >= endData) {
            				sawOutputEOS = true;
                            Log.d(TAG, "saw output EOS with endData:" + endData + " info.size:" + info.size);
                            break;
               			} 

                    }  
                    //numBytesDecoded += (info.size/incr);
                    //Log.d(TAG, "numBytesDecoded:0x" +  Integer.toHexString(numBytesDecoded) + " info.size:0x" + Integer.toHexString(info.size/incr));
                    codec.releaseOutputBuffer(outputBufIndex, false /* render */);
                    if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        Log.d(TAG, "saw output EOS with Flag EndOfStream");
                        sawOutputEOS = true;
                    }
                } else if (res == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    codecOutputBuffers = codec.getOutputBuffers();

                    Log.d(TAG, "output buffers have changed.");
                } else if (res == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    MediaFormat oformat = codec.getOutputFormat();
                    Log.d(TAG, "output format has changed to " + oformat);
                }
            }
            Log.d(TAG, "audioData length:" + rec);
            Main.audioDataLength = rec; // actual
            Main.audioDataLength -= Main.audioDataLength % PlaySong.base;
            Log.d(TAG, "decodeFile() audioDataLength (actual):" + Main.audioDataLength);
            codec.stop();
            codec.release();
            fis.close();
        } catch (IOException e) {
            Log.e(TAG, "openFd failed on file:" + mFileName);
            return -1;
        }	
    	return 0;
    }

} // decode file
