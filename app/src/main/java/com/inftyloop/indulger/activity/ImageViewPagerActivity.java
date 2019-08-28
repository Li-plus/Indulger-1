package com.inftyloop.indulger.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.fragment.BigImageFragment;
import com.inftyloop.indulger.listener.PermissionListener;
import com.inftyloop.indulger.ui.Eyes;
import com.inftyloop.indulger.ui.ViewPagerBugFixed;
import com.inftyloop.indulger.ui.BaseActivity;
import com.inftyloop.indulger.util.FileUtils;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageViewPagerActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    private static final String TAG = ImageViewPagerActivity.class.getSimpleName();
    public static final String IMG_URLS = "mImageUrls";
    public static final String POSITION = "position";
    @BindView(R.id.vp_pics)
    ViewPagerBugFixed mVpPics;
    @BindView(R.id.tv_indicator)
    TextView mTvIndicator;
    @BindView(R.id.tv_save)
    TextView mTvSave;

    private List<String> mImageUrls = new ArrayList<>();
    private List<BigImageFragment> mFragments = new ArrayList<>();
    private int mCurPos;
    private SparseBooleanArray mDownloadFlag = new SparseBooleanArray();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_image_view_pager;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Eyes.translucentStatusBar(this);
        Intent intent = getIntent();
        mImageUrls = intent.getStringArrayListExtra(IMG_URLS);
        mCurPos = intent.getIntExtra(POSITION, 0);
        for (int i = 0; i < mImageUrls.size(); ++i) {
            String url = mImageUrls.get(i);
            BigImageFragment frag = new BigImageFragment();
            Bundle bundle = new Bundle();
            bundle.putString(BigImageFragment.IMG_URL, url);
            frag.setArguments(bundle);
            mFragments.add(frag);
            mDownloadFlag.put(i, false);
        }
        mVpPics.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mVpPics.addOnPageChangeListener(this);
        mVpPics.setCurrentItem(mCurPos);
        setIndicator(mCurPos);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
    }

    @Override
    public void onPageSelected(int i) {
        mCurPos = i;
        setIndicator(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    private void setIndicator(int i) {
        mTvIndicator.setText(getString(R.string.image_pager_indicator, i + 1, mImageUrls.size()));
    }

    @OnClick(R.id.tv_save)
    public void onViewClicked() {
        requestRuntimePermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener() {
            @Override
            public void onGranted() {
                downloadImg();
            }

            @Override
            public void onDenied(List<String> deniedPermissions) {
                QMUITipDialog.Builder.makeToast(getBaseContext(), QMUITipDialog.Builder.ICON_TYPE_FAIL, getString(R.string.image_pager_failed_to_gain_write_access),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void downloadImg() {
        String imgUrl = mImageUrls.get(mCurPos);
        boolean isDownloading = mDownloadFlag.get(mCurPos);
        if (!isDownloading){
            mDownloadFlag.put(mCurPos,true);
            new DownloadImgTask(mCurPos).execute(imgUrl);
        }
    }

    @SuppressLint("StaticFieldLeak")
    class DownloadImgTask extends AsyncTask<String,Integer,Void> {

        private int mPosition;

        DownloadImgTask(int position){
            mPosition = position;
        }

        @Override
        protected Void doInBackground(String... params) {
            String imgUrl = params[0];
            File file;
            try {
                @SuppressWarnings("deprecation")
                FutureTarget<File> future = Glide
                        .with(ImageViewPagerActivity.this)
                        .load(imgUrl)
                        .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                file = future.get();

                String filePath = file.getAbsolutePath();

                // TODO - optimize saving process, using uuid instead of timestamp
                String destFileName = System.currentTimeMillis() + FileUtils.getImageFileExt(filePath);
                File destFile = new File(FileUtils.getDir(""), destFileName);

                FileUtils.copy(file, destFile);

                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.fromFile(new File(destFile.getPath()))));
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mDownloadFlag.put(mPosition,false);
            QMUITipDialog.Builder.makeToast(getBaseContext(), QMUITipDialog.Builder.ICON_TYPE_SUCCESS, getString(R.string.image_pager_save_success, FileUtils.getDir("")),
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {}
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}