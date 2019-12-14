package com.halo.techisfood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;

public class Activity_Home extends AppCompatActivity {

    final int RQS_IMAGE1 = 1;
    EditText editTextCaption;
    ImageView imageResult;
    Uri source1;

    int textSize, textShadow, darkness, topMargin;

    SeekBar seekbar_top;
    Bitmap bmp_final;
    Bitmap bm1, bmp, newBitmap;

    Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        editTextCaption = (EditText) findViewById(R.id.caption);
        imageResult = (ImageView) findViewById(R.id.result);

        imageResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageLoader();
            }
        });

        editTextCaption.addTextChangedListener(new TextWatcher() {
            @Override

            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                processText();
            }
        });

        SeekBar seekBar_textSize = findViewById(R.id.seekbar_textSize);
        seekBar_textSize.setMax(500);
        seekBar_textSize.setProgress(150);
        seekBar_textSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textSize = i;
                processText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar seekbar_textShadow = findViewById(R.id.seekbar_textShadow);
        seekbar_textShadow.setMax(100);
        seekbar_textShadow.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textShadow = i;
                processText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekbar_top = findViewById(R.id.seekbar_top);
        seekbar_top.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    topMargin = i;
                    processText();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final SeekBar seekbar_darkness = findViewById(R.id.seekbar_dark);
        seekbar_darkness.setMax(255);
        seekbar_darkness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                darkness = seekbar_darkness.getProgress();
                processBrandFilter();
            }
        });

        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RQS_IMAGE1:
                    source1 = data.getData();
                    bm1 = null;
                    try {
                        bm1 = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(source1));
                        bm1 = Bitmap.createScaledBitmap(bm1, 2000, 2000, true);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    Bitmap.Config config = bm1.getConfig();
                    if (config == null) {
                        config = Bitmap.Config.ARGB_8888;
                    }
                    newBitmap = Bitmap.createBitmap(bm1.getWidth(), bm1.getHeight(), config);
                    processBrandFilter();
                    break;
            }
        }
    }

    public void processText() {

        if (source1 != null) {
            Bitmap processedBitmap = ProcessingBitmap();
            if (processedBitmap != null) {
                imageResult.setImageBitmap(processedBitmap);
                bmp_final = processedBitmap;
            } else {
                Toast.makeText(getApplicationContext(),
                        "Something wrong in processing!",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Select both image!",
                    Toast.LENGTH_LONG).show();
        }

    }

    public void processBrandFilter() {
        Bitmap.Config config = bm1.getConfig();
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }
        Bitmap brandBitmap = Bitmap.createBitmap(bm1.getWidth(), bm1.getHeight(), config);
        Canvas newCanvas = new Canvas(brandBitmap);

        newCanvas.drawBitmap(bm1, 0, 0, null);

        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.BLACK);
        p.setAlpha(darkness);
        newCanvas.drawRect(0, 0, newCanvas.getWidth(), newCanvas.getHeight(), p);


        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.blank);
        bitmap1 = Bitmap.createScaledBitmap(bitmap1, 2000, 2000, true);

        newCanvas.drawBitmap(bitmap1, 0, 0, null);

        bmp = brandBitmap;

        processText();
    }

    private Bitmap ProcessingBitmap() {

        Canvas newCanvas = new Canvas(newBitmap);

        seekbar_top.setMax(newCanvas.getHeight());

        newCanvas.drawBitmap(bmp, 0, 0, null);

        String captionString = editTextCaption.getText().toString();
        if (captionString != null) {

            Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintText.setColor(Color.WHITE);
            paintText.setTextSize(textSize);
            paintText.setFakeBoldText(true);
            paintText.setTextAlign(Paint.Align.CENTER);
            paintText.setStyle(Paint.Style.FILL);
            paintText.setShadowLayer(textShadow, 0f, 0f, Color.parseColor("#AA000000"));

            Rect rectText = new Rect();
            rectText.offset(0, 0);
            paintText.getTextBounds(captionString, 0, captionString.length(), rectText);

            String lines[] = captionString.split("\n");

            int yy = 0;

            for (int i = 0; i < lines.length; ++i) {

                int xPos = (newCanvas.getWidth() / 2);
                int yPos = (newCanvas.getHeight() / 2) - ((rectText.height() * (lines.length) + yy) / 2);


//                int top = yPos + rectText.height() * (i + 1) + yy;
                int top = topMargin;

                if (i != 0) {
                    top = (int) (top + (textSize + textSize * 0.4) * (i));
                }

                newCanvas.drawText(lines[i],
                        xPos, top, paintText);
            }

        } else {
            Toast.makeText(getApplicationContext(),
                    "caption empty!",
                    Toast.LENGTH_LONG).show();
        }

        return newBitmap;
    }

    public void openImageLoader() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RQS_IMAGE1);
    }

    public void saveImage() {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/TechIsFood");
        myDir.mkdirs();
        String fname = Calendar.getInstance().getTime() + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bmp_final.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}