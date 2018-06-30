package ds6.dpc.fisc.utp.bookdb.fragments;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import ds6.dpc.fisc.utp.bookdb.R;
import ds6.dpc.fisc.utp.bookdb.database.BookDatabase;
import ds6.dpc.fisc.utp.bookdb.database.Books;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddFragment#newAddInstance} factory method to
 * create an instance of this fragment.
 * Use the {@link AddFragment#newUpdateInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TITLE = "titulo";
    private static final String ARG_AUTHOR = "autor";
    private static final String ARG_ISBN = "isbn";
    private static final String ARG_AREA = "area";
    private static final String ARG_YEAR = "año";
    private static final String ARG_EDITORIAL = "editorial";
    private static final String ARG_UPDATE = "siOno";

    // TODO: Rename and change types of parameters
    private String title, author, isbn, area, editorial;
    private int year = 0;
    private boolean isUpdate = false;

    private OnFragmentInteractionListener mListener;

    public AddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFragment newAddInstance() {
        AddFragment fragment = new AddFragment();
        return fragment;
    }

    public static AddFragment newUpdateInstance(String title, String author, String isbn,
                                                String area, int year, String editorial, boolean isUpdate){
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_AUTHOR, author);
        args.putString(ARG_ISBN, isbn);
        args.putString(ARG_AREA, area);
        args.putInt(ARG_YEAR, year);
        args.putString(ARG_EDITORIAL, editorial);
        args.putBoolean(ARG_UPDATE, isUpdate);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            author = getArguments().getString(ARG_AUTHOR);
            isbn = getArguments().getString(ARG_ISBN);
            area = getArguments().getString(ARG_AREA);
            year = getArguments().getInt(ARG_YEAR);
            editorial = getArguments().getString(ARG_EDITORIAL);
            isUpdate = getArguments().getBoolean(ARG_UPDATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_add, container, false);

        final EditText titleText = rootView.findViewById(R.id.addTitleText);
        final EditText authorText = rootView.findViewById(R.id.addAuthorText);
        final EditText isbnText = rootView.findViewById(R.id.addISBNText);
        final EditText yearText = rootView.findViewById(R.id.addYearText);
        final EditText editorialText = rootView.findViewById(R.id.addEditorialText);
        final Button addButton = rootView.findViewById(R.id.addButton);
        Spinner areaSpinner = rootView.findViewById(R.id.addAreaSpinner);
        String [] areaArray = getResources().getStringArray(R.array.area_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, areaArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areaSpinner.setAdapter(adapter);

        if (isUpdate){
            titleText.setText(title);
            authorText.setText(author);
            isbnText.setText(isbn);
            yearText.setText(Integer.toString(year));
            switch (area){
                case "Historia":
                    areaSpinner.setSelection(0);
                    break;
                case "Matematicas":
                    areaSpinner.setSelection(1);
                    break;
                case "Español":
                    areaSpinner.setSelection(2);
                    break;
                case "Ingles":
                    areaSpinner.setSelection(3);
                    break;
            }
            editorialText.setText(editorial);
            addButton.setText(R.string.saveChanges);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final BookDatabase bookDatabase = Room.databaseBuilder(getActivity().getApplicationContext(),
                                    BookDatabase.class, "book_db")
                                    .build();
                            title = titleText.getText().toString();
                            author = authorText.getText().toString();
                            isbn = isbnText.getText().toString();
                            if (yearText.getText().toString().equals("")){
                            }else {
                                year = Integer.parseInt(yearText.getText().toString());
                            }
                            editorial = editorialText.getText().toString();
                            if (
                                    (isbn.charAt(0) == 'H' && isbn.charAt(1) == 'S' && area.equals("Historia")) ||
                                    (isbn.charAt(0) == 'M' && isbn.charAt(1) == 'T' && area.equals("Matematicas")) ||
                                    (isbn.charAt(0) == 'E' && isbn.charAt(1) == 'S' && area.equals("Español")) ||
                                    (isbn.charAt(0) == 'I' && isbn.charAt(1) == 'N' && area.equals("Ingles"))
                                ){
                                Books book = new Books();
                                book.setTitle(title);
                                book.setAuthor(author);
                                book.setIsbn(isbn);
                                book.setArea(area);
                                book.setYear(year);
                                book.setEditorial(editorial);
                                bookDatabase.bookDao().updateBook(book);
                                bookDatabase.close();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mListener.onUpdate();
                                    }
                                });
                            }else {
                                final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which){
                                            case DialogInterface.BUTTON_POSITIVE:
                                                break;

                                            case DialogInterface.BUTTON_NEGATIVE:
                                                break;
                                        }
                                    }
                                };
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
                                        builder.setMessage(R.string.isbnDialog).setPositiveButton(R.string.isbnOK, dialogClickListener)
                                                .setNegativeButton(R.string.isbnCancel, dialogClickListener).show();
                                    }
                                });
                            }

                        }
                    }).start();

                }
            });
        }else {
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final BookDatabase bookDatabase = Room.databaseBuilder(getActivity().getApplicationContext(),
                                    BookDatabase.class, "book_db")
                                    .build();
                            title = titleText.getText().toString();
                            author = authorText.getText().toString();
                            isbn = isbnText.getText().toString();
                            if (yearText.getText().toString().equals("")){
                            }else {
                                year = Integer.parseInt(yearText.getText().toString());
                            }
                            editorial = editorialText.getText().toString();
                            if (
                                    (isbn.charAt(0) == 'H' && isbn.charAt(1) == 'S' && area.equals("Historia")) ||
                                            (isbn.charAt(0) == 'M' && isbn.charAt(1) == 'T' && area.equals("Matematicas")) ||
                                            (isbn.charAt(0) == 'E' && isbn.charAt(1) == 'S' && area.equals("Español")) ||
                                            (isbn.charAt(0) == 'I' && isbn.charAt(1) == 'N' && area.equals("Ingles"))
                                    ){
                                Books book = new Books();
                                book.setTitle(title);
                                book.setAuthor(author);
                                book.setIsbn(isbn);
                                book.setArea(area);
                                book.setYear(year);
                                book.setEditorial(editorial);
                                bookDatabase.bookDao().insertBook(book);
                                bookDatabase.close();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mListener.onAdd();
                                    }
                                });
                            }else {
                                final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which){
                                            case DialogInterface.BUTTON_POSITIVE:
                                                break;

                                            case DialogInterface.BUTTON_NEGATIVE:
                                                break;
                                        }
                                    }
                                };
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
                                        builder.setMessage(R.string.isbnDialog).setPositiveButton(R.string.isbnOK, dialogClickListener)
                                                .setNegativeButton(R.string.isbnCancel, dialogClickListener).show();
                                    }
                                    });
                                }
                        }
                    }).start();
                }
            });
        }

        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                area = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
        void onAdd();
        void onUpdate();
    }
}
