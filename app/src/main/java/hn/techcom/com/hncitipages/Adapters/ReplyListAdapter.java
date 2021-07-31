package hn.techcom.com.hncitipages.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hncitipages.Models.Reply;
import hn.techcom.com.hncitipages.R;

public class ReplyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    //Constants
    private static final String TAG = "ReplyListAdapter";
    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<Reply> allReplies = new ArrayList<>();

    public ReplyListAdapter(Context context, RecyclerView recyclerView, ArrayList<Reply> allReplies) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.allReplies = allReplies;

        Log.d(TAG, "ReplyListAdapter called = "+"YES");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_replies, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Reply reply = allReplies.get(position);
        ((ReplyViewHolder) holder).bind(reply);
    }

    @Override
    public int getItemCount() {
        return allReplies == null ? 0 : allReplies.size();
    }

    private class ReplyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public MaterialTextView name, location;
        public CircleImageView avatar;
        private MaterialTextView replyPost;

        public ReplyViewHolder(@NonNull View view) {
            super(view);

            name           = view.findViewById(R.id.name_post);
            location       = view.findViewById(R.id.location_post);
            avatar         = view.findViewById(R.id.avatar_post);
            replyPost      = view.findViewById(R.id.reply_post);
        }

        void bind(Reply reply){
            String address = reply.getUser().getCity() + ", " + reply.getUser().getCountry();

            //setting up user name and location
            name.setText(reply.getUser().getFullName());
            location.setText(address);

            //setting up user avatar
            String profilePhotoUrl = reply.getUser().getProfileImgThumbnail();
//            Picasso
//                    .get()
//                    .load(profilePhotoUrl)
//                    .into(avatar);

            Glide.with(context).load(profilePhotoUrl).centerCrop().into(avatar);

            replyPost.setText(String.valueOf(reply.getComment()));
        }

        @Override
        public void onClick(View view) {

        }
    }
}