import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class FFT_Runner {

	private static double SAMPLING_RATE = 100.0; //Samples per second
	private static double SIGNIFICANCE_THRESHOLD = 1.0; //Minimum significance of a frequency
	//hi sidd

	public static void main(String[] args) throws IOException {

		//Get the file and set up the scanners
		Scanner in = new Scanner(System.in);
		System.out.print("Please input the file name of the data you would like analyzed (don't forget the"
				+ ".txt): ");
		String fileName = in.nextLine(); //get the input file name
		in.close();
		File data = new File(fileName);
		Scanner fileReader = new Scanner(data);

		int fileLength = getFileLength(data); 
		final int N = getN(fileLength);

		double[] re = new double[N];
		double[] im = new double[N];

		/**
		 * Fill re with the original data. Note that the array's size may be greater than the number of
		 * data points
		 */
		for (int i = 0; i < fileLength; i++)
			re[i] = fileReader.nextDouble();
		fileReader.close();

		//		for (int i = 0; i < N; i++) //fill im with zeros, placeholder for future im code
		//			im[i] = 0;

		double[][] complex = analyze(N, re, im);
		//		printResults(results[0]); //print the results to a file

		double[][] magnitude = new double[2][N/2]; //stores each frequency and its respective magnitude

		for (int i = 0; i < magnitude[1].length; i++) //fill magnitude with the magnitudes
			magnitude[1][i] = (2.0 / (double)N) * imAbs(complex[0][i], complex[1][i]);

		double stepValue = SAMPLING_RATE/N; //step value for the frequency

		for (int i = 0; i < magnitude[0].length; i++) //fill magnitude with frequencies
			magnitude[0][i] = i * stepValue;

		ArrayList<Double> maxValues = getSignificantValues(magnitude);
		for (int i = 0; i < maxValues.size(); i++)
			System.out.println(maxValues.get(i) + " Hz");

	}

	/**
	 * Determines all frequencies that have a magnitude equal to or greater than SIGNIFICANCE_THRESHOLD 
	 * @param magnitude A 2D array of frequency and magnitude
	 * @return An ArrayList containing the significant frequencies
	 */
	private static ArrayList<Double> getSignificantValues(double[][] magnitude) {
		ArrayList<Double> values = new ArrayList<Double>();
		for (int i = 0; i < magnitude[1].length; i++)
			if (magnitude[1][i] >= SIGNIFICANCE_THRESHOLD)
				values.add(magnitude[0][i]);
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
	private static void printResults(double[] re) throws IOException {
		PrintWriter outputFile = new PrintWriter(new FileWriter("results.txt"));
		for (int i = 0; i < re.length; i++)
			outputFile.println(((int)(re[i]*1000)/1000.0));
		outputFile.close();
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
		double[][] results = new double[2][N/2]; //Only plots N/2 points because the magnitudes repeat afterwards
		for (int i = 0; i < results[0].length; i++)
			results[0][i] = re[i];
		for (int i = 0; i < results[1].length; i++)
			results[1][i] = im[i];
		return results;
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
