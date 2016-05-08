package net.tebyan.filesharingapp.classes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import net.tebyan.filesharingapp.R;
import net.tebyan.filesharingapp.activities.MainActivity;
import net.tebyan.filesharingapp.model.FileUploadInput;
import net.tebyan.filesharingapp.model.FileUploadResultModel;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class DataProvider {
    public static class UploadFileTask extends AsyncTask<FileUploadInput, Integer, String> {
        AppCompatActivity activity;
        ProgressDialog dialog;

        public UploadFileTask(AppCompatActivity activity) {
            this.activity = activity;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            if (dialog == null)
                dialog = new ProgressDialog(activity);
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMax(100);
            dialog.setTitle(R.string.uploading);
            dialog.show();

        }

        protected String doInBackground(FileUploadInput... input) {
            File file = new File(input[0].url);
            String userToken = Application.getToken(activity);
            String folder = Application.CurrentFolder;
            StringBuilder stringBuilder = new StringBuilder();
            String url = WebserviceUrl.UploadServiceUrl + "?folder=" + folder.trim() + "&filename=" + file.getName().trim();

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);
                FileEntity reqEntity = new FileEntity(file, "binary/octet-stream") {
                    @Override
                    public void writeTo(OutputStream outstream) throws IOException {
                        super.writeTo(new CoutingOutputStream(activity, outstream, file.length()));
                    }
                };
                reqEntity.setContentType("binary/octet-stream");
                reqEntity.setChunked(true);
                if (userToken != null) {
                    httppost.setHeader("userToken", userToken);
                }
                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                    InputStream inputStream = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    while (true) {
                        String line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        stringBuilder.append(line);
                    }
                    inputStream.close();
                    Gson gson = new Gson();
                    return stringBuilder.toString();//gson.from(stringBuilder.toString(),);
                } else if (statusCode == HttpStatus.SC_FORBIDDEN) {
                    return null;
                } else {
                    return null;
                }
            } catch (Exception e2) {
                Log.e("UPLOAD FILE", e2.getMessage() + " ");
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            dialog.setProgress(values[0]);
        }

        protected void onPostExecute(String json) {
           /* dialog.dismiss();
            if (json != null && json.length() > 0) {
                FileUploadResultModel result = new Gson().fromJson(json, FileUploadResultModel.class);
                try {
                    if (result != null) {
                        if (result.Error == null) {
                            if (result.Data != null && !result.Data.FileID.equals(""))
                                //Toast.makeText(activity, "[index : " + activity.indexInPhotos + "] file uploaded", Toast.LENGTH_SHORT).show();
                                Toast.makeText(activity, R.string.upload_completed, Toast.LENGTH_SHORT).show();
                            else {
                                Toast.makeText(activity, R.string.network_connection_fail, Toast.LENGTH_SHORT).show();
                                //Toast.makeText(activity, "[index : " + activity.indexInPhotos + "] file NOT uploaded", Toast.LENGTH_SHORT).show();
                            }
                            super.onPostExecute(json);
                        } else {
*//*
                            Toast.makeText(activity, "[index : " + activity.indexInPhotos + "] Error : " + result.Error.ErrorMessage, Toast.LENGTH_SHORT).show();
*//*
                        }
                       if (activity.indexInPhotos > 0) {
                            activity.indexInPhotos--;
                            activity.uploadPic(activity.uploadFiles, activity.indexInPhotos);
                        } else {
                            Toast.makeText(activity, R.string.upload_completed, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                }
            }*/
        }

        class CoutingOutputStream extends FilterOutputStream {
            long uploaded;
            long size;

            AppCompatActivity activity;

            CoutingOutputStream(AppCompatActivity activity, final OutputStream out, long size) {
                super(out);
                this.size = size;
                this.activity = activity;
                uploaded = 0;
            }

            @Override
            public void write(int b) throws IOException {
                uploaded += 1;
                publishProgress((int) (((float) uploaded / size) * 100));
                out.write(b);
            }

            @Override
            public void write(byte[] b) throws IOException {
                uploaded += b.length;
                publishProgress((int) (((float) uploaded / size) * 100));
                out.write(b);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                uploaded += len;
                publishProgress((int) (((float) uploaded / size) * 100));
                out.write(b, off, len);
            }
        }
    }

}
