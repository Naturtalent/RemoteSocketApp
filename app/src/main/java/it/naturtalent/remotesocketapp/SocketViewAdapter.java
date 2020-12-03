/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.naturtalent.remotesocketapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.naturtalent.databinding.RemoteData;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class SocketViewAdapter extends RecyclerView.Adapter<SocketViewAdapter.CustomHolder>
{

    private static final String TAG = "SocketViewAdapter";

    private List<RemoteData> mDataSet;

    /*
        Click Listener Funktionalitaet
     */
    private ClickInterface mClickInterface;

    public interface ClickInterface
    {
        void clickEventOne(Object obj);

        void clickEventTwo(Object obj1, Object obj2);
    }

    public void setClickInterface(ClickInterface clickInterface)
    {
        mClickInterface = clickInterface;
    }


    private class FirstClickListener implements View.OnClickListener
    {
        private int mPosition;
        private boolean mClickable;

        void setPosition(int position)
        {
            mPosition = position;
        }

        void setClickable(boolean clickable)
        {
            mClickable = clickable;
        }

        @Override
        public void onClick(View v)
        {
            if (mClickable)
            {
                mClickInterface.clickEventOne(mDataSet.get(mPosition));
            }
        }
    }

    private class SecondClickListener implements View.OnClickListener
    {
        private int mPosition;

        void setPosition(int position)
        {
            mPosition = position;
        }

        @Override
        public void onClick(View v)
        {
            mClickInterface.clickEventTwo(mDataSet.get(mPosition), v);
        }
    }


    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    /*
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
            textView = (TextView) v.findViewById(R.id.textView);
        }

        public TextView getTextView() {
            return textView;
        }
    }

     */
    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     */
    public SocketViewAdapter(List<RemoteData> remoteData)
    {
        mDataSet = remoteData;
        /*
        mDataSet = null;

        List<String> listSocketNames = new ArrayList<>();
        if ((remoteData != null) && (!remoteData.isEmpty()))
        {
            for (RemoteData remote : remoteData)
                listSocketNames.add(remote.getName());
        }

        mDataSet = listSocketNames.toArray(new String[listSocketNames.size()]);
         */
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public CustomHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new CustomHolder(v);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CustomHolder customHolder, final int position)
    {
        android.util.Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        customHolder.getTextView().setText(((RemoteData)mDataSet.get(position)).getName());

        //make all even positions not clickable
        customHolder.firstClickListener.setClickable(position % 2 == 0);
        customHolder.firstClickListener.setPosition(position);
        customHolder.secondClickListener.setPosition(position);

    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return mDataSet.size();
    }

    /*
         provatge ViewHolder - Klasse
     */
    protected class CustomHolder extends RecyclerView.ViewHolder
    {
        FirstClickListener firstClickListener;
        SecondClickListener secondClickListener;
        View v1, v2;

        public CustomHolder(View itemView)
        {
            super(itemView);
            v1 = itemView.findViewById(R.id.icon);
            v2 = itemView.findViewById(R.id.textView);
            firstClickListener = new FirstClickListener();
            secondClickListener = new SecondClickListener();

            v1.setOnClickListener(firstClickListener);
            v2.setOnClickListener(secondClickListener);
        }

        public TextView getTextView()
        {
            return (TextView) v2;
        }
    }

}
