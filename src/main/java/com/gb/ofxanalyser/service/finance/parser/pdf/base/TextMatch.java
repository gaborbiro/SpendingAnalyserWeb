package com.gb.ofxanalyser.service.finance.parser.pdf.base;

/**
 * Cartesian coordinate system. Origo is in the lower left corner
 *
 */
public class TextMatch {

	private float left;
	private float bottom;
	private float right;
	private float top;
	private float width;
	private float height;

	public TextMatch(float left, float bottom, float right, float top, float width, float height) {
		this.left = left;
		this.bottom = bottom;
		this.right = right;
		this.top = top;
		this.width = width;
		this.height = height;
	}

	/**
	 * Getter for the left margin.
	 * 
	 * @return the X position of the left margin
	 */
	public float getLeft() {
		return left;
	};

	/**
	 * Getter for the bottom margin.
	 * 
	 * @return the Y position of the bottom margin
	 */
	public float getBottom() {
		return bottom;
	};

	/**
	 * Getter for the right margin.
	 * 
	 * @return the X position of the right margin
	 */
	public float getRight() {
		return right;
	};

	/**
	 * Getter for the top margin.
	 * 
	 * @return the Y position of the top margin
	 */
	public float getTop() {
		return top;
	};

	/**
	 * Gets the width of the text block.
	 * 
	 * @return a width
	 */
	public float getWidth() {
		return width;
	};

	/**
	 * Gets the height of the text block.
	 * 
	 * @return a height
	 */
	public float getHeight() {
		return height;
	};
}
