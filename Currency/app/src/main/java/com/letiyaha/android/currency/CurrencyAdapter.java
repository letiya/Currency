package com.letiyaha.android.currency;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.letiyaha.android.currency.database.AppDatabase;
import com.letiyaha.android.currency.database.CurrencyEntry;
import com.letiyaha.android.currency.utilities.AppExecutors;
import com.letiyaha.android.currency.utilities.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Belle Lee on 7/15/2019.
 */

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder> {

    private final ListItemClickListener mOnClickListener;

    private Context mContext;

    private ArrayList<String> mAddedCurrencies;
    private ArrayList<String> mAddedCurrencyRates;

    private AppDatabase mDb;
    private Activity mActivity;

    public interface ListItemClickListener {
        void onListItemClick(String clickedCurrency);
    }

    public CurrencyAdapter(ListItemClickListener listener, Context context, Activity activity) {
        mOnClickListener = listener;
        mContext = context;
        mDb = AppDatabase.getInstance(mContext);
        mActivity = activity;
    }

    public void setFavoriteCurrencies() {
        AppExecutors.getInstance().DiskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<CurrencyEntry> currencyEntries = mDb.currencyDao().loadFavoriteCurrencies(Util.getToday());
                mAddedCurrencies = new ArrayList<>();
                mAddedCurrencyRates = new ArrayList<>();
                for (int i = 0; i < currencyEntries.size(); i++) {
                    CurrencyEntry currencyEntry = currencyEntries.get(i);
                    mAddedCurrencies.add(currencyEntry.getCurrency());
                    mAddedCurrencyRates.add(currencyEntry.getRate());
                }

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        });
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
        if (mAddedCurrencies == null || mAddedCurrencies.size() == 0) {
            return;
        }

        String majorCurrency = mAddedCurrencies.get(position);
        String majCurrVal;

        if (position == 0) {
            majCurrVal = mAddedCurrencyRates.get(position);
        } else {
            String minorCurrency = mAddedCurrencies.get(0);
            majCurrVal = mAddedCurrencyRates.get(position);

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
            String clickedCurrency = mAddedCurrencies.get(clickedPosition);
            mOnClickListener.onListItemClick(clickedCurrency);
        }
    }

}
