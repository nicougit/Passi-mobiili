package fi.softala.tyokykypassi.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import fi.softala.tyokykypassi.R;
import fi.softala.tyokykypassi.adapters.GroupAdapter;
import fi.softala.tyokykypassi.models.Kayttaja;
import fi.softala.tyokykypassi.models.Ryhma;
import fi.softala.tyokykypassi.network.PassiClient;
import fi.softala.tyokykypassi.network.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Ryhmat extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar mProgressBar;
    private RecyclerView.Adapter adapter;
    private boolean palaute;
    private OnRyhmatFragmentInteractionListener mListener;
    private OnRyhmatLisaaInteractionListener mLisaaListener;
    private OnRyhmatMenePalautteeseenListener mMeneListener;

    public Ryhmat() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            palaute = getArguments().getBoolean("palaute");
            Log.d("DEBU", "oncreate palaute on " + palaute);
        }
        getRyhmat();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("jeejeejee", "palaute on createview on " + palaute);
        if (palaute) {
            Log.d("jeejeejee", "mentiin tänne");
            View v = inflater.inflate(R.layout.fragment_palaute_boksit, container, false);

            recyclerView = (RecyclerView) v.findViewById(R.id.palauteryhma_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            mProgressBar = (ProgressBar) v.findViewById(R.id.progressBarTest);
            return v;
        } else {
            View v = inflater.inflate(R.layout.fragment_ryhmat, container, false);

            recyclerView = (RecyclerView) v.findViewById(R.id.ryhma_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            mProgressBar = (ProgressBar) v.findViewById(R.id.include);
            TextView otsikko = (TextView) v.findViewById(R.id.fragment_otsikko);
            otsikko.setText("RYHMÄT");

            return v;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMeneListener = (OnRyhmatMenePalautteeseenListener) context;
        mLisaaListener = (OnRyhmatLisaaInteractionListener) context;
        mListener = (OnRyhmatFragmentInteractionListener) context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mLisaaListener = null;
        mMeneListener = null;
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
    public interface OnRyhmatFragmentInteractionListener {
        // TODO: Update argument type and name
        void onRyhmatFragmentInteraction(Ryhma uri);
    }

    public interface OnRyhmatLisaaInteractionListener {
        void onRyhmatLisaaFragmentInteraction();
    }

    public interface OnRyhmatMenePalautteeseenListener {
        void onRyhmatMenePalauteInteraction(int groupID);
    }

    public void getRyhmat() {
        SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("konfiguraatio", Context.MODE_PRIVATE);
        String base = mySharedPreferences.getString("token", null);
        String tunnus = mySharedPreferences.getString("tunnus", null);

        PassiClient service = ServiceGenerator.createService(PassiClient.class, base);

        Call<Kayttaja> call = service.haeKayttaja(tunnus);

        call.enqueue(new Callback<Kayttaja>() {
            @Override
            public void onResponse(Call<Kayttaja> call, Response<Kayttaja> response) {
                Log.d("jeejeejee", response.message());
                if (response.isSuccessful()) {
                    List<Ryhma> ryhmat = response.body().getRyhmat();
                    asetaData(ryhmat);
                } else {
                    Toast.makeText(getActivity(), "Haku epäonnistui koodi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Kayttaja> call, Throwable t) {
                Log.e("Passi", "Ryhmien haku epäonnistui " + t.toString());
            }
        });
    }

    public void asetaData(final List<Ryhma> ryhmat) {


        adapter = new GroupAdapter(
                ryhmat,
                R.layout.button_layout,
                new GroupAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Ryhma ryhma) {
                        if (palaute) {
                            mMeneListener.onRyhmatMenePalauteInteraction(ryhma.getGroupID());
                        } else {
                            mListener.onRyhmatFragmentInteraction(ryhma);
                        }
                    }
                },
                new GroupAdapter.OnClickListener() {
                    @Override
                    public void onClick() {
                        mLisaaListener.onRyhmatLisaaFragmentInteraction();
                    }
                }
        );

        recyclerView.setAdapter(adapter);
        mProgressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        Log.d("jeejeejee", "Kaik tehty");
    }


}
