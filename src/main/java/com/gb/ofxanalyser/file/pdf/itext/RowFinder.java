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
	private Map<Float, TreeSet<TextRenderInfo>> candidates;
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
		candidates = new HashMap<Float, TreeSet<TextRenderInfo>>();
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

			// if (lineCandidates.size() >= target.length) {
			// int index = 0;
			// Rectangle2D.Float rectangle = null;
			//
			// for (Iterator<TextRenderInfo> i = lineCandidates.iterator();
			// i.hasNext();) {
			// TextRenderInfo renderInfo = i.next();
			//
			// if (index < target.length &&
			// target[index].equals(renderInfo.getText())) {
			// if (rectangle == null) {
			// rectangle = renderInfo.getAscentLine().getBoundingRectange();
			// }
			// rectangle.add(renderInfo.getDescentLine().getBoundingRectange());
			//
			// if (index == target.length - 1) {
			// resultList.add(rectangle);
			// break;
			// } else {
			// index++;
			// }
			// }
			// }
			// }

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
		return match_(target.getBytes(), input.getBytes())
				|| match_(reverse(target.getBytes()), reverse(input.getBytes()));
	}

	private static boolean match_(byte[] target, byte[] input) {
		if (target == null || target.length == 0 || input == null || input.length == 0) {
			return false;
		}
		int tI = 0;

		for (int i = 0; i < input.length; i++) {
			if (input[i] == target[tI]) {
				tI++;
			} else {
				// reset search
				tI = 0;
			}
		}
		return tI > 0;
	}

	public static String stripWhitespace(String str) {
		return str.trim().replaceAll("\\s", "");
	}

	public static byte[] reverse(final byte[] array) {
		if (array == null) {
			return null;
		}
		byte[] result = new byte[array.length];
		System.arraycopy(array, 0, result, 0, array.length);
		int i = 0;
		int j = result.length - 1;
		byte tmp;
		while (j > i) {
			tmp = result[j];
			result[j] = result[i];
			result[i] = tmp;
			j--;
			i++;
		}
		return result;
	}

	public static int search(int[] lengths, int position) {
		for (int i = 0; i < lengths.length; i++) {
			if (position < lengths[i]) {
				return i;
			}
		}
		return -1;
	}
}
