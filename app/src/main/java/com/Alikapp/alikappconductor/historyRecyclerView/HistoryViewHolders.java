package com.Alikapp.alikappconductor.historyRecyclerView;

import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import com.Alikapp.alikappconductor.HistorySingleActivity;
import com.Alikapp.alikappconductor.R;

/**
 * Created by manel on 10/10/2017.
 */

public class HistoryViewHolders extends RecyclerView.ViewHolder implements android.view.View.OnClickListener{

    public android.widget.TextView rideId;
    public android.widget.TextView time;
    public HistoryViewHolders(android.view.View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        rideId = (android.widget.TextView) itemView.findViewById(R.id.rideId);
        time = (android.widget.TextView) itemView.findViewById(R.id.time);
    }


    @Override
    public void onClick(android.view.View v) {
        Intent intent = new Intent(v.getContext(), HistorySingleActivity.class);
        android.os.Bundle b = new android.os.Bundle();
        b.putString("rideId", rideId.getText().toString());
        intent.putExtras(b);
        v.getContext().startActivity(intent);
    }
}
