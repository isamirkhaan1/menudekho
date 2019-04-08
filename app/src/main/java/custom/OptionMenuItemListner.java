package custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.samirkhan.apps.citymenu.AboutActivity;
import com.samirkhan.apps.citymenu.CityListActivity;
import com.samirkhan.apps.citymenu.R;
import com.samirkhan.apps.citymenu.RestaurantListActivity;

/**
 * Created by Samir KHan on 6/27/2016.
 */
public class OptionMenuItemListner {

    private final static String FILE = "com.samirkhan.apps.citymenu.file";

    Context context;
    Intent intent;

    private int itemIndex;
    Boolean isFinishCurrentActivity = true;
    private final String SETTING_DEFAULT_CITY = "setDefaultCity";

    public OptionMenuItemListner(Context context, int itemIndex) {
        this.context = context;
        this.itemIndex = itemIndex;

        if (context.getClass().getSimpleName().equals(RestaurantListActivity.class.getSimpleName())) {
            isFinishCurrentActivity = false;
        }
    }

    public void performAction() {

        // default city selection..
        if (itemIndex == R.id.text_menu_my_city) {
            intent = new Intent(context, CityListActivity.class);

            if (isFinishCurrentActivity)
                ((Activity) context).finish();

            intent.putExtra(SETTING_DEFAULT_CITY, true);
            context.startActivity(intent);
        }
        // all cities..
        else if (itemIndex == R.id.text_menu_all_cities) {
            intent = new Intent(context, CityListActivity.class);

            if (isFinishCurrentActivity)
                ((Activity) context).finish();

            context.startActivity(intent);

        }
        // restaurant user..
        else if (itemIndex == R.id.text_menu_restuser) {
            intent = new Intent(context, RestaurantListActivity.class);
            intent.putExtra("isRestaurantLogin", true);
            if (isFinishCurrentActivity)
                ((Activity) context).finish();
            context.startActivity(intent);

        }

        // about us & contact us..
        else if (itemIndex == R.id.text_menu_contact) {
            intent = new Intent(context, AboutActivity.class);

            if (isFinishCurrentActivity)
                ((Activity) context).finish();
            intent.putExtra(SETTING_DEFAULT_CITY, true);
            context.startActivity(intent);
        }
        // last updated date..
        else if (itemIndex == R.id.text_menu_last_updated) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            PreferencesFile preferencesFile = new PreferencesFile(context);
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_sync,null);
            TextView textView = (TextView) view.findViewById(R.id.text_dialog_sync);
            textView.setText("Last time, City Menu has been synced on " + preferencesFile.lastUpdates());
            builder.setView(view);
            builder.show();
        }

    }
}
