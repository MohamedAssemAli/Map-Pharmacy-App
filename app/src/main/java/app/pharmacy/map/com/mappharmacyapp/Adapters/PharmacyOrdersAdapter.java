package app.pharmacy.map.com.mappharmacyapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import app.pharmacy.map.com.mappharmacyapp.Activities.PharmacyOrderDetails;
import app.pharmacy.map.com.mappharmacyapp.App.AppConfig;
import app.pharmacy.map.com.mappharmacyapp.Models.Order;
import app.pharmacy.map.com.mappharmacyapp.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PharmacyOrdersAdapter extends RecyclerView.Adapter<PharmacyOrdersAdapter.OrderHolder> {

    private Context context;
    private ArrayList<Order> ordersArrayList;

    public PharmacyOrdersAdapter(Context context, ArrayList<Order> ordersArrayList) {
        this.context = context;
        this.ordersArrayList = ordersArrayList;
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View item = LayoutInflater.from(context).inflate(R.layout.item_order, viewGroup, false);
        return new OrderHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHolder orderHolder, int i) {
        final Order order = ordersArrayList.get(i);
        orderHolder.itemOrderUsername.setText(order.getUsername());
        orderHolder.itemOrderNumber.setText(context.getString(R.string.order_number) + " " + order.getIndex());
        if (order.getState() == 0) {
            orderHolder.itemOrderState.setText(context.getString(R.string.pending));
            orderHolder.itemOrderState.setBackgroundColor(ContextCompat.getColor(context, R.color.red));

        } else {
            orderHolder.itemOrderState.setText(context.getString(R.string.delivered));
            orderHolder.itemOrderState.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
        }

        orderHolder.itemOrderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToOrderDetails(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ordersArrayList.size();
    }

    class OrderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_order_username)
        TextView itemOrderUsername;
        @BindView(R.id.item_order_number)
        TextView itemOrderNumber;
        @BindView(R.id.item_order_state)
        TextView itemOrderState;
        @BindView(R.id.item_order_layout)
        LinearLayout itemOrderLayout;

        OrderHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void goToOrderDetails(Order order) {
        Intent intent = new Intent(context, PharmacyOrderDetails.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConfig.INTENT_KEY, order);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
