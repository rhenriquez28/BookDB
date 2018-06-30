package ds6.dpc.fisc.utp.bookdb.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import java.util.List;

@Dao
public interface BookDao {
    @Insert
    void insertBook(Books book);

    @Query("SELECT * FROM Books")
    List<Books> getAllBooks();

    @Query("SELECT * FROM Books WHERE ISBN = :isbn")
    List<Books> getBook(String isbn);

    @Query("SELECT * FROM Books")
    Cursor getAllCursors();

    @Update
    void updateBook(Books book);

    @Delete
    void deleteBook(Books book);
}
