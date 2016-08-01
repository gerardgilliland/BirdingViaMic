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
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.lang.System;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
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
    int dbSeek;
    int dbAudLen;
    int dbCntr;
    int dbDiff;
    private static FileOutputStream fos7 = null;
    int maxEnergy = 0;
    private static String mFileName = null;
    boolean sawInputEOS;
    boolean sawOutputEOS;
    long TIMEOUT_US;
    ByteBuffer dstBuf;
    int sampleSize;
    int audioObjectType;
    public static float skipAdj = 0; // used to get 48000 and 24000 to 44100 or 22050
    int fileLoc;
    FileInputStream fis = null;
    // the following three flags are not read in unless extension flag exists and
    // AudioObjectType is 17, 19, 20, or 23  (i'm looking at type 2)
    int srate;
    int chancnt;

    public void DecodeFileJava() {
        // empty constructor
    }

    public int getPcmData() {
        mFileName = Main.songpath + Main.existingName;
        String dbAud = Main.definepath + "dbAudLen.csv";
        Log.d(TAG, "Start Decode mFileName:" + mFileName);
        TIMEOUT_US = -1;
        System.gc();
        try {
            //if (Main.isDebug) {
            //    writeDataLen(0);
            //}
            fis = new FileInputStream(mFileName);
            MediaExtractor extractor;
            MediaCodec codec;
            ByteBuffer[] codecInputBuffers;
            ByteBuffer[] codecOutputBuffers;
            extractor = new MediaExtractor();
            extractor.setDataSource(mFileName);
            Log.d(TAG, "new extractor working with file:" + mFileName);
            int tc = extractor.getTrackCount(); // MediaExtractor.cpp -- always 1 so this is wrong
            extractor.selectTrack(0); // <= You must select a track. You will read samples from the media from this track!

            int tracInx = extractor.getSampleTrackIndex();
            Log.d(TAG, "trackCount:" + tc + " trackIndex:" + tracInx);
            MediaFormat format = extractor.getTrackFormat(0);
            String mime = format.getString(MediaFormat.KEY_MIME);
            srate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE); // 22050 but I need 7
            chancnt = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT); // 1=mono 2=stereo
            Main.stereoFlag = 0;
            if (chancnt == 2) {
                Main.stereoFlag = 1;
            }
            Log.d(TAG, "AAC_PROFILE:" + audioObjectType + " channelCount:" + chancnt);
            Log.d(TAG, "MIME TYPE:" + mime);  // "audio/mp4a-latm" -- Low-overhead MPEG-4 Audio Transport Multiplex (LATM)
            codec = MediaCodec.createDecoderByType(mime);
            codec.configure(format, null /* surface */, null /* crypto */, 0 /* flags */);

            codec.start();
            codecInputBuffers = codec.getInputBuffers();
            codecOutputBuffers = codec.getOutputBuffers();
            int numBytesDecoded = 0;
            int incr = 0;
            int sr = 4; // unknown sample rate
            if (srate == 22050) {
                incr = chancnt * (srate / 22050) * 2; // 1, 2, or 4 records ==> 2,4,8 bytes
                sr = 0;
                skipAdj = 0;
            }
            if (srate == 44100) {
                incr = chancnt * (srate / 22050) * 2; // 1, 2, or 4 records ==> 2,4,8 bytes
                sr = 1;
                skipAdj = 0;
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
                Log.d(TAG, " INVALID FREQUENCY:" + srate);
                //Main.sampleRate = srate;
                fis.close();
                return -1;
            }
            char q = 34;
            // get existing

            int sourceMic = 0;
            String qry = "SELECT SourceMic from SongList WHERE FileName =" + q + Main.existingName + q;
            Cursor rs = Main.songdata.getReadableDatabase().rawQuery(qry, null);
            int cntr = rs.getCount();
            if (cntr > 0) {
                rs.moveToFirst();
                sourceMic = rs.getInt(0);  // 0=pre-record 1=internal 2=external
            }
            Main.db.beginTransaction();
            qry = "UPDATE SongList SET SampleRate =" + sr +
                    ", SourceMic =" + sourceMic +
                    ", Stereo =" + Main.stereoFlag +
                    " WHERE FileName=" + q + Main.existingName + q;
            Main.db.execSQL(qry);
            Main.db.setTransactionSuccessful();
            Main.db.endTransaction();

            final long kTimeOutUs = 50;  // large timeout (5000000) causes mp3 to fail to seek
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            long x = info.offset;
            long sz = info.size;
            Main.shortCntr = 0;  // records to be stored in audioData[]
            // *************************** dimension audioData need to save shortCntr (counter below) instead of Main.bytesDecoded
            Log.d(TAG, "Seek:" + Main.audioDataSeek + " audioDataLength:" + Main.audioDataLength);
            dbSeek = Main.audioDataSeek;
            dbAudLen = Main.audioDataLength;
            Main.audioData = new short[Main.audioDataLength];
            long tm = info.presentationTimeUs;
            int flg = info.flags;
            Log.d(TAG, "bufferInfo:" + info.toString() + " offset:" + x);
            Log.d(TAG, "bufferInfo flags:" + flg);
            sawInputEOS = false;
            sawOutputEOS = false;
            int skipBytes = 0;
            if (Main.audioDataSeek > 0) {  // samples
                skipBytes = Main.audioDataSeek * 2;
                fis.skip(skipBytes); // bytes
                Log.d(TAG, "seek bytes:" + skipBytes);
                long usec = Main.songStartAtLoc * 1000; // msec to usec
                extractor.seekTo(usec, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
            }
            int endData = Main.audioDataLength; // - incr;
            while (!sawOutputEOS) {
                try {
                    if (!sawInputEOS) {
                        int inputBufIndex = codec.dequeueInputBuffer(kTimeOutUs);
                        if (inputBufIndex >= 0) {
                            dstBuf = codecInputBuffers[inputBufIndex];
                            // note: offset below is offset into the buffer not seek from the file.
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
                                    0 /* offset is shift into input Buffer */,
                                    sampleSize,
                                    presentationTimeUs,
                                    sawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);

                            if (!sawInputEOS) {
                                extractor.advance();
                            }
                        }
                    }
                } catch (Exception e) {
                    String msg = "Codec exception:" + e;
                    Log.d(TAG, msg);
                    return -3;  // interrupted
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
                    for (int i = 0; i < info.size; i += incr) {
                        int lo = buf.get(i);
                        int hi = buf.get(i + 1);
                        int hilo = ((hi << 8) & 0xff00) | (lo & 0xff);
                        Main.audioData[Main.shortCntr] = (short) hilo;
                        skipCnt += skipAdj; // record or short not byte
                        if (skipCnt >= 1f) {
                            i += incr;
                            skipCnt -= 1f;
                        }
                        Main.shortCntr++;
                        //}
                        if (Main.shortCntr >= endData) {
                            sawOutputEOS = true;
                            Log.d(TAG, "saw output EOS with endData:" + endData + " info.size:" + info.size);
                            break;
                        }
                    }
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
                    Log.d(TAG, "HERE -- output format has changed to:" + oformat);
                }
            }
            //int mod = Main.audioDataLength % PlaySong.base;
            //Log.d(TAG, "start audioDataLength:" + Main.audioDataLength + " base:" + PlaySong.base + " modulo:" + mod);
            Main.audioDataLength = Main.shortCntr; // actual
            //Log.d(TAG, "now audioDataLength:" + Main.audioDataLength);
            dbCntr = Main.shortCntr;
            dbDiff = dbAudLen - dbCntr;
            codec.stop();
            codec.release();
            fis.close();
            if (Main.isDebug) {
                writeDataLen();
            }
            if (dbDiff > PlaySong.base) {
                return -2; // decoder failed on Main.existingName.
            }
        } catch (IOException e) {
            Log.e(TAG, "openFd failed on file:" + mFileName);
            return -1;
        }
        // what to do if they stop playing before I finish decoding?
        //if(Main.isPlaying == false) { //
        //    return 0;  // if interrupted it will return 0 as set above
        //} else {
        Log.d(TAG, "finished decodeFile() audioDataLength:" + Main.audioDataLength );
        //Main.maxEnergy = maxEnergy; // already sent in realtime.
        return 1; // I have got to here so I finished decoding so don't repeat the decode.
        //}
    }

    void writeDataLen() {
        try {
            String sb = null;
            Log.d(TAG, "write AudioLen.csv");
            fos7 = new FileOutputStream(Main.definepath + "AudioLen.csv", true);  // append
            sb = Main.existingName + " inx:" + Main.existingInx + " seg:" + Main.existingSeg
                    + ", isId:" + Main.isIdentify
                    + ", isLoad:" + Main.isLoadDefinition
                    + ", Seek:" + dbSeek
                    + ", AudLen:" + dbAudLen
                    + ", Cntr:" + dbCntr
                    + ", Diff:" + dbDiff + "\n";
            byte buf[] = sb.getBytes();
            fos7.write(buf);
            fos7.close();
        } catch (IOException e) {
            Log.e(TAG, "IOException:" + e);
        }
    } // writeDataLen
}