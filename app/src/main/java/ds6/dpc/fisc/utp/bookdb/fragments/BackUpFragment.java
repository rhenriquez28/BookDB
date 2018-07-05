package ds6.dpc.fisc.utp.bookdb.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ds6.dpc.fisc.utp.bookdb.R;


public class BackUpFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_BACKUP = "backup";

    // TODO: Rename and change types of parameters
    private String backup;

    public BackUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BackUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BackUpFragment newInstance(String backup) {
        BackUpFragment fragment = new BackUpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BACKUP, backup);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            backup = getArguments().getString(ARG_BACKUP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_back_up, container, false);

        TextView seeBackupText = rootView.findViewById(R.id.seeBackupText);
        seeBackupText.setText(backup);

        return rootView;
    }
}
