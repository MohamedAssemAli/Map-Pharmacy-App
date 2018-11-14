package app.pharmacy.map.com.mappharmacyapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import app.pharmacy.map.com.mappharmacyapp.Activities.UserMakeOrderActivity;
import app.pharmacy.map.com.mappharmacyapp.App.AppConfig;
import app.pharmacy.map.com.mappharmacyapp.Models.Pharmacy;
import app.pharmacy.map.com.mappharmacyapp.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PharmacyAdapter extends RecyclerView.Adapter<PharmacyAdapter.PharmacyHolder> {

    private Context context;
    private ArrayList<Pharmacy> pharmacyArrayList;

    public PharmacyAdapter(Context context, ArrayList<Pharmacy> pharmacyArrayList) {
        this.context = context;
        this.pharmacyArrayList = pharmacyArrayList;
    }

    @NonNull
    @Override
    public PharmacyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View item = LayoutInflater.from(context).inflate(R.layout.item_order, viewGroup, false);
        return new PharmacyHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull PharmacyHolder pharmacyHolder, int i) {
        final Pharmacy pharmacy = pharmacyArrayList.get(i);
        pharmacyHolder.itemPharmacyUsername.setText(pharmacy.getUsername());
        pharmacyHolder.itemPharmacyMakeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMakeOrder(pharmacy);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pharmacyArrayList.size();
    }


    class PharmacyHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_pharmacy_username)
        TextView itemPharmacyUsername;
        @BindView(R.id.item_pharmacy_make_order)
        TextView itemPharmacyMakeOrder;

        PharmacyHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void goToMakeOrder(Pharmacy pharmacy) {
        Intent intent = new Intent(context, UserMakeOrderActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConfig.INTENT_KEY, pharmacy);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
