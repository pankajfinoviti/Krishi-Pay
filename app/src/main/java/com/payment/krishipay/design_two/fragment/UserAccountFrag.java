package com.payment.krishipay.design_two.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.payment.krishipay.R;
import com.payment.krishipay.dataModel.MainLocalData;
import com.payment.krishipay.model.ActivityListModel;
import com.payment.krishipay.utill.RecyclerTouchListener;
import com.payment.krishipay.views.BankAccountPage;
import com.payment.krishipay.views.ProfilePage;
import com.payment.krishipay.views.reports.adapter.VerticalReportListAdapter;

public class UserAccountFrag extends Fragment {
    private RecyclerView rvHome, optionList;
    private Activity context;

    public UserAccountFrag() {

    }

    private void init(View v) {
        optionList = v.findViewById(R.id.optionList);
        context = getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for context fragment
        View view = inflater.inflate(R.layout.all_profile_option_list_design_new, container, false);
        init(view);
        accountOptionList();
        return view;
    }

    private void accountOptionList() {
        LinearLayoutManager nearLayoutManager = new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false);
        optionList.setLayoutManager(nearLayoutManager);
        optionList.setItemAnimator(new DefaultItemAnimator());
        VerticalReportListAdapter homeAdapter = new VerticalReportListAdapter(context, MainLocalData.getProfilePageOptions());
        optionList.setAdapter(homeAdapter);
        optionList.addOnItemTouchListener(new RecyclerTouchListener(context,
                optionList, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent i;
                ActivityListModel model = MainLocalData.getMoneyTransferGrid().get(position);
                switch (position) {
                    case 0:
                        i = new Intent(context, ProfilePage.class);
                        startActivity(i);
                        break;
                    case 1:
                        i = new Intent(context, BankAccountPage.class);
                        startActivity(i);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

}
