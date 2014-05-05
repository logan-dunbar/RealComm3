package com.openbox.realcomm3.activities;

import com.openbox.realcomm3.R;
import com.openbox.realcomm3.base.BaseActivity;
import com.openbox.realcomm3.fragments.DataFragment;
import com.openbox.realcomm3.fragments.ListingPageFragment;
import com.openbox.realcomm3.fragments.ProfilePageFragment;
import com.openbox.realcomm3.fragments.SplashScreenFragment;
import com.openbox.realcomm3.utilities.enums.AnimationInterpolator;
import com.openbox.realcomm3.utilities.enums.RealcommPage;
import com.openbox.realcomm3.utilities.helpers.AnimationHelper;
import com.openbox.realcomm3.utilities.interfaces.ActivityInterface;
import com.openbox.realcomm3.utilities.managers.RealcommPageManager;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class RealCommActivity extends BaseActivity implements ActivityInterface
{
	private static final String CURRENT_PAGE_KEY = "currentPageKey";

	private RealcommPageManager pageManager;

	private ImageView clearRealcommBackground;
	private ImageView blurRealCommBackground;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawable(null);
		setContentView(R.layout.activity_realcomm);

		this.clearRealcommBackground = (ImageView) findViewById(R.id.clearRealcommBackgroundImageView);
		this.blurRealCommBackground = (ImageView) findViewById(R.id.blurRealcommBackgroundImageView);

		initPageManager(savedInstanceState);

		createDataFragment();

		if (this.pageManager.getCurrentPage() == RealcommPage.Initializing)
		{
			this.pageManager.changePage(RealcommPage.SplashScreen);
		}
	}

	private void initPageManager(Bundle savedInstanceState)
	{
		RealcommPage startingPage = RealcommPage.Initializing;
		if (savedInstanceState != null)
		{
			startingPage = (RealcommPage) savedInstanceState.getSerializable(CURRENT_PAGE_KEY);
		}

		this.pageManager = new RealcommPageManager(startingPage, this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putSerializable(CURRENT_PAGE_KEY, this.pageManager.getCurrentPage());
	}

	@Override
	public void showSplashScreenFragment()
	{
		animateRealcommBackground();

		SplashScreenFragment fragment = (SplashScreenFragment) getSupportFragmentManager().findFragmentByTag(SplashScreenFragment.TAG);
		if (fragment == null)
		{
			fragment = SplashScreenFragment.newInstance();

			int fadeIn = getResources().getInteger(R.integer.splashFragmentFadeInDuration);
			int delay = getResources().getInteger(R.integer.splashImageFadeDuration) - fadeIn;
			fragment.setInAnimationDuration(fadeIn);
			fragment.setInAnimationDelay(delay);
			fragment.setInAnimationInterpolator(AnimationInterpolator.LinearInterpolator);

			getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.id.fadeInAnimation, -1)
				.add(R.id.realcommFragmentContainer, fragment, SplashScreenFragment.TAG)
				.commit();
		}
	}

	@Override
	public void hideSplashScreenFragmentAndShowListingPageFragment()
	{
		SplashScreenFragment splashFragment = (SplashScreenFragment) getSupportFragmentManager().findFragmentByTag(SplashScreenFragment.TAG);
		if (splashFragment != null)
		{
			splashFragment.setOutAnimationDuration(getResources().getInteger(R.integer.splashFragmentToListingFragment));
			splashFragment.setOutAnimationInterpolator(AnimationInterpolator.LinearInterpolator);

			getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(-1, R.id.slideUpOutAnimation)
				.remove(splashFragment)
				.commit();
		}

		// TODO should maybe combine them
		ListingPageFragment listingFragment = (ListingPageFragment) getSupportFragmentManager().findFragmentByTag(ListingPageFragment.TAG);
		if (listingFragment == null)
		{
			listingFragment = ListingPageFragment.newInstance();

			listingFragment.setInAnimationDuration(getResources().getInteger(R.integer.splashFragmentToListingFragment));
			listingFragment.setInAnimationInterpolator(AnimationInterpolator.LinearInterpolator);

			getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.id.slideUpInAnimation, -1)
				.add(R.id.realcommFragmentContainer, listingFragment, ListingPageFragment.TAG)
				.commit();
		}
	}

	@Override
	public void showListingPageFragmentAndRemoveProfileFragment()
	{
		// TODO check the out animations, make out/in left etc
		ListingPageFragment listingFragment = (ListingPageFragment) getSupportFragmentManager().findFragmentByTag(ListingPageFragment.TAG);
		ProfilePageFragment profileFragment = (ProfilePageFragment) getSupportFragmentManager().findFragmentByTag(ProfilePageFragment.TAG);

		// TODO check this if
		if (listingFragment != null && profileFragment != null)
		{
			int duration = getResources().getInteger(R.integer.profileFragmentToListingFragment);

			listingFragment.setInAnimationDuration(duration);
			listingFragment.setInAnimationInterpolator(AnimationInterpolator.LinearInterpolator);

			profileFragment.setOutAnimationDuration(duration);
			profileFragment.setOutAnimationInterpolator(AnimationInterpolator.LinearInterpolator);

			getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.id.slideInLeftAnimation, R.id.slideOutRightAnimation, R.id.slideInRightAnimation, R.id.slideOutLeftAnimation)
				.remove(profileFragment)
				.show(listingFragment)
				.commit();
		}
	}

	@Override
	public void addProfilePageAndHideListingPage()
	{
		ListingPageFragment listingFragment = (ListingPageFragment) getSupportFragmentManager().findFragmentByTag(ListingPageFragment.TAG);

		// TODO might not need this
		ProfilePageFragment profileFragment = (ProfilePageFragment) getSupportFragmentManager().findFragmentByTag(ProfilePageFragment.TAG);

		if (listingFragment != null)
		{
			int duration = getResources().getInteger(R.integer.listingFragmentToProfileFragment);

			listingFragment.setOutAnimationDuration(duration);
			listingFragment.setOutAnimationInterpolator(AnimationInterpolator.LinearInterpolator);

			profileFragment = ProfilePageFragment.newInstance();

			profileFragment.setInAnimationDuration(duration);
			profileFragment.setInAnimationInterpolator(AnimationInterpolator.LinearInterpolator);

			getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.id.slideInRightAnimation, R.id.slideOutLeftAnimation, R.id.slideInLeftAnimation, R.id.slideOutRightAnimation)
				.hide(listingFragment)
				.add(R.id.realcommFragmentContainer, profileFragment, ProfilePageFragment.TAG)
				.commit();
		}
	}

	private void createDataFragment()
	{
		DataFragment fragment = (DataFragment) getSupportFragmentManager().findFragmentByTag(DataFragment.TAG);
		if (fragment == null)
		{
			getSupportFragmentManager().beginTransaction().add(DataFragment.newInstance(), DataFragment.TAG).commit();
		}
	}

	@Override
	public void onFragmentViewCreated()
	{

	}

	@Override
	public void changePage(RealcommPage page)
	{
		this.pageManager.changePage(page);
	}

	@Override
	public void onSplashScreenAnimationInComplete()
	{
		// Remove the clear picture
		ViewGroup vg = (ViewGroup) this.clearRealcommBackground.getParent();
		vg.removeView(this.clearRealcommBackground);

		// Keep the blurry picture around
		this.blurRealCommBackground.setVisibility(View.VISIBLE);

		this.pageManager.changePage(RealcommPage.ListingPage);
	}

	@Override
	public void onSplashScreenAnimationOutComplete()
	{
		// TODO Auto-generated method stub
	}

	private void animateRealcommBackground()
	{
		int imageFade = getResources().getInteger(R.integer.splashImageFadeDuration);
		Animation backgroundImageFadeIn = AnimationHelper.getFadeInAnimation(AnimationInterpolator.DeccelerateInterpolator, imageFade, 0, null);
		Animation backgroundImageFadeOut = AnimationHelper.getFadeOutAnimation(AnimationInterpolator.AccelerateInterpolator, imageFade, 0, null);

		this.blurRealCommBackground.setAnimation(backgroundImageFadeIn);
		this.clearRealcommBackground.setAnimation(backgroundImageFadeOut);
	}

	@Override
	public void onBackPressed()
	{
		if (this.pageManager.getCurrentPage() == RealcommPage.ProfilePage)
		{
			this.changePage(RealcommPage.ListingPage);
		}
		else
		{
			super.onBackPressed();
		}
	}
}
