package com.digi.diary.interfaces;

import android.view.View;

/**
 * Created by anupama.sinha on 21-12-2016.
 */
public interface RecyclerClick_Listener {

    /**
     * Interface for Recycler View Click listener
     **/

    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
