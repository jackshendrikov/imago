package ua.jackshen.editor;

public class Utils {

	public static double ComputeRatio(double sourceNum, double sourceDenom, double targetNum)
	{
		double targetDenom = (targetNum * sourceDenom)/sourceNum;
		return targetDenom;
	}

}
