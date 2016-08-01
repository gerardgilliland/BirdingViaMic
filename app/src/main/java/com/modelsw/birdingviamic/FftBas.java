package com.modelsw.birdingviamic;

import android.util.Log;

public class FftBas {
	/*	'gg -- Fast Fourier Transform ==> conversion from Time Domain to Frequency Domain.
	 *  converted to java and float instead of double

	'Attribute VB_Name = "VBFFT"
	'--------------------------------------------------------------------
	' VB FFT Release 2-B
	' by Murphy McCauley (MurphyMc@Concentric.NET)
	' 10/01/99
	'--------------------------------------------------------------------
	' About:
	' This code is very, very heavily based on Don Cross's fourier.pas
	' Turbo Pascal Unit for calculating the Fast Fourier Transform.
	' I've not implemented all of his functions, though I may well do
	' so in the future.
	' For more info, you can contact me by email, check my website at:
	' http://www.fullspectrum.com/deeth/
	' or check Don Cross's FFT web page at:
	' http://www.intersrv.com/~dcross/fft.html
	' You also may be intrested in the FFT.DLL that I put together based
	' on Don Cross's FFT C code.  It's callable with Visual Basic and
	' includes VB declares.  You can get it from either website.
	'--------------------------------------------------------------------
	' History of Release 2-B:
	' Fixed a couple of errors that resulted from me mucking about with
	'   variable names after implementation and not re-checking.  BAD ME.
	'  --------
	' History of Release 2:
	' Added FrequencyOfIndex() which is Don Cross's Index_to_frequency().
	' FourierTransform() can now do inverse transforms.
	' Added CalcFrequency() which can do a transform for a single
	'   frequency.
	'--------------------------------------------------------------------
	' Usage:
	' The useful functions are:
	' FourierTransform() performs a Fast Fourier Transform on an pair of
	'  Double arrays -- one real, one imaginary.  Don't want/need
	'  imaginary numbers?  Just use an array of 0s.  This function can
	'  also do inverse FFTs.
	' FrequencyOfIndex() can tell you what actual frequency a given index
	'  corresponds to.
	' CalcFrequency() transforms a single frequency.
	'--------------------------------------------------------------------
	' Notes:
	' All arrays must be 0 based (i.e. Dim TheArray(0 To 1023) or
	'  Dim TheArray(1023)).
	' The number of samples must be a power of two (i.e. 2^x).
	' FrequencyOfIndex() and CalcFrequency() haven't been tested much.
	' Use this ENTIRELY AT YOUR OWN RISK.
	'--------------------------------------------------------------------
*/
	FftBas() {
		// empty constructor
	}
	
	String TAG = "FftBas"; 
	float pi = 3.14159265358979f;
	

	private int numberOfBitsNeeded(int powerOfTwo) {
		int i;
	    for (i = 0; i<= 16; i++) {
	    	int x = (int) Math.pow(2, i) & powerOfTwo;
	        if (x != 0) {
	            break;
	        } // End If
	    } // Next
	    return i;
	}

	private boolean isPowerOfTwo(int x) {
		boolean ipot = false;
		if (x < 2) {
			ipot = false;
		}
		if ((x & (x - 1)) == 0) {
			ipot = true;
		}
		return ipot;
	}


	private int reverseBits(int index, int numBits) {
		int i=0;
		int rev=0;
		for (i=0; i<numBits; i++) {
			rev = (rev << 1) | (index & 1);
			index = index >> 1;
		} // Next
		return rev;
	}


	public void fourierTransform(int numSamples, float[] realIn, float[] imagIn, 
			float[] realOut, float[] imagOut, boolean inverseTransform) {
		float angleNumerator;
		int numBits;
		int i;
		int j;
		int k;
		int n;
		int blockSize;
		int blockEnd;    
		float deltaAngle;
		float deltaAr;
		float alpha;
		float beta;
		float tR;
		float tI;
		float aR;
		float aI;
    
		if (inverseTransform == true ) {
			angleNumerator = -2 * pi;
		} else {
			angleNumerator = 2 * pi;
		}

		if ((isPowerOfTwo(numSamples) == false) || (numSamples < 2) == true) { 
			Log.d(TAG, "Error in procedure fourierTransform nNumSamples:" + numSamples + " is not a positive integer power of two.");
			return;
		} 
   
		numBits = numberOfBitsNeeded(numSamples);
		for (i = 0; i<numSamples; i++) {
			j = reverseBits(i, numBits);
			realOut[j] = realIn[i];
			imagOut[j] = imagIn[i];
		} 
    
		blockEnd = 1;
		blockSize = 2;
    
		while (blockSize <= numSamples) {
			deltaAngle = angleNumerator / blockSize;
			alpha = (float) Math.sin(0.5 * deltaAngle);
			alpha = 2 * alpha * alpha;
			beta = (float) Math.sin(deltaAngle);
			i = 0;
			while (i < numSamples) {
				aR = 1;
				aI = 0;
            
				j = i;
				for (n = 0; n<blockEnd; n++) {
					k = j + blockEnd;
					tR = aR * realOut[k] - aI * imagOut[k];
					tI = aI * realOut[k] + aR * imagOut[k];
					realOut[k] = realOut[j] - tR;
					imagOut[k] = imagOut[j] - tI;
					realOut[j] = realOut[j] + tR;
					imagOut[j] = imagOut[j] + tI;
					deltaAr = alpha * aR + beta * aI;
					aI = aI - (alpha * aI - beta * aR);
					aR = aR - deltaAr;
					j += 1;
				} // Next n
            
				i += blockSize;
			} // while
        
			blockEnd = blockSize;
			blockSize = blockSize * 2;
		} // while

		if (inverseTransform == true) {
			// Normalize the resulting time samples...
			for (i=0; i<numSamples; i++) {
				realOut[i] = realOut[i] / numSamples;
				imagOut[i] = imagOut[i] / numSamples;
			} 
		}
		
	} // fourierTransform
		
