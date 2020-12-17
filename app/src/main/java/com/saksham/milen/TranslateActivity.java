package com.saksham.milen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.ClipboardManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.Locale;

public class TranslateActivity extends AppCompatActivity {

    private EditText sourceText;
    private Button translate;
    private TextView translateText;
    private String source;
    private Button speakBtn;
    private static final String TAG = "LgIdNotification";
    private TextToSpeech textToSpeech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        sourceText = findViewById(R.id.editText);
        translate = findViewById(R.id.translate);
        translateText = findViewById(R.id.textView);
        speakBtn = findViewById(R.id.speak);
        translateText.setMovementMethod(new ScrollingMovementMethod());
        translateText.setOnClickListener(new View.OnClickListener() { // set onclick listener to my textview
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager)getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(translateText.getText().toString());
                Toast.makeText(getApplicationContext(), "Copied 👍👍", Toast.LENGTH_SHORT).show();
            }
        });
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });
        Intent intent = getIntent();

        String value = intent.getStringExtra("translateText");
        sourceText.setText(value);
        translate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                identifyLanguage();
            }
        });
        speakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = sourceText.getText().toString();
                int speech = textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null);

            }
        });
    }

    private void identifyLanguage() {
        if(!sourceText.getText().toString().equals("")){
            source = sourceText.getText().toString();
            LanguageIdentifier languageIdentifier =
                    LanguageIdentification.getClient();
            languageIdentifier.identifyLanguage(source)
                    .addOnSuccessListener(
                            new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(@Nullable String languageCode) {
                                    if(languageCode.equals("en") || languageCode.equals("ta")){
                                        translateText(languageCode);
                                    }else{
                                        Toast.makeText(TranslateActivity.this,
                                                "Language Not Identified", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                        
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(TranslateActivity.this,
                                            "Model couldn’t be loaded or other internal error.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
        }else{
            Toast.makeText(this, "Enter Some Text to be Translated",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void translateText(String languageCode) {
        if(languageCode.equals("en")){
            TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(languageCode)
                        .setTargetLanguage(TranslateLanguage.TAMIL)
                        .build();
        final Translator englishFrenchTranslator =
                Translation.getClient(options);
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        englishFrenchTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object v) {
                                // Model downloaded successfully. Okay to start translating.
                                // (Set a flag, unhide the translation UI, etc.)
                                englishFrenchTranslator.translate(source).addOnSuccessListener
                                        (new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        translateText.setText(s);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(TranslateActivity.this,
                                                "Cannot Be Translated", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                });
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Model couldn’t be downloaded or other internal error.
                                // ...
                            }
                        });
        }else if(languageCode.equals("ta")){
            TranslatorOptions options =
                    new TranslatorOptions.Builder()
                            .setSourceLanguage(languageCode)
                            .setTargetLanguage(TranslateLanguage.ENGLISH)
                            .build();
            final Translator englishFrenchTranslator =
                    Translation.getClient(options);
            DownloadConditions conditions = new DownloadConditions.Builder()
                    .requireWifi()
                    .build();
            englishFrenchTranslator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener(
                            new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object v) {
                                    // Model downloaded successfully. Okay to start translating.
                                    // (Set a flag, unhide the translation UI, etc.)
                                    englishFrenchTranslator.translate(source).addOnSuccessListener
                                            (new OnSuccessListener<String>() {
                                                @Override
                                                public void onSuccess(String s) {
                                                    translateText.setText(s);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(TranslateActivity.this,
                                                    "Cannot Be Translated", Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    });
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Model couldn’t be downloaded or other internal error.
                                    // ...
                                }
                            });
        }
    }
}