package ua.pp.oped.aromateque;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.transition.ChangeBounds;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ua.pp.oped.aromateque.model.EntityIdName;
import ua.pp.oped.aromateque.model.FilterParameter;
import ua.pp.oped.aromateque.model.FilterParameterValue;
import ua.pp.oped.aromateque.model.PriceFilterParameterValue;
import ua.pp.oped.aromateque.utility.AdvancedListenerRangeSeekBar;

import static android.widget.RelativeLayout.BELOW;
import static android.widget.RelativeLayout.RIGHT_OF;

public class FilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "FILTER_ADAPTER";
    private List<EntityIdName> filterAdapterList;
    private LayoutInflater layoutInflater;
    private RecyclerView recyclerView;
    private ArrayList<FilterParameterValue> activeFilterParameterValues;
    private List<List<View>> activeValueViewRows;
    private RelativeLayout activeValuesLayout;
    final static private int headerOffset = 1;
    private ParameterValueViewOnClickListener parameterValueViewOnClickListener;
    private Context context;

    public FilterAdapter(Context context, List<FilterParameter> filterParameters, RecyclerView recyclerView) {
        layoutInflater = LayoutInflater.from(context);
        this.recyclerView = recyclerView;
        filterAdapterList = new ArrayList<>();
        filterAdapterList.addAll(filterParameters);
        activeFilterParameterValues = new ArrayList<>();
        this.context = context;
        activeValueViewRows = new ArrayList<>();
        activeValueViewRows.add(new ArrayList<View>());
        parameterValueViewOnClickListener = new ParameterValueViewOnClickListener();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        View v = layoutInflater.inflate(itemType, viewGroup, false);
        if (itemType == R.layout.filter_parameter) {
            return new FilterParameterViewHolder(v);
        }
        if (itemType == R.layout.filter_parameter_value) {
            return new FilterParameterValueViewHolder(v);
        }
        if (itemType == R.layout.filter_parameter_price_value) {
            return new PriceViewHolder(v);
        }
        if (itemType == R.layout.active_filter_parameter_values_relative) {
            return new ActiveParameterValuesViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof FilterParameterViewHolder) {
            fillFilterParameterViewHolder((FilterParameterViewHolder) viewHolder, position);
        }
        if (viewHolder instanceof FilterParameterValueViewHolder) {
            fillFilterParameterValueViewHolder((FilterParameterValueViewHolder) viewHolder, position);
        }
        if (viewHolder instanceof PriceViewHolder) {
            fillPriceViewHolder((PriceViewHolder) viewHolder, position);
        }
        if (viewHolder instanceof ActiveParameterValuesViewHolder) {
            fillActiveParameterValuesViewHolder((ActiveParameterValuesViewHolder) viewHolder);
        }
        if (viewHolder instanceof PriceViewHolder) {
            //  Nothing to do yet
        }
    }

    private void fillFilterParameterViewHolder(final FilterParameterViewHolder viewHolder, int position) {
        final FilterParameter filterParameter = (FilterParameter) filterAdapterList.get(position - headerOffset);
        viewHolder.txtCategoryName.setText(filterParameter.getName());
        viewHolder.mainItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int positionInAdapter = viewHolder.getAdapterPosition();
                if (!viewHolder.isExtended) {
                    filterAdapterList.addAll(positionInAdapter + 1 - headerOffset, filterParameter.getValues());
                    FilterAdapter.this.notifyItemRangeInserted(positionInAdapter + 1, filterParameter.getValues().size());
                    ObjectAnimator.ofFloat(viewHolder.imgArrow, "rotation", 0, 180f)
                            .setDuration(400)
                            .start();
                    //Smooth scroll to last of just added child filterParameters
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.smoothScrollToPosition(positionInAdapter + filterParameter.getValues().size());
                        }
                    }, recyclerView.getItemAnimator().getRemoveDuration());
                    viewHolder.isExtended = true;
                } else {
                    filterAdapterList.subList(positionInAdapter + 1 - headerOffset, positionInAdapter + 1 - headerOffset + filterParameter.getValues().size()).clear();
                    FilterAdapter.this.notifyItemRangeRemoved(positionInAdapter + 1, filterParameter.getValues().size());
                    ObjectAnimator.ofFloat(viewHolder.imgArrow, "rotation", 180f, 0)
                            .setDuration(400)
                            .start();
                    viewHolder.isExtended = false;
                }
            }
        });
    }

    private void fillFilterParameterValueViewHolder(final FilterParameterValueViewHolder viewHolder, final int position) {
        final FilterParameterValue filterParameterValue = (FilterParameterValue) filterAdapterList.get(position - headerOffset);

        boolean isChecked = activeFilterParameterValues.contains(filterParameterValue);
        //Log.d(TAG, filterParameterValue.getName() + " exist in activeFilterParameterValues: " + isChecked);
        viewHolder.chkbxParameterValueName.setChecked(isChecked);
        viewHolder.chkbxParameterValueName.setText(filterParameterValue.getName());
        viewHolder.chkbxParameterValueName.setOnClickListener(parameterValueViewOnClickListener);
    }

    private void fillPriceViewHolder(final PriceViewHolder viewHolder, final int position) {
        final int minPrice = ((PriceFilterParameterValue) filterAdapterList.get(position - headerOffset)).getMinPrice();
        final int maxPrice = ((PriceFilterParameterValue) filterAdapterList.get(position - headerOffset)).getMaxPrice();
        viewHolder.etFrom.setText(String.valueOf(minPrice));
        viewHolder.etTo.setText(String.valueOf(maxPrice));
        viewHolder.priceRangeBar.setRangeValues(minPrice, maxPrice);
        viewHolder.priceRangeBar.setNotifyWhileDragging(true);
        final AdvancedListenerRangeSeekBar.OnRangeSeekBarChangeListener<Integer> onRangeSeekBarChangeListener = new AdvancedListenerRangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(AdvancedListenerRangeSeekBar<?> bar, Integer minValue, Integer maxValue, boolean isDragging) {
                viewHolder.etFrom.setText(String.valueOf(minValue));
                viewHolder.etTo.setText(String.valueOf(maxValue));
                if (!isDragging) {
                    for (int i = 0; i < activeFilterParameterValues.size(); i++) {
                        if (activeFilterParameterValues.get(i) instanceof PriceFilterParameterValue) {
                            int[] valueViewCoords = parameterValueViewOnClickListener.getViewPosition(i);
                            activeFilterParameterValues.remove(i);
                            parameterValueViewOnClickListener.removeValue(valueViewCoords);
                        }
                    }
                    int selectedFromPrice = viewHolder.priceRangeBar.getSelectedMinValue();
                    int selectedToPrice = viewHolder.priceRangeBar.getSelectedMaxValue();
                    PriceFilterParameterValue activePriceValue = new PriceFilterParameterValue(0, selectedFromPrice, selectedToPrice);
                    activePriceValue.setMinPrice(selectedFromPrice);
                    activePriceValue.setMaxPrice(selectedToPrice);
                    activeFilterParameterValues.add(activePriceValue);
                    parameterValueViewOnClickListener.addValueView(activePriceValue);
                }
            }
        };
        viewHolder.priceRangeBar.setOnRangeSeekBarChangeListener(onRangeSeekBarChangeListener);
        TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                String fromPrice;
                String toPrice;
                int enteredFromPrice = (int) Float.parseFloat(viewHolder.etFrom.getText().toString());
                int enteredToPrice = (int) Float.parseFloat(viewHolder.etTo.getText().toString());
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (enteredFromPrice < minPrice) {
                        enteredFromPrice = minPrice;
                    } else if (enteredFromPrice > maxPrice) {
                        enteredFromPrice = maxPrice;
                    }
                }
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (enteredToPrice > maxPrice) {
                        enteredToPrice = maxPrice;
                    } else if (enteredToPrice < minPrice) {
                        enteredToPrice = minPrice;
                    }
                    fromPrice = String.valueOf(enteredFromPrice);
                    toPrice = String.valueOf(enteredToPrice);
                    //Log.d(TAG, String.valueOf(selectedMaxPrice + " " + selectedMinPrice));
                    viewHolder.priceRangeBar.setSelectedMinValue(enteredFromPrice);
                    viewHolder.priceRangeBar.setSelectedMaxValue(enteredToPrice);
                    textView.clearFocus();
                    InputMethodManager inputManager =
                            (InputMethodManager) context.
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(
                            textView.getWindowToken(),
                            0);
                    viewHolder.etFrom.setText(fromPrice);
                    viewHolder.etTo.setText(toPrice);
                    onRangeSeekBarChangeListener.onRangeSeekBarValuesChanged(viewHolder.priceRangeBar, enteredFromPrice, enteredToPrice, false);
                    handled = true;
                }

                return handled;
            }
        };
        viewHolder.etFrom.setOnEditorActionListener(editorActionListener);
        viewHolder.etTo.setOnEditorActionListener(editorActionListener);
        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    ((EditText) view).setText("");
                }
            }
        };
        viewHolder.etFrom.setOnFocusChangeListener(onFocusChangeListener);
        viewHolder.etTo.setOnFocusChangeListener(onFocusChangeListener);

    }

    private void removePriceActiveValue() {
        for (FilterParameterValue value : activeFilterParameterValues) {
            if (value instanceof PriceFilterParameterValue) {
                //parameterValueViewOnClickListener.getViewPosition(p)
            }
        }
    }

    private void fillActiveParameterValuesViewHolder(final ActiveParameterValuesViewHolder viewHolder) {
        this.activeValuesLayout = viewHolder.activeValuesLayout;
    }

    @Override
    public int getItemCount() {
        return filterAdapterList.size() + headerOffset;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return R.layout.active_filter_parameter_values_relative;
        }
        if (filterAdapterList.get(position - headerOffset) instanceof FilterParameter) {
            return R.layout.filter_parameter;
        }
        if (filterAdapterList.get(position - headerOffset) instanceof FilterParameterValue) {
            //TODO rethink
            if (((FilterParameterValue) filterAdapterList.get(position - headerOffset)).getName().equals("price")) {
                return R.layout.filter_parameter_price_value;
            }
            return R.layout.filter_parameter_value;
        }

        return 0;
    }

    private class FilterParameterViewHolder extends RecyclerView.ViewHolder {
        TextView txtCategoryName;
        ImageView imgArrow;
        RelativeLayout mainItemLayout;
        boolean isExtended;

        FilterParameterViewHolder(View itemView) {
            super(itemView);
            txtCategoryName = (TextView) itemView.findViewById(R.id.txt_category);
            imgArrow = (ImageView) itemView.findViewById(R.id.category_list_arrow);
            mainItemLayout = (RelativeLayout) itemView.findViewById(R.id.category_mainitem_layout);
            isExtended = false;
        }
    }

    private class PriceViewHolder extends RecyclerView.ViewHolder {
        TextView from;
        TextView to;
        TextView uah;
        EditText etFrom;
        EditText etTo;
        AdvancedListenerRangeSeekBar<Integer> priceRangeBar;

        PriceViewHolder(View itemView) {
            super(itemView);
            from = (TextView) itemView.findViewById(R.id.txt_from);
            to = (TextView) itemView.findViewById(R.id.txt_to);
            uah = (TextView) itemView.findViewById(R.id.txt_uah);
            etTo = (EditText) itemView.findViewById(R.id.et_price_to);
            etFrom = (EditText) itemView.findViewById(R.id.et_price_from);
            priceRangeBar = (AdvancedListenerRangeSeekBar<Integer>) itemView.findViewById(R.id.price_range_bar);
        }
    }

    private class FilterParameterValueViewHolder extends RecyclerView.ViewHolder {
        CheckBox chkbxParameterValueName;

        FilterParameterValueViewHolder(View itemView) {
            super(itemView);
            chkbxParameterValueName = (CheckBox) itemView.findViewById(R.id.txt_category);
        }
    }

    private class ActiveParameterValuesViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout activeValuesLayout;

        ActiveParameterValuesViewHolder(View itemView) {
            super(itemView);
            activeValuesLayout = (RelativeLayout) itemView.findViewById(R.id.active_filter_parameter_values_layout);
        }
    }

    public class ActiveParametersChanged {
        public final ArrayList<FilterParameterValue> activeFilterParameters;

        public ActiveParametersChanged(ArrayList<FilterParameterValue> activeFilterParameters) {
            this.activeFilterParameters = activeFilterParameters;
        }
    }

    public class ActiveParameterChanged {
        public final FilterParameterValue parameterValue;
        public final int action;
        public static final int ADD = 713;
        public static final int DELETE = 653;

        public ActiveParameterChanged(FilterParameterValue parameterValue, int action) {
            this.parameterValue = parameterValue;
            this.action = action;

        }
    }

    private class ParameterValueViewOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            FilterParameterValueViewHolder viewHolder = (FilterParameterValueViewHolder) recyclerView.getChildViewHolder(view);
            int viewAdapterPosition = recyclerView.getChildAdapterPosition(view);
            FilterParameterValue pressedValue = (FilterParameterValue) filterAdapterList.get(viewAdapterPosition - headerOffset);
            //Add new active value view to the list
            // Check mark toggle occur before onClick so isChecked() is always like after the click
            if (viewHolder.chkbxParameterValueName.isChecked()) {
                Fade fadeTransition = new Fade();
                fadeTransition.setDuration(200);
                TransitionManager.beginDelayedTransition(activeValuesLayout, fadeTransition);
                activeFilterParameterValues.add(pressedValue);
                addValueView(pressedValue);
                // Remove active value from list
            } else {
                int pressedValuePosition = activeFilterParameterValues.indexOf(pressedValue);
                activeFilterParameterValues.remove(pressedValue);
                removeValue(getViewPosition(pressedValuePosition));
            }
            Log.d(TAG, activeValueViewRows.size() + " rows");
            for (List<View> row :
                    activeValueViewRows) {
                String s = "";
                for (View value : row) {
                    s += String.valueOf(((TextView) value.findViewById(R.id.active_value_name)).getText()) + " | ";
                }
                Log.d(TAG, s);
            }
        }

        private void removeValue(int[] pressedValueGridPosition) {
            TransitionSet transition = new TransitionSet();
            Fade fadeOutTransition = new Fade(Fade.OUT);
            fadeOutTransition.setDuration(200);
            transition.addTransition(fadeOutTransition);
            Fade fadeInTransition = new Fade(Fade.IN);
            fadeInTransition.setDuration(200);
            transition.addTransition(fadeInTransition);
            ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.setDuration(200);
            transition.addTransition(changeBounds);
            transition.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);
            TransitionManager.beginDelayedTransition(activeValuesLayout, transition);

            int pressedValueViewRow = pressedValueGridPosition[0];
            int pressedValueViewPosition = pressedValueGridPosition[1];

            List<View> activeValueViewRowList = activeValueViewRows.get(pressedValueViewRow);
            View activeValueView = activeValueViewRowList.get(pressedValueViewPosition);
            //Log.d(TAG, pressedValueViewRow + " row");
            //Log.d(TAG, pressedValueViewPosition + " position");
            // Let's rearrange all views next and below removed one
            ArrayList<View> viewsToRearrange = new ArrayList<>();
            // Collecting views that we will rearrange to convenient collection
            if (activeValueViewRowList.size() - 1 > pressedValueViewPosition) {
                viewsToRearrange.addAll(activeValueViewRowList.subList(pressedValueViewPosition + 1, activeValueViewRowList.size()));
            }
            for (int i = pressedValueViewRow + 1; i < activeValueViewRows.size(); i++) {
                viewsToRearrange.addAll(activeValueViewRows.get(i));
            }
            for (int i = activeValueViewRows.size() - 1; i > pressedValueViewRow; i--) {
                //Log.d(TAG, "Removed row: " + i);
                activeValueViewRows.remove(i);

            }
            activeValueViewRowList.subList(pressedValueViewPosition, activeValueViewRowList.size()).clear();
            if (activeValueViewRowList.size() - 1 == pressedValueViewPosition) {
                activeValueViewRowList.remove(pressedValueViewPosition);
            }
            if (activeValueViewRowList.isEmpty() && activeValueViewRows.size() > 1) {
                activeValueViewRows.remove(pressedValueViewRow);
            }
            activeValuesLayout.removeView(activeValueView);
            for (View valueView : viewsToRearrange) {
                activeValuesLayout.removeView(valueView);
            }
            for (View valueView : viewsToRearrange) {
                addValueView(valueView);
            }
        }


        private void addValueView(FilterParameterValue pressedValue) {
            View newActiveValueView = layoutInflater.inflate(R.layout.active_filter_value, activeValuesLayout, false);
            newActiveValueView.setId(View.generateViewId());
            TextView txtValueName = (TextView) newActiveValueView.findViewById(R.id.active_value_name);

            if (pressedValue instanceof PriceFilterParameterValue) {
                String text = ((PriceFilterParameterValue) pressedValue).getMinPrice() + " - " + ((PriceFilterParameterValue) pressedValue).getMaxPrice() + " грн";
                txtValueName.setText(text);
            } else {
                txtValueName.setText(pressedValue.getName());
            }
            ImageView cancelButton = (ImageView) newActiveValueView.findViewById(R.id.cancel_button);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // super bad code
                    // get parent of a button which is newActiveValueView
                    int[] coords = ((int[]) ((View) view.getParent()).getTag());
                    //Log.d(TAG, "coords =  " + coords[0] + "  " + coords[1]);
                    int positionInActiveValues = getPositionInActiveValues(coords);
                    FilterParameterValue value = activeFilterParameterValues.get(positionInActiveValues);
                    if (!(value instanceof PriceFilterParameterValue)) {
                        int positionInLayout = filterAdapterList.indexOf(value);
                        //Log.d(TAG, "filterAdapterList.indexOf(value) " + filterAdapterList.indexOf(value));
                        if (positionInLayout != -1) {
                            View viewInRecyclerView = recyclerView.getChildAt(positionInLayout + headerOffset);
                            if (viewInRecyclerView != null) {
                                FilterParameterValueViewHolder viewHolder = (FilterParameterValueViewHolder) recyclerView.getChildViewHolder(viewInRecyclerView);
                                viewHolder.chkbxParameterValueName.setChecked(false);
                            } else {
                                FilterAdapter.this.notifyItemChanged(positionInLayout + headerOffset);
                            }
                        }
                    }
                    activeFilterParameterValues.remove(positionInActiveValues);
                    removeValue(coords);
                }
            });
            addValueView(newActiveValueView);
        }

        private int getPositionInActiveValues(int[] coords) {
            int row = coords[0];
            int positionInRow = coords[1];
            int positionInActiveValues = 0;
            for (int i = 0; i < row; i++) {
                positionInActiveValues += activeValueViewRows.get(i).size();
            }
            positionInActiveValues += positionInRow;
            Log.d(TAG, "positionInActiveValues " + positionInActiveValues);
            return positionInActiveValues;
        }

        private void addValueView(View newActiveValueView) {
            Fade fadeTransition = new Fade();
            fadeTransition.setDuration(200);
            TransitionManager.beginDelayedTransition(activeValuesLayout, fadeTransition);

            int pressedValueViewRow;
            int pressedValueViewPosition = 0;
            clearRules(newActiveValueView);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) newActiveValueView.getLayoutParams();
            int lastRow = getLastRow();
            List<View> lastRowViews = activeValueViewRows.get(lastRow);
            if (lastRowViews.isEmpty()) {
                //layoutParams.addRule(ALIGN_PARENT_START);
                if (activeValueViewRows.size() > 1) {
                    // View above
                    View viewAbove = activeValueViewRows.get(lastRow - 1).get(0);
                    layoutParams.addRule(BELOW, viewAbove.getId());
                }
                lastRowViews.add(newActiveValueView);
            } else {
                int previousViewId = lastRowViews.get(lastRowViews.size() - 1).getId();
                // check if there is enough space to place new value view
                int predictedOccupiedWidth = 0;
                for (View activeValueView :
                        lastRowViews) {
                    int viewWidth;
                    if (activeValueView.getWidth() != 0) {
                        viewWidth = activeValueView.getWidth();
                        Log.d(TAG, "activeValueView.getWidth() " + activeValueView.getWidth());
                    } else {
                        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        activeValueView.measure(widthMeasureSpec, heightMeasureSpec);
                        viewWidth = activeValueView.getMeasuredWidth();
                        Log.d(TAG, "activeValueView.getMeasuredWidth() " + activeValueView.getMeasuredWidth());
                    }
                    predictedOccupiedWidth += viewWidth;

                }
                //wasn't layed out yet
                if (newActiveValueView.getWidth() == 0) {
                    int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    newActiveValueView.measure(widthMeasureSpec, heightMeasureSpec);
                    predictedOccupiedWidth += newActiveValueView.getMeasuredWidth();
                } else {
                    predictedOccupiedWidth += newActiveValueView.getWidth();
                }

                Log.d(TAG, "newActiveValueView.getMeasuredWidth(): " + newActiveValueView.getMeasuredWidth());
                // Enough space
                if (recyclerView.getWidth() > predictedOccupiedWidth) {
                    //Log.d(TAG, "recyclerView.getWidth(): " + recyclerView.getWidth() + ", predictedOccupiedWidth: " + predictedOccupiedWidth);
                    layoutParams.addRule(RIGHT_OF, previousViewId);
                    if (activeValueViewRows.size() > 1) {
                        // Same viewAbove as first
                        View viewAbove = activeValueViewRows.get(lastRow - 1).get(0);
                        layoutParams.addRule(BELOW, viewAbove.getId());
                    }
                    lastRowViews.add(newActiveValueView);
                    pressedValueViewPosition = lastRowViews.size() - 1;
                    // Not enough space
                } else {
                    ArrayList<View> newRow = new ArrayList<>();
                    activeValueViewRows.add(newRow);
                    newRow.add(newActiveValueView);
                    // calculate new lastRow
                    lastRow = getLastRow();
                    // Different viewAbove than other two
                    View viewAbove = activeValueViewRows.get(lastRow - 1).get(0);
                    layoutParams.addRule(BELOW, viewAbove.getId());
                }
            }
            activeValuesLayout.addView(newActiveValueView);
            //position already assigned
            pressedValueViewRow = lastRow;
            int[] position = {pressedValueViewRow, pressedValueViewPosition};
            newActiveValueView.setTag(position);
        }


        private void clearRules(View view) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
            int[] rules = layoutParams.getRules();
            for (int i = 0; i < rules.length; i++) {
                rules[i] = 0;
            }
        }

        private int getLastRow() {
            if (activeValueViewRows.size() > 0) {
                return activeValueViewRows.size() - 1;
            } else {
                return 0;
            }
        }

        private int[] getViewPosition(int pressedValuePosition) {
            int positionCounter = 0;
            int pressedValueViewRow = 0;
            int pressedValueViewPosition = 0;
            for (int i = 0; i < activeValueViewRows.size(); i++) {
                positionCounter += activeValueViewRows.get(i).size();
                if (positionCounter > pressedValuePosition) {
                    pressedValueViewRow = i;
                    //Log.d(TAG, String.valueOf(pressedValueViewRow));
                    positionCounter -= activeValueViewRows.get(i).size();
                    pressedValueViewPosition = pressedValuePosition - positionCounter;
                    break;
                }
            }
            int[] valuePosition = new int[2];
            valuePosition[0] = pressedValueViewRow;
            valuePosition[1] = pressedValueViewPosition;
            return valuePosition;
        }


    }

}