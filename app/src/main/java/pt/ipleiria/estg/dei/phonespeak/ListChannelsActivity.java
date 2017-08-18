package pt.ipleiria.estg.dei.phonespeak;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import pt.ipleiria.estg.dei.phonespeak.executeURL.ListChannelsExecuteURL;
import pt.ipleiria.estg.dei.phonespeak.executeURL.ListOwnPrivateChannelsExecuteURL;
import pt.ipleiria.estg.dei.phonespeak.executeURL.ListOwnPublicChannelsExecuteURL;
import pt.ipleiria.estg.dei.phonespeak.executeURL.ListPublicChannelsExecuteURL;
import pt.ipleiria.estg.dei.phonespeak.model.Channel;
import pt.ipleiria.estg.dei.phonespeak.model.PhoneSpeakManager;
import pt.ipleiria.estg.dei.phonespeak.model.Tag;

public class ListChannelsActivity extends AppCompatActivity {

    private static final String URL_PUBLIC_THINGSPEAK = "https://api.thingspeak.com/channels/public.json";
    private static final String URL_PAGE_SUFIX = "?page=";
    private static final String URL_OWN_THINGSPEAK = "https://api.thingspeak.com/users/";
    private static final String URL_APIKEY_SUFFIX = "?api_key=";
    private static final int FIRST_PAGE = 1;
    private static final String URL_OWN_CHANNELS = "/channels.json";

    //private static final String URL_THINGSPEAK = "https://api.thingspeak.com/channels/public.json?page=800";
    //Para Verificar Quando Não existem canais

