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

package com.prapp.barcodescanner;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

public class CustomBarcodeEncoder {

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    private static final int TEXT_COLOR = Color.BLACK;
    private static final int TEXT_SIZE = 12;
    private static final int TEXT_SPACING_SIZE = 5;

    private class TextWrapper{
        private String text;
        private boolean bold;
        private int textSize = TEXT_SIZE;
        private int textSpacingSize = TEXT_SPACING_SIZE;

        public TextWrapper(String text, boolean bold) {
            this.text = text;
            this.bold = bold;
        }

        public int getTextSize() {
            return textSize;
        }

        public int getTextSpacingSize() {
            return textSpacingSize;
        }

        public String getText() {
            return text;
        }


        public boolean isBold() {
            return bold;
        }

        public int getHeight(){
            return textSize + textSpacingSize;
        }
/** 
* Restituisce la dimensione del testo prendendo la larghezza come parametro in ingresso.
* @param width
* @return intero che rappresenta la dimensione del testo
*/
        public int calculateTextSize(int width){
            int calculatedSize = width/textSize;
            return calculatedSize < textSize ? calculatedSize : textSize;
        }
    }

    private ArrayList<TextWrapper> listText;


    public CustomBarcodeEncoder() {
        listText = new ArrayList<>();
    }

    public CustomBarcodeEncoder(@NotNull ArrayList<TextWrapper> listText) {
        this.listText = listText;
    }

    public boolean add(String text, boolean bold) {
        TextWrapper textWrapper = new TextWrapper(text, bold);
        return listText.add(textWrapper);
    }
/** 
* Crea una Bitmap richiamando il metodo addTextToBitMap, usato per aggiungere informazioni testuali al QR code 
* @param matrix
* @return
*/
    public Bitmap createBitmap(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();

        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);


        return addTextToBitmap(bitmap, width, height);
    }

    /**
     * Restituisce l'altezza aggiuntiva dal testo.
     * @return intero che rappresenta l'aggiunta di altezza
     */
    private int getExtraHeight(){
        int extraHeight = 0;

        for(TextWrapper wrapper : listText){
            extraHeight += wrapper.getHeight();
        }

        return extraHeight;
    }

    /**
     * Restituisce la massima lunghezza che assume il testo.
     * @return
     */
    private int getMaxTextWidth(){
        int maxWordWidth = 0;

        for(TextWrapper wrapper : listText){
            int tmpWidth = wrapper.getTextSize() * wrapper.getText().length();
            if(maxWordWidth < tmpWidth){
                maxWordWidth = tmpWidth;
            }
        }

        return maxWordWidth;
    }

    /**
     * Usato per aggiungere informazioni testuali al QR Code.
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    private Bitmap addTextToBitmap(Bitmap bitmap, int width, int height){
        Bitmap mutableBitmap = Bitmap.createBitmap(width, height + getExtraHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);

        //Oggetto per il disegno.
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(TEXT_SIZE); // Text Size
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)); // Text Overlapping Pattern

        //Disegno un bianco di sfondo sull'extra.
        canvas.drawRect(0, 0, width, height+getExtraHeight(), paint);
        paint.setColor(TEXT_COLOR); // Text Color

        //Ridisegno il QR
        canvas.drawBitmap(bitmap, 0, 0, null);

        //Ricavo la lunghezza massima del testo:
        //Se troppo grande dovrò ridurre la lunghezza del testo.
        int maxTextWidth = getMaxTextWidth();
        maxTextWidth = maxTextWidth > mutableBitmap.getWidth() ? mutableBitmap.getWidth() : maxTextWidth;

        int startY = height;

        for(TextWrapper wrapper : listText){
            paint.setTypeface(wrapper.isBold() ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
            paint.setTextSize(wrapper.calculateTextSize(maxTextWidth));

            canvas.drawText(wrapper.getText(), 0, startY, paint);

            startY += wrapper.getHeight();
        }

        return mutableBitmap;
    }

    private BitMatrix encode(String contents, BarcodeFormat format, int width, int height) throws WriterException {
        try {
            return new MultiFormatWriter().encode(contents, format, width, height);
        } catch (WriterException e) {
            throw e;
        } catch (Exception e) {
            // ZXing sometimes throws an IllegalArgumentException
            throw new WriterException(e);
        }
    }

    private BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
        try {
            return new MultiFormatWriter().encode(contents, format, width, height, hints);
        } catch (WriterException e) {
            throw e;
        } catch (Exception e) {
            throw new WriterException(e);
        }
    }

    public Bitmap encodeBitmap(String contents, BarcodeFormat format, int width, int height) throws WriterException {
        return createBitmap(encode(contents, format, width, height));
    }

    public Bitmap encodeBitmap(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
        return createBitmap(encode(contents, format, width, height, hints));
    }

}
