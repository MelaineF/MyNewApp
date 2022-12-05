package fr.favennec.mynewapp.Model;

public class Post {

    //TODO : class mère de postVideo et postImage
    private String postid;
    private String post;
    private String description;
    private String publisher;
    private String hour;
    private String extension;
    private Boolean isPublic;

    public Post(String postid,String post, String description, String publisher, String hour, String extension,Boolean isPublic) {
        this.postid = postid;
        this.post = post;
        this.description = description;
        this.publisher = publisher;
        this.hour = hour;
        this.extension = extension;
        this.isPublic =isPublic;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Post() {

    }

}
