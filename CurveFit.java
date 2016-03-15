package com.modelsw.birdingviamic;

import android.util.Log;

public class CurveFit {
    // Includes: CurveFit, InitPoly, SelectY, SetupMatrix, SquareUpMatrix, CalcPoly, CalcResiduals, SaveCoefficients
	private static final String TAG = "CurveFit";
	int errFlag;
	int lastRow;
	int lastCol;
	int degree;
	float[] x;
	float[] y;
	public static float[] polyCoef;	
	public static float corrCoef;
	private float[][] datMatrix;
	private float[][] coefArry;
	float[] constVectr;
	float[][] workBuf;
	float[] solutnVectr;
	
	CurveFit() {
		// empty constructor
	}

	public void calcPolynomial(int phraseLength, int startI) {  // phraseLength
	    //Log.d(TAG, "phraseLength:" + phraseLength + " startI:" + startI );

		errFlag = 0;
		lastCol = 2;
		degree = 1;
		polyCoef = new float[lastCol];
		for (int i = 0; i< lastCol; i++) {  // pre-set to zero incase a short record you don't get null
			polyCoef[i] = 0.0f;
		}
		lastRow = phraseLength;
		if (lastRow == 0) {
			errFlag = 1;
			Log.d(TAG, "No Input data found ");
			return;
		} 
		y = new float[lastRow];
		x = new float[lastRow];
    	for (int k=0; k<lastRow; k++) {
    		x[k] = (float) k;
    		y[k] = (float) PlaySong.pitch[k+startI]; // 0 is fft freq; 1 is count zero crossings to freq 
    			//Log.d(TAG, "k:" + k + " x[k]:" + x[k] + " y[k]:" + y[k] );
				//Log.d(TAG, x[k] + "," + y[k] );
    	}
		if (lastCol > lastRow) {
			lastCol = lastRow;
			degree = lastCol - 1;
//			PlaySong.polyCoef = new float[lastCol];  // don't re-dim or you can't save in record 
		}
    
		if (lastRow > 0) {
			// echo " ";
			//Log.d(TAG, "Before SetupMatrix lastRow:" + lastRow  + " lastCol:" + lastCol + " degree:" + degree);
			setupMatrix();
			// echo "Before SquareUpMatrix ";
			squareUpMatrix();
			// echo "Before CalcPoly<br> ";
			errFlag = calcPoly();
			if (errFlag == 1) {
				Log.d(TAG, "Degree Exceedes Input Data ");      
				return;
			} // ErrFlag
			errFlag = calcResiduals();
		} // LastRow
	    //Log.d(TAG, " coef:" + PlaySong.polyCoef[0] + ", " + PlaySong.polyCoef[1] + ", " + PlaySong.polyCoef[2]
    	//		+ " corr:" + PlaySong.corrCoef );
		return;
	} // calcPolynomial
	
	// called from voiced fix to see if phrases increase or decrease or remains steady
	public void polyPhrase(int dimLen, int[] phraseCount) {   
	    //Log.d(TAG, "phraseLength:" + phraseLength + " startI:" + startI );

		errFlag = 0;
		lastCol = 3;
		degree = 2;
		polyCoef = new float[lastCol];
		for (int i = 0; i< lastCol; i++) {  // pre-set to zero incase a short record you don't get null
			polyCoef[i] = 0.0f;
		}
		lastRow = dimLen;
		if (lastRow == 0) {
			errFlag = 1;
			Log.d(TAG, "No Input data found ");
			return;
		} 
		y = new float[lastRow];
		x = new float[lastRow];
    	for (int k=0; k<lastRow; k++) {
    		x[k] = (float) k;
    		y[k] = (float) phraseCount[k]; //  
    			//Log.d(TAG, "k:" + k + " x[k]:" + x[k] + " y[k]:" + y[k] );
    	}
		if (lastCol > lastRow) {
			lastCol = lastRow;
			degree = lastCol - 1;
		}
    
		if (lastRow > 0) {
			// echo " ";
			//Log.d(TAG, "Before SetupMatrix lastRow:" + lastRow  + " lastCol:" + lastCol + " degree:" + degree);
			setupMatrix();
			// echo "Before SquareUpMatrix ";
			squareUpMatrix();
			// echo "Before CalcPoly<br> ";
			errFlag = calcPoly();
			if (errFlag == 1) {
				Log.d(TAG, "Degree Exceedes Input Data ");      
				return;
			} // ErrFlag			
			errFlag = calcResiduals();
		} // LastRow
	    //Log.d(TAG, " coef:" + coef[0] + ", " + coef[1] + ", " + coef[2]
    	//		+ " corr:" + corrCoef );
		return;
	} // polyPhrase
	
