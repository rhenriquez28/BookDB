package ds6.dpc.fisc.utp.bookdb.fragments;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ds6.dpc.fisc.utp.bookdb.R;
import ds6.dpc.fisc.utp.bookdb.database.BookDatabase;
import ds6.dpc.fisc.utp.bookdb.database.Books;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_BOOK = "param1";

    // TODO: Rename and change types of parameters
    private String[] book;
    private String title, author, isbn, area, editorial;
    private int year = 0;
    private boolean isUpdate = true;

    private OnFragmentInteractionListener mListener;

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(String[] book) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putStringArray(ARG_BOOK, book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            book = getArguments().getStringArray(ARG_BOOK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView textTitle = rootView.findViewById(R.id.textTitle);
        TextView textAuthor = rootView.findViewById(R.id.textAuthor);
        TextView textIsbn = rootView.findViewById(R.id.textISBN);
        TextView textArea = rootView.findViewById(R.id.textArea);
        TextView textYear = rootView.findViewById(R.id.textYear);
        TextView textEditorial = rootView.findViewById(R.id.textEditorial);
        Button updateButton = rootView.findViewById(R.id.updateButton);
        Button deleteButton = rootView.findViewById(R.id.deleteButton);

        title = book[0];
        Log.d( "Titulo Detail", title);
        author = book[1];
        isbn = book[2];
        area = book[3];
        year = Integer.parseInt(book[4]);
        editorial = book[5];

        textTitle.setText(book[0]);
        textAuthor.setText(book[1]);
        textIsbn.setText(book[2]);
        textArea.setText(book[3]);
        textYear.setText(book[4]);
        textEditorial.setText(book[5]);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDetailUpdate(title, author, isbn, area, year, editorial, isUpdate);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final BookDatabase bookDatabase = Room.databaseBuilder(getActivity().getApplicationContext(),
                                                BookDatabase.class, "book_db")
                                                .build();
                                        List<Books> book = bookDatabase.bookDao().getBook(isbn);
                                        bookDatabase.bookDao().deleteBook(book.get(0));
                                        bookDatabase.close();
                                    }
                                }).start();
                                mListener.onDelete();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
                builder.setMessage(R.string.deleteDialog).setPositiveButton(R.string.deleteYes, dialogClickListener)
                        .setNegativeButton(R.string.deleteNo, dialogClickListener).show();
            }
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
        void onDetailUpdate(String title, String author, String isbn, String area, int year, String editorial, boolean isUpdate);
        void onDelete();
    }
}
