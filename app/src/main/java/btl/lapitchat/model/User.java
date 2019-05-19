package btl.lapitchat.model;


public class User {
    private String email;
    private String name;
    private String image;
    private String status;
    private String thumbnail;


    public User(){
    }

    public User(String name, String avatar, String status, String thumbnail) {
        this.name = name;
        this.image = avatar;
        this.status = status;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String avatar) {
        this.image = avatar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
