package com.modelsw.birdingviamic;

import android.util.Log;


public class FFTjava {

	/**********************************************************************

	  FFT.cpp

	  Dominic Mazzoni

	  September 2000

	*******************************************************************//*!

	\file FFT.cpp
	\brief Fast Fourier Transform routines.

	  This file contains a few FFT routines, including a real-FFT
	  routine that is almost twice as fast as a normal complex FFT,
	  and a power spectrum routine when you know you don't care
	  about phase information.

	  Some of this code was based on a free implementation of an FFT
	  by Don Cross, available on the web at:

	    http://www.intersrv.com/~dcross/fft.html

	  The basic algorithm for his code was based on Numerican Recipes
	  in Fortran.  I optimized his code further by reducing array
	  accesses, caching the bit reversal table, and eliminating
	  float-to-double conversions, and I added the routines to
	  calculate a real FFT and a real power spectrum.

	*//*******************************************************************/
	/*
	  Salvo Ventura - November 2006
	  Added more window functions:
	    * 4: Blackman
	    * 5: Blackman-Harris
	    * 6: Welch
	    * 7: Gaussian(a=2.5)
	    * 8: Gaussian(a=3.5)
	    * 9: Gaussian(a=4.5)
	*/
	private static final String TAG = "FFT";
	private static int maxFastBits = 16;
	private int[][] gFFTBitTable;
	private double m_PI = 3.14159265358979323846;  /* pi */

	private boolean isPowerOfTwo(int x) {
	   if (x < 2) {
		   return false;
	   }
	   if ((x & (x - 1)) == 1) return false;           /* Thanks to 'byang' for this cute trick! */
	   return true;
	}

	private int numberOfBitsNeeded(int powerOfTwo) {
	   int i;
	   if (powerOfTwo < 2) {
		   Log.d(TAG, "Error: FFT called with size powerOfTwo:" + powerOfTwo);
	       return 1;
	   }
	   for (i = 0; i < 16; i++) { 
		  int temp = powerOfTwo & (1 << i);
	      if (temp > 0) {
	    	  // Log.d(TAG, "numberOfBitsNeeded:" + i);
	    	  break;
	      }
	   }
	   return i;
	}

	private int reverseBits(int index, int numBits)	{ 
	   int i, rev;
	   for (i = rev = 0; i < numBits; i++) {
	      rev = (rev << 1) | (index & 1);
	      index >>= 1;
	   }
	   return rev;
	}

	public void initFFT() {
	   int len = 2;
	   gFFTBitTable = new int [maxFastBits][len];
	   for (int b = 1; b <= maxFastBits; b++) {
	      gFFTBitTable[b - 1] = new int[len];
	      for (int i = 0; i < len; i++)
	         gFFTBitTable[b - 1][i] = reverseBits(i, b);
	      len <<= 1; 
	   }
	}

	void deinitFFT() {
	   if (gFFTBitTable != null) {
		  gFFTBitTable = null; 
	   }
	}

	private int fastReverseBits(int i, int numBits)
	{
	   if (numBits <= maxFastBits)
	      return gFFTBitTable[numBits - 1][i];
	   else
	      return reverseBits(i, numBits);
	}

	/*
	 * Complex Fast Fourier Transform
	 */

