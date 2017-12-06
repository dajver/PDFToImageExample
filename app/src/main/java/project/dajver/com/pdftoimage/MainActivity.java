package project.dajver.com.pdftoimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.dajver.com.pdftoimage.task.PDFToImage;
import project.dajver.com.pdftoimage.task.utils.ImageFilePathUtils;

public class MainActivity extends AppCompatActivity implements PDFToImage.OnPDFToImageConvertedListener {

    @BindView(R.id.image)
    ImageView imageView;

    private int PICKFILE_REQUEST_CODE = 1213;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.image)
    public void onImageClick() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(intent, PICKFILE_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            String fPath = ImageFilePathUtils.getPath(this, data.getData());

            PDFToImage pdfToImage = new PDFToImage(this);
            pdfToImage.setOnPDFToImageConvertedListener(this);
            pdfToImage.execute(new File(fPath));
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onPDFToImageConverted(String imageUri) {
        Bitmap myBitmap = BitmapFactory.decodeFile(new File(imageUri).getAbsolutePath());
        imageView.setImageBitmap(myBitmap);
    }
}
