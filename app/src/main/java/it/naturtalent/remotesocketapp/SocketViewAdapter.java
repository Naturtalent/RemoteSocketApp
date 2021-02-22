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

import java.util.ArrayList;
import java.util.List;

import it.naturtalent.databinding.RemoteData;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class SocketViewAdapter extends RecyclerView.Adapter<SocketViewAdapter.CustomHolder>
{

    private static final String TAG = "SocketViewAdapter";

    private List<RemoteData> mDataSet = new ArrayList<>();

    // Index der moentan selektierten Zeile
    private int selectedPos = RecyclerView.NO_POSITION;

    private CustomHolder customHolder;

    /*
        Click Listener Funktionalitaet
     */
    private ClickInterface mClickInterface;

    public interface ClickInterface
    {
        void clickEventOne(Object obj);

        void clickEventTwo(Object obj1, Object obj2);
    }

    /**
     * Uebergibt ein Listenerinterface dessen Event-Funktion aufgerufen wird, wenn
     * ein Click in einer Listzeile (auf ein SocketData-Item) erfolgt
     * @param clickInterface
     */
    public void setClickInterface(ClickInterface clickInterface)
    {
        mClickInterface = clickInterface;
    }

    /**
        FirstListener reagiert auf Click in die erste Spalte der Zeile (Icon)
     */
    private class FirstClickListener implements View.OnClickListener
    {
        private int mPosition;
        private boolean mClickable;

        // Position kommt von 'onBindViewHolder()'
        void setPosition(int position)
        {
            mPosition = position;
        }

        // clickable - Funktion wird momentan nicht genutzt
        void setClickable(boolean clickable)
        {
            mClickable = clickable;
        }

        @Override
        public void onClick(View v)
        {
            if (mClickable)
            {
                // 'alte' Zeile deselektiern
                notifyItemChanged(selectedPos);

                android.util.Log.d("SocketViewAdapter", "check Position  alt: "+ selectedPos + "  neu: "+mPosition);

                /*

                if(mPosition >= mDataSet.size())
                    mPosition = mDataSet.size() - 1;
                 */

                // 'neue' Zeile selektieren
                selectedPos = mPosition;
                notifyItemChanged(selectedPos);

                // Listenerinterface aufrufen und Position uebergeben
                mClickInterface.clickEventOne(mDataSet.get(mPosition));
            }
        }
    }

   /**
       SecondListener reagiert auf Click in die zweiten Spalte der Zeile (SocketData)
   */
    private class SecondClickListener implements View.OnClickListener
    {
        private int mPosition;

        // Position kommt von 'onBindViewHolder()'
        void setPosition(int position)
        {
            mPosition = position;
        }

        @Override
        public void onClick(View v)
        {
            // 'alte' Zeile deselektiern
            notifyItemChanged(selectedPos);

            android.util.Log.d("SocketViewAdapter", "check Position  alt: "+ selectedPos + "  neu: "+mPosition);

            /*
            // 'neue' Zeile selektieren
            if(mPosition >= mDataSet.size())
                mPosition = mDataSet.size() - 1;

             */

            selectedPos = mPosition;
            notifyItemChanged(selectedPos);

            // Listenerinterface aufrufen und Position und View uebergeben
            mClickInterface.clickEventTwo(mDataSet.get(mPosition), v);
        }
    }

    /**
     * Konstruktion
     *
     * Initialize the dataset of the Adapter.
     */
    public SocketViewAdapter(List<RemoteData> remoteData)
    {
        mDataSet = remoteData;
    }

    public List<RemoteData> getmDataSet()
    {
        return mDataSet;
    }

    public void setmDataSet(List<RemoteData> mDataSet)
    {
        this.mDataSet = mDataSet;
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
    // Methode wird nicht aufgerufen, bei delete, add, change Items.
    @Override
    public void onBindViewHolder(CustomHolder customHolder, final int position)
    {
        android.util.Log.d(TAG, "Element " + position + " set."+"customHolder: "+customHolder);

        customHolder.itemView.setSelected(selectedPos == position);

        this.customHolder = customHolder;

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        customHolder.getTextView().setText(((RemoteData)mDataSet.get(position)).getName());

        // Position in die xxxClickListener eintragen
        customHolder.firstClickListener.setClickable(position % 2 == 0);
        customHolder.firstClickListener.setPosition(position);
        customHolder.secondClickListener.setPosition(position);
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return (mDataSet != null) ? mDataSet.size() : 0;
    }

    /*
         private ViewHolder - Klasse
     */
    protected class CustomHolder extends RecyclerView.ViewHolder
    {
        FirstClickListener firstClickListener;
        SecondClickListener secondClickListener;
        View v1, v2;

        public CustomHolder(View itemView)
        {
            super(itemView);

            //
            //  Listener der ersten Spalte (Icon)
            //
            firstClickListener = new FirstClickListener();

            // der Listener der ersten Spalte (Icon) zuordnen
            v1 = itemView.findViewById(R.id.icon);
            v1.setOnClickListener(firstClickListener);

            //
            //  Listener der zweiten Spalte (Text)
            //
            secondClickListener = new SecondClickListener();

            // den Listener der zweiten Spalte (Text) zuordnen
            v2 = itemView.findViewById(R.id.textView);
            v2.setOnClickListener(secondClickListener);
        }

        public TextView getTextView()
        {
            return (TextView) v2;
        }
    }

    public int getSelectedPos()
    {
        return selectedPos;
    }



    public int getAdapterPosition()
    {
        return customHolder.getAdapterPosition();
    }

    public CustomHolder getCustomHolder()
    {
        return customHolder;
    }

    /*
            @toDo
                Selektiert nicht wirklich, sondern dient momentan nur zum Zurueksetzen der 'selectedPos'
                nach erfolgter Loeschung des selektierten Eintrags @see SecondFragment (loeschen)
             */
    public void setSelectedPos(int selectedPos)
    {
        this.selectedPos = selectedPos;
    }
}
