package com.letiyaha.android.currency;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Belle Lee on 7/15/2019.
 */

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder> {

    private static final String PREF_KEY_CURRENCIES = "key_currencies";
    private final ListItemClickListener mOnClickListener;

    private Currency mCurrencyData;

    private ArrayList<String> mAddedCurrencies;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    private Context mContext;

    public CurrencyAdapter(ListItemClickListener listener, Context context) {
        mOnClickListener = listener;
        mContext = context;
    }

    public void setCurrencyData(Currency currencyData) {
        mCurrencyData = currencyData;

        ArrayList<String> addedCurrencies = new ArrayList<String>();
        addedCurrencies.add(mCurrencyData.getBaseCurrency());

        ArrayList<String> preference = getSharedPreferences();
        if (preference != null) {
            preference.remove(mCurrencyData.getBaseCurrency());
            addedCurrencies.addAll(preference);
        }

        setFavoriteCurrencies(addedCurrencies);
        mCurrencyData.setFavoriteCurrencies(new HashSet<String>(addedCurrencies));
    }

    public void setFavoriteCurrencies(ArrayList<String> addedCurrencies) {
        mAddedCurrencies = addedCurrencies;
        notifyDataSetChanged();
    }

    public Currency getCurrencyData() {
        return mCurrencyData;
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

        String majorCurrency = mAddedCurrencies.get(position);
        String majCurrVal;
        if (position == 0) {
            majorCurrency = mCurrencyData.getBaseCurrency();
            majCurrVal = mCurrencyData.getRates().get(majorCurrency);
        } else {
            String minorCurrency = mCurrencyData.getBaseCurrency();
            majCurrVal = mCurrencyData.getRates().get(majorCurrency);
            float exchange = 1 / Float.parseFloat(majCurrVal);
            holder.mTvMinCurrencyVal.setText("1 " + majorCurrency + " = " + exchange + " " + minorCurrency);
        }
        holder.mTvCurrencyName.setText(majorCurrency);
        holder.mTvMajCurrencyVal.setText(majCurrVal);
    }

    @Override
    public int getItemCount() {
        if (mAddedCurrencies == null) {
            return 0;
        } else {
            return mAddedCurrencies.size();
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

    private ArrayList<String> getSharedPreferences() {
        ArrayList<String> addedCurrencies = null;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        Set<String> currentlyAddedCurrencies = sharedPreferences.getStringSet(PREF_KEY_CURRENCIES, null);
        if (currentlyAddedCurrencies != null) {
            addedCurrencies = new ArrayList<String>();
            for (String currency : currentlyAddedCurrencies) {
                addedCurrencies.add(currency);
            }
        }
        return addedCurrencies;
    }
}
