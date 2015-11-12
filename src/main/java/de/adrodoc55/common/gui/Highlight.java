package de.adrodoc55.common.gui;

import javax.swing.text.Highlighter.HighlightPainter;

public class Highlight {

    private int start;
    private int end;
    private HighlightPainter painter;

    public Highlight(int start, int end, HighlightPainter painter) {
        super();
        this.start = start;
        this.end = end;
        this.painter = painter;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public HighlightPainter getPainter() {
        return painter;
    }

    public void setPainter(HighlightPainter painter) {
        this.painter = painter;
    }

}