	public void cpxFFT(int numSamples, boolean inverseTransform,
	         float[] realIn, float[] imagIn, float[] realOut, float[] imagOut) {
	   int numBits;                 /* Number of bits needed to store indices */
	   int i, j, k, n;
	   int blockSize, blockEnd;
	   double angle_numerator = 2.0 * m_PI;
	   double tr, ti;                /* temp real, temp imaginary */

	   if (!isPowerOfTwo(numSamples)) {
		  Log.d(TAG, "numSamples is not a power of two:" + numSamples);
	      return;
	   }

	   if (gFFTBitTable == null)
	      initFFT();

	   if (inverseTransform == true) {
	      angle_numerator = -angle_numerator;
	   }
	   numBits = numberOfBitsNeeded(numSamples);

	   /*
	    **   Do simultaneous data copy and bit-reversal ordering into outputs...
	    */

	   for (i = 0; i < numSamples; i++) {
	      j = fastReverseBits(i, numBits);
	      realOut[j] = realIn[i];
	      if (imagIn == null) { 
	    	  imagOut[j] = 0.0f; 
	      } else {  
	    	  imagOut[j] = imagIn[i];
	      }
	   }

	   /*
	    **   Do the FFT itself...
	    */

	   blockEnd = 1;
	   for (blockSize = 2; blockSize <= numSamples; blockSize <<= 1) {

	      double delta_angle = angle_numerator / (double) blockSize;

	      double sm2 = Math.sin(-2 * delta_angle);
	      double sm1 = Math.sin(-delta_angle);
	      double cm2 = Math.cos(-2 * delta_angle);
	      double cm1 = Math.cos(-delta_angle);
	      double w = 2 * cm1;
	      double ar0, ar1, ar2, ai0, ai1, ai2;

	      for (i = 0; i < numSamples; i += blockSize) {
	         ar2 = cm2;
	         ar1 = cm1;

	         ai2 = sm2;
	         ai1 = sm1;

	         for (j = i, n = 0; n < blockEnd; j++, n++) {
	            ar0 = w * ar1 - ar2;
	            ar2 = ar1;
	            ar1 = ar0;

	            ai0 = w * ai1 - ai2;
	            ai2 = ai1;
	            ai1 = ai0;

	            k = j + blockEnd;
	            tr = ar0 * realOut[k] - ai0 * imagOut[k];
	            ti = ar0 * imagOut[k] + ai0 * realOut[k];

	            realOut[k] = (float) (realOut[j] - tr);
	            imagOut[k] = (float) (imagOut[j] - ti);

	            realOut[j] += tr;
	            imagOut[j] += ti;
	         }
	      }

	      blockEnd = blockSize;
	   }

	   /*
	      **   Need to normalize if inverse transform...
	    */

	   if (inverseTransform == true) {
	      float denom = (float) numSamples;
	      for (i = 0; i < numSamples; i++) {
	         realOut[i] /= denom;
	         imagOut[i] /= denom;
	      }
	   }
	}

	/*
	 * Real Fast Fourier Transform
	 *
	 * This function was based on the code in Numerical Recipes in C.
	 * In Num. Rec., the inner loop is based on a single 1-based array
	 * of interleaved real and imaginary numbers.  Because we have two
	 * separate zero-based arrays, our indices are quite different.
	 * Here is the correspondence between Num. Rec. indices and our indices:
	 *
	 * i1  <->  real[i]
	 * i2  <->  imag[i]
	 * i3  <->  real[n/2-i]
	 * i4  <->  imag[n/2-i]
	 */

	public void realFFT(int numSamples, float[] realIn, float[] realOut, float[] imagOut)
	{
	   int half = numSamples / 2;
	   int i;

	   float theta = (float) (m_PI / half);

	   float[] tmpReal = new float[half];
	   float[] tmpImag = new float[half];

	   for (i = 0; i < half; i++) {
	      tmpReal[i] = realIn[2 * i];
	      tmpImag[i] = realIn[2 * i + 1];
	   }

	   cpxFFT(half, false, tmpReal, tmpImag, realOut, imagOut);

	   float wtemp = (float) (Math.sin(0.5 * theta));

	   float wpr = (float) (-2.0 * wtemp * wtemp);
	   float wpi = (float) (-1.0 * Math.sin(theta));
	   float wr = (float) (1.0 + wpr);
	   float wi = wpi;

	   int i3;

	   float h1r, h1i, h2r, h2i;

	   for (i = 1; i < half / 2; i++) {

	      i3 = half - i;

	      h1r = (float) (0.5 * (realOut[i] + realOut[i3]));
	      h1i = (float) (0.5 * (imagOut[i] - imagOut[i3]));
	      h2r = (float) (0.5 * (imagOut[i] + imagOut[i3]));
	      h2i = (float) (-0.5 * (realOut[i] - realOut[i3]));

	      realOut[i] = h1r + wr * h2r - wi * h2i;
	      imagOut[i] = h1i + wr * h2i + wi * h2r;
	      realOut[i3] = h1r - wr * h2r + wi * h2i;
	      imagOut[i3] = -h1i + wr * h2i + wi * h2r;

	      wr = (wtemp = wr) * wpr - wi * wpi + wr;
	      wi = wi * wpr + wtemp * wpi + wi;
	   }

	   realOut[0] = (h1r = realOut[0]) + imagOut[0];
	   imagOut[0] = h1r - imagOut[0];

	   tmpReal = null;
	   tmpImag = null;
	}

	/*
	 * PowerSpectrum
	 *
	 * This function computes the same as RealFFT, above, but
	 * adds the squares of the real and imaginary part of each
	 * coefficient, extracting the power and throwing away the
	 * phase.
	 *
	 * For speed, it does not call RealFFT, but duplicates some
	 * of its code.
	 */

