package in.myinnos.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.ArrayList;

import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;
import in.myinnos.awesomeimagepicker.models.Image;
import in.myinnos.imagepicker.gallery.ItemTouchHelperViewHolder;


public class GalleryMainActivity extends Activity {

    private static final int READ_STORAGE_PERMISSION = 4000;
    private int LIMIT = 5;
    private ImageView imageView, ivAddImage, ivBack, ivRotate, ivCrop, ivDetele;

    LinearLayoutManager linearLayoutManager;
    RecyclerView rvImageList;
    Uri inputUri = null, outputUri = null;
    int selPosition = 0;
    ArrayList<Image> arrayListImages = null;
    RecyclerListAdapter recyclerListAdapter;

    ViewPager imagePager;
    CustomPagerAdapter customPagerAdapter;
    //boolean isAddClick = false;
    boolean clickImage = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gallery_activity_main);

        rvImageList = (RecyclerView) findViewById(R.id.rvImageList);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivRotate = (ImageView) findViewById(R.id.ivRotate);
        ivCrop = (ImageView) findViewById(R.id.ivCrop);
        ivDetele = (ImageView) findViewById(R.id.ivDetele);

        imagePager = (ViewPager) findViewById(R.id.imagePager);


        //LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvImageList.setLayoutManager(linearLayoutManager);
        rvImageList.setHasFixedSize(true);


        imageView = (ImageView) findViewById(R.id.imageView);
        ivAddImage = (ImageView) findViewById(R.id.ivAddImage);


/*
        imageView.setOnTouchListener(new OnSwipeTouchListener(GravMainActivity.this) {
            public void onSwipeTop() {
               // Toast.makeText(MyActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
               // Toast.makeText(MyActivity.this, "right", Toast.LENGTH_SHORT).show();

                if(selPosition>= 0) {
                    if(selPosition == 0)
                    {

                    }
                    else {
                        selPosition = selPosition - 1;
                    }
                    Uri uri = Uri.fromFile(new File(arrayListImages.get(selPosition).grav_path));

                    inputUri = uri;
                    Glide.with(GravMainActivity.this).load(uri)
                            .placeholder(R.drawable.image_selector)
                            .override(400, 400)
                            .crossFade()
                            .centerCrop()
                            .into(imageView);
                }

                //rvImageList.scrollToPosition(selPosition);
                linearLayoutManager.scrollToPositionWithOffset(selPosition, 0);
            }
            public void onSwipeLeft() {
              //  Toast.makeText(MyActivity.this, "left", Toast.LENGTH_SHORT).show();

                if(selPosition <= arrayListImages.size()) {
                    if(selPosition == arrayListImages.size()-1)
                    {

                    }
                    else {
                        selPosition = selPosition + 1;
                    }

                    Uri uri = Uri.fromFile(new File(arrayListImages.get(selPosition).grav_path));

                    inputUri = uri;
                    Glide.with(GravMainActivity.this).load(uri)
                            .placeholder(R.drawable.image_selector)
                            .override(400, 400)
                            .crossFade()
                            .centerCrop()
                            .into(imageView);
                }
                //rvImageList.scrollToPosition(selPosition);
                linearLayoutManager.scrollToPositionWithOffset(selPosition, 0);
            }
            public void onSwipeBottom() {
               // Toast.makeText(MyActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

        });

*/

        arrayListImages = new ArrayList<>();
        ivAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(arrayListImages !=null && arrayListImages.size() >= 5)
                {
                    Toast.makeText(GalleryMainActivity.this,"Please remove first already 5 images added",Toast.LENGTH_LONG).show();
                }
                else {

                    LIMIT = 5;

                    if (arrayListImages != null && arrayListImages.size() > 0) {
                        LIMIT = LIMIT - arrayListImages.size();
                    }

                    //isAddClick = true;
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (!Helper.checkPermissionForExternalStorage(GalleryMainActivity.this)) {
                            Helper.requestStoragePermission(GalleryMainActivity.this, READ_STORAGE_PERMISSION);
                        } else {
                            // opining custom gallery
                            Intent intent = new Intent(GalleryMainActivity.this, AlbumSelectActivity.class);
                            intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, LIMIT);
                            startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);
                        }
                    } else {
                        Intent intent = new Intent(GalleryMainActivity.this, AlbumSelectActivity.class);
                        intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, LIMIT);
                        startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);
                    }
                }
            }
        });
        ivCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputUri != null) {
                    outputUri = inputUri;
                    //Crop.of(inputUri, outputUri).asSquare().start(GravMainActivity.this);
                    Crop.of(inputUri, outputUri).start(GalleryMainActivity.this);
                }
            }
        });

        ivDetele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickImage = false;
                if (recyclerListAdapter != null) {

                    recyclerListAdapter.removeItem(selPosition);
                    int tempPos = selPosition;

                    if(arrayListImages !=null && tempPos > 0)
                    {
                        clickImage = true;
                        selPosition = (arrayListImages.size() - tempPos);
                    }
                    else {
                        selPosition = 0;
                    }

                    //rvImageList.scrollToPosition(selPosition);
                    recyclerListAdapter.notifyDataSetChanged();
                    linearLayoutManager.scrollToPositionWithOffset(selPosition, 0);
                    customPagerAdapter.notifyDataSetChanged();
                    imagePager.setCurrentItem(selPosition);
                }
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (arrayListImages ==null || arrayListImages.size() <= 0) {
            ivAddImage.performClick();
        }


        imagePager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            // optional
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            // optional
            @Override
            public void onPageSelected(final int position) {
                    clickImage = true;
                    selPosition = position;
                    recyclerListAdapter.notifyDataSetChanged();
                    //recyclerListAdapter = new RecyclerListAdapter(GravMainActivity.this, arrayListImages);
                    //rvImageList.setAdapter(recyclerListAdapter);
            }

            // optional
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


