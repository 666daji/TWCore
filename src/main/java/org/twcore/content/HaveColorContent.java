package org.twcore.content;

public class HaveColorContent extends Content{
    protected final int color;

    public HaveColorContent(String category, int color) {
        super(category);
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