	// called from voiced fix to see if phrases increase or decrease or remains steady
	public void polySN(int dimLen, float[] sn) {   
	    //Log.d(TAG, "phraseLength:" + phraseLength + " startI:" + startI );

		errFlag = 0;
		lastCol = 3;
		degree = 2;
		polyCoef = new float[lastCol];
		for (int i = 0; i< lastCol; i++) {  // pre-set to zero incase a short record you don't get null
			polyCoef[i] = 0.0f;
		}
		lastRow = dimLen;
		if (lastRow == 0) {
			errFlag = 1;
			Log.d(TAG, "No Input data found ");
			return;
		} 
		y = new float[lastRow];
		x = new float[lastRow];
    	for (int k=0; k<lastRow; k++) {
    		x[k] = (float) k;
    		y[k] = sn[k]; //  
    			//Log.d(TAG, "k:" + k + " x[k]:" + x[k] + " y[k]:" + y[k] );
    	}
		if (lastCol > lastRow) {
			lastCol = lastRow;
			degree = lastCol - 1;
		}
    
		if (lastRow > 0) {
			// echo " ";
			//Log.d(TAG, "Before SetupMatrix lastRow:" + lastRow  + " lastCol:" + lastCol + " degree:" + degree);
			setupMatrix();
			// echo "Before SquareUpMatrix ";
			squareUpMatrix();
			// echo "Before CalcPoly<br> ";
			errFlag = calcPoly();
			if (errFlag == 1) {
				Log.d(TAG, "Degree Exceedes Input Data ");      
				return;
			} // ErrFlag
			errFlag = calcResiduals();
		} // LastRow
	    //Log.d(TAG, " coef:" + coef[0] + ", " + coef[1] + ", " + coef[2]
    	//		+ " corr:" + corrCoef );
		return;
	} // polyPhrase
	
	private void setupMatrix() {    
		// setup data matrix
		datMatrix = new float[lastRow][lastCol];
	    for (int i = 0; i < lastRow; i++) {
	    	for (int j = 0; j < lastCol; j++) {
	    		datMatrix[i][j] = (float) Math.pow(x[i],j);
	    		//Log.d(TAG, "datMatrix: i:" + i + " j:" + j  + " datMatrix:" + datMatrix[i][j] );
	    	} // Next j
	    } // Next i
	} // SetupMatrix

	private void squareUpMatrix() {
		coefArry = new float[lastCol][lastCol];
		constVectr = new float[lastCol];
		for (int i=0; i<lastCol; i++) {
			for (int j=0; j<=i; j++) {
				coefArry[i][j] = 0.0f;
				for (int k=0; k < lastRow; k++) {
					//Log.d(TAG, "k:" + k + " j:" + j + "datMatrix[k][j]:" + datMatrix[k][j] + " i:" + i + "datMatrix[k][i]:" + datMatrix[k][i]); 
					coefArry[i][j] += datMatrix[k][j] * datMatrix[k][i];
		    		//Log.d(TAG, "k:" + k + " i:" + i + " j:" + j  + " coefArry[i][j]:" + coefArry[i][j]  );
					if (i != j) {
						coefArry[j][i] = coefArry[i][j];
					}
				} // next k
			} // next j
			constVectr[i] = 0;
			for (int j=0; j<lastRow; j++) {
				constVectr[i] += y[j] * datMatrix[j][i];
			} // next j	
		} // next i
	} // squareUpMatrix