	public void powerSpectrum(int numSamples, float[] in, float[] out) {

	   int half = numSamples / 2;
	   int i;

	   float theta = (float) (m_PI / half);

	   float[] tmpReal = new float[half];
	   float[] tmpImag = new float[half];
	   float[] realOut = new float[half];
	   float[] imagOut = new float[half];

	   for (i = 0; i < half; i++) {
	      tmpReal[i] = in[2 * i];
	      tmpImag[i] = in[2 * i + 1];
	   }

	   cpxFFT(half, false, tmpReal, tmpImag, realOut, imagOut);

	   float wtemp = (float) (Math.sin(0.5 * theta));

	   float wpr = (float) (-2.0 * wtemp * wtemp);
	   float wpi = (float) (-1.0 * (Math.sin(theta)));
	   float wr = (float) (1.0 + wpr);
	   float wi = wpi;

	   int i3;

	   float h1r, h1i, h2r, h2i, rt, it;

	   for (i = 1; i < half / 2; i++) {

	      i3 = half - i;

	      h1r = (float) (0.5 * (realOut[i] + realOut[i3]));
	      h1i = (float) (0.5 * (imagOut[i] - imagOut[i3]));
	      h2r = (float) (0.5 * (imagOut[i] + imagOut[i3]));
	      h2i = (float) (-0.5 * (realOut[i] - realOut[i3]));

	      rt = h1r + wr * h2r - wi * h2i;
	      it = h1i + wr * h2i + wi * h2r;

	      out[i] = rt * rt + it * it;

	      rt = h1r - wr * h2r + wi * h2i;
	      it = -h1i + wr * h2i + wi * h2r;

	      out[i3] = rt * rt + it * it;

	      wr = (wtemp = wr) * wpr - wi * wpi + wr;
	      wi = wi * wpr + wtemp * wpi + wi;
	   }

	   rt = (h1r = realOut[0]) + imagOut[0];
	   it = h1r - imagOut[0];
	   out[0] = rt * rt + it * it;

	   rt = realOut[half / 2];
	   it = imagOut[half / 2];
	   out[half / 2] = rt * rt + it * it;

	   tmpReal = null;
	   tmpImag = null;
	   realOut = null;
	   imagOut = null;
	}

	/*
	 * Windowing Functions
	 */

	public int numWindowFuncs() {
	   return 10;
	}


	public void windowFunc(int whichFunction, int numSamples, float[] in) {
	   int i;
	   double a;

	   switch( whichFunction )  {	   
	   case 0: // do nothing
		   break;
	   case 1:  // Bartlett (triangular) window
	      for (i = 0; i < numSamples / 2; i++) {
	         in[i] *= (i / (float) (numSamples / 2));
	         in[i + (numSamples / 2)] *= (1.0 - (i / (float) (numSamples / 2)));
	      }
	      break;
	   case 2:  // Hamming
	      for (i = 0; i < numSamples; i++)
	         in[i] *= 0.54 - 0.46 * Math.cos(2 * m_PI * i / (numSamples - 1));
	      break;
	   case 3:  // Hanning
	      for (i = 0; i < numSamples; i++)
	         in[i] *= 0.50 - 0.50 * Math.cos(2 * m_PI * i / (numSamples - 1));
	      break;
	   case 4:  // Blackman
	      for (i = 0; i < numSamples; i++) {
	          in[i] *= 0.42 - 0.5 * Math.cos (2 * m_PI * i / (numSamples - 1)) + 0.08 * Math.cos (4 * m_PI * i / (numSamples - 1));
	      }
	      break;
	   case 5:  // Blackman-Harris
	      for (i = 0; i < numSamples; i++) {
	          in[i] *= 0.35875 - 0.48829 * Math.cos(2 * m_PI * i /(numSamples-1)) + 0.14128 * Math.cos(4 * m_PI * i/(numSamples-1)) - 0.01168 * Math.cos(6 * m_PI * i/(numSamples-1));
	      }
	      break;
	   case 6:  // Welch
	      for (i = 0; i < numSamples; i++) {
	          in[i] *= 4*i/(float)numSamples*(1-(i/(float)numSamples));
	      }
	      break;
	   case 7:  // Gaussian (a=2.5)
	      // Precalculate some values, and simplify the fmla to try and reduce overhead
	      a=-2*2.5*2.5;
	      for (i = 0; i < numSamples; i++) {
	          // full
	          // in[i] *= exp(-0.5*(A*((i-NumSamples/2)/NumSamples/2))*(A*((i-NumSamples/2)/NumSamples/2)));
	          // reduced
	          in[i] *= Math.exp(a*(0.25 + ((i/(float)numSamples)*(i/(float)numSamples)) - (i/(float)numSamples)));
	      }
	      break;
	   case 8:  // Gaussian (a=3.5)
	      a=-2*3.5*3.5;
	      for (i = 0; i < numSamples; i++) {
	          // reduced
	          in[i] *= Math.exp(a*(0.25 + ((i/(float)numSamples)*(i/(float)numSamples)) - (i/(float)numSamples)));
	      }
	      break;
	   case 9:  // Gaussian (a=4.5)
	      a=-2*4.5*4.5;
	      for (i = 0; i < numSamples; i++) {
	          // reduced
	          in[i] *= Math.exp(a*(0.25 + ((i/(float)numSamples)*(i/(float)numSamples)) - (i/(float)numSamples)));
	      }
	      break;
	   default:
		  Log.d(TAG, "FFT::WindowFunc - Invalid window function whichFunction:" + whichFunction);
	   } // switch
	}

