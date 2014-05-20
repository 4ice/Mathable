package se.DV1456.mathable;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GameFragment extends Fragment 
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.fragment_menu, container,false);
				
		return view;
	}
	public void update() 
	{
		
	}
}
