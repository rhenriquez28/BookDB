package ds6.dpc.fisc.utp.bookdb.fragments;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import ds6.dpc.fisc.utp.bookdb.R;
import ds6.dpc.fisc.utp.bookdb.database.BookDatabase;
import ds6.dpc.fisc.utp.bookdb.database.Books;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String title, author, isbn, area, editorial;
    private int year = 0;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        Button searchButton = rootView.findViewById(R.id.searchButton);
        final EditText isbnText = rootView.findViewById(R.id.searchISBN);
        searchButton.setOnClickListener(v -> {
            final String isbn1 = isbnText.getText().toString();
            new Thread(() -> {
                final BookDatabase bookDatabase = Room.databaseBuilder(getActivity().getApplicationContext(),
                        BookDatabase.class, "book_db")
                        .build();
                List<Books> books = bookDatabase.bookDao().getBook(isbn1);
                if (!books.isEmpty()) {
                    title = books.get(0).getTitle();
                    Log.d("Titulo", title);
                    author = books.get(0).getAuthor();
                    isbn = books.get(0).getIsbn();
                    area = books.get(0).getArea();
                    year = books.get(0).getYear();
                    editorial = books.get(0).getEditorial();
                    getActivity().runOnUiThread(
                            () -> mListener.onSearch(title, author, isbn, area, year, editorial)
                    );

                }
                else {
                    getActivity().runOnUiThread(
                            () -> {
                                Toast toast =
                                Toast.makeText(getContext(), "Book not Found!", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP | Gravity.CENTER, 0,0);
                                toast.show();
                            }
                    );
                }

                    bookDatabase.close();
                }).start();

        });
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
        void onSearch(String title, String author, String isbn, String area, int year, String editorial);
    }
}
