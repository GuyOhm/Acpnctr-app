package com.acpnctr.acpnctr.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Window;
import android.widget.Button;

import com.acpnctr.acpnctr.R;
import com.acpnctr.acpnctr.models.PulseType;

/**
 * Util class for Acpnctr App
 */

public class AcpnctrUtil {

    /**
     * Generate an array of {@link PulseType} for the 28 types of pulse
     *
     * @return an Array of PulseType object
     */
    public static PulseType[] createPulseTypesArray(){

        return new PulseType[]{
                new PulseType(
                        Constants.PULSES_28_TYPES_FEOU_KEY,
                        R.string.pulses_28_types_feou,
                        R.string.pulses_28_types_feou_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_ROA_KEY,
                        R.string.pulses_28_types_roa,
                        R.string.pulses_28_types_roa_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_CHE_KEY,
                        R.string.pulses_28_types_che,
                        R.string.pulses_28_types_che_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_RONG_KEY,
                        R.string.pulses_28_types_rong,
                        R.string.pulses_28_types_rong_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_TSIN_KEY,
                        R.string.pulses_28_types_tsin,
                        R.string.pulses_28_types_tsin_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_KREOU_KEY,
                        R.string.pulses_28_types_kreou,
                        R.string.pulses_28_types_kreou_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_SIEN_KEY,
                        R.string.pulses_28_types_sien,
                        R.string.pulses_28_types_sien_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_TCHIN_KEY,
                        R.string.pulses_28_types_tchin,
                        R.string.pulses_28_types_tchin_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_TCHRE_KEY,
                        R.string.pulses_28_types_tchre,
                        R.string.pulses_28_types_tchre_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_SEU_KEY,
                        R.string.pulses_28_types_seu,
                        R.string.pulses_28_types_seu_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_OE_KEY,
                        R.string.pulses_28_types_oé,
                        R.string.pulses_28_types_oé_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_ROAN_KEY,
                        R.string.pulses_28_types_roan,
                        R.string.pulses_28_types_roan_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_JOAN_KEY,
                        R.string.pulses_28_types_joan,
                        R.string.pulses_28_types_joan_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_JOU_KEY,
                        R.string.pulses_28_types_jou,
                        R.string.pulses_28_types_jou_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_FOU_KEY,
                        R.string.pulses_28_types_fou,
                        R.string.pulses_28_types_fou_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_CHOU_KEY,
                        R.string.pulses_28_types_chou,
                        R.string.pulses_28_types_chou_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_HIU_KEY,
                        R.string.pulses_28_types_hiu,
                        R.string.pulses_28_types_hiu_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_KO_KEY,
                        R.string.pulses_28_types_ko,
                        R.string.pulses_28_types_ko_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_SAN_KEY,
                        R.string.pulses_28_types_san,
                        R.string.pulses_28_types_san_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_SI_KEY,
                        R.string.pulses_28_types_si,
                        R.string.pulses_28_types_si_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_LAO_KEY,
                        R.string.pulses_28_types_lao,
                        R.string.pulses_28_types_lao_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_TONG_KEY,
                        R.string.pulses_28_types_tong,
                        R.string.pulses_28_types_tong_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_TSOU_KEY,
                        R.string.pulses_28_types_tsou,
                        R.string.pulses_28_types_tsou_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_TSIE_KEY,
                        R.string.pulses_28_types_tsie,
                        R.string.pulses_28_types_tsie_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_TA_KEY,
                        R.string.pulses_28_types_ta,
                        R.string.pulses_28_types_ta_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_TCHRANG_KEY,
                        R.string.pulses_28_types_tchrang,
                        R.string.pulses_28_types_tchrang_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_TOAN_KEY,
                        R.string.pulses_28_types_toan,
                        R.string.pulses_28_types_toan_transl),
                new PulseType(
                        Constants.PULSES_28_TYPES_TAE_KEY,
                        R.string.pulses_28_types_taé,
                        R.string.pulses_28_types_taé_transl)
        };
    }

    /**
     * This method takes a string and replace the last char by a "z"
     * This is used as a take around to implement a search with firestore as there is no native
     * solution.
     *
     * @param searchText client name to be searched
     * @return String which is the modified text
     */
    public static String zeefyForSearch(String searchText) {
        return searchText + "z";
    }

    /**
     * Show a dialog that warns the user that data are going to be deleted
     *
     * @param context is the current context of the running Activity
     * @param deleteButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to delete data
     */
    public static void showDeleteDataDialog(Context context, DialogInterface.OnClickListener
            deleteButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Set the dialog message
        builder.setMessage(R.string.delete_data_message);
        // Set click listener for positive/delete button
        builder.setPositiveButton(R.string.delete_data_confirm, deleteButtonClickListener);
        // Set click listener for negative/cancel button
        builder.setNegativeButton(R.string.delete_data_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Customize the alert dialog box
        Window alertDialogWindow = alertDialog.getWindow();
        if (alertDialogWindow != null) {
            alertDialogWindow.setBackgroundDrawableResource(R.color.colorAccent);
        }
        Button negButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negButton.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        Button posButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        posButton.setTextColor(context.getResources().getColor(R.color.colorPrimary));
    }
}
