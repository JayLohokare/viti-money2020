package in.skylinelabs.money2020;

import android.media.Rating;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jaylo on 21-10-2018.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.PersonViewHolder>{

    CustomItemClickListener listener;

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardsloan, viewGroup, false);
        final PersonViewHolder pvh = new PersonViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, pvh.getPosition());
            }
        });
        return pvh;
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder{
        CardView cv;

        private String mItem;

        TextView personName;
        TextView amount;
        RatingBar rating;
        ImageView image;
        TextView endorsedBy;

        PersonViewHolder(View itemView) {
            super(itemView);

            cv = (CardView)itemView.findViewById(R.id.cv);

            personName = (TextView)itemView.findViewById(R.id.person_name);
            amount = (TextView)itemView.findViewById(R.id.person_age);
            rating = (RatingBar)itemView.findViewById(R.id.rating);
            endorsedBy= (TextView)itemView.findViewById(R.id.endoresedBY);
            image = (ImageView) itemView.findViewById(R.id.image);

        }


    }

    List<LendDetails> persons;

    MyAdapter(List<LendDetails> persons, CustomItemClickListener listener){
        this.persons = persons;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }




    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        personViewHolder.personName.setText(persons.get(i).get_name());
        personViewHolder.amount.setText(persons.get(i).get_amount());
        if (persons.get(i).get_endorsement() != "") {
            personViewHolder.endorsedBy.setText("\uD83D\uDC4D " + persons.get(i).get_endorsement());
        }
        else{
            personViewHolder.endorsedBy.setText("");
        }
        personViewHolder.rating.setRating(Float.parseFloat(persons.get(i).get_rating())/20);
        Picasso.get().load(persons.get(i).get_photo()).resize(500, 500).into(personViewHolder.image);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}