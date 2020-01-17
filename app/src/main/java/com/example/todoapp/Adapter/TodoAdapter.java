package com.example.todoapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.Model.Datum;
import com.example.todoapp.Model.Todo;
import com.example.todoapp.R;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    Context context;
    List<Datum> list;

    public TodoAdapter(Context context, List<Datum> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_todo_list, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "I am clicked "+list.get(viewHolder.getAdapterPosition()).getId()+"is the ID of the todo and "+list.get(viewHolder.getAdapterPosition()).getUserId(), Toast.LENGTH_LONG).show();
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Datum data = list.get(position);

        holder.tvTitle.setText(data.getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tv_todo_title);
            cardView = (CardView) itemView.findViewById(R.id.cardview_layout);
        }
    }

}
