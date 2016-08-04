import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class FFT_Runner {

	private static int Fs = 205; //Samples per second
	private static double SIGNIFICANCE_THRESHOLD = 1.0; //Minimum significance of a frequency
	

	public static void main(String[] args) throws IOException {

		//Get the file and set up the scanners
		Scanner in = new Scanner(System.in);
		System.out.print("Please input the file name of the data you would like analyzed (don't forget the"
				+ ".txt): ");
		String fileName = in.nextLine(); //get the input file name
		in.close();
		File data = new File(fileName);
		Scanner fileReader = new Scanner(data);
		
		/*//gets the sampling rate TODO fix this so no need to hard code
		 * 
		System.out.print("Please input the sampling rate of the data: ");
		Scanner Fs = new Scanner(System.in);
		int SAMPLING_RATE = Fs.nextInt();
		Fs.close();*/

		int fileLength = getFileLength(data); 
		final int N = getN(fileLength);

		double[] re = new double[N];
		double[] im = new double[N];
		double[] mag = new double[N];
		double[] mag_Smoothed = new double [N];

		/**
		 * Fill re with the original data. Note that the array's size may be greater than the number of
		 * data points
		 */
		for (int i = 0; i < fileLength; i++)
			re[i] = fileReader.nextDouble();
		fileReader.close();

		for (int i = 0; i < N; i++) 
			im[i] = 0;

		double[][] complex = analyze(N, re, im);
		
		
		for(int i = 0; i < N; i++) //takes the complex input and calculates magnitude
		{
			mag[i] = imAbs(complex[0][i], complex[1][i]);;
		}

		printResults(complex, mag);
		
		
		System.out.println("The Max Index is: " + getMaxIndex(mag, N, Fs));
		System.out.println("The Max Freq is: " +  getMaxIndex(mag, N, Fs) * Fs / N);
		
	/*	
	 * It's hard to hard-code the significance especially now
	 * 
	 * 
	 * ArrayList<Double> maxValues = getSignificantValues(mag);
		for (int i = 0; i < maxValues.size(); i++)
			System.out.println(maxValues.get(i) + " Hz");		
	*/		

	}

	/**
	 * Determines all frequencies that have a magnitude equal to or greater than SIGNIFICANCE_THRESHOLD 
	 * @param magnitude A 2D array of frequency and magnitude
	 * @return An ArrayList containing the significant frequencies
	 */
	private static ArrayList<Double> getSignificantValues(double[] magnitude) {
		ArrayList<Double> values = new ArrayList<Double>();
		for (int i = 0; i < magnitude.length; i++)
			if (magnitude[i] >= SIGNIFICANCE_THRESHOLD)
				values.add(magnitude[i]);
		return values;
	}

	/**
	 * Determines the absolute value of a complex number using the pythagorean theorem
	 * @param re The real component of the complex number
	 * @param im The imaginary component of the complex number
	 * @return The absolute value of the complex number
	 */
	private static double imAbs(double re, double im) {
		return Math.sqrt(Math.pow(re, 2.0) + Math.pow(im, 2.0));
	}

	/**
	 * Prints the analyzed data to a text file called results
	 * @param re The real number component of the data
	 * @throws IOException
	 */
	private static void printResults(double[][] complex, double[] mag) throws IOException {
		PrintWriter outputFile_Re = new PrintWriter(new FileWriter("results_Re.txt"));
		for (int i = 0; i < complex[0].length; i++)
			outputFile_Re.println(((int)(complex[0][i]*1000)/1000.0));
		outputFile_Re.close();
		
		PrintWriter outputFile_Im = new PrintWriter(new FileWriter("results_Im.txt"));
		for (int i = 0; i < complex[1].length; i++)
			outputFile_Im.println(((int)(complex[1][i]*1000)/1000.0));
		outputFile_Im.close();
		
		PrintWriter outputFile_Mag = new PrintWriter(new FileWriter("results_Mag.txt"));
		for (int i = 0; i < complex[1].length; i++)
			outputFile_Mag.println(mag[i]);
		outputFile_Mag.close();
	}

	/**
	 * Applies Fourier analysis to the input data
	 * @param N The number of values in the data 
	 * @param re The real numbers
	 * @param im The imaginary numbers
	 * @return A 2d array in which the first index is the real number data and the second index is the
	 * imaginary data
	 */
	private static double[][] analyze(int N, double[] re, double[] im) {
		FFT fft = new FFT(N);
		fft.fft(re, im); //Convert the data into complex numbers
		double[][] results = new double[2][N]; 
		for (int i = 0; i < results[0].length; i++)
			results[0][i] = re[i];
		for (int i = 0; i < results[1].length; i++)
			results[1][i] = im[i];
		return results;
	}
	
	
	/**
	 *Identifies the index of the maximum magnitdue 
	 * @param array1
	 * @param N
	 * @param Fs
	 * @return the max index; frequency is calculated in the main method -- due to locality of Fs
	 */	
	private static double getMaxIndex(double[] array1, int N, int Fs)
	{
		int n = N / 2;
		int maxIndex = 1;
		for(int i = 1; i <= n; 	i++)  //need to ignore the first reading
		{
			if(array1[i] > array1[maxIndex])
			{
				maxIndex = i;
			}
		}
		return maxIndex;
	}
	
	private static double[] Smooth(double[] mag, int N)
	{
		double value = mag[0];
		int smoothing = 10; //degree of smoothing
		double[] mag_Smoothed;
		
		for(int i = 0; i < N; i++)
		{
			double tempValue = mag[i];
			value += (tempValue - value) / smoothing;
			mag_Smoothed[i] = value;	
		}
		
		return mag_Smoothed;
	}
	
	

	/**
	 * Determines the length of the file and then sets the N value to a power of 2 TODO clean up
	 * @param fileLength
	 * @return A valid N value
	 */
	private static int getN(int fileLength) {
		if (fileLength < 8)
			return 8;
		else if (fileLength <= 16)
			return 16;
		else if (fileLength <= 32)
			return 32;
		else if (fileLength <= 64)
			return 64;
		else if (fileLength <= 128)
			return 128;
		else if (fileLength <= 256)
			return 256;
		else if (fileLength <= 512)
			return 512;
		else if (fileLength <= 1024)
			return 1024;
		else if (fileLength <= 2048)
			return 2048;
		else if (fileLength <= 4096)
			return 4096;
		else if (fileLength <= 8192)
			return 8192;
		else
			return fileLength;
	}

	/**
	 * Determines the number of lines in the input file
	 * @param data The input file
	 * @return The number of lines in the text file
	 * @throws IOException
	 */
	private static int getFileLength(File data) throws IOException {
		Scanner fileReader = new Scanner(data);
		int numLines = 0;
		while (fileReader.hasNext()) {
			numLines++;
			fileReader.next();
		}
		fileReader.close();
		return numLines;
	}

}
