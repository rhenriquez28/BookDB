package ds6.dpc.fisc.utp.bookdb.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "Books")
public class Books{

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "ISBN")
    private String isbn;

    @ColumnInfo(name = "Author")
    private String author;

    @ColumnInfo(name = "Title")
    private String title;

    @ColumnInfo(name = "Area")
    private String area;

    @ColumnInfo(name = "Year")
    private int year;

    @ColumnInfo(name = "Editorial")
    private String editorial;

    public Books(){

    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }
}
