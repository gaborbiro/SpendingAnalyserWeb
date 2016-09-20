package com.gb.ofxanalyser.service.finance.parser.pdf.hsbc;

import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.awt.geom.Rectangle2D.Float;
import com.itextpdf.text.pdf.parser.TextMarginFinder;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

public class BoundedTextMarginFinder extends TextMarginFinder {

	private Rectangle2D.Float bounds;

	public BoundedTextMarginFinder(Float bounds) {
		this.bounds = bounds;
	}

	@Override
	public void renderText(TextRenderInfo renderInfo) {
		Rectangle2D.Float ascRect = renderInfo.getAscentLine().getBoundingRectange();
		Rectangle2D.Float descRect = renderInfo.getDescentLine().getBoundingRectange();

		if (ascRect.y >= bounds.y && ascRect.x >= bounds.x
				&& (descRect.y + descRect.height) <= (bounds.y + bounds.height)
				&& (descRect.x + descRect.width) <= (bounds.x + bounds.width)) {
			super.renderText(renderInfo);
		}
	}
}
