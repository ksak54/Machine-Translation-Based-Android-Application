package com.saksham.milen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionCloudDocumentRecognizerOptions;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;


import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

//    private FirebaseDatabase database;
//    private DatabaseReference databaseReference;
    private ImageButton imageView;
    private TextView textView;

    private Button detectBTN;
    private Button translate;
    Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int CROP_IMAGE = 2;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);
        textView = findViewById(R.id.text_view);

        detectBTN = findViewById(R.id.detect_text_image);
        textView.setMovementMethod(new ScrollingMovementMethod());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();

            }
        });
        detectBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectTextFromImage();
            }
        });

        translate = findViewById(R.id.translate);
        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(textView.equals("")) {
                    Intent intent = new Intent(MainActivity.this,
                            TranslateActivity.class);
                    startActivity(intent);
                }else{
                    String value = textView.getText().toString();
                    Intent intent = new Intent(MainActivity.this,
                            TranslateActivity.class);
                    intent.putExtra("translateText" , value);
                    startActivity(intent);
                }
            }
        });
    }

    private void detectTextFromImage() {

        if (imageBitmap != null) {
            Log.d("Error:", "Inside Detect From Image");
            FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
            FirebaseVisionCloudDocumentRecognizerOptions options =
                    new FirebaseVisionCloudDocumentRecognizerOptions.Builder()
                            .setLanguageHints(Arrays.asList("en", "ta"))
                            .build();
            FirebaseVisionDocumentTextRecognizer textRecognizer = FirebaseVision.getInstance()
                    .getCloudDocumentTextRecognizer(options);

            textRecognizer.processImage(firebaseVisionImage)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionDocumentText>() {
                        @Override
                        public void onSuccess(FirebaseVisionDocumentText result) {
                            displayTextFromImage(result);
                        }
                    })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Error: " +
                                            e.getMessage(), Toast.LENGTH_SHORT);
                                    Log.d("Error: ", e.getMessage());
                                }
                            });
        }else
            Toast.makeText(this, "No Image", Toast.LENGTH_SHORT).show();
    }

    private void displayTextFromImage(FirebaseVisionDocumentText result) {
            try {
            List<FirebaseVisionDocumentText.Block> blockList = result.getBlocks();
            for(FirebaseVisionDocumentText.Block block: result.getBlocks()){
                String text = block.getText();
                textView.setText(text);
                Toast.makeText(this, "Text Found", Toast.LENGTH_SHORT).show();
            }
            }catch(Exception e){
                Toast.makeText(this, "No Text Found", Toast.LENGTH_SHORT).show();
            }
//        if(blockList.size() == 0){
//            Toast.makeText(this, "No Text Found", Toast.LENGTH_SHORT).show();
//        }
//        else{
//
//        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .start(this);
//            Bundle extras = data.getExtras();
//            imageBitmap = (Bitmap) extras.get("data");
//            imageView.setImageBitmap(imageBitmap);
        }
        else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                    imageView.setImageURI(resultUri);
//            Bundle extras = data.getExtras();
//            imageBitmap = (Bitmap) extras.get("data");
//            imageView.setImageBitmap(imageBitmap);
                try{imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver()
                        , resultUri);}catch(Exception e){

                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

            }
//
        }
    }
}