package hn.techcom.com.hncitipages.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hncitipages.Interfaces.OnLoadMoreListener;
import hn.techcom.com.hncitipages.Interfaces.OnReplyClickListener;
import hn.techcom.com.hncitipages.Models.Reply;
import hn.techcom.com.hncitipages.Models.ResultViewComments;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;

public class CommentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    //Constants
    private static final String TAG = "CommentListAdapter";
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<ResultViewComments> allComments = new ArrayList<>();
    private OnLoadMoreListener onLoadMoreListener;
    private OnReplyClickListener onReplyClickListener;

    public CommentListAdapter(RecyclerView recyclerView, ArrayList<ResultViewComments> allComments, Context context, OnReplyClickListener onReplyClickListener) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.allComments = allComments;
        this.onReplyClickListener = onReplyClickListener;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comments, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ResultViewComments comment = allComments.get(position);
        ((CommentViewHolder) holder).bind(comment);
    }

    @Override
    public int getItemCount() {
        return allComments == null ? 0 : allComments.size();
    }

    private class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public MaterialTextView name, location, title;
        public CircleImageView avatar, replyAvatar;
        private MaterialTextView commentPost;
        private RecyclerView repliesRecyclerview;
        private ReplyListAdapter replyListAdapter;
        private ImageButton replyButton, postReplyButton;
        private LinearLayout replyLayout;
        private EditText replyText;
        private Utils myUtils;

        public CommentViewHolder(@NonNull View view) {
            super(view);

            myUtils = new Utils();

            name           = view.findViewById(R.id.name_post);
            title          = view.findViewById(R.id.title_post);
            location       = view.findViewById(R.id.location_post);
            avatar         = view.findViewById(R.id.avatar_post);
            replyAvatar    = view.findViewById(R.id.avatar_post_reply);
            commentPost    = view.findViewById(R.id.comment_post);
            repliesRecyclerview = view.findViewById(R.id.recyclerview_posts_replies);
            replyButton    = view.findViewById(R.id.reply_button_comment);
            replyLayout    = view.findViewById(R.id.reply_layout);
            replyText      = view.findViewById(R.id.reply_editText);
            postReplyButton = view.findViewById(R.id.post_reply_button);

            replyButton.setOnClickListener(this);
            postReplyButton.setOnClickListener(this);
        }

        void bind(ResultViewComments comment){
            String address = comment.getUser().getCity() + ", " + comment.getUser().getCountry();
            String user_title = comment.getUser().getTitle();

            //setting up user name and location
            name.setText(comment.getUser().getFullName());
            if (!user_title.equals("User")){
                title.setVisibility(View.VISIBLE);
                location.setVisibility(View.GONE);

                String user_title_text = user_title + ", HN CitiPages";
                title.setText(user_title_text);
            }else {
                if (comment.getUser().getCity().equals("N/A") || comment.getUser().getCountry().equals("N/A"))
                    location.setVisibility(View.GONE);
                else {
                    location.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                    location.setText(address);
                }
            }

            //setting up user avatar
            String profilePhotoUrl = comment.getUser().getProfileImgThumbnail();
//            Picasso
//                    .get()
//                    .load(profilePhotoUrl)
//                    .into(avatar);

            Glide.with(context).load(profilePhotoUrl).centerCrop().into(avatar);

            Glide.with(context).load(myUtils.getNewUserFromSharedPreference(context).getProfileImgThumbnail()).centerCrop().into(replyAvatar);

            Log.d(TAG,"comment = "+comment.getComment());
            commentPost.setText(String.valueOf(comment.getComment()));

            if(comment.getReplies().size() > 0) {
                ArrayList<Reply> replyList = new ArrayList<>();
                replyList.addAll(comment.getReplies());
                setRecyclerView(replyList, repliesRecyclerview);
            }
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.reply_button_comment) {
                if(replyButton.getTag().equals("reply")) {
                    replyButton.setTag("cancel");
                    int position = getAbsoluteAdapterPosition();

                    replyLayout.setVisibility(View.VISIBLE);
                    replyButton.setImageResource(R.drawable.cancel_ic);
                }else{
                    replyButton.setTag("reply");
                    replyButton.setImageResource(R.drawable.reply_ic);
                    replyLayout.setVisibility(View.GONE);
                }
            }
            if(view.getId() == R.id.post_reply_button){
                int position = getAbsoluteAdapterPosition();
                int commentId = allComments.get(position).getId();
                if(!TextUtils.isEmpty(replyText.getText().toString()))
                    onReplyClickListener.onReplyClick(commentId,replyText.getText().toString(), position, replyLayout, replyButton);
                else
                    Toast.makeText(context,"Oops! You've forgot to enter your reply",Toast.LENGTH_LONG).show();
            }
        }

        public void setRecyclerView(ArrayList<Reply> replyList, RecyclerView repliesRecyclerview){
            repliesRecyclerview.setLayoutManager(new LinearLayoutManager(context));
            replyListAdapter = new ReplyListAdapter(context, this.repliesRecyclerview, replyList);
            repliesRecyclerview.setAdapter(replyListAdapter);
        }
    }
}
