package com.gb.ofxanalyser.service.finance.parser.pdf.hsbc;

import com.gb.ofxanalyser.util.dynagrid.Grid;
import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

public class BoundedTableFinder implements RenderListener {

	private Rectangle2D.Float bounds;
	private Grid<Float, String> grid;

	public BoundedTableFinder(Rectangle2D.Float bounds) {
		this.bounds = bounds;
		grid = new Grid<Float, String>();
	}

	public void renderText(TextRenderInfo renderInfo) {
		Rectangle2D.Float ascRect = renderInfo.getAscentLine().getBoundingRectange();
		Rectangle2D.Float descRect = renderInfo.getDescentLine().getBoundingRectange();

		if (ascRect.y >= bounds.y && ascRect.x >= bounds.x
				&& (descRect.y + descRect.height) <= (bounds.y + bounds.height)
				&& (descRect.x + descRect.width) <= (bounds.x + bounds.width)) {
			grid.add(ascRect.x, ascRect.y, renderInfo.getText());
		}
	}

	public Grid<Float, String> getTable() {
		return grid;
	}

	public void beginTextBlock() {
	}

	public void endTextBlock() {
	}

	public void renderImage(ImageRenderInfo renderInfo) {
	}
}