	private int calcPoly() {
		float[][] workBuf = new float[lastCol][lastCol];
		float[] solutnVectr = new float[lastCol];
		int[] rowIndex = new int[lastCol];
		int[] colIndex = new int[lastCol];
		float[] workIndex = new float[lastCol];
		int rowInx = 0;
		int colInx = 0;
		float largeElement;
		float tempX;
		float determinant;
		float pivot;
		
	    for (int i = 0; i < lastCol; i++) {
		      for (int j = 0; j < lastCol; j++) {
		          workBuf[i][j] = coefArry[i][j];
		      } // Next j
		      solutnVectr[i] = constVectr[i];
		} // Next i
		for (int i = 0; i < lastCol; i++) {  // zero out index
		    rowIndex[i] = 0;
		    colIndex[i] = 0;
		    workIndex[i] = 0;
		} // Next i
		determinant = 1;
		for (int i=0; i<lastCol; i++) { // find the polynomial
		    largeElement = 0;
		      for (int j = 0; j < lastCol; j++) {
		        if (workIndex[j] != 1) {  // == 1 GoTo ContLargJ;
		          for (int k = 0; k < lastCol; k++) {
		            if (workIndex[k] > 1) {
		              errFlag = 1;
		              return errFlag;
		            }
		            if (workIndex[k] != 1) { // == 1 GoTo ContLargK
		              if (largeElement < Math.abs(workBuf[j][k])) { // >= GoTo ContLargK;
		                rowInx = j;
		                colInx = k;
		                largeElement = Math.abs(workBuf[j][k]);
		              } // ContLargK
		            } // ContLargK 
		          } // Next k
		        }  // ContLargJ
		      } // Next j
		      workIndex[colInx] = workIndex[colInx] + 1;
		      rowIndex[i] = rowInx;
		      colIndex[i] = colInx;
		      if (rowInx != colInx) { // goto GetPivot  //largest pivot on diagonal
		        determinant = -determinant;
		        for (int j = 0; j < lastCol; j++) {
		          tempX = workBuf[rowInx][j];
		          workBuf[rowInx][j] = workBuf[colInx][j];
		          workBuf[colInx][j] = tempX;
		        } // Next j
		        tempX = solutnVectr[rowInx];
		        solutnVectr[rowInx] = solutnVectr[colInx];
		        solutnVectr[colInx] = tempX;
		      } // GetPivot:    // divide pivot row by pivot element
		      pivot = workBuf[colInx][colInx];
		      determinant = determinant * pivot;
		      workBuf[colInx][colInx] = 1;
		      for (int j = 0; j < lastCol; j++) {
		        workBuf[colInx][j] = workBuf[colInx][j] / pivot;
		      } // Next j
		      solutnVectr[colInx] = solutnVectr[colInx] / pivot;
		    for (int j = 0; j < lastCol; j++) { // reduce nonpivot rows
		        if (j != colInx) { // goto ContReduce;
		          tempX = workBuf[j][colInx];
		          workBuf[j][colInx] = 0;
		          for (int k = 0; k < lastCol; k++) {
		             workBuf[j][k] = workBuf[j][k] - workBuf[colInx][k] * tempX;
		          } // Next k
		          solutnVectr[j] = solutnVectr[j] - solutnVectr[colInx] * tempX;
		        } // ContReduce:
		    } // Next j
		} // Next i
		
		for (int i = 0; i < lastCol; i++) { // interchange columns
			int l;		
		    l = lastCol - 1;  // +1 leftover
		    if (rowIndex[l] != colIndex[l]) { // if == goto ContIntrchg;
		        rowInx = rowIndex[l];
		        colInx = colIndex[l];
		        for (int j = 0; j < lastCol; j++) {
		           tempX = workBuf[j][rowInx];
		           workBuf[j][rowInx] = workBuf[j][colInx];
		           workBuf[j][colInx] = tempX;
		        } // Next j
		    } //  ContIntrchg:
		 } // Next i
		 for (int j = 0; j < lastCol; j++) {
		     if (workIndex[j] != 1) { 
		        errFlag = 1;
		        // echo "Error in WorkIndex not 1<br>";
		        return errFlag;
		      }  
		    } // Next j
		    // echo "In CalcPoly LastCol= $LastCol ErrFlag=$ErrFlag<br>";
		    for (int i = 0; i < lastCol; i++) {      
		      polyCoef[i] = (float) solutnVectr[i];
		      if (i > lastCol) {
		    	  polyCoef[i] = 0;
		      }
			  //Log.d(TAG, "i:" + i + " coef:" + PlaySong.polyCoef[i] );			 			
	    } // Next i		 
		return 0;
	} // CalcPoly

	private int calcResiduals() {
		    // echo "CalcResiduals" . "<br>\n";
		    corrCoef = 0.0f;
			float denom = 0;
		    float sumY = 0.0f;
		    float sumYSqrd = 0.0f;
		    float sumResidSqrd = 0.0f;
		    float corrCoef1 = 0.0f;
		    for (int i = 0; i < lastRow; i++) {
		      float yCalcVal = 0f;
		      for (int j = 0; j < lastCol; j++) {
		        yCalcVal = yCalcVal + polyCoef[j] * datMatrix[i][j];
		      } // Next j
		      float resid = yCalcVal - y[i];
		      sumResidSqrd = sumResidSqrd + resid * resid;
		      sumY += y[i];
		      sumYSqrd +=  y[i] * y[i];
		    } // Next i
		    if (sumYSqrd == 0) { 
		    	corrCoef = -1;
		      return -1;
		    } else if (sumResidSqrd == 0) {
		        corrCoef = 1;
		    } else {
		    	denom = (sumYSqrd - sumY * sumY / lastRow);
		    	if (denom == 0) {
		    		corrCoef = 1;
		    	} else {
					//calcResiduals denom:312.90625 sumResidSqrd:312.91495 corrCoef:NaN
					// sqrt of a negative number --> NaN
					if (sumResidSqrd > denom) {
						//corrCoef = (float) Math.sqrt(1.0f - sumResidSqrd / denom);
						corrCoef = 0;
					} else {
						corrCoef = (float) Math.sqrt(1.0f - sumResidSqrd / denom);
					}
				}
		    } // End If
			//Log.d(TAG, "calcResiduals denom:" + denom + " sumResidSqrd:" + sumResidSqrd + " corrCoef:" + corrCoef);
		    return 0;
	} // CalcResiduals

}	// CurveFit