package cenizadelacruzenriquez.project.finalproject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Book extends RealmObject {
    @PrimaryKey
    private String uuid;
    private String shelfUUID;
    private String bookTitle;
    private String author;
    private String notes;
    private String path;
    private float rating;


    public Book(){}

    public Book(String uuid, String shelfUUID, String bookTitle, String author, String notes, String path, float rating) {
        this.uuid = uuid;
        this.shelfUUID = shelfUUID;
        this.bookTitle = bookTitle;
        this.author = author;
        this.notes = notes;
        this.path = path;
        this.rating = rating;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getShelfUUID() {
        return shelfUUID;
    }

    public void setShelfUUID(String shelfUUID) {
        this.shelfUUID = shelfUUID;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Book{" +
                "uuid='" + uuid + '\'' +
                ", shelfUUID='" + shelfUUID + '\'' +
                ", bookTitle='" + bookTitle + '\'' +
                ", author='" + author + '\'' +
                ", notes='" + notes + '\'' +
                ", path='" + path + '\'' +
                ", rating=" + rating +
                '}';
    }
}
