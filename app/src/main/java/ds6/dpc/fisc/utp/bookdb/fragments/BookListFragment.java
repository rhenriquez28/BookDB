package ds6.dpc.fisc.utp.bookdb.fragments;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ds6.dpc.fisc.utp.bookdb.R;
import ds6.dpc.fisc.utp.bookdb.database.BookDatabase;
import ds6.dpc.fisc.utp.bookdb.database.Books;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private ArrayList<String> booksIsbn;

    private OnFragmentInteractionListener mListener;

    public BookListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BookListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookListFragment newInstance() {
        BookListFragment fragment = new BookListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_book_list, container, false);

        final ListView bookList = rootView.findViewById(R.id.booksList);
        bookList.setOnItemClickListener((v,p,i,s) -> {
            String isbn = booksIsbn.get(i);
            new Thread(() ->{
                final BookDatabase bookDatabase = Room.databaseBuilder(getActivity().getApplicationContext(),
                        BookDatabase.class, "book_db")
                        .build();
                final List<Books> books = bookDatabase.bookDao().getBook(isbn);
                getActivity().runOnUiThread(() ->{
                    mListener.onBookSelection(books.get(0).getTitle(), books.get(0).getAuthor(),
                            isbn, books.get(0).getArea(), books.get(0).getYear(),
                            books.get(0).getEditorial());
                });
            }).start();
        } );
        new Thread(() -> {
            final BookDatabase bookDatabase = Room.databaseBuilder(getActivity().getApplicationContext(),
                    BookDatabase.class, "book_db")
                    .build();
            final List<Books> books = bookDatabase.bookDao().getAllBooks();
            final ArrayList<String> booksTitle = new ArrayList<>();
            booksIsbn = new ArrayList<>();
            for (int i=0; i<books.size(); i++){
                booksTitle.add(books.get(i).getTitle());
                booksIsbn.add(books.get(i).getIsbn());
            }
            getActivity().runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_list_item_1, booksTitle);
                adapter.notifyDataSetChanged();
                bookList.setAdapter(adapter);
            });
            bookDatabase.close();
        }).start();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onBookSelection(String title, String author, String isbn, String area, int year, String editorial);
    }
}
