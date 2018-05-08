package info.serxan.trackerprojectmanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

import info.serxan.trackerprojectmanager.R;
import info.serxan.trackerprojectmanager.models.PositionModel;

public class RegisteredSpyAdapter extends ArrayAdapter<PositionModel> {

    /**
     * Colors array.
     * We use these color and show a different color on odd and even item.
     */
    private String[] colors = new String[] { "#ECF0F1", "#FFFFFF" };

    /**
     * RegisteredSpyAdapter
     *
     * @param context
     * @param list
     */
    public RegisteredSpyAdapter(Context context, ArrayList<PositionModel> list) {
        super(context, 0, list);
    }

    /**
     * Initialize the list view item.
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        PositionModel positionModel = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_position_item, parent, false);
        }

        // Lookup view for data population
        RelativeLayout itemBC = (RelativeLayout) convertView.findViewById(R.id.rl_pos_item);
        TextView itemName = (TextView) convertView.findViewById(R.id.tv_pos_item_name);
        TextView itemDesc = (TextView) convertView.findViewById(R.id.tv_pos_item_desc);
        TextView itemDate = (TextView) convertView.findViewById(R.id.tv_pos_item_date);
        TextView itemLt = (TextView) convertView.findViewById(R.id.tv_pos_item_latitude);
        TextView itemLg = (TextView) convertView.findViewById(R.id.tv_pos_item_longitude);

        // Populate the data into the template view using the data object
        itemBC.setBackgroundColor(Color.parseColor(colors[(position % 2)]));
        itemName.setText(positionModel.getPhoneBrand() + " - " + positionModel.getPhoneModel());
        itemDesc.setText(positionModel.getPhoneId());
        itemDate.setText(
                getContext().getString(R.string.va_itemCreatedAt) +
                        " " + positionModel.getCreatedAt());
        itemLt.setText(
                getContext().getString(R.string.va_itemLt)
                        + " " + String.valueOf(positionModel.getLatitude()));
        itemLg.setText(
                getContext().getString(R.string.va_itemLg)
                        + " " + String.valueOf(positionModel.getLongitude()));

        // Return the completed view to render on screen
        return convertView;
    }
}
