package com.openbox.realcomm3.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.application.RealCommApplication;
import com.openbox.realcomm3.base.BaseFragment;
import com.openbox.realcomm3.database.models.BoothModel;
import com.openbox.realcomm3.database.models.CompanyLogoModel;
import com.openbox.realcomm3.utilities.enums.RealcommPage;
import com.openbox.realcomm3.utilities.helpers.BitmapHelper;
import com.openbox.realcomm3.utilities.loaders.CompanyLogoLoader;

public class BoothFragment extends BaseFragment implements OnClickListener
{
	private static final String BOOTH_ID_KEY = "boothIdKey";
	private static final String IS_BIG_KEY = "isBigKey";
	private static final String BOOTH_PREFIX = "BOOTH ";

	private static final int COMPANY_LOGO_LOADER_ID = 1;

	private BoothModel boothModel;
	private CompanyLogoModel companyLogoModel;

	private FrameLayout circleLayout;
	private ImageView logo;
	private TextView header;
	private TextView subHeader;
	private TextView details;
	private Button viewProfileButton;

	public int getBoothId()
	{
		return this.boothModel != null ? this.boothModel.getBoothId() : -1;
	}

	public static BoothFragment newInstance(int boothId, Boolean isBig)
	{
		BoothFragment fragment = new BoothFragment();

		Bundle args = new Bundle();
		args.putInt(BOOTH_ID_KEY, boothId);
		args.putBoolean(IS_BIG_KEY, isBig);
		fragment.setArguments(args);

		return fragment;
	}

	/**********************************************************************************************
	 * Fragment Lifecycle Implements
	 **********************************************************************************************/
	@Override
	public void onDetach()
	{
		super.onDetach();

		// Clean up
		this.boothModel = null;
		if (this.companyLogoModel != null && this.companyLogoModel.getCompanyLogo() != null)
		{
			this.companyLogoModel.getCompanyLogo().recycle();
		}

		this.companyLogoModel = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_booth, container, false);

		RealCommApplication application = (RealCommApplication) getActivity().getApplication();

		this.circleLayout = (FrameLayout) view.findViewById(R.id.boothCircleLayout);
		this.logo = (ImageView) view.findViewById(R.id.boothFragmentLogo);
		this.header = (TextView) view.findViewById(R.id.boothFragmentHeader);
		this.subHeader = (TextView) view.findViewById(R.id.boothFragmentSubHeader);
		this.details = (TextView) view.findViewById(R.id.boothFragmentDetails);

		this.header.setTypeface(application.getExo2Font());
		this.subHeader.setTypeface(application.getExo2FontBold());
		this.details.setTypeface(application.getExo2Font());

		this.viewProfileButton = (Button) view.findViewById(R.id.boothFragmentViewProfileButton);
		if (this.viewProfileButton != null)
		{
			this.viewProfileButton.setTypeface(application.getExo2Font());
			this.viewProfileButton.setOnClickListener(this);
		}

		RelativeLayout boothClickableLayout = (RelativeLayout) view.findViewById(R.id.boothClickableLayout);
		if (boothClickableLayout != null)
		{
			boothClickableLayout.setOnClickListener(this);
		}

		ViewTreeObserver observer = details.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener()
		{
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout()
			{
				int maxLines = details.getHeight() / details.getLineHeight();
				details.setMaxLines(maxLines);
				details.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});

		updateBooth();

		return view;
	}
	
	/**********************************************************************************************
	 * Click Implements
	 **********************************************************************************************/
	@Override
	public void onClick(View v)
	{
		goToProfilePage();
	}

	/**********************************************************************************************
	 * Public Methods
	 **********************************************************************************************/
	public void updateBooth()
	{
		// TODO check how many times this method gets called
		if (getDataInterface() != null)
		{
			this.boothModel = getDataInterface().getBoothModelForBoothId(getArguments().getInt(BOOTH_ID_KEY));
			if (this.boothModel != null)
			{
				if (this.logo != null)
				{
					initCompanyLogoLoader();
				}

				int color = this.boothModel.getColor(getActivity().getResources());

				if (this.viewProfileButton != null)
				{
					GradientDrawable buttonBg = (GradientDrawable) this.viewProfileButton.getBackground();
					buttonBg.setColor(color);
				}

				if (this.circleLayout != null)
				{
					GradientDrawable circle = (GradientDrawable) this.circleLayout.getBackground();
					circle.setColor(color);
				}

				this.header.setText(this.boothModel.getCompanyName());

				this.subHeader.setTextColor(color);
				this.subHeader.setText(BOOTH_PREFIX + this.boothModel.getBoothNumber());

				if (getArguments().getBoolean(IS_BIG_KEY))
				{
					this.details.setText(this.boothModel.getCompanyDescription());
				}
				else
				{
					this.details.setVisibility(View.GONE);
				}
			}
		}
	}

	/**********************************************************************************************
	 * Data Changed Callbacks
	 **********************************************************************************************/
	@Override
	public void onDataLoaded()
	{
		updateBooth();
	}

	@Override
	public void onDataChanged()
	{
		updateBooth();
	}

	@Override
	public void onBeaconsUpdated()
	{
		updateBooth();
	}

	/**********************************************************************************************
	 * Private Helper Methods
	 **********************************************************************************************/
	private void updateLogo()
	{
		if (this.companyLogoModel != null && this.companyLogoModel.getCompanyLogo() != null && this.logo != null)
		{
			float radius = getResources().getDimension(R.dimen.defaultCornerRadius);
			int height = (int) getResources().getDimension(R.dimen.boothCompanyLogoHeight);

			double aspectRatio = ((double) this.companyLogoModel.getCompanyLogo().getWidth()) / this.companyLogoModel.getCompanyLogo().getHeight();
			int width = (int) Math.round(height * aspectRatio);

			Bitmap companyLogo = BitmapHelper.getRoundedBitmap(this.companyLogoModel.getCompanyLogo(), width, height, radius);
			this.logo.setImageBitmap(companyLogo);
		}
	}

	private void initCompanyLogoLoader()
	{
		// TODO check whether this changes or not, probably a non-issue
		getLoaderManager().initLoader(COMPANY_LOGO_LOADER_ID, null, this.companyLogoLoaderCallbacks);
	}

	private void finishLogoLoad(CompanyLogoModel results)
	{
		this.companyLogoModel = results;
		updateLogo();
	}

	private void goToProfilePage()
	{
		if (getActivityInterface() != null && this.boothModel != null)
		{
			getActivityInterface().setSelectedBooth(this.boothModel.getBoothId(), this.boothModel.getCompanyId());
			getActivityInterface().changePage(RealcommPage.PROFILE_PAGE);
		}
	}

	/**********************************************************************************************
	 * Loader Callbacks
	 **********************************************************************************************/
	private LoaderCallbacks<CompanyLogoModel> companyLogoLoaderCallbacks = new LoaderCallbacks<CompanyLogoModel>()
	{
		@Override
		public Loader<CompanyLogoModel> onCreateLoader(int loaderId, Bundle args)
		{
			return new CompanyLogoLoader(getActivity(), BoothFragment.this.boothModel);
		}

		@Override
		public void onLoadFinished(Loader<CompanyLogoModel> loader, CompanyLogoModel results)
		{
			finishLogoLoad(results);
		}

		@Override
		public void onLoaderReset(Loader<CompanyLogoModel> loader)
		{
		}
	};
}
