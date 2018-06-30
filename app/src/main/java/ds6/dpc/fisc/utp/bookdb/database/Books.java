package ds6.dpc.fisc.utp.bookdb.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "Books")
public class Books implements Parcelable{

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


    protected Books(Parcel in) {
        isbn = in.readString();
        author = in.readString();
        title = in.readString();
        area = in.readString();
        year = in.readInt();
        editorial = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(isbn);
        dest.writeString(author);
        dest.writeString(title);
        dest.writeString(area);
        dest.writeInt(year);
        dest.writeString(editorial);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Books> CREATOR = new Creator<Books>() {
        @Override
        public Books createFromParcel(Parcel in) {
            return new Books(in);
        }

        @Override
        public Books[] newArray(int size) {
            return new Books[size];
        }
    };

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
