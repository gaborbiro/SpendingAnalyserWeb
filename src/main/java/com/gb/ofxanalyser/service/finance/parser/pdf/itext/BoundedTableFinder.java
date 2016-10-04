package com.gb.ofxanalyser.service.finance.parser.pdf.itext;

import com.gb.ofxanalyser.service.finance.parser.pdf.Rect;
import com.gb.ofxanalyser.service.finance.parser.pdf.StringGrid;
import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

public class BoundedTableFinder implements RenderListener {

	private Rect bounds;
	private StringGrid grid;

	public BoundedTableFinder(Rect bounds) {
		this.bounds = bounds;
		grid = new StringGrid();
	}

	public void renderText(TextRenderInfo renderInfo) {
		Rectangle2D.Float ascRect = renderInfo.getAscentLine().getBoundingRectange();
		Rectangle2D.Float descRect = renderInfo.getDescentLine().getBoundingRectange();

		if (ascRect.y <= bounds.getTop() && descRect.y >= bounds.getBottom() && ascRect.x >= bounds.getLeft()
				&& (descRect.x + descRect.width) <= bounds.getRight()) {
			grid.add(ascRect.x, ascRect.y, renderInfo.getText());
		}
	}

	public StringGrid getTable() {
		return grid;
	}

	public void beginTextBlock() {
	}

	public void endTextBlock() {
	}

	public void renderImage(ImageRenderInfo renderInfo) {
	}
}
