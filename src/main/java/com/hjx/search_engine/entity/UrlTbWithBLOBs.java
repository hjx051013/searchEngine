import com.hjx.search_engine.entity.UrlTb;

public class UrlTbWithBLOBs extends UrlTb {
    private String content;

    private String pointing;

    private String text;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getPointing() {
        return pointing;
    }

    public void setPointing(String pointing) {
        this.pointing = pointing == null ? null : pointing.trim();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text == null ? null : text.trim();
    }
}