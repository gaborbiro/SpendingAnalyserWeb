package com.gb.ofxanalyser.file.pdf.itext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

/**
 * Finds rows which contain all the specified target in the same order (case
 * sensitive). This may not actually be an actual sentence because the target
 * are not required to be adjacent. <br>
 * For eg, if the specified text is "a b c", both "a b c" and "a x b y c" will
 * match it.
 */
public class RowFinder implements RenderListener {

	private String target;
	private Map<Float, TreeSet<TextRenderInfo>> candidates = new HashMap<Float, TreeSet<TextRenderInfo>>();
	private Rectangle2D.Float[] result;

	public RowFinder(String text) {
		text = stripWhitespace(text);
		if (text == null || text.length() == 0) {
			throw new IllegalArgumentException("Specified text must not be empty!");
		}
		target = text;
	}

	public void renderText(TextRenderInfo renderInfo) {
		String input = stripWhitespace(renderInfo.getText());
		if (match(target, input)) {
			float y = renderInfo.getAscentLine().getBoundingRectange().y;
			TreeSet<TextRenderInfo> lineCandidates = candidates.get(y);

			if (lineCandidates == null) {
				lineCandidates = new TreeSet<TextRenderInfo>(new Comparator<TextRenderInfo>() {

					public int compare(TextRenderInfo o1, TextRenderInfo o2) {
						return (int) (o1.getAscentLine().getBoundingRectange().x
								- o2.getAscentLine().getBoundingRectange().x);
					}
				});
				candidates.put(y, lineCandidates);
			}
			lineCandidates.add(renderInfo);
		}
	}

	public void beginTextBlock() {
//		candidates = new HashMap<Float, TreeSet<TextRenderInfo>>();
	}

	public void endTextBlock() {
		Float[] keys = candidates.keySet().toArray(new Float[candidates.size()]);
		Arrays.sort(keys, new Comparator<Float>() {

			public int compare(Float o1, Float o2) {
				return (int) (o2 - o1);
			}
		});
		List<Rectangle2D.Float> resultList = new ArrayList<Rectangle2D.Float>();

		for (float y : keys) {
			TreeSet<TextRenderInfo> lineCandidates = candidates.get(y);
			TextRenderInfo[] candidateArray = lineCandidates.toArray(new TextRenderInfo[lineCandidates.size()]);
			int[] lengths = new int[candidateArray.length];
			StringBuffer line = new StringBuffer();

			for (int i = 0; i < candidateArray.length; i++) {
				String text = stripWhitespace(candidateArray[i].getText());
				line.append(text);
				lengths[i] = (i > 0 ? lengths[i - 1] : 0) + text.length();
			}
			int index;
			if ((index = line.toString().indexOf(new String(target))) >= 0) {
				TextRenderInfo renderInfo = candidateArray[search(lengths, index)];
				Rectangle2D.Float rectangle = renderInfo.getAscentLine().getBoundingRectange();
				rectangle.add(renderInfo.getDescentLine().getBoundingRectange());
				resultList.add(rectangle);
				break; // we only consider the first match in the row
			}
		}
		result = resultList.toArray(new Rectangle2D.Float[resultList.size()]);
	}

	public void renderImage(ImageRenderInfo renderInfo) {
	}

	/**
	 * Getter for the left margin.
	 * 
	 * @return the X position of the left margin
	 */
	public float getLlx(int index) {
		return getResult(index).x;
	}

	/**
	 * Getter for the bottom margin.
	 * 
	 * @return the Y position of the bottom margin
	 */
	public float getLly(int index) {
		return getResult(index).y;
	}

	/**
	 * Getter for the right margin.
	 * 
	 * @return the X position of the right margin
	 */
	public float getUrx(int index) {
		return getResult(index).x + getResult(index).width;
	}

	/**
	 * Getter for the top margin.
	 * 
	 * @return the Y position of the top margin
	 */
	public float getUry(int index) {
		return getResult(index).y + getResult(index).height;
	}

	/**
	 * Gets the width of the text block.
	 * 
	 * @return a width
	 */
	public float getWidth(int index) {
		return getResult(index).width;
	}

	/**
	 * Gets the height of the text block.
	 * 
	 * @return a height
	 */
	public float getHeight(int index) {
		return getResult(index).height;
	}