	private float frequencyOfIndex(int numberOfSamples, int index) {
		// Based on IndexToFrequency().  This name makes more sense to me.
		float foi = 0f;
		if (index >= numberOfSamples) {
			foi = 0;
			return foi;
		} else if (index <= (numberOfSamples / 2)) {
			foi = (float) index / (float) numberOfSamples;
			return foi;
		} else {
			foi = - (float) (numberOfSamples - index) / (float) numberOfSamples;
        	return foi;
		}
	}
	
	public void inverseDFT (int numSamples, float[] realIn, float[] imagIn, float[] realOut) { 
//		The Scientist and Engineer's Guide to Digital Signal Processing -- table 8.1		
//		realOut[numSamples-1]; // realOut[ ] returns the time domain signal
//		realIn[numSamples/2];  // realIn[ ] holds the real part of the frequency domain
//		imagIn[numSamples/2]; //ImagIn[ ] holds the imaginary part of the frequency domain
//		pi = 3.14159265 'Set the constant, PI
//		numSamples is the number of points in realOut[ ] -- the base
// 		find the cosine and sine wave amplitudes and invert the sine wave using Eq. 8-3
		// this is NOT normalizing (divide by max) but rather divide by stepSize
		int stepSize = numSamples/2;
		for (int k = 0; k < stepSize; k++) {
			realIn[k] = realIn[k] / stepSize; 
			imagIn[k] = -imagIn[k] / stepSize;
		}
		realIn[0] = realIn[0] / 2;  // special case
		realIn[stepSize-1] = realIn[stepSize-1] / 2; // special case
		for (int i = 0; i < numSamples; i++) { // i loops through each sample in realOut[ ]
			realOut[i] = 0;
			for (int k = 0; k < stepSize; k++) { // k loops through each sample in realIn[ ] and ImagIn[ ]
				float temp = 2*pi*k*i/numSamples;
				realOut[i] += (realIn[k] * Math.cos(temp)); 
				realOut[i] += (imagIn[k] * Math.sin(temp));
			}
		}
		
	}

	public void forwardDFT(int numSamples, float[] realIn, float[] imagIn,	float[] realOut, float[] imagOut) {
//		The Scientist and Engineer's Guide to Digital Signal Processing -- table 8.2		
		// THE DISCRETE FOURIER TRANSFORM
		// The frequency domain signals, held in realOut[ ] and imagOut[ ], are calculated from
		// the time domain signal, held in realIn[ ].
		// realIn[511] realIn[ ] holds the time domain signal
		// realOut[256] 'realOut[ ] holds the real part of the frequency domain
		// imagOut[256] 'imagOut[ ] holds the imaginary part of the frequency domain
		// pi = 3.14159265 'Set the constant, PI
		// numSamples is the number of points in realIn[ ] 
		int stepSize = numSamples/2;
		for(int k = 0; k< stepSize; k++) { // Zero realOut[ ] & imagOut[ ] so they can be used as accumulators
			realOut[k] = 0;
			imagOut[k] = 0;
		} // next k

		// Correlate realIn[ ] with the cosine and sine waves, Eq. 8-4
		for (int i = 0; i< numSamples; i++) { // i loops through each sample in realIn[ ]
			for (int k = 0; k < stepSize; k++) {  // k loops through each sample in realOut[ ] and imagOut[ ]
				realOut[k] += (realIn[i] * Math.cos(2*pi*k*i/numSamples));
				imagOut[k] -= (realIn[i] * Math.sin(2*pi*k*i/numSamples));
			} // next i
		} // next k
	} // forwardDft

	
	public void customFilter(int numSamples, int filterLen, float[] filterKernel) {
		// CUSTOM FILTER DESIGN
		// This program converts an aliased numSamples point impulse response signal into an M+1 point
		// filter kernel (such as Fig. 17-1b being converted into Fig. 17-1c)
		// signal comes from the time domain having just done an inverseFFT 
	
		// signal[1023] 'REX[ ] holds the signal being converted
		float[] temp = new float[numSamples]; //  'temp[ ] is a temporary storage buffer
		//pi = 3.14159265;
		//int filterLen = 40;  // Set filter kernel length (41 total points)
	
		// GOSUB XXXX 'Mythical subroutine to load filterKernel[ ] with impulse response
		// filterKernel is the impulse response 

		for (int i = 0; i <numSamples; i++) { // Shift (rotate) the signal HALF the filterLen to the right
			int inx = i + filterLen/2;
			if (inx >= numSamples) { // rotate the signal into the left (if signal + i > numSamples)  
				inx = inx-numSamples;
			}
			temp[inx] = filterKernel[i]; 
		} // next i
		for (int i = 0; i <numSamples; i++) { // put the shifted and rotated signal back in signal
			filterKernel[i] = temp[i];
		} // next i

		// Truncate and window the signal 
		for (int i = 0; i <numSamples; i++) {
			if (i <= filterLen) { // window the filter
				filterKernel[i] *= (0.54 - 0.46 * Math.cos(2*pi*i/filterLen));
			} else {
				filterKernel[i] = 0; // pad with zeros
			}
		} // NEXT I%
		// The filter kernel now resides in filterKernel[0] to filterKernel[40] 
		// 40 is not magic -- the longer the filterLen the better (and slower) the filter
	} // custom filter
	
} // fftBas

