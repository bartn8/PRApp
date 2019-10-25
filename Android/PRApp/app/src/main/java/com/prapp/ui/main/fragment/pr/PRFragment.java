/*
 * PRApp  Copyright (C) 2019  Luca Bartolomei
 *
 * This file is part of PRApp.
 *
 *     PRApp is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     PRApp is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with PRApp.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.prapp.ui.main.fragment.pr;


import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.prapp.R;
import com.prapp.model.MyContext;
import com.prapp.ui.main.InterfaceHolder;
import com.prapp.ui.main.MainActivityInterface;
import com.prapp.ui.main.MainViewModel;
import com.prapp.ui.main.MyWebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PRFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PRFragment extends Fragment implements InterfaceHolder<MainActivityInterface> {

    private MainViewModel mainViewModel;
    private Unbinder unbinder;

    /**
     * Interfaccia usata per comunicare con l'activity madre.
     */
    private MainActivityInterface mainActivityInterface;

    @Override
    public void holdInterface(MainActivityInterface mainActivityInterface) {
        this.mainActivityInterface = mainActivityInterface;
    }

    @Override
    public boolean isInterfaceSet() {
        return this.mainActivityInterface != null;
    }

    @BindView(R.id.fragment_pr_webView)
    public WebView webView;

    public PRFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PRFragment.
     */
    public static PRFragment newInstance() {
        PRFragment fragment = new PRFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);    //Opzione menu
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    //ROBA MENU------------------------------------------------------------------------------

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.pr_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //--------------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pr, container, false);

        unbinder = ButterKnife.bind(this, view);

        mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        mainViewModel.acceptThirdPartyCookies(webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new MyWebViewClient(getContext()));

        startWebApp();

        return view;
    }


    private void startWebApp() {
        webView.loadUrl(getUri().toString());
    }

    private Uri getUri() {
        String stringUri = MyContext.DEFAULT_WEBAPP_ADDRESS;


        try {
            stringUri += "?token=" + URLEncoder.encode(mainViewModel.getToken(), "UTF-8");
            stringUri += "&idEvento=" + URLEncoder.encode(mainViewModel.getEvento().getId() + "", "UTF-8");
            stringUri += "&nomeEvento=" + URLEncoder.encode(mainViewModel.getEvento().getNome(), "UTF-8");
            stringUri += "&idStaff=" + URLEncoder.encode(mainViewModel.getStaff().getId() + "", "UTF-8");
            stringUri += "&nomeStaff=" + URLEncoder.encode(mainViewModel.getStaff().getNome(), "UTF-8");
        } catch (UnsupportedEncodingException e) {

        }

        Uri uri = Uri.parse(stringUri);

        return uri;
    }

}
