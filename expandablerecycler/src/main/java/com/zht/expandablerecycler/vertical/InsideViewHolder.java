package com.zht.expandablerecycler.vertical;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.zht.expandablerecycler.ChildViewHolder;
import com.zht.expandablerecycler.R;


public class InsideViewHolder extends ChildViewHolder {

    private TextView mIngredientTextView;

    public InsideViewHolder(@NonNull View itemView) {
        super(itemView);
        mIngredientTextView = (TextView) itemView.findViewById(R.id.inside_textview);
    }

    public void bind(@NonNull Inside inside) {
        mIngredientTextView.setText(inside.getName());
    }
}
