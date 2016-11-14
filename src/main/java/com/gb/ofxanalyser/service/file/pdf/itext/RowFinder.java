package com.gb.ofxanalyser.service.file.pdf.itext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

/**
 * Finds rows which contain all the specified words in the same order (case
 * sensitive). This may not actually be an actual sentence because the words are
 * not required to be adjacent. <br>
 * For eg, if the specified text is "a b c", both "a b c" and "a x b y c" will
 * match it.
 */
public class RowFinder implements RenderListener {

	private final static String WHITESPACE_REGEX = "\\s+";

	private String[] words;
	private Pattern pattern;
	private Map<Float, TreeSet<TextRenderInfo>> candidates = new HashMap<Float, TreeSet<TextRenderInfo>>();
	private Rectangle2D.Float[] result;

	public RowFinder(String text) {
		if (text == null || text.length() == 0) {
			throw new IllegalArgumentException("Specified text must not be empty!");
		}
		words = text.split(WHITESPACE_REGEX);
		pattern = Pattern.compile(text.replaceAll(WHITESPACE_REGEX, "|"));
	}

	public void renderText(TextRenderInfo renderInfo) {
		Matcher m = pattern.matcher(renderInfo.getText());

		if (m.find()) {
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

			if (lineCandidates.size() >= words.length) {
				int index = 0;
				Rectangle2D.Float rectangle = null;

				for (Iterator<TextRenderInfo> i = lineCandidates.iterator(); i.hasNext();) {
					TextRenderInfo renderInfo = i.next();

					if (index < words.length && words[index].equals(renderInfo.getText())) {
						if (rectangle == null) {
							rectangle = renderInfo.getAscentLine().getBoundingRectange();
						}
						rectangle.add(renderInfo.getDescentLine().getBoundingRectange());

						if (index == words.length - 1) {
							resultList.add(rectangle);
							break;
						} else {
							index++;
						}
					}
				}
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
}
