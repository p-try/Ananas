package iamutkarshtiwari.github.io.ananas.editimage.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import iamutkarshtiwari.github.io.ananas.R;

import static iamutkarshtiwari.github.io.ananas.editimage.utils.Utils.dpToPx;

public class ColorPickerAdapter extends RecyclerView.Adapter<ColorPickerAdapter.ViewHolder> {

    private final List<Integer> colorPickerColors;
    private OnColorPickerClickListener onColorPickerClickListener;
    private int selectedPos;

    private RelativeLayout.LayoutParams selectItemParams;
    private RelativeLayout.LayoutParams unselectItemParams;

    public ColorPickerAdapter(@NonNull Context context, int selectedPos) {
        this.colorPickerColors = getKelly22Colors(context);
        this.selectedPos = selectedPos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.color_picker_item_list, parent, false);

        //Prepare the layout params in prior to avoid initializing it in onBindViewHolder
        int height, width, margin = dpToPx(4);
        height = width = dpToPx(50);
        selectItemParams = new RelativeLayout.LayoutParams(width, height);
        selectItemParams.setMargins(margin, margin, margin, margin);

        height = width = dpToPx(40);
        unselectItemParams = new RelativeLayout.LayoutParams(width, height);
        unselectItemParams.setMargins(0, 0, 0, 0);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.colorPickerView.setBackgroundColor(colorPickerColors.get(position));
        if (selectedPos == position) {
            holder.border.setVisibility(View.VISIBLE);
            holder.colorPickerView.setLayoutParams(selectItemParams);
        } else {
            holder.border.setVisibility(View.GONE);
            holder.colorPickerView.setLayoutParams(unselectItemParams);
        }
    }

    @Override
    public int getItemCount() {
        return colorPickerColors.size();
    }

    public void setOnColorPickerClickListener(OnColorPickerClickListener onColorPickerClickListener) {
        this.onColorPickerClickListener = onColorPickerClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View colorPickerView, border;

        ViewHolder(View itemView) {
            super(itemView);
            colorPickerView = itemView.findViewById(R.id.color_picker_view);
            border = itemView.findViewById(R.id.color_picker_background);
            itemView.setOnClickListener(v -> {
                if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

                // Updating old as well as new positions
                notifyItemChanged(selectedPos);
                selectedPos = getAdapterPosition();
                notifyItemChanged(selectedPos);

                if (onColorPickerClickListener != null)
                    onColorPickerClickListener.onColorPickerClickListener(
                            colorPickerColors.get(getAdapterPosition()), selectedPos);
            });
        }
    }

    public interface OnColorPickerClickListener {
        void onColorPickerClickListener(int colorCode, int selectedColorPos);
    }

    private List<Integer> getKelly22Colors(Context context) {
        Resources resources = context.getResources();
        List<Integer> colorList = new ArrayList<>();
        for (int i = 0; i <= 21; i++) {
            int resourceId = resources.getIdentifier("kelly_" + (i + 1), "color",
                    context.getPackageName());
            colorList.add(resources.getColor(resourceId));
        }
        return colorList;
    }
}
