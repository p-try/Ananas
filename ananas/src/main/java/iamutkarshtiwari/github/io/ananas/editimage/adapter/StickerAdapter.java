package iamutkarshtiwari.github.io.ananas.editimage.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import iamutkarshtiwari.github.io.ananas.R;
import iamutkarshtiwari.github.io.ananas.editimage.adapter.viewholders.StickerViewHolder;

public class StickerAdapter extends RecyclerView.Adapter<ViewHolder> {
    private final OnStickerSelection mCallback;
    private ImageClick imageClick = new ImageClick();
    private List<String> pathList = new ArrayList<>();

    public StickerAdapter(OnStickerSelection callback) {
        super();
        this.mCallback = callback;
    }

    @Override
    public int getItemCount() {
        return pathList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewtype) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.view_sticker_item, parent, false);
        return new StickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder mViewHolder, int position) {
        StickerViewHolder viewHolder = (StickerViewHolder) mViewHolder;
        String path = pathList.get(position);

        String imageUrl = "drawable/" + path;
        int imageKey = viewHolder.itemView.getResources().getIdentifier(imageUrl, "drawable", viewHolder.itemView.getContext().getPackageName());
        viewHolder.image.setImageDrawable(ContextCompat.getDrawable(viewHolder.itemView.getContext(), imageKey));
        viewHolder.image.setTag(imageUrl);
        viewHolder.image.setOnClickListener(imageClick);
    }

    public void addStickerImages(String folderPath, int stickerCount) {
        pathList.clear();
        for (int i = 0; i < stickerCount; i++) {
            pathList.add(folderPath + "_" + Integer.toString(i + 1));
        }
        this.notifyDataSetChanged();
    }

    private final class ImageClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            String data = (String) v.getTag();
            mCallback.onStickerSelected(data);
//            stickerFragment.selectedStickerItem(data);
        }
    }

    public interface OnStickerSelection {
        void onStickerSelected(String data);
    }
}
