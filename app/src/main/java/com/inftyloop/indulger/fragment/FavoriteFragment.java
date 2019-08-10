package com.inftyloop.indulger.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.inftyloop.indulger.R;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.popup.QMUIListPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoriteFragment extends QMUIFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    ArrayList<Map<String, Object>> mData = new ArrayList<>();
    FavoriteItemAdapter mAdapter;
    int mAdapterPosition;

    @Override
    public View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.favorite, null);
        ButterKnife.bind(this, root);
        mTopBar.setTitle(getString(R.string.favorite_title));
        mTopBar.addRightTextButton("add fav", 0).setOnClickListener((View view) -> {
            Toast.makeText(getActivity(), "add", Toast.LENGTH_SHORT).show();
            addFavoriteItem(R.mipmap.ic_launcher, "news " + Math.random(), "plus-Li", "9102-08-08");
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView recyclerView = root.findViewById(R.id.favorite_item);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new FavoriteItemAdapter(getActivity(), mData);
        recyclerView.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        addFavoriteItem(R.mipmap.ic_launcher, "Java Summer Semester", "Li-plus", "2019-08-08");

        return root;
    }

    public void removeFavoriteItem(int index) {
        mData.remove(index);
        mAdapter.notifyDataSetChanged();
    }

    public void addFavoriteItem(int imageSource, String title, String press, String date) {
        Map<String, Object> map = new HashMap<>();
        map.put("img", imageSource);
        map.put("title", title);
        map.put("press", press);
        map.put("date", date);
        mData.add(map);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public TransitionConfig onFetchTransitionConfig() {
        return SLIDE_TRANSITION_CONFIG;
    }

    public class FavoriteItemAdapter extends RecyclerView.Adapter<FavoriteItemAdapter.ViewHolder> {

        private List<Map<String, Object>> mData;
        private Context mContext;
        private QMUIListPopup mListPopup;

        public FavoriteItemAdapter(Context context, List<Map<String, Object>> data) {
            mContext = context;
            mData = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.favorite_item, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Map<String, Object> brief = mData.get(position);
            holder.viewTitle.setText((String) brief.get("title"));
            holder.viewDate.setText((String) brief.get("date"));
            holder.viewImg.setImageResource((int) brief.get("img"));
            holder.viewPress.setText((String) brief.get("press"));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView viewPress;
            TextView viewDate;
            ImageView viewImg;
            TextView viewTitle;

            ViewHolder(View itemView) {
                super(itemView);
                viewPress = itemView.findViewById(R.id.press);
                viewDate = itemView.findViewById(R.id.date);
                viewImg = itemView.findViewById(R.id.img);
                viewTitle = itemView.findViewById(R.id.title);
                itemView.setOnClickListener((View view) -> {
                    Toast.makeText(mContext, "showing news " + viewTitle.getText(), Toast.LENGTH_SHORT).show();
                });
                itemView.setOnLongClickListener((View view) -> {
                    mAdapterPosition = getAdapterPosition();
                    initListPopupIfNeed();
                    mListPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
                    mListPopup.setPreferredDirection(QMUIPopup.DIRECTION_TOP);
                    mListPopup.show(view);
                    return true;
                });
            }
        }

        private void initListPopupIfNeed() {
            if (mListPopup != null)
                return;

            String[] listItems = new String[]{
                    getString(R.string.favorite_cancel),
                    getString(R.string.favorite_delete),
            };
            List<String> data = new ArrayList<>();
            Collections.addAll(data, listItems);
            ArrayAdapter adapter = new ArrayAdapter<>(mContext, R.layout.simple_list_item, data);
            mListPopup = new QMUIListPopup(mContext, QMUIPopup.DIRECTION_NONE, adapter);
            mListPopup.create(QMUIDisplayHelper.dp2px(mContext, 100), QMUIDisplayHelper.dp2px(mContext, 200),
                    (AdapterView<?> adapterView, View view, int i, long l) -> {
                        Toast.makeText(mContext, "Item " + (i + 1), Toast.LENGTH_SHORT).show();
                        mListPopup.dismiss();
                        if (listItems[i].equals(getString(R.string.favorite_delete)))
                            removeFavoriteItem(mAdapterPosition);
                    });
        }
    }
}
