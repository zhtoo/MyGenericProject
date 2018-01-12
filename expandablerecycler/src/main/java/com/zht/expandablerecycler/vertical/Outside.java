package com.zht.expandablerecycler.vertical;


import com.zht.expandablerecycler.model.Parent;

import java.util.List;

public class Outside implements Parent<Inside> {

    private String mName;
    private List<Inside> mInsides;

    public Outside(String name, List<Inside> insides) {
        mName = name;
        mInsides = insides;
    }

    public String getName() {
        return mName;
    }

    @Override
    public List<Inside> getChildList() {
        return mInsides;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public Inside getIngredient(int position) {
        return mInsides.get(position);
    }

    public boolean isVegetarian() {
        for (Inside inside : mInsides) {
            if (!inside.isVegetarian()) {
                return false;
            }
        }
        return true;
    }
}
