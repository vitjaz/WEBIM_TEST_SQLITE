package com.example.webim_sqlite;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    //слушатель нажатия на элемент recyclerview
    private Listener listener;

    //конструктор адаптера
    public NoteAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;
    }

    interface Listener{
        void onClick(int position, long id);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //объявлем контейнер для строк
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        if(!mCursor.moveToPosition(position)){
            return;
        }
        String title = mCursor.getString(mCursor.getColumnIndex("TITLE"));
        long id = mCursor.getLong(mCursor.getColumnIndex("_id"));                   //получаем идентификатор для удаления записи по свайпу
        String desc = mCursor.getString((mCursor.getColumnIndex("DESCRIPTION")));
        holder.titleText.setText(title);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onClick(position, id);
                }
            }
        });
        holder.itemView.setTag(id);                                             //отправляем индентификатор в MainActivity
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder{

        public TextView titleText;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.textview_title_item);
        }
    }

    public void swapCursor(Cursor newCursor){
        if(mCursor!=null){
            mCursor.close();
        }

        mCursor = newCursor;
        //обновляем recyclerview
        if(newCursor!=null){
            notifyDataSetChanged();
        }
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }
}
