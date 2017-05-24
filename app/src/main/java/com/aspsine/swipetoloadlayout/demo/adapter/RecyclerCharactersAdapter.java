package com.aspsine.swipetoloadlayout.demo.adapter;

import android.content.res.Resources;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.demo.fragment.TwitterRecyclerFragment;
import com.aspsine.swipetoloadlayout.demo.model.Character;
import com.aspsine.swipetoloadlayout.demo.model.Section;
import com.aspsine.swipetoloadlayout.hcare.R;
import com.squareup.picasso.CircleTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aspsine on 2015/9/9.
 */
public class RecyclerCharactersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_VIEWPAGER = 0;
    private static final int TYPE_GROUP = 1;
    private static final int TYPE_CHILD = 2;

    private final List<Character> mHeroes;

    private final List<Section> mSections;

    private final List<Integer> mGroupPositions;

    private LoopViewPagerAdapter mPagerAdapter;

    protected OnGroupItemClickListener mOnGroupItemClickListener;

    protected OnGroupItemLongClickListener mOnGroupItemLongClickListener;

    protected OnChildItemClickListener mOnChildItemClickListener;

    protected OnChildItemLongClickListener mOnChildItemLongClickListener;

    private final int mType;

    public RecyclerCharactersAdapter(int type) {
        mType = type;
        mHeroes = new ArrayList<>();
        mSections = new ArrayList<>();
        mGroupPositions = new ArrayList<>();
    }

    public void setList(List<Character> heroes, List<Section> sections) {
        mHeroes.clear();
        mSections.clear();
        mHeroes.addAll(heroes);
        append(sections);
    }

    public void append(List<Section> sections) {
        mSections.addAll(sections);
        notifyDataSetChanged();
        initGroupPositions();
    }

    public void initGroupPositions() {
        mGroupPositions.clear();
        int groupPosition = 0;
        for (int i = 0; i < getGroupCount(); i++) {
            if (i == 0) {
                groupPosition = 0;
            } else {
                groupPosition += getChildCount(i - 1) + 1;
            }
            mGroupPositions.add(groupPosition + 1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_VIEWPAGER;
        } else if (mGroupPositions.contains(position)) {
            return TYPE_GROUP;
        } else {
            return TYPE_CHILD;
        }
    }

    @Override
    public int getItemCount() {
        int count = mHeroes.size() == 0 ? 0 : 1;
        for (int i = 0; i < getGroupCount(); i++) {
            count += getChildCount(i);
        }
        return count + getGroupCount();
    }

    public int getChildCount(int groupPosition) {
        List<Character> characters = mSections.get(groupPosition).getCharacters();

        return characters != null ? characters.size() : 0;
    }

    public int getGroupCount() {
        return mSections.size();
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    RecyclerView.Adapter adapter = recyclerView.getAdapter();
                    if (isFullSpanType(adapter.getItemViewType(position))) {
                        return gridLayoutManager.getSpanCount();
                    }
                    return 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        int type = getItemViewType(position);
        if (isFullSpanType(type)) {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
                lp.setFullSpan(true);
            }
        }
    }

    private boolean isFullSpanType(int type) {
        return type == TYPE_VIEWPAGER || type == TYPE_GROUP;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        int type = getItemViewType(i);
        View itemView = null;
        switch (type) {
            case TYPE_VIEWPAGER:
                itemView = inflate(viewGroup, R.layout.layout_viewpager);
                return new ViewPagerHolder(itemView);
            case TYPE_GROUP:
                itemView = inflate(viewGroup, R.layout.item_header);
                return new GroupHolder(itemView);
            case TYPE_CHILD:
                if (mType == TwitterRecyclerFragment.TYPE_LINEAR) {
                    itemView = inflate(viewGroup, R.layout.item_hero);
                } else {
                    itemView = inflate(viewGroup, R.layout.item_hero_grid);
                }
                final ChildHolder holder = new ChildHolder(itemView);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int absolutePosition = holder.getAdapterPosition();
                        int groupPosition = getGroupPosition(absolutePosition);
                        int childPosition = getChildPosition(absolutePosition);
                        Character character = mSections.get(groupPosition).getCharacters().get(childPosition);
                        Toast.makeText(view.getContext(), character.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
                return holder;
        }
        throw new IllegalArgumentException("Wrong type!");
    }

    private View inflate(ViewGroup parent, int layoutRes) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        int type = getItemViewType(i);
        switch (type) {
            case TYPE_VIEWPAGER:
                onBindViewPagerHolder((ViewPagerHolder) viewHolder);
                break;
            case TYPE_GROUP:
                onBindGroupHolder((GroupHolder) viewHolder, getGroupPosition(i));
                break;
            case TYPE_CHILD:
                onBindChildHolder((ChildHolder) viewHolder, getGroupPosition(i), getChildPosition(i));
                break;
        }
    }

    private void onBindViewPagerHolder(ViewPagerHolder holder) {
        if (holder.viewPager.getAdapter() == null) {
            mPagerAdapter = new LoopViewPagerAdapter(holder.viewPager, holder.indicators);
            holder.viewPager.setAdapter(mPagerAdapter);
            holder.viewPager.addOnPageChangeListener(mPagerAdapter);
            holder.viewPager.setBackgroundDrawable(holder.itemView.getResources().getDrawable(R.mipmap.bg_viewpager));
            mPagerAdapter.setList(mHeroes);
        } else {
            mPagerAdapter.setList(mHeroes);
        }
    }

    private void onBindGroupHolder(GroupHolder holder, int parentPosition) {
        holder.tvGroup.setText(mSections.get(parentPosition).getName());
    }

    private void onBindChildHolder(ChildHolder holder, int parentPosition, int childPosition) {
        Character character = mSections.get(parentPosition).getCharacters().get(childPosition);
        holder.tvName.setText(character.getName());
        Resources resources = holder.itemView.getResources();
        int size = resources.getDimensionPixelOffset(R.dimen.hero_avatar_size);
        int width = resources.getDimensionPixelOffset(R.dimen.hero_avatar_border);
        Picasso.with(holder.itemView.getContext())
                .load(character.getAvatar())
                .resize(size, size)
                .transform(new CircleTransformation(width))
                .into(holder.ivAvatar);
    }

    int getGroupPosition(int position) {
        int groupPosition = 1;
        for (int i = mGroupPositions.size() - 1; i >= 0; i--) {
            if (position >= mGroupPositions.get(i)) {
                groupPosition = i;
                break;
            }
        }
        return groupPosition;
    }

    int getChildPosition(int position) {
        int groupPosition = getGroupPosition(position);
        int absGroupPosition = mGroupPositions.get(groupPosition);
        int childPositionInGroup = position - absGroupPosition - 1;
        return childPositionInGroup;
    }

    public void start() {
        if (mPagerAdapter != null) {
            mPagerAdapter.start();
        }
    }

    public void stop() {
        if (mPagerAdapter != null) {
            mPagerAdapter.stop();
        }
    }

    static class ViewPagerHolder extends RecyclerView.ViewHolder {
        ViewPager viewPager;
        ViewGroup indicators;

        public ViewPagerHolder(View itemView) {
            super(itemView);
            viewPager = (ViewPager) itemView.findViewById(R.id.viewPager);
            indicators = (ViewGroup) itemView.findViewById(R.id.indicators);
        }
    }

    static class GroupHolder extends RecyclerView.ViewHolder {
        TextView tvGroup;

        public GroupHolder(View itemView) {
            super(itemView);
            tvGroup = (TextView) itemView.findViewById(R.id.tvHeader);
        }
    }

    static class ChildHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName;

        public ChildHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            ivAvatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
        }
    }


}
