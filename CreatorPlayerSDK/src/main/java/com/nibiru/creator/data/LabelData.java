package com.nibiru.creator.data;

public class LabelData {
    private int id;
    private float bgWidth;
    private float bgHeight;
    private int textColor;
    private int bgColor;
    private float fontSize;
    private String title;
    private boolean bold;
    private boolean italic;
    private boolean underline;
    private float spacingMultiplier;
    private int alignment;
    private String textFont;

    public LabelData() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getBgWidth() {
        return bgWidth;
    }

    public void setBgWidth(float bgWidth) {
        this.bgWidth = bgWidth;
    }

    public float getBgHeight() {
        return bgHeight;
    }

    public void setBgHeight(float bgHeight) {
        this.bgHeight = bgHeight;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public boolean isUnderline() {
        return underline;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
    }

    public float getSpacingMultiplier() {
        return spacingMultiplier;
    }

    public void setSpacingMultiplier(float spacingMultiplier) {
        this.spacingMultiplier = spacingMultiplier;
    }

    public int getAlignment() {
        return alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public String getTextFont() {
        return textFont;
    }

    public void setTextFont(String textFont) {
        this.textFont = textFont;
    }

    @Override
    public String toString() {
        return "LabelData{" +
                "id=" + id +
                ", bgWidth=" + bgWidth +
                ", bgHeight=" + bgHeight +
                ", textColor=" + textColor +
                ", bgColor=" + bgColor +
                ", fontSize=" + fontSize +
                ", title='" + title + '\'' +
                ", bold=" + bold +
                ", italic=" + italic +
                ", underline=" + underline +
                ", spacingMultiplier=" + spacingMultiplier +
                ", alignment=" + alignment +
                ", textFont='" + textFont + '\'' +
                '}';
    }
}
