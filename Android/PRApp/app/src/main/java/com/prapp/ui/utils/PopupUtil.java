/*
 * PRApp  Copyright (C) 2019  Luca Bartolomei
 *
 * This file is part of PRApp.
 *
 *     PRApp is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     PRApp is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with PRApp.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.prapp.ui.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gurutouchlabs.kenneth.elegantdialog.ElegantActionListeners;
import com.gurutouchlabs.kenneth.elegantdialog.ElegantDialog;
import com.prapp.R;

import org.jetbrains.annotations.NotNull;

public class PopupUtil {

    private Context context;
    private Dialog popup;

    public PopupUtil(Context context) {
        this.context = context;
        this.popup = new Dialog(context);
    }

    //https://www.awsrh.com/2017/10/custom-pop-up-window-with-android-studio.html
    /**
     * Mostra un popup personalizzato di esito.
     *
     * @param imageResId ID immagine da visualizzare
     * @param textResId ID testo da visualizzare
     * @param text Testo custom da visualizzare (come arg di textResId)
     */
    public void showEsitoPopup(Activity activity, int imageResId, int textResId, String text) {
        showEsitoPopup(activity, context.getDrawable(imageResId), textResId, text);
    }

    public void showEsitoPopup(Activity activity, Bitmap image, int textResId, String text) {
        showEsitoPopup(activity, new BitmapDrawable(context.getResources(), image), textResId, text);
    }

    public void showEsitoPopup(Activity activity, Drawable image, int textResId, String text){
        ImageView esitoImage;
        TextView esitoText;

        popup.setContentView(R.layout.esito_popup);

        esitoImage = popup.findViewById(R.id.popup_esito_image);
        esitoText = popup.findViewById(R.id.popup_esito_text);

        //Imposto la schermata di successo
        Glide.with(activity).load(image).into(esitoImage);

        //Costruisco il testo se bisogno:
        if (text != null) {
            String esitoTextString = context.getString(textResId, text);
            esitoText.setText(esitoTextString);
        } else {
            esitoText.setText(textResId);
        }

        esitoImage.setOnClickListener(view -> popup.dismiss());

        popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.show();
    }


    public void showQRPopup(Activity activity, int imageResId, View.OnClickListener listener, int buttonTextResId) {
        showQRPopup(activity, context.getDrawable(imageResId), listener, buttonTextResId);
    }

    public void showQRPopup(Activity activity, Bitmap image, View.OnClickListener listener, int buttonTextResId) {
        showQRPopup(activity, new BitmapDrawable(context.getResources(), image), listener, buttonTextResId);
    }

    public void showQRPopup(Activity activity, Drawable image, View.OnClickListener listener, int buttonTextResId){
        ImageView qrImage;
        Button qrButton;

        popup.setContentView(R.layout.qr_popup);

        qrImage = popup.findViewById(R.id.popup_qr_image);
        qrButton = popup.findViewById(R.id.popup_qr_button);

        //Imposto la schermata di successo
        Glide.with(activity).load(image).into(qrImage);

        //Se trovo il listener allora faccio vedere il pulsante
        if(listener != null){
            qrButton.setText(buttonTextResId);
            qrButton.setOnClickListener(listener);
            qrImage.setOnClickListener(listener);
        }else{
            qrButton.setVisibility(View.GONE);
            qrImage.setOnClickListener(view -> popup.dismiss());
        }


        popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.show();
    }

    /**
     * Mostra un popup personalizzato di esito.
     *
     * @param iconResId ID icona da visualizzare
     * @param textResId ID testo da visualizzare
     * @param text Testo custom da visualizzare (come arg di textResId)
     */
    public void showElegantEsitoPopup(Activity activity, int iconResId, int textResId, String text) {
        ElegantDialog dialog = new ElegantDialog(context);

        dialog.setTitleIcon(context.getDrawable(iconResId));
        dialog.setTitleIconBackgroundColor(android.R.color.white);

        dialog.setBackgroundTopColor(R.color.primaryColorGrey)
                .setBackgroundBottomColor(R.color.primaryColorGrey);

        dialog.setCustomView(R.layout.esito_popup);

        dialog.setCornerRadius(50f)                 //Set dialog corner radius
                .setCanceledOnTouchOutside(false)   // Dismiss on tap outside
                .setTitleHidden(false);             // Hide title


        dialog.setElegantActionClickListener(new ElegantActionListeners() {
            @Override
            public void onPositiveListener(@NotNull ElegantDialog elegantDialog) {
                dialog.dismiss();
            }

            @Override
            public void onNegativeListener(@NotNull ElegantDialog elegantDialog) {
                dialog.dismiss();
            }

            @Override
            public void onGotItListener(@NotNull ElegantDialog elegantDialog) {
                dialog.dismiss();
            }

            @Override
            public void onCancelListener(@NotNull DialogInterface dialogInterface) {
                dialog.dismiss();
            }
        });

        dialog.show();

        dialog.getPositiveButton().setVisibility(View.VISIBLE);
        dialog.getNegativeButton().setVisibility(View.GONE);
        dialog.getGotItButton().setVisibility(View.GONE);

        View customView = dialog.getCustomView();

        TextView esitoText = customView.findViewById(R.id.popup_esito_text);

        //Costruisco il testo se bisogno:
        if (text != null) {
            String esitoTextString = context.getString(textResId, text);
            esitoText.setText(esitoTextString);
        } else {
            esitoText.setText(textResId);
        }



    }

}
