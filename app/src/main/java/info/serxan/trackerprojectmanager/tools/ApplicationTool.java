package info.serxan.trackerprojectmanager.tools;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Menu;

import info.serxan.trackerprojectmanager.R;
import info.serxan.trackerprojectmanager.models.UserModel;

public class ApplicationTool extends Application {

    /**
     * Current view name.
     */
    public String currentView;

    /**
     * Current logged user.
     */
    public UserModel loggedUser;

    /**
     * Current context.
     */
    public Context context;

    /**
     * Menu
     */
    public Menu menu;

    /**
     * Check if the user is currently connected or not.
     *
     * @return boolean
     */
    public boolean isUserlogged() {
        return loggedUser != null;
    }

    /**
     * Show an alert.
     *
     * @param context - current context object.
     * @param title - Alert title.
     * @param message - Alert message.
     */
    public void showAlert(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Set the menu context.
     *
     * @param _menu - Menu.
     */
    public void setMenu(Menu _menu) {
        menu = _menu;
    }

    /**
     * Update the menu logged state.
     *
     * @param state - Set true or false
     */
    public void setMenuLogged(Boolean state) {
        if (menu != null) {
            menu.setGroupVisible(R.id.group_logged, state);
        }
    }

}
