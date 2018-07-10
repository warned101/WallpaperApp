package com.example.android.wallpaperworld.adapters;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.android.wallpaperworld.R;
import com.example.android.wallpaperworld.models.Category;
import com.example.android.wallpaperworld.models.Wallpaper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static android.Manifest.permission.SYSTEM_ALERT_WINDOW;

public class WallpapersAdapter extends RecyclerView.Adapter<WallpapersAdapter.WallpaperViewHolder> {

    private Context mCtx;
    private List<Wallpaper> wallpaperList;

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;


    public WallpapersAdapter(Context mCtx, List<Wallpaper> wallpaperList) {
        this.mCtx = mCtx;
        this.wallpaperList = wallpaperList;

    }

    @Override
    public WallpaperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_wallpapers, parent, false);
        return new WallpaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WallpaperViewHolder holder, int position) {
        Wallpaper w = wallpaperList.get(position);
        holder.textView.setText(w.title);
        Glide.with(mCtx)
                .load(w.url)
                .into(holder.imageView);

        if (w.isFavourite){
            holder.checkBoxFav.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return wallpaperList.size();
    }

    class WallpaperViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        TextView textView;
        ImageView imageView;

        CheckBox checkBoxFav;
        FloatingActionButton buttonShare, buttonDownload, buttonSet, fab_plus;
        Animation fab_open, fab_close, rotate_clockwise, rotate_anticlockwise;
        boolean isOpen = false;

        public WallpaperViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.text_view_title);
            imageView = itemView.findViewById(R.id.image_view);

            checkBoxFav = itemView.findViewById(R.id.checkbox_favourite);

            buttonShare = itemView.findViewById(R.id.button_share);
            buttonDownload = itemView.findViewById(R.id.button_download);
            buttonSet = itemView.findViewById(R.id.button_set);
            fab_plus = itemView.findViewById(R.id.fab_plus);

            fab_open = AnimationUtils.loadAnimation(mCtx.getApplicationContext(), R.anim.fab_open);
            fab_close = AnimationUtils.loadAnimation(mCtx.getApplicationContext(), R.anim.fab_close);
            rotate_clockwise = AnimationUtils.loadAnimation(mCtx.getApplicationContext(), R.anim.rotate_clockwise);
            rotate_anticlockwise = AnimationUtils.loadAnimation(mCtx.getApplicationContext(), R.anim.rotate_anticlockwise);

            checkBoxFav.setOnCheckedChangeListener(this);
            buttonShare.setOnClickListener(this);
            buttonDownload.setOnClickListener(this);
            buttonSet.setOnClickListener(this);
            fab_plus.setOnClickListener(this);
        }

        @TargetApi(Build.VERSION_CODES.M)
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View view) {
            if (isOpen){
                buttonSet.startAnimation(fab_close);
                buttonDownload.startAnimation(fab_close);
                buttonShare.startAnimation(fab_close);
                fab_plus.startAnimation(rotate_anticlockwise);
                buttonSet.setClickable(false);
                buttonDownload.setClickable(false);
                buttonShare.setClickable(false);
                isOpen = false;
                switch (view.getId()) {
                    case R.id.button_share:
                        shareWallpaper(wallpaperList.get(getAdapterPosition()));
                        break;
                    case R.id.button_download:
                        downloadWallpaper(wallpaperList.get(getAdapterPosition()));
                        break;
                    case R.id.button_set:
                        setAsWallpaper(wallpaperList.get(getAdapterPosition()));
                }
            }
            else {
                buttonSet.startAnimation(fab_open);
                buttonDownload.startAnimation(fab_open);
                buttonShare.startAnimation(fab_open);
                fab_plus.startAnimation(rotate_clockwise);
                buttonSet.setClickable(true);
                buttonDownload.setClickable(true);
                buttonShare.setClickable(true);
                isOpen = true;

                switch (view.getId()) {
                    case R.id.button_share:
                        shareWallpaper(wallpaperList.get(getAdapterPosition()));
                        break;
                    case R.id.button_download:
                        downloadWallpaper(wallpaperList.get(getAdapterPosition()));
                        break;
                    case R.id.button_set:
                        setAsWallpaper(wallpaperList.get(getAdapterPosition()));
                }
            }

        }

        private void shareWallpaper(Wallpaper w){
            ((Activity) mCtx).findViewById(R.id.progressbar).setVisibility(View.VISIBLE);

            Glide.with(mCtx)
                    .asBitmap()
                    .load(w.url)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            ((Activity) mCtx).findViewById(R.id.progressbar).setVisibility(View.GONE);

                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("image/*");
                            intent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(resource));

                            mCtx.startActivity(Intent.createChooser(intent, "Wallpaper World"));
                        }
                    });
        }

        private Uri getLocalBitmapUri(Bitmap bmp){
            Uri bmpUri = null;

            try {
                File file = new File(mCtx.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "wallpaper_world_"+System.currentTimeMillis()+".png");

                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 60, out);
                out.close();
                bmpUri = Uri.fromFile(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmpUri;
        }

        private void downloadWallpaper(final Wallpaper wallpaper){
            ((Activity) mCtx).findViewById(R.id.progressbar).setVisibility(View.VISIBLE);

            Glide.with(mCtx)
                    .asBitmap()
                    .load(wallpaper.url)
                    .into(new SimpleTarget<Bitmap>() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            ((Activity) mCtx).findViewById(R.id.progressbar).setVisibility(View.GONE);

                            Intent intent = new Intent(Intent.ACTION_VIEW);

                            Uri uri = saveWallpaperAndGetUri(resource, wallpaper.id);

                            if (uri!=null){
                                intent.setDataAndType(uri, "image/*");
                                mCtx.startActivity(Intent.createChooser(intent, "Wallpaper World"));
                            }
                        }
                    });

        }


        private Uri saveWallpaperAndGetUri(Bitmap bitmap, String id){
            if (ContextCompat.checkSelfPermission(mCtx, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mCtx, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                    Uri uri = Uri.fromParts("package", mCtx.getPackageName(), null);
                    intent.setData(uri);
                    mCtx.startActivity(intent);
                }
                else {
                    ActivityCompat.requestPermissions((Activity) mCtx, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                }
                return null;
            }

            File folder = new File(Environment.getExternalStorageDirectory().toString()+"/Wallpaper_World");
            folder.mkdirs();

            File file = new File(folder, id + ".jpg");

            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                return Uri.fromFile(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.M)
        @RequiresApi(api = Build.VERSION_CODES.M)
        private void setAsWallpaper(final Wallpaper wallpaper) {
            ((Activity) mCtx).findViewById(R.id.progressbar).setVisibility(View.VISIBLE);

            Glide.with(mCtx)
                    .asBitmap()
                    .load(wallpaper.url)
                    .into(new SimpleTarget<Bitmap>() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            ((Activity) mCtx).findViewById(R.id.progressbar).setVisibility(View.GONE);


                            int requestCode = 5469;
                            if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
                                if (!Settings.canDrawOverlays((Activity) mCtx)) {
                                    Toast.makeText(mCtx, "GIVE PERMISSIONS", Toast.LENGTH_LONG).show();
                                    checkPermission();
                                } else {
                                    try {
                                        WallpaperManager.getInstance(mCtx.getApplicationContext()).setBitmap(resource);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Toast.makeText(mCtx, "WALLPAPER CHANGED SUCCESSFULLY", Toast.LENGTH_LONG).show();

                            }
                        }

                        private void checkPermission() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (!Settings.canDrawOverlays((Activity)mCtx)){
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mCtx.getPackageName()));
                                    intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                    mCtx.startActivity(intent);
                                }
                            }
                        }
                    });
        }



        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            if (FirebaseAuth.getInstance().getCurrentUser()==null){
                Toast.makeText(mCtx,"Please login first...", Toast.LENGTH_LONG).show();
                compoundButton.setChecked(false);
                return;
            }

            int position = getAdapterPosition();
            Wallpaper w = wallpaperList.get(position);

            DatabaseReference dbFavs = FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("favourites")
                    .child(w.category);


            if (b){
                dbFavs.child(w.id).setValue(w);
            } else {
                dbFavs.child(w.id).setValue(null);
            }

        }

    }
}
