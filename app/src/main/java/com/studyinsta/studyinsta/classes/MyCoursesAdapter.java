package com.studyinsta.studyinsta.classes;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.studyinsta.studyinsta.CourseSubjectsActivity;
import com.studyinsta.studyinsta.DeliveryActivity;
import com.studyinsta.studyinsta.MainActivity;
import com.studyinsta.studyinsta.NotesDisplayActivity;
import com.studyinsta.studyinsta.R;
import com.studyinsta.studyinsta.TestSetsActivity;

import java.util.List;
import java.util.UUID;

public class MyCoursesAdapter extends RecyclerView.Adapter<MyCoursesAdapter.ViewHolder> {

    private List<MyCoursesModel> myCoursesModelList;
    private List<String> downloadUrlList;

    public MyCoursesAdapter(List<MyCoursesModel> myCoursesModelList) {
        this.myCoursesModelList = myCoursesModelList;
    }



    public MyCoursesAdapter(List<MyCoursesModel> myCoursesModelList, List<String> url) {
        this.myCoursesModelList = myCoursesModelList;
        this.downloadUrlList = url;
    }



    @NonNull
    @Override
    public MyCoursesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchased_product_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCoursesAdapter.ViewHolder viewHolder, int position) {

        String icon = myCoursesModelList.get(position).getResource();
        String productId = myCoursesModelList.get(position).getProductId();
        String productTitle = myCoursesModelList.get(position).getProductTitle();
        String productDescription = myCoursesModelList.get(position).getProductDescription();

        viewHolder.setData(productTitle, productDescription, icon, productId);
    }

    @Override
    public int getItemCount() {
        return myCoursesModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView productTitle;
        private TextView productDescription;
        private ImageView courseImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productTitle = itemView.findViewById(R.id.product_title);
            productDescription = itemView.findViewById(R.id.product_description);
            courseImage = itemView.findViewById(R.id.product_Image);
        }

        private void setData(final String titleName, String descName, String imageLink, final String productId) {
            productTitle.setText(titleName);
            productDescription.setText(descName);
            Glide.with(itemView.getContext()).load(imageLink).transition(GenericTransitionOptions.with(R.anim.fade_in)).apply(new RequestOptions().placeholder(R.drawable.placeholder_image)).into(courseImage);

            if (!titleName.equals("")) {
                if (MainActivity.currentFragment == 2) {
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // todo : Send to Purchased Courses Activity and send the product id via intent
                            Intent purchasedCoursesIntent = new Intent(itemView.getContext(), CourseSubjectsActivity.class);
                            purchasedCoursesIntent.putExtra("product_ID", productId);
                            purchasedCoursesIntent.putExtra("product_TITLE", titleName);
                            itemView.getContext().startActivity(purchasedCoursesIntent);
                        }
                    });

                } else if (MainActivity.currentFragment == 6) {
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            todo: send to SETS activity and send the product id via intent
                        Intent purchasedTestsIntent = new Intent(itemView.getContext(), TestSetsActivity.class);
                        purchasedTestsIntent.putExtra("product_ID", productId);
                        purchasedTestsIntent.putExtra("title", titleName);

                            itemView.getContext().startActivity(purchasedTestsIntent);
                        }
                    });

                } else if (MainActivity.currentFragment == 7) {
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //todo: send via intent the html link of the notes to the notes display activity
                            Intent notesDisplayIntent = new Intent(itemView.getContext(), NotesDisplayActivity.class);
                            notesDisplayIntent.putExtra("product_ID", productId);
                            itemView.getContext().startActivity(notesDisplayIntent);
                        }
                    });

                }else if (MainActivity.currentFragment == 8) {
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try {
                                String filen = UUID.randomUUID().toString().substring(0, 5);

                                Uri uri = Uri.parse(downloadUrlList.get(getAdapterPosition()));

                                DownloadManager downloadManager = (DownloadManager)v.getContext().getSystemService(DOWNLOAD_SERVICE);
                                DownloadManager.Request request = new DownloadManager.Request(uri);
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setDestinationInExternalFilesDir(v.getContext(), DIRECTORY_DOWNLOADS, myCoursesModelList.get(getAdapterPosition()).getProductTitle()+ ".pdf");

                                downloadManager.enqueue(request);
                                Toast.makeText(v.getContext(), "Download Started! Check status bar!", Toast.LENGTH_LONG).show();
                            } catch (Exception e){
                                Toast.makeText(v.getContext(), "Sorry Download Could Not Be Started! Please contact StudyInsta Team!", Toast.LENGTH_LONG).show();
                            } catch (Throwable e){
                                Toast.makeText(v.getContext(), "Sorry Download Could Not Be Started! Please contact StudyInsta Team!", Toast.LENGTH_LONG).show();
                            }
//ye fix karo yaha par log cat banna chahiye ek baar ye thik karo fir dobara connect karunga


                        }
                    });

                } else {

                }
            }
        }
    }

}