    private ListView listViewChannels;
    private int lastPage;
    private int currentPage; // página atual
    private TextView currentPageTextView;
    private ImageButton btnPrev;
    private ImageButton btnNext;
    private ImageButton btnFirst;
    private ImageButton btnLast;
    private TextView txtErrorLog;
    private SearchView searchView;
    public static final int PUBLIC = 0;
    public static final int FAVORITE = 1;
    public static final int OWN = 2;
    private int channels_flag;
    private CheckBox[] checkBoxes;
    private TextView textViewTitle;
    private String username = "";
    private String apiKey = "";
    private LinearLayout layoutSearch;
    private LinearLayout layoutFilters;
    private Button btnAll;
    private boolean allChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_channels);

        initialize();

        checkNetworkState("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_public, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selected = item.getItemId();
        if (selected == R.id.btnMenuSearch) {
            clickBtnSearch();
        } else if (selected == R.id.btnMenuPublic) {
            clickBtnListPublic();
        } else if (selected == R.id.btnMenuFav) {
            clickBtnListFavorite();
        } else if (selected == R.id.btnMenuOwn) {
            clickBtnListOwn();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialize() {
        loadUserData();
        this.currentPage = FIRST_PAGE;
        this.currentPageTextView = (TextView) findViewById(R.id.pageTextView);
        this.btnPrev = (ImageButton) findViewById(R.id.btnPreviousPage);
        this.btnNext = (ImageButton) findViewById(R.id.btnNextPage);
        this.btnFirst = (ImageButton) findViewById(R.id.btnFirstPage);
        this.btnLast = (ImageButton) findViewById(R.id.btnLastPage);
        this.listViewChannels = (ListView) findViewById(R.id.listViewChannels);
        this.txtErrorLog = (TextView) findViewById(R.id.txtErrorLog);
        this.layoutSearch = (LinearLayout) findViewById(R.id.layoutSearch);
        this.layoutSearch.setVisibility(View.GONE);
        this.searchView = (SearchView) findViewById(R.id.searchBar);
        this.checkBoxes= new CheckBox[5];
        this.checkBoxes[0] = (CheckBox) findViewById(R.id.chkBoxId);
        this.checkBoxes[1] = (CheckBox) findViewById(R.id.chkBoxName);
        this.checkBoxes[2] = (CheckBox) findViewById(R.id.chkBoxDescription);
        this.checkBoxes[3] = (CheckBox) findViewById(R.id.chkBoxTags);
        this.checkBoxes[4] = (CheckBox) findViewById(R.id.chkBoxData);
        this.textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        this.layoutSearch = (LinearLayout) findViewById(R.id.layoutSearch);
        this.layoutFilters = (LinearLayout) findViewById(R.id.layoutFilters);
        this.layoutFilters.setVisibility(View.GONE);
        this.textViewTitle.setText(R.string.publicChannels);
        this.btnAll = (Button) findViewById(R.id.btnAll);
        this.btnAll.setVisibility(View.GONE);
        this.allChecked = true;

        this.channels_flag = PUBLIC;

        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchChannels(getSelectedList(), query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchChannels(getSelectedList(), newText);
                return true;
            }
        });

    }

    public void onClickItemChannel(View view) {
        Intent intent = new Intent(ListChannelsActivity.this, ChannelDetailsActivity.class);
        TextView id = (TextView) view.findViewById(R.id.textViewID);
        intent.putExtra("id", id.getText());
        if (PhoneSpeakManager.INSTANCE.isFavorite(Integer.parseInt(id.getText().toString()))) {
            intent.putExtra("mode", "FAVORITE");
        } else {
            intent.putExtra("mode", "PUBLIC");
        }

        startActivity(intent);
    }

    public void loadUserData() {
        SharedPreferences sPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        username = !sPref.getString("usernameKey", "").isEmpty() ? sPref.getString("usernameKey", "") : "";
        apiKey = !sPref.getString("apiKey", "").isEmpty() ? sPref.getString("apiKey", "") : "";
    }


    private LinkedList<Channel> getSelectedList() {
        LinkedList<Channel> aux = new LinkedList<>();
        switch (channels_flag) {
            case PUBLIC:
                aux = PhoneSpeakManager.INSTANCE.getChannels(PhoneSpeakManager.ChannelsLists.PUBLIC);
                break;
            case FAVORITE:
                aux = PhoneSpeakManager.INSTANCE.getChannels(PhoneSpeakManager.ChannelsLists.FAV);
                break;
            case OWN:
                aux = PhoneSpeakManager.INSTANCE.getChannels(PhoneSpeakManager.ChannelsLists.OWN_PRIVATE);
                break;
        }
        return aux;
    }

    private void searchChannels(LinkedList<Channel> aux, String string) {

        if(!string.isEmpty())
        {
            txtErrorLog.setText("");
            LinkedList<Channel> foundChannels = new LinkedList<>();

            for (Channel c : aux) {
                if (checkBoxes[0].isChecked()) {
                    if (String.valueOf(c.getId()).contains(string)) {
                        if (!foundChannels.contains(c)) {
                            foundChannels.add(c);
                        }
                    }

                }
                if (checkBoxes[1].isChecked()) {
                    if (c.getName().contains(string)) {
                        if (!foundChannels.contains(c))

                        {
                            foundChannels.add(c);
                        }
                    }
                }
                if (checkBoxes[2].isChecked()) {
                    if (c.getDescription().contains(string)) {
                        if (!foundChannels.contains(c)) {
                            foundChannels.add(c);
                        }
                    }
                }
                if (checkBoxes[3].isChecked()) {
                    for (Tag tag : c.getTags()) {
                        if (tag.getName().contains(string)) {
                            if (!foundChannels.contains(c)) {
                                foundChannels.add(c);
                                break;
                            }
                        }
                    }
                }

                if (checkBoxes[4].isChecked()) {
                    try {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Calendar date = Calendar.getInstance();
                        date.setTime(df.parse(string));
                        if (c.getCreatedAt().get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH)
                                && c.getCreatedAt().get(Calendar.MONTH) == date.get(Calendar.MONTH)
                                && c.getCreatedAt().get(Calendar.YEAR) == date.get(Calendar.YEAR)) {
                            if (!foundChannels.contains(c)) {
                                foundChannels.add(c);
                            }
                        }
                    }catch (ParseException ignored) {}
                    try{
                        int datePart = Integer.parseInt(string);
                        Log.d(DashBoardActivity.DEBUGTAG, datePart+"");
                        int dayOfMonth = c.getCreatedAt().get(Calendar.DAY_OF_MONTH);
                        if(dayOfMonth == datePart){
                            if (!foundChannels.contains(c)) {
                                Log.d(DashBoardActivity.DEBUGTAG, dayOfMonth+"");
                                foundChannels.add(c);
                            }
                        }
                        int month = c.getCreatedAt().get(Calendar.MONTH)+1;

                        if(month == datePart){
                            if (!foundChannels.contains(c)) {
                                Log.d(DashBoardActivity.DEBUGTAG, month+"");
                                foundChannels.add(c);
                            }
                        }

                        int year = c.getCreatedAt().get(Calendar.YEAR);
                        if(year == datePart){
                            if (!foundChannels.contains(c)) {
                                Log.d(DashBoardActivity.DEBUGTAG, year+"");
                                foundChannels.add(c);
                            }
                        }
                    }catch (NumberFormatException ignored){}

                }
/*
            if (!checkBoxes[0].isChecked() && !checkBoxes[1].isChecked()
                    && !checkBoxes[2].isChecked() && !checkBoxes[3].isChecked()) {
                //Se nenhuma checkbox estiver selecionada, faz a pesquisa pelo id, nome, descrição e tags do canal
                boolean flag = false;
                for (Tag tag : c.getTags()) {
                    if (tag.getName().contains(string)) {
                        flag = true;

                    }
                }
                if (String.valueOf(c.getId()).contains(string) || c.getName().contains(string) ||
                        c.getDescription().contains(string) || flag) {
                    if (!foundChannels.contains(c))

                    {
                        foundChannels.add(c);
                    }
                }
            }*/
            }

            if (foundChannels.isEmpty()) {
                setError(getString(R.string.NoChannelsFound));
            }

            updateChannelsList(foundChannels);
        } else {
            //Caso a pesquisa fique vazia, volta a devolver todos os canais públicos dessa página
            updateChannelsList(PhoneSpeakManager.INSTANCE.getChannels(PhoneSpeakManager.ChannelsLists.PUBLIC));
        }

    }

    public void updateChannelsList(LinkedList<Channel> channels) {

        ChannelAdapter adapter = new ChannelAdapter(this, channels);
        listViewChannels.setAdapter(adapter);
        currentPageTextView.setText(String.valueOf(currentPage));
        toggleButtons();

    }

    private void checkNetworkState(String sufix) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Consome Web Service

            if (!username.isEmpty()) {
                Log.d(DashBoardActivity.DEBUGTAG, "NETWORK OK - GET OWN CHANNELS");
                String url = URL_OWN_THINGSPEAK + username + URL_OWN_CHANNELS;
                Log.d(DashBoardActivity.DEBUGTAG, "URL: " + url);

                new ListOwnPublicChannelsExecuteURL(this).execute(url);
                if (!apiKey.isEmpty()) {
                    new ListOwnPrivateChannelsExecuteURL(this).execute(url + URL_APIKEY_SUFFIX + apiKey);
                }
            }
            Log.d(DashBoardActivity.DEBUGTAG, "NETWORK OK - GET PUBLIC CHANNELS");
            new ListPublicChannelsExecuteURL(this).execute(URL_PUBLIC_THINGSPEAK + sufix);
        } else {
            // Apresenta mensagem "Não há ligação à Rede"
            Log.d(DashBoardActivity.DEBUGTAG, "NO NETWORK");
            setError(getString(R.string.NoNetwork));
            toggleButtons();
        }
    }

    public void switchPage(int page)
    {
        disableButtons();
        currentPage = page;
        checkNetworkState(URL_PAGE_SUFIX + currentPage);
    }


    public void onClickBtnPreviousPage(View view) {
        if (currentPage <= FIRST_PAGE) {
            return;
        }

        currentPage--;
        switchPage(currentPage--);


    }

    public void onClickBtnNextPage(View view) {
        if (currentPage >= lastPage) {
            return;
        }
        currentPage++;
        switchPage(currentPage++);


    }


    public void onClickBtnFirstPage(View view) {
        switchPage(FIRST_PAGE);
    }

    public void onClickBtnLastPage(View view) {

        switchPage(lastPage);
    }

    public void toggleButtons() {
        if (currentPage == FIRST_PAGE) {
            btnPrev.setClickable(false);
            btnFirst.setClickable(false);
        } else {
            btnPrev.setClickable(true);
            btnFirst.setClickable(true);
        }

        if (currentPage == lastPage) {
            btnNext.setClickable(false);
            btnLast.setClickable(false);
        } else {
            btnNext.setClickable(true);
            btnLast.setClickable(true);
        }

    }

    private void disableButtons() {
        btnFirst.setClickable(false);
        btnPrev.setClickable(false);
        btnLast.setClickable(false);
        btnNext.setClickable(false);
    }

    public void onClickBtnFav(View view) {
        ImageButton btn = (ImageButton) view.findViewById(R.id.btnFav);
        int id = Integer.parseInt(btn.getTag().toString());
        Channel channel = PhoneSpeakManager.INSTANCE.getChannelById(id);

        if (channel == null) {
            channel = PhoneSpeakManager.INSTANCE.getFavChannelById(id);
        }

        if (!PhoneSpeakManager.INSTANCE.isFavorite(id)) {
            btn.setImageResource(R.drawable.ic_action_fav);
            PhoneSpeakManager.INSTANCE.addChannel(channel, PhoneSpeakManager.ChannelsLists.FAV);
        } else {
            //If user is in Favorites List
            if (channels_flag == FAVORITE) {
                final Channel channelParam = channel;
                new AlertDialog.Builder(view.getContext())
                        .setTitle(R.string.delete_fav_channel_title)
                        .setMessage(R.string.delete_fav_channel_confirm)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                PhoneSpeakManager.INSTANCE.removeChannel(channelParam, PhoneSpeakManager.ChannelsLists.FAV);
                                updateChannelsList(PhoneSpeakManager.INSTANCE.getChannels(PhoneSpeakManager.ChannelsLists.FAV));
                                checkFavoriteChannelsEmpty();

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        }).setIcon(R.drawable.ic_action_warning).show();
            } else {
                btn.setImageResource(R.drawable.ic_action_not_fav);
                PhoneSpeakManager.INSTANCE.removeChannel(channel, PhoneSpeakManager.ChannelsLists.FAV);
            }

        }
    }

    private void clickBtnListPublic() {
        if (channels_flag != PUBLIC) {
            updateChannelsList(PhoneSpeakManager.INSTANCE.getChannels(PhoneSpeakManager.ChannelsLists.PUBLIC));
            textViewTitle.setText(R.string.publicChannels);
            channels_flag = PUBLIC;
            setError(getString(R.string.empty_text));
            toggleButtonsVisibility();

        }
    }

    private void clickBtnListFavorite() {
        if (channels_flag != FAVORITE) {
            updateChannelsList(PhoneSpeakManager.INSTANCE.getChannels(PhoneSpeakManager.ChannelsLists.FAV));
            textViewTitle.setText(R.string.favoriteChannels);
            channels_flag = FAVORITE;
            toggleButtonsVisibility();
            setError(getString(R.string.empty_text));
            checkFavoriteChannelsEmpty();
        }
    }

    private void clickBtnListOwn() {
        if (!txtErrorLog.getText().toString().equals(getString(R.string.NoNetwork))) {
            if (channels_flag != OWN) {
                channels_flag = OWN;


                if (username.isEmpty() && apiKey.isEmpty()) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.defineUsernameTitle)
                            .setMessage(R.string.defineUsernameConfirmation)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(ListChannelsActivity.this, AccountActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        Intent intent = new Intent(ListChannelsActivity.this, ListChannelsActivity.class);
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        Toast.makeText(ListChannelsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setIcon(R.drawable.ic_action_warning)
                            .show();
                } else {
                    textViewTitle.setText(R.string.ownChannels);
                    if (PhoneSpeakManager.INSTANCE.getChannels(PhoneSpeakManager.ChannelsLists.OWN_PUBLIC).isEmpty() &&
                            PhoneSpeakManager.INSTANCE.getChannels(PhoneSpeakManager.ChannelsLists.OWN_PRIVATE).isEmpty()) {
                        listViewChannels.setAdapter(null);
                        setError(getString(R.string.not_have_channels_to_show));
                    } else if (!username.isEmpty() && apiKey.isEmpty()) {
                        setError(getString(R.string.see_private_channels));

                        updateChannelsList(PhoneSpeakManager.INSTANCE.getChannels(PhoneSpeakManager.ChannelsLists.OWN_PUBLIC));
                    } else if (PhoneSpeakManager.INSTANCE.isUserDataVerified()) {
                        if (!username.isEmpty() && !apiKey.isEmpty()) {
                            updateChannelsList(PhoneSpeakManager.INSTANCE.getOwnChannels());
                        }
                       
                        toggleButtonsVisibility();
                        checkOwnChannelsEmpty();
                        setError(getString(R.string.empty_text));
                    } else {
                        listViewChannels.setAdapter(null);
                        setError(getString(R.string.verify_account_user_and_api_not_match));
                    }

                }
            }
        }
    }

    private void checkFavoriteChannelsEmpty() {

        if (PhoneSpeakManager.INSTANCE.getChannels(PhoneSpeakManager.ChannelsLists.FAV).isEmpty() && channels_flag == FAVORITE) {
            setError(getString(R.string.dont_have_favorite_channels));
        } else {
            setError(getString(R.string.empty_text));
        }
    }

    private void toggleButtonsVisibility() {

        if (channels_flag == PUBLIC) {
            btnFirst.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
            btnPrev.setVisibility(View.VISIBLE);
            btnLast.setVisibility(View.VISIBLE);
            currentPageTextView.setVisibility(View.VISIBLE);
        } else {
            btnFirst.setVisibility(View.INVISIBLE);
            btnNext.setVisibility(View.INVISIBLE);
            btnPrev.setVisibility(View.INVISIBLE);
            btnLast.setVisibility(View.INVISIBLE);
            currentPageTextView.setVisibility(View.INVISIBLE);

        }

        setError(getString(R.string.empty_text));


    }

    private void checkOwnChannelsEmpty() {
        if (PhoneSpeakManager.INSTANCE.getChannels(PhoneSpeakManager.ChannelsLists.OWN_PRIVATE).isEmpty() && channels_flag == OWN) {
            setError(getString(R.string.dont_have_own_channels));
        } else {
            setError(getString(R.string.empty_text));
        }
    }

    private void clickBtnSearch() {
        if (layoutSearch.getVisibility() == View.GONE) {
            layoutSearch.setVisibility(View.VISIBLE);
        } else {
            layoutSearch.setVisibility(View.GONE);
        }
    }

    public void onClickChk(View view)
    {
        boolean noneChecked = true;
        if(checkBoxes[0].isChecked() && checkBoxes[1].isChecked() && checkBoxes[2].isChecked() && checkBoxes[3].isChecked() && checkBoxes[4].isChecked())
        {
            allChecked = true;
        }
        for(CheckBox c : checkBoxes){
            if(c.isChecked()){
                noneChecked = false;
            }
            if(!c.isChecked())
            {
                allChecked = false;
            }

        }
        if(noneChecked){
            ((CheckBox) view).setChecked(true);
            setError(getString(R.string.at_least_one_filter));
        }

        if (checkBoxes[4].isChecked()) {
            searchView.setQueryHint(getString(R.string.date_hint));

        } else {
            searchView.setQueryHint(getString(R.string.empty_text));
        }

        LinkedList<Channel> aux = getSelectedList();
        if (searchView.getQuery().toString().isEmpty()) {
            updateChannelsList(aux);
        } else {
            searchChannels(aux, searchView.getQuery().toString());
        }

        if(!allChecked)
        {
            btnAll.setVisibility(View.VISIBLE);
        }
        else{
            btnAll.setVisibility(View.GONE);
        }
    }

    public void setError(String s) {
        txtErrorLog.setText(s);
        txtErrorLog.setVisibility(View.VISIBLE);
        clearError();
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public void onClickToggleFilters(View view)
    {
        Drawable drawable = ((ImageButton) view).getDrawable();
        if(drawable.getConstantState() == ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_show_filters, null).getConstantState())
        {
            layoutFilters.setVisibility(View.VISIBLE);
            if(!allChecked)
            {
                btnAll.setVisibility(View.VISIBLE);
            }

            ((ImageButton) view).setImageResource(R.drawable.ic_action_hide_filters);
        }
        else {
            layoutFilters.setVisibility(View.GONE);
            btnAll.setVisibility(View.GONE);
            ((ImageButton) view).setImageResource(R.drawable.ic_action_show_filters);
        }

    }
    private void clearError(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtErrorLog.setText(R.string.empty_text);
                        txtErrorLog.setVisibility(View.GONE);
                    }
                });
            }
        }, 5000);
    }

    public void onClickBtnAll(View view) {
        for(CheckBox c : checkBoxes)
        {
            c.setChecked(true);

        }
        allChecked = true;
        btnAll.setVisibility(View.GONE);
    }
}

