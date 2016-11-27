package com.gb.ofxanalyser.file.pdf;

/**
 * Origo is in the upper bottom corner. <br>
 * Bottom is smaller than top.<br>
 * Left is smaller than right.
 */
public class Rect {

	private float left;
	private float bottom;
	private float right;
	private float top;

	public Rect(float left, float bottom, float right, float top) {
		this.left = left;
		this.bottom = bottom;
		this.right = right;
		this.top = top;
	}

	/**
	 * Getter for the left margin.
	 * 
	 * @return the X position of the left margin
	 */
	public float getLeft() {
		return left;
	}

	/**
	 * Getter for the bottom margin.
	 * 
	 * @return the Y position of the bottom margin
	 */
	public float getBottom() {
		return bottom;
	}

	/**
	 * Getter for the right margin.
	 * 
	 * @return the X position of the right margin
	 */
	public float getRight() {
		return right;
	}

	/**
	 * Getter for the top margin.
	 * 
	 * @return the Y position of the top margin
	 */
	public float getTop() {
		return top;
	}

	/**
	 * Gets the width of the text block.
	 * 
	 * @return a width
	 */
	public float getWidth() {
		return right - left;
	}

	/**
	 * Gets the height of the text block.
	 * 
	 * @return a height
	 */
	public float getHeight() {
		return top - bottom;
	}

	@Override
	public String toString() {
		return "Rect [left=" + left + ", bottom=" + bottom + ", right=" + right + ", top=" + top + "]";
	};
}