	public int getCount() {
		return result.length;
	}

	private Rectangle2D.Float getResult(int index) {
		return result[index];
	}

	private boolean match(String target, String input) {
		Map<Byte, List<Integer>> inputMap = new HashMap<>();
		byte[] inputBytes = input.getBytes();

		for (int i = 0; i < inputBytes.length; i++) {
			List<Integer> positions = inputMap.get(inputBytes[i]);

			if (positions == null) {
				positions = new ArrayList<>();
				inputMap.put(inputBytes[i], positions);
			}
			positions.add(i);
		}

		byte[] targetBytes = target.getBytes();

		for (int i = 0; i < targetBytes.length; i++) {
			List<Integer> indexes = inputMap.get(targetBytes[i]);

			if (indexes == null) {
				continue;
			}

			for (int index : indexes) {
				int inputI = index;
				int targetI = i;
				do {
					inputI++;
					targetI++;
				} while (inputI < inputBytes.length && targetI < targetBytes.length
						&& inputBytes[inputI] == targetBytes[targetI]);
				boolean prefixMatch = i == 0 && inputI == inputBytes.length;
				boolean middleMatch = index == 0 && inputI == inputBytes.length;
				boolean postfixMatch = targetI == targetBytes.length && index == 0;
				if (prefixMatch || middleMatch || postfixMatch) {
					return true;
				}
			}
		}
		return false;
	}

	public static void main(String[] args) {
		System.out.println("T:");
		System.out.println(new RowFinder("a").match("BALANCEBROUGHTFORWARD", "BALANCEBROUGHTFORWARD"));
		System.out.println(new RowFinder("a").match("BALANCEBROUGHTFORWARD", "B"));
		System.out.println(new RowFinder("a").match("BALANCEBROUGHTFORWARD", "BALANCE"));
		System.out.println(new RowFinder("a").match("BALANCEBROUGHTFORWARD", "BROUGHT"));
		System.out.println(new RowFinder("a").match("BALANCEBROUGHTFORWARD", "FORWARD"));
		System.out.println(new RowFinder("a").match("BALANCEBROUGHTFORWARD", "aaaBALANCE"));
		System.out.println(new RowFinder("a").match("BALANCEBROUGHTFORWARD", "FORWARDaaa"));
		System.out.println("\nF:");
		System.out.println(new RowFinder("a").match("BALANCEBROUGHTFORWARD", "BALANCEaaa"));
		System.out.println(new RowFinder("a").match("BALANCEBROUGHTFORWARD", "aaaBROUGHT"));
		System.out.println(new RowFinder("a").match("BALANCEBROUGHTFORWARD", "aaaBROUGHTaaa"));
		System.out.println(new RowFinder("a").match("BALANCEBROUGHTFORWARD", "BROUGHTaaa"));
		System.out.println(new RowFinder("a").match("BALANCEBROUGHTFORWARD", "aaaFORWARD"));
		System.out.println(new RowFinder("a").match("BALANCEBROUGHTFORWARD", " "));
	}

	public static String stripWhitespace(String str) {
		return str.trim().replaceAll("\\s", "");
	}

	public static int search(int[] lengths, int position) {
		for (int i = 0; i < lengths.length; i++) {
			if (position < lengths[i]) {
				return i;
			}
		}
		return -1;
	}

	private static String print(Map<Float, TreeSet<TextRenderInfo>> candidates) {
		StringBuffer result = new StringBuffer();
		Float[] keys = candidates.keySet().toArray(new Float[candidates.size()]);
		Arrays.sort(keys, new Comparator<Float>() {

			public int compare(Float o1, Float o2) {
				return (int) (o2 - o1);
			}
		});
		for (float y : keys) {
			TreeSet<TextRenderInfo> lineCandidates = candidates.get(y);
			TextRenderInfo[] candidateArray = lineCandidates.toArray(new TextRenderInfo[lineCandidates.size()]);
			StringBuffer line = new StringBuffer();

			for (int i = 0; i < candidateArray.length; i++) {
				String text = stripWhitespace(candidateArray[i].getText());
				line.append(text);
			}
			result.append(line);
			result.append("\n");
		}
		return result.toString();
	}
}
