public class Question {
    private String title;
    private String text;
    private String labelLow;
    private String labelHigh;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setLabelLow(String labelLow) {
        this.labelLow = labelLow;
    }

    public void setLabelHigh(String labelHigh) {
        this.labelHigh = labelHigh;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getLabelLow() {
        return labelLow;
    }

    public String getLabelHigh() {
        return labelHigh;
    }

    @Override
    public String toString() {
        return "Question{" +
                "title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", labelLow='" + labelLow + '\'' +
                ", labelHigh='" + labelHigh + '\'' +
                '}';
    }
}
