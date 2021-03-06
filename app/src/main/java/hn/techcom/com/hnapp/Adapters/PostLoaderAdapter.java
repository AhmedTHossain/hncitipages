package hn.techcom.com.hnapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Handler;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.Models.Post;
import hn.techcom.com.hnapp.Models.SupporterProfile;
import hn.techcom.com.hnapp.R;
import hn.techcom.com.hnapp.Transitions.DepthPageTransformer;

public class PostLoaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static int TYPE_SUPPORTED_PROFILES = 1;
    private static int TYPE_POSTS = 2;

    private ArrayList<Post> postList, tempList;
    private ArrayList<SupporterProfile> userSupportedProfiles;
    private Context context;

    private static final String TAG = "PostLoaderAdapter";

    public PostLoaderAdapter(ArrayList<Post> postList, ArrayList<SupporterProfile> userSupportedProfiles, Context context) {
        this.postList = postList;
        this.tempList = postList;
        this.userSupportedProfiles = userSupportedProfiles;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        //for top item avatar list
        if (viewType == TYPE_SUPPORTED_PROFILES) {
            view = LayoutInflater.from(context).inflate(R.layout.row_first_post, parent, false);
            return new SupportedProfilesHolder(view);
        }
        //for all item posts
        else {
            view = LayoutInflater.from(context).inflate(R.layout.row_post, parent, false);
            return new PostsHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_SUPPORTED_PROFILES;
        else
            return TYPE_POSTS;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_SUPPORTED_PROFILES)
            ((SupportedProfilesHolder) holder).setRecyclerView(userSupportedProfiles);
        else
            ((PostsHolder) holder).setPostView(postList.get(position));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    //This method will filter the list
    //here we are passing the filtered data
    //and assigning it to the list with notifydatasetchanged method
    public void filterList(ArrayList<Post> filterdNames) {
        this.postList = filterdNames;
        notifyDataSetChanged();
    }

    //function to convert server UTC time to local
    public String utcToLocalTime(String utcTime) {
        Log.d(TAG, "time got from server = " + utcTime);
        SimpleDateFormat oldFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        oldFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = null;
        String dueDateAsNormal = "";
        try {
            value = oldFormatter.parse(utcTime);
            SimpleDateFormat newFormatter = new SimpleDateFormat("MM/dd/yyyy - hh:mm a", Locale.getDefault());

            newFormatter.setTimeZone(TimeZone.getDefault());
            dueDateAsNormal = newFormatter.format(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "time returned from all post adapter = " + dueDateAsNormal);
        return dueDateAsNormal;
    }

    class SupportedProfilesHolder extends RecyclerView.ViewHolder {
        private RecyclerView recyclerView;

        SupportedProfilesHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerview_supported_avatars_supportsection);
        }

        void setRecyclerView(ArrayList<SupporterProfile> userSupportedProfiles) {
            ArrayList<String> avatarList = new ArrayList<>();
            ArrayList<String> nameList = new ArrayList<>();
            for (SupporterProfile supportingProfile : userSupportedProfiles) {
                avatarList.add(supportingProfile.getProfileImgUrl());
                nameList.add(supportingProfile.getFullName());
            }
            Log.d(TAG, "avatar list size = " + avatarList.size());
            AvatarLoaderAdapter adapter = new AvatarLoaderAdapter(avatarList, nameList);
            LinearLayoutManager horizontalLayout = new LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
            );
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(horizontalLayout);
            recyclerView.setAdapter(adapter);
        }
    }

    class PostsHolder extends RecyclerView.ViewHolder {

        private CircleImageView userImage;
        private MaterialTextView userName, userLocation, postTime, postBody, postLikes, postComments;
        private ViewPager imageSliderView;
        private ImageButton prevImageButton, nextImageButton, likeImageButton, favoriteImageButton, followImageButton;
        private TabLayout indicatorLayout;
        private RelativeLayout imageSliderLayout;

        public PostsHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.circleimageview_postedBy_image);
            userName = itemView.findViewById(R.id.textview_postedby_name);
            userLocation = itemView.findViewById(R.id.textview_postedfrom_location);
            postTime = itemView.findViewById(R.id.textview_postedat_time);
            postBody = itemView.findViewById(R.id.textview_post_body);
            imageSliderView = itemView.findViewById(R.id.image_slider_post);
            indicatorLayout = itemView.findViewById(R.id.tab_indicator_image_slider_post);
            prevImageButton = itemView.findViewById(R.id.imagebutton_back_imageslider);
            nextImageButton = itemView.findViewById(R.id.imagebutton_next_imageslider);
            likeImageButton = itemView.findViewById(R.id.imagebutton_like_post);
            favoriteImageButton = itemView.findViewById(R.id.imagebutton_favorite_post);
            followImageButton = itemView.findViewById(R.id.imagebutton_follow_post);
            imageSliderLayout = itemView.findViewById(R.id.relativelayout_image_slider);
            postLikes = itemView.findViewById(R.id.textview_post_likes);
            postComments = itemView.findViewById(R.id.textview_post_comments);
        }

        void setPostView(Post post) {
            String fullname = post.getUser().getFirstName() + " " + post.getUser().getLastName();
            String location = post.getUser().getCity() + ", " + post.getUser().getCountry();

            String likeText = "";
            if (post.getLikeCount() > 1)
                likeText = post.getLikeCount() + " likes";
            else if (post.getLikeCount() == 0)
                postLikes.setVisibility(View.GONE);
            else
                likeText = post.getLikeCount() + " like";

            String commentText = "";
            if (post.getCommentCount() > 1)
                commentText = post.getCommentCount() + " comments";
            else if (post.getCommentCount() == 0)
                postComments.setVisibility(View.GONE);
            else
                commentText = post.getCommentCount() + " comment";

            if (post.getUser().getProfileImgUrl() != null) {
                Picasso.get()
                        .load("http://hn.techcomengine.com" + post.getUser().getProfileImgUrl())
                        .fit()
                        .centerInside()
                        .into(userImage);
            }else{
                userImage.setImageResource(0);
                userImage.setImageResource(R.drawable.dummy_profile_avatar);
            }
            userName.setText(fullname);
            userLocation.setText(location);
            postTime.setText(utcToLocalTime(post.getCreatedOn()));
            postBody.setText(post.getText());
            postLikes.setText(likeText);
            postComments.setText(commentText);

            if (post.getType().equals("I")) {
                imageSliderLayout.setVisibility(View.VISIBLE);

                ArrayList<String> imageList = new ArrayList<>();

                imageList.add(post.getImageUrl());
                imageList.add(post.getImageUrl());
                imageList.add(post.getImageUrl());

                ImageLoaderAdapter adapter = new ImageLoaderAdapter(context, imageList);
                imageSliderView.setAdapter(adapter);
                indicatorLayout.setupWithViewPager(imageSliderView, true);
                imageSliderView.setPageTransformer(true, new DepthPageTransformer());

                if (post.getText().equals(""))
                    postBody.setVisibility(View.GONE);

            } else
                imageSliderLayout.setVisibility(View.GONE);

            nextImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    nextImageButton.setImageResource(R.drawable.arrow_forward_selected_ic);
                    imageSliderView.setCurrentItem(imageSliderView.getCurrentItem() + 1);
                }
            });

            prevImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    prevImageButton.setImageResource(R.drawable.arrow_backward_selected_ic);

                    imageSliderView.setCurrentItem(imageSliderView.getCurrentItem() - 1);
                }
            });

            imageSliderView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    switch (position) {
                        case 0:
                            prevImageButton.setVisibility(View.INVISIBLE);

//                            nextImageButton.setImageResource(R.drawable.arrow_forward_ic);
                            break;
                        case 2:
                            nextImageButton.setVisibility(View.INVISIBLE);

//                            prevImageButton.setImageResource(R.drawable.arrow_backward_ic);
                            break;
                        default:
                            prevImageButton.setVisibility(View.VISIBLE);
                            nextImageButton.setVisibility(View.VISIBLE);

//                            prevImageButton.setImageResource(R.drawable.arrow_backward_ic);
//                            nextImageButton.setImageResource(R.drawable.arrow_forward_ic);
                    }
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            
            if(post.getSupport())
                followImageButton.setImageResource(R.drawable.support_icon_selected);

            if(post.getLiked())
                likeImageButton.setImageResource(R.drawable.like_ic_selected);

            if(post.getFavorite())
                favoriteImageButton.setImageResource(R.drawable.favorite_ic_selected);
        }
    }
}
