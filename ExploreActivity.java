package com.selebrety.app.explore;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.selebrety.app.R;
import com.selebrety.app.database.SelebretyDatabaseAccessObject;
import com.selebrety.app.internetconnectivity.ConnectionDetector;
import com.selebrety.app.invite.SelebretySignup;
import com.selebrety.app.prefrence.SharedPref;
import com.selebrety.app.session.NetworkUrls;
import com.selebrety.app.tutorial.ExploreTutorialActivity;
import com.selebrety.app.video.NetworkAccessUtils;

public class ExploreActivity extends Fragment implements OnClickListener{

	private boolean IS_KISS_LAYOUT = true;

	ThreadPoolExecutor mSingleThreadExecutor = new ThreadPoolExecutor(
			1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	private ConnectionDetector cd;
	private ImageView _channelBtn = null;
	private ImageView _kissBtn = null;
	private ImageView _popularBtn = null;

	private ImageView _selector = null;
	private TextView _tvExploreChannel = null;
	private TextView _tvMusicChannel = null;
	private TextView _tvSportsChannel = null;
	private TextView _tvMoviesChannel = null;
	private TextView _tvModelsChannel = null;
	private TextView _tvBrandsChannel = null;
	private TextView _tvExecutivesChannel = null;
	private TextView _tvLocalBusinessChannel = null;
	private TextView _tvPoliticiansChannel = null;
	private TextView _tvFaithChannel = null;
	private TextView _tvArtistsChannel = null;
	private TextView _tvAnimalChannel = null;
	private TextView _tvOtherInfluChannel = null;

	private RelativeLayout _channelMainLayout = null;
	private LinearLayout _kissMainLayout = null;
	private LinearLayout _loadingLayout = null;

	private LinearLayout _musicBtn = null;
	private LinearLayout _sportsBtn = null;
	private LinearLayout _moviesBtn = null;
	private LinearLayout _modelsBtn = null;
	private LinearLayout _brandsBtn = null;
	private LinearLayout _executivesBtn = null;
	private LinearLayout _localBusinessBtn = null;
	private LinearLayout _politiciansBtn = null;
	private LinearLayout _faithBtn = null;
	private LinearLayout _artistsBtn = null;
	private LinearLayout _animalBtn = null;
	private LinearLayout _otherInfluBtn = null;

	private TextView _recentTabBtn = null;
	private TextView _popularTabBtn = null;

	private ListView _listViewKissRecent = null;
	private ListView _listViewKissPopular = null;
	private ListView _listViewBuzzRecent = null;
	private ListView _listViewBuzzPopular = null;

	public static String KEY_MUSIC = "Music";
	public static String KEY_SPORTS = "Sports";
	public static String KEY_CINEMA = "Cinema";
	public static String KEY_MODELS = "Models";
	public static String KEY_BRANDS = "Brands";
	public static String KEY_EXECUTIVES = "Executives";
	public static String KEY_LOCAL_BUSINESS = "Business";
	public static String KEY_POLITICIANS = "Politicians";
	public static String KEY_FAITH = "Faith";
	public static String KEY_ARTISTS = "Artists";
	public static String KEY_ANIMAL = "Animal";
	public static String KEY_OTHER_INFLU = "otherinfluances";	

	private int KEY_DAY_RECENT = 3;
	private int KEY_DAY_POPULAR_NOW = 1;

	private int _kissType = KEY_DAY_POPULAR_NOW;
	private int _popularType = KEY_DAY_RECENT;

	private ArrayList<KissBean> kisslistRecentItem = new ArrayList<KissBean>();
	private ArrayList<BuzzGiftsBean> hashlistRecentItem = new ArrayList<BuzzGiftsBean>();
	private ArrayList<KissBean> kisslistPopularItem = new ArrayList<KissBean>();
	private ArrayList<BuzzGiftsBean> hashlistPopularItem = new ArrayList<BuzzGiftsBean>();
	ArrayList<String> hashGiftRecentcode = new ArrayList<String>();
	ArrayList<String> hashGiftRecentcount = new ArrayList<String>();
	ArrayList<String> hashGiftPopularcode = new ArrayList<String>();
	ArrayList<String> hashGiftPopularcount = new ArrayList<String>();
	int maxKissGiftRecentCount=0;
	int minKissGiftRecentCount=Integer.MAX_VALUE;
	int maxhashGiftRecentCount=0;
	int minhashGiftRecentCount=Integer.MAX_VALUE;
	int maxKissGiftPopularCount=0;
	int minKissGiftPopularCount=Integer.MAX_VALUE;
	int maxhashGiftPopularCount=0;
	int minhashGiftPopularCount=Integer.MAX_VALUE;
	int blankWeightHashRecent=0;
	int tvWeightHashRecent=0;
	int blankWeightHashPopular=0;
	int tvWeightHashPopular=0;
	private CustomKissListAdapter listadapter;
	private Typeface typeFaceNormal = null;
	private Typeface typefaceBold = null;
	String value;

	private EasyTracker easyTracker = null;

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(ExploreActivity.this.getActivity()).activityStart(ExploreActivity.this.getActivity());
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(ExploreActivity.this.getActivity()).activityStop(ExploreActivity.this.getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {	
		final View view = inflater.inflate(R.layout.explore_activity, container, false);
		value=SharedPref.getSharedPrefData(getActivity(), SharedPref.PREFRENCE_KEY_CHECK_EXPLORE_TUTORIAL);
		Log.e("value", value);
		
		
		easyTracker = EasyTracker.getInstance(ExploreActivity.this.getActivity());
		
		return getViews(view);
	}


	private View getViews(View view) {
		
		typeFaceNormal = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/bariol_regular-webfont.ttf");
		typefaceBold = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/bariol_bold-webfont.ttf");

		_listViewKissRecent = (ListView)view.findViewById(R.id.listView_kiss_recent);
		_listViewKissPopular = (ListView)view.findViewById(R.id.listView_kiss_popular);
		_listViewBuzzRecent = (ListView)view.findViewById(R.id.listView_buzz_recent);
		_listViewBuzzPopular = (ListView)view.findViewById(R.id.listView_buzz_popular);
		
		_tvExploreChannel = (TextView) view.findViewById(R.id.channelsTXt);
		_tvMusicChannel = (TextView) view.findViewById(R.id.musictext);
		_tvSportsChannel = (TextView) view.findViewById(R.id.sportstext);
		_tvMoviesChannel = (TextView) view.findViewById(R.id.cinematext);
		_tvModelsChannel = (TextView) view.findViewById(R.id.modelstext);
		_tvBrandsChannel = (TextView) view.findViewById(R.id.brandstext);
		_tvExecutivesChannel = (TextView) view.findViewById(R.id.executivetext);
		_tvLocalBusinessChannel = (TextView) view.findViewById(R.id.localtext);
		_tvPoliticiansChannel = (TextView) view.findViewById(R.id.politicaltext);
		_tvFaithChannel = (TextView) view.findViewById(R.id.faithtext);
		_tvArtistsChannel = (TextView) view.findViewById(R.id.artisttext);
		_tvAnimalChannel = (TextView) view.findViewById(R.id.animaltext);
		_tvOtherInfluChannel = (TextView) view.findViewById(R.id.othertext);

		_tvExploreChannel.setTypeface(typefaceBold);
		_tvMusicChannel.setTypeface(typefaceBold);
		_tvSportsChannel.setTypeface(typefaceBold);
		_tvMoviesChannel.setTypeface(typefaceBold);
		_tvModelsChannel.setTypeface(typefaceBold);
		_tvBrandsChannel.setTypeface(typefaceBold);
		_tvExecutivesChannel.setTypeface(typefaceBold);
		_tvLocalBusinessChannel.setTypeface(typefaceBold);
		_tvPoliticiansChannel.setTypeface(typefaceBold);
		_tvFaithChannel.setTypeface(typefaceBold);
		_tvArtistsChannel.setTypeface(typefaceBold);
		_tvAnimalChannel.setTypeface(typefaceBold);
		_tvOtherInfluChannel.setTypeface(typefaceBold);

		
		_channelMainLayout = (RelativeLayout)view.findViewById(R.id.channel_main_relay);
		_kissMainLayout = (LinearLayout)view.findViewById(R.id.kiss_main_relay);
		_loadingLayout = (LinearLayout)view.findViewById(R.id.loadinglayout);

		_selector = (ImageView)view.findViewById(R.id.selector);

		_recentTabBtn = (TextView)view.findViewById(R.id.recentTabBtn);
		_recentTabBtn.setOnClickListener(this);
		_recentTabBtn.setTypeface(typefaceBold);

		_popularTabBtn = (TextView)view.findViewById(R.id.popularTabBtn);
		_popularTabBtn.setOnClickListener(this);
		_recentTabBtn.setTypeface(typeFaceNormal);

		_channelBtn = (ImageView)view.findViewById(R.id.channel_btn);
		_channelBtn.setOnClickListener(this);

		_kissBtn = (ImageView)view.findViewById(R.id.kiss_btn);
		_kissBtn.setOnClickListener(this);

		_popularBtn = (ImageView)view.findViewById(R.id.popular_btn);
		_popularBtn.setOnClickListener(this);

		_musicBtn = (LinearLayout)view.findViewById(R.id.musicBtn);
		_musicBtn.setOnClickListener(this);

		_sportsBtn = (LinearLayout)view.findViewById(R.id.sportsBtn);
		_sportsBtn.setOnClickListener(this);

		_moviesBtn = (LinearLayout)view.findViewById(R.id.moviesBtn);
		_moviesBtn.setOnClickListener(this);

		_modelsBtn = (LinearLayout)view.findViewById(R.id.modelsBtn);
		_modelsBtn.setOnClickListener(this);

		_brandsBtn = (LinearLayout)view.findViewById(R.id.brandsBtn);
		_brandsBtn.setOnClickListener(this);

		_executivesBtn = (LinearLayout)view.findViewById(R.id.executivesBtn);
		_executivesBtn.setOnClickListener(this);

		_localBusinessBtn = (LinearLayout)view.findViewById(R.id.localBusinessBtn);
		_localBusinessBtn.setOnClickListener(this);

		_politiciansBtn = (LinearLayout)view.findViewById(R.id.politiciansBtn);
		_politiciansBtn.setOnClickListener(this);

		_faithBtn = (LinearLayout)view.findViewById(R.id.faithBtn);
		_faithBtn.setOnClickListener(this);

		_artistsBtn = (LinearLayout)view.findViewById(R.id.artistsBtn);
		_artistsBtn.setOnClickListener(this);

		_animalBtn = (LinearLayout)view.findViewById(R.id.animalsBtn);
		_animalBtn.setOnClickListener(this);

		_otherInfluBtn = (LinearLayout)view.findViewById(R.id.otherinfluencerBtn);
		_otherInfluBtn.setOnClickListener(this);
		topGiftOnKiss(_kissType);
		return view;

	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {

		case R.id.channel_btn:
			_channelBtn.setImageResource(R.drawable.explore_channels_on);         
			_kissBtn.setImageResource(R.drawable.explore_kiss_off);
			_popularBtn.setImageResource(R.drawable.explore_popularnow_off);

			_channelMainLayout.setVisibility(View.VISIBLE);
			_kissMainLayout.setVisibility(View.GONE);

			break;
		case R.id.kiss_btn:
			//if(value.equalsIgnoreCase("0"))
			//{
			//}
			IS_KISS_LAYOUT = true;
			_channelBtn.setImageResource(R.drawable.explore_channels_off);
			_kissBtn.setImageResource(R.drawable.explore_kiss_on);
			_popularBtn.setImageResource(R.drawable.explore_popularnow_off);

			_channelMainLayout.setVisibility(View.GONE);
			_kissMainLayout.setVisibility(View.VISIBLE);

			if(_kissType == KEY_DAY_RECENT && kisslistRecentItem.size()>0){
				_selector.setImageResource(R.drawable.explore_separator_kiss_popularnow);
				listadapter = new CustomKissListAdapter(getActivity(), kisslistRecentItem,
						maxKissGiftRecentCount,minKissGiftRecentCount);
				_listViewKissRecent.setAdapter(listadapter);
				disableAllListViews();
				_loadingLayout.setVisibility(View.GONE);
				_listViewKissRecent.setVisibility(View.VISIBLE);
				 _recentTabBtn.setTypeface(typefaceBold);
					_popularTabBtn.setTypeface(typeFaceNormal);

			}else if (_kissType==KEY_DAY_POPULAR_NOW && kisslistPopularItem.size()>0) {
				_selector.setImageResource(R.drawable.explore_separator_kiss_recent);
				_recentTabBtn.setTypeface(typeFaceNormal);
				_popularTabBtn.setTypeface(typefaceBold);
				listadapter = new CustomKissListAdapter(getActivity(), kisslistPopularItem,
						maxKissGiftPopularCount,minKissGiftPopularCount);
				_listViewKissPopular.setAdapter(listadapter);
				disableAllListViews();
				_loadingLayout.setVisibility(View.GONE);
				_listViewKissPopular.setVisibility(View.VISIBLE);
				
			}else{
				if(_kissType == KEY_DAY_RECENT)
				{    _recentTabBtn.setTypeface(typefaceBold);
				_popularTabBtn.setTypeface(typeFaceNormal);
					_selector.setImageResource(R.drawable.explore_separator_kiss_popularnow);
					
				}
				else
				{_recentTabBtn.setTypeface(typeFaceNormal);
				_popularTabBtn.setTypeface(typefaceBold);
					_selector.setImageResource(R.drawable.explore_separator_kiss_recent);
				}
				topGiftOnKiss(_kissType);
			}
			break;
		case R.id.popular_btn:
			IS_KISS_LAYOUT = false;
			_channelBtn.setImageResource(R.drawable.explore_channels_off);
			_kissBtn.setImageResource(R.drawable.explore_kiss_off);
			_popularBtn.setImageResource(R.drawable.explore_popularnow_on);

			_channelMainLayout.setVisibility(View.GONE);
			_kissMainLayout.setVisibility(View.VISIBLE);

			if(_popularType == KEY_DAY_RECENT && hashlistRecentItem.size()>0){
				_recentTabBtn.setTypeface(typefaceBold);
				_popularTabBtn.setTypeface(typeFaceNormal);
				_selector.setImageResource(R.drawable.explore_separator_popularnow);
				_listViewBuzzRecent.setAdapter(new CustomBuzzGiftsAdapter(getActivity(), hashlistRecentItem,
						maxhashGiftRecentCount, minhashGiftRecentCount));
				disableAllListViews();
				_loadingLayout.setVisibility(View.GONE);
				_listViewBuzzRecent.setVisibility(View.VISIBLE);
			}else if(_popularType == KEY_DAY_POPULAR_NOW && hashlistPopularItem.size()>0){
				_selector.setImageResource(R.drawable.explore_separator_popular_recent);
				
				_listViewBuzzPopular.setAdapter(new CustomBuzzGiftsAdapter(getActivity(),
						hashlistPopularItem, maxhashGiftPopularCount, minhashGiftPopularCount));
				disableAllListViews();
				_loadingLayout.setVisibility(View.GONE);
				_listViewBuzzPopular.setVisibility(View.VISIBLE);

			}else {
				if(_popularType == KEY_DAY_RECENT)
				{    _recentTabBtn.setTypeface(typefaceBold);
				_popularTabBtn.setTypeface(typeFaceNormal);
					_selector.setImageResource(R.drawable.explore_separator_popularnow);
					
				}
				else
				{_recentTabBtn.setTypeface(typeFaceNormal);
				_popularTabBtn.setTypeface(typefaceBold);
					_selector.setImageResource(R.drawable.explore_separator_popular_recent);
				}
				topHashTagsOnBUZZ(_popularType);
			}	
			break;

		case R.id.recentTabBtn:
			_recentTabBtn.setTypeface(typefaceBold);
			_popularTabBtn.setTypeface(typeFaceNormal);
			_kissType = KEY_DAY_RECENT;
			_popularType = KEY_DAY_RECENT;
			if (IS_KISS_LAYOUT) {
				_selector.setImageResource(R.drawable.explore_separator_kiss_popularnow);
				if(kisslistRecentItem.size()>0){
					listadapter = new CustomKissListAdapter(getActivity(), kisslistRecentItem,
							maxKissGiftRecentCount,minKissGiftRecentCount);
					_listViewKissRecent.setAdapter(listadapter);
					disableAllListViews();
					_loadingLayout.setVisibility(View.GONE);
					_listViewKissRecent.setVisibility(View.VISIBLE);

				}else{
					topGiftOnKiss(_kissType);
				}	
			}else {
				_selector.setImageResource(R.drawable.explore_separator_popularnow);
                   if (hashlistRecentItem.size()>0) {
					_listViewBuzzRecent.setAdapter(new CustomBuzzGiftsAdapter(getActivity(), hashlistRecentItem,
							maxhashGiftRecentCount, minhashGiftRecentCount));
					disableAllListViews();
					_loadingLayout.setVisibility(View.GONE);
					_listViewBuzzRecent.setVisibility(View.VISIBLE);
				}else{
					topHashTagsOnBUZZ(_popularType);
				}	
			}

			break;

		case R.id.popularTabBtn:
			_recentTabBtn.setTypeface(typeFaceNormal);
			_popularTabBtn.setTypeface(typefaceBold);
			_kissType = KEY_DAY_POPULAR_NOW;
			_popularType = KEY_DAY_POPULAR_NOW;

			if (IS_KISS_LAYOUT) {
				_selector.setImageResource(R.drawable.explore_separator_kiss_recent);

				if(kisslistPopularItem.size()>0){
					listadapter = new CustomKissListAdapter(getActivity(), kisslistPopularItem,
							maxKissGiftPopularCount,minKissGiftPopularCount);
					_listViewKissPopular.setAdapter(listadapter);
					disableAllListViews();
					_loadingLayout.setVisibility(View.GONE);
					_listViewKissPopular.setVisibility(View.VISIBLE);
				}else{
					topGiftOnKiss(_kissType);	
				}
			}else {
				_selector.setImageResource(R.drawable.explore_separator_popular_recent);

				if (hashlistPopularItem.size()>0) {
					_listViewBuzzPopular.setAdapter(new CustomBuzzGiftsAdapter(getActivity(),
							hashlistPopularItem, maxhashGiftPopularCount, minhashGiftPopularCount));
					disableAllListViews();
					_loadingLayout.setVisibility(View.GONE);
					_listViewBuzzPopular.setVisibility(View.VISIBLE);
				}else{
					topHashTagsOnBUZZ(_popularType);
				}	
			}

			break;

		case R.id.musicBtn:
			intent = new Intent(this.getActivity(), ExploreChannelOptionScreen.class);
			intent.putExtra("SCREEN_NAME", KEY_MUSIC);
			startActivity(intent);
			break;

		case R.id.sportsBtn:
			intent = new Intent(this.getActivity(), ExploreChannelOptionScreen.class);
			intent.putExtra("SCREEN_NAME", KEY_SPORTS);
			startActivity(intent);
			break;
		case R.id.moviesBtn:
			intent = new Intent(this.getActivity(), ExploreChannelOptionScreen.class);
			intent.putExtra("SCREEN_NAME", KEY_CINEMA);
			startActivity(intent);
			break;
		case R.id.modelsBtn:
			intent = new Intent(this.getActivity(), ExploreChannelOptionScreen.class);
			intent.putExtra("SCREEN_NAME", KEY_MODELS);
			startActivity(intent);
			break;
		case R.id.brandsBtn:
			intent = new Intent(this.getActivity(), ExploreChannelOptionScreen.class);
			intent.putExtra("SCREEN_NAME", KEY_BRANDS);
			startActivity(intent);
			break;
		case R.id.executivesBtn:
			intent = new Intent(this.getActivity(), ExploreChannelOptionScreen.class);
			intent.putExtra("SCREEN_NAME", KEY_EXECUTIVES);
			startActivity(intent);
			break;
		case R.id.localBusinessBtn:
			intent = new Intent(this.getActivity(), ExploreChannelOptionScreen.class);
			intent.putExtra("SCREEN_NAME", KEY_LOCAL_BUSINESS);
			startActivity(intent);
			break;
		case R.id.politiciansBtn:
			intent = new Intent(this.getActivity(), ExploreChannelOptionScreen.class);
			intent.putExtra("SCREEN_NAME", KEY_POLITICIANS);
			startActivity(intent);
			break;
		case R.id.faithBtn:
			intent = new Intent(this.getActivity(), ExploreChannelOptionScreen.class);
			intent.putExtra("SCREEN_NAME", KEY_FAITH);
			startActivity(intent);
			break;
		case R.id.artistsBtn:
			intent = new Intent(this.getActivity(), ExploreChannelOptionScreen.class);
			intent.putExtra("SCREEN_NAME", KEY_ARTISTS);
			startActivity(intent);
			break;
		case R.id.animalsBtn:
			intent = new Intent(this.getActivity(), ExploreChannelOptionScreen.class);
			intent.putExtra("SCREEN_NAME", KEY_ANIMAL);
			startActivity(intent);
			break;
		case R.id.otherinfluencerBtn:
			intent = new Intent(this.getActivity(), ExploreChannelOptionScreen.class);
			intent.putExtra("SCREEN_NAME", KEY_OTHER_INFLU);
			startActivity(intent);
			break;

		}
	}


	/*
	 * Method for top hash tags on BUZZ screen
	 * 
	 * */
	public void topHashTagsOnBUZZ(final int days) {
		final ExploreActivity thisReference = this;

		disableAllListViews();

		_loadingLayout.setVisibility(View.VISIBLE);
		Runnable r2 = new Runnable() {
			@Override
			public void run() {
				String serverResponse = "";
				Bitmap bitmap = null ;
				cd = new ConnectionDetector(ExploreActivity.this.getActivity());
				if (cd.isConnectingToInternet())
				{
				JSONObject jsonParamObject = null;
				try {               
					jsonParamObject = new JSONObject();
					jsonParamObject.put("days",days);
					jsonParamObject.put("maxTags",10);

					serverResponse = NetworkAccessUtils.sendPostRequest(ExploreActivity.this.getActivity(),
							NetworkUrls.SERVER_URL+"/gettophashtags",
							jsonParamObject.toString());
					if(serverResponse.equalsIgnoreCase("401"))
					{
						 Intent i = new Intent(getActivity(),SelebretySignup.class);
			                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			                
			                i.putExtra("islogin", true);
			                startActivity(i);
					}

				} catch (Exception e) {
					e.printStackTrace();

					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							_loadingLayout.setVisibility(View.GONE);		
						}
					});

				} 

				Log.d("serverResponse", serverResponse);

				JSONObject json = null;
				try {
					json = new JSONObject(serverResponse);
					String status = json.getString("status");

					if (!status.equalsIgnoreCase("OK")) {
						Log.e("Explore Activity Screen", "Response ERROR: " + status + " - " + json.getString("errMsg"));

						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								_loadingLayout.setVisibility(View.GONE);		
							}
						});
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();

					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							_loadingLayout.setVisibility(View.GONE);		
						}
					});
				}


				try {
					json = new JSONObject(serverResponse);

					if (days==KEY_DAY_RECENT) {
						SelebretyDatabaseAccessObject selebretyDatabaseAccessObject = new SelebretyDatabaseAccessObject(ExploreActivity.this.getActivity());
						try
						{
						selebretyDatabaseAccessObject.removeBuzzData(""+days);
						}
						catch(Exception e)
						{
							System.out.println("#####Exception="+e);
						}
						hashlistRecentItem.clear();
						hashGiftRecentcode.clear();
						hashGiftRecentcount.clear();
						maxhashGiftRecentCount = 0;
						minhashGiftRecentCount = Integer.MAX_VALUE;

						JSONArray array = json.getJSONArray("hashtags");

						for (int i = 0; i < array.length(); i++) {
                            String bbcount=array.getJSONObject(i).getString("count");
                            String bbtoken=array.getJSONObject(i).getString("token");
                            int bbcountint=Integer.parseInt(bbcount);
							hashGiftRecentcount.add(bbcount);
							hashGiftRecentcode.add(bbtoken);
							if(maxhashGiftRecentCount<bbcountint){
								maxhashGiftRecentCount=bbcountint;
							}
							if(minhashGiftRecentCount>bbcountint){
								minhashGiftRecentCount=bbcountint;
							}
							selebretyDatabaseAccessObject.insertBuzzData(""+days,bbcount, bbtoken);

						}
						for(int i=0;i<hashGiftRecentcount.size();i++)
						{	
							for(int j=0;j<hashGiftRecentcount.size();j++)
							{	
								if(Integer.parseInt(hashGiftRecentcount.get(i))>Integer.parseInt(hashGiftRecentcount.get(j))){
									String temp = hashGiftRecentcount.get(i);
									hashGiftRecentcount.set(i, hashGiftRecentcount.get(j));
									hashGiftRecentcount.set(j, temp);	

									String temp2 = hashGiftRecentcode.get(i);
									hashGiftRecentcode.set(i, hashGiftRecentcode.get(j));
									hashGiftRecentcode.set(j, temp2);	
								}
							}
						}

					}
					else	{
						SelebretyDatabaseAccessObject selebretyDatabaseAccessObject = new SelebretyDatabaseAccessObject(ExploreActivity.this.getActivity());
						try
						{
						selebretyDatabaseAccessObject.removeBuzzData(""+days);
						}
						catch(Exception e)
						{
							System.out.println("#####Exception="+e);
						}
						hashlistPopularItem.clear();
						hashGiftPopularcode.clear();
						hashGiftPopularcount.clear();
						maxhashGiftPopularCount = 0;
						minhashGiftPopularCount = Integer.MAX_VALUE;

						JSONArray array = json.getJSONArray("hashtags");

						for (int i = 0; i < array.length(); i++) {
							String bbcount=array.getJSONObject(i).getString("count");
                            String bbtoken=array.getJSONObject(i).getString("token");
                            int bbcountint=Integer.parseInt(bbcount);
							hashGiftPopularcount.add(bbcount);
							hashGiftPopularcode.add(bbtoken);
							if(maxhashGiftPopularCount<bbcountint){
								maxhashGiftPopularCount=bbcountint;
							}
							if(minhashGiftPopularCount>bbcountint){
								minhashGiftPopularCount=bbcountint;
							}
							selebretyDatabaseAccessObject.insertBuzzData(""+days,bbcount, bbtoken);
						}
						
						for(int i=0;i<hashGiftPopularcount.size();i++)
						{	
							for(int j=0;j<hashGiftPopularcount.size();j++)
							{	
								if(Integer.parseInt(hashGiftPopularcount.get(i))>Integer.parseInt(hashGiftPopularcount.get(j))){
									String temp = hashGiftPopularcount.get(i);
									hashGiftPopularcount.set(i, hashGiftPopularcount.get(j));
									hashGiftPopularcount.set(j, temp);	

									String temp2 = hashGiftPopularcode.get(i);
									hashGiftPopularcode.set(i, hashGiftPopularcode.get(j));
									hashGiftPopularcode.set(j, temp2);	
								}
							}
						}

					}

				} catch (JSONException e) {
					e.printStackTrace();
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							_loadingLayout.setVisibility(View.GONE);		
						}
					});
				}catch (Exception e) {
					e.printStackTrace();
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							_loadingLayout.setVisibility(View.GONE);		
						}
					});
				}
				}
				else
				{
					if (days==KEY_DAY_RECENT) {
						SelebretyDatabaseAccessObject selebretyDatabaseAccessObject = new SelebretyDatabaseAccessObject(ExploreActivity.this.getActivity());
						hashlistRecentItem.clear();
						hashGiftRecentcode.clear();
						hashGiftRecentcount.clear();
						maxhashGiftRecentCount = 0;
						minhashGiftRecentCount = Integer.MAX_VALUE;
                        ArrayList<String> BuzzDataRecent=new ArrayList<String>();
                        BuzzDataRecent=selebretyDatabaseAccessObject.getBuzzData(""+days);
                        for (int i = 0; i <BuzzDataRecent.size()-1; i=i+2) {
                            String bbcount=BuzzDataRecent.get(i);
                            String bbtoken=BuzzDataRecent.get(i+1);
                            int bbcountint=Integer.parseInt(bbcount);
							hashGiftRecentcount.add(bbcount);
							hashGiftRecentcode.add(bbtoken);
							if(maxhashGiftRecentCount<bbcountint){
								maxhashGiftRecentCount=bbcountint;
							}
							if(minhashGiftRecentCount>bbcountint){
								minhashGiftRecentCount=bbcountint;
							}
                       }
						for(int i=0;i<hashGiftRecentcount.size();i++)
						{	
							for(int j=0;j<hashGiftRecentcount.size();j++)
							{	
								if(Integer.parseInt(hashGiftRecentcount.get(i))>Integer.parseInt(hashGiftRecentcount.get(j))){
									String temp = hashGiftRecentcount.get(i);
									hashGiftRecentcount.set(i, hashGiftRecentcount.get(j));
									hashGiftRecentcount.set(j, temp);	

									String temp2 = hashGiftRecentcode.get(i);
									hashGiftRecentcode.set(i, hashGiftRecentcode.get(j));
									hashGiftRecentcode.set(j, temp2);	
								}
							}
						}
                      selebretyDatabaseAccessObject.close();
					}
					else	{
						SelebretyDatabaseAccessObject selebretyDatabaseAccessObject = new SelebretyDatabaseAccessObject(ExploreActivity.this.getActivity());
						hashlistPopularItem.clear();
						hashGiftPopularcode.clear();
						hashGiftPopularcount.clear();
						maxhashGiftPopularCount = 0;
						minhashGiftPopularCount = Integer.MAX_VALUE;
						ArrayList<String> BuzzData=new ArrayList<String>();
                        BuzzData=selebretyDatabaseAccessObject.getBuzzData(""+days);
                        for (int i = 0; i < (BuzzData.size()-1); i=i+2) {
							String bbcount=BuzzData.get(i);
                            String bbtoken=BuzzData.get(i+1);
                            int bbcountint=Integer.parseInt(bbcount);
							hashGiftPopularcount.add(bbcount);
							hashGiftPopularcode.add(bbtoken);
							if(maxhashGiftPopularCount<bbcountint){
								maxhashGiftPopularCount=bbcountint;
							}
							if(minhashGiftPopularCount>bbcountint){
								minhashGiftPopularCount=bbcountint;
							}
						}
						
						for(int i=0;i<hashGiftPopularcount.size();i++)
						{	
							for(int j=0;j<hashGiftPopularcount.size();j++)
							{	
								if(Integer.parseInt(hashGiftPopularcount.get(i))>Integer.parseInt(hashGiftPopularcount.get(j))){
									String temp = hashGiftPopularcount.get(i);
									hashGiftPopularcount.set(i, hashGiftPopularcount.get(j));
									hashGiftPopularcount.set(j, temp);	

									String temp2 = hashGiftPopularcode.get(i);
									hashGiftPopularcode.set(i, hashGiftPopularcode.get(j));
									hashGiftPopularcode.set(j, temp2);	
								}
							}
						}
                      selebretyDatabaseAccessObject.close();
					}

	
				}
				Runnable rim = new Runnable() {

					@Override
					public void run() {
						if (days==KEY_DAY_RECENT) {

							for(int j = 0;j<hashGiftRecentcount.size();j++)
							{
								BuzzGiftsBean beanCollectedGifts = new BuzzGiftsBean();
								tvWeightHashRecent = getTvWeight(Integer.parseInt(hashGiftRecentcount.get(j)));
								blankWeightHashRecent = getBlankWeight(tvWeightHashRecent);
								beanCollectedGifts.setGiftCode(hashGiftRecentcode.get(j));
								beanCollectedGifts.setGiftCount(hashGiftRecentcount.get(j));
								beanCollectedGifts.setBlankweight(blankWeightHashRecent);
								beanCollectedGifts.setTvweight(tvWeightHashRecent);
								//beanCollectedGifts.setHashTxt(hashTxt)

								hashlistRecentItem.add(beanCollectedGifts);
							}	


							_listViewBuzzRecent.setAdapter(new CustomBuzzGiftsAdapter(getActivity(), hashlistRecentItem,
									maxhashGiftRecentCount, minhashGiftRecentCount));
							disableAllListViews();
							_loadingLayout.setVisibility(View.GONE);
							_listViewBuzzRecent.setVisibility(View.VISIBLE);
                            _kissType=KEY_DAY_RECENT;
                            _popularType=KEY_DAY_RECENT;
						}else if (days==KEY_DAY_POPULAR_NOW) {

							for(int j = 0;j<hashGiftPopularcount.size();j++)
							{
								BuzzGiftsBean beanCollectedGifts = new BuzzGiftsBean();
								tvWeightHashPopular = getTvWeight(Integer.parseInt(hashGiftPopularcount.get(j)));
								blankWeightHashPopular = getBlankWeight(tvWeightHashPopular);
								beanCollectedGifts.setGiftCode(hashGiftPopularcode.get(j));
								beanCollectedGifts.setGiftCount(hashGiftPopularcount.get(j));
								beanCollectedGifts.setBlankweight(blankWeightHashPopular);
								beanCollectedGifts.setTvweight(tvWeightHashPopular);
								//beanCollectedGifts.setHashTxt(hashTxt)

								hashlistPopularItem.add(beanCollectedGifts);
							}	


							_listViewBuzzPopular.setAdapter(new CustomBuzzGiftsAdapter(getActivity(), hashlistPopularItem,
									maxhashGiftPopularCount, minhashGiftPopularCount));
							disableAllListViews();
							_kissType=KEY_DAY_POPULAR_NOW;
							_popularType=KEY_DAY_POPULAR_NOW;
							_loadingLayout.setVisibility(View.GONE);
							_listViewBuzzPopular.setVisibility(View.VISIBLE);

						}
					}

				};

				getActivity().runOnUiThread(rim);


			}

			private int getTvWeight(int count) {
				if (days==KEY_DAY_RECENT) {
					int diff=maxhashGiftRecentCount-minhashGiftRecentCount;
					int size = (int)(((float)count / maxhashGiftRecentCount) * diff) + minhashGiftRecentCount;
					return size;
				}else{
					int diff=maxhashGiftPopularCount-minhashGiftPopularCount;
					int size = (int)(((float)count / maxhashGiftPopularCount) * diff) + minhashGiftPopularCount;
					return size;
				}
			}

			private int getBlankWeight(int tvWeight2) {

				if (days==KEY_DAY_RECENT) {
					return maxhashGiftRecentCount-tvWeight2;
				}else{
					return maxhashGiftPopularCount-tvWeight2;
				}
			}
		}; 
		mSingleThreadExecutor.execute(r2);
	}



	/*
	 * Method to disable All ListViews
	 * 
	 * */
	private void disableAllListViews() {
		_listViewBuzzPopular.setVisibility(View.GONE);
		_listViewBuzzRecent.setVisibility(View.GONE);
		_listViewKissPopular.setVisibility(View.GONE);
		_listViewKissRecent.setVisibility(View.GONE);
	}


	/*
	 * Method for top gift users on kiss Screen
	 * 
	 * */

	public void topGiftOnKiss(final int days) {
		final ExploreActivity thisReference = this;

		disableAllListViews();
		_loadingLayout.setVisibility(View.VISIBLE);

		Runnable r1 = new Runnable() {
			@Override
			public void run() {
				String serverResponse = "";
				Bitmap bitmap = null ;
                JSONObject jsonParamObject = null;
                cd = new ConnectionDetector(ExploreActivity.this.getActivity());
				if (cd.isConnectingToInternet())
				{
					System.out.println("#####NetIs Connected in topGiftOnKiss");
                    try {               
					jsonParamObject = new JSONObject();
					jsonParamObject.put("days",days);
					jsonParamObject.put("maxUsers",10);
					serverResponse = NetworkAccessUtils.sendPostRequest(getActivity(),NetworkUrls.SERVER_URL+"/gettopgiftusers",jsonParamObject.toString());
					if(serverResponse.equalsIgnoreCase("401"))
					{
						 Intent i = new Intent(getActivity(),SelebretySignup.class);
			                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			                
			                i.putExtra("islogin", true);
			                startActivity(i);
					}
				} catch (Exception e) {
					e.printStackTrace();

					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							_loadingLayout.setVisibility(View.GONE);		
						}
					});
				} 

				Log.d("serverResponse", serverResponse);

				JSONObject json = null;
				try {
					json = new JSONObject(serverResponse);
					String status = json.getString("status");

					if (!status.equalsIgnoreCase("OK")) {
						Log.e("Explore Activity Screen", "Response ERROR: " + status + " - " + json.getString("errMsg"));

						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								_loadingLayout.setVisibility(View.GONE);		
							}
						});

						return;
					}
				} catch (Exception e) {
					e.printStackTrace();

					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							_loadingLayout.setVisibility(View.GONE);		
						}
					});
				}


				try {
					if (days==KEY_DAY_RECENT) {
						kisslistRecentItem.clear();
						maxKissGiftRecentCount = 0;
						minKissGiftRecentCount = Integer.MAX_VALUE;
						SelebretyDatabaseAccessObject selebretyDatabaseAccessObject = new SelebretyDatabaseAccessObject(ExploreActivity.this.getActivity());
						try
						{
						selebretyDatabaseAccessObject.removeKissData(""+days);
						selebretyDatabaseAccessObject.removeKissCountData(""+days);
						}
						catch(Exception e)
						{
							System.out.println("#####Exception="+e);
						}
						json = new JSONObject(serverResponse);

						JSONArray array = json.getJSONArray("users");

						for (int i = 0; i < array.length(); i++) {

							ArrayList<HashMap<String, Object>> userData= new ArrayList<HashMap<String,Object>>();

							KissBean beanTopfans = new KissBean();

							String topFanUserId=array.getJSONObject(i).getString("userId");
							String topFanUserName=array.getJSONObject(i).getString("userName");
							
							String topFanUserPicture="";
							if(array.getJSONObject(i).has("userPicture")){
								topFanUserPicture=array.getJSONObject(i).getString("userPicture");
								bitmap = getProfileImage(topFanUserPicture);
							}else if(array.getJSONObject(i).has("gender")){
								topFanUserPicture=array.getJSONObject(i).getString("gender");
								if(topFanUserPicture.equalsIgnoreCase("m")){
									bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.settings_user_thumbnail_boy);
								}else{
									bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.settings_user_thumbnail_boy);
								}
								
							}
							int totalGift=array.getJSONObject(i).getInt("totalGifts");

							selebretyDatabaseAccessObject.insertKissData(""+days,topFanUserId, topFanUserName, topFanUserPicture, ""+totalGift);
							JSONArray giftsjs=	array.getJSONObject(i).getJSONArray("gifts");
							for (int j = 0; j < giftsjs.length(); j++) {
								HashMap<String, Object> userhash= new HashMap<String, Object>();

								String giftCount=giftsjs.getJSONObject(j).getString("count");
								String giftcode=giftsjs.getJSONObject(j).getString("giftCode");
								selebretyDatabaseAccessObject.insertKissCountData(""+days, topFanUserId,giftCount, giftcode);
								userhash.put("fanUserId", topFanUserId);
								userhash.put("giftCount", giftCount);
								userhash.put("giftCode", giftcode);
								userData.add(userhash);
							}
							if(maxKissGiftRecentCount<totalGift){
								maxKissGiftRecentCount=totalGift;
							}
							if(minKissGiftRecentCount>totalGift){
								minKissGiftRecentCount=totalGift;
							}
							beanTopfans.setTotalGift(totalGift);
							beanTopfans.setGifts(userData);
							beanTopfans.setTopFanUserName(topFanUserName);
							beanTopfans.setFanUserId(topFanUserId);
							beanTopfans.setTopFanUserPicture(bitmap);
							kisslistRecentItem.add(beanTopfans);
                               }
						selebretyDatabaseAccessObject.close();
					} else{
						kisslistPopularItem.clear();
						maxKissGiftPopularCount = 0;
						minKissGiftPopularCount = Integer.MAX_VALUE;
						SelebretyDatabaseAccessObject selebretyDatabaseAccessObject = new SelebretyDatabaseAccessObject(ExploreActivity.this.getActivity());
						try
						{
						selebretyDatabaseAccessObject.removeKissData(""+days);
						selebretyDatabaseAccessObject.removeKissCountData(""+days);
						}
						catch(Exception e)
						{
							System.out.println("#####Exception="+e);
						}

						json = new JSONObject(serverResponse);

						JSONArray array = json.getJSONArray("users");

						for (int i = 0; i < array.length(); i++) {

							ArrayList<HashMap<String, Object>> userData= new ArrayList<HashMap<String,Object>>();

							KissBean beanTopfans = new KissBean();

							String topFanUserId=array.getJSONObject(i).getString("userId");
							String topFanUserName=array.getJSONObject(i).getString("userName");
							//String topFanUserPicture=array.getJSONObject(i).getString("userPicture");
							int totalGift=array.getJSONObject(i).getInt("totalGifts");
							
							//bitmap = getProfileImage(topFanUserPicture);
							
							String topFanUserPicture="";
							if(array.getJSONObject(i).has("userPicture")){
								topFanUserPicture=array.getJSONObject(i).getString("userPicture");
								bitmap = getProfileImage(topFanUserPicture);
							}else if(array.getJSONObject(i).has("gender")){
								topFanUserPicture=array.getJSONObject(i).getString("gender");
								if(topFanUserPicture.equalsIgnoreCase("m")){
									bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.settings_user_thumbnail_boy);
								}else{
									bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.settings_user_thumbnail_girl);
								}
								
							}else{
								bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.settings_user_thumbnail_boy);
							}
							selebretyDatabaseAccessObject.insertKissData(""+days,topFanUserId, topFanUserName, topFanUserPicture, ""+totalGift);
							JSONArray giftsjs=	array.getJSONObject(i).getJSONArray("gifts");
							for (int j = 0; j < giftsjs.length(); j++) {
								HashMap<String, Object> userhash= new HashMap<String, Object>();

								String giftCount=giftsjs.getJSONObject(j).getString("count");
								String giftcode=giftsjs.getJSONObject(j).getString("giftCode");
								selebretyDatabaseAccessObject.insertKissCountData(""+days, topFanUserId,giftCount, giftcode);
								userhash.put("fanUserId", topFanUserId);
								userhash.put("giftCount", giftCount);
								userhash.put("giftCode", giftcode);
								userData.add(userhash);
							}
							if(maxKissGiftPopularCount<totalGift){
								maxKissGiftPopularCount=totalGift;
							}
							if(minKissGiftPopularCount>totalGift){
								minKissGiftPopularCount=totalGift;
							}
							beanTopfans.setTotalGift(totalGift);
							beanTopfans.setGifts(userData);
							beanTopfans.setTopFanUserName(topFanUserName);
							beanTopfans.setFanUserId(topFanUserId);
							beanTopfans.setTopFanUserPicture(bitmap);
							kisslistPopularItem.add(beanTopfans);


						}
                        selebretyDatabaseAccessObject.close();
					} 

				} catch (JSONException e) {
					e.printStackTrace();
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							_loadingLayout.setVisibility(View.GONE);		
						}
					});
				}catch (Exception e) {
					e.printStackTrace();
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							_loadingLayout.setVisibility(View.GONE);		
						}
					});
				}
				}
				else
				{   System.out.println("#####Net is not connected.....");
			       try
					{
						if (days==KEY_DAY_RECENT) {
							kisslistRecentItem.clear();
							maxKissGiftRecentCount = 0;
							minKissGiftRecentCount = Integer.MAX_VALUE;
							SelebretyDatabaseAccessObject selebretyDatabaseAccessObject = new SelebretyDatabaseAccessObject(ExploreActivity.this.getActivity());
							ArrayList<String> KissDataListRecent=new ArrayList<String>();
							KissDataListRecent=selebretyDatabaseAccessObject.getKissData(""+days);
							for (int i = 0; i < (KissDataListRecent.size()-3);i=i+4) {
								ArrayList<HashMap<String, Object>> userData= new ArrayList<HashMap<String,Object>>();
	                            KissBean beanTopfans = new KissBean();
	                            String topFanUserId=KissDataListRecent.get(i);
								String topFanUserName=KissDataListRecent.get(i+1);
								String topFanUserPicture=KissDataListRecent.get(i+2);
								int totalGift=Integer.parseInt(KissDataListRecent.get(i+3));
								System.out.println("#####topFanUserId in recent="+topFanUserId);
								System.out.println("#####topFanUserName in recent="+topFanUserName);
								System.out.println("#####topFanUserPicture in recent="+topFanUserPicture);
								System.out.println("#####TotalGift in recent="+totalGift);
	                            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.settings_user_thumbnail_boy);
	                            ArrayList<String> KissDataCountList=new ArrayList<String>();
	    						KissDataCountList=selebretyDatabaseAccessObject.getKissCountData(""+days,topFanUserId);
	                            for (int j = 0; j < (KissDataCountList.size()-1); j=j+2) {
									HashMap<String, Object> userhash= new HashMap<String, Object>();
	                           
									String giftCount=KissDataCountList.get(j);
									String giftcode=KissDataCountList.get(j+1);
									System.out.println("!!!!!!!!!!!!GiftCount in recent="+giftCount);
									System.out.println("!!!!!!!!!!!!GiftCode in recent="+giftcode);
									userhash.put("fanUserId", topFanUserId);
									userhash.put("giftCount", giftCount);
									userhash.put("giftCode", giftcode);
									userData.add(userhash);
								}
								if(maxKissGiftRecentCount<totalGift){
									maxKissGiftRecentCount=totalGift;
								}
								if(minKissGiftRecentCount>totalGift){
									minKissGiftRecentCount=totalGift;
								}
								beanTopfans.setTotalGift(totalGift);
								beanTopfans.setGifts(userData);
								beanTopfans.setTopFanUserName(topFanUserName);
								beanTopfans.setFanUserId(topFanUserId);
								beanTopfans.setTopFanUserPicture(bitmap);
								kisslistRecentItem.add(beanTopfans);
	                               }
							selebretyDatabaseAccessObject.close();
						} else{
							kisslistPopularItem.clear();
							maxKissGiftPopularCount = 0;
							minKissGiftPopularCount = Integer.MAX_VALUE;
							SelebretyDatabaseAccessObject selebretyDatabaseAccessObject = new SelebretyDatabaseAccessObject(ExploreActivity.this.getActivity());
							ArrayList<String> KissDataList=new ArrayList<String>();
							KissDataList=selebretyDatabaseAccessObject.getKissData(""+days);
							for (int i = 0; i < (KissDataList.size()-3);i=i+4) {

								ArrayList<HashMap<String, Object>> userData= new ArrayList<HashMap<String,Object>>();
	                            KissBean beanTopfans = new KissBean();
	                            String topFanUserId=KissDataList.get(i);
								String topFanUserName=KissDataList.get(i+1);
								String topFanUserPicture=KissDataList.get(i+2);
								int totalGift=Integer.parseInt(KissDataList.get(i+3));
								System.out.println("**************topFanUserId="+topFanUserId);
								System.out.println("**************topFanUserName="+topFanUserName);
								System.out.println("**************topFanUserPicture="+topFanUserPicture);
								System.out.println("**************TotalGift="+totalGift);
	                            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.settings_user_thumbnail_boy);
	                            ArrayList<String> KissDataCountList=new ArrayList<String>();
	    						KissDataCountList=selebretyDatabaseAccessObject.getKissCountData(""+days,topFanUserId);
	                            for (int j = 0; j < (KissDataCountList.size()-1); j=j+2) {
									HashMap<String, Object> userhash= new HashMap<String, Object>();
	                           
									String giftCount=KissDataCountList.get(j);
									String giftcode=KissDataCountList.get(j+1);
									System.out.println("!!!!!!!!!!GiftCount="+giftCount);
									System.out.println("!!!!!!!!!!GiftCode="+giftcode);
									userhash.put("fanUserId", topFanUserId);
									userhash.put("giftCount", giftCount);
									userhash.put("giftCode", giftcode);
									userData.add(userhash);
								}

								if(maxKissGiftPopularCount<totalGift){
									maxKissGiftPopularCount=totalGift;
								}
								if(minKissGiftPopularCount>totalGift){
									minKissGiftPopularCount=totalGift;
								}
								beanTopfans.setTotalGift(totalGift);
								beanTopfans.setGifts(userData);
								beanTopfans.setTopFanUserName(topFanUserName);
								beanTopfans.setFanUserId(topFanUserId);
								beanTopfans.setTopFanUserPicture(bitmap);
								kisslistPopularItem.add(beanTopfans);


							}
	                        selebretyDatabaseAccessObject.close();
						} 

					} catch (Exception e) {
						e.printStackTrace();
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								_loadingLayout.setVisibility(View.GONE);		
							}
						});
					}

			    //##################################################
				}
				Runnable rim = new Runnable() {

					@Override
					public void run() {
						if(days==KEY_DAY_RECENT){
							listadapter = new CustomKissListAdapter(getActivity(), kisslistRecentItem,
									maxKissGiftRecentCount,minKissGiftRecentCount);
							_listViewKissRecent.setAdapter(listadapter);
							disableAllListViews();
							_loadingLayout.setVisibility(View.GONE);
							_listViewKissRecent.setVisibility(View.VISIBLE);
                            _popularType=KEY_DAY_RECENT;
                            _kissType=KEY_DAY_RECENT;
						}else {
							listadapter = new CustomKissListAdapter(getActivity(), kisslistPopularItem,
									maxKissGiftPopularCount,minKissGiftPopularCount);
							_listViewKissPopular.setAdapter(listadapter);
							disableAllListViews();
							_loadingLayout.setVisibility(View.GONE);
							_listViewKissPopular.setVisibility(View.VISIBLE);
							_kissType=KEY_DAY_POPULAR_NOW;
							_popularType=KEY_DAY_POPULAR_NOW;
							
						}
					}

				};

				getActivity().runOnUiThread(rim);


			}

		}; 
		mSingleThreadExecutor.execute(r1);
	}


	/*
	 * Method to get profile bitmap from url
	 * 
	 */

	private Bitmap getProfileImage(String reqURL) {

		Bitmap bitmap = null;
		Bitmap createdBitmap = null;
		int _diameter=0;
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int displayHeight = displaymetrics.heightPixels;
		int displayWidth = displaymetrics.widthPixels;

		if(displayHeight <= 480 || displayWidth <= 320){
			_diameter = 124;
		}else if(displayHeight == 800 || displayWidth == 480){
			_diameter = 183;
		}else if(displayHeight > 800 || displayWidth > 480){
			_diameter = 276;
		}
		try {

			HttpGet httpRequest = new HttpGet(
					URI.create(reqURL));
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = (HttpResponse) httpclient
					.execute(httpRequest);
			HttpEntity entity = response.getEntity();
			BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(
					entity);
			bitmap = BitmapFactory
					.decodeStream(bufHttpEntity
							.getContent());
			httpRequest.abort();

			int height = bitmap.getHeight();
			int width = bitmap.getWidth();
			if (height >= width){

				int diff=height-width;
				int newHeight = height-diff; 

				/*int newHeight = (int)(height * ((float)_diameter/width));
				int newWidth = (int)(width * ((float)_diameter/width));*/
				createdBitmap = ThumbnailUtils.extractThumbnail(bitmap, width, newHeight);
			}else{
				int diff=width-height;
				int newWidth = width-diff; 

				/*int newHeight = (int)(height * ((float)_diameter/height));
				int newWidth = (int)(width * ((float)_diameter/height));*/
				createdBitmap = ThumbnailUtils.extractThumbnail(bitmap, newWidth, height);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return createdBitmap;

	}
}
