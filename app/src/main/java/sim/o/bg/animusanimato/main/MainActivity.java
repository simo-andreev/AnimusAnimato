package sim.o.bg.animusanimato.main;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import sim.o.bg.animusanimato.R;
import sim.o.bg.animusanimato.model.Client;

public class MainActivity extends Activity {
    public static ColorDrawable mDefaultBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mDefaultBack == null)
            mDefaultBack = new ColorDrawable(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);

        RecyclerView recycler = findViewById(R.id.activity_main_recycler);
        recycler.setAdapter(new RecyclerView.Adapter<ClientHolder>() {
            final ArrayList<Client> clients;

            {
                String nameBase, urlBase;
                nameBase = "Testomass Testovski №";
                urlBase = "http://via.placeholder.com/240x240&text=№";

                clients = new ArrayList<>();
                for (int i = 0; i < 100_000; i++) {
                    try {
                        clients.add(new Client(nameBase + i, i, new URL(urlBase + i)));
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public ClientHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ClientHolder(getLayoutInflater().inflate(R.layout.row_image_title_details, parent, false));
            }

            @Override
            public void onBindViewHolder(ClientHolder holder, int position) {
                try {
                    holder.bind(clients.get(position));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public int getItemCount() {
                return clients.size();
            }
        });
    }

    private class ClientHolder extends RecyclerView.ViewHolder {
        private final ImageView mPhoto;
        private final TextView mTitle;
        private final TextView mDetails;
        private AsyncImageRetriever mImageRetriever;

        ClientHolder(View itemView) {
            super(itemView);

            mPhoto = itemView.findViewById(R.id.row_image_title_details_image);
            mTitle = itemView.findViewById(R.id.row_image_title_details_title);
            mDetails = itemView.findViewById(R.id.row_image_title_details_details);
        }

        private void bind(Client client) throws IOException {
            if (mImageRetriever != null)
                mImageRetriever.cancel(true);

            mPhoto.setImageDrawable(mDefaultBack);

            mImageRetriever = new AsyncImageRetriever(mPhoto);
            mImageRetriever.execute(client.getPhotoURL());

            mTitle.setText(client.getName());
            mDetails.setText(client.getDescription());
        }
    }
}

class AsyncImageRetriever extends AsyncTask<URL, Void, Bitmap>{

    private final WeakReference<ImageView> mTargetView;

    AsyncImageRetriever(ImageView targetView) {
        this.mTargetView = new WeakReference<>(targetView);
    }

    @Override
    protected Bitmap doInBackground(URL... urls) {
        try {
            Thread.sleep(10);

            if (isCancelled()) return null;

            return BitmapFactory.decodeStream(urls[0].openConnection().getInputStream());
        } catch (IOException|InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onCancelled() {
        if (mTargetView.get() != null)
            mTargetView.get();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (mTargetView.get() != null)
            mTargetView.get().setImageBitmap(bitmap);
    }
}

