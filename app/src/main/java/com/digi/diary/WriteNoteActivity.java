package com.digi.diary;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.digi.diary.adapter.SpinnerAdapter;
import com.digi.diary.db.tables.NotesTable;
import com.digi.diary.model.NotesModel;
import com.digi.diary.utils.CommonUtilities;
import com.digi.diary.utils.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WriteNoteActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mEditTextDate, mEdittextTitle, mEditTextContent;
    Calendar mCalendar;
    private String formattedDate;
    private ImageView mSave, mCreatePdf, mEdit, mApperarance,mLock;
    RoundedImageView mAttachment;
    //    private Spinner mFontSpinner;
    private static String FILE = Environment.getExternalStorageDirectory() + File.separator + "firstPdf.pdf";
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.RED);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);
    TextView txt1;
    NotesModel mNotesModel;
    Boolean flag = true;
    boolean isSaved;
    private static final int SELECT_PHOTO = 100;
    private LinearLayout mBottomLayout;
    private String themeName;
    int mWidth;
    Uri selectedImage;
    public static final int MIN_VALUE=10;
    InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        themeName = pref.getString("theme", "Theme1");
        if (themeName.equals("Theme1")) {
            setTheme(R.style.AppTheme);
        } else if (themeName.equals("Theme2")) {
            Toast.makeText(this, "set theme", Toast.LENGTH_SHORT).show();
            setTheme(R.style.AppTheme1);
        }
        setContentView(R.layout.activity_write_note);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mWidth = displaymetrics.widthPixels;
        initView();
        //ads
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7134639728280028/2990643597");
//        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        AdRequest adRequest = new AdRequest.Builder()

                // Add a test device to show Test Ads
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mInterstitialAd.loadAd(adRequest);

        // Prepare an Interstitial Ad Listener
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                // Call displayInterstitial() function
                displayInterstitial();
            }
        });
        //ads


    }
    public void displayInterstitial() {
        // If Ads are loaded, show Interstitial else show nothing.
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }
    private void initView() {
        mEditTextDate = (EditText) findViewById(R.id.editdate);
        mEdittextTitle = (EditText) findViewById(R.id.edittitle);
        mEditTextContent = (EditText) findViewById(R.id.editnote);
        mApperarance = (ImageView) findViewById(R.id.apperance);
        mLock = (ImageView) findViewById(R.id.lock);
        mAttachment = (RoundedImageView) findViewById(R.id.attachment);
        mBottomLayout = (LinearLayout) findViewById(R.id.bottom);
        mApperarance.setOnClickListener(this);
        mAttachment.setOnClickListener(this);
        if (themeName.equals("Theme1")) {
            mBottomLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else if (themeName.equals("Theme2")) {
            mBottomLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary_Pink));
        }
