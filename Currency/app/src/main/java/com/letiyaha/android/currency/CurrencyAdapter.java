package com.letiyaha.android.currency;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Belle Lee on 7/15/2019.
 */

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder> {

    private final ListItemClickListener mOnClickListener;

    private Currency mCurrencyData;
    
    private String[] mCurrencies;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public CurrencyAdapter(ListItemClickListener listener) {
        mOnClickListener = listener;
    }

    public void setCurrencyData(Currency currencyData) {
        mCurrencyData = currencyData;
        String[] currency = {mCurrencyData.getBaseCurrency()};
        setFavoriteCurrencies(currency);
    }

    public void setFavoriteCurrencies(String[] currencies) {
        mCurrencies = currencies;
        notifyDataSetChanged();
    }

    @Override
    public CurrencyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.currency_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        CurrencyViewHolder currencyViewHolder = new CurrencyViewHolder(view);
        return currencyViewHolder;
    }

    @Override
    public void onBindViewHolder(CurrencyViewHolder holder, int position) {
        if (mCurrencyData == null) {
            return;
        }
        String majorCurrency = mCurrencyData.getBaseCurrency();
        String minorCurrency = mCurrencies[position];
        holder.mTvCurrencyName.setText(minorCurrency);
        if (position == 0) {
            holder.mTvMajCurrencyVal.setText("1.0000");
        } else {
            String minCurrVal = mCurrencyData.getRates().get(minorCurrency);
            holder.mTvMajCurrencyVal.setText(minCurrVal);
            float exchange = 1 / Float.parseFloat(minCurrVal);
            holder.mTvMinCurrencyVal.setText("1 " + minorCurrency + " = " + exchange + " " + majorCurrency);
        }
    }

    @Override
    public int getItemCount() {
        if (mCurrencies == null) {
            return 0;
        } else {
            return mCurrencies.length;
        }
    }

    public class CurrencyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTvCurrencyName;

        TextView mTvMajCurrencyVal;
        TextView mTvMinCurrencyVal;

        public CurrencyViewHolder(View itemView) {
            super(itemView);

            mTvCurrencyName = (TextView) itemView.findViewById(R.id.tv_currency_name);
            mTvMajCurrencyVal = (TextView) itemView.findViewById(R.id.tv_major_currency_val);
            mTvMinCurrencyVal = (TextView) itemView.findViewById(R.id.tv_minor_currency_val);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

}
