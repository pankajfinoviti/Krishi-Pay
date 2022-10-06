package com.payment.krishipay.views.member.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.payment.krishipay.R;
import com.payment.krishipay.utill.ExtensionFunction;
import com.payment.krishipay.views.member.model.AppMember;
import com.payment.krishipay.views.reports.AepsWalletStatement;
import com.payment.krishipay.views.reports.WalletStatement;

import java.util.List;

public class AllMemberAdapter extends RecyclerView.Adapter<AllMemberAdapter.ViewHolder> {

    private Context context;
    private List<AppMember> activityListModels;
    private ItemClick clickLis;
    String selectedReport = "Aeps",id;

    public AllMemberAdapter(Context context, List<AppMember> activityListModels, ItemClick clickLis) {
        this.context = context;
        this.activityListModels = activityListModels;
        this.clickLis = clickLis;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_member_list_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        AppMember model = activityListModels.get(position);
        holder.tvId.setText(model.getId());
        holder.tvCity.setText(model.getCity());
        holder.tvEmail.setText(model.getEmail());
        holder.tvMobile.setText(model.getMobile());
        holder.tvName.setText(model.getName());
        holder.tvAepsBalance.setText("₹ "+model.getAepsbalance());
        holder.tvMainBalance.setText("₹ "+model.getMainwallet());
        holder.tvParent.setText(model.getParents().replaceAll("<br>", "  "));
/*

        holder.btnAepsReport.setOnClickListener(v -> {
            Intent i = new Intent(context, AEPSTransaction.class);
            i.putExtra("title", "AEPS Wallet Statement");
            i.putExtra("type", "awalletstatement");
            i.putExtra("agent", model.getId());
            context.startActivity(i);
        });

        holder.btnWalletReport.setOnClickListener(v -> {
            Intent i = new Intent(context, MainWalletFundReqStatement.class);
            i.putExtra("title", "Main Wallet Statement");
            i.putExtra("type", "fundrequest");
            i.putExtra("agent", model.getId());
            context.startActivity(i);
        });

         holder.btnMatmReport.setOnClickListener(v -> {
            Intent i = new Intent(context, MainWalletFundReqStatement.class);
            i.putExtra("title", "Matm Statement");
            i.putExtra("type", "matmwalletstatement");
            i.putExtra("agent", model.getId());
            context.startActivity(i);
        });
*/

         holder.viewReport.setOnClickListener(v -> {
             model.getId();
             showReportDialog(id);
         });

        holder.imgRec.setOnClickListener(v -> clickLis.clickRec(model));
        holder.imgSend.setOnClickListener(v -> clickLis.clickSend(model));
    }

    private void showReportDialog(String agentId) {
        selectedReport = "Aeps";

        String[] report =  {"Aeps","Wallet"};
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Select Report");
        alertDialog.setSingleChoiceItems(report,0 , (dialog, which) -> {
             selectedReport = report[which];
//            ExtensionFunction.showToast(context,selectedReport);
        });

        alertDialog.setPositiveButton("Proceed", (dialog, which) -> {
            if (selectedReport!= null){

            if (selectedReport.equalsIgnoreCase("Aeps")){
                Intent intent = new Intent(context, AepsWalletStatement.class);
                intent.putExtra("type", "awalletstatement");
                intent.putExtra("title", "Aeps Wallet");
                intent.putExtra("agent", agentId);
                context.startActivity(intent);
            }else if (selectedReport.equalsIgnoreCase("Wallet")){
                Intent intent = new Intent(context, WalletStatement.class);
                intent.putExtra("type", "accountstatement");
                intent.putExtra("title", "Main Wallet");
                context.startActivity(intent);
            }else if (selectedReport.equalsIgnoreCase("Matm")){
                Intent intent = new Intent(context, AepsWalletStatement.class);
                intent.putExtra("type", "matmwalletstatement");
                intent.putExtra("title", "MATM Wallet");
                intent.putExtra("agent", agentId);
                context.startActivity(intent);
            }

            }else {
                ExtensionFunction.showToast(context,"Select the report");
            }
        });


        alertDialog.setNegativeButton("Cancle", (dialog, which) -> dialog.dismiss());

        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return activityListModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvMobile, tvCity, tvParent,tvAepsBalance,tvMainBalance,tvId,viewReport;
        ImageView imgRec,  imgSend;
        Button btnAepsReport,btnWalletReport,btnMatmReport;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvParent = itemView.findViewById(R.id.tvParent);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvMobile = itemView.findViewById(R.id.tvMobile);
            tvCity = itemView.findViewById(R.id.tvCity);
            imgRec = itemView.findViewById(R.id.btnRec);
            imgSend = itemView.findViewById(R.id.imgSend);
//            btnAepsReport = itemView.findViewById(R.id.btnAepsReport);
//            btnWalletReport = itemView.findViewById(R.id.btnWalletReport);
            tvAepsBalance = itemView.findViewById(R.id.tvAepsBalance);
            tvMainBalance = itemView.findViewById(R.id.tvMainBalance);
//            btnMatmReport = itemView.findViewById(R.id.btnMatmReport);
            tvId = itemView.findViewById(R.id.tvId);
            viewReport = itemView.findViewById(R.id.viewReport);
        }
    }

    public interface ItemClick {
        void clickRec(AppMember model);

        void clickSend(AppMember model);
    }
}
