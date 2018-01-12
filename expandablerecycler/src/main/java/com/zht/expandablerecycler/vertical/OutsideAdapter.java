package com.zht.expandablerecycler.vertical;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zht.expandablerecycler.ExpandableRecyclerAdapter;
import com.zht.expandablerecycler.R;

import java.util.List;

public class OutsideAdapter extends ExpandableRecyclerAdapter<Outside, Inside, OutsideViewHolder, InsideViewHolder> {

    private static final int PARENT_VEGETARIAN = 0;
    private static final int PARENT_NORMAL = 1;
    private static final int CHILD_VEGETARIAN = 2;
    private static final int CHILD_NORMAL = 3;

    private LayoutInflater mInflater;
    private List<Outside> mOutsideList;

    public OutsideAdapter(Context context, @NonNull List<Outside> outsideList) {
        super(outsideList);
        mOutsideList = outsideList;
        mInflater = LayoutInflater.from(context);
    }

    @UiThread
    @NonNull
    @Override
    public OutsideViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View recipeView;
        switch (viewType) {
            default:
            case PARENT_NORMAL:
                recipeView = mInflater.inflate(R.layout.outside_view, parentViewGroup, false);
                break;
            case PARENT_VEGETARIAN:
                recipeView = mInflater.inflate(R.layout.vegetarian_outside_view, parentViewGroup, false);
                break;
        }
        return new OutsideViewHolder(recipeView);
    }

    @UiThread
    @NonNull
    @Override
    public InsideViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View ingredientView;
        switch (viewType) {
            default:
            case CHILD_NORMAL:
                ingredientView = mInflater.inflate(R.layout.inside_view, childViewGroup, false);
                break;
            case CHILD_VEGETARIAN:
                ingredientView = mInflater.inflate(R.layout.vegetarian_inside_view, childViewGroup, false);
                break;
        }
        return new InsideViewHolder(ingredientView);
    }

    @UiThread
    @Override
    public void onBindParentViewHolder(@NonNull OutsideViewHolder outsideViewHolder, int parentPosition, @NonNull Outside outside) {
        outsideViewHolder.bind(outside);
    }

    @UiThread
    @Override
    public void onBindChildViewHolder(@NonNull InsideViewHolder insideViewHolder, int parentPosition, int childPosition, @NonNull Inside inside) {
        insideViewHolder.bind(inside);
    }

    @Override
    public int getParentViewType(int parentPosition) {
        if (mOutsideList.get(parentPosition).isVegetarian()) {
            return PARENT_VEGETARIAN;
        } else {
            return PARENT_NORMAL;
        }
    }

    @Override
    public int getChildViewType(int parentPosition, int childPosition) {
        Inside inside = mOutsideList.get(parentPosition).getIngredient(childPosition);
        if (inside.isVegetarian()) {
            return CHILD_VEGETARIAN;
        } else {
            return CHILD_NORMAL;
        }
    }

    @Override
    public boolean isParentViewType(int viewType) {
        return viewType == PARENT_VEGETARIAN || viewType == PARENT_NORMAL;
    }

}
