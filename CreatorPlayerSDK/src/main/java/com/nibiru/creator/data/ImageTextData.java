package com.nibiru.creator.data;

public class ImageTextData {
    private int id;
    private String imagePath;
    private String text;
    private int imageType;
    private int textMode;
    private float width;
    private float height;
    private int textColor;
    private float fontSize;
    private boolean bold;
    private boolean italic;
    private boolean underline;
    private float spacingMultiplier;
    private String textFont;

    public ImageTextData() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getImageType() {
        return imageType;
    }

    public void setImageType(int imageType) {
        this.imageType = imageType;
    }

    public int getTextMode() {
        return textMode;
    }

    public void setTextMode(int textMode) {
        this.textMode = textMode;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
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

    public String getTextFont() {
        return textFont;
    }

    public void setTextFont(String textFont) {
        this.textFont = textFont;
    }

    @Override
    public String toString() {
        return "ImageTextData{" +
                "id=" + id +
                ", imagePath='" + imagePath + '\'' +
                ", text='" + text + '\'' +
                ", imageType=" + imageType +
                ", textMode=" + textMode +
                ", width=" + width +
                ", height=" + height +
                ", textColor=" + textColor +
                ", fontSize=" + fontSize +
                ", bold=" + bold +
                ", italic=" + italic +
                ", underline=" + underline +
                ", spacingMultiplier=" + spacingMultiplier +
                ", textFont='" + textFont + '\'' +
                '}';
    }
}
