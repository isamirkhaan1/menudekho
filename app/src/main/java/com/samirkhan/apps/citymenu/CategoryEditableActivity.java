package com.samirkhan.apps.citymenu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import custom.ExceptionHandling;
import custom.PreferencesFile;
import datalayer.LocalDataManager;
import datalayer.PostRequestData;
import datalayer.Updates;
import datalayer.UrlString;

public class CategoryEditableActivity extends AppCompatActivity {

    String mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_editable);
        getSupportActionBar().setTitle(R.string.title_category_activity);
        final Activity mActivity = this;

        PreferencesFile preferencesFile = new PreferencesFile(this);
        final int restId = preferencesFile.restId();
        if (restId == preferencesFile.DEFAULT_REST_ID) {
            Snackbar.make(findViewById(android.R.id.content), "Please Login to the Restaurant Account.",
                    Snackbar.LENGTH_SHORT).show();
            finish();
        }

        ListView list = (ListView) findViewById(R.id.list_category_editable);
        String keyword = handleSearch(getIntent());
        if (keyword == null) {
            mQuery = "SELECT name FROM category WHERE rest_id =" + restId + " ORDER BY name";
        } else if (keyword.equals("")) {
            Snackbar.make(findViewById(android.R.id.content), "Please Login to the Restaurant Account.",
                    Snackbar.LENGTH_SHORT).show();
            finish();
        } else {
            mQuery = "SELECT name FROM category WHERE name LIKE '%" + keyword + "%'" + "ORDER BY name";
        }

         ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_category,
         new LocalDataManager(this).getSingleColumn(mQuery));

        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String categoryOldName = ((TextView) view).getText().toString();

                // finish this..
                LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
                View viewDialog = layoutInflater.inflate(R.layout.dialog_edit_category, null);

                final EditText editText = (EditText) viewDialog.findViewById(R.id.text_dialog_edit_category);
                editText.setText(categoryOldName);

                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setView(viewDialog);
                builder.setNeutralButton(R.string.action_back, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
           /*     builder.setNegativeButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        AlertDialog.Builder alertDelete = new AlertDialog.Builder(mActivity);
                        alertDelete.setTitle(R.string.title_delete);
                        alertDelete.setMessage("\n Do you want to delete the category? \n \n");
                        alertDelete.setNeutralButton(R.string.action_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogDelete, int which) {
                                dialogDelete.dismiss();
                            }
                        });
                        alertDelete.setPositiveButton(R.string.action_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogDelete, int which) {
                                mQuery = "DELETE FROM category where name = '" + categoryOldName + "' and restaurant_id = " + restId;
                                Boolean isUpdated = LocalDataManager.updateRow(getBaseContext(), mQuery);
                                if (isUpdated) {
                                    Toast.makeText(getBaseContext(), "The Category has been Deleted.", Toast.LENGTH_SHORT).show();
                                    mActivity.finish();
                                    mActivity.startActivity(mActivity.getIntent());
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getBaseContext(), "Sorry, Couldn't Delete the Category.", Toast.LENGTH_SHORT).show();
                                }
                                dialogDelete.dismiss();
                            }
                        });
                        alertDelete.show();
                    }
                });*/
                builder.setPositiveButton(R.string.action_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().toString().trim().length() < 3) {
                            PromptDialog promptDialog =
                                    new PromptDialog(mActivity, "Category Name", "Category Name must be larger than 3 characters.", R.string.action_got_it);
                            promptDialog.show(getFragmentManager(), "edit_cat");
                        } else {
                            mQuery = "SELECT id FROM category WHERE name = '" + categoryOldName + "'";
                            int id = Integer.parseInt(new LocalDataManager(getBaseContext()).getSingleColumn(mQuery).get(0));
                            UpdateCategoryService service = new UpdateCategoryService(CategoryEditableActivity.this,
                                    editText.getText().toString().trim(), id, restId);
                            service.execute();
                            dialog.dismiss();
                        }

                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.category_editable, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_category_editable_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_category_editable_add) {
            CategoryAddDialog dialog = new CategoryAddDialog(this);
            dialog.show(getFragmentManager(), "add_cat");
        }
        return super.onOptionsItemSelected(item);
    }

    public String handleSearch(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String keyword = intent.getStringExtra(SearchManager.QUERY);
            return keyword.trim();
        }
        return null;
    }
}

class UpdateCategoryService extends AsyncTask {

    Context context;
    ;
    ProgressDialog progressDialog;

    String updateName, userMessage;
    int id, restId;

    public UpdateCategoryService(Context context, String updatedName,  int id, int restId) {
        this.context = context;
        this.updateName = updatedName;
        this.id = id;
        this.restId = restId;
        progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setMessage("Updating category..");
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        URL url;
        HttpURLConnection connection;
        String urlString = UrlString.RootUrl() + "category.php";
        HashMap<String, String> parameters = new HashMap<>();
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setReadTimeout(10000);
            OutputStream os = connection.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            parameters.put("action", "UPDATE");
            parameters.put("id", id + "");
            parameters.put("name", updateName);
            parameters.put("desc", "desc");
            parameters.put("rest_id", restId + "");

            bw.write(PostRequestData.getData(parameters));
            bw.flush();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line, responseData = "";
                while ((line = br.readLine()) != null) {
                    responseData = responseData + line;
                }

                JSONArray jsonArray = new JSONArray(responseData);
                JSONObject obj = jsonArray.getJSONObject(0);
                switch (obj.getString("response")) {
                    case "success":
                        userMessage = "Category has been updated successfully";
                        LocalDataManager manager = new LocalDataManager(context);
                        String query = "UPDATE category SET name = '" + updateName + "' WHERE id = " +
                                id + " AND rest_id = " + restId;
                        new LocalDataManager(context).updateRow(query);

                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Updates updates = new Updates(context, false);
                                updates.execute();
                            }
                        });
                        break;
                    case "error":
                        userMessage = "Error occured while updating profile..";
                        throw new Exception(ExceptionHandling.SERVER_RESPONSE_EXCEPTION);
                }
            } else {
                throw new Exception(ExceptionHandling.SERVER_RESPONSE_CODE_NOT_OK);
            }
            os.close();
            bw.close();
        } catch (MalformedURLException e) {
            userMessage = "Please check the connectivity..";
            e.printStackTrace();
        } catch (IOException e) {
            userMessage = "Please check the connectivity..";
            e.printStackTrace();
        } catch (JSONException e) {
            userMessage = "Json error occured while updating category.";
            e.printStackTrace();
        } catch (Exception e) {
            userMessage = e.getMessage();
        } finally {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.hide();
                    Toast.makeText(context, userMessage, Toast.LENGTH_SHORT).show();
                    ((Activity) context).finish();
                    ((Activity) context).startActivity(((Activity) context).getIntent());

                }
            });
        }
        return null;
    }
}