//        mEditTextContent.setHorizontallyScrolling(false);
//        mEditTextContent.setMaxLines(Integer.MAX_VALUE);
        mCalendar = Calendar.getInstance();
        mSave = (ImageView) findViewById(R.id.save);
        mEdit = (ImageView) findViewById(R.id.edit);

        mCreatePdf = (ImageView) findViewById(R.id.genpdf);
        mCreatePdf.setOnClickListener(this);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.profle);
        mAttachment.setImageBitmap(icon);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yy");
        String formattedDate = df.format(mCalendar.getTime());
        mEditTextDate.setHint(formattedDate);
        Intent intent = getIntent();
        if (intent != null) {
            mNotesModel = intent.getParcelableExtra("val");
            if (mNotesModel != null) {
                if(mNotesModel.getmPhotoUri()!=null) {
                    selectedImage = Uri.parse(mNotesModel.getmPhotoUri());
                    final int takeFlags = intent.getFlags()
                            & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
// Check for the freshest data.
                    getContentResolver().takePersistableUriPermission(selectedImage, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    Log.d("check","uri "+selectedImage);

                    Bitmap bm=null;
                    try
                    {
                       bm = MediaStore.Images.Media.getBitmap(getContentResolver() , selectedImage);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    //Bitmap bm = BitmapFactory.decodeFile(selectedImage.getPath());
                    Log.d("check","bm "+bm);

//                    InputStream imageStream = null;
//                    try {
//                        imageStream = getContentResolver().openInputStream(selectedImage);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    Bitmap bm = BitmapFactory.decodeStream(imageStream);
                    mAttachment.setImageBitmap(bm);
//                    BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), getCircleBitmap(bm));
//                    mAttachment.setBackgroundDrawable(bitmapDrawable);
                }
                int font=CommonUtilities.getTextFont(WriteNoteActivity.this);
//                if(CommonUtilities.getTextFontSize(WriteNoteActivity.this)!=0)
                mEditTextContent.setTextSize(CommonUtilities.getTextFontSize(WriteNoteActivity.this));
//                else
//                    mEditTextContent.setTextSize(18);
                switch (font)
                {
                    case 0:
                            Typeface face = Typeface.createFromAsset(getAssets(), "fonts/GoodDog.otf");
                            mEditTextContent.setTypeface(face);
                            CommonUtilities.setTextFont(WriteNoteActivity.this,0);
                        break;
                    case 1:
                        Typeface face1 = Typeface.createFromAsset(getAssets(), "fonts/KaushanScript-Regular.otf");
                        mEditTextContent.setTypeface(face1);
                        CommonUtilities.setTextFont(WriteNoteActivity.this,1);

                        break;
                    case 2:
                        Typeface face2 = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
                        mEditTextContent.setTypeface(face2);
                        CommonUtilities.setTextFont(WriteNoteActivity.this,2);
                        break;
                }
                mEdittextTitle.setText(mNotesModel.getmTitle());
                mEditTextContent.setText(mNotesModel.getmContents());
                String myFormat = "dd-MMM-yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                mEditTextDate.setText("" + sdf.format(mNotesModel.getmDate() * 1000));
//                mEditTextContent.setInputType(InputType.TYPE_NULL);
                mEditTextContent.setEnabled(false);
                mEdittextTitle.setEnabled(false);
                mEditTextDate.setEnabled(false);
                mApperarance.setEnabled(false);
//                mEdit.setAlpha(.5f);

                mLock.setImageResource(R.mipmap.lock);

            }
//            mEditTextContent.setEnabled(false);
//            mEdittextTitle.setEnabled(false);
//            mEditTextDate.setEnabled(false);

            mEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (flag) {

                        flag = false;

                        Toast.makeText(WriteNoteActivity.this, "Read Write Mode Enable", Toast.LENGTH_SHORT).show();
//                        view.setEnabled(true);
                        mEditTextContent.setEnabled(true);
                        mEdittextTitle.setEnabled(true);
                        mEditTextDate.setEnabled(true);
                        mApperarance.setEnabled(true);
//                        view.setEnabled(false);
//                        view.setAlpha(1f);
                        mLock.setImageResource(0);
                    } else {

                        flag = true;


                        Toast.makeText(WriteNoteActivity.this, "Read Only Mode Enable", Toast.LENGTH_SHORT).show();
                        mEditTextContent.setEnabled(false);
                        mEdittextTitle.setEnabled(false);
                        mEditTextDate.setEnabled(false);
                        mApperarance.setEnabled(false);
//                        view.setAlpha(.5f);
                        mLock.setImageResource(R.mipmap.lock);
                    }

                }
            });

        }
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        mEditTextDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(WriteNoteActivity.this, date, mCalendar
                        .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSaved = true;
                String ds = mEditTextDate.getText().toString();
                String cs = mEditTextContent.getText().toString();
                Log.d("check", "ds " + ds + " cs " + cs);
                if ((ds != null && !ds.isEmpty()) && (cs != null && !cs.isEmpty())) {
                    NotesModel notesModel = new NotesModel();
                    String myFormat = "dd-MMM-yy"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                    Date d = null;
                    try {
                        d = sdf.parse(mEditTextDate.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
//                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                    df.setTimeZone(TimeZone.getTimeZone("GMT"));
//                    Date d = mCalendar.getTime();
//                    df.format(d);
                    Log.d("dateval", " in save miliisec val " + d.getTime() + " date " + date + " year " + new Date(d.getTime()).getYear());

//                timecheck(d.getTime());
                    NotesTable notesTable = new NotesTable((BaseApplication) getApplicationContext());
                    if (mNotesModel != null) {
//                        if (notesTable.isRecordExistById(mNotesModel.getId())) {
                        notesModel.setId(mNotesModel.getId());
                        notesModel.setmDate(d.getTime() / 1000);
                        notesModel.setmTitle(mEdittextTitle.getText().toString());
                        notesModel.setmContents(mEditTextContent.getText().toString());
                        if(selectedImage!=null)
                            notesModel.setmPhotoUri(selectedImage.toString());
                        notesTable.updateRecord(notesModel, mNotesModel.getId());
//                        }
                    } else {
                        notesModel.setId("" + System.currentTimeMillis() + Math.random());
                        notesModel.setmDate(d.getTime() / 1000);
                        notesModel.setmTitle(mEdittextTitle.getText().toString());
                        notesModel.setmContents(mEditTextContent.getText().toString());
                        if(selectedImage!=null) {
                            notesModel.setmPhotoUri(selectedImage.toString());

                        }
//                        notesModel.setmPhotoUri("anuuri");
                        notesTable.insertData(notesModel);
                    }
//                    Log.d("dateval", " without db notesModel.getmDate() " + notesModel.getmDate());

                    finish();
                } else {
                    if (ds == null || ds.isEmpty())
                        Toast.makeText(WriteNoteActivity.this, "Please select a date", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(WriteNoteActivity.this, "Please write contents", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void timecheck(long currentDateTime) {
        Date currentDate = new Date(currentDateTime);
        System.out.println("current Date: " + currentDate);
    }

    private void updateLabel() {
        String myFormat = "dd-MMM-yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        mEditTextDate.setText(sdf.format(mCalendar.getTime()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.genpdf:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(WriteNoteActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(WriteNoteActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(WriteNoteActivity.this,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                1);
                    } else {
                        showDailog();
                    }
                } else {
                    showDailog();
                }

                break;
            case R.id.apperance:
                showDailogAppearance();
                break;
            case R.id.attachment:
                Intent intent;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                }else{
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                }
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Choose Photo"), SELECT_PHOTO);
//                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//// Start the Intent
//                startActivityForResult(galleryIntent, SELECT_PHOTO);
                break;
//            case R.id.fontsize:
//                ShowDialog();
//                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        final int takeFlags = imageReturnedIntent.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                        ContentResolver resolver = getContentResolver();

                            resolver.takePersistableUriPermission(selectedImage, takeFlags);

                    }

                    Log.d("check"," first time uri "+selectedImage);
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap bm = BitmapFactory.decodeStream(imageStream);
//                    BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), getCircleBitmap(bm));
                    mAttachment.setImageBitmap(bm);
                }
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        Log.d("check"," in method  width "+width+" hieght "+height);
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    private void showDailog() {
        final Dialog dialog = new Dialog(WriteNoteActivity.this);
        dialog.setContentView(R.layout.df_option_dialog);
        dialog.show();
        dialog.findViewById(R.id.current_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createandDisplayPdf(false);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.complete_dairy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createandDisplayPdf(true);
                dialog.dismiss();
            }
        });
    }

    private void showDailogAppearance() {
        final Dialog dialog1 = new Dialog(WriteNoteActivity.this);
        dialog1.setContentView(R.layout.appearance_dialog);
        dialog1.show();
        Spinner mFontSpinner = (Spinner) dialog1.findViewById(R.id.spinner);
        ArrayList<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("style 1");
        spinnerArray.add("style 2");
        spinnerArray.add("style 3");
        SpinnerAdapter spinnerArrayAdapter = new SpinnerAdapter(WriteNoteActivity.this, R.layout.spinner_item, spinnerArray); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFontSpinner.setAdapter(spinnerArrayAdapter);
        mFontSpinner.setSelection(CommonUtilities.getTextFont(WriteNoteActivity.this));
        mFontSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)//if 1 is for sunday
                {
                    Typeface face = Typeface.createFromAsset(getAssets(), "fonts/GoodDog.otf");
                    mEditTextContent.setTypeface(face);
                    CommonUtilities.setTextFont(WriteNoteActivity.this,0);
                } else if (position == 1)//if 1 is for monday
                {
                    Typeface face = Typeface.createFromAsset(getAssets(), "fonts/KaushanScript-Regular.otf");
                    mEditTextContent.setTypeface(face);
                    CommonUtilities.setTextFont(WriteNoteActivity.this,1);

                } else if (position == 2)//if 1 is for monday
                {
                    Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
                    mEditTextContent.setTypeface(face);
                    CommonUtilities.setTextFont(WriteNoteActivity.this,2);

                }
