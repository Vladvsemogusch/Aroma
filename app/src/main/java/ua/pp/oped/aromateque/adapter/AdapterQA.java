package ua.pp.oped.aromateque.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.pp.oped.aromateque.R;
import ua.pp.oped.aromateque.model.Category;
import ua.pp.oped.aromateque.model.InfoChildItem;
import ua.pp.oped.aromateque.model.InfoItem;
import ua.pp.oped.aromateque.model.InfoMainItem;
import ua.pp.oped.aromateque.utility.Utility;

public class AdapterQA extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_CHILD_ITEM = 3;
    private List<Category> categories;
    private Resources resources;
    private LayoutInflater layoutInflater;
    private Context context;
    private RecyclerView recyclerView;
    private Map<InfoMainItem, InfoChildItem> questionAnswerMap;
    private List<InfoItem> adapterList;

    public AdapterQA(Context context, RecyclerView recyclerView) {
        resources = context.getResources();
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.recyclerView = recyclerView;
        questionAnswerMap = new HashMap<>();
        adapterList = new ArrayList<>();
        InfoMainItem infoMainItem;
        InfoChildItem infoChildItem;
        String filePath;
        // we have 18 questions
        for (int i = 1; i <= 18; i++) {
            filePath = "info/q" + i + ".txt";
            infoMainItem = new InfoMainItem(Utility.readFromAssets(resources.getAssets(), filePath));
            filePath = "info/a" + i + ".txt";
            infoChildItem = new InfoChildItem(Utility.readFromAssets(resources.getAssets(), filePath));
            questionAnswerMap.put(infoMainItem, infoChildItem);
            adapterList.add(infoMainItem);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        View v;
        if (itemType == TYPE_ITEM) {
            v = layoutInflater.inflate(R.layout.q_a_list_item, viewGroup, false);
            return new MainItemViewHolder(v);
        }
        if (itemType == TYPE_CHILD_ITEM) {
            v = layoutInflater.inflate(R.layout.q_a_list_child_item, viewGroup, false);
            return new ChildItemViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof MainItemViewHolder) {
            final MainItemViewHolder mainItemViewHolder = (MainItemViewHolder) viewHolder;
            InfoMainItem currentInfoItem = (InfoMainItem) adapterList.get(position);
            if (adapterList.size() > position + 1 && adapterList.get(position + 1) instanceof InfoChildItem) {
                mainItemViewHolder.setExtended(true);
                mainItemViewHolder.imgArrow.setRotation(180);
            } else {
                mainItemViewHolder.setExtended(false);
                mainItemViewHolder.imgArrow.setRotation(0);
            }
            final String question = currentInfoItem.getText();
            final InfoItem answerInfoItem = questionAnswerMap.get(currentInfoItem);
            mainItemViewHolder.txtCategoryName.setText(question);
            mainItemViewHolder.mainItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int positionInAdapter = viewHolder.getAdapterPosition();
                    if (!mainItemViewHolder.isExtended()) {
                        adapterList.add(positionInAdapter + 1, answerInfoItem);
                        AdapterQA.this.notifyItemRangeInserted(positionInAdapter + 1, 1);
                        ObjectAnimator.ofFloat(mainItemViewHolder.imgArrow, "rotation", 0, -180f)
                                .setDuration(400)
                                .start();
                        //Smooth scroll to last of just added child categories
                        recyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.smoothScrollToPosition(positionInAdapter);
                            }
                        }, recyclerView.getItemAnimator().getRemoveDuration());
                        //recyclerView.smoothScrollToPosition(viewHolder.getAdapterPosition() + category.getChildrenIds().size());
                        mainItemViewHolder.setExtended(true);
                    } else {
                        adapterList.subList(positionInAdapter + 1, positionInAdapter + 2).clear();
                        AdapterQA.this.notifyItemRangeRemoved(positionInAdapter + 1, 1);
                        ObjectAnimator.ofFloat(mainItemViewHolder.imgArrow, "rotation", -180f, 0)
                                .setDuration(400)
                                .start();
                        mainItemViewHolder.setExtended(false);
                    }
                }
            });

        } else if (viewHolder instanceof ChildItemViewHolder) {
            InfoChildItem currentInfoItem = (InfoChildItem) adapterList.get(position);
            ((ChildItemViewHolder) viewHolder).txtCategoryName.setText(currentInfoItem.getText());
        }
    }


    @Override
    public int getItemCount() {
        return adapterList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (adapterList.get(position) instanceof InfoChildItem) {
            return TYPE_CHILD_ITEM;
        }
        return TYPE_ITEM;
    }

    private class MainItemViewHolder extends RecyclerView.ViewHolder {
        TextView txtCategoryName;
        ImageView imgArrow;
        RelativeLayout mainItemLayout;
        private boolean isExtended;

        MainItemViewHolder(View itemView) {
            super(itemView);
            txtCategoryName = (TextView) itemView.findViewById(R.id.txt_category);
            imgArrow = (ImageView) itemView.findViewById(R.id.category_list_arrow);
            mainItemLayout = (RelativeLayout) itemView.findViewById(R.id.category_mainitem_layout);
        }

        boolean isExtended() {
            return isExtended;
        }

        void setExtended(boolean extended) {
            isExtended = extended;
        }
    }

    private class ChildItemViewHolder extends RecyclerView.ViewHolder {
        TextView txtCategoryName;

        ChildItemViewHolder(View itemView) {
            super(itemView);
            txtCategoryName = (TextView) itemView.findViewById(R.id.txt_category);
        }
    }
}