package project.dajver.com.pdftoimage.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;

import org.vudroid.core.DecodeServiceBase;
import org.vudroid.core.codec.CodecPage;
import org.vudroid.pdfdroid.codec.PdfContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import project.dajver.com.pdftoimage.task.utils.FileUtils;

/**
 * Created by gleb on 12/6/17.
 */

public class PDFToImage extends AsyncTask<File, Void, String> {

    private Context context;
    private OnPDFToImageConvertedListener onPDFToImageConvertedListener;
    private String fileName;

    public PDFToImage(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(File... files) {
        File originFile = files[0];
        fileName = originFile.getName().toLowerCase().replace(".pdf", ".jpeg").replace(".ppt", ".jpeg");
        File filePath = FileUtils.getCacheDir(context);
        File file = new File (filePath, fileName);
        String path = file.getPath();
        if(!new File(path).exists()) {
            try {
                DecodeServiceBase decodeService = new DecodeServiceBase(new PdfContext());
                decodeService.setContentResolver(context.getContentResolver());
                if (originFile.exists()) {
                    decodeService.open(Uri.fromFile(originFile));
                    int pageCount = decodeService.getPageCount();
                    for (int i = 0; i < 1; i++) {
                        CodecPage page = decodeService.getPage(i);
                        RectF rectF = new RectF(0, 0, 1, 1);
                        double scaleBy = Math.min(2480 / (double) page.getWidth(), 3508 / (double) page.getHeight());
                        int with = (int) (page.getWidth() * scaleBy);
                        int height = (int) (page.getHeight() * scaleBy);
                        Bitmap bitmap = page.renderBitmap(with, height, rectF);
                        try {
                            OutputStream outputStream = new FileOutputStream(new File(FileUtils.getCacheDir(context), System.currentTimeMillis() + ".JPEG"));
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            outputStream.close();
                            path = saveImageAndGetURI(bitmap).toString();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return path;
    }

    @Override
    protected void onPostExecute(String uris) {
        onPDFToImageConvertedListener.onPDFToImageConverted(uris);
    }

    private Uri saveImageAndGetURI(Bitmap finalBitmap) {
        File file = new File (FileUtils.getCacheDir(context), fileName);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Uri.parse(file.getPath());
    }

    public void setOnPDFToImageConvertedListener(OnPDFToImageConvertedListener onPDFToImageConvertedListener) {
        this.onPDFToImageConvertedListener = onPDFToImageConvertedListener;
    }

    public interface OnPDFToImageConvertedListener {
        void onPDFToImageConverted(String imageUri);
    }
}