//                    dialog.dismiss();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//                dialog.dismiss();
//            }
//        });

                SeekBar seekbar = (SeekBar) dialog1.findViewById(R.id.seekbar);

        seekbar.setMax(50);

seekbar.setProgress(CommonUtilities.getTextFontSize(WriteNoteActivity.this));
                seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(progress>MIN_VALUE) {
                            Log.d("check", " progress " + progress);
                            mEditTextContent.setTextSize(progress);
                            CommonUtilities.setTextFontSize(WriteNoteActivity.this,progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }

//                ShowDialog();
                //dialog.dismiss();

        });
    }

    // Method for creating a pdf file from text, saving it then opening it for display
    public void createandDisplayPdf(boolean isCompletDiary) {
        String pdfName="";
//        Document doc = new Document();
//        Document document = new Document();
        try {
            String DEFAULT_STORAGE_LOCATION = Environment.getExternalStorageDirectory().getPath()+"/Diary";
            File dir = new File(DEFAULT_STORAGE_LOCATION);
            if (!dir.exists()) {
                try {
                    dir.mkdirs();
                } catch (Exception e) {
                    Log.e("check",
                            "RecordService::makeOutputFile unable to create directory "
                                    + dir + ": " + e);
                }
            } else {
                if (!dir.canWrite()) {
                    Log.e("check",
                            "RecordService::makeOutputFile does not have write permission for directory: "
                                    + dir);
                }
            }
             pdfName=System.currentTimeMillis()+mEditTextDate.getText().toString()+".pdf";
            File file = new File(dir, pdfName);
            FileOutputStream fOut = new FileOutputStream(file);


//            PdfWriter.getInstance(document, fOut);
//            document.open();



            addTitlePage(isCompletDiary, fOut, mEditTextDate.getText().toString(),
                    mEdittextTitle.getText().toString(), mEditTextContent.getText().toString(),selectedImage);

        }
// catch (DocumentException de) {
//            Log.e("PDFCreator", "DocumentException:" + de);
//        } catch (IOException e) {
//            Log.e("PDFCreator", "ioException:" + e);
//        } finally {
//            try {
//                document.close();
//            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        //}

        viewPdf(pdfName, "Diary");
    }

    private void addTitlePage(boolean isCompleteDiary, FileOutputStream fOut, String date, String title, String content,Uri uri)
            throws DocumentException {


        if (!isCompleteDiary) {
            if(!content.equals("")&&content!=null) {
                Document document = new Document();
                try {
                    PdfWriter.getInstance(document, fOut);
                    document.open();
                    Paragraph preface = new Paragraph();
//                    addEmptyLine(preface, 1);
                    Paragraph p1 = new Paragraph(date, catFont);
                    p1.setAlignment(Element.ALIGN_RIGHT);
                    preface.add(p1);

                    //Lets add image
                    if (uri != null) {
                        try {
                            Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                            bm = getResizedBitmap(bm, mWidth / 10, mWidth / 10);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            Image myImg = Image.getInstance(stream.toByteArray());
                            myImg.setAlignment(Image.MIDDLE);
                            Paragraph paraImage = new Paragraph();
                            paraImage.add(myImg);
                            preface.add(paraImage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        }
                    }
//                    preface.add(p1);
                    //end
                    // We add one empty line
                    addEmptyLine(preface, 1);
                    // Lets write a big header
                    Paragraph p = new Paragraph(title, catFont);
                    p.setAlignment(Element.ALIGN_CENTER);
                    preface.add(p);
                    addEmptyLine(preface, 1);

                    // Lets write a big header
//                    Paragraph p1 = new Paragraph(date, catFont);
//                    p1.setAlignment(Element.ALIGN_BOTTOM);
//                    preface.add(p1);
//                    // We add one empty line
//                    addEmptyLine(preface, 1);
//                    // Lets write a big header
//                    Paragraph p = new Paragraph(title, catFont);
//                    p.setAlignment(Element.ALIGN_CENTER);
//                    preface.add(p);
//                    addEmptyLine(preface, 1);
                    // Will create: Report generated by: _name, _date
                    preface.add(new Paragraph(content));
                    addEmptyLine(preface, 5);
                    document.add(preface);
                    // Start a new page
                    document.newPage();
                } catch (DocumentException de) {
                    Log.e("PDFCreator", "DocumentException:" + de);
                } finally {
                    try {
                        document.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            else
            {
                Toast.makeText(WriteNoteActivity.this,"please write something before generate pdf",Toast.LENGTH_SHORT).show();
            }
        }
        else {
            NotesTable mNotesTable = new NotesTable((BaseApplication) getApplicationContext());
            ArrayList<NotesModel> mNotesModels = mNotesTable.getAllNotesData();
            if (mNotesModels != null && mNotesModels.size() > 0)
            {
                Document document = new Document();
                try {
                    PdfWriter.getInstance(document, fOut);
                    document.open();
                    for (NotesModel model : mNotesModels
                            ) {
                        Paragraph preface = new Paragraph();
//                        addEmptyLine(preface, 1);
                        String myFormat = "dd-MMM-yy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//                    mEditTextDate.setText("" + sdf.format(notesModel.getmDate() * 1000));
                        Paragraph p1 = new Paragraph(sdf.format(model.getmDate() * 1000), catFont);
                        p1.setAlignment(Element.ALIGN_RIGHT);
                        preface.add(p1);
                        // We add one empty line
//                        addEmptyLine(preface, 1);
                        //Lets add image
                        try {
                            Uri inneruri = Uri.parse(model.getmPhotoUri());
                            Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), inneruri);
                            bm = getResizedBitmap(bm, mWidth / 6, mWidth / 6);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            Image myImg = Image.getInstance(stream.toByteArray());
                            myImg.setAlignment(Image.MIDDLE);
                            Paragraph paraImage = new Paragraph();
//                paraImage.setAlignment(Element.ALIGN_LEFT);
                            paraImage.add(myImg);
                            preface.add(paraImage);
//                document.add(new Paragraph("Hello World2!"));
                            // step 5
//                document.close();

                            Log.d("Suceess", "Sucess");
//                return true;
                        } catch (IOException e) {
                            e.printStackTrace();
//                return false;
                        } catch (DocumentException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
//                return false;
                        }

                        //end
                        // Lets write a big header
//                        String myFormat = "dd-MMM-yy"; //In which you need put here
//                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
////                    mEditTextDate.setText("" + sdf.format(notesModel.getmDate() * 1000));
//                        Paragraph p1 = new Paragraph(sdf.format(model.getmDate() * 1000), catFont);
//                        p1.setAlignment(Element.ALIGN_RIGHT);
//                        preface.add(p1);
//                        // We add one empty line
//                        addEmptyLine(preface, 1);
                        // Lets write a big header
                        Paragraph p = new Paragraph(model.getmTitle(), catFont);
                        p.setAlignment(Element.ALIGN_CENTER);
                        preface.add(p);

                        addEmptyLine(preface, 1);
                        // Will create: Report generated by: _name, _date
                        preface.add(new Paragraph(model.getmContents()));
                        addEmptyLine(preface, 3);
                        document.add(preface);
                        // Start a new page
                        document.newPage();
                    }
                } catch (DocumentException de) {
                    Log.e("PDFCreator", "DocumentException:" + de);
                } finally {
                    try {
                        document.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            else
            {
                Toast.makeText(WriteNoteActivity.this,"please write something before generate pdf",Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void ShowDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.fontsize_dialo);
        dialog.setTitle("Set size!");
        dialog.setCancelable(true);
//there are a lot of settings, for dialog, check them all out!
        dialog.show();

        SeekBar seekbar = (SeekBar) dialog.findViewById(R.id.dialog);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mEditTextContent.setTextSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private static void addContent(Document document) throws DocumentException {
        Anchor anchor = new Anchor("ESTIMATING APP", catFont);
        anchor.setName("ESTIMATING APP");

        // Second parameter is the number of the chapter
        Chapter catPart = new Chapter(new Paragraph(anchor), 1);

        Paragraph subPara = new Paragraph("Subcategory 1", subFont);
        Section subCatPart = catPart.addSection(subPara);
        subCatPart.add(new Paragraph("Hello"));

        subPara = new Paragraph("Subcategory 2", subFont);
        subCatPart = catPart.addSection(subPara);
        subCatPart.add(new Paragraph("Paragraph 1"));
        subCatPart.add(new Paragraph("Paragraph 2"));
        subCatPart.add(new Paragraph("Paragraph 3"));

        // Add a list
        createList(subCatPart);
        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 5);
        subCatPart.add(paragraph);

        // Add a table
        createTable(subCatPart);

        // Now add all this to the document
        document.add(catPart);

        // Next section
        anchor = new Anchor("Second Chapter", catFont);
        anchor.setName("Second Chapter");

        // Second parameter is the number of the chapter
        catPart = new Chapter(new Paragraph(anchor), 1);

        subPara = new Paragraph("Subcategory", subFont);
        subCatPart = catPart.addSection(subPara);
        subCatPart.add(new Paragraph("This is a very important message"));

        // Now add all this to the document
        document.add(catPart);

    }

    private static void addMetaData(Document document) {
        document.addTitle("My first PDF");
        document.addSubject("Using iText");
        document.addKeywords("Java, PDF, iText");
        document.addAuthor("Lars Vogel");
        document.addCreator("Lars Vogel");
    }

    private static void createTable(Section subCatPart)
            throws BadElementException {
        PdfPTable table = new PdfPTable(3);

        // t.setBorderColor(BaseColor.GRAY);
        // t.setPadding(4);
        // t.setSpacing(4);
        // t.setBorderWidth(1);

        PdfPCell c1 = new PdfPCell(new Phrase("Job Name:"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Test 001"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase(""));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        table.setHeaderRows(1);

        table.addCell("Date:");
        table.addCell("1.1");
        table.addCell("");
        table.addCell("Labor Rate:");
        table.addCell("2.2");
        table.addCell("");
        table.addCell("Labor Cost:");
        table.addCell("3.2");
        table.addCell("3.3");

        subCatPart.add(table);

    }

    private static void createList(Section subCatPart) {
        List list = new List(true, false, 10);
        list.add(new ListItem("First point"));
        list.add(new ListItem("Second point"));
        list.add(new ListItem("Third point"));
        subCatPart.add(list);
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    // Method for opening a pdf file
    private void viewPdf(String file, String directory) {

        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
        Uri path = Uri.fromFile(pdfFile);

        // Setting the intent for pdf reader
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(WriteNoteActivity.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (mNotesModel != null) {
            String myFormat = "dd-MMM-yy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            String content = mEditTextContent.getText().toString();
            String title = mEdittextTitle.getText().toString();
            String date = mEditTextDate.getText().toString();
            if (!(content.equalsIgnoreCase(mNotesModel.getmContents())) ||
                    !(title.equalsIgnoreCase(mNotesModel.getmTitle())) || !(date.equalsIgnoreCase(sdf.format(mNotesModel.getmDate() * 1000)))) {
                new AlertDialog.Builder(WriteNoteActivity.this)
                        .setTitle("Save Changes")
                        .setMessage("Do You Want to Save Changes?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                isSaved = true;
                                String ds = mEditTextDate.getText().toString();
                                String cs = mEditTextContent.getText().toString();
                                Log.d("check", "ds " + ds + " cs " + cs);
                                if ((ds != null && !ds.isEmpty()) && (cs != null && !cs.isEmpty())) {
                                    NotesModel notesModel = new NotesModel();
                                    String myFormat = "dd-MMM-yy"; //In which you need put here
                                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                    Date d = null;
                                    try {
                                        d = sdf.parse(mEditTextDate.getText().toString());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
//                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                    df.setTimeZone(TimeZone.getTimeZone("GMT"));
//                    Date d = mCalendar.getTime();
//                    df.format(d);
//                                    Log.d("dateval", " in save miliisec val " + d.getTime() + " date " + date + " year " + new Date(d.getTime()).getYear());

//                timecheck(d.getTime());
                                    NotesTable notesTable = new NotesTable((BaseApplication) getApplicationContext());
                                    if (mNotesModel != null) {
//                        if (notesTable.isRecordExistById(mNotesModel.getId())) {
                                        notesModel.setId(mNotesModel.getId());
                                        notesModel.setmDate(d.getTime() / 1000);
                                        notesModel.setmTitle(mEdittextTitle.getText().toString());
                                        notesModel.setmContents(mEditTextContent.getText().toString());
//                        if(mNotesModel.getmPhotoUri()!=null)
                                        notesModel.setmPhotoUri(selectedImage.toString());
                                        notesTable.updateRecord(notesModel, mNotesModel.getId());
//                        }
                                    } else {
                                        notesModel.setId("" + System.currentTimeMillis() + Math.random());
                                        notesModel.setmDate(d.getTime() / 1000);
                                        notesModel.setmTitle(mEdittextTitle.getText().toString());
                                        notesModel.setmContents(mEditTextContent.getText().toString());
                                        if(selectedImage!=null) {
                                            notesModel.setmPhotoUri(selectedImage.toString());

                                        }
//                        notesModel.setmPhotoUri("anuuri");
                                        notesTable.insertData(notesModel);
                                    }
//                    Log.d("dateval", " without db notesModel.getmDate() " + notesModel.getmDate());

                                    finish();
                                } else {
                                    if (ds == null || ds.isEmpty())
                                        Toast.makeText(WriteNoteActivity.this, "Please select a date", Toast.LENGTH_LONG).show();
                                    else
                                        Toast.makeText(WriteNoteActivity.this, "Please write contents", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                           finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
//                Toast.makeText(WriteNoteActivity.this, "Please save before exit", Toast.LENGTH_SHORT).show();
            } else {
                super.onBackPressed();
            }
        } else {
            if (!mEditTextContent.getText().toString().equals("") ||
                    !mEdittextTitle.getText().toString().equals("") || !mEditTextDate.getText().toString().equals("")) {
                if (isSaved)
                    super.onBackPressed();
                else {
                    Toast.makeText(WriteNoteActivity.this, "Please save before exit", Toast.LENGTH_SHORT).show();

                }
            } else {
                super.onBackPressed();
            }
        }

    }
    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}


