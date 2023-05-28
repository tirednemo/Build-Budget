package com.example.buildbudget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.FaqVH> {

    private static final String TAG="FaqAdapter";
    List<faq> faqList;

    public FaqAdapter(List<faq> faqList) {
        this.faqList = faqList;
    }

    @NonNull
    @Override
    public FaqVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        return new FaqVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaqVH holder,int position)
    {
        faq faqs=faqList.get(position);
        holder.question.setText(faqs.getQuestion());
        holder.answer.setText(faqs.getAnswer());

        boolean isExpandable=faqList.get(position).isExpandable();
        holder.expandablelayout.setVisibility(isExpandable?View.VISIBLE:View.GONE);

    }

    @Override
    public int getItemCount()
    {
        return faqList.size();
    }

    public class FaqVH extends RecyclerView.ViewHolder{
        private static final String TAG="FaqVH";

        TextView question,answer;
        ConstraintLayout expandablelayout;

        public FaqVH(@NonNull View itemView) {
            super(itemView);

            question=itemView.findViewById(R.id.question);
            answer=itemView.findViewById(R.id.answer);
            expandablelayout=itemView.findViewById(R.id.expandableLayout);

            question.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    faq faqs=faqList.get(getBindingAdapterPosition());
                    faqs.setExpandable(!faqs.isExpandable());
                    notifyItemChanged(getBindingAdapterPosition());

                }
            });
        }
    }


}