	// data -- some block of audio data (PCM time based)
	// width: the number of samples -- looks like it could be records * windowSize
	// calculates windowSize/2 frequency samples (1024 or 2048)
	// rate -- not used
	// output the spectrum
	// autocorrelation == compare with its self.
	// windowFunc -- only note shows use windowfunc = 3 (Hanning)
	// note: start += half so it is repeating twice
	Boolean computeSpectrum(float[] data, int width, int windowSize,
        double rate, float[] output, boolean autocorrelation, int windowFunc) {

		if (width < windowSize) return false;
		if (data == null || output == null) return false;
		float[] processed = new float[windowSize];

		int i;
		int half = windowSize / 2;
		float[] in = new float[windowSize];
		float[] out = new float[windowSize];
		float out2[] = new float[windowSize];
		int start = 0;
		int windows = 0;
		while (start + windowSize <= width) {
			for (i = 0; i < windowSize; i++) {
				in[i] = data[start + i];
			}
			windowFunc(windowFunc, windowSize, in);

			if (autocorrelation == true) {
				cpxFFT(windowSize, false, in, null, out, out2);
				// Compute power
				for (i = 0; i < windowSize; i++) {
					in[i] = (out[i] * out[i]) + (out2[i] * out2[i]);  
				}
				// Tolonen and Karjalainen recommend taking the cube root
				// of the power, instead of the square root
				for (i = 0; i < windowSize; i++) {
					in[i] = (float) Math.pow(in[i], (1.0f / 3.0f));
				}
				// Take FFT -- again !!??
				cpxFFT(windowSize, false, in, null, out, out2);
			} else {
				powerSpectrum(windowSize, in, out);
			}	
			// Take real part of result
			for (i = 0; i < half; i++)
				processed[i] += out[i];

			start += half;
			windows++;
		}  // wend

		if (autocorrelation) {

		// Peak Pruning as described by Tolonen and Karjalainen, 2000 -- KarjT99-pitch.pdf
		/* 
			Combine most of the calculations in a single for loop.
			It should be safe, as indexes refer only to current and previous elements,
			that have already been clipped, etc...
		 */
			for (i = 0; i < half; i++) {
				// Clip at zero, copy to temp array
				if (processed[i] < 0.0)
					processed[i] = 0.0f;
				out[i] = processed[i];
				// Subtract a time-doubled signal (linearly interp.) from the original
				// (clipped) signal
				if ((i % 2) == 0) {
					processed[i] -= out[i / 2];
				} else {
					processed[i] -= ((out[i / 2] + out[i / 2 + 1]) / 2);
				}
				// Clip at zero again
				if (processed[i] < 0.0) {
					processed[i] = 0.0f;
				}
			} // next

			// Reverse and scale
			for (i = 0; i < half; i++) {
				in[i] = processed[i] / (windowSize / 4);
			}
			for (i = 0; i < half; i++) {
				processed[half - 1 - i] = in[i];
			}
		} else { // autocorrelation
			// Convert to decibels
			// But do it safely; -Inf is nobody's friend
			for (i = 0; i < half; i++) {
				float temp=(processed[i] / windowSize / windows);
				if (temp > 0.0) {
					processed[i] = (float) (10.0f * Math.log10(temp));
				} else {
					processed[i] = 0f;
				} 
			} // next
		} // else autocorrelation

		for(i=0;i<half;i++) {
			output[i] = processed[i];
		}
		in = null;
		out = null;
		out2 = null;
		processed = null;
		return true;
	}

	
}
