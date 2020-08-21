package d.factory.haeming.data;

import d.factory.haeming.exception.KollusException;

public class ContentItem {

    private ContentTypes contentType;
    private EncryptTypes encryptType;
    private String title;
    private String mediaContentKey;
    private String playUrl;

    public ContentItem(String title, ContentTypes contentType, EncryptTypes encrypttype, String mediaContentKey){
        this.title = title;
        this.contentType = contentType;
        this.encryptType = encrypttype;
        this.mediaContentKey = mediaContentKey;
    }
    public ContentItem(String title, ContentTypes contentType, EncryptTypes encrypttype, String mediaContentKey, String playUrl){
        this.title = title;
        this.contentType = contentType;
        this.encryptType = encrypttype;
        this.mediaContentKey = mediaContentKey;
        this.playUrl = playUrl;
    }

    public ContentTypes getContentType() {
        return contentType;
    }

    public void setContentType(ContentTypes contentType) {
        this.contentType = contentType;
    }

    public EncryptTypes getEncryptType() {
        return encryptType;
    }

    public void setEncryptType(EncryptTypes encrypttype) {
        this.encryptType = encrypttype;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMediaContentKey() {
        return mediaContentKey;
    }

    public void setMediaContentKey(String mediaContentKey) {
        this.mediaContentKey = mediaContentKey;
    }
    public String getPlayUrl() throws KollusException{
        if(this.contentType == null) {
            throw new KollusException("컨텐츠 종류 미지정");
        }
        if(this.mediaContentKey == null || this.mediaContentKey.isEmpty()){
            throw new KollusException("미디어컨텐츠키 미지정");
        }
        if(this.playUrl == null || this.playUrl.isEmpty()){
            switch (this.contentType){
                case AOD:
                case VOD:
                    return "https://v.kr.kollus.com/i/"+this.mediaContentKey;
                case LIVE:
                    return "https://v-live-kr.kollus.com/i/"+this.mediaContentKey;
            }
        }
        return playUrl;
    }
    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getPoster() throws KollusException {
        String posterUrl = null;
        if(this.contentType == null) {
            throw new KollusException("컨텐츠 종류 미지정");
        }
        if(this.mediaContentKey == null || this.mediaContentKey.isEmpty()){
            throw new KollusException("미디어컨텐츠키 미지정");
        }
        switch (this.contentType){
            case AOD:
            case VOD:
                posterUrl = "https://v.kr.kollus.com/poster/"+this.mediaContentKey;
                break;
            case LIVE:
                posterUrl = "https://v-live-kr.kollus.com/poster/"+this.mediaContentKey;
        }
        return posterUrl;
    }
}