/*
    @Override
    protected void onResume() {
        super.onResume();

        if(isAddClick == true)
        {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(arrayListImages != null && arrayListImages.size() > 0)
                    {

                    }
                    else
                    {
                        finish();
                    }
                }
            },5000);
        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConstantsCustomGallery.REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {


            ArrayList<Image> arrListTmpImage = null;
            arrListTmpImage = new ArrayList<>();

            arrListTmpImage = data.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);

            //The array list has the image paths of the selected images
            arrayListImages.addAll(arrListTmpImage);

           // arrayListImages.addAll(arrayListImages);



            recyclerListAdapter = new RecyclerListAdapter(this, arrayListImages);
            rvImageList.setAdapter(recyclerListAdapter);


            selPosition = 0;
            customPagerAdapter = new CustomPagerAdapter(this, arrayListImages);
            imagePager.setAdapter(customPagerAdapter);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    linearLayoutManager.scrollToPositionWithOffset(selPosition, 0);
                    customPagerAdapter.notifyDataSetChanged();
                    recyclerListAdapter.notifyDataSetChanged();
                }
            }, 500);


        } else {
            if (arrayListImages != null && arrayListImages.size() > 0) {
            } else {
                finish();
            }
        }


        if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            Glide.with(this).load(outputUri)
                    .placeholder(R.drawable.image_selector)
                    .override(400, 400)
                    .crossFade()
                    .centerCrop()
                    .into(imageView);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder> {

        ArrayList<Image> mItems = new ArrayList<>();
        Context mContext;


        public RecyclerListAdapter(Context context, ArrayList<Image> arrayListImage) {
            mContext = context;
            mItems = arrayListImage;
            //mItems.addAll(arrayListImage);
        }

        @Override
        public RecyclerListAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item_main, parent, false);
            RecyclerListAdapter.ItemViewHolder itemViewHolder = new RecyclerListAdapter.ItemViewHolder(view);
            return itemViewHolder;
        }

        @Override
        public void onBindViewHolder(final RecyclerListAdapter.ItemViewHolder holder, final int position) {
            //holder.textView.setText(mItems.get(position).name);

            final Uri uri = Uri.fromFile(new File(mItems.get(position).path));

            Glide.with(mContext).load(uri)
                    .placeholder(R.drawable.image_selector)
                    .override(400, 400)
                    .crossFade()
                    .centerCrop()
                    .into(holder.ivImage);

            if (position == 0 && clickImage == false) {
                selPosition = 0;
                inputUri = uri;
                imagePager.setCurrentItem(selPosition);

               /* Glide.with(mContext).load(uri)
                        .placeholder(R.drawable.image_selector)
                        .override(400, 400)
                        .crossFade()
                        .centerCrop()
                        .into(imageView);*/
            }

            if (selPosition == position) {
                holder.ivImage.setSelected(true);
            } else {
                holder.ivImage.setSelected(false);
            }


            holder.ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selPosition = position;
                    inputUri = uri;
                    imagePager.setCurrentItem(selPosition);

                 /*   Glide.with(mContext).load(uri)
                            .placeholder(R.drawable.image_selector)
                            .override(400, 400)
                            .crossFade()
                            .centerCrop()
                            .into(imageView);*/

                    notifyDataSetChanged();
                }
            });

            holder.handleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mItems.remove(position);
                    arrayListImages.remove(position);

                    notifyDataSetChanged();
                    if (mItems.size() == 0) {
                        finish();
                      /*  Glide.with(mContext).load("")
                                .placeholder(R.drawable.image_selector)
                                .override(400, 400)
                                .crossFade()
                                .centerCrop()
                                .into(imageView);*/
                    } else {
                        selPosition = 0;
                        linearLayoutManager.scrollToPositionWithOffset(selPosition, 0);
                        //rvImageList.scrollToPosition(selPosition);
                    }
                }
            });


        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public void removeItem(int position) {
            if (mItems != null) {
                //mItems.remove(position);
                arrayListImages.remove(position);
            }
//            notifyDataSetChanged();

            if (mItems.size() == 0) {
                finish();
               /* Glide.with(mContext).load("")
                        .placeholder(R.drawable.image_selector)
                        .override(400, 400)
                        .crossFade()
                        .centerCrop()
                        .into(imageView);*/
            }


        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {

            //public final TextView textView;
            public final ImageView handleView;
            public final ImageView ivImage;

            public ItemViewHolder(View itemView) {
                super(itemView);
                //   textView = (TextView) itemView.findViewById(R.id.text);
                handleView = (ImageView) itemView.findViewById(R.id.handle);
                ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            }
        }
    }

    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;
        ArrayList<Image> mItems = new ArrayList<>();

        public CustomPagerAdapter(Context context, ArrayList<Image> arrayListItems) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mItems = arrayListItems;
        }


        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((FrameLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //selPosition = position +1;

            View itemView = mLayoutInflater.inflate(R.layout.gallery_pager_item, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);

            final Uri uri = Uri.fromFile(new File(mItems.get(position).path));

            Glide.with(mContext).load(uri)
                    .placeholder(R.drawable.image_selector)
                    //.override(400, 400)
                    .crossFade()
                    //  .centerCrop()
                    .into(imageView);

            container.addView(itemView);





            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((FrameLayout) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }
    }

/*


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
*/



/*
    public class RecyclerListAdapter extends RecyclerView.Adapter<in.myinnos.imagepicker.gallery.RecyclerListAdapter.ItemViewHolder>
            implements ItemTouchHelperAdapter {

        //  private final List<String> mItems = new ArrayList<>();
        private final ArrayList<Image> mItems = new ArrayList<>();

        Context mContext;
        private final OnStartDragListener mDragStartListener;

        public RecyclerListAdapter(Context context, OnStartDragListener dragStartListener, ArrayList<Image> arrayListImage ) {
            mContext = context;
            mDragStartListener = dragStartListener;
            mItems.addAll(arrayListImage);
        }

        @Override
        public in.myinnos.imagepicker.gallery.RecyclerListAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item_main, parent, false);
            in.myinnos.imagepicker.gallery.RecyclerListAdapter.ItemViewHolder itemViewHolder = new in.myinnos.imagepicker.gallery.RecyclerListAdapter.ItemViewHolder(view);
            return itemViewHolder;
        }

        @Override
        public void onBindViewHolder(final in.myinnos.imagepicker.gallery.RecyclerListAdapter.ItemViewHolder holder, int position) {
            holder.textView.setText(mItems.get(position).name);

          final  Uri uri = Uri.fromFile(new File(mItems.get(position).grav_path));

            Glide.with(mContext).load(uri)
                    .placeholder(R.drawable.image_selector)
                    .override(400, 400)
                    .crossFade()
                    .centerCrop()
                    .into(holder.ivImage);

            // Start a drag whenever the handle view it touched
            holder.handleView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(holder);
                    }
                    return false;
                }
            });

            holder.ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Glide.with(mContext).load(uri)
                            .placeholder(R.drawable.image_selector)
                            .override(400, 400)
                            .crossFade()
                            .centerCrop()
                            .into(imageView);
                }
            });




        }

        @Override
        public void onItemDismiss(int position) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            Collections.swap(mItems, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        *//**
     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     *//*
        public  class ItemViewHolder extends RecyclerView.ViewHolder implements
                ItemTouchHelperViewHolder {

            public final TextView textView;
            public final ImageView handleView;
            public final ImageView ivImage;

            public ItemViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.text);
                handleView = (ImageView) itemView.findViewById(R.id.handle);
                ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            }

            @Override
            public void onItemSelected() {
                itemView.setBackgroundColor(Color.LTGRAY);
            }

            @Override
            public void onItemClear() {
                itemView.setBackgroundColor(0);
            }
        }
    }*/
}
